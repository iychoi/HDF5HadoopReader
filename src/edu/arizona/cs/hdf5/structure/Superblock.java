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



public class Superblock {
	public static final byte[] FORMAT_SIGNATURE = {(byte)0x89, (byte)0x48, (byte)0x44, (byte)0x46, (byte)0x0d, (byte)0x0a, (byte)0x1a, (byte)0x0a};
	
	private long m_address;
	
	private byte[] m_formatSignature;
	private int m_versionOfSuperblock;
	private int m_versionOfFileFreeSpaceStorage;
	private int m_versionOfRootGroupSymbolTableEntry;
	private int m_reserved0;
	private int m_versionOfShardedHeaderMessageFormat;
	private int m_sizeOfOffsets;
	private int m_sizeOfLengths;
	private int m_reserved1;
	private int m_groupLeafNodeK;
	private int m_groupInternalNodeK;
	private int m_fileConsistencyFlags;

	// for ver1
	private int m_indexedStorageInterNodeK;
	private int m_reserved2;
	
	private long m_baseAddress;
	private long m_addressOfFileFreeSpaceInfo;
	private long m_endOfFileAddress;
	private long m_driverInformationBlockAddress;
	
	private SymbolTableEntry m_rootGroupSymbolTableEntry;
	
	private int m_totalSuperBlockSize;
	
	public Superblock(BinaryReader in, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		// signature
		this.m_formatSignature = in.readBytes(8);
		
		if(!this.isValidFormatSignature()) {
			throw new IOException("signature is not valid");
		}
		
		this.m_versionOfSuperblock = in.readByte();
		
		if (this.m_versionOfSuperblock <= 1) {
			readVersion1(in);
	    } else if (this.m_versionOfSuperblock == 2) {
	    	readVersion2(in);
	    } else {
	    	throw new IOException("Unknown superblock version " + this.m_versionOfSuperblock);
	    }
	}
	
	private void readVersion1(BinaryReader in) throws IOException {
		this.m_versionOfFileFreeSpaceStorage = in.readByte();
		this.m_versionOfRootGroupSymbolTableEntry = in.readByte();
		this.m_reserved0 = in.readByte();
		this.m_versionOfShardedHeaderMessageFormat = in.readByte();
		this.m_sizeOfOffsets = in.readByte();
		this.m_sizeOfLengths = in.readByte();
		this.m_reserved1 = in.readByte();
		
		this.m_groupLeafNodeK = in.readShort();
		this.m_groupInternalNodeK = in.readShort();
		this.m_fileConsistencyFlags = in.readInt();
		
		this.m_totalSuperBlockSize = 24;
		
		if(this.m_versionOfSuperblock == 1) {
			this.m_indexedStorageInterNodeK = in.readShort();
			this.m_reserved2 = in.readShort();
			
			this.m_totalSuperBlockSize += 4;
		}
		
		this.m_baseAddress = ReadHelper.readO(in, this);
		this.m_addressOfFileFreeSpaceInfo = ReadHelper.readO(in, this);
		this.m_endOfFileAddress = ReadHelper.readO(in, this);
		this.m_driverInformationBlockAddress = ReadHelper.readO(in, this);
		
		this.m_totalSuperBlockSize += this.m_sizeOfOffsets * 4;
		
		this.m_rootGroupSymbolTableEntry = new SymbolTableEntry(in, this, in.getOffset());
		
		this.m_totalSuperBlockSize += this.m_rootGroupSymbolTableEntry.getTotalSymbolTableEntrySize();	
	}
	
	private void readVersion2(BinaryReader in) throws IOException {
		throw new IOException("version 2 is not implemented");
	}
	
