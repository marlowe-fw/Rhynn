package rhynn;




import data.GDataStore;
import graphics.GImageClip;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import net.GameBitsNetManager;
import net.NetTools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */

public class ImageManager {
    private static final int MAX_MESSAGES_PER_ITERATION = 40;

    private Hashtable permanentImages = new Hashtable();
    private Hashtable temporaryImages = new Hashtable();
    private ImageManagerObserver observer = null;

    private GameBitsNetManager gbManager = null;

    private static String lastLoadError = "";
    private Vector loadingQueue = new Vector();

    private NetImageLoadRecord curNetImageLoadRecord = null;
    private boolean netError = false;


    /*
    private boolean waitingForNetImageSizeInfo = false;
    private int curNetImageSize = 0;
    private byte[] curNetImageLoadBuffer = null;
    private int curNetImageWriteCursor = 0;
    private boolean curNetImageToPermanentCache = false;
    */
    public ImageManager(GameBitsNetManager gbManager, ImageManagerObserver observer) {
        this.gbManager = gbManager;
        this.observer = observer;
    }



    public int loadingCount() {
        return loadingQueue.size();
    }



    /** Load the given file from the record store. */
    public static Image loadFromRecordStore(String filename) {
        Image img = null;
        lastLoadError = "";

        GDataStore gd = new GDataStore(filename);
        if (gd.connect()) {
            byte[] temp=null;
            if((temp = gd.getBytes(filename))!=null){
                //System.out.println("ImageManager: load from RS");
                img = Image.createImage(temp, 0, temp.length);
                temp = null;
            } else {
                lastLoadError = "Image not found in record store: " + filename;
            }
            gd.disconnect();
        } else {
            lastLoadError = "record store error: connect";
        }
        return img;
    }

    public static void insertImageToRecordStore(String filename, byte[] data) {
        GDataStore gd = new GDataStore(filename);
        if (gd.connect()) {
            gd.setBytes(filename, data);
            gd.disconnect();
        }
    }

    public static String filenameFromGraphicsId(int graphicsId) {
        return graphicsId + ".png";
    }


    public static Image loadFromJar(String filename) {
        lastLoadError = "";
        Image img = null;
        try {
            img = Image.createImage("/" + filename);
        } catch (IOException ioe) {
            lastLoadError = "error loading image from jar: loadFromJar, " + ioe.toString();
        }
        return img;
    }


    public Image getImageFromCache(int graphicsId) {
        ImageRecord imgRecord = getImageRecordFromCache(graphicsId);
        if (imgRecord != null) {
            return imgRecord.getImage();
        }
        return null;
    }

    public GImageClip getImageClipFormCache(int graphicsId, int clipX, int clipY, int width, int height) {
        ImageRecord imgRecord = getImageRecordFromCache(graphicsId);
        if (imgRecord != null) {
            return new GImageClip(imgRecord.image, clipX, clipY, width, height);
        }
        return null;
    }

    public void putImageToCache(int graphicsId, Image img, boolean toPermanentCache) {
        ImageRecord imgRecord = new ImageRecord(graphicsId, img);
        if (toPermanentCache) {
            permanentImages.put("" +  graphicsId, imgRecord);
        } else {
            temporaryImages.put("" +  graphicsId, imgRecord);
        }
    }

    /**
     * Use this method if you know the given image should be present in the jar. You will still need to know the associated graphics id
     * to allow lookup of this image.
     * @param graphicsId The graphics id this image image is associated with
     * @param filename The name of the image file inside the jar
     * @return true on success, false otherwise
     */
    public boolean loadImageFromJarToCache(int graphicsId, String filename, boolean toPermanentCache) {
        Image img = loadFromJar(filename);
        if (img!=null) {
            // since we have successfully loaded the image
            //  create and insert new record into the apporpriate cache
            putImageToCache(graphicsId, img, toPermanentCache);
            return true;
        }
        return false;
    }

