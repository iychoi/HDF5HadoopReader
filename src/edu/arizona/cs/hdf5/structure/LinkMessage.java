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

public class LinkMessage {
	private long m_address;
	
	private int m_version;
	private byte m_flags;
	private byte m_encoding;
	private int m_linkType; // 0=hard, 1=soft, 64 = external
	private long m_creationOrder;
	private String m_linkName;
	private String m_link;
	private long m_linkAddress;
	
	
	public LinkMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_version = in.readByte();
		this.m_flags = in.readByte();
		
		if((this.m_flags & 0x8) != 0) {
			this.m_linkType = in.readByte();
		}
		
		if((this.m_flags & 0x4) != 0) {
			this.m_creationOrder = in.readLong();
		}
		
		if((this.m_flags & 0x10) != 0) {
			this.m_encoding = in.readByte();
		}
		
		int linkNameLength = (int)ReadHelper.readVariableSizeFactor(in, (this.m_flags & 0x3));
		this.m_linkName = in.readASCIIString(linkNameLength);
		
		if(this.m_linkType == 0) {
			// hard link
			this.m_linkAddress = ReadHelper.readO(in, sb);
		} else if(this.m_linkType == 1) {
			// soft link
			int len = in.readShort();
			this.m_link = in.readASCIIString(len);
		} else if(this.m_linkType == 64) {
			// external
			int len = in.readShort();
			this.m_link = in.readASCIIString(len);
		}
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public byte getFlags() {
		return this.m_flags;
	}
	
	public byte getEncoding() {
		return this.m_encoding;
	}
	
	public int getLinkType() {
		return this.m_linkType;
	}
	
	public long getCreationOrder() {
		return this.m_creationOrder;
	}
	
	public String getLinkName() {
		return this.m_linkName;
	}
	
	public String getLink() {
		return this.m_link;
	}
	
	public long getLinkAddress() {
		return this.m_linkAddress;
	}
	
	public void printValues() {
		System.out.println("LinkMessage >>>");
		
		System.out.println("address : " + this.m_address);
		System.out.println("version : " + this.m_version);
		System.out.println("flags : " + this.m_flags);
		System.out.println("encoding : " + this.m_encoding);
		System.out.println("link type : " + this.m_linkType);
		System.out.println("creation order : " + this.m_creationOrder);
		System.out.println("link name : " + this.m_linkName);
		System.out.println("link : " + this.m_link);
		System.out.println("link address : " + this.m_linkAddress);
		
		System.out.println("LinkMessage <<<");
	}
}
