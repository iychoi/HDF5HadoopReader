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

public class DataTypeMessage {

	public static final int DATATYPE_FIXED_POINT = 0;
	public static final int DATATYPE_FLOATING_POINT = 1;
	public static final int DATATYPE_TIME = 2;
	public static final int DATATYPE_STRING = 3;
	public static final int DATATYPE_BIT_FIELD = 4;
	public static final int DATATYPE_OPAQUE = 5;
	public static final int DATATYPE_COMPOUND = 6;
	public static final int DATATYPE_REFERENCE = 7;
	public static final int DATATYPE_ENUMS = 8;
	public static final int DATATYPE_VARIABLE_LENGTH = 9;
	public static final int DATATYPE_ARRAY = 10;

	private long m_address;
	private int m_type;
	private int m_version;
	private byte[] m_flags;
	private int m_byteSize;
	private boolean m_littleEndian;

	private boolean m_unsigned;
	private int m_timeTypeByteSize;
	private String m_opaqueDesc;
	private int m_referenceType;
	private Vector<StructureMember> m_members;
	private boolean m_isOK;
	
	private DataTypeMessage m_base;

	public DataTypeMessage(BinaryReader in, Superblock sb, long address)
			throws IOException {
		in.setOffset(address);

		this.m_address = address;

		byte tandv = in.readByte();
		this.m_type = (tandv & 0xf);
		this.m_version = ((tandv & 0xf0) >> 4);

		this.m_flags = in.readBytes(3);
		this.m_byteSize = in.readInt();
		this.m_littleEndian = ((this.m_flags[0] & 0x1) == 0);
		this.m_timeTypeByteSize = 4;

		this.m_isOK = true;

		if (this.m_type == DATATYPE_FIXED_POINT) {
			this.m_unsigned = ((this.m_flags[0] & 0x8) == 0);

			short bitOffset = in.readShort();
			short bitPrecision = in.readShort();

			this.m_isOK = (bitOffset == 0) && (bitPrecision % 8 == 0);
		} else if (this.m_type == DATATYPE_FLOATING_POINT) {
			short bitOffset = in.readShort();
			short bitPrecision = in.readShort();
			byte expLocation = in.readByte();
			byte expSize = in.readByte();
			byte manLocation = in.readByte();
			byte manSize = in.readByte();
			int expBias = in.readInt();
		} else if (this.m_type == DATATYPE_TIME) {
			short bitPrecision = in.readShort();
			this.m_timeTypeByteSize = bitPrecision / 8;
		} else if (this.m_type == DATATYPE_STRING) {
			int ptype = this.m_flags[0] & 0xf;
		} else if (this.m_type == DATATYPE_BIT_FIELD) {
			short bitOffset = in.readShort();
			short bitPrecision = in.readShort();
		} else if (this.m_type == DATATYPE_OPAQUE) {
			byte len = this.m_flags[0];
			this.m_opaqueDesc = (len > 0) ? in.readASCIIString(len).trim() : null;
		} else if (this.m_type == DATATYPE_COMPOUND) {
			int nmembers = (this.m_flags[1] * 256) + this.m_flags[0];
			this.m_members = new Vector<StructureMember>();

			for (int i = 0; i < nmembers; i++) {
				this.m_members.add(new StructureMember(in, sb, in.getOffset(), this.m_version, this.m_byteSize));
			}
		} else if (this.m_type == DATATYPE_REFERENCE) {
	        this.m_referenceType = this.m_flags[0] & 0xf;
		} else if (this.m_type == DATATYPE_ENUMS) {
			throw new IOException("data type enums is not implemented");
			/*
			int nmembers = ReadHelper.bytesToUnsignedInt(this.m_flags[1], this.m_flags[0]);
	        this.m_base = new DataTypeMessage(in, sb, in.getOffset()); // base type

			// read the enums
			String[] enumName = new String[nmembers];
			for (int i = 0; i < nmembers; i++) {
				if (this.m_version < 3)
					enumName[i] = ReadHelper.readString8(in); // padding
				else
					enumName[i] = in.readASCIIString(); // no padding
			}

	        // read the values; must switch to base byte order (!)
	        if (!this.m_base.m_littleEndian)
	        	in.setBigEndian();
	        
	        int[] enumValue = new int[nmembers];
			for (int i = 0; i < nmembers; i++)
				enumValue[i] = (int) ReadHelper.readVariableSizeUnsigned(in, this.m_base.m_byteSize); // assume size is 1, 2, or 4

	        in.setLittleEndian();

	        enumTypeName = objectName;
	        map = new TreeMap<Integer, String>();
	        for (int i = 0; i < nmembers; i++)
	          map.put(enumValue[i], enumName[i]);
	        */
		} else if(this.m_type == DATATYPE_VARIABLE_LENGTH) {
			throw new IOException("data type variable length is not implemented");
		} else if(this.m_type == DATATYPE_ARRAY) {
			throw new IOException("data type array is not implemented");
		}
	}

	public long getAddress() {
		return this.m_address;
	}
	
	public boolean getIsLittleEndian() {
		return this.m_littleEndian;
	}
	
	public boolean getIsBigEndian() {
		return !this.m_littleEndian;
	}
	
	public int getType() {
		return this.m_type;
	}
	
	public int getByteSize() {
		return this.m_byteSize;
	}
	
	public Vector<StructureMember> getStructureMembers() {
		return this.m_members;
	}

	public void printValues() {
		System.out.println("DataTypeMessage >>>");
		System.out.println("address : " + this.m_address);
		System.out.println("data type : " + this.m_type);
		System.out.println("byteSize : " + this.m_byteSize);
		
		if(this.m_members != null) {
			for(StructureMember mem : this.m_members) {
				mem.printValues();
			}
		}
		System.out.println("DataTypeMessage <<<");
	}

}
