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

public class DataNode {
	
	public static final byte[] SIGNATURE = {'T', 'R', 'E', 'E'};
	
	private long m_address;
	private Layout m_layout;
	
    private int m_level;
    private int m_numberOfEntries;
    private DataNode m_currentNode;

    // level 0 only
    private Vector<DataChunk> m_entries;
    // level > 0 only
    private int[][] m_offsets; // int[nentries][ndim]; // other levels

    // "For raw data chunk nodes, the child pointer is the address of a single raw data chunk"
    private long[] m_childPointer; // long[nentries];

    private int m_currentEntry; // track iteration; LOOK this seems fishy - why not an iterator ??

    public DataNode(BinaryReader in, Superblock sb, Layout layout, long address) throws IOException {

    	in.setOffset(address);
    	
    	this.m_address = address;
    	this.m_layout = layout;
    	byte[] signature = in.readBytes(4);
    	
    	for(int i=0;i<4;i++) {
			if(signature[i] != SIGNATURE[i]) {
				throw new IOException("signature is not valid");
			}
		}
    	
    	int type = in.readByte();
    	this.m_level = in.readByte();
    	this.m_numberOfEntries = in.readShort();
    	
    	long size = 8 + 2 * sb.getSizeOfOffsets() + this.m_numberOfEntries * (8 + sb.getSizeOfOffsets() + 8 + layout.getNumberOfDimensions());

    	long leftAddress = ReadHelper.readO(in, sb);
    	long rightAddress = ReadHelper.readO(in, sb);
    	
    	if(this.m_level == 0) {
	        // read all entries as a DataChunk
	        this.m_entries = new Vector<DataChunk>();
	        
	        for(int i=0;i<=this.m_numberOfEntries;i++) {
	        	DataChunk dc = new DataChunk(in, sb, in.getOffset(), layout.getNumberOfDimensions(), (i == this.m_numberOfEntries));
	        	this.m_entries.add(dc);
	        }
    	} else {
    		// just track the offsets and node addresses
    		this.m_offsets = new int[this.m_numberOfEntries + 1][layout.getNumberOfDimensions()];
    		this.m_childPointer = new long[this.m_numberOfEntries + 1];
        
    		for(int i=0;i<=this.m_numberOfEntries;i++) {
    			in.skipBytes(8); // skip size, filterMask
    			for (int j=0;j<layout.getNumberOfDimensions();j++) {
    				this.m_offsets[i][j] = (int)in.readLong();
    			}
          
    			this.m_childPointer[i] = (i == this.m_numberOfEntries) ? -1 : ReadHelper.readO(in, sb);
    		}
    	}
    }

    // this finds the first entry we dont want to skip.
    // entry i goes from [offset(i),offset(i+1))
    // we want to skip any entries we dont need, namely those where want >= offset(i+1)
    // so keep skipping until want < offset(i+1)
    public void first(BinaryReader in, Superblock sb) throws IOException {
    	if(this.m_level == 0) {
    		this.m_currentEntry = 0;
    		
			/* note nentries-1 - assume dont skip the last one
			for (currentEntry = 0; currentEntry < nentries-1; currentEntry++) {
				DataChunk entry = myEntries.get(currentEntry + 1);
				if ((wantOrigin == null) || tiling.compare(wantOrigin, entry.offset) < 0) 
					break;   // LOOK ??
			} 
			*/
    	} else {
    		this.m_currentNode = null;
    		for(this.m_currentEntry = 0; this.m_currentEntry < this.m_numberOfEntries; this.m_currentEntry++) {
				this.m_currentNode = new DataNode(in, sb, this.m_layout, this.m_childPointer[this.m_currentEntry]);
				this.m_currentNode.first(in, sb);
				break;
    		}

			// heres the case where its the last entry we want; the tiling.compare() above may fail
			if (this.m_currentNode == null) {
				this.m_currentEntry = this.m_numberOfEntries - 1;
				this.m_currentNode = new DataNode(in, sb, this.m_layout, this.m_childPointer[this.m_currentEntry]);
				this.m_currentNode.first(in, sb);
			}
      	}
    }

    // LOOK - wouldnt be a bad idea to terminate if possible instead of running through all subsequent entries
    public boolean hasNext(BinaryReader in, Superblock sb) {
		if(this.m_level == 0) {
			return (this.m_currentEntry < this.m_numberOfEntries);
		} else {
			if(this.m_currentNode.hasNext(in, sb)) { 
				return true;
			}
			
			return (this.m_currentEntry < this.m_numberOfEntries - 1);
		}
	}

    public DataChunk next(BinaryReader in, Superblock sb) throws IOException {
    	if(this.m_level == 0) {
    		return this.m_entries.get(this.m_currentEntry++);
    	} else {
    		if(this.m_currentNode.hasNext(in, sb)) {
    			return this.m_currentNode.next(in, sb);
    		}

    		this.m_currentEntry++;
    		this.m_currentNode = new DataNode(in, sb, this.m_layout, this.m_childPointer[this.m_currentEntry]);
    		this.m_currentNode.first(in, sb);
    		
    		return this.m_currentNode.next(in, sb);
    	}
    }
}
