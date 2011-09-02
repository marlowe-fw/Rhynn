/*
 * GSound.java
 */

package sound;

import java.io.*;

import javax.microedition.media.*;
import javax.microedition.media.control.*;

/**
 *
 * @author  nopper
 */
public class GSoundTools { // implements PlayerListener 

    /**
     * The player for MIDI files.
     */
    public Player        midiPlayer;
    
    /**
     * The volume control for the player.
     */
    public VolumeControl volume;

    public GSoundTools() {
    }
        
    /**
     * Initializes a player and a volume control.
     */
    public synchronized void initSound(InputStream is) {
        
        try {
            if(midiPlayer != null) {
                midiPlayer.stop();
                //midiPlayer.setMediaTime(0);
                midiPlayer.close();
                midiPlayer = null;
                System.gc();
            }
            is.reset();
            midiPlayer = Manager.createPlayer(is, "audio/midi");

            midiPlayer.realize();

            volume = (VolumeControl)midiPlayer.getControl("javax.microedition.media.control.VolumeControl");
            
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }
    
    /*
    private void printPlayerState() {
        switch (midiPlayer.getState()) {
            case Player.CLOSED:
                System.out.println("CLOSED");
                break;
            case Player.UNREALIZED:
                System.out.println("UNREALIZED");
                break;
            case Player.REALIZED:
                System.out.println("REALIZED");
                break;
            case Player.PREFETCHED:
                System.out.println("PREFETCHED");
                break;
            case Player.STARTED:
                System.out.println("STARTED");
                break;
        }
    }
    */
    
    /**
     * Starts and plays a sound with the amount of loops. -1 is infinite.
     */
    public synchronized boolean startSound(int loops, int level) {
        
        if (midiPlayer==null) {
            return false;
        }
        
        try {
            if (midiPlayer.getState() == Player.STARTED) {
                    midiPlayer.stop();
                    /*
                    while(midiPlayer.getState() != Player.PREFETCHED) {
                        try {
                            Thread.sleep(100);
                        } catch(Exception e){}
                    }
                     */
                }

            /*
            if (midiPlayer.getState() == Player.PREFETCHED) {
                midiPlayer.deallocate();
                while(midiPlayer.getState() != Player.REALIZED) {
                    try {
                        Thread.sleep(100);
                    } catch(Exception e){}
                }
            }
              */              
            System.gc();

            /*
            midiPlayer.prefetch();

            while(midiPlayer.getState() != Player.PREFETCHED) {
                try {
                    Thread.sleep(100);
                } catch(Exception e){}
            }
            */

            //midiPlayer.setMediaTime(0);
            midiPlayer.setLoopCount(loops);
            setVolume(level);
            midiPlayer.start();

            /*while(midiPlayer.getState() != Player.STARTED) {
                try {
                    Thread.sleep(100);
                } catch(Exception e){}
            } */           
                
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
    
    /**
     * Stops the sound.
     */
    public synchronized boolean stopSound() {
        try {
            if (midiPlayer.getState()==Player.STARTED) {
                midiPlayer.stop();
                //midiPlayer.deallocate();
            }
        } catch(Exception e) {
            return false;
        }
        return true;
    }
    
    /**
     * Sets the volume level. Valid values are:
     * -1       Mute
     * 0 .. 100 Volume level, where 100 is loudest.
     */
    public synchronized boolean setVolume(int level) {
        try {
            if (this.volume != null) {
                if(level == -1) {
                    volume.setMute(true);
                } else {
                    if(volume.isMuted()) {
                        volume.setMute(false);
                    }
                    volume.setLevel(level);
                }
            }
        } catch(Exception e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
    
    
    /**
     * Receive and react on any asynchronous events delivered by the player.
     */
    /*
    public void playerUpdate(Player player, String event, Object eventData) {
        
        if (event.equals(PlayerListener.CLOSED)) { //Posted when a Player is closed.
            System.out.println("%%%%%%%%%%%%%%%%%%%%% CLOSED");
        }
        if (event.equals(PlayerListener.DEVICE_AVAILABLE )) {//Posted when the system or another higher priority application has released an exclusive device which is now available to the Player. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% DEVICE_AVAILABLE");
        }
        if (event.equals(PlayerListener.DEVICE_UNAVAILABLE )) {//Posted when the system or another higher priority application has temporarily taken control of an exclusive device which was previously available to the Player. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% DEVICE_UNAVAILABLE");
        }
        if (event.equals(PlayerListener.DURATION_UPDATED )) {//Posted when the duration of a Player is updated. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% DURATION_UPDATED");
        }
        if (event.equals(PlayerListener.END_OF_MEDIA )) {//Posted when a Player has reached the end of the media. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% END OF MEDIA");
        }
        if (event.equals(PlayerListener.ERROR )) {//Posted when an error had occurred. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% ERROR");
        }
        if (event.equals(PlayerListener.STARTED )) {//Posted when a Player is started. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% STARTED");
        }
        if (event.equals(PlayerListener.STOPPED )) {//Posted when a Player stops in response to the stop method call. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% STOPPED");
        }
        if (event.equals(PlayerListener.VOLUME_CHANGED )) {//Posted when the volume of an audio device is changed. 
            System.out.println("%%%%%%%%%%%%%%%%%%%%% VOLUME CHANGED");
        }
    }
     **/
    
}
