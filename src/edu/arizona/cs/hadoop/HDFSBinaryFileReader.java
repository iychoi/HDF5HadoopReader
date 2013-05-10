/*
 * Written by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hadoop;

import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import edu.arizona.cs.hdf5.io.BinaryReader;

public class HDFSBinaryFileReader extends BinaryReader {

	private FSDataInputStream m_inputStream;
	
	
	public HDFSBinaryFileReader(FSDataInputStream inputStream) {
		if(inputStream == null)
			throw new IllegalArgumentException("inputStream must not be null");
		
		this.m_inputStream = inputStream;
		this.m_offset = 0;
		
		this.m_filesize = Long.MAX_VALUE;
		
		this.m_littleEndian = true;
		this.m_maxOffset = 0;
	}
	
	@Override
	public void close() {
		try {
			this.m_inputStream.close();
		} catch (IOException e) {
		}
		this.m_offset = 0;
	}

	@Override
	public byte readByte() throws IOException {
		if(this.m_offset >= this.m_filesize)
			throw new IOException("file offset reached to end of file");
		byte b = this.m_inputStream.readByte();
		
		this.m_offset++;
		
		if(this.m_maxOffset < this.m_offset) {
			this.m_maxOffset = this.m_offset;
		}
		
		return b;
	}
	
	public byte[] readBytes(int n) throws IOException {
		if(n < 0)
			throw new IllegalArgumentException("n should be greater than 0");

		byte[] buf = new byte[n];

		int len = 0;
		int count = this.m_inputStream.read(buf, 0, n);
		if(count < n) {
			len = count;
			while(len < n) {
				count = this.m_inputStream.read(buf, len, n - len);
				len += count;
			}
		}
		
		this.m_offset += n;
		
		if(this.m_maxOffset < this.m_offset) {
			this.m_maxOffset = this.m_offset;
		}
		
/*
		for(int i=0;i<n;i++) {
			buf[i] = readByte();
		}
*/
		return buf;
	}
	
	@Override
	public void skipBytes(int n) throws IOException{
		this.m_inputStream.skipBytes(n);
		
		this.m_offset += n;
		
		if(this.m_maxOffset < this.m_offset) {
			this.m_maxOffset = this.m_offset;
		}
	}

	@Override
	public void setOffset(long offset) throws IOException {
		if(offset < 0)
			throw new IllegalArgumentException("offset must be positive and bigger than 0");
		if(offset > this.m_filesize)
			throw new IllegalArgumentException("offset must be positive and smaller than filesize");
		
		if(this.m_offset == offset)
			return;
		
		this.m_offset = offset;
		if(this.m_maxOffset < offset) {
			this.m_maxOffset = offset;
		}
		
		// change underlying file offset
		this.m_inputStream.seek(offset);
	}

}
