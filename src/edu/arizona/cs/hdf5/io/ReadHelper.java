/*
 * Some part of this code is copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.io;
import java.io.IOException;

import edu.arizona.cs.hdf5.structure.Superblock;


public class ReadHelper {
	public static long readO(BinaryReader in, Superblock sb) throws IOException {
		if(in == null) {
			throw new IllegalArgumentException("in is null");
		}
		
		if(sb == null) {
			throw new IllegalArgumentException("sb is null");
		}
		
		int sizeOfOffsets = sb.getSizeOfOffsets();
		if(sizeOfOffsets == 1) {
			return in.readByte();
		} else if(sizeOfOffsets == 2) {
			return in.readShort();
		} else if(sizeOfOffsets == 4) {
			return in.readInt();
		} else if(sizeOfOffsets == 8) {
			return in.readLong();
		}
		throw new IOException("size of offsets is not specified");
	}
	
	public static long readL(BinaryReader in, Superblock sb) throws IOException {
		if(in == null) {
			throw new IllegalArgumentException("in is null");
		}
		
		if(sb == null) {
			throw new IllegalArgumentException("sb is null");
		}
		
		int sizeOfLengths = sb.getSizeOfLengths();
		if(sizeOfLengths == 1) {
			return in.readByte();
		} else if(sizeOfLengths == 2) {
			return in.readShort();
		} else if(sizeOfLengths == 4) {
			return in.readInt();
		} else if(sizeOfLengths == 8) {
			return in.readLong();
		}
		throw new IOException("size of lengths is not specified");
	}
	
	public static int padding(int dataLen, int paddingSize) {
		if(dataLen < 0) {
			throw new IllegalArgumentException("dataLen is negative");
		}
		
		if(paddingSize <= 0) {
			throw new IllegalArgumentException("dataLen is 0 or negative"); 
		}
		
		int remain = dataLen % paddingSize;
		if(remain != 0)
			remain = paddingSize - remain;
		return remain;
	}

	public static int getNumBytesFromMax(long maxNumber) {
		int size = 0;
		while (maxNumber != 0) {
			size++;
			maxNumber >>>= 8; // right shift with zero extension
		}
		return size;
	}
	
	public static long readVariableSizeUnsigned(BinaryReader in, int size) throws IOException {
		long vv;
		if (size == 1) {
			vv = unsignedByteToShort(in.readByte());
		} else if (size == 2) {
			short s = in.readShort();
			vv = unsignedShortToInt(s);
		} else if (size == 4) {
			vv = unsignedIntToLong(in.readInt());
		} else if (size == 8) {
			vv = in.readLong();
		} else {
			vv = readVariableSizeN(in, size);
		}
		return vv;
	}

	public static long readVariableSizeMax(BinaryReader in, int maxNumber) throws IOException {
		int size = getNumBytesFromMax(maxNumber);
		return readVariableSizeUnsigned(in, size);
	}
	
	private static long readVariableSizeN(BinaryReader in, int nbytes) throws IOException {
		int[] ch = new int[nbytes];
		for (int i = 0; i < nbytes; i++)
			ch[i] = in.readByte();

		long result = ch[nbytes - 1];
		for (int i = nbytes - 2; i >= 0; i--) {
			result = result << 8;
			result += ch[i];
		}

		return result;
	}
	
	public static long unsignedIntToLong(int i) {
		return (i < 0) ? (long) i + 4294967296L : (long) i;
	}

	public static int unsignedShortToInt(short s) {
		return (s & 0xffff);
	}

	public static short unsignedByteToShort(byte b) {
		return (short) (b & 0xff);
	}
	
	public static int bytesToUnsignedInt(byte upper, byte lower) {
		return unsignedByteToShort(upper) * 256 + unsignedByteToShort(lower);
	}
	
	public static String readString8(BinaryReader in) throws IOException {
		long filePos = in.getOffset();

		String str = in.readASCIIString();

		long newFilePos = in.getOffset();
		
		int readCount = (int)(newFilePos - filePos);
		
		// skip to 8 byte boundary, note zero byte is skipped
		int padding = padding(readCount, 8);
		in.skipBytes(padding);
		
		return str;
	}
	
	public static long readVariableSizeFactor(BinaryReader in, int sizeFactor) throws IOException {
		int size = (int) Math.pow(2, sizeFactor);
		return readVariableSizeUnsigned(in, size);
	}
}
