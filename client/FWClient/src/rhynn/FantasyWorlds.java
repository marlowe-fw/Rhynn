package rhynn;



/*
 * FantasyWorlds.java
 */

import rhynn.FantasyWorldsGame;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.Display;
//import javax.microedition.lcdui.Displayable;

/**
 * Main class of the game client.
 *
 * @author  marlowe
 */
public class FantasyWorlds extends MIDlet implements Runnable {
    
    /**
     * Thread for updating the screen.
     */
    private Thread                  updateThread;
    
    /**
     * Global current game time.
     */
    private long                    gameTime;

    private long                    lastTime;

    private long                    currentTime;
    
    private long                    timeDiff;
    
    /**
     * Last global game time.
     */
    private long                    lastGameTime;
    
    /**
     * Display of the MIDlet.
     */
    private Display                 display;
    
    /**
     * The Fantasy Worlds game.
     */
    private FantasyWorldsGame       fwg;
    
    /**
     * Indicates if the FW shutdown was already done properly.
     */
    private boolean                 shutDownDone = false;
    
    /**
     * Constructor.
     */
    public FantasyWorlds() {
        updateThread = null;
        
        fwg = new FantasyWorldsGame();

        fwg.initCanvas();
        
        display = Display.getDisplay(this);
        display.setCurrent(fwg);
        
        go();
    }
    
    protected void startApp() {
    }
    
    protected void pauseApp() {   
    }
    
    public void destroyApp(boolean unconditional) {
        if (!shutDownDone) {
            shutDown();
            shutDownDone = true;
        }
        
        updateThread = null;
        display.setCurrent(null);
    }
    
    public void go() {
        if(updateThread == null) {
            updateThread = new Thread(this);
            updateThread.setPriority(Thread.MIN_PRIORITY);
        }
        updateThread.start();
    }
    
    public void run() {
        // save the real start time ...
        currentTime = System.currentTimeMillis();
        // ... for updating the gameTime
        lastTime = currentTime;
        
        while(!fwg.shutdown) {
            
            lastTime = currentTime;
            // here we update the game time with the passed time in the real world
            currentTime = System.currentTimeMillis();
            
            // remember the time
            lastGameTime = gameTime;
            gameTime += currentTime - lastTime;
            
            fwg.curGametime = gameTime;
            fwg.lastGametime = lastGameTime;
            fwg.actualTime = currentTime;
            
            // update the game scene
            //fwg.updateGame(gameTime, lastGameTime);
            //fwg.updateGame();
            
            
            // render the game
            fwg.doPaint = true;
            fwg.repaint();

            // wait a little bit
            //updateThread.yield();
            try {
                //updateThread.yield();

                /*
                timeDiff = System.currentTimeMillis() - currentTime;
                if(timeDiff > 20) {
                    updateThread.sleep(20);
                } else {
                    updateThread.sleep(40-timeDiff);
                    //System.out.println("SLEEPING " + (100-timeDiff));
                }
                */
                
                // -> MAX 20 fps
                
                timeDiff = System.currentTimeMillis() - currentTime;
                updateThread.sleep(20);

                /*
                if(timeDiff >= 0 && timeDiff < 20) {
                    updateThread.sleep(20 + 20-timeDiff);
                    //System.out.println("SLEEPING " + (100-timeDiff));
                } else {
                    updateThread.sleep(20);
                }*/
                
            } catch(Exception e) {
                System.out.println("ex");
            }
            
        }
        if (fwg.shutdown) {
            shutDown();
        }
    }
    
    private void shutDown() {
        // Logout + stop net
        fwg.shutDown();
        shutDownDone = true;

        display.setCurrent(null);
        destroyApp(false);
        notifyDestroyed();
    }
}
