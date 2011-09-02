package rhynn;




import graphics.GFont;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class GlobalSettings {

    private static GFont font = null;

    public static final int MAX_FLASHPHASEDURATION = 100;
    public static final int PLAYER_SPEED_PER_FRAME = 3;


    public static final int CHARACTER_CURSOR_DEFAULT = 0;
    public static final int CHARACTER_CURSOR_TRIGGER_TARGET = 1;
    public static final int CHARACTER_CURSOR_FIGHT = 2;
    public static final int CHARACTER_CURSOR_FIGHT_ACTIVE = 3;
    // private static int CHARACTER_CURSOR_FRIEND = 3;

    public static final int DEFAULT_WEAPON_RANGE = 36;
    public static final int DEFAULT_RECHARGE_TIME_NO_WEAPON = 1400;

    static GFont getFont() {return font;}
    static void setFont(GFont newFont) {font = newFont;}

}
