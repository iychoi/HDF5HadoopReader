/*
 * Written by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5;

import java.io.IOException;
import java.util.Vector;

import edu.arizona.cs.hdf5.structure.DataBTree;
import edu.arizona.cs.hdf5.structure.DataChunk;
import edu.arizona.cs.hdf5.structure.DataChunkIterator;
import edu.arizona.cs.hdf5.structure.DataObjectFacade;
import edu.arizona.cs.hdf5.structure.Group;
import edu.arizona.cs.hdf5.structure.Layout;
import edu.arizona.cs.hdf5.structure.Superblock;
import edu.arizona.cs.hdf5.structure.SymbolTableEntry;
import edu.arizona.cs.hdf5.io.BinaryFileReader;
import edu.arizona.cs.hdf5.io.BinaryReader;


public class HDF5Reader {
	
	private BinaryReader m_reader;
	
	private String m_filename;
	
	private String m_datasetName;
	private Layout m_layout;
	private DataBTree m_dataTree;
	private Vector<DataChunk> m_chunks;
	private long m_headerLength;
	
	public HDF5Reader(String filename, String datasetName) throws IOException {
		this.m_filename = filename;
		this.m_reader = new BinaryFileReader(this.m_filename);
		this.m_datasetName = datasetName; 
		
		this.m_layout = null;
		this.m_dataTree = null;
		
		this.m_chunks = new Vector<DataChunk>();
		
		this.m_headerLength = 0;
	}
	
	public HDF5Reader(BinaryReader in, String datasetName) {
		this.m_filename = null;
		this.m_reader = in; 
		this.m_datasetName = datasetName; 
		
		this.m_layout = null;
		this.m_dataTree = null;
		
		this.m_chunks = new Vector<DataChunk>();
		
		this.m_headerLength = 0;
	}
	
	public void parseHeader() throws IOException {
		Superblock sb = new Superblock(this.m_reader, 0);
		SymbolTableEntry rootSymbolTableEntry = sb.getRootGroupSymbolTableEntry();
		DataObjectFacade objectFacade = new DataObjectFacade(this.m_reader, sb, "root", rootSymbolTableEntry.getObjectHeaderAddress());
		Group rootGroup = new Group(this.m_reader, sb, objectFacade);
		
		Vector<DataObjectFacade> objects = rootGroup.getObjects();
		for(DataObjectFacade dobj : objects) {
			// compare dataset name
			if(dobj.getSymbolName().equalsIgnoreCase(this.m_datasetName)) {
				Layout layout = dobj.getLayout();
				this.m_layout = layout;
				
				DataBTree dataTree = new DataBTree(layout);
				this.m_dataTree = dataTree;
				
				DataChunkIterator iter = dataTree.getChunkIterator(this.m_reader, sb);
				
				while(iter.hasNext(this.m_reader, sb)) {
					DataChunk chunk = iter.next(this.m_reader, sb);
					this.m_chunks.add(chunk);
				}
				break;
			}
		}
		
		this.m_headerLength = this.m_reader.getMaxOffset();
	}
	
	public long getHeaderSize() {
		return this.m_headerLength;
	}
	
	public String getFileName() {
		return this.m_filename;
	}
	
	public BinaryReader getReader() {
		return this.m_reader;
	}
	
	public String getDatasetName() {
		return this.m_datasetName;
	}
	
	public Layout getLayout() {
		return this.m_layout;
	}
	
	public DataBTree getDataBTree() {
		return this.m_dataTree;
	}
	
	public Vector<DataChunk> getChunks() {
		return this.m_chunks;
	}
	
	public void close() {
		this.m_reader.close();
	}
}
