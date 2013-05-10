/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;

import edu.arizona.cs.hdf5.io.BinaryReader;

public class LastModifiedMessage {
	private long m_address;
	private int m_version;
	private int m_seconds;
	
	public LastModifiedMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_version = in.readByte();
		
		in.skipBytes(3);
		
		this.m_seconds = in.readInt();
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public int getSeconds() {
		return this.m_seconds;
	}
	
	public void printValues() {
		System.out.println("LastModifiedMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("version : " + this.m_version);
		System.out.println("seconds : " + this.m_seconds);
		
		System.out.println("LastModifiedMessage <<<");
	}
}
