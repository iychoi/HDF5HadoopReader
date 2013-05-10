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


public class ObjectHeader {
	private long m_address;
	private int m_version;
	private int m_numberOfMessages; 
	private int m_objectReferenceCount; 
	private int m_objectHeaderSize;
	
	private Vector<ObjectHeaderMessage> m_headerMessages;
	
	public ObjectHeader(BinaryReader in, Superblock sb, long address) throws IOException {
		
		in.setOffset(address);
		
		this.m_address = address;
		
		this.m_version = in.readByte();
		
		if(this.m_version == 1) {
			
			in.skipBytes(1);
			this.m_numberOfMessages = in.readShort();
			this.m_objectReferenceCount = in.readInt();
			this.m_objectHeaderSize = in.readInt();
			
			in.skipBytes(4);
			
			readVersion1(in, sb, in.getOffset(), this.m_numberOfMessages, Long.MAX_VALUE);
		} else {
			readVersion2(in, sb, in.getOffset());
		}
	}
	
	private int readVersion1(BinaryReader in, Superblock sb, long address, int readMessages, long maxBytes) throws IOException {
		
		in.setOffset(address);
		
		int count = 0;
		int byteRead = 0;
		
		if(this.m_headerMessages == null)
			this.m_headerMessages = new Vector<ObjectHeaderMessage>();
		
		// read messages
		long messageOffset = address;
		while(count < readMessages && byteRead < maxBytes) {
			ObjectHeaderMessage msg = new ObjectHeaderMessage(in, sb, messageOffset);

			messageOffset += msg.getHeaderLength() + msg.getSizeOfHeaderMessageData();
			byteRead += msg.getHeaderLength() + msg.getSizeOfHeaderMessageData();
			
			count++;
			
			if(msg.getHeaderMessageType() == ObjectHeaderMessageType.ObjectHeaderContinuation){
				// CONTINUE
				ContinueMessage cmsg = msg.getContinueMessage();
				long continuationBlockFilePos = cmsg.getOffset();

				count += readVersion1(in, sb, continuationBlockFilePos, readMessages - count, cmsg.getLength());
			} else if(msg.getHeaderMessageType() != ObjectHeaderMessageType.NIL) {
				// NOT NIL
				this.m_headerMessages.add(msg);
			}
		}
		return count;
	}
	
	private void readVersion2(BinaryReader in, Superblock sb, long address) throws IOException {
		throw new IOException("version not implented");
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public int getVersion() {
		return this.m_version;
	}
	
	public int getTotalNumberOfHeaderMessages() {
		return this.m_numberOfMessages;
	}
	
	public int getObjectReferenceCount() {
		return this.m_objectReferenceCount;
	}
	
	public int getObjectHeaderSize() {
		return this.m_objectHeaderSize;
	}
	
	public Vector<ObjectHeaderMessage> getHeaderMessages() {
		return this.m_headerMessages;
	}
	
	public void printValues() {
		System.out.println("ObjectHeader >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("version : " + this.m_version);
		System.out.println("number of messages : " + this.m_numberOfMessages);
		System.out.println("object reference count : " + this.m_objectReferenceCount);
		System.out.println("object header size : " + this.m_objectHeaderSize);
		
		for(int i=0;i<this.m_headerMessages.size();i++) {
			this.m_headerMessages.get(i).printValues();
		}
		
		System.out.println("ObjectHeader <<<");
	}
}
