package rhynn;




import javax.microedition.lcdui.Image;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public interface ImageManagerObserver {
    //public void onImageLoadError(int graphicsId, String message);
    public void onImageLoadFromNetError(int graphicsId, String message);

    public void onImageLoadFormNetStarted(int graphicsId, int numRemainingInQueue);
    public void onImageLoadFromNetFinished(int graphicsId, Image loadedImage, int numRemainingInQueue);
    public void onImageLoadFromNetChunkLoaded(int graphicsId, int curChunk, int totalChunks);
    public void onImageLoadFromNetNonImageMessageReceived(byte[] message);

    public void onImageLoadDebug(String msg);
}