	public byte[] getFormatSignature() {
		return this.m_formatSignature;
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public boolean isValidFormatSignature() {
		for(int i=0;i<8;i++) {
			if(this.m_formatSignature[i] != FORMAT_SIGNATURE[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int getVersionOfSuperblock() {
		return this.m_versionOfSuperblock;
	}
	
	public int getVersionOfFileFreeSpaceStorage() {
		return this.m_versionOfFileFreeSpaceStorage;
	}
	
	public int getVersionOfRootGroupSymbolTableEntry() {
		return this.m_versionOfRootGroupSymbolTableEntry;
	}
	
	public int getVersionOfShardedHeaderMessageFormat() {
		return this.m_versionOfShardedHeaderMessageFormat;
	}
	
	public int getSizeOfOffsets() {
		return this.m_sizeOfOffsets;
	}
	
	public int getSizeOfLengths() {
		return this.m_sizeOfLengths;
	}
	
	public int getGroupLeafNodeK() {
		return this.m_groupLeafNodeK;
	}
	
	public int getGroupInternalNodeK() {
		return this.m_groupInternalNodeK;
	}
	
	public int getFileConsistencyFlags() {
		return this.m_fileConsistencyFlags;
	}

	// for ver1
	public int getIndexedStorageInterNodeK() {
		return this.m_indexedStorageInterNodeK;
	}
	
	public long getBaseAddress() {
		return this.m_baseAddress;
	}
	
	public long getAddressOfFileFreeSpaceInfo() {
		return this.m_addressOfFileFreeSpaceInfo;
	}
	
	public long getEndOfFileAddress() {
		return this.m_endOfFileAddress;
	}
	
	public long getDriverInformationBlockAddress() {
		return this.m_driverInformationBlockAddress;
	}
	
	public SymbolTableEntry getRootGroupSymbolTableEntry() {
		return this.m_rootGroupSymbolTableEntry;
	}
	
	public int getTotalSuperBlockSize() {
		return this.m_totalSuperBlockSize;
	}
	
	public void printValues() {
		System.out.println("Superblock >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("signature : " + Integer.toHexString(this.m_formatSignature[0] & 0xFF) + 
				Integer.toHexString(this.m_formatSignature[1] & 0xFF) + 
				Integer.toHexString(this.m_formatSignature[2] & 0xFF) +
				Integer.toHexString(this.m_formatSignature[3] & 0xFF) +
				Integer.toHexString(this.m_formatSignature[4] & 0xFF) +
				Integer.toHexString(this.m_formatSignature[5] & 0xFF) +
				Integer.toHexString(this.m_formatSignature[6] & 0xFF) +
				Integer.toHexString(this.m_formatSignature[7] & 0xFF));
		
		System.out.println("version of super block : " + this.m_versionOfSuperblock);
		System.out.println("version of file free space storage : " + this.m_versionOfFileFreeSpaceStorage);
		System.out.println("version of root group symbol table entry : " + this.m_versionOfRootGroupSymbolTableEntry);
		System.out.println("reserved 0 : " + this.m_reserved0);
		System.out.println("version of sharded header message format : " + this.m_versionOfShardedHeaderMessageFormat);
		System.out.println("size of offsets : " + this.m_sizeOfOffsets);
		System.out.println("size of lengths : " + this.m_sizeOfLengths);
		System.out.println("reserved 1 : " + this.m_reserved1);
		System.out.println("group leaf node k : " + this.m_groupLeafNodeK);
		System.out.println("group internal node k : " + this.m_groupInternalNodeK);
		System.out.println("file consistency flags : " + this.m_fileConsistencyFlags);
		
		if(this.m_versionOfSuperblock >= 1) {
			System.out.println("indexed storage internode k : " + this.m_indexedStorageInterNodeK);
			System.out.println("reserved 2 : " + this.m_reserved2);
		}
		
		System.out.println("base address : " + this.m_baseAddress);
		System.out.println("address of file free space info : " + this.m_addressOfFileFreeSpaceInfo);
		System.out.println("end of file address : " + this.m_endOfFileAddress);
		System.out.println("driver information block address : " + this.m_driverInformationBlockAddress);
		
		this.m_rootGroupSymbolTableEntry.printValues();
		
		System.out.println("total super block size : " + this.m_totalSuperBlockSize);
		System.out.println("Superblock <<<");
	}
}
