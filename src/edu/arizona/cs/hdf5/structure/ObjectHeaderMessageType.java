/*
 * Mostly copied from NETCDF4 source code.
 * refer : http://www.unidata.ucar.edu
 * 
 * Modified by iychoi@email.arizona.edu
 */

package edu.arizona.cs.hdf5.structure;

public class ObjectHeaderMessageType {
    private static int MAX_MESSAGE = 23;
    private static java.util.Map<String, ObjectHeaderMessageType> hash = new java.util.HashMap<String, ObjectHeaderMessageType>(10);
    private static ObjectHeaderMessageType[] mess = new ObjectHeaderMessageType[MAX_MESSAGE];

    public final static ObjectHeaderMessageType NIL = new ObjectHeaderMessageType("NIL", 0);
    public final static ObjectHeaderMessageType SimpleDataspace = new ObjectHeaderMessageType("SimpleDataspace", 1);
    public final static ObjectHeaderMessageType GroupNew = new ObjectHeaderMessageType("GroupNew", 2);
    public final static ObjectHeaderMessageType Datatype = new ObjectHeaderMessageType("Datatype", 3);
    public final static ObjectHeaderMessageType FillValueOld = new ObjectHeaderMessageType("FillValueOld", 4);
    public final static ObjectHeaderMessageType FillValue = new ObjectHeaderMessageType("FillValue", 5);
    public final static ObjectHeaderMessageType Link = new ObjectHeaderMessageType("Link", 6);
    public final static ObjectHeaderMessageType ExternalDataFiles = new ObjectHeaderMessageType("ExternalDataFiles", 7);
    public final static ObjectHeaderMessageType Layout = new ObjectHeaderMessageType("Layout", 8);
    public final static ObjectHeaderMessageType GroupInfo = new ObjectHeaderMessageType("GroupInfo", 10);
    public final static ObjectHeaderMessageType FilterPipeline = new ObjectHeaderMessageType("FilterPipeline", 11);
    public final static ObjectHeaderMessageType Attribute = new ObjectHeaderMessageType("Attribute", 12);
    public final static ObjectHeaderMessageType Comment = new ObjectHeaderMessageType("Comment", 13);
    public final static ObjectHeaderMessageType LastModifiedOld = new ObjectHeaderMessageType("LastModifiedOld", 14);
    public final static ObjectHeaderMessageType SharedObject = new ObjectHeaderMessageType("SharedObject", 15);
    public final static ObjectHeaderMessageType ObjectHeaderContinuation = new ObjectHeaderMessageType("ObjectHeaderContinuation", 16);
    public final static ObjectHeaderMessageType Group = new ObjectHeaderMessageType("Group", 17);
    public final static ObjectHeaderMessageType LastModified = new ObjectHeaderMessageType("LastModified", 18);
    public final static ObjectHeaderMessageType AttributeInfo = new ObjectHeaderMessageType("AttributeInfo", 21);
    public final static ObjectHeaderMessageType ObjectReferenceCount = new ObjectHeaderMessageType("ObjectReferenceCount", 22);

    private String name;
    private int num;

    private ObjectHeaderMessageType(String name, int num) {
      this.name = name;
      this.num = num;
      hash.put(name, this);
      mess[num] = this;
    }

    /**
     * Find the MessageType that matches this name.
     *
     * @param name find DataTYpe with this name.
     * @return DataType or null if no match.
     */
    public static ObjectHeaderMessageType getType(String name) {
      if (name == null) return null;
      return hash.get(name);
    }

    /**
     * Get the MessageType by number.
     *
     * @param num message number.
     * @return the MessageType
     */
    public static ObjectHeaderMessageType getType(int num) {
      if ((num < 0) || (num >= MAX_MESSAGE)) return null;
      return mess[num];
    }

    /**
     * Message name.
     */
    public String toString() {
      return name + "(" + num + ")";
    }

    /**
     * @return Message number.
     */
    public int getNum() {
      return num;
    }
  }