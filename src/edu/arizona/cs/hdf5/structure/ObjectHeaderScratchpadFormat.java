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

public class ObjectHeaderScratchpadFormat {
	private long m_address;
	private long m_addressOfBTree;
	private long m_addressOfNameHeap;
	
	private int m_totalObjectHeaderScratchpadFormatSize;
	
	public ObjectHeaderScratchpadFormat(BinaryReader in, Superblock sb, long address) throws IOException {
		
		in.setOffset(address);
		
		this.m_address = address;
		this.m_addressOfBTree = ReadHelper.readO(in, sb);
		this.m_addressOfNameHeap = ReadHelper.readO(in, sb);
		
		this.m_totalObjectHeaderScratchpadFormatSize = sb.getSizeOfOffsets() * 2;
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public long getAddressOfBTree() {
		return this.m_addressOfBTree;
	}
	
	public long getAddressOfNameHeap() {
		return this.m_addressOfNameHeap;
	}
	
	public int getTotalObjectHeaderScratchpadFormatSize() {
		return this.m_totalObjectHeaderScratchpadFormatSize;
	}
	
	public void printValues() {
		System.out.println("ObjectHeaderScratchpadFormat >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("address of BTree : " + this.m_addressOfBTree);
		System.out.println("address of name heap : " + this.m_addressOfNameHeap);
		
		System.out.println("total object header scratchpad format size : " + this.m_totalObjectHeaderScratchpadFormatSize);
		System.out.println("ObjectHeaderScratchpadFormat <<<");
	}
}
