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
import edu.arizona.cs.hdf5.io.ReadHelper;

public class GroupBTree {

	public static final byte[] SIGNATURE = {'T', 'R', 'E', 'E'};
	
	private long m_address;
	
	private Vector<SymbolTableEntry> m_SymbolTableEntries;
	
	public GroupBTree(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_SymbolTableEntries = new Vector<SymbolTableEntry>();
		
		Vector<BTreeEntry> entryList = new Vector<BTreeEntry>();
		readAllEntries(in, sb, address, entryList);
		
		for (BTreeEntry e : entryList) {
			GroupNode node = new GroupNode(in, sb, e.getTargetAddress());
			m_SymbolTableEntries.addAll(node.getSymbols());
		}
	}

	private void readAllEntries(BinaryReader in, Superblock sb, long address, Vector<BTreeEntry> entryList) throws IOException {
		in.setOffset(address);
		
		byte[] signature = in.readBytes(4);
		for(int i=0;i<4;i++) {
			if(signature[i] != SIGNATURE[i]) {
				throw new IOException("signature is not valid");
			}
		}
		
		int type = in.readByte();
		int level = in.readByte();
		int entryNum = in.readShort();
		
		long leftAddress = ReadHelper.readO(in, sb);
		long rightAddress = ReadHelper.readO(in, sb);
		
		Vector<BTreeEntry> myEntries = new Vector<BTreeEntry>();
		for(int i=0;i<entryNum;i++) {
			myEntries.add(new BTreeEntry(in, sb, in.getOffset()));
		}
		
		if(level == 0) {
			entryList.addAll(myEntries);
		} else {
			for(BTreeEntry entry : myEntries) {
				readAllEntries(in, sb, entry.getTargetAddress(), entryList);
			}
		}
	}

	public Vector<SymbolTableEntry> getSymbolTableEntries() {
		return this.m_SymbolTableEntries;
	}
	
	public void printValues() {
		System.out.println("GroupBTree >>>");
		System.out.println("address : " + this.m_address);
		
		for(int i=0;i<this.m_SymbolTableEntries.size();i++) {
			this.m_SymbolTableEntries.get(i).printValues();
		}
		
		System.out.println("GroupBTree <<<");
	}
}
