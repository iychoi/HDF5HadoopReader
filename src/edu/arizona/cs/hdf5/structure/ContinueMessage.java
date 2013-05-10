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


public class ContinueMessage {
	private long m_address;
	private long m_offset;
	private long m_length;
	
	private int m_totalObjectHeaderMessageContinueSize;
	
	public ContinueMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		
		in.setOffset(address);
		
		this.m_address = address;
		this.m_offset = ReadHelper.readO(in, sb);
		this.m_length = ReadHelper.readL(in, sb);
		
		this.m_totalObjectHeaderMessageContinueSize = sb.getSizeOfOffsets() + sb.getSizeOfLengths();
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public long getOffset() {
		return this.m_offset;
	}
	
	public long getLength() {
		return this.m_length;
	}
	
	public int getTotalObjectHeaderMessageContinueSize() {
		return this.m_totalObjectHeaderMessageContinueSize;
	}
	
	public void printValues() {
		System.out.println("ObjectHeaderMessageContinue >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("offset : " + this.m_offset);
		System.out.println("length : " + this.m_length);
		System.out.println("total header message continue size : " + this.m_totalObjectHeaderMessageContinueSize);
		System.out.println("ObjectHeaderMessageContinue <<<");
	}
}
