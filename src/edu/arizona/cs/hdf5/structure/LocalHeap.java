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


public class LocalHeap {
	
	public static final byte[] SIGNATURE = {'H', 'E', 'A', 'P'};
	
	private long m_address;
	
	private byte[] m_signature;
	private int m_version;
	private byte[] m_reserved0;
	private long m_dataSegmentSize; 
	private long m_offsetToHeadOfFreeList;
	private long m_addressOfDataSegment;
	
	private byte[] m_data;
	
	private int m_totalLocalHeapSize;
	
	public LocalHeap(BinaryReader in, Superblock sb, long address) throws IOException {
		
		this.m_address = address;
		
		in.setOffset(address);
		
		// signature
		this.m_signature = in.readBytes(4);
		
		if(!this.isValidSignature()) {
			throw new IOException("signature is not valid");
		}
		
		this.m_version = in.readByte();
		
		if(this.m_version > 0) {
			throw new IOException("version not implemented");
		}
		
		this.m_reserved0 = in.readBytes(3);
		
		this.m_totalLocalHeapSize = 8;
		
		this.m_dataSegmentSize = ReadHelper.readL(in, sb);
		this.m_offsetToHeadOfFreeList = ReadHelper.readL(in, sb);
		
		this.m_totalLocalHeapSize += sb.getSizeOfLengths() * 2;
		
		this.m_addressOfDataSegment = ReadHelper.readO(in, sb);
		
		this.m_totalLocalHeapSize += sb.getSizeOfOffsets();
		
		// data
		in.setOffset(this.m_addressOfDataSegment);
		this.m_data = in.readBytes((int)this.m_dataSegmentSize);
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public byte[] getSignature() {
		return this.m_signature;
	}
	
	public boolean isValidSignature() {
		for(int i=0;i<4;i++) {
			if(this.m_signature[i] != SIGNATURE[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public long getDataSegmentSize() {
		return this.m_dataSegmentSize;
	}
	
	public long getOffsetToHeadOfFreeList() {
		return this.m_offsetToHeadOfFreeList;
	}
	
	public long getAddressOfDataSegment() {
		return this.m_addressOfDataSegment;
	}
	
	public int getTotalLocalHeapSize() {
		return this.m_totalLocalHeapSize;
	}
	
	public byte[] getData() {
		return this.m_data;
	}
	
	public void printValues() {
		System.out.println("LocalHeap >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("signature : " + Integer.toHexString(this.m_signature[0] & 0xFF) + 
				Integer.toHexString(this.m_signature[1] & 0xFF) + 
				Integer.toHexString(this.m_signature[2] & 0xFF) +
				Integer.toHexString(this.m_signature[3] & 0xFF));
		
		System.out.println("version : " + this.m_version);
		System.out.println("data segment size : " + this.m_dataSegmentSize);
		System.out.println("offset to head of free list : " + this.m_offsetToHeadOfFreeList);
		System.out.println("address of data segment : " + this.m_addressOfDataSegment);
		
		System.out.println("total local heap size : " + this.m_totalLocalHeapSize);
		
		if(this.m_data != null) {
			for(int i=0;i<this.m_data.length;i++) {
				System.out.println("data[" + i + "] : " + this.m_data[i]);
			}
		}
		
		System.out.println("LocalHeap <<<");
	}

	public String getString(int offset) {
		int count = 0;
		while(this.m_data[offset + count] != 0)
			count++;
		
		return new String(this.m_data, offset, count);
	}
}
