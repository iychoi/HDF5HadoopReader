package edu.arizona.cs.hdf5.io;
import java.io.IOException;


public abstract class BinaryReader {
	protected long m_offset;
	protected long m_filesize;
	protected boolean m_littleEndian;
	protected long m_maxOffset;
	
	public abstract void setOffset(long offset) throws IOException;
	
	public abstract byte readByte() throws IOException;
	
	public abstract void close();
	
	public long getMaxOffset() {
		return this.m_maxOffset;
	}
	
	public void clearMaxOffset() {
		this.m_maxOffset = 0;
	}
	
	public long getOffset() {
		return this.m_offset;
	}
	
	public long getSize() {
		return this.m_filesize;
	}
	
	public void setLittleEndian() {
		this.m_littleEndian = true;
	}
	
	public void setBigEndian() {
		this.m_littleEndian = false;
	}
	
	public boolean isLittleEndian() {
		return this.m_littleEndian;
	}
	
	public boolean isBigEndian() {
		return !this.m_littleEndian;
	}
	
	public byte[] readBytes(int n) throws IOException {
		if(n < 0)
			throw new IllegalArgumentException("n should be greater than 0");
		
		byte[] buf = new byte[n];
		for(int i=0;i<n;i++) {
			buf[i] = readByte();
		}
		return buf;
	}
	
	public void skipBytes(int n) throws IOException{
		if(n < 0)
			throw new IllegalArgumentException("n should be greater than 0");
		
		for(int i=0;i<n;i++) {
			readByte();
		}
	}
	
	public int readInt() throws IOException {
		byte[] data = readBytes(4);
		int temp = 0;
		
		if(this.m_littleEndian) {
			temp = (data[0] & 0xff);
			temp |= (data[1] & 0xff) << 8;
			temp |= (data[2] & 0xff) << 16;
			temp |= (data[3] & 0xff) << 24;
		} else {
			temp = (data[0] & 0xff) << 24;
			temp |= (data[1] & 0xff) << 16;
			temp |= (data[2] & 0xff) << 8;
			temp |= (data[3] & 0xff);
		}
		return temp;
	}
	
	public long readLong() throws IOException {
		byte[] data = readBytes(8);
		long temp = 0;
		
		if(this.m_littleEndian) {
			temp = (data[0] & 0xff);
			temp |= (data[1] & 0xff) << 8;
			temp |= (data[2] & 0xff) << 16;
			temp |= (data[3] & 0xff) << 24;
			temp |= (data[4] & 0xff) << 32;
			temp |= (data[5] & 0xff) << 40;
			temp |= (data[6] & 0xff) << 48;
			temp |= (data[7] & 0xff) << 56;
		} else {
			temp = (data[0] & 0xff) << 56;
			temp |= (data[1] & 0xff) << 48;
			temp |= (data[2] & 0xff) << 40;
			temp |= (data[3] & 0xff) << 32;
			temp |= (data[4] & 0xff) << 24;
			temp |= (data[5] & 0xff) << 16;
			temp |= (data[6] & 0xff) << 8;
			temp |= (data[7] & 0xff);
		}
		return temp;
	}
	
	public short readShort() throws IOException {
		byte[] data = readBytes(2);
		short temp = 0;
		
		if(this.m_littleEndian) {
			temp = (short) (data[0] & 0xff);
			temp |= (data[1] & 0xff) << 8;
		} else {
			temp = (short) ((data[0] & 0xff) << 8);
			temp |= (data[1] & 0xff);
		}
		return temp;
	}
	
	public String readASCIIString() throws IOException {
		StringBuilder sb = new StringBuilder();
		
		for(long i=this.m_offset;i<this.m_filesize;i++) {
			char c = (char) readByte();
			if(c == '\0') {
				break;
			} else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}
	
	public String readASCIIString(int length) throws IOException {
		StringBuilder sb = new StringBuilder();
		int nCount = 0;
		
		for(long i=0;i<length;i++) {
			char c = (char) readByte();
			nCount++;
			if(c == '\0') {
				break;
			} else {
				sb.append(c);
			}
		}
		
		if(nCount < length) {
			skipBytes(length - nCount);
		}
		
		return sb.toString();
	}
}
