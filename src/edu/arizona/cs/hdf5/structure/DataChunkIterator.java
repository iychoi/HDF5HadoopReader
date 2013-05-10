/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;

import edu.arizona.cs.hdf5.io.BinaryReader;

public class DataChunkIterator {
	private long m_address;
	private DataNode m_root;
	
	public DataChunkIterator(BinaryReader in, Superblock sb, Layout layout) throws IOException {
		
		this.m_address = layout.getDataAddress();
		
		in.setOffset(this.m_address);
		
		this.m_root = new DataNode(in, sb, layout, this.m_address);
		this.m_root.first(in, sb);
	}
	
	public boolean hasNext(BinaryReader in, Superblock sb) {
		return this.m_root.hasNext(in, sb);
	}
	
	public DataChunk next(BinaryReader in, Superblock sb) throws IOException {
		return this.m_root.next(in, sb);
	}
}
