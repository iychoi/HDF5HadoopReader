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


public class BTreeEntry {

	private long m_address;
	private long m_key;
	private long m_targetAddress;
	
	public BTreeEntry(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		this.m_key = ReadHelper.readL(in, sb);
		this.m_targetAddress = ReadHelper.readO(in, sb);
	}
	
	public long getAddress() {
		return this.m_address;
	}

	public long getTargetAddress() {
		return this.m_targetAddress;
	}

	public long getKey() {
		return this.m_key;
	}
	
	public void printValues() {
		System.out.println("BTreeEntry >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("key : " + this.m_key);
		System.out.println("target address : " + this.m_targetAddress);
		System.out.println("BTreeEntry <<<");
	}
}