    /**
     * Tries to load the image from jar, record store (if flag is not set to false), or net - in this order until found.
     * The image will be stored in the cache. nothing is done if the image is already found in the cache.
     * If the image could not be loaded from the jar or from the record store, then the manager tries
     * to load the image associated with the graphicsId from the server over the net (provided the useNet flag is set). In such a case
     * the observer should react on the appropriate callbacks (onImageNetLoadStarted, onImageChunkLoad, onImageLoaded).
     * In this case the return type will also be false to indicate that the image was not loaded immediately and also isLoadingInProgress
     * will return true until loading is finished.
     * After the image has been loaded into the cache, a call to getImage(graphicsId) will return the image object from the cache
     * @return true if the image object was fully loaded, false otheriwse (which would indicate loading from net - when the useNet flag was set -
     * or a possible error).
     */
    public boolean loadImageToCache(int graphicsId, boolean toPermanentCache, boolean useJar, boolean useRS, boolean useNet) {
        Image img = null;

        if (getImageFromCache(graphicsId) != null) {
            // already in cache
            return true;
        }

        // this is the simple file name convention, just the graphics id plus the extension
        String filename = filenameFromGraphicsId(graphicsId);
        if (useJar) {
            // try to load from jar
            img = loadFromJar(filename);
        }

        if (img == null && useRS) {
            // try to load from the record store
            img = loadFromRecordStore(filename);
        }

        if (img == null && useNet) {
            // try to load from the net
            addToLoadingQueue(graphicsId, toPermanentCache, useRS);
        }
        if (!lastLoadError.equals("")) {
            //System.out.println("Image load error: " + lastLoadError);
            //observer.onImageLoadError(graphicsId, lastLoadError);
        }

        if (img!=null) {
            // since we have successfully loaded the image
            //  create and insert new record into the apporpriate cache
            putImageToCache(graphicsId, img, toPermanentCache);
            return true;
        }
        return false;
    }

    public boolean removeImageFromCache(int graphicsId, boolean removeFromPermanentCacheAlso) {
        if ((ImageRecord)temporaryImages.remove("" + graphicsId) == null && removeFromPermanentCacheAlso) {
            permanentImages.remove("" + graphicsId);
        }
        return true;
    }

    /**
     * Load the graphics associated with the ids passed to this method.
     * This calls loadImageToCache for each graphicsId in the graphicsIds vector, nothing is done if the individual image is already
     * found in the cache.
     */
    public void loadImageQueueToCache(Vector graphicsIds, boolean toPermanentCache, boolean useJar, boolean useRS, boolean useNet) {
        int size = graphicsIds.size();
        for (int i=0; i<size; i++) {
            Integer gId = (Integer)(graphicsIds.elementAt(i));
            int graphicsId = gId.intValue();
            loadImageToCache(graphicsId, toPermanentCache, useJar, useRS, useNet);
        }
    }

    public void flagAllImagesInTempCacheForRemoval() {
        Enumeration e = temporaryImages.elements();
        while(e.hasMoreElements()) {
            ImageRecord ir = (ImageRecord)(e.nextElement());
            ir.flaggedForRemoval = true;
        }
    }

    public boolean excludeImageInTempCacheFromRemoval(int graphicsId) {
        ImageRecord ir = (ImageRecord)(temporaryImages.get("" + graphicsId));
        if (ir != null) {
            ir.flaggedForRemoval = false;
            return true;
        }
        return false;
    }

    public void excludeImagesInTempCacheFromRemoval(Vector graphicsIds) {
        int size = graphicsIds.size();
        for (int i=0; i<size; i++) {
            Integer gId = (Integer)(graphicsIds.elementAt(i));
            int graphicsId = gId.intValue();
            excludeImageInTempCacheFromRemoval(graphicsId);
        }
    }

    public void removeFlaggedImagesInTempCache() {
        Enumeration e = temporaryImages.elements();
        while(e.hasMoreElements()) {
            ImageRecord ir = (ImageRecord)(e.nextElement());
            if (ir.flaggedForRemoval) {
                temporaryImages.remove("" + ir.graphicsId);
            }
        }
    }

