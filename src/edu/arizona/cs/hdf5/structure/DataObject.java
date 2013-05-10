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


public class DataObject {
	private long m_address;
	private ObjectHeader m_objectHeader;
	private GroupMessage m_groupMessage;
	
	public DataObject(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_objectHeader = new ObjectHeader(in, sb, address);
		
		for(ObjectHeaderMessage msg : this.m_objectHeader.getHeaderMessages()) {
			if(msg.getHeaderMessageType() == ObjectHeaderMessageType.Group) {
				this.m_groupMessage = msg.getGroupMessage();
			}
		}
	}

	public long getAddress() {
		return this.m_address;
	}
	
	public Vector<ObjectHeaderMessage> getMessages() {
		if(this.m_objectHeader != null) {
			return this.m_objectHeader.getHeaderMessages();
		}
		return null;
	}
	
	public void printValues() {
		System.out.println("DataObject >>>");
		System.out.println("address : " + this.m_address);
		if(this.m_objectHeader != null)
			this.m_objectHeader.printValues();
		System.out.println("DataObject <<<");
	}

	public GroupMessage getGroupMessage() {
		return m_groupMessage;
	}
}
