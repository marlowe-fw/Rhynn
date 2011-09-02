package FWUtils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class SwitchSetting {

    long toggleTime = -1;
    boolean on;

    public SwitchSetting(boolean val) {
        on = val;
    }

    public void setOn(boolean val) {
        setOnForDuration(val, -1);
    }
    public void setOnForDuration(boolean val, long lastForMilliSeconds) {
        on = val;
        if (lastForMilliSeconds > 0)
            toggleTime = System.currentTimeMillis() + lastForMilliSeconds;
        else
            toggleTime =-1;
    }

    public boolean isOn() {
        if (toggleTime > 0 && System.currentTimeMillis() > toggleTime) {
            on = !on;
            toggleTime = -1;
        }
        return on;
    }

    public boolean isOff() {
        return !isOn();
    }
}
