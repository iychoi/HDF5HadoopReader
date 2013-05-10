/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;

import edu.arizona.cs.hdf5.io.BinaryReader;


public class FillValueMessage {
	private long m_address;
	private int m_version;
	private int m_spaceAllocateTime;
	private int m_flags;
	private int m_fillWriteTime;
	private boolean m_hasFillValue;
	private int m_size;
	private byte[] m_value;
	
	public FillValueMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_version = in.readByte();
		
		if (this.m_version < 3) {
			this.m_spaceAllocateTime = in.readByte();
			this.m_fillWriteTime = in.readByte();
			this.m_hasFillValue = (in.readByte() != 0);

		} else {
			this.m_flags = in.readByte();
			this.m_spaceAllocateTime = (byte) (this.m_flags & 3);
			this.m_fillWriteTime = (byte) ((this.m_flags >> 2) & 3);
			this.m_hasFillValue = (this.m_flags & 32) != 0;
		}

		if (this.m_hasFillValue) {
			this.m_size = in.readInt();
			if (this.m_size > 0) {
				this.m_value = in.readBytes(this.m_size);
				this.m_hasFillValue = true;
			} else {
				this.m_hasFillValue = false;
			}
		}
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public int getSpaceAllocateTime() {
		return this.m_spaceAllocateTime;
	}
	
	public int getFlags() {
		return this.m_flags;
	}
	
	public int getFillWriteTime() {
		return this.m_fillWriteTime;
	}
	
	public boolean getHasFillValue() {
		return this.m_hasFillValue;
	}
	
	public int getSize() {
		return this.m_size;
	}
	
	public byte[] getValue() {
		return this.m_value;
	}
	
	public void printValues() {
		System.out.println("FillValueMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("version : " + this.m_version);
		System.out.println("space allocate time : " + this.m_spaceAllocateTime);
		System.out.println("flags : " + this.m_flags);
		System.out.println("fill write time : " + this.m_fillWriteTime);
		System.out.println("has fill value : " + this.m_hasFillValue);
		System.out.println("size : " + this.m_size);
		
		System.out.println("FillValueMessage <<<");
	}
}
