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

public class LayoutMessage {
	private long m_address;
	private int m_version;
	private int m_numberOfDimensions;
	private int m_type;
	private long m_dataAddress;
	private long m_continuousSize;
	private int[] m_chunkSize;
	private int m_dataSize;
	
	public LayoutMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_version = in.readByte();
		
		if(this.m_version < 3) {
			this.m_numberOfDimensions = in.readByte();
			this.m_type = in.readByte();
			
			in.skipBytes(5);
			
			boolean isCompact = (this.m_type == 0);
			if(!isCompact) {
				this.m_dataAddress = ReadHelper.readO(in, sb);
			}
			
			this.m_chunkSize = new int[this.m_numberOfDimensions];
			for(int i=0;i<this.m_numberOfDimensions;i++) {
				this.m_chunkSize[i] = in.readInt();
			}
			
			if(isCompact) {
				this.m_dataSize = in.readInt();
				this.m_dataAddress = in.getOffset();
			}
		} else {
			this.m_type = in.readByte();
			
			if(this.m_type == 0) {
				this.m_dataSize = in.readShort();
				this.m_dataAddress = in.getOffset();
			} else if(this.m_type == 1) {
				this.m_dataAddress = ReadHelper.readO(in, sb);
				this.m_continuousSize = ReadHelper.readL(in, sb);
			} else if(this.m_type == 2) {
				this.m_numberOfDimensions = in.readByte();
				this.m_dataAddress = ReadHelper.readO(in, sb);
				this.m_chunkSize = new int[this.m_numberOfDimensions];
				
				for(int i=0;i<this.m_numberOfDimensions;i++) {
					this.m_chunkSize[i] = in.readInt();
				}
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
	
	public long getDataAddress() {
		return this.m_dataAddress;
	}
	
	public long getContinuousSize() {
		return this.m_continuousSize;
	}
	
	public int[] getChunkSize() {
		return this.m_chunkSize;
	}
	
	public int getDataSize() {
		return this.m_dataSize;
	}
	
	public void printValues() {
		System.out.println("LayoutMessage >>>");

		System.out.println("address : " + this.m_address);
		System.out.println("version : " + this.m_version);
		System.out.println("number of dimensions : " + this.m_numberOfDimensions);
		System.out.println("type : " + this.m_type);
		System.out.println("data address : " + this.m_dataAddress);
		System.out.println("continuous size : " + this.m_continuousSize);
		System.out.println("data size : " + this.m_dataSize);
		
		for(int i=0;i<this.m_chunkSize.length;i++) {
			System.out.println("chunk size [" + i + "] : " + this.m_chunkSize[i]);
		}
		
		System.out.println("LayoutMessage <<<");
	}
}
