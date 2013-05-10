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


public class SymbolTableEntry {
	private long m_address;
	private long m_linkNameOffset;
	private long m_objectHeaderAddress;
	private int m_cacheType; 
	private int m_reserved; 
	private byte[] m_scratchpadSpace; //16
	private int m_size;
	
	// case m_cacheType = 1
	private ObjectHeaderScratchpadFormat m_objectHeaderScratchpadFormat;
	// case m_cacheType = 2
	private SymbolicLinkScratchpadFormat m_symbolicLinkScratchpadFormat;
	
	private int m_totalSymbolTableEntrySize;
	
	public SymbolTableEntry(BinaryReader in, Superblock sb, long address) throws IOException {
		
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_linkNameOffset = ReadHelper.readO(in, sb);
		this.m_objectHeaderAddress = ReadHelper.readO(in, sb);
		
		this.m_totalSymbolTableEntrySize = sb.getSizeOfOffsets() * 2;
		
		this.m_cacheType = in.readInt();
		this.m_reserved = in.readInt();
		
		this.m_totalSymbolTableEntrySize += 8;
		
		if(this.m_cacheType == 0) {
			this.m_scratchpadSpace = in.readBytes(16);
		} else if(this.m_cacheType == 1) {
			this.m_objectHeaderScratchpadFormat = new ObjectHeaderScratchpadFormat(in, sb, in.getOffset());
			
			// skip 
			int size = this.m_objectHeaderScratchpadFormat.getTotalObjectHeaderScratchpadFormatSize();
			int remained = 16 - size;
			in.skipBytes(remained);
		} else if(this.m_cacheType == 2) {
			this.m_symbolicLinkScratchpadFormat = new SymbolicLinkScratchpadFormat(in, sb, in.getOffset());
			
			// skip
			int size = this.m_symbolicLinkScratchpadFormat.getTotalSymbolicLinkScratchpadFormatSize();
			int remained = 16 - size;
			in.skipBytes(remained);
		}
		
		this.m_totalSymbolTableEntrySize += 16;
		
		if(sb.getSizeOfOffsets() == 8) {
			this.m_size = 40; 
		} else {
			this.m_size = 32;
		}
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public long getLinkNameOffset() {
		return this.m_linkNameOffset;
	}
	
	public long getObjectHeaderAddress() {
		return this.m_objectHeaderAddress;
	}
	
	public int getCacheType() {
		return this.m_cacheType;
	}
	
	public byte[] getScratchpadSpace() {
		return this.m_scratchpadSpace;
	}
	
	public ObjectHeaderScratchpadFormat getObjectHeaderScratchpadFormat() {
		// only work for cache type = 1
		return this.m_objectHeaderScratchpadFormat;
	}
	
	public SymbolicLinkScratchpadFormat getSymbolicLinkScratchpadFormat() {
		// only work for cache type = 2
		return this.m_symbolicLinkScratchpadFormat;
	}
	
	public int getTotalSymbolTableEntrySize() {
		return this.m_totalSymbolTableEntrySize;
	}
	
	public long getSize() {
		return this.m_size;
	}
	
	public void printValues() {
		System.out.println("SymbolTableEntry >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("link name offset : " + this.m_linkNameOffset);
		System.out.println("object header address : " + this.m_objectHeaderAddress);
		System.out.println("cache type : " + this.m_cacheType);
		System.out.println("reserved : " + this.m_reserved);
		
		if(this.m_cacheType == 0) {
			System.out.println("scratchpad space : " + Integer.toHexString(this.m_scratchpadSpace[0] & 0xFF) + 
					Integer.toHexString(this.m_scratchpadSpace[1] & 0xFF) + 
					Integer.toHexString(this.m_scratchpadSpace[2] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[3] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[4] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[5] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[6] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[7] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[8] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[9] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[10] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[11] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[12] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[13] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[14] & 0xFF) +
					Integer.toHexString(this.m_scratchpadSpace[15] & 0xFF));
		} else if(this.m_cacheType == 1) {
			this.m_objectHeaderScratchpadFormat.printValues();
		} else if(this.m_cacheType == 2) {
			this.m_symbolicLinkScratchpadFormat.printValues();
		}
		
		System.out.println("total symbol table entry size : " + this.m_totalSymbolTableEntrySize);
		System.out.println("SymbolTableEntry <<<");
	}
}
