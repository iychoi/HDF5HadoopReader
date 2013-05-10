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

public class StructureMember {
	private long m_address;
	
	private String m_name;
    private int m_offset;
    private int m_dims;
    private DataTypeMessage m_message;
	
	public StructureMember(BinaryReader in, Superblock sb, long address, int version, int byteSize) throws IOException {
		this.m_address = address;
		
		in.setOffset(address);
		
		this.m_name = in.readASCIIString();
		if(version < 3) {
			in.skipBytes(ReadHelper.padding(this.m_name.length() + 1, 8));
			this.m_offset = in.readInt();
		} else {
			this.m_offset = (int) ReadHelper.readVariableSizeMax(in, byteSize);
		}
		
		if (version == 1) {
			this.m_dims = in.readByte();
			in.skipBytes(3);
			in.skipBytes(24); // ignore dimension info for now
		}
		
		this.m_message = new DataTypeMessage(in, sb, in.getOffset());
	}
	
	public long getAddress() {
		return this.m_address;
	}
	
	public String getName() {
		return this.m_name;
	}
	
    public int getOffset() {
    	return this.m_offset;
    }
    
    public int getDims() {
    	return this.m_dims;
    }
    
    public DataTypeMessage getMessage() {
    	return this.m_message;
    }

	public void printValues() {
		System.out.println("StructureMember >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("name : " + this.m_name);
		System.out.println("offset : " + this.m_offset);
		System.out.println("m_dims : " + this.m_dims);
		
		if(this.m_message != null) {
			this.m_message.printValues();
		}
		System.out.println("StructureMember >>>");
	}
}
