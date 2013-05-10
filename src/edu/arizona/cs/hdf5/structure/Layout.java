/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.util.Vector;

public class Layout {

	private long m_dataAddress;
	private int m_numberOfDimensions;
	private int[] m_dimensionLength;
	private int[] m_maxDimensionLength;
	private int[] m_chunkSize;
	private Vector<LayoutField> m_fields;
	
	public Layout() {
		this.m_dataAddress = 0;
		this.m_numberOfDimensions = 0;
		this.m_dimensionLength = null;
		this.m_maxDimensionLength = null;
		this.m_chunkSize = null;
		this.m_fields = new Vector<LayoutField>();
	}
	
	public void setDataAddress(long dataAddress) {
		this.m_dataAddress = dataAddress;
	}

	public long getDataAddress() {
		return this.m_dataAddress;
	}
	
	public void setChunkSize(int[] chunkSize) {
		this.m_chunkSize = chunkSize;
	}
	
	public int[] getChunkSize() {
		return this.m_chunkSize;
	}

	public void setNumberOfDimensions(int numberOfDimensions) {
		this.m_numberOfDimensions = numberOfDimensions;
	}
	
	public int getNumberOfDimensions() {
		return this.m_numberOfDimensions;
	}
	
	public void setDimensionLength(int[] dimensionLength) {
		this.m_dimensionLength = dimensionLength;
	}
	
	public int[] getDimensionLength() {
		return this.m_dimensionLength;
	}
	
	public void setMaxDimensionLength(int[] maxDimensionLength) {
		this.m_maxDimensionLength = maxDimensionLength;
	}
	
	public int[] getMaxDimensionLength() {
		return this.m_maxDimensionLength;
	}
	
	public void addField(String name, int offset, int ndims, int dataType, int byteLength) {
		LayoutField lf = new LayoutField(name, offset, ndims, dataType, byteLength);
		this.m_fields.add(lf);
	}
	
	public Vector<LayoutField> getFields() {
		return this.m_fields;
	}
}
