/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import edu.arizona.cs.hdf5.io.BinaryReader;


public class DataObjectFacade {
	
	private static Hashtable<Long, DataObject> ObjectAddressMap = new Hashtable<Long, DataObject>();
	
	private long m_address;
	private DataObject m_dataObject;
	
	private String m_symbolName;
	private String m_linkName;
	
	private Layout m_layout;
	
	public DataObjectFacade(BinaryReader in, Superblock sb, String symbolName, long address) throws IOException {
		this.m_address = address;
		
		DataObject dobj = readDataObject(in, sb, address);
		this.m_dataObject = dobj;
		
		this.m_symbolName = symbolName;
		this.m_linkName = null;
		
		this.m_layout = null;
	}
	
	public DataObjectFacade(BinaryReader in, Superblock sb, String symbolName, String linkName) throws IOException {
		this.m_symbolName = symbolName;
		this.m_linkName = linkName;
		
		this.m_layout = null;
	}
	
	public long getAddress() {
		return this.m_address;
	}

	private DataObject readDataObject(BinaryReader in, Superblock sb, long address) throws IOException {
		DataObject dobj = ObjectAddressMap.get(address);
		if(dobj == null) {
			dobj = new DataObject(in, sb, address);
			ObjectAddressMap.put(address, dobj);
		}
		return dobj;
	}
	
	public DataObject getDataObject() throws IOException {
		return this.m_dataObject;
	}
	
	/*
	public Hashtable<String, String> getAttributes(BinaryReader in, Superblock sb) throws IOException {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		
		if(this.m_dataObject != null) {
			Vector<ObjectHeaderMessage> msgs = this.m_dataObject.getMessages();
			if(msgs != null) {
				for(ObjectHeaderMessage msg : msgs) {
					if(msg.getHeaderMessageType() == ObjectHeaderMessageType.Attribute) {
						AttributeMessage am = msg.getAttributeMessage();
						String key = am.getName();
						
						long doffset = am.getDataPos();
						in.setOffset(doffset);
						
						String value = in.readASCIIString();
						
						ht.put(key, value);
					}
				}
			}
		}
		
		return ht;
	}
	*/
	
	public Layout getLayout() {
		if(this.m_layout == null) {
			Layout layout = new Layout();
			if(this.m_dataObject != null) {
				Vector<ObjectHeaderMessage> msgs = this.m_dataObject.getMessages();
				if(msgs != null) {
					for(ObjectHeaderMessage msg : msgs) {
						if(msg.getHeaderMessageType() == ObjectHeaderMessageType.Layout) {
							LayoutMessage lm = msg.getLayoutMessage();
							
							int numberOfDimensions = lm.getNumberOfDimensions();
							int[] chunkSize = lm.getChunkSize();
							long dataAddress = lm.getDataAddress();
							
							layout.setNumberOfDimensions(numberOfDimensions);
							layout.setChunkSize(chunkSize);
							layout.setDataAddress(dataAddress);
						} else if(msg.getHeaderMessageType() == ObjectHeaderMessageType.Datatype) {
							DataTypeMessage dm = msg.getDataTypeMessage();
							
							if(dm.getType() == DataTypeMessage.DATATYPE_COMPOUND) {
								Vector<StructureMember> sms = dm.getStructureMembers();
								if(sms != null) {
									for(StructureMember sm : sms) {
										String name = sm.getName();
										int offset = sm.getOffset();
										int dims = sm.getDims();
										
										int dataType = -1;
										int byteLength = -1;
										DataTypeMessage dtm = sm.getMessage();
										if(dtm != null) {
											dataType = dtm.getType();
											byteLength = dtm.getByteSize();
										}
										
										layout.addField(name, offset, dims, dataType, byteLength);
									}
								}
							}
						} else if(msg.getHeaderMessageType() == ObjectHeaderMessageType.SimpleDataspace) {
							DataspaceMessage dm = msg.getDataspaceMessage();
							
							if(dm != null) {
								int[] dimensionLength = dm.getDimensionLength();
								int[] maxDimensionLength = dm.getMaxDimensionLength();
								
								layout.setDimensionLength(dimensionLength);
								layout.setMaxDimensionLength(maxDimensionLength);
								
								//int ndims = Math.min(dimensionLength.length, maxDimensionLength.length);
								//layout.setNumberOfDimensions(ndims);
							}
						}
						/*
						else if(msg.getHeaderMessageType() == ObjectHeaderMessageType.Attribute) {
							AttributeMessage am = msg.getAttributeMessage();
							
							DataTypeMessage dm = am.getDataType();
							if(dm != null) {
								dm.getType()
							}
						}
						*/
					}
				}
			}
			this.m_layout = layout;
			return layout;
		}
		
		return this.m_layout;
	}
	
	public String getSymbolName() {
		return this.m_symbolName;
	}
	
	public String getLinkName() {
		return this.m_linkName;
	}
	
	public void printValues() {
		System.out.println("DataObjectFacade >>>");
		
		System.out.println("address : " + this.m_address);
		
		if(this.m_dataObject != null) {
			this.m_dataObject.printValues();
		}
		
		if(this.m_symbolName != null) {
			System.out.println("symbol name : " + this.m_symbolName);
		}
		
		if(this.m_linkName != null) {
			System.out.println("link name : " + this.m_linkName);
		}
		
		System.out.println("DataObjectFacade <<<");
	}
}
