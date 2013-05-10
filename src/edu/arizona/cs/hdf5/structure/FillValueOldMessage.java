/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;

import edu.arizona.cs.hdf5.io.BinaryReader;

public class FillValueOldMessage {
	private long m_address;
	private int m_size;
	private byte[] m_value;
	
	public FillValueOldMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_size = in.readInt();
		this.m_value = in.readBytes(this.m_size);
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getSize() {
		return this.m_size;
	}
	
	public byte[] getValue() {
		return this.m_value;
	}
	
	public void printValues() {
		System.out.println("FillValueOldMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("size : " + this.m_size);
		
		System.out.println("FillValueOldMessage <<<");
	}
}
