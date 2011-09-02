/*
 * GameBitsNetListener.java
 *
 * Created on 22. September 2006, 12:22
 *--
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net;

import java.io.*;
import javax.microedition.io.*;
// note that Vector is thread safe
import java.util.Vector;

/**
 *
 * @author Besitzer
 */
public class GameBitsNetListener extends Thread {
    
    DataInputStream dis = null;
    
    private byte[] incomingData = null;
    private Vector messageQueue = new Vector();
    private int queueSize = 0;
    
    private long lastSleep = 0;
    private long sleepRead = 10;
    
    private boolean error = false;
    private boolean shutdown = false;
    
    // helpers
    private int msgLength = 0;
    private byte firstByte = 0;
    
    
    /**
     * Connection object used for socket (stream) transfer.
     */
//#if _AB_StreamConnection
//#     private StreamConnection 	sc;
//#     private StreamConnection 	scWrite;
//#endif


//#if !_AB_StreamConnection
    private SocketConnection sc;
//#endif
    
    
    /** Creates a new instance of GameBitsNetListener */
    public GameBitsNetListener(DataInputStream dis, long sleepRead) {
        this.dis = dis;
        this.sleepRead = sleepRead;
    }
    
    public void run() {
        setPriority(Thread.NORM_PRIORITY);
        
        error = (dis==null);
        
        while(!error && !shutdown) {
            // read from network
            getFromStream();
            if (incomingData != null) {
                queueSize++;
                messageQueue.addElement(incomingData);
            }
        }
    
        //Leave enough time for other threads (e.g. reading
        // input) so no problem arises when network traffic
        //is high.
        try {
            sleep(sleepRead);
        } catch(InterruptedException e) {
            // do nothing
        }


    } // END run()
    
    
    /**
     * Receive data from the SocketConnection.
     * Does not need to be synchronized (is inherently thread-safe because 
     * vector is thread-safe.
     * @return The data that was received.
     */
    private byte[] getFromStream() {
        
        incomingData = null;
        
        try {

            // read method 1: read fully
            // default / SE MIDP 2.0 // DefaultConfiguration || SOCKET_MIDP_2_SE
//#if _AB_NetMethod_readFully1
//#             //Series60, SonyEricsson START START START
//#             //At this point the thread is blocked for as
//#             //long as there is no data to receive.
//#             //As defined by the protocol, the first byte
//#             //defines the length of a message.
//#             
//# //System.out.println("Netmanager: before read first byte");            
//#             firstByte = (byte)dis.read();
//# //System.out.println("Netmanager: after read first byte: " + firstByte);                        
//#             // START JP -- changed to allow receiving long messages
//#             int msgLength = 1;
//#             int startRead = 1;
//# 
//#             if (firstByte != 0) {
//# 
//# //System.out.println("bytelen: " + firstByte);
//#                 // usual message, length up to 255
//#                 msgLength = (int)(firstByte & 0xFF);
//# //System.out.println("length: " + msgLength);
//#                 incomingData = new byte[msgLength];
//#                 incomingData[0] = firstByte;
//#             } else {
//#                 //System.out.println("received long message");
//#                 // long message was received
//#                 byte secondByte = (byte)dis.read();
//#                 byte thirdByte = (byte)dis.read();
//#                 // get message length from 2 bytes
//#                 msgLength = ((secondByte&127) << 8) | (thirdByte&255);
//# 
//#                 //System.out.println("length: " + msgLength);
//# 
//#                 // set incomingData
//#                 incomingData = new byte[msgLength];
//#                 incomingData[0] = (byte)0;
//#                 incomingData[1] = secondByte;
//#                 incomingData[2] = thirdByte;
//#                 // first 3 bytes have already been read
//#                 startRead = 3;
//#             }
//# 
//#             //Receive the given number of bytes as defined
//#             //by the length-byte of the message.
//#             /*
//#             for(int i = startRead; i < msgLength; i++) {
//#                 incomingData[i] = (byte)dis.read();
//#             }
//#              */
//#             // END JP            
//#             dis.readFully(incomingData, startRead, msgLength-startRead);
//#             
//#             return incomingData;
//#             // Series60, SonyEricsson END END END
//#             
//#elif _AB_NetMethod_readFully2
//#             
//#             //      Series40               START START START
//#                     
//#             int max = 0;
//#             max = dis.available();
//#             if(max > 0) {
//#                 
//#                 //prepare the array for the data and read it
//#                 incomingData = new byte[max+overHead.length];
//#                 dis.readFully(incomingData, overHead.length, max);
//#                 System.arraycopy(overHead, 0, incomingData, 0, overHead.length);
//#                 overHead = null;
//#                 overHead = new byte[0];
//#                 //now cut all data to messages
//#                 int pointer = 0;
//#                 int pointerData = 0;
//#                 while(pointer<incomingData.length) {
//#                     pointerData = incomingData[pointer];
//#                     if(pointerData != 0){
//#                         //ordinary message
//#                         if((pointer+pointerData)<=incomingData.length) {
//#                             //100% available
//#                             byte[] temp = new byte[pointerData];
//#                             System.arraycopy(incomingData, pointer, temp, 0, pointerData);
//#                             transport.addData(temp);
//#                         } else {
//#                             overHead = new byte[incomingData.length-pointer];
//#                             System.arraycopy(incomingData, pointer, overHead, 0, overHead.length);
//#                         }
//#                     } else {
//#                         //long message
//#                         if(pointer+2<incomingData.length) {
//#                             pointerData = ((incomingData[pointer+1]&127) << 8) | (incomingData[pointer+2]&255);
//#                             if((pointer+pointerData)<=incomingData.length) {
//#                                 //100% available
//#                                 byte[] temp = new byte[pointerData];
//#                                 System.arraycopy(incomingData, pointer, temp, 0, pointerData);
//#                                 transport.addData(temp);
//#                             } else {
//#                                 overHead = new byte[incomingData.length-pointer];
//#                                 System.arraycopy(incomingData, pointer, overHead, 0, overHead.length);
//#                             }
//#                         } else {
//#                             pointerData = 2;
//#                             overHead = new byte[incomingData.length-pointer];
//#                             System.arraycopy(incomingData, pointer, overHead, 0, overHead.length);
//#                         }
//#                     }
//#                     pointer += pointerData;
//#                 }
//#                 return null;
//#             }
//# 
//#             try{Thread.sleep(sleepRead);}catch(InterruptedException e){}
//#             
//#             return null;
//#             //Series40           END END END
//#             //
//#endif
            
        } catch(IOException e) {
            if(!shutdown)
                error = true;
            
            //System.out.println("At GameBitsListener.getFromStream() " + e.toString());
        }
        
        return null;
    }

    public void shutdown() {
        shutdown = true;    // make sure run loop is exited
        // note: the DataInputStream (dis) is closed (after this call to shutdown()) 
        // in GameBitsNetManager to interrupt this Thread at dis.read() / dis.readFully()
    }
    

    // note: operations on messageQueue are inherently thread-safe, because vector is thread-safe
    // queueSize however is not granted to be synchronized, thus it should only be used as an 
    // internal indicator for optimization and for nothing else
    public byte[] getDataAvailable() {
        if (messageQueue.size() > 0) {    
            byte[] msg = (byte[])(messageQueue.firstElement());
            queueSize--;
            messageQueue.removeElementAt(0);
            return msg;
        }
        return null;
    }
    
    public int getNumBytesAvailable() {
        try {
        return dis.available();
        } catch(Exception e) {}
        return 0;
    }
    
    public int getMessageQueueSize() {
        return messageQueue.size();
    }
    
    /**
     * Check if the current status is ok, or if there
     * were errors.
     * @returns false if an error occured recently,
     * true otherwise.
     */
    public boolean status() {
        return !error;
    }
    
    public void setSleepRead(long sleepRead) {
        this.sleepRead = sleepRead;
    }
    
    
    
}
