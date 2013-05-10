/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;
import java.util.Vector;

import edu.arizona.cs.hdf5.io.BinaryReader;

public class GroupNode {

	public static final byte[] SIGNATURE = {'S', 'N', 'O', 'D'};
	
	private long m_address;
	private byte[] m_signature;
	private int m_version;
	private int m_entryNumber;
	
	private Vector<SymbolTableEntry> m_symbols;
	
	public GroupNode(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		this.m_signature = in.readBytes(4);
		
		if(!this.isValidSignature()) {
			throw new IOException("signature is not valid");
		}
		
		this.m_version = in.readByte();
		in.skipBytes(1);
		
		this.m_entryNumber = in.readShort();
		
		this.m_symbols = new Vector<SymbolTableEntry>();
		
		long entryPos = in.getOffset();
		for(int i=0;i<this.m_entryNumber;i++) {
			SymbolTableEntry entry = new SymbolTableEntry(in, sb, entryPos);
			entryPos += entry.getSize();
			if(entry.getObjectHeaderAddress() != 0) {
				m_symbols.add(entry);
			}
		}
	}
	
	private boolean isValidSignature() {
		for(int i=0;i<4;i++) {
			if(this.m_signature[i] != SIGNATURE[i]) {
				return false;
			}
		}
		return true;
	}

	public long getAddress() {
		return this.m_address;
	}
	
	public byte[] getSignature() {
		return this.m_signature;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public int getEntryNumber() {
		return this.m_entryNumber;
	}
	
	public Vector<SymbolTableEntry> getSymbols() {
		return this.m_symbols;
	}

	public void printValues() {
		System.out.println("GroupNode >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("signature : " + Integer.toHexString(this.m_signature[0] & 0xFF) + 
				Integer.toHexString(this.m_signature[1] & 0xFF) + 
				Integer.toHexString(this.m_signature[2] & 0xFF) +
				Integer.toHexString(this.m_signature[3] & 0xFF));
		
		System.out.println("version : " + this.m_version);
		System.out.println("entry number : " + this.m_entryNumber);
		
		for(int i=0;i<this.m_symbols.size();i++) {
			this.m_symbols.get(i).printValues();
		}
		
		System.out.println("GroupNode <<<");
	}
}
