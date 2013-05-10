/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;

import edu.arizona.cs.hdf5.io.BinaryReader;


public class ObjectHeaderMessage {
	private long m_address;
	
	private ObjectHeaderMessageType m_headerMessageType;
	private int m_sizeOfHeaderMessageData;
	private byte m_headerMessageFlags;
	private GroupMessage m_groupMessage;
	private FillValueMessage m_fillvalueMessage;
	private FillValueOldMessage m_fillvalueoldMessage;
	private ContinueMessage m_continueMessage;
	private DataTypeMessage m_datatypeMessage;
	private DataspaceMessage m_dataspaceMessage;
	private AttributeMessage m_attributeMessage;
	private LinkMessage m_linkMessage;
	private LayoutMessage m_layoutMessage;
	private LastModifiedMessage m_lastmodifiedMessage;
	private int m_headerLength;
	
	private byte[] m_headerMessageData;
	
	public ObjectHeaderMessage(BinaryReader in, Superblock sb, long address) throws IOException {
		in.setOffset(address);
	
		this.m_address = address;
		
		short messageTypeNo = in.readShort();
		this.m_headerMessageType = ObjectHeaderMessageType.getType(messageTypeNo);
		if(this.m_headerMessageType == null) {
			throw new IOException("message type no (" + messageTypeNo + ") not supported");
		}
		this.m_sizeOfHeaderMessageData = in.readShort();
		this.m_headerMessageFlags = in.readByte();
		
		in.skipBytes(3);
		
		this.m_headerLength = 8;
		
		if((this.m_headerMessageFlags & 0x2) != 0) {
			// shared
			throw new IOException("shared message is not implemented");
		}
		
		if(this.m_headerMessageType == ObjectHeaderMessageType.ObjectHeaderContinuation) {
			this.m_continueMessage = new ContinueMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Group) {
			this.m_groupMessage = new GroupMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.NIL) {
			// do nothing
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.SimpleDataspace) {
			this.m_dataspaceMessage = new DataspaceMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.GroupNew) {
			throw new IOException("Group New not implemented");
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Datatype) {
			this.m_datatypeMessage = new DataTypeMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.FillValueOld) {
			this.m_fillvalueoldMessage = new FillValueOldMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.FillValue) {
			this.m_fillvalueMessage = new FillValueMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Link) {
			this.m_linkMessage = new LinkMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Layout) {
			this.m_layoutMessage = new LayoutMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.GroupInfo) {
			throw new IOException("Group Info not implemented");
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.FilterPipeline) {
			throw new IOException("Filter Pipeline not implemented");
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Attribute) {
			this.m_attributeMessage = new AttributeMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Comment) {
			throw new IOException("Comment not implemented");
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.LastModifiedOld) {
			throw new IOException("Last Modified Old not implemented");
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.LastModified) {
			this.m_lastmodifiedMessage = new LastModifiedMessage(in, sb, in.getOffset());
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.AttributeInfo) {
			throw new IOException("Attribute Info not implemented");
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.ObjectReferenceCount) {
			throw new IOException("Object Reference Count not implemented");
		} else {
			this.m_headerMessageData = in.readBytes(this.m_sizeOfHeaderMessageData);
		}
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getHeaderMessageTypeNo() {
		return this.m_headerMessageType.getNum();
	}
	
	public ObjectHeaderMessageType getHeaderMessageType() {
		return this.m_headerMessageType;
	}
	
	public int getSizeOfHeaderMessageData() {
		return this.m_sizeOfHeaderMessageData;
	}
	
	public byte getHeaderMessageFlags() {
		return this.m_headerMessageFlags;
	}
	
	public int getHeaderLength() {
		return this.m_headerLength;
	}
	
	public byte[] getHeaderMessageData() {
		return this.m_headerMessageData;
	}
	
	public GroupMessage getGroupMessage() {
		return this.m_groupMessage;
	}
	
	public ContinueMessage getContinueMessage() { 
		return this.m_continueMessage;
	}
	
	public FillValueMessage getFillValueMessage() {
		return this.m_fillvalueMessage;
	}
	
	public FillValueOldMessage getFillValueOldMessage() {
		return this.m_fillvalueoldMessage;
	}
	
	public DataTypeMessage getDataTypeMessage() {
		return this.m_datatypeMessage;
	}
	
	public AttributeMessage getAttributeMessage() {
		return this.m_attributeMessage;
	}
	
	public LinkMessage getLinkMessage() {
		return this.m_linkMessage;
	}
	
	public LayoutMessage getLayoutMessage() {
		return this.m_layoutMessage;
	}
	
	public LastModifiedMessage getLastModifiedMessage() {
		return this.m_lastmodifiedMessage;
	}
	
	public DataspaceMessage getDataspaceMessage() {
		return this.m_dataspaceMessage;
	}
	
	public void printValues() {
		System.out.println("ObjectHeaderMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("header message type : " + this.m_headerMessageType);
		System.out.println("size of header message data : " + this.m_sizeOfHeaderMessageData);
		System.out.println("header message flags : " + this.m_headerMessageFlags);
		
		if(this.m_headerMessageType == ObjectHeaderMessageType.ObjectHeaderContinuation) {
			System.out.println("header message continue");
			this.m_continueMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Group) {
			this.m_groupMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.SimpleDataspace) {
			this.m_dataspaceMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.FillValue) {
			this.m_fillvalueMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.FillValueOld) {
			this.m_fillvalueoldMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Datatype) {
			this.m_datatypeMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Attribute) {
			this.m_attributeMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Link) {
			this.m_linkMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.Layout) {
			this.m_layoutMessage.printValues();
		} else if(this.m_headerMessageType == ObjectHeaderMessageType.LastModified) {
			this.m_lastmodifiedMessage.printValues();
		} else {
			System.out.println("header message data : " + this.m_headerMessageData);
		}
		
		System.out.println("ObjectHeaderMessage <<<");
	}
}
