/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

public class LayoutField {

	private String m_name;
	private int m_offset;
	private int m_ndims;
	private int m_dataType;
	private int m_byteLength;
	
	public LayoutField(String name, int offset, int ndims, int dataType, int byteLength) {
		this.m_name = name;
		this.m_offset = offset;
		this.m_ndims = ndims;
		this.m_dataType = dataType;
		this.m_byteLength = byteLength;
	}
	
	public String getName() {
		return this.m_name;
	}
	
	public int getOffset() {
		return this.m_offset;
	}
	
	public int getNDims() {
		return this.m_ndims;
	}
	
	public int getDataType() {
		return this.m_dataType;
	}
	
	public int getByteLength() {
		return this.m_byteLength;
	}
}
