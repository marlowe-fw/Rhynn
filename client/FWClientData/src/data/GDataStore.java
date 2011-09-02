/*
 * GDataStore.java
 *
 * Created on 29. Februar 2004, 13:18
 */

package data;

import javax.microedition.rms.*;

/**
 * Object for accessing and storing persistent data
 *
 * @author  nopper
 */
public class GDataStore {

    private static String separator = "|";
    
    /**
     * The name of the database
     */
    private String      filename;

    /**
     * The database we are connecting to
     */
    private RecordStore database;
    
    private RecordEnumeration rE;
    
    /**
     * Indicates if recordEnumeration has to be rebuild before next use
     */
    private boolean needsRebuild = false;
    
    /**
     * Creates the data storage without connecting to it
     */
    public GDataStore(String filename) {
        this.filename = filename;
    }
    
    /**
     * Connects to the given data storage
     */
    public boolean connect() {
        if(filename == null)
            return false;

        try {
            if(database != null)
                disconnect();

            database = RecordStore.openRecordStore(filename, true);
            rE = database.enumerateRecords(null, null, false);

        } catch(Exception e) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Disconnects from the database
     */
    public void disconnect() {
        try {
            if(database != null)
                database.closeRecordStore();

            database = null;
            rE.destroy();
            rE = null;
            
        } catch(Exception e) {}
    }
    
    /**
     * Returns the value of the given key
     */
    public String getValue(String key) {
        String value = null;
        
        try {
            int index = getIndex(key);

            if(index!=-1) {
                value = new String(database.getRecord(index));
                value = value.substring(value.indexOf(separator)+1);
            }
        } catch(Exception e) {
            value = null;
        }
        
        return value;
    }
    
    /**
     * Returns the value of the given key
     */
    public byte[] getBytes(String key) {
        byte[] value = null;
        
        try {
            int index = getIndex(key);
            
            if(index!=-1) {
                byte[] record = database.getRecord(index);

                value = new byte[record.length-key.length()-separator.length()];
                System.arraycopy(record, key.length()+separator.length(), value, 0, value.length);
                record = null;
            }
        } catch(Exception e) {
            value = null;
        }
        
        return value;
    }
    
    /**
     * Sets the key value pair
     */
    public void setValue(String key, String value) {
        try {
            if(database.getSizeAvailable() > key.length()+separator.length()+value.length()) {
                removeValue(key);
                needsRebuild = true;
            
                String toStore = key + separator + value;
                // create new record
                database.addRecord(toStore.getBytes(), 0, toStore.length());
            }
        } catch(Exception e) {}
    }
    
    /**
     * Sets the key value pair
     */
    public void setBytes(String key, byte[] value) {
        try {
            if(database.getSizeAvailable() > key.length()+separator.length()+value.length) {
                removeValue(key);
                needsRebuild = true;

                byte[] toStore = new byte[key.length()+separator.length()+value.length];
                System.arraycopy(key.getBytes(), 0, toStore, 0, key.length());
                System.arraycopy(separator.getBytes(), 0, toStore, key.length(), separator.length());
                System.arraycopy(value, 0, toStore, key.length()+separator.length(), value.length);
                // create new record
                database.addRecord(toStore, 0, toStore.length);
            }
        } catch(Exception e) {
            System.out.println("storing to rs, exception: " + e.toString());
        }
    }
    
    /**
     * Removes the value by the given key
     */
    public void removeValue(String key) {
        try {
            int index = getIndex(key);
            
            if(index != -1) {
                database.deleteRecord(index);
            }
        } catch(Exception e) {}
    }
    
    /**
     * Returns the slot of the given key 
     */
    private int getIndex(String key) {
        try {
            if(needsRebuild) {
                needsRebuild = false;
                rE.rebuild();
            } else {
                rE.reset();
            }
            
            while(rE.hasNextElement()) {
                try {
                    //String x = new String(rE.nextRecord(), 0, key.length() + separator.length());
                    //System.out.println("XX: " + x);
                    if((new String(rE.nextRecord(), 0, key.length() + separator.length())).startsWith(key + separator)) {
                        if(rE.hasNextElement()) {
                            rE.nextRecordId();
                            return rE.previousRecordId();
                        } else if(rE.hasPreviousElement()) {
                            rE.previousRecordId();
                        } else {
                            rE.reset();
                        }
                        return rE.nextRecordId();
                    }
                } catch (Exception e) {/* might be caused by new String(byte[], off, len)*/}
            }
        } catch(Exception e) {e.printStackTrace();}
        
        return -1;
    }
    
    /**
     * Returns total size available for recordStore.
     */
    public int getSizeTotal() {
        try {
            return database.getSize()+database.getSizeAvailable();
        } catch(Exception e) {
            return 0;
        }
    }
    
    /**
     * Returns free space available for recordStore.
     */
    public int getSizeAvailable() {
        try {
            return database.getSizeAvailable();
        } catch(Exception e) {
            return 0;
        }
    }
}