    public synchronized boolean continueLoadingFromNet() {
        if (netError) {
            //observer.onImageLoadDebug("net error");
            return false;
        }

        // since the ImageManager will always set the current NetImageLoadRecord for loading, we know that we should continue loading only when
        // the curNetImageToLoad member is set
        if (curNetImageLoadRecord != null) {
            // proceed to load until one image is loaded or until max chunks have been received, abort on error

            long sleepReadRestoreValue = gbManager.getSleepRead();
            gbManager.setSleepRead(10);
            int numMessagesRead = 0;
            boolean imageLoaded = false;
            // receive next images / chunk(s) from server

            while (!netError && numMessagesRead < MAX_MESSAGES_PER_ITERATION && !imageLoaded) {
                byte[] message = gbManager.getDataAvailable();
                if (message!= null) {
                    numMessagesRead++;
                    imageLoaded = processServerMessage(message);
                } else {
                   break;
                }
                //System.out.println("in loop");
            }
            //try {Thread.sleep(200);} catch (Exception e) {}
            gbManager.setSleepRead(sleepReadRestoreValue);

            if (imageLoaded) {
                try {

                    // finished loading, create image from buffer
                    Image loadedImage = Image.createImage(curNetImageLoadRecord.dataBuffer, 0, curNetImageLoadRecord.size);
                    // insert into appropriate cache
                    putImageToCache(curNetImageLoadRecord.graphicsId, loadedImage, curNetImageLoadRecord.storeToPermanentCache);

                    // insert to record store if applicable
                    if (curNetImageLoadRecord.useRS) {
//System.out.println("ALSO STORING TO RS");
                        String filename = filenameFromGraphicsId(curNetImageLoadRecord.graphicsId);
                        insertImageToRecordStore(filename, curNetImageLoadRecord.dataBuffer);
                    }

                    // remove from loading queue
                    loadingQueue.removeElementAt(0);
                    observer.onImageLoadFromNetFinished(curNetImageLoadRecord.graphicsId, loadedImage, loadingCount());
                    curNetImageLoadRecord = null;

                    // be sure that the next time this is called we proceed with loading the next image, if any
                    if (loadingCount() > 0) {
                        // process next
                        processFirstInLoadingQueue();
                    }
                } catch (Exception e) {
                    // todo: react properly with net error notify
                    observer.onImageLoadFromNetError(curNetImageLoadRecord.graphicsId, "Image net load error.");
                    //System.out.println("Error: " + e.toString() + " " + e.getMessage());
                }
            }
        }
        return false;
    }




    /**
     * @return The number of images wating in the queue to complete loading
     */
    private int addMultipleToLoadingQueue(Vector graphicsIds, boolean toPermanentCache, boolean useRS) {
        int prevLoadCount = loadingCount();
        int addCount = graphicsIds.size();
        for (int i=0; i<addCount; i++) {
            Integer gId = (Integer)(graphicsIds.elementAt(i));
            NetImageLoadRecord nlRecord = new NetImageLoadRecord(gId.intValue(), toPermanentCache, useRS);
            loadingQueue.addElement(nlRecord);
        }
        
        if (prevLoadCount == 0) {
            // there were no images loading before, so request loading this one
            processFirstInLoadingQueue();
        }

        return loadingCount();
    }

    private void addToLoadingQueue(int graphicsId, boolean toPermanentCache, boolean useRS) {
        int prevLoadCount = loadingCount();

        // add to loading queue
        loadingQueue.addElement(new NetImageLoadRecord(graphicsId, toPermanentCache, useRS));

        if (prevLoadCount == 0) {
            // there were no images loading before, so request loading this one
            processFirstInLoadingQueue();
        }
    }


    /*
    private int getFirstGraphicsIdFromLoadingQueue() {
        if(loadingCount() > 0) {
            NetImageLoadRecord nlRecord = (NetImageLoadRecord)(loadingQueue.firstElement());
            if (nlRecord != null) {
                return nlRecord.graphicsId;
            }
        }
        return 0;
    }*/

    private boolean processFirstInLoadingQueue() {
        NetImageLoadRecord nlRecord = (NetImageLoadRecord)(loadingQueue.firstElement());
        if (nlRecord != null) {
            curNetImageLoadRecord = nlRecord;
            // send message to server
            sendGetImage(curNetImageLoadRecord.graphicsId);
            // notify observer
            observer.onImageLoadFormNetStarted(curNetImageLoadRecord.graphicsId, loadingCount());
            return true;
        }
        return false;
    }


    private boolean sendGetImage(int graphicsId) {
        
        /*

        // note: there is no message signature included in this message, so the server will need to act accordingly and not expect a signature for this message
System.out.println("sending get image for id: " + graphicsId);
        byte[] buffer = new byte[8];
        buffer[0] = 8;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_GRAPHICS_LOAD_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(graphicsId, buffer, 4);
        gbManager.sendMessage(buffer);
        */
        return true;
    }

