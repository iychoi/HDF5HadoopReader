package edu.arizona.cs.hdf5.io;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class BinaryFileReader extends BinaryReader {

	protected File m_file;
	protected RandomAccessFile m_randomaccessfile;
	
	public BinaryFileReader(String filepath) throws IOException {
		if(filepath == null)
			throw new IllegalArgumentException("filepath must not be null");
		if(filepath.isEmpty())
			throw new IllegalArgumentException("filepath must not be empty");
		
		_BinaryFileReader(new File(filepath));
	}
	
	public BinaryFileReader(File file) throws IOException {
		_BinaryFileReader(file);
	}
	
	private void _BinaryFileReader(File file) throws IOException {
		if(file == null)
			throw new IllegalArgumentException("file must not be null");
		if(!file.isFile())
			throw new IllegalArgumentException("file must be file");
		
		this.m_file = file;
		this.m_offset = 0;
		this.m_filesize = file.length();
		this.m_littleEndian = true;
		this.m_maxOffset = 0;
		
		this.m_randomaccessfile = new RandomAccessFile(file, "r");
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
		this.m_randomaccessfile.seek(offset);
	}

	@Override
	public byte readByte() throws IOException {
		if(this.m_offset >= this.m_filesize)
			throw new IOException("file offset reached to end of file");
		byte b = this.m_randomaccessfile.readByte();
		
		this.m_offset++;
		
		if(this.m_maxOffset < this.m_offset) {
			this.m_maxOffset = this.m_offset;
		}
		
		return b;
	}

	@Override
	public void close() {
		try {
			this.m_randomaccessfile.close();
		} catch (IOException e) {
		}
		this.m_offset = 0;
	}
}
