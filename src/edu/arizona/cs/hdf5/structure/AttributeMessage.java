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

public class AttributeMessage {
	private long m_address;
	private int m_version;
	private String m_name;
	private DataTypeMessage m_dataTypeMessage;
	private DataspaceMessage m_dataspaceMessage;
	private long m_dataPos;
	
	public AttributeMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		short nameSize, typeSize, spaceSize;
		byte flags = 0;
		byte encoding = 0; // 0 = ascii, 1 = UTF-8

		this.m_version = in.readByte();
		
		if (this.m_version == 1) {
			in.skipBytes(1);
			
			nameSize = in.readShort();
			typeSize = in.readShort();
			spaceSize = in.readShort();
		} else if ((this.m_version == 2) || (this.m_version == 3)) {
			flags = in.readByte();
			nameSize = in.readShort();
			typeSize = in.readShort();
			spaceSize = in.readShort();
			
			if (this.m_version == 3) {
				encoding = in.readByte();
			}
		} else {
			throw new IOException("version error");
		}

		// read the attribute name
		long filePos = in.getOffset();
		this.m_name = in.readASCIIString(nameSize); // read at current pos
		if (this.m_version == 1) {
			nameSize += ReadHelper.padding(nameSize, 8);
		}
		
		in.setOffset(filePos + nameSize);

		// read the datatype
		filePos = in.getOffset();
		
		boolean isShared = (flags & 1) != 0;
		
		if (isShared) {
			throw new IOException("shared data object is not implemented");
			//mdt = getSharedDataObject(MessageType.Datatype).mdt;
		} else {
			this.m_dataTypeMessage = new DataTypeMessage(in, sb, in.getOffset());
			if (this.m_version == 1) {
				typeSize += ReadHelper.padding(typeSize, 8);
			}
		}
		
		in.setOffset(filePos + typeSize); // make it more robust for errors

		// read the dataspace
		filePos = in.getOffset();
		this.m_dataspaceMessage = new DataspaceMessage(in, sb, in.getOffset());

		if (this.m_version == 1)
			spaceSize += ReadHelper.padding(spaceSize, 8);
		in.setOffset(filePos + spaceSize); // make it more robust for errors

		// the data starts immediately afterward - ie in the message
		this.m_dataPos = in.getOffset(); // note this is absolute position (no
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public String getName() {
		return this.m_name;
	}
	
	public long getDataPos() {
		return this.m_dataPos;
	}
	
	public DataTypeMessage getDataType() {
		return this.m_dataTypeMessage;
	}
	
	public DataspaceMessage getDataSpace() {
		return this.m_dataspaceMessage;
	}
	
	public void printValues() {
		System.out.println("AttributeMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("version : " + this.m_version);
		System.out.println("name : " + this.m_name);
		
		if(this.m_dataTypeMessage != null) {
			this.m_dataTypeMessage.printValues();
		}
		
		if(this.m_dataspaceMessage != null) {
			this.m_dataspaceMessage.printValues();
		}
		
		System.out.println("data pos : " + this.m_dataPos);
		
		System.out.println("AttributeMessage <<<");
	}
}
