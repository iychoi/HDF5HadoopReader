/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;
import edu.arizona.cs.hdf5.io.BinaryReader;

public class DataBTree {
	private Layout m_layout;
	
	public DataBTree(Layout layout) throws IOException {
		this.m_layout = layout;
	}
	
	public DataChunkIterator getChunkIterator(BinaryReader in, Superblock sb) throws IOException {
		return new DataChunkIterator(in, sb, this.m_layout);
	}
}
