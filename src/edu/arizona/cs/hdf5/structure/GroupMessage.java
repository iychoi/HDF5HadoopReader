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

public class GroupMessage {
	private long m_address;
	private long m_bTreeAddress;
	private long m_nameHeapAddress;

	public GroupMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		this.m_bTreeAddress = ReadHelper.readO(in, sb);
		this.m_nameHeapAddress = ReadHelper.readO(in, sb);
	}

	public long getAddress() {
		return this.m_address;
	}
	
	public long getBTreeAddress() {
		return this.m_bTreeAddress;
	}

	public long getNameHeapAddress() {
		return this.m_nameHeapAddress;
	}

	public void printValues() {
		System.out.println("GroupMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("btree address : " + this.m_bTreeAddress);
		System.out.println("nameheap address : " + this.m_nameHeapAddress);
		System.out.println("GroupMessage <<<");
	}

}