    /**
     * 
     * @param message The message received from the server, normally containing either the size info or an image chunk
     * @return true if the currently loading image has been completely loaded, false otherwise
     */
    private synchronized boolean processServerMessage(byte[] message) {
        int firstByte = message[0];
        int msgId = 0;

        if (firstByte != 0) {
            msgId = NetTools.intFrom3Bytes(message[1], message[2], message[3]);
            if (msgId == FWGMessageIDs.MSGID_GAME_GRAPHICS_LOAD_INFO) {
//System.out.println("got graphic info");
                curNetImageLoadRecord.waitingForSizeInfo = false;
                int imageSize = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                if (imageSize == 0) {
                    // image cannot be loaded from server, signal error
                    netError = true;
                    observer.onImageLoadFromNetError(curNetImageLoadRecord.graphicsId, "Image not found on server");
                } else {
                    // got the size, store it and get ready to receive the chunks by preserving enough space in the receive buffer
                    curNetImageLoadRecord.size = imageSize;
                    curNetImageLoadRecord.dataBuffer = new byte[imageSize];

//System.out.println("size: " + imageSize);
                }
            } else {
                // some other message, pass through to observer
                observer.onImageLoadFromNetNonImageMessageReceived(message);
            }
        } else {
            // for an image chunk the first byte must be 0 as an image chunk is always a long message
            msgId = NetTools.intFrom3Bytes(message[3], message[4], message[5]);
            if (msgId == FWGMessageIDs.MSGID_GAME_GRAPHICS_LOAD_CHUNK) {
                if (curNetImageLoadRecord.waitingForSizeInfo) {
                    // cannot receive chunks at this point, signal error
                    netError = true;
                    observer.onImageLoadFromNetError(curNetImageLoadRecord.graphicsId, "Error while receiving image from server (message order).");
                } else {
                    // read in the chunk length and copy the chunk into the current byte buffer
                    int totalChunks = NetTools.intFrom2Bytes(message[6], message[7]);
                    int chunkNum = NetTools.intFrom2Bytes(message[8], message[9]);
                    int chunkLength = NetTools.intFrom4Bytes(message[10], message[11], message[12], message[13]);
                    if (curNetImageLoadRecord.dataWriteCursor + chunkLength > curNetImageLoadRecord.size) {
                        // total received siz exceeds expected image size
                        netError = true;
                        observer.onImageLoadFromNetError(msgId, "Error while receiving image from server (image size).");
                    } else {
                        observer.onImageLoadFromNetChunkLoaded(curNetImageLoadRecord.graphicsId, chunkNum, totalChunks);
                        System.arraycopy(message, 14, curNetImageLoadRecord.dataBuffer, curNetImageLoadRecord.dataWriteCursor, chunkLength);
                        curNetImageLoadRecord.dataWriteCursor += chunkLength;
                        if (curNetImageLoadRecord.dataWriteCursor == curNetImageLoadRecord.size) {
                            // image was fully loaded
                            return true;
                        }
                    }
                }
            } else {
                // some other message, pass through to observer
                observer.onImageLoadFromNetNonImageMessageReceived(message);
            }

        }
        return false;
    }
    

    private ImageRecord getImageRecordFromCache(int graphicsId) {
        ImageRecord imgRecord = (ImageRecord)permanentImages.get("" + graphicsId);

        if (imgRecord == null) {
            imgRecord = (ImageRecord)temporaryImages.get("" + graphicsId);
        }
        return imgRecord;
    }


    /** Internal class used for storing an image with its associated last time of use. */
    class ImageRecord {
        int graphicsId = 0;
        Image image = null;
        long lastUse = 0;
        boolean flaggedForRemoval = false;

        public ImageRecord(int graphicsId, Image newImage) {
            this.graphicsId = graphicsId;
            image = newImage;
            lastUse = System.currentTimeMillis();
        }

        public Image getImage() {
            lastUse = System.currentTimeMillis();
            return image;
        }

    }

    class NetImageLoadRecord {
        int graphicsId;
        int size = 0;
        boolean waitingForSizeInfo = true;
        boolean storeToPermanentCache = false;
        boolean useRS = true;
        int dataWriteCursor = 0;
        byte[] dataBuffer = null;
        boolean hasError = false;

        public NetImageLoadRecord(int graphicsId, boolean toPermanentCache, boolean useRS) {
            this.graphicsId = graphicsId;
            this.storeToPermanentCache = toPermanentCache;
            this.useRS = useRS;
        }


    }
}
