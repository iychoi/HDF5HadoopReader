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


public class BLinkTreeNode {
	public static final byte[] SIGNATURE = {'T', 'R', 'E', 'E'};
	
	private byte[] m_signature;
	private int m_nodeType;
	private int m_nodeLevel;
	private int m_entriesUsed;
	
	private long m_addressOfLeftSibling;
	private long m_addressOfRightSibling;
	
	private Vector<Long> m_offsetToLocalHeap;
	private Vector<byte[]> m_keyOfChild;
	
	private Vector<Long> m_addressOfChild;
	
	private int m_totalBLinkTreeNodeSize;
	
	public BLinkTreeNode(BinaryReader in, Superblock sb) throws IOException {
		
		// signature
		this.m_signature = new byte[4];
		
		for(int i=0;i<4;i++) {
			this.m_signature[i] = in.readByte();
		}
		
		if(!this.isValidSignature()) {
			throw new IOException("signature is not valid");
		}
		
		this.m_nodeType = in.readByte();
		this.m_nodeLevel = in.readByte();
		this.m_entriesUsed = in.readShort();
		
		this.m_totalBLinkTreeNodeSize = 8;
		
		this.m_addressOfLeftSibling = ReadHelper.readO(in, sb);
		this.m_addressOfRightSibling = ReadHelper.readO(in, sb);
		
		this.m_totalBLinkTreeNodeSize += sb.getSizeOfOffsets() * 2;
		
		this.m_offsetToLocalHeap = new Vector<Long>();
		this.m_keyOfChild = new Vector<byte[]>();
		this.m_addressOfChild = new Vector<Long>();
		
		for(int i=0;i<this.m_entriesUsed;i++) {
			if(this.m_nodeType == 0) {
				Long key = ReadHelper.readL(in, sb);
				this.m_offsetToLocalHeap.add(key);
			} else if(this.m_nodeType == 1) {
				int chunksize = in.readInt();
				int filtermask = in.readInt();
			} else {
				throw new IOException("node type is not implemented");
			}
		}
	}
	
	public byte[] getSignature() {
		return this.m_signature;
	}
	
	public boolean isValidSignature() {
		for(int i=0;i<4;i++) {
			if(this.m_signature[i] != SIGNATURE[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int getTotalBLinkTreeNodeSize() {
		return this.m_totalBLinkTreeNodeSize;
	}
	
	public void printValues() {
		System.out.println("BLinkTreeNode >>>");
		System.out.println("signature : " + Integer.toHexString(this.m_signature[0] & 0xFF) + 
				Integer.toHexString(this.m_signature[1] & 0xFF) + 
				Integer.toHexString(this.m_signature[2] & 0xFF) +
				Integer.toHexString(this.m_signature[3] & 0xFF));
		/*
		System.out.println("version : " + this.m_version);
		System.out.println("data segment size : " + this.m_dataSegmentSize);
		System.out.println("offset to head of free list : " + this.m_offsetToHeadOfFreeList);
		System.out.println("address of data segment : " + this.m_addressOfDataSegment);
		
		System.out.println("total local heap size : " + this.m_totalLocalHeapSize);
		*/
		System.out.println("BLinkTreeNode <<<");
	}
}
