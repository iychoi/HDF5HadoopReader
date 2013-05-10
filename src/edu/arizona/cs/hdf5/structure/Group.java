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


public class Group {

	private static Vector<DataObjectFacade> NESTED_OBJECTS = new Vector<DataObjectFacade>();

	private DataObjectFacade m_facade;

	public Group(BinaryReader in, Superblock sb, DataObjectFacade facade)
			throws IOException {
		this.m_facade = facade;

		if (facade.getDataObject().getGroupMessage() != null) {
			GroupMessage gm = facade.getDataObject().getGroupMessage();
			readGroup(in, sb, gm.getBTreeAddress(), gm.getNameHeapAddress());
		}
	}

	private void readGroup(BinaryReader in, Superblock sb, long bTreeAddress,
			long nameHeapAddress) throws IOException {
		LocalHeap nameHeap = new LocalHeap(in, sb, nameHeapAddress);
		GroupBTree btree = new GroupBTree(in, sb, bTreeAddress);
		
		for (SymbolTableEntry s : btree.getSymbolTableEntries()) {
			String sname = nameHeap.getString((int)s.getLinkNameOffset());
			if (s.getCacheType() == 2) {
				String linkName = nameHeap.getString((int)s.getLinkNameOffset());
				DataObjectFacade dobj = new DataObjectFacade(in, sb, sname, linkName);
				NESTED_OBJECTS.add(dobj);
			} else {
				DataObjectFacade dobj = new DataObjectFacade(in, sb, sname, s.getObjectHeaderAddress());
				NESTED_OBJECTS.add(dobj);
			}
		}
	}
	
	public Vector<DataObjectFacade> getObjects() {
		return NESTED_OBJECTS;
	}
	
	public void printValues() {
		System.out.println("Group >>>");
		
		if(NESTED_OBJECTS != null) {
			for(DataObjectFacade dobj : NESTED_OBJECTS) {
				dobj.printValues();
			}
		}
		
		System.out.println("Group <<<");
	}
}
