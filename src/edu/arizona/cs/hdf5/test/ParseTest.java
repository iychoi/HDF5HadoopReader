/*
 * Written by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.test;

import java.io.IOException;
import java.util.Vector;

import edu.arizona.cs.hdf5.HDF5Reader;
import edu.arizona.cs.hdf5.structure.DataChunk;
import edu.arizona.cs.hdf5.structure.DataTypeMessage;
import edu.arizona.cs.hdf5.structure.Layout;
import edu.arizona.cs.hdf5.structure.LayoutField;
import edu.arizona.cs.hdf5.io.BinaryReader;

public class ParseTest {
	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.out.println("Error : inputfile is necessary");
			return;
		}
		
		String option = "";
		String filename = "";
		
		if(args.length == 2) {
			option = args[0];
			filename = args[1];
		} else if(args.length == 1) {
			filename = args[0];
		}
		
		if(filename.isEmpty()) {
			System.out.println("Error : inputfile is necessary");
			return;
		}
		
		// check option
		boolean showHeader = false;
		boolean showData = false;
		if(option.contains("h")) {
			// header
			showHeader = true;
		}
		if(option.contains("d")) {
			// data
			showData = true;
		}
		
		try {
			HDF5Reader reader = new HDF5Reader(filename, "dset");
			reader.parseHeader();
			
			if(showHeader) {
				long headerSize = reader.getHeaderSize();
				System.out.println("header size : " + headerSize);
			}
			// layout
			Layout layout = reader.getLayout();
			
			int dims = layout.getNumberOfDimensions();
			if(showHeader) {
				System.out.println("dimensions : " + dims);
			}
			
			int[] chunkSize = layout.getChunkSize();
			int[] dlength = layout.getDimensionLength();
			int[] maxdlength = layout.getMaxDimensionLength();
			
			if(showHeader) {
				for(int i=0;i<dims;i++) {
					if(chunkSize.length > i) {
						System.out.println("chunk size[" + i + "] : " + chunkSize[i]);
					}
					
					if(dlength.length > i) {
						System.out.println("dimension length[" + i + "] : " + dlength[i]);
					}
					
					if(maxdlength.length > i) {
						System.out.println("max dimension length[" + i + "] : " + maxdlength[i]);
					}
				}
			}
			
			Vector<LayoutField> fields = layout.getFields();
			
			// chunk
			BinaryReader chunkReader = reader.getReader();
			chunkReader.setLittleEndian();
			
			
			int dataTotal = dlength[0];
			int readCount = 0;
			
			Vector<DataChunk> chunks = reader.getChunks();
			for(DataChunk chunk : chunks) {
				
				long filepos = chunk.getFilePosition();
				if(showHeader) {
					chunk.printValues();
				}
				
				chunkReader.setOffset(filepos);
				
				if(showData) {
					int dataCountPerChunk = chunk.getSize() / chunkSize[0];
					for(int i=0;i<dataCountPerChunk;i++) {
						byte[] bytes = chunkReader.readBytes(chunkSize[0]);
						
						for(int j=0;j<fields.size();j++) {
							LayoutField field = fields.get(j);
							
							int offset = field.getOffset();
							int len = field.getByteLength();
							int dataType = field.getDataType();
							int ndims = field.getNDims();
							String name = field.getName();
							
							if(dataType == DataTypeMessage.DATATYPE_STRING) {
								String val = new String(bytes, offset, len);
								System.out.println(name + " : " + val.trim());
							}
						}
						
						readCount++;
						
						if(readCount >= dataTotal) {
							break;
						}
					}
				}
				
				if(readCount >= dataTotal) {
					break;
				}
			}
			
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
