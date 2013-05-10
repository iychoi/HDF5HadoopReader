/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;

import edu.arizona.cs.hdf5.io.BinaryReader;
import edu.arizona.cs.hdf5.io.ReadHelper;

public class DataChunk {

	private long m_address; 
	private int m_size;
	private int m_filterMask;
	private int[] m_offsets;
	private long m_filePos;

	DataChunk(BinaryReader in, Superblock sb, long address, int numberOfDimensions, boolean last) throws IOException {
		
		in.setOffset(address);
		
		this.m_address = address;
		this.m_size = in.readInt();
		this.m_filterMask = in.readInt();
		
		this.m_offsets = new int[numberOfDimensions];
		for (int i = 0; i < numberOfDimensions; i++) {
			this.m_offsets[i] = (int)in.readLong();
		}
		
		this.m_filePos = last ? -1 : ReadHelper.readO(in, sb);
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getSize() {
		return this.m_size;
	}
	
	public int getFilterMask() {
		return this.m_filterMask;
	}
	
	public int[] getOffsets() {
		return this.m_offsets;
	}
	
	public long getFilePosition() {
		return this.m_filePos;
	}
	
	public void printValues() {
		System.out.println("DataChunk >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("size : " + this.m_size);
		System.out.println("filter mask : " + this.m_filterMask);
		if(this.m_offsets != null) {
			for(int i=0;i<this.m_offsets.length;i++) {
				System.out.println("offsets[" + i + "] : " + this.m_offsets[i]);
			}
		}
		System.out.println("file position : " + this.m_filePos);
		
		System.out.println("DataChunk <<<");
	}
}
