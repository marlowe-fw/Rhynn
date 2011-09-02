/*
 * GSoundChecker.java
 */

package sound;

/**
 *
 * @author  nopper
 */
public class GSoundChecker {

    /**
     * Checks, if the device can play sounds.
     */
    public static boolean checkSound() {
        try {
            Class c = Class.forName("javax.microedition.media.Player");
            c = null;
            System.gc();
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
}
