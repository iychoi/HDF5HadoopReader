/*
 * Written by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hadoop;

import java.io.IOException;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import edu.arizona.cs.hdf5.HDF5Reader;
import edu.arizona.cs.hdf5.structure.DataChunk;
import edu.arizona.cs.hdf5.structure.DataTypeMessage;
import edu.arizona.cs.hdf5.structure.Layout;
import edu.arizona.cs.hdf5.structure.LayoutField;
import edu.arizona.cs.hdf5.io.BinaryReader;

public class HDF5RecordReader extends RecordReader<LongWritable, Vector<String[]>> {

	private LongWritable key;
	private Vector<String[]> value = new Vector<String[]>();
	private long start = 0;
	private long end = 0;
	private long pos = 0;
	private int readLine = 0;
	
	private HDF5Reader reader;
	private BinaryReader breader;
	private Layout layout;
	private int dims;
	private int[] chunkSize;
	private int[] dlength;
	private int[] maxdlength;
	private Vector<LayoutField> fields;
	
	private int totalDataNoInFile;
	private int lastChunkDataNo;
	private Vector<DataChunk> chunks;
	private Vector<DataChunk> targetChunks;
	
	
	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public LongWritable getCurrentKey() throws IOException,
			InterruptedException {
		return key;
	}

	@Override
	public Vector<String[]> getCurrentValue() throws IOException, InterruptedException {
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		if(start == end) {
			return 0.0f;
		} else {
			return Math.min(1.0f, (pos - start) / (float)(end - start));
		}
	}

	@Override
	public void initialize(InputSplit genericSplit, TaskAttemptContext context)
			throws IOException, InterruptedException {
		FileSplit split = (FileSplit)genericSplit;
		final Path file = split.getPath();
		
		Configuration conf = context.getConfiguration();
		
		readLine = conf.getInt(HDF5RecordReaderConstants.CONF_RECORD_READ_STRING, HDF5RecordReaderConstants.RECORD_READ_DEFAULT);
		if(readLine <= 0) {
			readLine = HDF5RecordReaderConstants.RECORD_READ_DEFAULT;
		}
		
		FileSystem fs = file.getFileSystem(conf);
		
		start = split.getStart();
		end = start + split.getLength();
		
		FSDataInputStream filein = fs.open(file);
		
		HDFSBinaryFileReader binaryFileReader = new HDFSBinaryFileReader(filein);
		
		binaryFileReader.setOffset(0);
		
		String[] datasetNames = conf.getStrings(HDF5RecordReaderConstants.CONF_DATASET_STRING);
		if(datasetNames == null || datasetNames.length == 0) {
			throw new IOException("dataset is not specified");
		}
		
		String datasetName = datasetNames[0];
		
		this.reader = new HDF5Reader(binaryFileReader, datasetName);
		this.reader.parseHeader();
		
		this.layout = reader.getLayout();
		this.dims = this.layout.getNumberOfDimensions();
		if(this.dims < 1)
			throw new IOException("number of dimensions is less than 1");
		
		this.chunkSize = layout.getChunkSize();
		this.dlength = layout.getDimensionLength();
		this.maxdlength = layout.getMaxDimensionLength();
		this.fields = layout.getFields();
		this.chunks = reader.getChunks();
		
		this.totalDataNoInFile = this.dlength[0];
		
		this.lastChunkDataNo = this.totalDataNoInFile;
		
		for(int i=0;i<this.chunks.size()-1;i++) {
			this.lastChunkDataNo -= this.chunks.get(i).getSize() / this.chunkSize[0];
		}
		
		// recalc start
		long new_start = 0;
		int chunkStartID = 0;
		for(int i=0;i<this.chunks.size();i++) {
			DataChunk chunk = this.chunks.get(i);
			if(this.start >= chunk.getFilePosition()
					&& this.start < chunk.getFilePosition() + chunk.getSize()) {
				
				long offset = start - chunk.getFilePosition();
				
				if(offset % this.chunkSize[0] == 0) {
					new_start = start;
				} else {
					new_start = start + (this.chunkSize[0] - (offset % this.chunkSize[0])); 
				}
				
				chunkStartID = i;
				
				break;
			} else if(this.start < chunk.getFilePosition()) {
				new_start = chunk.getFilePosition();
				
				chunkStartID = i;
				
				break;
			}
		}
		
		// recalc end
		long new_end = 0;
		int chunkEndID = 0;
		for(int i=0;i<this.chunks.size();i++) {
			DataChunk chunk = this.chunks.get(i);
			if(this.end > chunk.getFilePosition()
					&& this.end <= chunk.getFilePosition() + chunk.getSize()) {
				
				long offset = end - chunk.getFilePosition();
				
				if(offset % this.chunkSize[0] == 0) {
					new_end = end;
				} else {
					new_end = end + (this.chunkSize[0] - (offset % this.chunkSize[0])); 
				}
				
				chunkEndID = i;
				
				break;
			} else if(this.end <= chunk.getFilePosition()) {
				if(i == 0) {
					new_end = 0;
				} else {
					DataChunk prevChunk = this.chunks.get(i-1);
					new_end = prevChunk.getFilePosition() + chunk.getSize();	
				}
				
				chunkEndID = i;
				
				break;
			}
		}
		
		if(new_start >= new_end) {
			new_end = new_start;
		}
		
		this.targetChunks = new Vector<DataChunk>();
		if(new_start != new_end) {
			for(int i=chunkStartID;i<=chunkEndID;i++) {
				DataChunk chunk = this.chunks.get(i);
				
				this.targetChunks.add(chunk);
			}
		}
		
		
		start = new_start;
		end = new_end;
		
		pos = start;
		this.breader = this.reader.getReader();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if(key == null) {
			key = new LongWritable();
		}
		key.set(pos);
		
		value.clear();
		if(start >= end || pos >= end || this.targetChunks.size() == 0) {
			key = null;
			value = null;
			return false;
		}
		
		DataChunk chunk = this.targetChunks.get(0);
		
		if(pos < chunk.getFilePosition())
			pos = chunk.getFilePosition();
		
		for(int i=0;i<this.readLine;i++) {
			if(pos >= end) {
				this.targetChunks.clear();
				break;
			}
			
			if(chunk.getFilePosition() == this.chunks.get(this.chunks.size() - 1).getFilePosition()) {
				// last chunk
				if(pos >= chunk.getFilePosition() + (this.lastChunkDataNo * this.chunkSize[0])) {
					this.targetChunks.clear();
					pos = end;
					break;
				}
			}
			
			if(pos >= chunk.getFilePosition() + chunk.getSize()) {
				this.targetChunks.remove(0);
				
				if(this.targetChunks.size() == 0) {
					break;
				} else {
					chunk = this.targetChunks.get(0);
					if(pos < chunk.getFilePosition())
						pos = chunk.getFilePosition();
				}
			}
			
			// start read
			this.breader.setOffset(pos);
			
			byte[] bytes = this.breader.readBytes(this.chunkSize[0]);
			pos += this.chunkSize[0];
			
			String[] vs = new String[this.fields.size()];
			for(int k=0;k<this.fields.size();k++) {
				LayoutField field = this.fields.get(k);
				
				int offset = field.getOffset();
				int len = field.getByteLength();
				int dataType = field.getDataType();
				String name = field.getName();
				
				if(dataType == DataTypeMessage.DATATYPE_STRING) {
					String val = new String(bytes, offset, len);
					vs[k] = val.trim();
				} else {
					throw new IOException("Unimplemented data type");
				}
			}
			
			value.add(vs);
		}
		
		if(this.targetChunks.size() != 0) {
			if(pos >= end) {
				this.targetChunks.clear();
			}
			
			if(chunk.getFilePosition() == this.chunks.get(this.chunks.size() - 1).getFilePosition()) {
				// last chunk
				if(pos >= chunk.getFilePosition() + (this.lastChunkDataNo * this.chunkSize[0])) {
					this.targetChunks.clear();
				}
			}
			
			if(pos >= chunk.getFilePosition() + chunk.getSize()) {
				this.targetChunks.remove(0);
				
				if(this.targetChunks.size() == 0) {
					pos = end;
				} else {
					chunk = this.targetChunks.get(0);
					if(pos < chunk.getFilePosition())
						pos = chunk.getFilePosition();
				}
			}
		}
		
		return true;
	}
}
