/*
 * GameBitsNetManager.java
 *
 * Created on 22. September 2006, 12:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net;

import java.io.*;
import javax.microedition.io.*;


/**
 *
 * @author Besitzer
 */
public class GameBitsNetManager{
    
    /**
     * Connection object(s) used for socket (stream).
     */
//#if _AB_StreamConnection
//#     private StreamConnection 	sc;
//#     private StreamConnection 	scWrite;   // needed since we use half duplex only
//#endif


//#if !_AB_StreamConnection
    private SocketConnection sc;    // full duplex read / write with one socket
//#endif
    
    private GameBitsNetListener listener = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    private long lastReceive = 0;
    private long lastSend = 0;
    private long lastTraffic = 0;
    private long bytesSent;
    private long messagesSent = 0;
    
    private boolean allowSend = false;
    
    private Thread startNetThread = null;
    private boolean networkInited = false;
    
    private String host = "";
    private int socketDuplexPort = 0;
    private int socketReadPort = 0;
    private int socketWritePort = 0;
    
    private long sleepRead = 10;


    /**
     * Creates a new instance of GameBitsNetManager
     */
    public GameBitsNetManager() {
    }

    // ----
    private class NetStarter extends Thread {
    
        public NetStarter() {};
        
        public void run() {
            networkInited = false;
            setPriority(Thread.MAX_PRIORITY);
            networkInited = connectAll();
            // we don't need a reference to the thread anymore
            // NOTE: this is also an important indication that the NetStarter thread is done
            startNetThread = null;
        }
        
    }
    // ----
    
    
    
    public synchronized void startNetworking(String host, int socketDuplexPort, int socketReadPort, int socketWritePort) {
        this.host = host;
        this.socketDuplexPort = socketDuplexPort;
        this.socketReadPort = socketReadPort;
        this.socketWritePort = socketWritePort;
        
        lastReceive = System.currentTimeMillis();
        lastSend = lastReceive;
        lastTraffic = lastReceive;

        if(networkInited)
            stopNetworking();
        
        messagesSent = 0;
        // actual network init is encapsulated into a thread
        startNetThread = new NetStarter();
        startNetThread.start();
        
        try{
            //enable to pop up the "allow net usage"- dialog
            Thread.sleep(600);
        } catch(Exception e) {}
    }

    
    public synchronized void stopNetworking() {
        networkInited = false;
        
        // stop sender (socket closed further down
        allowSend = false;

        // stop listener
        if (listener!=null) {
            listener.shutdown();
        }
        
        
        // close DataInputStream, this will interrupt the listener 
        // (unblock the read() / readFully call)
        if (dis!=null) {
            try {
                dis.close();
            } catch (IOException ioe1) {
            } finally {
                dis = null;
            }
        }

        // wait for listener thread to exit
        if (listener != null) {
            try {
                listener.join();
            } catch (InterruptedException ire) {}
        }
        listener = null;
        
        
        
        // clean up sockets
        if(sc!= null) {
            try {
                sc.close();  //socket connection used
            } catch (IOException ioe2) {
            } finally {
                sc = null;
            }
        }
        
//#if _AB_StreamConnection            
//#         if(scWrite!= null) {
//#             try {
//#                 scWrite.close();  //socket connection used
//#             } catch (IOException ioe3) {
//#             } finally {
//#                 scWrite = null;
//#             }
//#         }
//#endif
    
        System.gc();
        
    }
    
    
    
    private boolean connectAll() {
        try {
            // open socket
//#if _AB_StreamConnection
//#         sc = (StreamConnection)Connector.open("socket://" + host + ":" + socketWritePort, Connector.READ);                
//#         scWrite = (StreamConnection)Connector.open("socket://" + host + ":" + socketReadPort, Connector.WRITE);
//#         // sender = new GameBitsNetSender(scWrite, startNetHost, SOCKETPORT);
//#else
            sc = (SocketConnection)Connector.open("socket://" + host + ":" + socketDuplexPort);                
            // sender = new GameBitsNetSender(sc, startNetHost, SOCKETPORT);
//#endif
        
            // open data input stream
            dis = sc.openDataInputStream();
//#if _AB_StreamConnection            
//#             dos = scWrite.openDataOutputStream();
//#else
            dos = sc.openDataOutputStream();
//#endif
            if (dis!=null && dos!=null) {
                // start listener
//#if _AB_StreamConnection
//# /*
//#             address = new byte[6];
//#             for(int i = 0; i < 6; i++)
//#                 address[i] = (byte)dis.read();
//#  */
//#endif
                listener = new GameBitsNetListener(dis, sleepRead);
                listener.start();
                // unlock sending
                allowSend = true;
                return true;
            }
        } catch(Exception e) {
            dis = null;
            sc = null;
            listener = null;
//#if _AB_StreamConnection            
//#             scWrite = null;
//#endif            
        }        

        return false;
    
    }
    
    

    public byte[] getDataAvailable() {
        if (listener != null) {
            lastTraffic = System.currentTimeMillis();
            lastReceive = lastTraffic;
            return listener.getDataAvailable();
        }
        return null;
    }
    

    public boolean sendMessage(byte[] message) {
        if (!allowSend) return false;

        //System.out.println("sending message");

        try {
            dos.write(message, 0, message[0]);
            dos.flush();    //force sending
            messagesSent++;
            bytesSent += message[0];
            lastTraffic = System.currentTimeMillis();
            lastSend = lastTraffic;
        } catch (Exception e) {
            return false;
        }
        return true;
    }    
    
    public void setSleepRead(long sleepRead) {
        this.sleepRead = sleepRead;
        if (listener != null) {
            listener.setSleepRead(sleepRead);
        }
    }

    public long getSleepRead() {
        return sleepRead;
    }


    public long getBytesSent() {
        return bytesSent;
    }
    
    public long getLastReceive() {
        return lastReceive;
    }
    
    public long getLastTraffic() {
        return lastTraffic;
    }
    
    public long getLastSend() {
        return lastSend;
    }
    
    public int getNumBytesAvailable() {
        if (listener!=null) {
            return listener.getNumBytesAvailable();
        }
        return 0;
    }
    
    public int getMessageQueueSize() {
        if (listener!=null) {
            return listener.getMessageQueueSize();
        }
        return 0;
    }
    
    
    public synchronized int connectFinished() {
        if(startNetThread == null && networkInited) {
            return 1;
        } else if(startNetThread != null) {
            return 0;
        } else {
            return -1;
        }
    }
    
    public boolean isNetworkInited() {
        return networkInited;
    }

    public synchronized boolean status() {
        if(dis==null || dos == null || listener==null) {
            return false;
        }
        return listener.status();
    }




    
}
