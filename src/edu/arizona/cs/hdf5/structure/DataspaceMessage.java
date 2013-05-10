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

public class DataspaceMessage {
	private long m_address;
	private int m_version;
	private int m_numberOfDimensions;
	private byte m_flags;
	private int m_type;
	private int[] m_dimensionLength;
	private int[] m_maxDimensionLength;
	
	public DataspaceMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_version = in.readByte();
		
		if (this.m_version == 1) {
			this.m_numberOfDimensions = in.readByte();
			this.m_flags = in.readByte();
			this.m_type = this.m_numberOfDimensions == 0 ? 0 : 1;
			in.skipBytes(5);
		} else if (this.m_version == 2) {
			this.m_numberOfDimensions = in.readByte();
			this.m_flags = in.readByte();
			this.m_type = in.readByte();
		} else {
			throw new IOException("unknown version");
		}
		
		this.m_dimensionLength = new int[this.m_numberOfDimensions];
		for(int i=0;i<this.m_numberOfDimensions;i++) {
			this.m_dimensionLength[i] = (int)ReadHelper.readL(in, sb);
		}
		
		boolean hasMax = ((this.m_flags & 0x01) != 0);
		this.m_maxDimensionLength = new int[this.m_numberOfDimensions];
		if(hasMax) {
			 for(int i=0;i<this.m_numberOfDimensions;i++) {
				 this.m_maxDimensionLength[i] = (int)ReadHelper.readL(in, sb);
			 }
		} else {
			for(int i=0;i<this.m_numberOfDimensions;i++) {
				this.m_maxDimensionLength[i] = this.m_dimensionLength[i];
			}
		}
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public int getNumberOfDimensions() {
		return this.m_numberOfDimensions;
	}
	
	public byte getFlags() {
		return this.m_flags;
	}
	
	public int getType() {
		return this.m_type;
	}
	
	public int[] getDimensionLength() {
		return this.m_dimensionLength;
	}
	
	public int[] getMaxDimensionLength() {
		return this.m_maxDimensionLength;
	}
	
	public void printValues() {
		System.out.println("DataspaceMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("version : " + this.m_version);
		System.out.println("number of dimensions : " + this.m_numberOfDimensions);
		System.out.println("flags : " + this.m_flags);
		System.out.println("type : " + this.m_type);
		
		System.out.println("DataspaceMessage <<<");
	}
}
