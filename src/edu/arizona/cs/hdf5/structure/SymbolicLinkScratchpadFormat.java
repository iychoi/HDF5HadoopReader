/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;

import edu.arizona.cs.hdf5.io.BinaryReader;


public class SymbolicLinkScratchpadFormat {
	private long m_address;
	private int m_offsetToLinkValue;
	
	private int m_totalSymbolicLinkScratchpadFormatSize;
	
	public SymbolicLinkScratchpadFormat(BinaryReader in, Superblock sb, long address) throws IOException {
		
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_offsetToLinkValue = in.readInt();
		
		this.m_totalSymbolicLinkScratchpadFormatSize = 4;
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getOffsetToLinkValue() {
		return this.m_offsetToLinkValue;
	}
	
	public int getTotalSymbolicLinkScratchpadFormatSize() {
		return this.m_totalSymbolicLinkScratchpadFormatSize;
	}
	
	public void printValues() {
		System.out.println("SymbolicLinkScratchpadFormat >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("offset to link value : " + this.m_offsetToLinkValue);
		
		System.out.println("total symbolic link scratchpad format size : " + this.m_totalSymbolicLinkScratchpadFormatSize);
		System.out.println("SymbolicLinkScratchpadFormat <<<");
	}
}
