package rhynn;



/*
 * FantasyWorldsGame.java
 *
* NetBeans configurations:
 *   - default: Nokia FullCanvas
 *   - SonyEricssonWTK2_0MIDP2_0: MIDP 2.0 with screen min 128x160, Softkeys: -6, -7 (Nokia, SonyEricsson)
 *   - Series40_MIDP2_0: MIDP 2.0 with screen 128x128, Softkeys: -6, -7 (Nokia, SonyEricsson)
 *   - SonyEricssonP800: MIDP 1.0, Softkeys: -10, -12
 */

//#if __VALIDATOR_IMPL
import crypto.PacketValidatorImpl;
//#endif

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


import java.io.*;
import java.util.*;

import net.*;
import graphics.GFont;
import graphics.GTools;
import graphics.GWindow;
import graphics.GTextWindow;
import graphics.GInputWindow;
import graphics.GImageWindow;
import graphics.GList;
import graphics.GMenu;

import data.*;

import graphics.GImageClip;
import sound.*;


//#if DefaultConfiguration
import com.nokia.mid.ui.FullCanvas;
//#else
//# import javax.microedition.lcdui.Canvas;
//#endif

/**
 * The game screen and the main game.
 *
 * @author  marlowe
 */
public class FantasyWorldsGame extends
//#if DefaultConfiguration
        FullCanvas
//#else
//#         Canvas
//#endif
implements ImageManagerObserver
{
     private final static boolean release = false;

    private static String testHost = "127.0.0.1";
    private static String releaseHost = "127.0.0.1";
    private  static String defaultHost = "127.0.0.1";
    private String host = defaultHost;

    
//#if WebEmulator
//#      private final static boolean RSForcedOff = true;
//#else
     private final static boolean RSForcedOff = false;
//#endif
    // $-> RELEASE
    private final static byte billingInfo = 1;  
                                                
    // $-> RELEASE
    private static boolean clusterDisable = false; // SET FOR RELEASE: false
    private static boolean readDefaultHostFromDB = true; // SET FOR RELEASE: true
    

    // ==============
    //VERSION 1.4 -- corresponds to new server
    // ==============
    private final static int versionHigh = 1;
    private final static int versionLow  = 4;
    private final static int versionLowSub = 4;

    private static final String versionExtra = "pre-alpha";
    private static final String versionCodeName = "Spark";

    private String versionName(boolean useLeadingZero) {
        String name = versionHigh + "." + versionLow + ".";
        if (useLeadingZero && versionLowSub < 10) {
            name += "0";
        }
        name += versionLowSub;
        if (versionExtra != null) {
            name += " " + versionExtra;
        }
        if (versionCodeName != null) {
            name += " (" + versionCodeName + ")";
        }
        return name;
    }

     
//#if Series40_MIDP2_0 || SonyEricssonWTK2_0MIDP2_0
//#     private static int KEY_SOFTKEY1 = -6;
//#     private static int KEY_SOFTKEY2 = -7;
//#else
    private static int KEY_SOFTKEY1 = -6;
    private static int KEY_SOFTKEY2 = -7;
//#endif

// Motorola
 // private static int KEY_SOFTKEY1 = 21;
 //   private static int KEY_SOFTKEY2 = 22;
    

public boolean keyPressedNoConflict(int keyCode, int checkKey) {
    return  keyCode == checkKey
            && keyCode != KEY_SOFTKEY1
            && keyCode != KEY_SOFTKEY2
            ;
}


//#if SonyEricssonP800
//# private final static int KEY_SOFTKEY1 = -10;
//# private final static int KEY_SOFTKEY2 = -12;
//#endif
       


    
//#if Series40_MIDP2_0
//#     private final static int    MAX_CACHE_IMAGES = 8;
//#else
    private final static int    MAX_CACHE_IMAGES = 14;
//#endif

    private final static int MAX_CONVERSATIONS = 16;
    
    // ==========================================================
    
    // PUBLIC MEMBERS
    public long                                 curGametime;
    public long                                 lastGametime;
    public long                                 actualTime;
    public boolean                              doPaint;
    
    
    private boolean showTraffic = false;

    boolean showDebug4 = false; //fps, availableBytes
    boolean showDebug5 = false; //memory
    boolean showDebug6 = false; //key times
    
    int[] frameTimes = new int[10];
    int frameTimePointer = 0;
    
    private final static String gameName = "FantasyWorlds";
    
    //SETTINGS
    private static int debugLevel = 0;

    long[][] soundTimes = {{0, 120000000}, {130003766, 187000000}};
    
    /** Default socket port R. */
    public final static int               SOCKETREAD     = 15169;

    /** Default socket port RW. */
    public final static int               SOCKETPORT     = 23179;

    /** Default socket port W. */
    public final static int               SOCKETWRITE    = 15171;
    
    
    private static int DEFAULT_PACKETPERLOOP = 8;
    private static int PACKETPERLOOP = DEFAULT_PACKETPERLOOP;
    
    
    private final static int TRAFFIC_TIMEOUT = 35000;
    private final static int RECEIVE_TIMEOUT = 60000;
    private final static int PINGINTERVAL = 15000;
    private final static int TIMEPASSEDINTERVAL = 25000;
    
    private static int DISPLAYWIDTH = 96;
    private static int DISPLAYHEIGHT = 96;
    private static int TOTALHEIGHT;
    private static final int MIN_DISPLAYWIDTH = 96;
    private static final int MIN_DISPLAYHEIGHT = 96;
    private static int BOTTOM_INFOHEIGHT = 16;
//#if Series40_MIDP2_0
//#     private final static int TOP_INFOHEIGHT = 0;
//#else
    private final static int TOP_INFOHEIGHT = 20;
//#endif
    private final static int MAX_PLAYERTYPES    = 4;
    
    
    private final static int TILEWIDTH = 24;
    private final static int TILEHEIGHT = 24;
    private final static int PLAYERWIDTH = 20;
    private final static int PLAYERHEIGHT = 20;
    private final static int PLAYERWIDTH_HALF = 10;
    private final static int PLAYERHEIGHT_HALF = 10;
    public final static int ITEMWIDTH              = 15;
    public final static int ITEMHEIGHT             = 15;
    public final static int ITEMSLOTWIDTH          = 16;
    public final static int ITEMSLOTHEIGHT         = 16;

    
    public final static int QUESTCLASSES_ICON_SIZE = 18;
    public final static int INGAME_ICON_WIDTH     = 16;
    public final static int INGAME_ICON_HEIGHT     = 16;
    public final static int DIM                    = 5;
    //public final static int PLAYER_DIM             = 4;
    
    //number of entries in the action menu
    
    public final int SCROLL_RANGE = 50;
    
    /** Amount of tiles of the background template. */
    private final static int TILES = 4;
    
    private final static int INVENTORY_COLS           = 11;
    private final static int INVENTORY_ROWS           = 2;

    // EQUIPMENT TYPES
    private final int WEAPON = 0;
    //private final int AMMUNITION = 1;
    private final int SHIELD = 2;
    private final int HELMET = 3;
    private final int HANDPROTECTION = 4;
    private final int ARMOUR = 5;
    private final int BOOTS = 6;

    //private final int GlobalSettings.DEFAULT_WEAPON_RANGE = 44;

    public final static byte OVERLAY_NONE           = 0;
    public final static byte OVERLAY_MESSAGE        = 1;
    public final static byte OVERLAY_GAMEOPTIONS    = 2;
    public final static byte OVERLAY_SOUND          = 3;
    public final static byte OVERLAY_CREDITS        = 4;
    public final static byte OVERLAY_DIED           = 5;
    public final static byte OVERLAY_HELP           = 6;
    public final static byte OVERLAY_HELP_WAIT      = 7;

    public final static byte OPTIONSTATE_NONE       = 0;
    public final static byte OPTIONSTATE_EMAIL_ENTRY = 10;

    public final static byte OPTIONSUBSTATE_NONE       = 0;
    public final static byte OPTIONSUBSTATE_EMAIL_GET = 4;
    public final static byte OPTIONSUBSTATE_EMAIL_OPTIONS = 5;
    public final static byte OPTIONSUBSTATE_EMAIL_CHANGE_WAIT = 7;


    /** POSSIBLE GAME STATES. */    
    public final static int STATE_INTRO         = 0;
    public final static int STATE_INTRO_EULA    = 5;
    public final static int STATE_INTRO_LIST    = 10;
    private final static int STATE_PRE_CONNECT  = 20;
    private final static int STATE_WAIT_FOR_CONNECT_THREAD  = 30;
    private final static int STATE_WAIT_FOR_CONNECT_THREAD_PORTAL  = 40;
    private final static int STATE_CONNECT      = 50;
    private final static int STATE_CONNECT_GET_SERVERS = 60;
    private final static int STATE_CONNECT_ERROR   = 70;
    private final static int STATE_CONNECT_CHECK_VERSION = 80;
    public final static int STATE_LOGIN_MENU    = 90;
    public final static int STATE_WAIT          = 100;
    public final static int STATE_CHARACTER_SELECT  = 110;
    public final static int STATE_INVENTORY_LOAD_WAIT = 120;
    public final static int STATE_FRIEND_RECEIVE_LIST_WAIT = 130;
    public final static int STATE_REGISTER_NEW      = 140;
    public final static int STATE_REGISTER_NEW_WAIT = 150;
    public final static int STATE_REGISTER_OK   = 160;
    public final static int STATE_LOGIN_ERROR   = 170;
    public final static int STATE_WAIT_LOAD_GFX = 180;
    public final static int STATE_GAUGE         = 190;
    public final static int STATE_GAME          = 200;
    public final static int STATE_FORCED_EXIT   = 210;
    public final static int STATE_BLACK         = 220;
    private final static int STATE_RESPAWN_REQUEST = 250;
    private final static int STATE_RESPAWN_WAIT    = 260;
    private final static int STATE_SUBSCRIBE_NEW   = 300;
    private final static int STATE_SUBSCRIBE_OPTIONS = 320;
    private final static int STATE_SUBSCRIBE_WAIT_FOR_RESPONSE = 360;
    private final static int STATE_SUBSCRIBE_DONE_EXIT = 380;
    private final static int STATE_SUBSCRIBE_EXIT_CONFIRM = 400;
    private final static int STATE_SUBSCRIBE_EXIT_WAIT_FOR_MSG = 420;
    public final static int STATE_EMAIL_ENTRY      = 440;
    public final static int STATE_GET_PASSWORD_RESET_CODE = 470;
    public final static int STATE_ENTER_NAME_FOR_RESET_CODE = 480;
    public final static int STATE_ENTER_PASSWORD_RESET_CODE = 490;
    public final static int STATE_RECOVER_PASSWORD_MAIN_OPTIONS = 500;
    public final static int STATE_DEFINE_KEYS = 520;


    private final static byte SUBSTATE_NORMAL                = 0;
    private final static byte SUBSTATE_ACTIVE                = 1;
    //private final static byte SUBSTATE_PASSIVE               = 2;

    private final static byte SUBSTATE_DEFINE_KEY_SK1       = 5;
    private final static byte SUBSTATE_DEFINE_KEY_SK2       = 6;
    
    private final static byte SUBSTATE_ACTIONMENU               = 9;
    private final static byte SUBSTATE_INVENTORY             = 10;
    private final static byte SUBSTATE_INVITEM_OPTIONS       = 11;
    private final static byte SUBSTATE_TALKTO_FIND           = 12;
    private final static byte SUBSTATE_TALKTO                = 13;   
    private final static byte SUBSTATE_TALKTOALL            = 14;
    private final static byte SUBSTATE_TALKINPUT_OPTIONS    = 15;
    private final static byte SUBSTATE_TALK_SUBOPTIONS      = 16;
    private final static byte SUBSTATE_CHAT_SHORTCUT_SELECT = 17;   
    private final static byte SUBSTATE_CHAT_SHORTCUT_EDIT   = 18;
    private final static byte SUBSTATE_CHAT_SHORTCUT_EDIT_DETAIL = 19;
    private final static byte SUBSTATE_DIALOGUE_INIT        = 20;
    private final static byte SUBSTATE_DIALOGUE_ACTIVE      = 21;

    private final static byte SUBSTATE_QUEST_OVERVIEW       = 23;
    private final static byte SUBSTATE_QUEST_OVERVIEW_OPTIONS = 24;
    private final static byte SUBSTATE_QUEST_REQUESTDETAILS = 25;
    private final static byte SUBSTATE_QUEST_DETAILS        = 26;
    private final static byte SUBSTATE_QUEST_DELETE_CONFIRM = 27;
    private final static byte SUBSTATE_QUEST_DELETE_WAIT    = 28;
    private final static byte SUBSTATE_FIGHT_FIND           = 29;
    private final static byte SUBSTATE_FIGHT_ACTIVE           = 30;
    private final static byte SUBSTATE_EVENT_LIST            = 32;
    private final static byte SUBSTATE_EVENT_LIST_OPTIONS    = 33;
    
    private final static byte SUBSTATE_TRIGGERTARGET_FIND    = 37;
    private final static byte SUBSTATE_TRIGGERTARGET_ACTIVE = 38;
    
    private final static byte SUBSTATE_GROUND_FIND        = 40;
    private static final byte SUBSTATE_BELT_SELECT_SLOT      = 43;
    private static final byte SUBSTATE_BELT                  = 44;
    
    private final static byte SUBSTATE_OK_DIALOG             = 50;
    private final static byte SUBSTATE_PORTAL_WAIT           = 55;    
    private final static byte SUBSTATE_FAR_PORTAL_WAIT       = 60;
    private final static byte SUBSTATE_FAR_PORTAL_LIST       = 64;
    private final static byte SUBSTATE_LOAD_CHR_GFX          = 69;
    private final static byte SUBSTATE_CHARACTER_OPTIONS     = 70;    
    private final static byte SUBSTATE_CHARACTER_NEW         = 71;
    private final static byte SUBSTATE_CHARACTER_NEW_NAME    = 72;
    private final static byte SUBSTATE_CHARACTER_RENAME      = 73;
    private final static byte SUBSTATE_CHARACTER_RENAME_WAIT  = 74;
    private final static byte SUBSTATE_CHARACTER_DELETE_WAIT    = 75;
    private final static byte SUBSTATE_CHARACTER_DELETE_CONFIRM = 76;
    private final static byte SUBSTATE_MAIN_MENU             = 77;
    private final static byte SUBSTATE_BUILDCHARACTER        = 78;

    private final static byte SUBSTATE_TRADE_FIND            = 80;
    private final static byte SUBSTATE_TRADE_BUY_CONFIRM     = 81;    
    private final static byte SUBSTATE_TRADE_REQUEST         = 82;
    private final static byte SUBSTATE_TRADE_TRANSFER_WAIT   = 83;

    private final static byte SUBSTATE_SET_ITEMOFFER         = 90;
    private final static byte SUBSTATE_SET_DROPITEM_AMOUNT   = 94;
    
    private final static byte SUBSTATE_FRIEND_SUBOPTIONS      = 100;
    private final static byte SUBSTATE_FRIEND_FIND            = 101;
    private final static byte SUBSTATE_FRIEND_FIND_CONFIRM    = 102;
    private final static byte SUBSTATE_FRIEND_REQUEST_LIST    = 107;
    private final static byte SUBSTATE_FRIEND_REQUEST_LIST_OPTIONS = 109;
    private final static byte SUBSTATE_FRIEND_REQUEST_ACCEPT_CONFIRM = 111;
    private final static byte SUBSTATE_FRIEND_LIST            = 112;    
    private final static byte SUBSTATE_FRIEND_LIST_OPTIONS    = 113;
    private final static byte SUBSTATE_FRIENDSHIP_CANCEL_CONFIRM = 114;
    public final static byte SUBSTATE_EMAIL_OPTIONS      = 116;
    public final static byte SUBSTATE_EMAIL_CHANGE_WAIT      = 117;

    public final static byte SUBSTATE_RECOVER_PASSWORD_WAIT      = 118;


    public boolean shutdown = false;        
    
    private int nextState = -1;
    private int nextSubState = -1;
    
    private final static int MAX_NUMDEADBODIES = 5;
    private final static int MAX_BELT_ITEMS    = 4;

    private final static int MESSAGE_MAXSHOWDURATION = 8500; 
    private final static int MAX_HITSHOWDURATION = 1800;
    private final static int MAX_ATTACKSHOWDURATION = 450;
    private final static long MAX_DEADSHOWDURATION = 10000;

    private final static int MAX_CONTEXTOPTIONS = 8;
    private final static int MAX_FREECONTEXTOPTIONS = 9;
    private final static int ACTION_ITEMS = 7;
    private static int       ACTION_MENU_SIZE = ACTION_ITEMS*25;
    
    private final static int ICON_HITLOCAL = 0;
    private final static int ICON_BONUSLOCAL = 1;
    private final static int ICON_HIT = 2;
    private final static int ICON_DEFEND = 3;
    private final static int ICON_DEAD = 4;
    //private final static int ICON_TRADE = 5;
    private final static int ICON_ATTACK = 6;
    private final static int ICON_ATTACK_SPELL1 = 11;
    private final static int ICON_ATTACK_SPELL2 = 12;
    // private final static int ICON_ATTACK_SPELL3 = 13;
    
    private static int MAX_CHATCHARS_PER_LINE = 24;
    private static int MAX_CHATCHARS = 32;
    
    private int user_DB_ID;
    private int character_DB_ID;

    private String usernameForResetCode;

    /** The current state of the game. */
    public int currentState;
    public int currentSubState;
    public int overlayState;
    public int optionState;
    public int optionSubState;


    /**  Offset to the right. */
    private int xOffset;
    
    /** Offset to the top. */
    private int yOffset;
    
    /** The image to render on the current scene. */
    private Image         currentImage;
    
    /** The buffered image to render on the current scene. */
    private Image         currentImage2;
    
    /** The grapics context of image one. */
    private Graphics      graphicsOne;
    
    /** The grapics context of image one. */
    private Graphics      graphicsTwo;
    
    /** The current grapics context. */
    private Graphics      currentGraphics;
    
    /** Flag for doing double buffering. */
    private boolean       flip;
    
    /** The image with the background tiles. */
    private Image         background;
    private Image         dynamic;
    //private Image         ingame;
    private Image         phoneTemplateImage;
    private Image          special;
    private Image         menu;
    private Image         questclasses;
    private Image         players;

    // frequently used icons
    GImageClip iconMessageNew = null;
    GImageClip iconFriendRequestNew = null;
    GImageClip iconFriendOnline = null;
    GImageClip iconFriendOffline = null;
    GImageClip inventoryBackgroundSlot = null;

    /** Remember the types of the images you already loaded from net, plus temporary Image. */
    private int          prevType = -1;
    private int          prevDynamicT = -1;
    private Image         tempBack;
    
    /** Images dynamically loaded from net, contains the graphics of the enemies. */
    private Image[]         enemies = new Image[255];
    
    /** Filename of the image.*/
    // private String        backgroundString;
    
    /** The legacyPlayfield.*/
    private byte[][]      legacyPlayfield;
    Playfield playfield;
    PlayfieldActorView  playfieldView;
    
    private String          playfieldName = null;
    private String          playfieldServer = null;
    private int             playfieldID;
    private int             playfieldType;
    private int             dynamicType;
    private int             playfieldWidth;
    private int             playfieldHeight;
    
    private boolean         loadPlayfield = false;
    private boolean         loadingPlayfield = false;
    
    private Character playerObject = null;
    
    private int           xPos;
    private int           xCounter;
    private int           xStart;
    private int           xEnd;
    
    private int           yPos;
    private int           yCounter;
    private int           yStart;
    private int           yEnd;
    
    //
    
    private int           playerScreenX;
    private int           playerScreenY;
    
    private int          playerDirection;
    private boolean       playerMove;
    private boolean       playerDirectionChanged;
    private int           playerSpeed;
    private int           playerAnim;
    
    /** The current incoming data. */
    //private byte[]                  currentData;
    
    /** Counter, how many packets arrived. */
    private int                     playfieldCounter;
    
    /** Container for all characters in the current world. */
    private Hashtable               idToCharacters;
    
    /** Container for all items in the current world. */
    private Hashtable               idToItems;

    //private Hashtable               idToTempDroppedItems;
    
    /** Name of client (login name for FW). */
    private String                  clientName =  null;
    
    /**  Password of client (login pasword for FW). */
    private String                  clientPass = null;
    
    private int                  clientCharacterNumber;
    
    
    /** Time last send. */
    private long                    posLastSent;
    
    /** Flag, if the situation of the players changed */
    //private boolean                 changed;
    private boolean                 sendPos;
    
    /** ??? */
    private long                    timeBreak;

    private long                    timePassed;
    
    private long                    timeDiff;
    
    /** Generic check time */
    private long                    lastCheck;
    
    private long                    lastFlash;
    private long                    lastFastFlash;
    
    private long                    lastFireWallCheck;
    
    private int                     messageTimeout = -1;
    private String                  messageTimeoutMessage = null;
    private int                     stateAfterTimeout=-1;
    private int                     subStateAfterTimeout=-1;



    // ----------------------------


    private static char[] awaitedMessage = new char[4];
    private static int awaitedMessageId = 0;
    
    
    private boolean flash = false;
    private boolean fastFlash = false;
    private boolean allowGameInput = false;
    
    /** Send buffer for messages. */
    private byte[]					buffer;

    int packetCounter = 0;
    
    /** Defines which group was joined most recently. */
    private String lastJoinedGroup = null;
    
    /** The image for the items. */
    private Image[]                               items = new Image[2];
    
    private char[] actionPartnerName;
    private int actionPartnerID = -1;
    private int sellerID = -1;
    private char[] ownName;
    private boolean chatRequest=false;
    private int chatRequestID = -1;
    private char[] chatBuffer = null;
    private boolean noAction = false;
    private long lastHit = 0;
    
    private int triggerTarget_TriggerType;
    private int triggerTarget_ItemID;
    
    private GWindow                 gaugeWindow  = null;
    private GWindow                 gaugeWindow1 = null;
    private GMenu                   menuLogin=null;
    private GMenu                   menuEmail=null;
    private GMenu                   menuChat=null;
    private GMenu                   menuTrade=null;
    private GTextWindow             eulaWindow=null;
    private GTextWindow             chatWindow=null;
    private GInputWindow            emailField1=null;
    private GInputWindow            emailField2=null;
    private GImageWindow            atIcon = null;
    private GImageWindow            phoneTemplate = null;
    private GInputWindow            inputChatWindow=null;
    private GInputWindow            usernameWindow=null;
    private GInputWindow            passwordWindow=null;
    // private GTextWindow             loginBackButton = null;            
    private GTextWindow             bottomInfoWindow = null;
    
    private GTextWindow              info1Line = null;
    private GTextWindow              info1Line2 = null;
//#if !(Series40_MIDP2_0)
    private GTextWindow              playerLevelWindow = null;
    private GTextWindow              playerExperienceWindow = null;
//#endif
    private GTextWindow              playerGoldWindow = null;
    private GTextWindow              labelWait = null;
    private GTextWindow              label1 = null;
    private GTextWindow              label2 = null;

    private final int                CM_BOTTOM = 0;
    private final int                CM_OVERLAY = 1;
    private final int                CM_OPTION = 2;

    private GTextWindow              comButton1 = null;
    private GTextWindow              comButton2 = null;
    private GTextWindow              overlayButton1 = null;
    private GTextWindow              overlayButton2 = null;
    private GTextWindow              optionButton1 = null;
    private GTextWindow              optionButton2 = null;

    
    private GMenu                    menuActionSub = null;
    private GMenu                    menuGameOptions = null;

    private GTextWindow               confirmWindow = null;

    private GMenu                    editBox = null;
    private GInputWindow             editBoxInput = null;
    
    private GMenu                    priceBox = null;
    private GMenu                    dropItemBox = null;
    //private GInputWindow             priceInput = null;
    private GInputWindow             amountInput = null;
    private GTextWindow             labelAmount = null;
    //private GTextWindow             labelPrice = null;
    
    private GInputWindow            dropAmountInput = null;
    private GTextWindow             labelDropAmount = null;
    private GWindow                 dropAmountSpinbutton = null;

    
    private GList                    genericList = null;
    private GList                    bigList = null;
    private GList                    friendList = null;
    private GList                    friendRequestList = null;
    private GMenu                    menuList;
    private GMenu                    menuBigList = null;
        
    private GInputWindow              talkToAllInput = null;
    
    private GFont                     font;
    private Image                     fontImage = null;

    //private int[]                               invItems;
    private int                                 selectedTradeSlot;
    private int                                 selectedInvItem;
    private int                                 selectedActionMenuEntry;
    private int                                 invItemsCount;
    private int                                 tradeOfferItemsCount;
    private int                                 itemIndex;
    private int                                 itemPos;
    private Item[]                              invItems;
    private Item[]                              equipment;
    private Item[]                              belt;
    private int                                 selectedBeltItem = 0;
    
    private boolean                             requestInventory=true;
    private boolean                             requestOpenQuests=true;
    private boolean                             inventoryNeedsScrolling = false;
    private int                                 inventoryOffset = 0; //x-Offset for phones with small display (<176)
    private long                                inventoryScrollHugeItemTime = 0;
    private final int                           inventoryScrollDuration = 1500;
    private boolean                             inventoryScrollHugeItemDown = true;
    private int                                 inventoryScrollHugeItemOffset = 0;
    
    private byte functionCellX;
    private byte functionCellY;

    private byte curCellX;
    private byte curCellY;
    private byte cellWindow_XStart;
    private byte cellWindow_YStart;

    private static int FIREWALL_WINDOWSIZE = 13;
    private static int FIREWALL_CELLRANGE = 6;
    private byte[][] fireWalls = new byte[FIREWALL_WINDOWSIZE][FIREWALL_WINDOWSIZE];
    private byte[][] tmpFireWallArray = new byte[FIREWALL_WINDOWSIZE][FIREWALL_WINDOWSIZE];
    //byte[][] tmpFireWallArray = new byte[9][9];
    byte[] emptyFireWallElement = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    
    int[][] specialFields                        = null;
    
    
    int minDisplayXPos = 0;
    int minDisplayYPos = 0;

    private StringBuffer sb = new StringBuffer();
    private char[] tmpCharsK = null;
    private char[] tmpCharsK1 = null;
    private char[] tmpCharsM = null;
    private char[] tmpChars1M = null;
    private String tmpStringM = null;
    private String tmpStringM1 = null;
    private String tmpStringK = null;
    
    private static boolean offline = false;
    private static boolean netStarted = false;
    private static boolean netError = false;
    
    private Character[] playersOnScreen = new Character[16];
    private int[] playersOnScreenXSortedIndex = new int[16];
    private int[] playersOnScreenYSortedIndex = new int[16];

    
    private int selectedPlayer;
    
    private Character characterTmpD = null;
    private Item itemTmpD = null;
    
    private Character characterTmpK = null;
    private Item itemTmpK = null;
    
    private Character characterTmpM = null;
    private Item itemTmpM = null;
    
    private Character characterTmp1M = null;
    private Item itemTmpM1 = null;
    
    private WorldObject fwgoTmpM = null;
    
    private final static int MAX_FAR_PORTALS = 32;
    
    private final static int MAX_QUEUED_EVENTS = 16;
    private int[] queuedEventsIDs = new int[MAX_QUEUED_EVENTS];

    private final static int MAX_QUEUED_FRIEND_REQUESTS = 8;
    //private int[] queuedFriendIDs = new int[MAX_QUEUED_FRIEND_REQUESTS];
    private final static int MAX_FRIENDS = 24;
    
    private static boolean addCharacterOK = false;
    private static boolean initGameWindows = true;

    //Item[] tradeBox = new Item[8];

    private boolean tradeAccept, tradeAcceptOther;
    private boolean listMenuConstructed = false;
    
    private int i,j,k,l,m,n;
    private int w,h,s;    
    private int k1, k2, k3, k4, k5, d, d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, m1, m2, m3, m4, m5, m6;
    private boolean db1, db2, mb1, kb1;
    private boolean checkNet = false;
    private GameBitsNetManager gbManager = new GameBitsNetManager();
    private PacketValidator packetValidator;

    
    private long curTime;
    private long lastPingSent;
    private int usersOnline;
    private int usersTotal;
    
    private boolean bCommand1 = false;
    private boolean bCommand2 = true;
    private boolean ovCommand1 = false;
    private boolean ovCommand2 = true;
    private boolean opCommand1 = false;
    private boolean opCommand2 = true;

    private int doConnect = 0;
    private boolean bExitConfirm = false;
    
    private long info1Line_DisplayTime;    
    private long bottomInfo_DisplayTime;
    private long peacefulDisplayTime;
    private boolean bottomInfo_Foreground = false;
    
    private int gAction;


    private boolean isInWeaponRange = false;
    
    private int[] characterModifier = new int[7];

    private int maxhealth;
    private int maxmana;
    private int attack;
    private int defense;
    private int skill;
    private int magic;
    private int damage;
    private int healthregenerate;
    private int manaregenerate;
    
    private int deadBodyCount;
    private Character[] deadCharacters = new Character[MAX_NUMDEADBODIES];
    
    private String bottomCommand1AfterTimeout = null;
    private String bottomCommand2AfterTimeout = null;
    private long overlayControlsTimeOut = 0;
    
    private boolean gameOptionsAvailable = true;
    private boolean gameOptionsAvailableOp = false;
    private boolean confirmYesNo = false;
    private boolean confirmOK = false;

    GMenu menuContextOptions = null;
    GMenu menuFreeContextOptions = null;
    
    private GTextWindow OKButton = null;
    private GTextWindow YESButton = null;
    private GTextWindow NOButton = null;
    
    private long trafficCounterReceive;
    //private long trafficByHTTP;
    
    private GTextWindow labelTraffic = null;
    
    private boolean sendLogout = true;
    
    String msgOK_tmp = null;
    //private GMenu currentMenu = null;
    
    private boolean waitingForTrigger;
    
    
    private char[] atrSelltimes     = "units 000".toCharArray();
    private char[] atrSellprice     = "price 000000".toCharArray();
        
    private char[] atrReqSkill      = "req. skill  000".toCharArray();
    private char[] atrReqMagic      = "req. magic  000".toCharArray();
    private char[] atrConsumetimes  = "units left  000".toCharArray();
    private char[] atrAttackrate    = "attackrate  000".toCharArray();
    private char[] atrRange         = "range       000".toCharArray();
    
    
    private char[] atrHealth        = "health     +000.0".toCharArray();
    private char[] atrMana          = "mana       +000.0".toCharArray();
    private char[] atrDamage        = "damage     +000.0".toCharArray();
    private char[] atrAttack        = "attack     +000.0".toCharArray();
    private char[] atrDefense       = "defense    +000.0".toCharArray();
    private char[] atrSkill         = "skill      +000.0".toCharArray();
    private char[] atrMagic         = "magic      +000.0".toCharArray();
    private char[] atrHRegen        = "healthfill +000".toCharArray();
    private char[] atrMRegen        = "manafill   +000".toCharArray();
    
    private char[] atrCHR_Level     = "Level  00".toCharArray();
    private char[] atrCHR_Experience= "XP     0000000".toCharArray();
    private char[] atrCHR_Points    = "Attribute points  000".toCharArray();
    private char[][] classNames     = {"Human".toCharArray(), "Elf".toCharArray(), "Dwarf".toCharArray(), "Orc".toCharArray(), "Wizard".toCharArray()};
    private char[] classString      = "Class ".toCharArray();

    String characterClassName = "Human";

    /*
    private char[] atrCHR_Health    = "healthBase 000.0";
    private char[] atrCHR_Mana      = "manaBase   000.0";
    private char[] atrCHR_Attack    = "attackBase    000.0";
    private char[] atrCHR_Defense   = "defenseBase   000.0";
    private char[] atrCHR_Skill     = "skillBase     000.0";
    private char[] atrCHR_Magic     = "magicBase     000.0";
    private char[] atrCHR_Damage    = "damageBase    000.0";
    */
    
    char[] highscoreText = "1. PLAYERNAME  000000\n\n".toCharArray();
    
    char[] xpInfo = "XP +    ".toCharArray();
    long xpInfoShowDuration;
    
    GTextWindow highScoreWindow = null;
    
    int atrCHR_Modifiers[] = new int[7];
    int characterbuildSelection;
    
    
    private char[] numReplace           = new char[16];
    private char[] numReplaceEmpty      = new char[16];
    
    private char[][] attributeDisplay = null;
    Item atDisplay_Item = null;
    int atDisplay_Height;
    int atDisplay_Width = 89;
    int atDisplay_DescriptionHeight;
    private GTextWindow itemDescriptionWindow = null;


    private Item[] tradeOfferItems = new Item[INVENTORY_ROWS*INVENTORY_COLS];
    int selectedTradeOfferItem = 0;
    
    int experienceCurOffset;
    int experiencePlusForNextLevel;
    
    boolean usingServerPortal;
    
    //
    // Data Storage
    //
    private GDataStore database;
    private GDataStore databaseGfxBack;
    private GDataStore databaseGfxEnemy;
    private GDataStore tempDatabase;
    
    
    //
    // Sound stuff    
    //
    //private GSoundTools     sound;
    
    private int curSoundType = 0;
    private long nextAutoSoundTypeChange = -1;
    
    private GMenu menuSound = null;
    private GTextWindow buttonMusic = null;
    private GTextWindow buttonVolume = null;
    private GWindow gaugeSound;
    private int curSoundVolume = 30;
    private boolean soundPossible = false;
    private boolean soundON = true;

    private GTextWindow creditsWindow = null;
    private byte creditid = 0;
    private GTextWindow label3 = null;
    
    //private GTextWindow labelDebug1 = null;
    //private GTextWindow labelDebug2 = null;
    //private GTextWindow labelDebug3 = null;
    private GTextWindow labelDebug4 = null;
    private GTextWindow labelDebug5 = null;
    private GTextWindow labelDebug6 = null;
    
    private int onlineMinutes = 0;
    private long lastOnlineMinute;
    
    private ByteArrayInputStream[] soundStreams = new ByteArrayInputStream[3];
    
    private GSoundTools soundPlayer;

    private int soundIDs[] = {0,1,2};

    private long blockDuration = 0;
    private int blockTriggerX = 0;
    private int blockTriggerY = 0;

    private ImageManager imageManager = null;

    private byte[] dynamicEnemies_ToLoad = null;
    private int dynamicEnemies_ToLoad_count;
    private int currentDynamicEnemy_ToLoad;
    
    private int currentBackgroundImage_ToLoad = 10;
    private int[] backgroundImages_ToLoad = new int[2];
    
    private boolean isDoubleBuffered = false;
    private boolean talkSubMenu_ShowActionMenu;
    
    private boolean firstTime_SendAddMe = true;
    
    private GTextWindow botphraseWindow = null;
    private GTextWindow[] clientphraseWindows = new GTextWindow[3];
    private int[] botphraseNextIDs = new int[3]; // each clientphrase is associated with one botphrase next id
    private GMenu menuClientphrases = null;
    
    private int dialogueTotalCount;
    private int dialogueCurrent;
    
    GMenu menuQuests = null;
    GList listQuests = null;
    //GTextWindow questNameWindow = null;
    GList questNameWindow = null;
    GTextWindow questDescriptionWindow = null;
    GTextWindow questLocationWindow = null;
    
    private int     nextImageSize;
    private byte[]  nextImage;
    private int     nextImageWalker;
    private String  lastImage;
    
    private boolean bServerList = false;

    private int peacefulDisplay = 0;

    private long[] triggerFlashPhases = {2000, 150, 120, 120, 120, 100};
    private long triggerFlashDuration;
    private int curTriggerFlash = 0;
    
    private int fireAniPhase;
    private long lastFireAniPhase;
    
    private Hashtable           imageCache = new Hashtable();
    private Vector              imageCacheList = new Vector();

    
    private int key;
    
    // various optimizations
    private static final int anchorTopLeft = Graphics.TOP|Graphics.LEFT;
/*#!Series40_MIDP2_0#*///<editor-fold>
    private static Image topBottomBackground = null;
/*$!Series40_MIDP2_0$*///</editor-fold>
/*#Series40_MIDP2_0#*///<editor-fold>
//#     //private static Image playfieldImage;// = Image.createImage(216, 216);
//#     //private static boolean playfieldImageValid = false;
//#     //private static int playfieldImageX, playfieldImageY;
//#     private final static int series40_TOP_INFOHEIGHT = 4;
/*$Series40_MIDP2_0$*///</editor-fold>
    
    // using recordStore for image caching -> reduce traffic to minimum -> YEEHAW
    private static boolean useRSwriting = false, useRSreading = false;
    private static final int MINIMUM_RS = 100000; // rs should have at least 100kByte
    // note setting RSForcedOff

    private boolean bDrawHealthAll;
    private int groundCursorX;
    private int groundCursorY;
    
    private static byte[][] fireWallDisplayOffsets = 
    {
        {14, 6},
        {6, 15},
        {4,4},
        {13,13},
        {10,12}                
    };

    private boolean firstConnect = true;
    
    private boolean justRegistered = false;
    private String registerSuccessMessage = "";

    /*
    private long lastMoveTime;
    private int lastMoveDirection = -1;
    private int extraMovePixels = 0;
    */
    private long lastPlayerAniChange;
    
    private int nextHelpID;
    private int playfieldHelptextID;
    private boolean bAllowHelpReceive = false;

    // whether or not the user has a premium account, never trust this flag, always do the real checks on the server
    // however, it is useful for some calculations / logic paths which are double double checked on the server
    boolean isPremium = false;

    private static String[] chatShortcuts = new String[24];
    private static int nextChatSubstate = SUBSTATE_TALKTOALL;

    private static final String title_belt = "Belt";
    private static final String title_belt_select = "Select Slot";
    private static final String title_belt_use = "Use Item";
    private static final String title_equipment = "Equ.";
    private static final String hint_belt_activate = "Belt Use: #";

    private static final String NOT_IMPLEMENTED = "This feature is not yet unlocked.";

    private static int beltUseReturnState = SUBSTATE_NORMAL;
    private static int triggerOrGroundFindReturnState = SUBSTATE_INVENTORY;

    private static Vector friendsOnline = new Vector();
    private static Vector friendsOffline = new Vector();
    private static Hashtable friendsNames = new Hashtable();

    private Vector characterClasses = new Vector();
    private Vector ownCharacters = new Vector();

    private int numImagesToLoad = 0;

    private int blockTolerance = 3;

    boolean skipNormalMessagePump = false;

    Hashtable conversations = new Hashtable();
    Conversation activeConversation = null;

    // ==========================================================
    // ==========================================================
    // ==========================================================
    // ==========================================================
    // ==========================================================
    // ==========================================================
    
    
    private synchronized void aquireSound(int soundType, int soundID) {
        if(GSoundChecker.checkSound() && soundType < soundIDs.length) {
            if(soundPlayer == null) {    // create GSoundTools object if necessary
                soundPlayer = new GSoundTools();
            }
            
            if (soundIDs[soundType]!=soundID || soundStreams[soundType]==null) {
                InputStream is = null;
                byte b;
                // set new sound id
                soundIDs[soundType] = soundID;
                    try {
                        try {
                            int in;
                            is = (getClass().getResourceAsStream("sound" + soundID + ".mid"));
                            soundStreams[soundType] = null;
                            sb = new StringBuffer();
                            System.gc();
                            while ((in = is.read()) != -1) {
                                sb.append((char)in);
                            }
                            soundStreams[soundType] = new ByteArrayInputStream((sb.toString()).getBytes());
                            soundPlayer.initSound(soundStreams[soundType]);
                        } catch (Exception e) {
                        } finally {
                            if (is != null) {is.close();}
                        }
                    } catch (IOException ioe) {}
                    
                }
        }
    }
    
    private synchronized boolean playbackSound(int curSoundType, long nextAutoSoundTypeChange) {
        if(GSoundChecker.checkSound() && curSoundType < soundIDs.length) {
            // stopPlay the player that is currently playing - if necessary
            //if (curSoundType==this.curSoundType) {
                    //soundPlayer.stopSound(true);
            //} else {
                    //soundStreams[curSoundType].reset();
                    //soundPlayer.initSound((InputStream)soundStreams[curSoundType]);
            //soundPlayer.initSound(soundStreams[0]);
            soundPlayer.startSound(-1, curSoundVolume);
            this.curSoundType = curSoundType;
            this.nextAutoSoundTypeChange = nextAutoSoundTypeChange;
            return true;
        } else {
            soundPossible=false;
            soundON = false;
        }
        
        return false;
    }

    private void setCommand(int type, int buttonIndex, String label) {
        switch (type) {
            case CM_BOTTOM:
                if (buttonIndex == 1) {setBottomCommand1(label);}
                else {setBottomCommand2(label);}
                break;
            case CM_OVERLAY:
                if (buttonIndex == 1) {setOverlayCommand1(label);}
                else {setOverlayCommand2(label);}
                break;
            case CM_OPTION:
                if (buttonIndex == 1) {setOptionCommand1(label);}
                else {setOptionCommand2(label);}
                break;
        }
    }

    /** Set the bottom commands. */
    private void setBottomCommand1(String c1) {
        if (c1 != null) {
            GTools.buttonSetText(comButton1, c1, false);
            comButton1.prepareButtonImage();
            bCommand1 = true;
        } else {
            bCommand1 = false;
        }
    }

    /** Set the bottom commands. */
    private void setBottomCommand2(String c2) {
        if (c2 != null)  {
            GTools.buttonSetText(comButton2, c2, true);
            comButton2.prepareButtonImage();
            /*if (c2.equals("Game")) {
                gameOptionsAvailable = true;
            } else {
                gameOptionsAvailable = false;
            }*/
            gameOptionsAvailable = c2.equals("Game");
            bCommand2 = true;
        } else {
            bCommand2 = false;
            gameOptionsAvailable = false;
        }
    }
    

    /** Set the bottom commands. */
    private void setOverlayCommand1(String c1) {
        if (c1 != null) {
            GTools.buttonSetText(overlayButton1, c1, false);
            overlayButton1.prepareButtonImage();
            ovCommand1 = true;
        } else {
            ovCommand1 = false;
        }
    }

    /** Set the bottom commands. */
    private void setOverlayCommand2(String c2) {
        if (c2 != null)  {
            GTools.buttonSetText(overlayButton2, c2, true);
            overlayButton2.prepareButtonImage();
            ovCommand2 = true;
        } else {
            ovCommand2 = false;
        }
    }
    
    /** Set the bottom commands. */
    private void setOptionCommand1(String c1) {
        if (c1 != null) {
            GTools.buttonSetText(optionButton1, c1, false);
            optionButton1.prepareButtonImage();
            opCommand1 = true;
        } else {
            opCommand1 = false;
        }
    }

    /** Set the bottom commands. */
    private void setOptionCommand2(String c2) {
        if (c2 != null)  {
            GTools.buttonSetText(optionButton2, c2, true);
            optionButton2.prepareButtonImage();
            opCommand2 = true;
            gameOptionsAvailableOp = c2.equals("Game");
        } else {
            opCommand2 = false;
            gameOptionsAvailableOp = false;
        }
    }
    
    
    
    /** Display bottom Commands */
    private void drawBottomCommands() {
        if (bCommand1) {
            GTools.drawWindow(currentGraphics, comButton1, true);
        }
        if (bCommand2) {
            GTools.drawWindow(currentGraphics, comButton2, true);
        }
    }
    
/*#!Series40_MIDP2_0#*///<editor-fold>
    /** Draw bottom background frame. */
    private void drawBottomFrame() {
        //if (items[0]==null || items[1]==null) 
        //    return;
    
        GTools.saveGraphicsSettings(currentGraphics);

        /*for(d4=TOP_INFOHEIGHT + DISPLAYHEIGHT; d4<TOTALHEIGHT; d4+=INGAME_ICON_HEIGHT) {
            if (TOTALHEIGHT - d4 >= INGAME_ICON_HEIGHT) {
                d1=INGAME_ICON_WIDTH;
            } else {
                d1=TOTALHEIGHT-d4;
            }
            //cols
            for (d5=0; d5 < DISPLAYWIDTH; d5+=ITEMWIDTH) {
                if (DISPLAYWIDTH-d5 >= ITEMWIDTH) {
                    d2=INGAME_ICON_WIDTH;
                } else {
                    d2=DISPLAYWIDTH-d5;
                }

                currentGraphics.setClip(d5, d4, d2, d1);
                currentGraphics.drawImage(GlobalResources.imgIngame, d5-INGAME_ICON_WIDTH, d4, anchorTopLeft); // bottom frame
            }
        }*/
        currentGraphics.setClip(0, TOP_INFOHEIGHT+DISPLAYHEIGHT, DISPLAYWIDTH, BOTTOM_INFOHEIGHT);
        currentGraphics.drawImage(topBottomBackground, 0, TOP_INFOHEIGHT+DISPLAYHEIGHT, anchorTopLeft);
        GTools.restoreGraphicsSettings(currentGraphics);
    }
/*$!Series40_MIDP2_0$*///</editor-fold>
    
    

    
    
    ////////////////////////////////////
    // INIT
    ////////////////////////////////////
    
    private long decreasedTime(long oldValue, long resetValue) {
        if (oldValue == -1)
            return oldValue;
        
        if (oldValue > 0) {
            oldValue -= timeDiff;
            if (oldValue < 0) {
                oldValue = resetValue;
            }
        }
        return oldValue;
    }

    /*
    private int checkDisplayIconForObject(Character c, boolean allowDrawHealth) {
        
        if ((currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) && bDrawHealthAll) {
            if (allowDrawHealth) {
                drawHealthManaState(c, curGametime, 4);
            }
        } else {
            bDrawHealthAll = false;
        }
        
        boolean bAllowIconAttack = true;
        
        
        c.attackShowDuration = decreasedTime(c.attackShowDuration, 0);
        
        if (c.icon == ICON_HIT) {
            c.hitDisplayDelay =  decreasedTime(c.hitDisplayDelay, 0);
        } else {
            c.hitDisplayDelay = 0;
        }
        
        c.extraIconShowDuration = decreasedTime(c.extraIconShowDuration, 0);
        
        if (c.hitDisplayDelay <= 0) { c.hitShowDuration = decreasedTime(c.hitShowDuration, 0); }
        
        // ATTACK
        if (c.extraIconShowDuration > 0 || c.extraIconShowDuration == -1) {
            // decrease flashphase time
            c.extraFlashPhaseDuration = decreasedTime(c.extraFlashPhaseDuration, 0);
            if (c.extraFlashPhaseDuration == 0) {
                c.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                c.extraFlashPhase = !c.extraFlashPhase;
            }

            // check extra fight animation for any object but player object
            if (c.attackAnimate > 0  && c.objectId != playerObject.objectId) {
                if (c.attackAnimate == 2) {
                    // switch move ani
                    c.animation = (byte)((c.animation+1) % 2);
                    c.attackAnimate--;
                    if (c.classId == 0) {
                        // for players show also attackBase hit 'stripe'
                        d6 = (c.graphicsDim * DIM)>>1;
                        d7 = c.x + d6 - xPos - 9;
                        d8 = c.y - yPos + 8;
                        bAllowIconAttack = false;
                        currentGraphics.setClip(d7, d8, 18, 5);
                        if (c.direction == DirectionInfo.UP || c.direction == DirectionInfo.LEFT) {
                            currentGraphics.drawImage(GlobalResources.imgIngame, d7-5, d8-29, anchorTopLeft);    // white attackBase slice
                        } else {
                            currentGraphics.drawImage(GlobalResources.imgIngame, d7-23, d8-29, anchorTopLeft); // white attackBase slice
                        }
                    }
                } else if (c.extraIconShowDuration < 1600) {
                    c.animation = (byte)((c.animation+1) % 2);
                    c.attackAnimate--;
                }
            }
            
            
            if (c.extraFlashPhase) {
                if (c.extraicon >= ICON_ATTACK_SPELL1 && bAllowIconAttack) {
                    d6 = (c.graphicsDim * DIM)>>1;
                    d7 = (14 + 9 * (c.extraicon-ICON_ATTACK_SPELL1));
                    d1 = c.x + d6 - xPos - 4;
                    d2 = c.y - yPos - 19  + TOP_INFOHEIGHT;
                    currentGraphics.setClip(d1, d2, 9, 12);
                    currentGraphics.drawImage(GlobalResources.imgIngame, d1-d7, d2-16, anchorTopLeft); // spell visuals at attacker
                } else {
                    switch (c.extraicon) {
                        case SPRITE_ICON_ATTACK:
                            if (bAllowIconAttack) {
                                //d6 = (c.graphicsDim * DIM)/2;
                                d6 = (c.graphicsDim * DIM)>>1;
                                d1 = c.x + d6 - xPos - 4;
                                d2 = c.y - yPos - 12  + TOP_INFOHEIGHT;
                                currentGraphics.setClip(d1, d2, 9, 7);
                                currentGraphics.drawImage(GlobalResources.imgIngame, d1-32, d2-16, anchorTopLeft); // attackBase icon
                            }
                            break;
                    }
               }
            }
            
        }
        
        // DEFEND
        if (c.hitShowDuration > 0 || c.hitShowDuration == -1) {
            // decrease flashphase time
            c.flashPhaseDuration = decreasedTime(c.flashPhaseDuration, 0);
            if (c.flashPhaseDuration == 0) {
                c.flashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                c.flashPhase = !c.flashPhase;
            }
            
            if (c.flashPhase) {
                switch (c.icon) {
                    case ICON_HIT:
                        if (c.hitDisplayDelay <= 0 && !bDrawHealthAll) {
                            drawHealthManaState(c, curGametime, 2);
                            //c.attackShowDuration = 0;
                        }
                        break;
                    case ICON_HITLOCAL:
                    case ICON_BONUSLOCAL:
                        if (c.icon == ICON_BONUSLOCAL) {
                            currentGraphics.setColor(0,255,0);
                        } else {
                            currentGraphics.setColor(255,0,0);
                        }
                        d1 = c.x - xPos - 1;
                        d2 = c.y - yPos - 1 + TOP_INFOHEIGHT;
                        d6 = (c.graphicsDim * DIM)+1;
                        currentGraphics.setClip(d1, d2, d6+1, d6+1);
                        currentGraphics.drawRect(d1, d2, d6, d6);

                        break;
                    case SPRITE_ICON_DEFEND:
                        if (!(c.extraIconShowDuration > 0 && c.extraicon==SPRITE_ICON_ATTACK)) {
                            //d6 = (c.graphicsDim * DIM)/2;
                            d6 = (c.graphicsDim * DIM)>>1;
                            d1 = c.x + d6 - xPos - 4;
                            d2 = c.y - yPos - 12  + TOP_INFOHEIGHT;
                            currentGraphics.setClip(d1, d2, 9, 7);
                            currentGraphics.drawImage(GlobalResources.imgIngame, d1-32, d2, anchorTopLeft);    // defend icon
                            //c.attackShowDuration = 0;
                        }
                        break;
                    case ICON_DEAD:
                        //d6 = (c.graphicsDim * DIM)/2;
                        d6 = (c.graphicsDim * DIM)>>1;
                        d1 = c.x + d6 - xPos - 5;
                        d2 = c.y + d6 - yPos - 6  + TOP_INFOHEIGHT;
                        currentGraphics.setClip(d1, d2, 10, 12);
                        currentGraphics.drawImage(GlobalResources.imgIngame, d1-41, d2, anchorTopLeft);    // death icon
                        c.attackShowDuration = 0;
                        return -1;
                }
            } else if (c.icon==ICON_HIT){
                //c.attackShowDuration = 0;
                if (c.hitDisplayDelay <= 0) {
                    return -1;
                }
            }
        }  else if (c.icon==ICON_DEAD) {
            c.attackShowDuration = 0;
            return -1;
        }
        return 1;
    }
    */
    
    
    private void drawAttackAnimation(Character c) {
        //
        
        currentGraphics.setColor(255, 32, 0);
        d1 = c.x - xPos + ((c.graphicsDim*DIM)>>1);
        d2 = c.y - yPos + TOP_INFOHEIGHT + ((c.graphicsDim*DIM)>>1);
        
        d3 = playerObject.x - xPos + ((playerObject.graphicsDim*DIM)>>1);
        d4 = playerObject.y - yPos + TOP_INFOHEIGHT + ((playerObject.graphicsDim*DIM)>>1);
        
        // get endpoints of a line directly connecting the object with the player
        
/*#Series40_MIDP2_0#*///<editor-fold>
//#         currentGraphics.setClip(0, 0, DISPLAYWIDTH, DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT);
/*$Series40_MIDP2_0$*///</editor-fold>
/*#!Series40_MIDP2_0#*///<editor-fold>
        currentGraphics.setClip(0, 0, DISPLAYWIDTH, DISPLAYHEIGHT + TOP_INFOHEIGHT);
/*$!Series40_MIDP2_0$*///</editor-fold>
        //currentGraphics.drawLine(d1, d2, d3, d4);
        
        // xDistance
        d9 = d1 - d3;
        // yDistance
        d10 = d2 - d4;
        
        long indicator = (long)((c.attackShowDuration*MAX_ATTACKSHOWDURATION) / MAX_ATTACKSHOWDURATION);
        
        d11 = (int)((indicator * d9) / MAX_ATTACKSHOWDURATION);
        d12 = (int)((indicator * d10) / MAX_ATTACKSHOWDURATION);

        d11 = d1 - d11;
        d12 = d2 - d12;
        
        currentGraphics.fillRect(d11-1, d12-1, 3, 3);
        currentGraphics.setColor(255, 255, 255);
        currentGraphics.fillRect(d11, d12, 1, 1);
        
        d5 = playerObject.x - xPos;
        d6 = playerObject.y - yPos + 8;
        
        if (playerObject.attackAnimate > 0) {
            if (playerObject.attackAnimate == 2) {
                playerAnim = (byte)((playerAnim+1) % 2);
                playerObject.attackAnimate--;
                
                currentGraphics.setClip(d5, d6, 18, 5);
                if (playerDirection == 0 || playerDirection == 3) {
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5-5, d6-29, anchorTopLeft);    // white attackBase flash
                } else {
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5-23, d6-29, anchorTopLeft); // white attackBase flash
                }
                
            } else if (c.attackShowDuration < 400) {
                playerAnim = (byte)((playerAnim+1) % 2);
                playerObject.attackAnimate--;
            }
        }
    }
    
    
    private void readjustPlayerMsg(WorldObject fwgo) {
        if (fwgo==null || fwgo.msgText == null) {
            return;
        }
        
        k = 0; // lineFill
        m = -1; //lastSpaceInLine
        for(n=0; n<fwgo.msgText.length; n++) {
            k++; //lineFill++
            if (fwgo.msgText[n]==' ') {
                m = k; // remember last space in current line (measured in line fill, starting from 1)
            }
            if (k==16 || fwgo.msgText[n]=='\n') {    // end of line encountered
                if (   fwgo.msgText[n]!=' ' && fwgo.msgText[n]!='\n' && fwgo.msgText.length > n+1 
                    && fwgo.msgText[n+1]!= ' ' && fwgo.msgText[n+1]!= '\n' && m > 0) { // word would be wrapped inside, and space exists in line
                    k = k - m; // lineFill = lineFill - lastSpaceInLine -> transferred to next line
                    fwgo.msgText[n - k] = '\n';  // force linebreak at the last remembered space position
                } else {
                    k = 0; // normal line break: reset linefill
                }
                m = -1; // reset lastSpaceInLine
            }
        }
    }
    
    private void checkDisplayTextForObject(WorldObject fwgo, boolean displayName) {
        if (fwgo==null || fwgo.msgText == null) {
            return;
        }

        if (fwgo.msgShowDuration <= 0 || fwgo.msgText.length == 0) {
            fwgo.msgText = null;
            return;
        }
        //subtract elapsed time since last frame
        fwgo.msgShowDuration -= timeDiff;

        //d = fwgo.msgText.length;
        d12 = fwgo.msgText.length;;
        
        /*
        if (d > 16) {
            d = 16;
        }*/

        d10 = 0; // current linefill
        d9 = 0; // maximum line length
        // find the maximum line length for this text to allow decent centering
        for(d=0; d<d12; d++) {
            d10++;
            if (d > 0 &&  (d10==17 || fwgo.msgText[d-1]=='\n')) {  //line break
                if (d10 > d9) {
                    d9 = d10;
                }
                d10 = 0; // reset linefill
            }
        }
        if (d9 == 0) {
            d9 = d12;
        }
        
        d11 = (fwgo.graphicsDim * DIM) >> 1;
        
        //get rel. position to upper left screen corner
        d1 = (fwgo.x - xPos) + d11;
        d2 = (fwgo.y - yPos) + d11 + TOP_INFOHEIGHT;

        //d5 = fwgo.y - (2 * font.charHeight);    //y pos of text
        d5 = fwgo.y - (font.charHeight<<1);    //y pos of text

        //fwgo.msgY = fwgo.y - (2 * font.charHeight);
        //fwgo.msgY = (fwgo.y - yPos) + TILEHEIGHT/2 + TOP_INFOHEIGHT;

        if (d1 < 0  || d2 < TOP_INFOHEIGHT || d1 > DISPLAYWIDTH || d2 > TOTALHEIGHT || d5 < 8) {
            return; //object not visible to at least half of its extends
        }
        d3 = (d9*font.charWidth)>>1;   //half width in pixels of the text
        d4 = fwgo.x + (d11) - d3;     //x pos of text

        //fwgo.msgX = fwgo.x + (PLAYERWIDTH_HALF) - d3;     //x pos of text

        //adjust textpos so msg is fully visible
        d7 = d1 + d3;  //text endpos
        if (d7 > DISPLAYWIDTH)  { //right overlap
            d4 -= (d7-DISPLAYWIDTH);
        //} else  if(d7 < 2*d3) { //left overlap
            //d4 += (2*d3)-d7;
        } else  if(d7 < d3<<1) { //left overlap
            d4 += (d3<<1)-d7;
        }

        
        //display the message
        d6 = (d5 - yPos) + TOP_INFOHEIGHT;
        d7 =(d4 - xPos);
        if (displayName) {
            /*if (d > 0) {*/
                font.drawString(currentGraphics, fwgo.name + ":", d7, d6-font.charHeight-3);
            /*} else {
                font.drawString(currentGraphics, fwgo.name, d7, d6-font.charHeight-3);
            }*/
        }

        
        d10 = 0; // current linefill
        for(d=0; d<d12; d++) {
            d10++;
            if (d > 0 &&  (d10==17 || fwgo.msgText[d-1]=='\n')) {  //line break
                d6 += font.charHeight;
                d7=d4 - xPos;   // reset xPos
                d10 = 0; // reset linefill
            }
            //only draw if part of char is visible
/*#Series40_MIDP2_0#*///<editor-fold>
//#             if (d6 > TOP_INFOHEIGHT - font.charHeight && d6 < TOP_INFOHEIGHT + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT + font.charHeight && 
/*$Series40_MIDP2_0$*///</editor-fold>
/*#!Series40_MIDP2_0#*///<editor-fold>
            if (d6 > TOP_INFOHEIGHT - font.charHeight && d6 < TOP_INFOHEIGHT + DISPLAYHEIGHT + font.charHeight && 
/*$!Series40_MIDP2_0$*///</editor-fold>
                d7 > -font.charWidth &&  d7 < DISPLAYWIDTH + font.charWidth) 
            {
                    font.drawChar(currentGraphics, fwgo.msgText[d], d7, d6);
            }
            d7+=font.charWidth;
        }
    }
    
    
    private void checkSpellVisualsForCharacter(Character c) {

        
        if (c.spellVisualsEndTime1  > curGametime) {
            // raise / reduce attribute, draw + / - symbol
            d9 = 0;

            d6 = c.x - xPos + ((c.graphicsDim*DIM) >> 1) - 4; // symbol xPos
            d7 = c.y - yPos - 11 + TOP_INFOHEIGHT;// symbol yPos

            d8 = (c.spellVisualsColorType1) * 7;

            currentGraphics.setClip(d6, d7, 7, 5);
            currentGraphics.drawImage(GlobalResources.imgIngame, d6-0-d8, d7-67, anchorTopLeft); // spell symbol for attribute raise / reduce

        }
        
        if (c.spellVisualsEndTime  > curGametime) {

            d3 = 16 + (c.spellVisualsColorType * 8);    // yPos in ingame graphic
            d4 = d3 + 3;

            d1 = (c.x - xPos);
            d2 = (c.y - yPos) + TOP_INFOHEIGHT;


            d6 = c.graphicsDim*DIM; // width / height of character
            d9 = c.graphicsDim -1;   // number of magicBase ani positions

            if (d9 > 1) {
                // initial position for spell visuals
                d10 = (((d6) >> 1) - (((d9 >> 1)- c.magicAniPhase)*DIM) + c.magicAniPos - 2);    // x Pos
                d11 = (d9>>2)*DIM + c.magicAniPhase * 2 - c.magicAniPos + 2;     // y Pos: bottom of character

                if (c.magicAniPhase%2 == 0) {
                    d5 = d1 + d10;
                    d8 = d2 + d11;
                    // smaller animation frame / star
                    currentGraphics.setClip(d5, d8, 3, 3);
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5, d8 - d3, anchorTopLeft);  // magicBase ani target

                    // new
                    currentGraphics.setClip(d5-2, d8 + 8, 3, 3);
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5 - 2, d8 + 8 - d3, anchorTopLeft);  // magicBase ani target

                    d5 = d1 + d6 - d10 - 4;
                    d8 = d2 + d6 - 4;
                    currentGraphics.setClip(d5, d8, 5, 5);
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5, d8 - d4, anchorTopLeft);  // magicBase ani target

                    // new
                    currentGraphics.setClip(d5 - 6 , d8 - 5 , 3, 3);
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5 - 6, d8 - 5 - d3, anchorTopLeft);  // magicBase ani target


                } else {
                    d5 = d1 + d10;
                    d8 = d2 + d11;
                    // bigger animation frame / star
                    currentGraphics.setClip(d5 - 3, d8 + 1, 5, 5);
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5 - 3, d8 + 1 - d4, anchorTopLeft);  // magicBase ani target

                    currentGraphics.setClip(d5 + 3, d8 - 4, 3, 3);
                    currentGraphics.drawImage(GlobalResources.imgIngame, d5 + 3,  d8 - 4 - d3, anchorTopLeft); // magicBase ani target
                }
            }
            
            if (curGametime - c.magicAniLastCycle > 160) {
                c.magicAniLastCycle = curGametime;

                    c.magicAniPhase++; 
                    if (c.magicAniPhase==3) {
                        c.magicAniPhase = 0;
                        c.magicAniPos = (byte)((c.magicAniPos+1)%d9);
                    }
            }

        }



            /*
            for (d12=0; d12<d9; d12++) {
                currentGraphics.setClip(playerScreenX + d10, playerScreenY + d11, 3, 3);
                currentGraphics.drawImage(GlobalResources.imgIngame, playerScreenX + d10 - 16, playerScreenY + d11 - 16, anchorTopLeft);
                d10 += DIM;
            }
             */
    }
    
    
    
    
    private void drawOtherCharacter(Character character, boolean allowHealthDisplay) {
        if (character.graphicsel>-1) {
            if (enemies[character.graphicsel]== null) {
                return;
            }
        }
        
        // do not draw own player
        if(character!=null && character.objectId!= character_DB_ID) {
            if (/*checkDisplayIconForObject(character, allowHealthDisplay)!=-1*/ true) {
                xStart = character.x - xPos;
                yStart = character.y - yPos + TOP_INFOHEIGHT;

                d4 = character.graphicsDim*DIM;
                
                    // only draw visible players
                    //if(xStart + d4 >= 0 && yStart + d4 >= TOP_INFOHEIGHT) {
/*#Series40_MIDP2_0#*///<editor-fold>
//#                 if(xStart + d4 >= 0 && yStart + d4 >= TOP_INFOHEIGHT && xStart < DISPLAYWIDTH && yStart < DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT) {    
/*$Series40_MIDP2_0$*///</editor-fold>
/*#!Series40_MIDP2_0#*///<editor-fold>
                    if(xStart + d4 >= 0 && yStart + d4 >= TOP_INFOHEIGHT && xStart < DISPLAYWIDTH && yStart < DISPLAYHEIGHT + TOP_INFOHEIGHT) {    
/*$!Series40_MIDP2_0$*///</editor-fold>
                        xEnd = d4;
                        if(DISPLAYWIDTH - xStart < d4)
                            xEnd = DISPLAYWIDTH - xStart;
                        yEnd = d4;
/*#Series40_MIDP2_0#*///<editor-fold>
//#                             if(DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT - yStart < d4)
//#                                 yEnd = DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT - yStart;
/*$Series40_MIDP2_0$*///</editor-fold>
/*#!Series40_MIDP2_0#*///<editor-fold>
                        if(DISPLAYHEIGHT + TOP_INFOHEIGHT - yStart < d4)
                            yEnd = DISPLAYHEIGHT + TOP_INFOHEIGHT - yStart;
/*$!Series40_MIDP2_0$*///</editor-fold>
                        xCounter = xStart;
                        if(xCounter < 0) {
                            xEnd += xStart;
                            xCounter = 0;
                        }
                        yCounter = yStart;
                        if(yCounter < 0) {
                            yEnd += yStart;
                            yCounter = 0;
                        }

                        currentGraphics.setClip(xCounter, yCounter, xEnd, yEnd);
                        //currentGraphics.setColor(255,0,0);
                        //currentGraphics.drawRect(xCounter, yCounter, xEnd, yEnd);
                        //currentGraphics.drawImage(players[fwgoTmp.subclassId], xStart - (PLAYERWIDTH*fwgoTmp.animation), yStart - (TILEHEIGHT*fwgoTmp.direction), anchorTopLeft);
                        if(character.graphicsel > -1) {
                            currentGraphics.drawImage(enemies[character.graphicsel], xStart - (character.graphicsX*DIM) - (2*d4*character.direction) - (d4*0 /*character.animation*/),
                                                           yStart - (character.graphicsY*DIM),
                                                           anchorTopLeft);
                        } else {
                            currentGraphics.drawImage(players, xStart - (character.graphicsX*DIM) - (2*d4*character.direction) - (d4*0 /*character.animation*/),
                                                           yStart - (character.graphicsY*DIM),
                                                           anchorTopLeft);
                        }

                        
                        if (character.classId == 0 && !character.previouslyInRange) {
                            d5 = d4 >> 1;
                            if (xEnd > d5 && yEnd > d5 && yStart > TOP_INFOHEIGHT + 7) {
                                character.previouslyInRange = true;
                                character.msgText = character.name.toCharArray();
                                character.msgDisplayName = false;
                                character.msgShowDuration = 4000;
                                readjustPlayerMsg(character);
                            }
                        }
                    } else {
                        // character not visible
                        character.previouslyInRange = false;
                    }
            }
        }
    }

    /*
    private void checkDisplayDeadCharacters() {
        
        for (d11=0; d11<deadBodyCount; d11++) {
            characterTmpD = deadCharacters[d11];
            drawOtherCharacter(characterTmpD, false);
            if (characterTmpD.hitShowDuration <= 0) {
                if (characterTmpD.icon == ICON_HIT) {
                    characterTmpD.hitShowDuration = MAX_DEADSHOWDURATION;
                    characterTmpD.icon = ICON_DEAD;    // display character as dead
                    characterTmpD.flashPhaseDuration = -1;
                    characterTmpD.flashPhase = true;
                } else {    // Remove dead body from array -> shift
                    for (d12=d11; d12<deadBodyCount-1; d12++) {
                        deadCharacters[d12] = deadCharacters[d12+1];
                    }
                    // deadBodyCount is at least 1 at this point
                    deadCharacters[deadBodyCount-1] = null; // make sure no body is saved twice
                    deadBodyCount--;    // one dead body less
                    d11--;   // do not skip bodies
                }
            }
        }
        characterTmpD = null;
    }*/
    
/*#!Series40_MIDP2_0#*///<editor-fold>
    private void prepareTopBottomBackground() {
        if(topBottomBackground == null) {
            topBottomBackground = Image.createImage(DISPLAYWIDTH, BOTTOM_INFOHEIGHT);
            Graphics g = topBottomBackground.getGraphics();

            //rows
            for(d4=0; d4<BOTTOM_INFOHEIGHT; d4+=INGAME_ICON_HEIGHT) {
                if (BOTTOM_INFOHEIGHT-d4 >= INGAME_ICON_HEIGHT) {
                    d1=INGAME_ICON_HEIGHT;
                } else {
                    d1=BOTTOM_INFOHEIGHT-d4;
                }
                //cols
                for (d5=0; d5 < DISPLAYWIDTH; d5+=INGAME_ICON_WIDTH) {
                    if (DISPLAYWIDTH-d5 >= INGAME_ICON_WIDTH) {
                        d2=INGAME_ICON_WIDTH;
                    } else {
                        d2=DISPLAYWIDTH-d5;
                    }

                    g.setClip(d5, d4, d2, d1);
                    g.drawImage(GlobalResources.imgIngame, d5-INGAME_ICON_WIDTH, d4, anchorTopLeft);   // bottom bg
                }
            }
        }
    }
/*$!Series40_MIDP2_0$*///</editor-fold>
    
//#if Series40_MIDP2_0
//#     /*private void preparePlayfieldImage() {
//#         if(playfieldImage != null) {
//#             Graphics g = playfieldImage.getGraphics();
//#             
//#             //adjust playfieldImage_XY
//#             playfieldImageX = xPos/TILEWIDTH - 1;
//#             if(playfieldImageX<=0) {
//#                 playfieldImageX = 0;
//#             } else if(playfieldImageX > playfieldWidth-9) {
//#                 playfieldImageX = playfieldWidth-9;
//#             }
//#             playfieldImageY = yPos/TILEHEIGHT - 1;
//#             if(playfieldImageY<=0) {
//#                 playfieldImageY = 0;
//#             } else if(playfieldImageY > playfieldHeight-9) {
//#                 playfieldImageY = playfieldHeight-9;
//#             }
//#             
//#             int x=0, y=0, gfxid=0;
//#             for(int i=playfieldImageX; i<playfieldImageX+9; i++) {
//#                 y = 0;
//#                 for(int j=playfieldImageY; j<playfieldImageY+9; j++) {
//#                     g.setClip(x, y, TILEWIDTH, TILEHEIGHT);
//#                     gfxid = (legacyPlayfield[i][j] & 15); //extract graphic id
//#                     if((legacyPlayfield[i][j] & 128)==128 && dynamic != null) {
//#                         g.drawImage(dynamic, x - TILEWIDTH*(gfxid%TILES), y - TILEHEIGHT*(gfxid/TILES), anchorTopLeft);
//#                     } else {
//#                         g.drawImage(background, x - TILEWIDTH*(gfxid%TILES), y - TILEHEIGHT*(gfxid/TILES), anchorTopLeft);
//#                     }
//#                     y += TILEHEIGHT;
//#                 }
//#                 x += TILEWIDTH;
//#             }
//#             playfieldImageX*=TILEWIDTH;
//#             playfieldImageY*=TILEHEIGHT;
//#             playfieldImageValid = true;
//#         }
//#     }*/
//#endif
    
//#if !(Series40_MIDP2_0)
    private void drawTopFrame() {
        //if (items[0]==null || items[1]==null) 
        //    return;
    
        GTools.saveGraphicsSettings(currentGraphics);
        /*//TOP
        //rows
        for(d4=0; d4<TOP_INFOHEIGHT-4; d4+=ITEMHEIGHT) {
            if (TOP_INFOHEIGHT-4-d4 >= ITEMHEIGHT) {
                d1=ITEMHEIGHT;
            } else {
                d1=TOP_INFOHEIGHT-4-d4;
            }
            //cols
            for (d5=0; d5 < DISPLAYWIDTH; d5+=ITEMWIDTH) {
                if (DISPLAYWIDTH-d5 >= ITEMWIDTH) {
                    d2=ITEMWIDTH;
                } else {
                    d2=DISPLAYWIDTH-d5;
                }

                currentGraphics.setClip(d5, d4, d2, d1);
                currentGraphics.drawImage(GlobalResources.imgIngame, d5-INGAME_ICON_WIDTH, d4, anchorTopLeft); // bottom bg
            }
        }*/
        currentGraphics.setClip(0, 0, DISPLAYWIDTH, BOTTOM_INFOHEIGHT);
        currentGraphics.drawImage(topBottomBackground, 0, 0, anchorTopLeft);
        GTools.restoreGraphicsSettings(currentGraphics);
    }
//#endif


    /**
     * Draw Action menu at the bottom of display.
     */
    private void drawActionMenu() {
        if (menu!=null && GlobalResources.imgIngame!=null) {
            d10 = (TOP_INFOHEIGHT + DISPLAYHEIGHT - 26);
            d11 = 0; // x-Offset for big action menu
            if((selectedActionMenuEntry+1)*25 > DISPLAYWIDTH) {
                d12  = (((selectedActionMenuEntry+1)*25-DISPLAYWIDTH)/25+1)*25;
                d11 -= d12;
            }
            //for (d9=0; d9<ACTION_ITEMS; d9++) {
            for (d9=ACTION_ITEMS; --d9>=0; ) {
                d8 = 25*d9+d11;
                d6 = d8;
                // draw background
                currentGraphics.setClip(d8, d10, 25, 26);
                if (d9==selectedActionMenuEntry) {
                    db1 = true;
                    currentGraphics.drawImage(GlobalResources.imgIngame, d8-25, d10-34, anchorTopLeft); // selected    action menu
                    d8+=4;
                    d7 = d10+4;
                } else {
                    db1 = false;
                    currentGraphics.drawImage(GlobalResources.imgIngame, d8, d10-34, anchorTopLeft); // not selected   action menu
                    d8+=3;
                    d7 = d10+3;
                }
                
                // flash chat menu / friend menu symbols if necessary
                // 3 = Chat Menu, 6 = Friend Menu
                db2 = !db1 && ((d9==3 && getNumUpdatedConversations() > 0) /* || (d9==6 && friendRequestList.entries.size() > 0)*/);
                
                // db2 indicates whether the current symbol should be highlighted (true) or not (false)
                if (db2) {    
                    currentGraphics.setColor(0xFFCC00);
                    currentGraphics.drawRect(d6, d10, 24, 25);
                }

                
                // draw menu icon
                currentGraphics.setClip(d8, d7, 19, 19);
                currentGraphics.drawImage(menu, d8 - (d9*19), d7, anchorTopLeft); // selected
                
            }
            d12 = d10+10;
            if(d11 != 0 && flash) {
                currentGraphics.setClip(0, d12, 5, 7);
                currentGraphics.drawImage(GlobalResources.imgIngame, -14, d12-67, anchorTopLeft);  // orange arrow left
            }
            if(ACTION_MENU_SIZE+d11 > DISPLAYWIDTH && flash) {
                currentGraphics.setClip(DISPLAYWIDTH-5, d12, 5, 7);
                currentGraphics.drawImage(GlobalResources.imgIngame, DISPLAYWIDTH-23, d12-67, anchorTopLeft);  // orange arrow right
            }
        }
    }
    
    
    
    /**
     * Set the attributes of the character for the character build screen.
     */
    private void prepareCharacterBuildScreen() {
        if (playerObject!=null) {
            replaceNumberLeftAlign(atrCHR_Level, playerObject.level, 7, 8, false);
            replaceNumberLeftAlign(atrCHR_Experience, playerObject.experience, 7, 13, false);
            replaceNumberLeftAlign(atrCHR_Points, playerObject.levelpoints, 18, 20, false);
            
            replaceNumberLeftAlign(atrHealth, maxhealth, 12, 16, true); atrHealth[11] = ' ';
            replaceNumberLeftAlign(atrMana, maxmana, 12, 16, true); atrMana[11] = ' ';
            replaceNumberLeftAlign(atrDamage, damage, 12, 16, true);  atrDamage[11] = ' ';
            replaceNumberLeftAlign(atrAttack, attack, 12, 16, true); atrAttack[11] = ' ';
            replaceNumberLeftAlign(atrDefense, defense, 12, 16, true);  atrDefense[11] = ' ';
            replaceNumberLeftAlign(atrSkill, skill, 12, 16, true);  atrSkill[11] = ' ';
            replaceNumberLeftAlign(atrMagic, magic, 12, 16, true);  atrMagic[11] = ' ';
        }
    }
    
    
    /**
     * Draw the character build screen that allows the player to distribute 
     * attributepoints on the attributes of the character.
     */
    private synchronized void drawCharacterBuildScreen() {
        if (playerObject==null) {
            return;
        }
        
        d4 = TOP_INFOHEIGHT;
        
        // background
//#if Series40_MIDP2_0
//#         currentGraphics.setClip(0, d4, 128, 112);
//#         currentGraphics.setColor(0, 0, 80);
//#         currentGraphics.fillRect(0, d4, 128, 112);
//#         currentGraphics.setColor(255, 204, 0);
//#         currentGraphics.drawRect(0, d4, 127, 111);
//#         d4 += 4;
//#else
        currentGraphics.setClip(0, d4, 128, 124);
        currentGraphics.setColor(0, 0, 80);
        currentGraphics.fillRect(0, d4, 128, 124);
        currentGraphics.setColor(255, 204, 0);
        currentGraphics.drawRect(0, d4, 127, 123);
        d4 += 5;
//#endif

        // name
        font.drawString(currentGraphics, playerObject.name, 3, d4);
        d4 += 11;

        // character image
        currentGraphics.setClip(7, d4, PLAYERWIDTH, PLAYERHEIGHT);
        //currentGraphics.drawImage(players, 7 - (playerObject.graphicsX*DIM) - (4*PLAYERWIDTH),  d4 - (playerObject.graphicsY*DIM), anchorTopLeft);
        currentGraphics.drawImage(players, 7 - (playerObject.graphicsX*DIM) - (PLAYERWIDTH<<2),  d4 - (playerObject.graphicsY*DIM), anchorTopLeft);
        
        
        // class name
        font.drawString(currentGraphics, classString, 33, d4);
        font.drawString(currentGraphics, characterClassName, 68, d4);
        d4 += 9;
        
        // level
        font.drawString(currentGraphics, atrCHR_Level, 33, d4);
        d4 += 9;
        
        // experience
        font.drawString(currentGraphics, atrCHR_Experience, 33, d4);
        d4 += 11;

        // attributepoints
        font.drawString(currentGraphics, atrCHR_Points, 3, d4);
//#if Series40_MIDP2_0
//#         d4 += 11;
//#else
        d4 += 15;
//#endif

        // separation line
        currentGraphics.setColor(80, 80, 80);
//#if Series40_MIDP2_0
//#         currentGraphics.setClip(3, d4-3, 122, 1);
//#         currentGraphics.drawLine(2, d4-3, 126, d4-3);
//#else
        currentGraphics.setClip(3, d4-5, 122, 1);
        currentGraphics.drawLine(2, d4-5, 126, d4-5);
//#endif

        
//#if Series40_MIDP2_0
//#         d5 = d4 + (8*characterbuildSelection)-2;
//#else
        d5 = d4 + (9*characterbuildSelection)-2;
//#endif
        
        // draw attribute selection
        currentGraphics.setColor(0, 80, 160);
        currentGraphics.setClip(3, d5, 122, 9);
        currentGraphics.fillRect(3, d5, 122, 9);
        
        if (playerObject.levelpoints > 0) {
            // draw plus sign
            currentGraphics.setClip(92, d5, 9, 9);
            currentGraphics.setColor(0, 160, 0);
            currentGraphics.fillRect(92, d5, 9, 9);
            currentGraphics.setColor(208, 208, 208);
            currentGraphics.drawRect(92, d5, 8, 8);
            font.drawChar(currentGraphics, '+', 95, d5 + 2);
            font.drawChar(currentGraphics, (char)((atrCHR_Modifiers[characterbuildSelection]/10) + '0'), 105, d5 + 2);
            font.drawChar(currentGraphics, '.', 110, d5 + 2);
            font.drawChar(currentGraphics, (char)((atrCHR_Modifiers[characterbuildSelection]%10) + '0'), 115, d5 + 2);
        }
        
        // healthBase
        font.drawString(currentGraphics, atrHealth, 3, d4);
//#if Series40_MIDP2_0
//#         d4 += 8;
//#else
        d4 += 9;
//#endif
        // manaBase
        font.drawString(currentGraphics, atrMana, 3, d4);
//#if Series40_MIDP2_0
//#         d4 += 8;
//#else
        d4 += 9;
//#endif
        // damageBase
        font.drawString(currentGraphics, atrDamage, 3, d4);
//#if Series40_MIDP2_0
//#         d4 += 8;
//#else
        d4 += 9;
//#endif
        // attackBase
        font.drawString(currentGraphics, atrAttack, 3, d4);
//#if Series40_MIDP2_0
//#         d4 += 8;
//#else
        d4 += 9;
//#endif
        // defenseBase
        font.drawString(currentGraphics, atrDefense, 3, d4);
//#if Series40_MIDP2_0
//#         d4 += 8;
//#else
        d4 += 9;
//#endif
        // skillBase
        font.drawString(currentGraphics, atrSkill, 3, d4);
//#if Series40_MIDP2_0
//#         d4 += 8;
//#else
        d4 += 9;
//#endif
        // magicBase
        font.drawString(currentGraphics, atrMagic, 3, d4);
    }
    
    
    
    /**
     * Set the Attribute values of an item for display.
     * @param Item The item to set the values of
     */
    /*
    private void setAttributeValuesDisplay(Item it, boolean inventory) {
        if (it==null) {
            return;
        }

        atDisplay_Height = 0;
        db1 = false;
        
        // reset attributeDisplay array
        for (k4=attributeDisplay.length; --k4>=0; ) {
            attributeDisplay[k4] = null;
        }

        // set the item description
        GTools.textWindowSetText(itemDescriptionWindow, it.description);
        
        // adjust description displayheight
        if (itemDescriptionWindow.noOfExistingLines > 1) {
            atDisplay_DescriptionHeight = 30;
        } else {
            atDisplay_DescriptionHeight = 24;
        }
        
        // required skillBase
        if (it.requiredSkill > 0) {
            replaceNumberLeftAlign(atrReqSkill, it.requiredSkill, 12, 14, false);
            attributeDisplay[3] = atrReqSkill;
            atDisplay_Height += 8;
            db1 = true;
        }
        
        // required magicBase
        if (it.requiredMagic > 0) {
            replaceNumberLeftAlign(atrReqMagic, it.requiredMagic, 12, 14, false);
            attributeDisplay[4] = atrReqMagic;
            atDisplay_Height += 8;
            db1 = true;
        }

        // attackrate
        if (it.frequency>0) {
            replaceNumberLeftAlign(atrAttackrate, it.frequency, 12, 14, false);
            attributeDisplay[5] = atrAttackrate;
            atDisplay_Height += 8;
            db1 = true;
        }

        // range
        if (it.data>0 && it.classId >1 && it.classId < 4) {
            replaceNumberLeftAlign(atrRange, it.data-GlobalSettings.DEFAULT_WEAPON_RANGE, 12, 14, false);
            attributeDisplay[6] = atrRange;
            atDisplay_Height += 8;
            db1 = true;
        }
        
        k4 = 7;
        if (it.units==-2) {  // ITEM FOR EQUIPMENT
            // healthBase
            if (it.healthBase!=0) {
                setSingleAttributeDisplay(it.healthBase, atrHealth, k4);
                k4++;
                atDisplay_Height += 8;
            }
            //manaBase
            if (it.manaBase!=0) {
                setSingleAttributeDisplay(it.manaBase, atrMana, k4);
                k4++;
                atDisplay_Height += 8;
            }

            //damageBase
            if (it.damageBase!=0) {
                setSingleAttributeDisplay(it.damageBase, atrDamage, k4);
                k4++;
                atDisplay_Height += 8;
            }
            
            //attackBase
            if (it.attackBase!=0) {
                setSingleAttributeDisplay(it.attackBase, atrAttack, k4);
                k4++;
                atDisplay_Height += 8;
            }
            //defenseBase
            if (it.defenseBase!=0) {
                setSingleAttributeDisplay(it.defenseBase, atrDefense, k4);
                k4++;
                atDisplay_Height += 8;
            }
            //skillBase
            if (it.skillBase!=0) {
                setSingleAttributeDisplay(it.skillBase, atrSkill, k4);
                k4++;
                atDisplay_Height += 8;
            }
            //magicBase
            if (it.magicBase!=0) {
                setSingleAttributeDisplay(it.magicBase, atrMagic, k4);
                k4++;
                atDisplay_Height += 8;
            }
            
            //healthregenerateBase
            if (it.healthregenerateBase!=0) {
                if (it.healthregenerateBase>0) {
                    atrHRegen[11] = '+';
                } else {
                    atrHRegen[11] = '-';
                }
                replaceNumberLeftAlign(atrHRegen, it.healthregenerateBase, 12, 14, false);
                attributeDisplay[k4++] = atrHRegen;
                atDisplay_Height += 8;
            }
            //manaregenerateBase
            if (it.manaregenerateBase!=0) {
                if (it.manaregenerateBase>0) {
                    atrMRegen[11] = '+';
                } else {
                    atrMRegen[11] = '-';
                }
                replaceNumberLeftAlign(atrMRegen, it.manaregenerateBase, 12, 14, false);
                attributeDisplay[k4++] = atrMRegen;
                atDisplay_Height += 8;
            }
            
        } else if (it.units>0 || it.units==-1) { // USABLE ITEM!
            if (inventory) {
                // units
                if (it.units>0) {
                    replaceNumberLeftAlign(atrConsumetimes, it.units, 12, 14, false);
                    attributeDisplay[2] = atrConsumetimes;
                    atDisplay_Height += 8;
                    db1 = true;
                }
            }
            // curHealth
            if (it.healthBase!=0) {
                setSingleAttributeDisplay(it.healthBase, atrHealth, k4);
                k4++;
                atDisplay_Height += 8;
            }
            //curMana
            if (it.manaBase!=0) {
                setSingleAttributeDisplay(it.manaBase, atrMana, k4);
                k4++;
                atDisplay_Height += 8;
            }
        }
        
        if (!inventory) {
            // display for trade
            replaceNumberLeftAlign(atrSelltimes, it.unitsSell, 6, 8, false);
            attributeDisplay[0] = atrSelltimes;
            replaceNumberLeftAlign(atrSellprice, it.gold, 6, 11, false);
            attributeDisplay[1] = atrSellprice;
            atDisplay_Height += 10;
        }
        
        
        if (atDisplay_Height>0) {
            atDisplay_Height += 4;
            if (k4>7 && db1) {
                atDisplay_Height += 5;
            }
        }
        
        if (atDisplay_Height > 0 && atDisplay_Height < 48) {
            atDisplay_Height = 48;
        }
        
        // attribute draw settings
        atDisplay_Item = it;
    }

    
    private void setSingleAttributeDisplay(int value, char[] targetArray, int displayIndex) {
        if (value >= 0) {
            targetArray[11]='+';
        } else {
            targetArray[11]='-';
            value *= -1;
        }
        replaceNumberLeftAlign(targetArray, value, 12, 16, true);
        attributeDisplay[displayIndex] = targetArray;
    }
    */

    
    private void drawInventory(int yOffset, boolean drawAttributes, boolean drawEquipment) {
        playerObject.inventory.draw(currentGraphics, font, inventoryBackgroundSlot, 0, yOffset, DISPLAYWIDTH, DISPLAYHEIGHT, playerObject, true);

        if(drawEquipment) {
            playerObject.inventory.drawEquipment(currentGraphics, DISPLAYWIDTH, DISPLAYHEIGHT + TOP_INFOHEIGHT, font, title_equipment, true);
            // -- drawBelt(0, false);
        }

    }

    /** Draw the invemtory. */
    /*
    private void drawInventory(int yOffset, Item[] itemArray, boolean drawAttributes, boolean drawEquipment) {
        playerObject.inventory.draw(currentGraphics, font, inventoryBackgroundSlot, 0, yOffset, DISPLAYWIDTH, DISPLAYHEIGHT, playerObject, true);


        
        if(drawEquipment) {
            playerObject.inventory.drawEquipment(currentGraphics, DISPLAYWIDTH, DISPLAYHEIGHT + TOP_INFOHEIGHT, font, title_equipment, true);
            // -- drawBelt(0, false);
        }

        if (true)
            return;


        db2 = (itemArray == invItems);
        
        if (db2) {
            d10 = selectedInvItem;
            d11 = invItemsCount;
        } else {
            d10 = selectedTradeOfferItem;
            d11 = tradeOfferItemsCount;
        }
        
        GTools.saveGraphicsSettings(currentGraphics);
        
        m = INVENTORY_COLS * ITEMSLOTWIDTH;
        n = 100-m;
        
        j=0;
        if(inventoryNeedsScrolling) { //x-Offset for phones with small display (<176)
            d12 = (d10%INVENTORY_COLS+1)*ITEMSLOTWIDTH; //right end of selection marker
            if(d12 > (DISPLAYWIDTH+inventoryOffset)) {
                inventoryOffset = d12 - DISPLAYWIDTH;
            } else if(d12-ITEMSLOTWIDTH-inventoryOffset < 0){
                inventoryOffset = d12-ITEMSLOTWIDTH;
            }
        }
        
        Image itemImage;
        for (i=0; i<INVENTORY_ROWS; i++) {
            for (k=0; k<INVENTORY_COLS; k++) {
                j = i*INVENTORY_COLS + k;
                currentGraphics.setClip(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                //get the item
                itemTmpD = itemArray[j];
                // draw inventory background
                if (db2) 
                    d7 = 1;
                else 
                    d7 = 0;
                drawItemTypeBackground(itemTmpD, i, k, yOffset, j==d10, d7);
                //currentGraphics.setClip(k*ITEMWIDTH-inventoryOffset, i*ITEMHEIGHT + yOffset, ITEMWIDTH, ITEMHEIGHT);

                if (itemTmpD!=null && j < d11) {  // draw the item itself
                    //itemimage
                    currentGraphics.setClip(k*ITEMSLOTWIDTH+1-inventoryOffset, i*ITEMSLOTHEIGHT+1 + yOffset, ITEMWIDTH, ITEMHEIGHT);
                    if(itemTmpD.graphicsel == 1) {
                        itemImage = items[1];
                    } else {
                        itemImage = items[0];
                    }

                    currentGraphics.drawImage(itemImage,  k*ITEMSLOTWIDTH+1 - ((itemTmpD.graphicsX*DIM))-inventoryOffset,
                                                      i*ITEMSLOTHEIGHT+1 + yOffset -((itemTmpD.graphicsY*DIM)),
                                                      anchorTopLeft);
                    
                    
                    
                    if (db2 && itemTmpD.gold > 0 && !(j==d10&&flash)) {
                        currentGraphics.setClip(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, 8, 8);
                        // dollar sign if for sale
                        currentGraphics.drawImage(GlobalResources.imgIngame,  k*ITEMSLOTWIDTH - 32-inventoryOffset,
                                                          i*ITEMSLOTHEIGHT + yOffset -8, 
                                                          anchorTopLeft);   // dollar sign
                    }
                    //draw details for the given item
                    if (j==d10) {
                        if (!(atDisplay_Item==itemTmpD)) {
                            setAttributeValuesDisplay(itemTmpD, db2);
                        }
                        
                        if (drawAttributes) {
                            drawItemAttributes(itemTmpD, 0, yOffset + 32, db2, itemTmpD.units > 0);
                        }
                    }
                }

                if (j==d10) { // selection frame
                    currentGraphics.setClip(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);                    
                    if (flash) {
                        currentGraphics.setColor(255, 204, 0);
                    } else {
                        currentGraphics.setColor(255, 255, 255);
                    }
                    currentGraphics.drawRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
                    //currentGraphics.drawRect(k*ITEMSLOTWIDTH+1, i*ITEMSLOTHEIGHT+1 + yOffset, ITEMSLOTWIDTH-3, ITEMSLOTHEIGHT-3);
                }
                
                
            }

            itemTmpD = null;
            GTools.restoreGraphicsSettings(currentGraphics);
        }
        //draw left/right arrow
        if(inventoryNeedsScrolling && flash) {
            //left arrow
            if(inventoryOffset > 0) {
                currentGraphics.setClip(1, yOffset + ITEMSLOTHEIGHT - 3, 5, 7);
                currentGraphics.drawImage(GlobalResources.imgIngame, -13, yOffset+ITEMSLOTHEIGHT-70, anchorTopLeft);   // orange left arrow
            }
            if(ITEMSLOTWIDTH*INVENTORY_COLS-inventoryOffset > DISPLAYWIDTH) {
                currentGraphics.setClip(DISPLAYWIDTH-6, yOffset + ITEMSLOTHEIGHT-3, 5, 7);
                currentGraphics.drawImage(GlobalResources.imgIngame, DISPLAYWIDTH-24, yOffset+ITEMSLOTHEIGHT-70, anchorTopLeft);   // orange arrow right
            }
        }
//#if Series40_MIDP2_0
//#         GTools.drawWindow(currentGraphics, playerGoldWindow, true);
//#         currentGraphics.setClip(playerGoldWindow.x + playerGoldWindow.width + 1, TOTALHEIGHT-15, ITEMWIDTH, ITEMHEIGHT);
//#         currentGraphics.drawImage(items[0], currentGraphics.getClipX()-45, TOTALHEIGHT-115, anchorTopLeft);
//#endif
    }
*/
    
    public void drawEquipment(boolean attributesVisible) {
        Image itemImage;
        
        // equipment title - draw once        
        d3 = ITEMSLOTWIDTH << 1;
        d4 = DISPLAYWIDTH - d3 - 2;
        d12 = d4 + ((d3 - 20) >> 1) + 2;   // title indent for centering
        
        d10 = ITEMSLOTHEIGHT * 3;
        d11 = DISPLAYHEIGHT + TOP_INFOHEIGHT - d10 - 8; // starty
        
        currentGraphics.setClip(d4 - 2, d11, d3 + 4, d10 + 8);
        currentGraphics.setColor(0, 0, 0);
        currentGraphics.fillRect(d4 - 2, d11, d3 + 4, d10 + 8);
        font.drawString(currentGraphics, title_equipment, d12, d11 + 1);
        
        
        for (i=0; i<3; i++) {
            for (k=0; k<2; k++) {
                d5 =  d4 + k * ITEMSLOTWIDTH;
                d6 = DISPLAYHEIGHT + TOP_INFOHEIGHT - ((3-i) * ITEMSLOTHEIGHT);
                /*
                if (attributesVisible) {
                    d5 += atDisplay_Width;
                    d6 += atDisplay_DescriptionHeight;
                }
                 */

                itemTmpD = null;
                
                //switch((i*2)+k) {
                switch((i<<1)+k) {
                    case 0:
                        if (equipment[HELMET]!=null) {
                            itemTmpD = equipment[HELMET];
                        }
                        break;
                    case 1:
                        if (equipment[ARMOUR]!=null) {
                            itemTmpD = equipment[ARMOUR];
                        }
                        break;
                    case 2:
                        if (equipment[WEAPON]!=null) {
                            itemTmpD = equipment[WEAPON];
                        }
                        break;
                    case 3:
                        if (equipment[SHIELD]!=null) {
                            itemTmpD = equipment[SHIELD];
                        }
                        break;
                    case 4:
                        if (equipment[BOOTS]!=null) {
                            itemTmpD = equipment[BOOTS];
                        }
                        break;
                    case 5:
                        if (equipment[HANDPROTECTION]!=null) {
                            itemTmpD = equipment[HANDPROTECTION];
                        }
                        break;
                }
                
                
                currentGraphics.setClip(d5,  d6, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                if (itemTmpD!=null && itemTmpD==invItems[selectedInvItem]) {
                    //currentGraphics.setColor(0, 240, 100);
                    currentGraphics.setColor(0, 192, 255);   // light blue
                    currentGraphics.fillRect(d5,  d6, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                    currentGraphics.setColor(255, 255, 255);
                    currentGraphics.drawRect(d5,  d6, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
                } else {
                    //currentGraphics.setColor(80, 0, 0);
                    currentGraphics.setColor(0, 64, 128);    // medium blue
                    currentGraphics.fillRect(d5,  d6, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                    currentGraphics.setColor(128, 128, 128);
                    currentGraphics.drawRect(d5,  d6, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
                }
                
                
                if (itemTmpD!=null) {
                    currentGraphics.setClip(d5+1, d6+1, ITEMWIDTH, ITEMHEIGHT);
                    if(itemTmpD.graphicsel == 1) {
                        itemImage = items[1];
                    } else {
                        itemImage = items[0];
                    }
                    currentGraphics.drawImage(itemImage,  d5+1 - ((itemTmpD.graphicsX*DIM)),
                                      d6+1 -((itemTmpD.graphicsY*DIM)),
                                      anchorTopLeft);
                }

            }
        }
    }

    
    
    
    
    private void drawBelt(int slotSelection, boolean centerY) {
        
        if (slotSelection > 0) {
            d6 = 5; // xPad
            d7 = 21; // yPadTop
            if (slotSelection == 1) {
                d8 = 28; // yPadBottom, enough room for usage hint
            } else {
                d8 = 13; // yPadBottom
            }
        } else {
            d6 = 2; // xPad
            d7 = 8; // yPadTop
            d8 = 0; // yPadBottom
        }
        
        d5 = ITEMSLOTWIDTH * MAX_BELT_ITEMS;    // total slot width
        d3 = d5 + (d6 << 1);    // total belt width
        d4 = ITEMSLOTHEIGHT + d7 + d8; // total belt height
        
        // xStart
        d1 = (DISPLAYWIDTH - d3) >> 1; // centered
        if (slotSelection == 0 && d1 + d3 > DISPLAYWIDTH - 35) {
            // make sure belt does not overlap equipment display
            d1 = DISPLAYWIDTH - 39 - d3;
        }
            //d1 = DISPLAYWIDTH - d3 - ITEMSLOTWIDTH * 3;    // right edge next to equipment

        d10 = d1 + d6;   // x pos for slots
        
        // yStart
        if (centerY) {
            d2 = (DISPLAYHEIGHT + TOP_INFOHEIGHT - d4) >> 1;   // v-centered
        } else {
            d2 = DISPLAYHEIGHT + TOP_INFOHEIGHT - d4;   // bottom edge
        }
        d11 = d2 + d7;   // y pos for slots
        
        // title indent
        if (slotSelection == 1) {
            d9 = (d3 - 55) >> 1;    // indent x: "Select Slot" 11 chars -> 55
        } else if (slotSelection == 2) {
            d9 = (d3 - 40) >> 1;    // indent x: "Use Item" 8 chars -> 40
        } else {
            d9 = (d3 - 20) >> 1;    // indent x: "Belt" 4 chars -> 20
        }
        

        // belt title and bg - draw once
        currentGraphics.setColor(0, 0, 0);
        currentGraphics.setClip(d1, d2, d3, d4);
        currentGraphics.fillRect(d1, d2, d3, d4);
        if (slotSelection > 0) {
            // draw frame for slot selection
            currentGraphics.setColor(128, 128, 128);
            currentGraphics.drawRect(d1, d2, d3-1, d4-1);
            if (slotSelection == 1) {
                font.drawString(currentGraphics, title_belt_select, d1 + d9, d2 + 3);
                font.drawString(currentGraphics, hint_belt_activate, d1 + d9, d11 + ITEMSLOTHEIGHT + 17);
            } else if (slotSelection == 2) {
                font.drawString(currentGraphics, title_belt_use, d1 + d9, d2 + 3);
            }
        } else {
            font.drawString(currentGraphics, title_belt, d1 + d9, d2 + 1);
        }

        
        
        Image itemImage;
                
        for (d12=0; d12 < MAX_BELT_ITEMS; d12++) {
            itemTmpD = belt[d12];
            
            font.drawChar(currentGraphics, (char)(48 + d12), d10 + 6, d11 + ITEMSLOTHEIGHT + 2);
            
            currentGraphics.setClip(d10, d11, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
            
            // draw background
            if ( (itemTmpD != null && (slotSelection==0 && itemTmpD == invItems[selectedInvItem])) || (slotSelection>0 && d12 == selectedBeltItem) ) {
                // item is set in belt at given slot and item selected in invemtory
                currentGraphics.setColor(192, 192, 255);
                currentGraphics.fillRect(d10, d11, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                currentGraphics.setColor(255, 255, 255);
                currentGraphics.drawRect(d10,  d11, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
                
                if (slotSelection == 2 || (slotSelection == 1 && flash)) {    // draw selection arrow
                    GTools.drawArrow(currentGraphics, 2, d10 + 7, d11 - 6, 5, 0xFFFFFF);
                }
            } else {
                currentGraphics.setColor(80, 80, 255);
                currentGraphics.fillRect(d10, d11, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                currentGraphics.setColor(128, 128, 128);
                currentGraphics.drawRect(d10,  d11, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
            }
            
            if (itemTmpD != null) {
                
                // draw item
                currentGraphics.setClip(d10 + 1 , d11+1, ITEMWIDTH, ITEMHEIGHT);
                if(itemTmpD.graphicsel == 1) {
                    itemImage = items[1];
                } else {
                    itemImage = items[0];
                }

                currentGraphics.drawImage(itemImage,  d10+1 - ((itemTmpD.graphicsX*DIM)),
                                                  d11+1 -((itemTmpD.graphicsY*DIM)),
                                                  anchorTopLeft);
            }
            
            d10 += ITEMSLOTWIDTH;
        }
        
        
    }
    
    
    
    
    /**
     * Draw the background of an item in either the inventory or trade offer 
     * items.
     * @param Item The item to draw the background for
     * @param i The current row
     * @param k The current column
     */
    private void drawItemTypeBackground(Item item, int i, int k, int yOffset, boolean selected, int inventory) {
        // inventory 0 = buy screen
        // inventory 1 = inventory
        // inventory 2 = belt / fight mode
        
        if (inventory==1 && item != null && item.equipped!=0) {
            // equipped
            if (selected) {
                if (item.equipped > 1) {
                    currentGraphics.setColor(192, 192, 255);   // belt: light violet
                } else {
                    currentGraphics.setColor(0, 192, 255);   // equipment: light blue
                }
            } else {
                if (item.equipped > 1) {
                    currentGraphics.setColor(80, 80, 128);    // belt: medium violet
                } else {
                    currentGraphics.setColor(0, 64, 128);    // equipment: medium blue
                }
            }
            currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);            
            return;
        }

        // not equipped
        if (!selected || item == null) {        // item not selected
            // default inv. bg
            if (inventory == 1) {
                    currentGraphics.drawImage(GlobalResources.imgIngame, k*ITEMSLOTWIDTH-inventoryOffset,
                                                 i*ITEMSLOTHEIGHT + yOffset, 
                                                 anchorTopLeft);
            } else if (inventory == 2) {
                // belt
                currentGraphics.setColor(80, 80, 128);    // belt: medium violet
                currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                
            } else {    // black: buy list
                currentGraphics.setColor(0, 0, 0);
                currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                currentGraphics.setColor(128, 128, 128);
                currentGraphics.drawRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
            }

        } else {    // item is selected, not equipped
            if (item.requiredMagic * 10 > magic || item.requiredSkill * 10 > skill || (inventory==0 && item.gold > playerObject.gold)) {
                // player does not meet the requirements for this item -> red background
                currentGraphics.setColor(160, 0, 0);
                currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
            } else if (inventory != 2) {
                // player meets the requirements for this item -> green background
                currentGraphics.setColor(0, 240, 100);
                currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
            }
        }

            /*
            
            if (item.equipped!=0) {    // item is equipped
                if (!selected) {
                    currentGraphics.setColor(0, 160, 80);
                } else {
                    currentGraphics.setColor(0, 240, 100);
                }
                currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);

                // if (!selected) {
                //    currentGraphics.setColor(0, 200, 100);
                //    currentGraphics.drawRect(k*ITEMWIDTH, i*ITEMHEIGHT + yOffset, ITEMWIDTH-1, ITEMHEIGHT-1);
                //}

            } else if (item.units!=-2 && item.units!=0) {    // usable
                if (!selected) {
                    currentGraphics.setColor(0, 80, 160);
                } else {
                    currentGraphics.setColor(0, 160, 255);
                }
                currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);

                // if (!selected) {
                //    currentGraphics.setColor(0, 100, 200);
                //    currentGraphics.drawRect(k*ITEMWIDTH, i*ITEMHEIGHT + yOffset, ITEMWIDTH-1, ITEMHEIGHT-1);
                // }
                 
            } else {    // other
                if (!selected) {
                    // draw the inventory background tile
                    if (inventory) {
                        currentGraphics.drawImage(GlobalResources.imgIngame, k*ITEMSLOTWIDTH-inventoryOffset,
                                                         i*ITEMSLOTHEIGHT + yOffset, 
                                                         anchorTopLeft);    // inventory bg
                    } else {
                        currentGraphics.setColor(0, 0, 0);
                        currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                        currentGraphics.setColor(128, 128, 128);
                        currentGraphics.drawRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
                    }
                    
                } else {
                    //default selection background
                    currentGraphics.setColor(160, 0, 0);
                    currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                }
            }
        } else {
            // draw the inventory background tile
            if (!selected) {
                if (inventory) {
                    currentGraphics.drawImage(GlobalResources.imgIngame, k*ITEMSLOTWIDTH-inventoryOffset,
                                                     i*ITEMSLOTHEIGHT + yOffset, 
                                                     anchorTopLeft);    // inventory bg
                } else {
                    currentGraphics.setColor(0, 0, 0);
                    currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
                    currentGraphics.setColor(128, 128, 128);
                    currentGraphics.drawRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
                }
            } else {
                currentGraphics.setColor(120, 0, 0);            
                currentGraphics.fillRect(k*ITEMSLOTWIDTH-inventoryOffset, i*ITEMSLOTHEIGHT + yOffset, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
            }
        }
             */
    }

    
    /** Show details of an item to the right of the inventory. */
    private void drawItemAttributes(Item fwgo, int xStart, int yStart, boolean inventory, boolean isUsable) {

        d9 = (DISPLAYWIDTH > 176) ? 176 : DISPLAYWIDTH;
        
        // name /description
        currentGraphics.setClip(0, TOP_INFOHEIGHT + 32, d9, atDisplay_DescriptionHeight);
        currentGraphics.setColor(0, 0, 80);
        currentGraphics.fillRect(0, TOP_INFOHEIGHT + 32, d9, atDisplay_DescriptionHeight);
        currentGraphics.setColor(255, 204, 0);
        currentGraphics.drawRect(0, TOP_INFOHEIGHT + 32, d9-1, atDisplay_DescriptionHeight-1);

        // draw Item name
        font.drawString(currentGraphics, fwgo.name, 4, TOP_INFOHEIGHT + 36);
        // draw item description
        GTools.drawTextWindow(currentGraphics, itemDescriptionWindow);
        // draw separation line
        currentGraphics.setClip(2, itemDescriptionWindow.y - 3, d9 - 4, 1);
        currentGraphics.setColor(80, 80, 80);
        currentGraphics.drawLine(2, itemDescriptionWindow.y - 3, d9 - 2, itemDescriptionWindow.y - 3);

        
        yStart += atDisplay_DescriptionHeight;
        
        if (atDisplay_Height>0) {
        
            /*d6 = 0;
            if(curGametime > this.inventoryScrollHugeItemTime+2*this.inventoryScrollDuration) {
                this.inventoryScrollHugeItemTime = this.curGametime;
            } else if(curGametime > this.inventoryScrollHugeItemTime+this.inventoryScrollDuration) {
                //in this timeframe the attributes are scrolled upwards
                d6 = (DISPLAYHEIGHT-32-atDisplay_DescriptionHeight) - (atDisplay_Height); // amount of pixel
                if(d6 > 0) {
                    d6 = 0;
                } else {
                    //needs scrolling by d6 pixel upwards
                    d6 = (((-d6)/8)+1)*8;
                }
            }
            */
            d6 = inventoryScrollHugeItemOffset;
            if(curGametime > this.inventoryScrollHugeItemTime+this.inventoryScrollDuration) {
//#if Series40_MIDP2_0
//#                 //4 additional pixel because no bottom background gfx
//#                 d6 = (DISPLAYHEIGHT-28-atDisplay_DescriptionHeight) - (atDisplay_Height); // amount of pixel
//#else
                d6 = (DISPLAYHEIGHT-32-atDisplay_DescriptionHeight) - (atDisplay_Height); // amount of pixel
//#endif
                if(d6 > 0) {
                    d6 = 0;
                    inventoryScrollHugeItemTime = Long.MAX_VALUE;
                } else {
                    //needs scrolling by d6 pixel upwards
                    //d6 = (((-d6)/8)+1)*8;
                    d6 = (((-d6)>>3)+1)<<3;
                    inventoryScrollHugeItemTime = this.curGametime;
                    if(this.inventoryScrollHugeItemOffset <= 0) {
                        this.inventoryScrollHugeItemDown = true;
                        d6 = 8; //one line
                    } else if(inventoryScrollHugeItemOffset >= d6){
                        this.inventoryScrollHugeItemDown = false;
                        d6 = inventoryScrollHugeItemOffset-8; //one line
                    } else if(inventoryScrollHugeItemDown){
                        d6 = inventoryScrollHugeItemOffset+8; //one line
                    } else {
                        d6 = inventoryScrollHugeItemOffset-8; //one line
                    }
                    inventoryScrollHugeItemOffset = d6;
                }
            }
            
            
            //draw attribute background
            currentGraphics.setClip(xStart, yStart-1, atDisplay_Width, atDisplay_Height+1-d6);
            currentGraphics.setColor(0, 0, 0);
            currentGraphics.fillRect(xStart, yStart-1, atDisplay_Width, atDisplay_Height+1-d6);
            currentGraphics.setColor(255, 204, 0);
            currentGraphics.drawRect(xStart, yStart-1, atDisplay_Width-1, atDisplay_Height-d6);

            /*
            for (d5=0; d5<5; d5++) {
                if (attributeDisplay[d5]!=null) {
                    font.drawString(currentGraphics, attributeDisplay[d5], xStart, yStart);
                    yStart += 8;
                }
            }
             */

            if (!inventory) { // trade specific display
                // blue background
                currentGraphics.setClip(xStart, yStart-1, DISPLAYWIDTH, 11);
                currentGraphics.setColor(0, 0, 80);
                currentGraphics.fillRect(xStart+1, yStart-1, DISPLAYWIDTH-2, 10);
                currentGraphics.setColor(255, 204, 0);
                currentGraphics.drawRect(xStart, yStart-1, DISPLAYWIDTH-1, 10);
                
                // draw unitsSell
                if (attributeDisplay[0]!=null) {
                    font.drawString(currentGraphics, attributeDisplay[0], xStart + 2, yStart + 2);
                }
                // draw gold
                if (attributeDisplay[1]!=null) {
                    if(DISPLAYWIDTH > 140)
                        font.drawString(currentGraphics, attributeDisplay[1], xStart + 88, yStart + 2);
                    else
                        font.drawString(currentGraphics, attributeDisplay[1], xStart + 67, yStart + 2);
                }
                yStart += 10;
            }

            yStart += 2;
            xStart += 2;
            //scrolling
            d7 = yStart;
            yStart -= d6;
            
            // draw units
            if (attributeDisplay[2]!=null && inventory) {
                if(yStart>=d7) font.drawString(currentGraphics, attributeDisplay[2], xStart, yStart);
                yStart += 8;
                db1=true;
            }
            // draw requiredskill
            if (attributeDisplay[3]!=null) {
                if(yStart>=d7) {
                    if (skill < fwgo.requiredSkill*10) {
                        drawAttributeBackground(xStart, yStart, false);
                    }
                    font.drawString(currentGraphics, attributeDisplay[3], xStart, yStart);
                }
                yStart += 8;
                db1=true;
            }
            // draw requiredmagic
            if (attributeDisplay[4]!=null) {
                if(yStart>=d7) {
                    if (magic < fwgo.requiredMagic*10) {
                        drawAttributeBackground(xStart, yStart, false);
                    }
                    font.drawString(currentGraphics, attributeDisplay[4], xStart, yStart);
                }
                yStart += 8;
                db1=true;
            }
            // draw attackrate
            if (attributeDisplay[5]!=null) {
                if(yStart>=d7) font.drawString(currentGraphics, attributeDisplay[5], xStart, yStart);
                yStart += 8;
                db1=true;
            }

            // draw range
            if (attributeDisplay[6]!=null) {
                if(yStart>=d7) font.drawString(currentGraphics, attributeDisplay[6], xStart, yStart);
                yStart += 8;
                db1=true;
            }


            if (db1) {
                yStart += 5;
            }

            // draw other attributes
            for(d5=7; d5 < attributeDisplay.length && attributeDisplay[d5]!=null; d5++) {
                if(yStart>=d7) {
                    if (!isUsable) {
                        drawAttributeBackground(xStart, yStart, attributeDisplay[d5][11]=='+');
                    }
                    font.drawString(currentGraphics, attributeDisplay[d5], xStart, yStart);
                }
                yStart+=8;  // next line
            }
        }
    }
    
    /**
     * Highlight an attribute in the item details window.
     * @param xStart X Position of the attribute
     * @param yStart Y Position of the attribute
     * @param condition If true, the backgroundcolor will be green, red otherwise
     */
    private void drawAttributeBackground(int xStart, int yStart, boolean condition) {
            if (!condition) {
                currentGraphics.setColor(128, 0, 0);
            } else {
                currentGraphics.setColor(0, 96, 0);
            }
            currentGraphics.setClip(xStart-1, yStart, atDisplay_Width-2, font.charHeight);
            currentGraphics.fillRect(xStart-1, yStart, atDisplay_Width-2, font.charHeight);
    }
    
    
    
    /** Clear the whole screen. */
    private void clearScreen() {
        if (currentGraphics!=null) {
                    currentGraphics.setColor(0, 0, 0);
                    currentGraphics.setClip(0,0, DISPLAYWIDTH, TOTALHEIGHT);
                    currentGraphics.fillRect(0, 0, DISPLAYWIDTH, TOTALHEIGHT);
        }
    }
    
    
    
    /**
     * Constructor creates images and initializes network.
     */
    public FantasyWorldsGame() {
        //int xy = 255;
        //int val = NetTools.uintFrom1Byte(xy);
        //System.out.println("val: " + val);

        if (release) {
            defaultHost = releaseHost;   // gb-dev root server II
            clusterDisable = true; // SET FOR RELEASE: false
            readDefaultHostFromDB = true; // SET FOR RELEASE: true
            host = defaultHost;
        } else {
            defaultHost = testHost;
            clusterDisable = true;
            readDefaultHostFromDB = false;
            host = defaultHost;
        }
        
        
//#if Motorola || Series40_MIDP2_0 || SonyEricssonWTK2_0MIDP2_0 || MIDP_2_0_GENERIC_KEYS || WebEmulator
//#         this.setFullScreenMode(true);
//#endif
        
        if (debugLevel > 0)
            System.out.println("Total Memory: " + Runtime.getRuntime().totalMemory() + " bytes");
        

        DISPLAYWIDTH = 176;
        DISPLAYHEIGHT = 208-BOTTOM_INFOHEIGHT-TOP_INFOHEIGHT;
        TOTALHEIGHT = 208;
        minDisplayXPos = (DISPLAYWIDTH/2)-(MIN_DISPLAYWIDTH/2);
        minDisplayYPos = (DISPLAYHEIGHT/2)-(MIN_DISPLAYHEIGHT/2);
        //isDoubleBuffered = false;
        isDoubleBuffered = this.isDoubleBuffered();
        
        
        buffer = new byte[128];
        idToCharacters = new Hashtable();
        idToItems = new Hashtable();
        //idToTempDroppedItems = new Hashtable();
        
        // Test
        xPos = 0;
        yPos = 0;
        playerSpeed = GlobalSettings.PLAYER_SPEED_PER_FRAME;
        
        // backgroundString = "back0.png";
        
        items[0] = null;
        items[1] = null;
        
        invItems = new Item[INVENTORY_ROWS * INVENTORY_COLS];
        equipment = new Item[7];
        belt = new Item[MAX_BELT_ITEMS];
        
        attributeDisplay = new char[16][15];
        atDisplay_Height = INVENTORY_ROWS * ITEMSLOTHEIGHT + 2;
        
        for (int i=16; --i>=0; ) {
            numReplace[i] = ' ';
        }
        System.arraycopy(numReplace, 0, numReplaceEmpty, 0, numReplace.length);

        database = new GDataStore("FantasyWorlds");
        databaseGfxBack = new GDataStore("back0.png");
        databaseGfxEnemy = new GDataStore("enemy0.png");
        if(!databaseGfxBack.connect())
            databaseGfxBack = null;
        if(!databaseGfxEnemy.connect())
            databaseGfxEnemy = null;
        if(database.connect()) {
            if (readDefaultHostFromDB) {
                host = database.getValue("host");
                // START DEBUG - for release: "//host = defaultHost;"
                // host = defaultHost;
                // END DEBUG
                if(host == null || host.equals("")) {
                    database.setValue("host", defaultHost);
                    host = defaultHost;
                }
            }
            //System.out.println(host);

            clientName = database.getValue("clientname");
            clientPass = database.getValue("clientpass");



            // initialize the chat shortcuts
            chatShortcuts[0] = "Yes";
            chatShortcuts[1] = "No";
            chatShortcuts[2] = "Thank you!";
            chatShortcuts[3] = "I need help!";
            chatShortcuts[4] = "I need mana!";
            chatShortcuts[5] = "Heal me!";
            chatShortcuts[6] = "Follow me!";
            chatShortcuts[7] = "Let's move!";
            chatShortcuts[8] = "Please wait!";
            chatShortcuts[9] = "Where to?";
            chatShortcuts[10] ="What's up?";
            chatShortcuts[11] ="Want to trade?";
            chatShortcuts[12] ="Where are you from?";
            chatShortcuts[13] ="Attack!";
            chatShortcuts[14] ="Draw back!";
            chatShortcuts[15] ="Well done!";
            chatShortcuts[16] ="Uh - That's gotta hurt!";
            chatShortcuts[17] ="Peace of cake!";
            chatShortcuts[18] ="Who wants some?!";
            chatShortcuts[19] ="You're going down ..";
            chatShortcuts[20] ="You're welcome!";
            chatShortcuts[21] ="Hail to the king!";
            chatShortcuts[22] ="Rhynn forever!";
            chatShortcuts[23] ="Good Bye!";

            
            // load sound settings
            try {
                tmpStringM = database.getValue("soundvolume");
                if (tmpStringM!=null) {
                    d10 = Integer.parseInt(tmpStringM);
                    if ((d10 >= 0 && d10 <=100 && d10 % 10 == 0) || d10 == -1) {
                        curSoundVolume = d10;
                    }
                }
                tmpStringM = database.getValue("sound");
                if (tmpStringM!=null) {
                    if (tmpStringM.equals("ON")) {
                        soundON = true;
                    } else if (tmpStringM.equals("OFF")) {
                        soundON = false;
                    }
                }
                
            } catch (Exception e) {}
            
        }
        
        key = createKey();
        
        checkRS();
        
        imageManager = new ImageManager(gbManager, this);
    }

    // ------------------------------------
    // Implementations for the ImageManagerObserver callbacks
    // ------------------------------------

    public void onImageLoadDebug(String msg) {
        //sendDebug("ILD: " + msg);
    }

    public void onImageLoadFromNetError(int graphicsId, String message) {
        // this is a critical error
        // todo: call netError or similar, exit program
        //System.out.println("Image net load error: " + graphicsId + ", msg: " + message);
        forcedExit(message);
    }

    public void onImageLoadFormNetStarted(int graphicsId, int numRemainingInQueue) {
        //System.out.println("Image net load started for graphicsId: " + graphicsId + ", remaining: " + numRemainingInQueue);
        // note: there is no message signature included in this message, so the server will need to act accordingly and not expect a signature for this message
        //System.out.println("sending get image for id: " + graphicsId);
        //byte[] buffer = new byte[8];
        buffer[0] = 8;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_GRAPHICS_LOAD_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(graphicsId, buffer, 4);
        //gbManager.sendMessage(buffer);
        doSend(buffer);
    }

    public void onImageLoadFromNetFinished(int graphicsId, Image loadedImage, int numRemainingInQueue) {
        //System.out.println("Image net load finished for graphicsId: " + graphicsId);
        if (imageManager.loadingCount() > 0) {
            GTools.labelSetText(label3, "Loading graphic " + (numImagesToLoad - imageManager.loadingCount() + 1), false);
            GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
        } else {
            GTools.labelSetText(label3, "All graphics loaded", false);
            GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
        }
    }
    public void onImageLoadFromNetChunkLoaded(int graphicsId, int curChunk, int totalChunks) {
        //System.out.println("Chunk " + curChunk + " of " + totalChunks + " for graphicsId " + graphicsId);
    }

    public void onImageLoadFromNetNonImageMessageReceived(byte[] message) {
        //System.out.println("Received a non-image-loading message ");
        this.executeMessage(message);
    }



    // ------------------------------------


    private int createKey() {
        int x = 0;
        int bit = 1;
        boolean doIt = true;
        for(int i=0; i<16; i++) {
            if(doIt) {
                x = x | bit;
                doIt = !doIt;
            }
            bit = bit << 1;
            if((i==2) || (i==3) || (i==4) || (i==6) ||(i==9) ||(i==10) || (i==12) || (i==14)) {
                doIt = !doIt;
            }
        }
        return x;
    }
    
    private void checkRS() {
        if (RSForcedOff) {
            return;
        }
        int total = 0;
        if(databaseGfxBack != null && databaseGfxEnemy != null)
            total += databaseGfxBack.getSizeTotal() + databaseGfxEnemy.getSizeTotal();
        int free = 0;
        if(databaseGfxBack != null && databaseGfxEnemy != null)
            free += databaseGfxBack.getSizeAvailable() + databaseGfxEnemy.getSizeAvailable();
        
        if(free > MINIMUM_RS || (total > MINIMUM_RS && free > MINIMUM_RS/2)) {
            useRSwriting = true;
            useRSreading = true;
        } else if(total > 1000){
            useRSreading = true;
        }
    }
    
    public void initCanvas() {
        DISPLAYWIDTH = getWidth();
        TOTALHEIGHT = getHeight();
        
        //DISPLAYWIDTH=128;
        //TOTALHEIGHT=128;

        DISPLAYHEIGHT = TOTALHEIGHT - BOTTOM_INFOHEIGHT-TOP_INFOHEIGHT;
        
        minDisplayXPos = (DISPLAYWIDTH/2)-(MIN_DISPLAYWIDTH/2);
        minDisplayYPos = (DISPLAYHEIGHT/2)-(MIN_DISPLAYHEIGHT/2);        
        
        // set the game state
        currentState = STATE_INTRO;
        currentSubState = SUBSTATE_NORMAL;
        
        GWindow.activateKey2 = KEY_SOFTKEY1;
        
        soundPossible = GSoundChecker.checkSound();
        
        if (soundPossible) {
            soundPlayer = new GSoundTools();
            for (int j=0; j<1; j++) {
                // fetch sound file
                aquireSound(j, j);
            }
            //aquireSound(0, 1);
            
            // Sound 1 is default sound            
            if (soundON && !playbackSound(0, -1)) {
                soundON = false;
                soundPossible = false;
            }
            
        } else {
            soundON = false;
        }

        GlobalResources.init();
        initGraphics();
        initWindows();
//#if MIDP_2_0_GENERIC_KEYS
//#         int storedKey1 = 0;
//#         int storedKey2 = 0;
//#         if (!RSForcedOff) {
//#             String sKey1 = database.getValue("skey1");
//#             String sKey2 = database.getValue("skey2");
//#             try {
//#                 storedKey1 = Integer.parseInt(sKey1);
//#                 storedKey2 = Integer.parseInt(sKey2);
//#             } catch (Exception e) {
//#             }
//#         }
//# 
//#         if (storedKey1 == 0 && storedKey2 == 0) {
//#             currentState = STATE_DEFINE_KEYS;
//#             currentSubState = SUBSTATE_DEFINE_KEY_SK1;
//#             setWaitLabelText("Press LEFT soft key.");
//#             setBottomCommand1(null);
//#             setBottomCommand2(null);
//#         } else {
//#             KEY_SOFTKEY1 = storedKey1;
//#             KEY_SOFTKEY2 = storedKey2;
//#             setBottomCommand1("Connect");
//#             setBottomCommand2("Change Keys");
//#         }
//#endif



        //if (showDebug1) {
            /*System.out.println("TEST");
                i=0;
                sb = new StringBuffer();
                do{
                    tmpStringK = null;
                    tmpStringK = database.getValue("debug"+(i++));
                    if(tmpStringK!=null){
                        sb = sb.append(tmpStringK);} else {
                    break;
                    }
                } while (true);
                GTools.labelSetText(labelDebug1, sb.toString(), false);
                                            //System.out.println(sb.toString());

            */
            //GTools.labelSetText(labelDebug1, database.getValue("debug"), false);
        //}
        
    }
    
 
    ////////////////////////////
    // GAME UPDATE METHODS
    ////////////////////////////
    
    /**
     * Repaint display.
     */
    public void paint(Graphics g) {
        if (doPaint) {
            doPaint=false;
            if (isDoubleBuffered) {
                currentGraphics = g;
                updateGame();
            } else {
                updateGame();
                update(g);
            }
        }
    }
    
    /**
     * Update. Called by paint().
     */
    public synchronized void update(Graphics g) {
        if(currentImage != null && currentImage2 != null) {
            if(flip) {
                g.drawImage(currentImage, xOffset, yOffset, anchorTopLeft);
            } else {
                g.drawImage(currentImage2, xOffset, yOffset, anchorTopLeft);
            }
        }
    }


    
    
// aa private boolean sentStateInfo = false;

    /**
     * Update game status and display.
     */
    public synchronized void updateGame() {
        if (currentState == STATE_FORCED_EXIT) {
            shutdown = true;
            return;
        }

        timeDiff = curGametime - lastGametime;

        if (netStarted && curGametime - lastPingSent >= PINGINTERVAL) {
            sendPing();
            lastPingSent = curGametime;
        }

        
        if (soundPossible && soundON) {
            /*if (soundPlayer.midiPlayer.getMediaTime() > soundTimes[soundtrack][1]) {
                // loop
                try {
                    soundPlayer.midiPlayer.stopPlay();
                    soundPlayer.midiPlayer.setMediaTime(soundTimes[soundtrack][0]);
                    soundPlayer.midiPlayer.startPlay();
                } catch (Exception e) {
                    System.out.println(e.toString());
                }                            
            }*/
        }

        
        
        if (curGametime-lastOnlineMinute > 60000) {
            lastOnlineMinute = curGametime;
            onlineMinutes++;
            /*if (showDebug3) {
                replaceNumber(labelDebug3.text, onlineMinutes, 3, 5);
            }*/
        }
        
        //check message timeout
        if (messageTimeout > 0) {
            messageTimeout -= timeDiff;
            if (messageTimeout <= 0) {  //time up!
                subStateOKDialog(messageTimeoutMessage, stateAfterTimeout, subStateAfterTimeout);
                setBottomCommand1(bottomCommand1AfterTimeout);
                setBottomCommand2(bottomCommand2AfterTimeout);
                messageTimeout = -1;
                database.setValue("host", defaultHost);
            }
        }
        
        if (soundON && soundPossible) {
            this.decreasedTime(nextAutoSoundTypeChange, -1);
            if (nextAutoSoundTypeChange > -1) {
                nextAutoSoundTypeChange -= timeDiff;
                if (nextAutoSoundTypeChange<0) {
                    if (currentSubState==SUBSTATE_FIGHT_ACTIVE) {
                         nextAutoSoundTypeChange = 12000;
                    } else {
                         nextAutoSoundTypeChange = -1;
                         /* // $-> activate!
                         //if (curSoundType == 2) {
                             if (!isPeaceful(playerObject.x + (PLAYERWIDTH_HALF), playerObject.y + (PLAYERHEIGHT_HALF)) ) {
                                playbackSound(0, -1);
                             } else {
                                playbackSound(1, -1);
                             }
                         //}
                          */
                    }
                }
            }
        }

        if (blockDuration > 0) {
            blockDuration -= timeDiff;
        }
        
        if (info1Line_DisplayTime > 0) {
            info1Line_DisplayTime -= timeDiff;
        }
            
        if (peacefulDisplayTime > 0) {
            peacefulDisplayTime -= timeDiff;
        }
        
        if (bottomInfo_DisplayTime > 0) {
            bottomInfo_DisplayTime -= timeDiff;
        }

        /*
        if (playerObject!=null && playerObject.weaponRechargeStartTime > 0) {
            long remainder = playerObject.weaponRechargeEndTime - System.currentTimeMillis();
            playerObject.weaponRechargeStartTime = remainder;
            if (playerObject.weaponRechargeStartTime <= 0) {
                playerObject.weaponRechargeEndTime = 0;
            }
            //weaponRechargeStartTime -= timeDiff;
        }*/
        
        if (xpInfoShowDuration > 0) {
             xpInfoShowDuration -= timeDiff;
             
        }
        
        if (curGametime-lastFastFlash > 80) {
            lastFastFlash = curGametime;
            fastFlash = !fastFlash;
        }
        
        // 5000
        if (curGametime-lastFlash > 500) {
            //make sure connection is up
            if (netStarted && checkNet && currentState!=STATE_FORCED_EXIT) {
                checkNetworkAlive();
            }
            
            if (debugLevel > 2) {
                System.out.println("Memory: " + Runtime.getRuntime().freeMemory() + " bytes");
            }
            if (debugLevel > 3 && playerObject!=null) {
                //System.out.println("playerx: " + (xPos + playerScreenX) + "playery: " + (yPos + playerScreenY - TOP_INFOHEIGHT) + "(" + playerObject.x + ", " + playerObject.y + ")");
            }
            lastFlash = curGametime;
            flash = !flash;
        }

        triggerFlashDuration += timeDiff;
        if (triggerFlashDuration >  triggerFlashPhases[curTriggerFlash]) {
            triggerFlashDuration = 0;
            curTriggerFlash++;
            if (curTriggerFlash > 5) {
                curTriggerFlash = 0;
            }
        }
        

        /*
        // $-> m-- call the following code to load images. prepareLoadingWorldScreen will likely need adjustment / refactoring
        prepareLoadingWorldScreen();
        currentState = STATE_WAIT_LOAD_GFX;
        System.out.println("loading call for image manager");
        imageManager.loadImageToCache(2, false, false, true, true);
        */

        skipNormalMessagePump = false;
        if (currentState == STATE_WAIT_LOAD_GFX) {

            // -- checkNetLoadImages();
            if (imageManager.loadingCount() > 0) {
                skipNormalMessagePump = true;
                imageManager.continueLoadingFromNet();
            } else {
                // finished loading all gfx
                //System.out.println("in FWG: finished loading all gfx.");

                // set the playfield graphics
                Tileset ts = null;
                while ((ts = playfield.nextUnloadedTileset()) != null) {
                    int graphicId = ts.getGraphicId();
                    Image img = imageManager.getImageFromCache(graphicId);
                    //System.out.println("setting pf image: " + graphicId + ": " + img.getWidth() + "," + img.getHeight());
                    ts.load(img);
                }

                //GTools.labelSetText(label3, "All gfx loaded", false);
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
                System.gc();

                currentState = STATE_GAUGE;
                currentSubState = SUBSTATE_NORMAL;

                // now that the graphics are loaded, load the playfield data
                sendRequestPlayfieldMessage();

                //sendJoinGroupMessage(playfieldName);
                //client left group, join legacyPlayfield
                //if (playfieldName!=null && !playfieldName.equals("")) {
                //sendJoinGroupMessage(playfieldName);
            }
        }



        //////////////////////////////
        //  Receive the incoming packets
        
        packetCounter = 0;
        if (netStarted && !skipNormalMessagePump) {

            byte[] currentData;
            do {
                currentData = null;

                currentData = gbManager.getDataAvailable();
                if(currentData != null) {
                    executeMessage(currentData);
                }
                packetCounter++;

            } while(currentData != null && packetCounter < PACKETPERLOOP && !skipNormalMessagePump);

        }

        if(showDebug5) {
            this.replaceNumber(labelDebug5.text, Runtime.getRuntime().freeMemory(), 0, 7);
            this.replaceNumber(labelDebug5.text, Runtime.getRuntime().totalMemory(), 12, 19);
            this.replaceNumber(labelDebug5.text, Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory(), 24, 31);
        }            
        if(showDebug4) {
            frameTimes[frameTimePointer] = (int)(curGametime-lastGametime);
            frameTimePointer = (frameTimePointer+1)%10;
            j=0;
            for(i=10; --i>=0; ) {
                j += frameTimes[i];
                //System.out.println("Time " + i + ": " + frameTimes[i]);
            }
            if(j>0) {
                this.replaceNumber(labelDebug4.text, (10000/j), 0, 3);
                if(netStarted){
                    this.replaceNumber(labelDebug4.text, gbManager.getNumBytesAvailable(), 9, 13);
                    this.replaceNumber(labelDebug4.text, gbManager.getMessageQueueSize(), 21, 24);
                }
            }
        }

        clearScreen();
        ////////////////////////////////

        /**
         * Render depending on the game state.
         */
        switch(currentState) {

                //
                // Game State
                //
            case STATE_GAME:
                

                // All things which should be done every x miliseconds
                if(curGametime - timeBreak > 100) {
                    timeBreak = curGametime;

                    //check if player should be moved
                    checkPlayerMove();
                    
                    // Update the world with your position
                    if((sendPos && (System.currentTimeMillis()-posLastSent > 300)) && playfieldCounter == 0) {
                        if (netStarted) {
                            sendMoveObjectMessage();
                        }
                        posLastSent = System.currentTimeMillis();
                        sendPos = false;
                    }                    
                    //System.out.println("pos: (" + (xPos + playerScreenX) + ", " + (yPos + playerScreenY) + ")");
                }
                
                                
                GTools.saveGraphicsSettings(currentGraphics);

                // see, if we should reduce the firewall times
                /*
                if (curGametime - lastFireWallCheck > 5000) {
                    lastFireWallCheck = curGametime;

                    for (i=0; i<FIREWALL_WINDOWSIZE; i++) {
                        for (j=0; j<FIREWALL_WINDOWSIZE; j++) {
                            m = fireWalls[i][j] & 15;   // extract time
                            // reduce remaining firewall time, if neccessary
                            if (m > 0) {
                                m -= 1; // measured in 5 second units
                                fireWalls[i][j] = (byte)( (fireWalls[i][j] & 240) | m);  // simply put new time back to the byte
                                // System.out.println("new time: " + m);
                            }
                        }
                    }
                }*/

                if (currentSubState != SUBSTATE_PORTAL_WAIT) {
                    playfieldView.draw(currentGraphics, false);


                    if (currentSubState==SUBSTATE_FIGHT_FIND || currentSubState==SUBSTATE_FIGHT_ACTIVE || currentSubState==SUBSTATE_TALKTO_FIND || currentSubState==SUBSTATE_TRADE_FIND || currentSubState==SUBSTATE_TRIGGERTARGET_FIND) {
                       int color = 0xffffff;
                       int cursorType = GlobalSettings.CHARACTER_CURSOR_DEFAULT;
                       switch (currentSubState) {
                           case SUBSTATE_FIGHT_FIND:
                               cursorType = GlobalSettings.CHARACTER_CURSOR_FIGHT;
                               break;
                           case SUBSTATE_FIGHT_ACTIVE:
                                cursorType = GlobalSettings.CHARACTER_CURSOR_FIGHT_ACTIVE;
                                // todo: determine in range, peaceful, weapon recharge, direct line to target
                                if (playfieldView.attackPossible(playfieldView.getSelectedCharacter(), false)) {
                                    color = 0x00ff00;
                                } else {
                                    color = 0xffc000;
                                    //currentGraphics.setColor(255,192,0);
                                }
                                break;
                           default:
                               break;
                        }

                        playfieldView.drawSelectedCharacterCursor(currentGraphics, cursorType, color);
                        GTools.drawWindow(currentGraphics, info1Line, true);
                    }
                    if (info1Line_DisplayTime > 0 ) {
                        GTools.drawWindow(currentGraphics, info1Line, true);
                    }

                }




                
                //
                // Draw the background
                //
                if(currentGraphics != null && players != null && currentSubState != SUBSTATE_PORTAL_WAIT) {
/*
                    yCounter = -(yPos%TILEHEIGHT) + TOP_INFOHEIGHT;
                    i = 0;
                    m = xPos / TILEWIDTH;
                    n = yPos/ TILEHEIGHT;
                    j = -(xPos%TILEWIDTH);
                    
                    // fire animation cycle
                    if (curGametime - lastFireAniPhase > 200) {
                        lastFireAniPhase = curGametime;
                        fireAniPhase = (fireAniPhase + 1);
                        if (fireAniPhase > 3) {fireAniPhase = 0;}
                    }
                    
                    
                    
//#if Series40_MIDP2_0
//#                     
//#                     while(yCounter < DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT && (n+i) < this.playfieldHeight) {
//#else
                    while(yCounter < DISPLAYHEIGHT + TOP_INFOHEIGHT && (n+i) < this.playfieldHeight) {
//#endif
                        xCounter = j;
                        k = 0;
                        while(xCounter < DISPLAYWIDTH && (m+k) < this.playfieldWidth) {
                            w = (m + k);
                            h = (n + i);
                            
                            xStart = xCounter;
                            if(xStart < 0) {
                                xEnd = TILEWIDTH+xStart;    // cut off at right end
                                xStart = 0;
                            } else {
                                xEnd = TILEWIDTH;
                            }

                            if(DISPLAYWIDTH - xCounter < TILEWIDTH)
                                xEnd = DISPLAYWIDTH - xCounter;
                            
                            yStart = yCounter;
                            
                            if(yStart < TOP_INFOHEIGHT) {
                                yEnd = TILEHEIGHT - (TOP_INFOHEIGHT - yStart);  // cut off at bottom
                                yStart = TOP_INFOHEIGHT;
                            } else {
                                yEnd = TILEHEIGHT;
                            }
                            
//#if Series40_MIDP2_0
//#                             //if(!playfieldImageValid) {
//#                                 if(DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT - yCounter < TILEHEIGHT)
//#                                     yEnd = DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT - yCounter;
//#else
                                if(DISPLAYHEIGHT + TOP_INFOHEIGHT - yCounter < TILEHEIGHT)
                                    yEnd = DISPLAYHEIGHT + TOP_INFOHEIGHT - yCounter;
//#endif

                                currentGraphics.setClip(xStart, yStart, xEnd, yEnd);

                                l = (legacyPlayfield[w][h] & 15); //extract graphic id
                                //draw image
                                if((legacyPlayfield[w][h] & 128)==128 && dynamic != null) {
                                    currentGraphics.drawImage(dynamic, xCounter - TILEWIDTH*(l%TILES), yCounter - TILEHEIGHT*(l/TILES), anchorTopLeft);
                                } else {
                                    currentGraphics.drawImage(background, xCounter - TILEWIDTH*(l%TILES), yCounter - TILEHEIGHT*(l/TILES), anchorTopLeft);
                                }

// -- FIRE WALL START                                
                                d11 = w - cellWindow_XStart;    // firewall x index of current tile
                                d12 = h - cellWindow_YStart;    // firewall y index of current tile
                                
                                //System.out.println("xENd " + xEnd + ", yEnd " + yEnd);
                                
                                // draw firewall if applicable
                                if (d11 >= 0 && d12 >= 0 && d11 < FIREWALL_WINDOWSIZE && d12 < FIREWALL_WINDOWSIZE) {
                                   
                                    d9 = fireWalls[d11][d12];
                                    
                                    d3 = d9 & 15;  // extract the time, 4 lower bits ----tttt
                                    
                                    if (d3 > 0) {
                                        
                                        d4 = ((d9 & 192) >> 6) + 2;  // extract the value class, 2 higher bits vv------
                                        l = (d9 & 48) >> 4;  // extract the display offset, 2 medium bits --dd----
                                        
                                        //System.out.print(" (" + d11 + "," + d12 + "):" + fireWalls[d11][d12]);
                                        // a firewall is active at this tile
                                        // get x startPlay of available fire wall canvas within tile
                                        d7 = 0;
                                        if (xEnd < TILEWIDTH && xStart == 0) {  // tile is cut off on left
                                                d7 = TILEWIDTH - xEnd;
                                        }
                                        // get y startPlay of available fire wall canvas within tile
                                        d8 = 0;
                                        if (yEnd < TILEHEIGHT && yStart == TOP_INFOHEIGHT) {  // tile is cut off on top
                                                d8 = TILEHEIGHT - yEnd;
                                        }
                                        
                                        d2 = fireAniPhase * 7;

                                        for (d1=0; d1<d4; d1++) {
                                            d5 = fireWallDisplayOffsets[d1][0]; // fire xPos in tile
                                            d6 = fireWallDisplayOffsets[d1][1]; // fire yPos in tile
                                            if (d1 != 0 && d1 < 3) {d5 += l;}
                                            if (d1 != 1  && d1 < 3) {d6 += l;} else {d6 -= l;}
                                            currentGraphics.setClip(xStart-d7+d5, yStart-d8+d6, 7, 7);
                                            currentGraphics.drawImage(GlobalResources.imgIngame, xStart-d7+d5 - d2, yStart-d8+d6 - 60, anchorTopLeft);
                                            
                                        }
                                        
                                    }
                                }

// -- FIRE WALL END                                
                                
                                
//#if Series40_MIDP2_0
//#                             //}
//#endif
                            
                            // check for trigger at this tile
                            if ((legacyPlayfield[w][h] & 64) ==64) {
                                // draw trigger flash hint
                                drawTriggerFlash(xCounter, yCounter);
                            }
                            
                            
                            
                            xCounter+=TILEWIDTH;
                            k++;
                        }
                        i++;

                        yCounter+=TILEHEIGHT;
                    }

// -- SPECIAL TRIGGER START                                
                    yCounter = -(yPos%TILEHEIGHT) + TOP_INFOHEIGHT;
                    xCounter = -(xPos%TILEWIDTH);
                    xStart = xPos / TILEWIDTH;
                    yStart = yPos/ TILEHEIGHT;
                    xEnd = (xPos + DISPLAYWIDTH) / TILEWIDTH;
//#if Series40_MIDP2_0
//#                     yEnd = (yPos + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT) / TILEHEIGHT;
//#else
                    yEnd = (yPos + DISPLAYHEIGHT) / TILEHEIGHT;
//#endif
                    

                    if (specialFields!=null) {
                        d4 = specialFields.length;
                        for (d1=0; d1<d4; d1++) {
                            if (specialFields[d1]!=null && specialFields[d1][2] >= 553 
                            ) {
                                m = specialFields[d1][0];
                                n = specialFields[d1][1];
                                
                                if (    m >= xStart && m <= xEnd
                                    &&  n >= yStart && n <= yEnd) 
                                {
                                    m = ((m - xStart) * TILEWIDTH) + xCounter; 
                                    n = ((n - yStart) * TILEHEIGHT) + yCounter;
                                    currentGraphics.setClip(m, n, 24, 24);
                                    currentGraphics.drawImage(special, m-(specialFields[d1][2]-553), n, anchorTopLeft);
                                    drawTriggerFlash(m, n);
                                }
                            }
                            
                        }
                    }
// -- SPECIAL TRIGGER END

                    
                    // draw dead bodies
                    checkDisplayDeadCharacters();
*/
                    //
                    // Draw Items
                    //
/*
                    Image itemImage;
                    Enumeration e = idToItems.elements();
                    while(e.hasMoreElements()) {
                        itemTmpD = (Item)e.nextElement();

                        // Draw Items
                        if(itemTmpD!=null) {
                            xStart = itemTmpD.x - xPos;
                            yStart = itemTmpD.y - yPos + TOP_INFOHEIGHT;

                            // only draw visible items
                            if(xStart + ITEMWIDTH >= 0 && yStart + ITEMHEIGHT >= TOP_INFOHEIGHT) {
//#if Series40_MIDP2_0
//#                                 if(xStart < DISPLAYWIDTH && yStart < DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT) {
//#else
                                if(xStart < DISPLAYWIDTH && yStart < DISPLAYHEIGHT + TOP_INFOHEIGHT) {
//#endif
                                    xEnd = ITEMWIDTH;
                                    if(DISPLAYWIDTH - xStart < ITEMWIDTH)
                                        xEnd = DISPLAYWIDTH - xStart;
                                    yEnd = ITEMHEIGHT;
//#if Series40_MIDP2_0
//#                                     if(DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT - yStart < ITEMHEIGHT)
//#                                         yEnd = DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT - yStart;
//#else
                                    if(DISPLAYHEIGHT + TOP_INFOHEIGHT - yStart < ITEMHEIGHT)
                                        yEnd = DISPLAYHEIGHT + TOP_INFOHEIGHT - yStart;
//#endif
                                    xCounter = xStart;
                                    if(xCounter < 0) {
                                        xEnd += xStart;
                                        xCounter = 0;
                                    }
                                    yCounter = yStart;
                                    if(yCounter < 0) {
                                        yEnd += yStart;
                                        yCounter = 0;
                                    }

                                    currentGraphics.setClip(xCounter, yCounter, xEnd, yEnd);
                                    if(itemTmpD.graphicsel == 1) {
                                        itemImage = items[1];
                                    } else {
                                        itemImage = items[0];
                                    }
                                    currentGraphics.drawImage(itemImage, xStart - (itemTmpD.graphicsX*DIM), yStart - (itemTmpD.graphicsY*DIM), anchorTopLeft);

                                }
                            }
                        }
                    }
                    
*/
                    //
                    // Draw the others
                    //
/*
                    e = idToCharacters.elements();
                    while(e.hasMoreElements()) {
                        characterTmpD = (Character)e.nextElement();
                        if (characterTmpD!=null && characterTmpD.objectId != character_DB_ID) {
                            drawOtherCharacter(characterTmpD, true);
                            if (characterTmpD.attackShowDuration > 0) {
                                drawAttackAnimation(characterTmpD);
                            }
                            checkSpellVisualsForCharacter(characterTmpD);
                            checkDisplayTextForObject(characterTmpD, characterTmpD.msgDisplayName);
                        }
                    }
                    characterTmpD = null;
*/
                    
                    
                    // Draw the player
                    //
/*
                    if(playerObject != null) {
                        if (checkDisplayIconForObject(playerObject, true)!=-1) {
                            currentGraphics.setClip(playerScreenX, playerScreenY, PLAYERWIDTH, PLAYERHEIGHT);
                            if (players!=null) {
                                //currentGraphics.drawImage(players[playerObject.subclassId], playerScreenX - (TILEWIDTH*playerAnim), playerScreenY - (TILEHEIGHT*playerDirection), anchorTopLeft);
                                currentGraphics.drawImage(players, playerScreenX - (playerObject.graphicsX*DIM) - (2*PLAYERWIDTH*playerDirection) - (PLAYERWIDTH*playerAnim), playerScreenY - (playerObject.graphicsY*DIM), anchorTopLeft);
                            }
                            drawHealthManaState(playerObject, curGametime, 2);
                        }
                        
                        checkSpellVisualsForCharacter(playerObject);
                        checkDisplayTextForObject(playerObject, true);
                    }
*/

                    if (peacefulDisplayTime > 0) {
                        GTools.drawWindow(currentGraphics, info1Line2, true);
                    } else if (peacefulDisplay == 1) {
                        // draw small P in lower right corner
                        currentGraphics.setClip(DISPLAYWIDTH - (font.charWidth + 5), DISPLAYHEIGHT + TOP_INFOHEIGHT - (font.charHeight + 5), font.charWidth + 5, font.charHeight + 5);
                        if (flash) {
                            currentGraphics.setColor(0, 102, 0);
                        } else {
                            currentGraphics.setColor(0, 144, 0);
                        }
                        currentGraphics.fillRect(DISPLAYWIDTH - (font.charWidth + 5), DISPLAYHEIGHT + TOP_INFOHEIGHT - (font.charHeight + 5), font.charWidth + 5, font.charHeight + 5);
                        currentGraphics.setColor(0, 0, 0);
                        currentGraphics.drawRect(DISPLAYWIDTH - (font.charWidth + 5), DISPLAYHEIGHT + TOP_INFOHEIGHT - (font.charHeight + 5), font.charWidth + 4, font.charHeight + 4);
                        font.drawChar(currentGraphics, 'P', DISPLAYWIDTH - (font.charWidth + 5) + 3, DISPLAYHEIGHT + TOP_INFOHEIGHT - (font.charHeight + 5) + 3);
                    }
                    
                    // bottom info
                    if (this.bottomInfo_DisplayTime > 0 && !bottomInfo_Foreground) {
                        GTools.drawWindow(currentGraphics, bottomInfoWindow, false);
                    }

                    if (xpInfoShowDuration > 0 && (currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND)) {
                        if (currentSubState == SUBSTATE_GROUND_FIND) {
                            d2 = TOP_INFOHEIGHT;
                        } else {
                            d2 = TOP_INFOHEIGHT + 10;
                        }
                        currentGraphics.setClip(DISPLAYWIDTH - 44, d2, 44, 9);
                        currentGraphics.setColor(0,0,0);
                        currentGraphics.fillRect(DISPLAYWIDTH - 44, d2, 44, 9);
                        font.drawString(currentGraphics, xpInfo, DISPLAYWIDTH - 42, d2 + 2);
                    }
                    /*
                    if (currentSubState == SUBSTATE_GROUND_FIND) {
                        // draw ground cursor
                        d1 = xPos / TILEWIDTH; // top left cell X (cellnum)
                        d2 = yPos / TILEHEIGHT;  // top left cell Y (cellnum)
                        d3 = xPos % TILEWIDTH; // left cutoff of top left cell (pixels)
                        d4 = yPos % TILEHEIGHT;  // right cutoff of top left cell (pixels)
                        
                        d5 = groundCursorX - d1; // cell distance X from top left to cursor X
                        d6 = groundCursorY - d2; // cell distance Y from top left to cursor Y
                        
                        d7 = (d5 * TILEWIDTH) - d3;    // startPlay pos X of cursor rectangle
                        d8 = TOP_INFOHEIGHT + (d6 * TILEHEIGHT) - d4;   // startPlay pos Y of cursor rectangle

                        // orange if cell is blocked and fire wall selection
                        if (triggerTarget_TriggerType == 76 && ((legacyPlayfield[groundCursorX][groundCursorY] & 32) == 32 || (legacyPlayfield[groundCursorX][groundCursorY] & 16) == 16))  {
                            currentGraphics.setColor(255, 204, 0);
                        } else {
                            currentGraphics.setColor(0, 255, 0);
                        }

                        d1 = d7 + 11;
                        d2 = d8 + 11;
                        d3 = d1 - 4;
                        d4 = d2 - 4;
                        
                        if (flash) {
                            currentGraphics.setClip(d7, d8, TILEWIDTH, TILEHEIGHT);
                            // plus sign within cursor
                            currentGraphics.drawLine(d1, d4, d1, d4+9);
                            currentGraphics.drawLine(d3, d2, d3+9, d2);
                            currentGraphics.drawLine(d1+1, d4, d1+1, d4+9);
                            currentGraphics.drawLine(d3, d2+1, d3+9, d2+1);

                        }

                        
                        if (triggerTarget_TriggerType > 76) {
                            // mass action selection
                            //currentGraphics.setStrokeStyle(Graphics.DOTTED);
                            
                            d1 -= 51;
                            d2 -= 51;
                            d3 = 101;
                            
                            currentGraphics.setClip(d1, d2, d3 + 1, d3 + 1);
                            
                            // outer strokes
                            currentGraphics.drawArc(d1, d2, d3, d3, 10, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 100, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 190, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 280, 70);
                            
                            d1 += 1;
                            d2 += 1;
                            d3 = 99;
                            currentGraphics.drawArc(d1, d2, d3, d3, 10, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 100, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 190, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 280, 70);
                            
                            // inner strokes
                            if (weaponRechargeStartTime > 0) {currentGraphics.setColor(128,192,128);}

                            d1 += 17;
                            d2 += 17;
                            d3 = 66;
                            
                            currentGraphics.drawArc(d1, d2, d3, d3, 10, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 100, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 190, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 280, 70);
                            
                            d1 += 1;
                            d2 += 1;
                            d3 = 64;
                            currentGraphics.drawArc(d1, d2, d3, d3, 10, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 100, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 190, 70);
                            currentGraphics.drawArc(d1, d2, d3, d3, 280, 70);
                            
                            
                            
                            // inner strokes
                            //currentGraphics.drawArc(d1 - 50, d2 - 50, 99, 99, 0, 360);

                            

                            //currentGraphics.setStrokeStyle(Graphics.SOLID);
                        } else {
                            // fire wall selection
                        
                            d9 = d7 + TILEWIDTH-1;
                            d10 = d8 + TILEHEIGHT-1;


                            currentGraphics.setClip(d7, d8, TILEWIDTH, TILEHEIGHT);

                            // outer strokes
                            currentGraphics.drawLine( d7, d8, d7 + 4, d8);  //topleft h
                            currentGraphics.drawLine( d9 - 4, d8, d9, d8);  //topright h
                            currentGraphics.drawLine( d7, d8, d7, d8 + 4);  //topleft v
                            currentGraphics.drawLine( d9, d8, d9, d8 + 4);  //topright v
                            currentGraphics.drawLine( d7, d10, d7 + 4, d10);    //bottomleft h
                            currentGraphics.drawLine( d9 - 4, d10, d9, d10);    //bottomright h
                            currentGraphics.drawLine( d7, d10-4, d7, d10);  //bottomleft v
                            currentGraphics.drawLine( d9, d10-4, d9, d10);  //bottomright v

                            // inner strokes
                            if (weaponRechargeStartTime > 0) {currentGraphics.setColor(128,192,128);}
                            currentGraphics.drawLine( d7+1, d8+1, d7 + 4, d8+1);  //topleft h
                            currentGraphics.drawLine( d9 - 4, d8+1, d9-1, d8+1);  //topright h
                            currentGraphics.drawLine( d7+1, d8+1, d7+1, d8 + 4);  //topleft v
                            currentGraphics.drawLine( d9-1, d8+1, d9-1, d8 + 4);  //topright v
                            currentGraphics.drawLine( d7+1, d10-1, d7 + 4, d10-1);    //bottomleft h
                            currentGraphics.drawLine( d9 - 4, d10-1, d9-1, d10-1);    //bottomright h
                            currentGraphics.drawLine( d7+1, d10-4, d7+1, d10-1);  //bottomleft v
                            currentGraphics.drawLine( d9-1, d10-4, d9-1, d10-1);  //bottomright v
                        }
                       

                        itemTmpD = invItems[selectedInvItem];
                        drawItemInUse(0);
                        itemTmpD = null;
                        isInWeaponRange = true;
                        drawWeaponRecharge();
                    }
                     */
                    /*
                    if (currentSubState == SUBSTATE_TALKTO_FIND || currentSubState == SUBSTATE_TRADE_FIND || currentSubState == SUBSTATE_FIGHT_FIND || currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_FIGHT_ACTIVE || currentSubState == SUBSTATE_FRIEND_FIND) {
                        if (currentSubState == SUBSTATE_FIGHT_ACTIVE || currentSubState == SUBSTATE_TRIGGERTARGET_FIND) {
                            characterTmpD = playersOnScreen[selectedPlayer];
                            
                            int factor = 1;
                            if (currentSubState == SUBSTATE_FIGHT_ACTIVE) {
                                itemTmpD = equipment[WEAPON];   // normal weapon attackBase range
                            } else {
                                // %%%
                     
                                //if (currentSubState == SUBSTATE_BELT) {
                                //    itemTmpD = belt[selectedBeltItem];
                                //}
                                 
                                factor = 3; // spell attackBase range
                                itemTmpD = invItems[selectedInvItem];
                                // note above statement is ok also for belt selection because the selected belt item will force the correct item in the inventory to be selected
                                drawItemInUse(info1Line.height);
                                itemTmpD = null; // set item null for correct cursor display
                            }
                            
                            if (characterTmpD!=null && isInWeaponRange(characterTmpD, itemTmpD, factor)) {
                                isInWeaponRange = true;
                            } else {
                                isInWeaponRange = false;
                            }
                            if (selectedPlayer < 0 || !objectInsideView(playersOnScreen[selectedPlayer], 28) ) {
                                info1Line_DisplayTime = 3000;
                                GTools.textWindowSetText(info1Line, "Target too far away.");
                                subStateNormal();
                            } else {
                                drawWeaponRecharge();                            
                            }
                            itemTmpD = null;
                        }
                        drawSelectedPlayerCursor();
                        
                        GTools.drawWindow(currentGraphics, info1Line, true);
                    } else if (info1Line_DisplayTime > 0) {
                        GTools.drawWindow(currentGraphics, info1Line, true);
                    }
                    */
//#if Series40_MIDP2_0
//#                     
//#                     // draw experience bar
//#                     if (experiencePlusForNextLevel > 0 && experienceCurOffset <= experiencePlusForNextLevel) {
//#                         currentGraphics.setClip(0, 0, DISPLAYWIDTH, series40_TOP_INFOHEIGHT);
//#                         currentGraphics.setColor(0,0,0);
//#                         currentGraphics.fillRect(0, 0, DISPLAYWIDTH, series40_TOP_INFOHEIGHT);
//#                         currentGraphics.setColor(153,153,255);
//#                         currentGraphics.fillRect(1, 1, (experienceCurOffset * (DISPLAYWIDTH-1)) / experiencePlusForNextLevel, 2);
//#                     }
//#else
                    
                    // draw experience bar
                    currentGraphics.setClip(0, TOP_INFOHEIGHT-4, DISPLAYWIDTH, 4);
                    currentGraphics.setColor(0,0,0);
                    currentGraphics.fillRect(0, TOP_INFOHEIGHT-4, DISPLAYWIDTH, 4);

                    if (experiencePlusForNextLevel > 0 && experienceCurOffset <= experiencePlusForNextLevel) {
                        currentGraphics.setColor(153,153,255);
                        currentGraphics.fillRect(1, TOP_INFOHEIGHT-4 + 1, (experienceCurOffset * (DISPLAYWIDTH-1)) / experiencePlusForNextLevel, 2);
                    }
//#endif
                    
                    //draw top frame
                    //drawPlayerStatusTop();
                    
                    GTools.restoreGraphicsSettings(currentGraphics);
    
                    
                    switch(currentSubState) {
                        case SUBSTATE_NORMAL:
                            break;
                        case SUBSTATE_ACTIONMENU:
                            //GTools.drawWindow(currentGraphics, menuActionSub, true);
                            drawActionMenu();
                            GTools.drawWindow(currentGraphics, info1Line, true);
                            break;
                        case SUBSTATE_INVENTORY:
                            drawInventory(TOP_INFOHEIGHT, true, true);
                            break;
                        case SUBSTATE_INVITEM_OPTIONS:
                            drawInventory(TOP_INFOHEIGHT, false, true);
                            GTools.drawWindow(currentGraphics, menuFreeContextOptions, false);
                            break;
                        case SUBSTATE_SET_ITEMOFFER:
                            drawInventory(TOP_INFOHEIGHT, false, true);
                            GTools.drawWindow(currentGraphics, priceBox, false);
                            break;
                        case SUBSTATE_SET_DROPITEM_AMOUNT:
                            drawInventory(TOP_INFOHEIGHT, false, true);
                            GTools.drawWindow(currentGraphics, dropItemBox, false);
                            break;
                        case SUBSTATE_BELT_SELECT_SLOT:
                            drawInventory(TOP_INFOHEIGHT, false, false);
                            drawBelt(1, false);
                            break;
                        case SUBSTATE_BELT:
                            drawBelt(2, true);
                            break;
                        case SUBSTATE_TALK_SUBOPTIONS:
                            if (talkSubMenu_ShowActionMenu) {
                                drawActionMenu();
                            }
                            GTools.drawWindow(currentGraphics, menuActionSub, false);
                            break;
                        case SUBSTATE_FRIEND_FIND:
                            if (curGametime - lastCheck > 4000) {
                                // only check on players
                                getPlayersOnScreen(false, 28, false, 0);
                                lastCheck = curGametime;
                            }
                            break;
                        case SUBSTATE_TALKTO_FIND:
                            if (checkKeepCharacterSelection(false, true)==null) {
                                info1Line_DisplayTime = 3000;
                                GTools.textWindowSetText(info1Line, "Target too far away.");
                                subStateNormal();
                            }
                            break;
                        case SUBSTATE_TRADE_FIND:
                            if (curGametime - lastCheck > 4000) {
                                getPlayersOnScreen(false, 28, false, -1);
                                lastCheck = curGametime;
                            }
                            break;
                        case SUBSTATE_TRIGGERTARGET_FIND:
                            if (curGametime - lastCheck > 4000) {
                                getPlayersOnScreen(false, 28, false, -1);
                                lastCheck = curGametime;
                            }
                            break;
                        
                        case SUBSTATE_FIGHT_FIND:
                        case SUBSTATE_FIGHT_ACTIVE:
                            int curId = playfieldView.getSelectedCharacterId();
                            if (checkKeepCharacterSelection(true, true)==null) {
                                info1Line_DisplayTime = 3000;
                                GTools.textWindowSetText(info1Line, "Target too far away.");
                                subStateNormal();
                            } else {
                                if (curId != playfieldView.getSelectedCharacterId()) {
                                    // selected character changed - switch to find mode in either case
                                    setBottomCommand1("Sel. Target");
                                    setBottomCommand2("Back");
                                    currentSubState = SUBSTATE_FIGHT_FIND;
                                }
                                if (currentSubState == SUBSTATE_FIGHT_ACTIVE) {
                                    boolean active = playfieldView.attackPossible(playfieldView.getSelectedCharacter(), false);
                                    playerObject.drawRechargeForAttack(currentGraphics, 2, TOTALHEIGHT - BOTTOM_INFOHEIGHT - 30, 8, 28, active);
                                }
                            }
                            break;
                            
                            
                        case SUBSTATE_TRADE_BUY_CONFIRM:
                        case SUBSTATE_TRADE_REQUEST:
                            // todo draw other inventory
                            drawInventory(TOP_INFOHEIGHT, true, false);
                            break;
                        case SUBSTATE_TALKTO: 
                            GTools.drawWindow(currentGraphics, menuChat, true);
                            break;
                        case SUBSTATE_TALKTOALL:
                            GTools.drawWindow(currentGraphics, menuChat, true);
                            break;
                        case SUBSTATE_TALKINPUT_OPTIONS:
                            GTools.drawWindow(currentGraphics, menuChat, true);
                            GTools.drawWindow(currentGraphics, menuContextOptions, true);
                            break;
                        case SUBSTATE_FRIEND_REQUEST_LIST:
                        case SUBSTATE_EVENT_LIST:
                            GTools.drawWindow(currentGraphics, menuList, true);
                            break;
                        case SUBSTATE_FRIEND_REQUEST_LIST_OPTIONS:
                        case SUBSTATE_EVENT_LIST_OPTIONS:
                            GTools.drawWindow(currentGraphics, menuList, true);
                            GTools.drawWindow(currentGraphics, menuContextOptions, true);
                            break;
                        case SUBSTATE_FRIEND_LIST_OPTIONS:
                            GTools.drawWindow(currentGraphics, menuBigList, true);
                            GTools.drawWindow(currentGraphics, menuContextOptions, true);
                            break;
                        case SUBSTATE_FRIEND_REQUEST_ACCEPT_CONFIRM:
                            GTools.drawWindow(currentGraphics, confirmWindow, true);
                            break;
                        case SUBSTATE_FRIENDSHIP_CANCEL_CONFIRM:
                            GTools.drawWindow(currentGraphics, menuBigList, true);
                            GTools.drawWindow(currentGraphics, confirmWindow, true);
                            break;
                        case SUBSTATE_TRADE_TRANSFER_WAIT:
                        case SUBSTATE_FAR_PORTAL_WAIT:
                            //GTools.drawWindow(currentGraphics, menuTrade, true);
                            GTools.drawWindow(currentGraphics, labelWait, true);
                            break;
                        case SUBSTATE_FAR_PORTAL_LIST:
                        case SUBSTATE_FRIEND_LIST:
                        case SUBSTATE_CHAT_SHORTCUT_SELECT:
                        case SUBSTATE_CHAT_SHORTCUT_EDIT:
                            GTools.drawWindow(currentGraphics, menuBigList, true);
                            break;
                        case SUBSTATE_CHAT_SHORTCUT_EDIT_DETAIL:
                            GTools.drawWindow(currentGraphics, menuBigList, true);
                            GTools.drawWindow(currentGraphics, editBox, true);
                            break;
                        case SUBSTATE_BUILDCHARACTER:
                            drawCharacterBuildScreen();
                            break;
                        case SUBSTATE_DIALOGUE_INIT:
                            GTools.drawWindow(currentGraphics, menuClientphrases, true);
                            GTools.drawWindow(currentGraphics, botphraseWindow, true);
                            drawDialogueLoadProgress();
                            break;
                        case SUBSTATE_DIALOGUE_ACTIVE:
                            GTools.drawWindow(currentGraphics, menuClientphrases, true);
                            GTools.drawWindow(currentGraphics, botphraseWindow, true);
                            drawDialogueLoadProgress();
                            break;
                        default:
                            break;
                        case SUBSTATE_QUEST_OVERVIEW:
                            GTools.drawWindow(currentGraphics, menuQuests, true);
                            break;
                        case SUBSTATE_QUEST_OVERVIEW_OPTIONS:
                            GTools.drawWindow(currentGraphics, menuQuests, true);
                            GTools.drawWindow(currentGraphics, menuContextOptions, true);
                            break;
                        case SUBSTATE_QUEST_REQUESTDETAILS:
                            GTools.drawWindow(currentGraphics, menuQuests, true);
                            GTools.drawWindow(currentGraphics, labelWait, true);
                            break;
                        case SUBSTATE_QUEST_DELETE_WAIT:
                            GTools.drawWindow(currentGraphics, menuQuests, true);
                            GTools.drawWindow(currentGraphics, labelWait, true);
                            break;
                        case SUBSTATE_QUEST_DETAILS:
                            GTools.drawWindow(currentGraphics, menuQuests, true);
                            break;
                        case SUBSTATE_FRIEND_SUBOPTIONS:
                            drawActionMenu();
                            GTools.drawWindow(currentGraphics, menuActionSub, false);
                            break;
                            
                    }
                    
                    // bottom info foreground
                    if (this.bottomInfo_DisplayTime > 0 && bottomInfo_Foreground) {
                        GTools.drawWindow(currentGraphics, bottomInfoWindow, false);
                    }

                    drawPlayerStatusTop();
                    
//#if !(Series40_MIDP2_0)
                    //bottom frame always visible in game mode
                    drawBottomFrame();
//#endif
                    
                    if (blockDuration > 0) {
                        currentGraphics.setClip(blockTriggerX, blockTriggerY, 6, 11);
                        currentGraphics.drawImage(GlobalResources.imgIngame, blockTriggerX - 41, blockTriggerY - 12, anchorTopLeft);   // blocking trigger flash
                    }


                    

                } //STATE_GAME if images.. != null
                
                break;
            
            
            
            
            case STATE_INTRO:
                if(background != null) {
                    currentGraphics.drawImage(background, DISPLAYWIDTH/2-(background.getWidth()/2), (TOTALHEIGHT)/2-(background.getHeight()/2), anchorTopLeft);
                }
                if (currentSubState == SUBSTATE_ACTIVE) {
                    GTools.drawWindow(currentGraphics, comButton2, true);
                }
                if (label3!=null) {
                    GTools.drawWindow(currentGraphics, label3, true);
                }
                
                break;

            case STATE_INTRO_EULA:
                 GTools.drawWindow(currentGraphics, eulaWindow, true);
                 break;

            case STATE_INTRO_LIST:
            case STATE_RECOVER_PASSWORD_MAIN_OPTIONS:
                if (currentSubState==SUBSTATE_NORMAL) {
                    GTools.drawWindow(currentGraphics, menuList, true);
                }
                break;


            case STATE_DEFINE_KEYS:
                GTools.drawWindow(currentGraphics, phoneTemplate, true);
                switch (currentSubState) {
                    case SUBSTATE_DEFINE_KEY_SK1:
                        if (flash) {
                            currentGraphics.setColor(0xffcc00);
                            currentGraphics.drawRect(phoneTemplate.x + 1, phoneTemplate.y + 36, 11, 5);
                        }
                        GTools.drawArrow(currentGraphics, GTools.POSITION_BOTTOM, 10, TOTALHEIGHT-10, 5, 0xffcc00);
                        break;
                    case SUBSTATE_DEFINE_KEY_SK2:
                        if (flash) {
                            currentGraphics.setColor(0xffcc00);
                            currentGraphics.drawRect(phoneTemplate.x + 20, phoneTemplate.y + 36, 11, 5);
                        }                        
                        GTools.drawArrow(currentGraphics, GTools.POSITION_BOTTOM, DISPLAYWIDTH-10, TOTALHEIGHT-10, 5, 0xffcc00);
                        break;
                }
                GTools.drawWindow(currentGraphics, labelWait, true);
                
                break;

            //this state lasts only several frames long (or until initNet() returns)
            //first, we wait few frames to make sure the wait label is displayed, then we actuall try to connect
            case STATE_PRE_CONNECT:
                
                //wait long enough to have the message displayed
                if(doConnect == 30) {
                    initNet();                   
                    currentState = STATE_WAIT_FOR_CONNECT_THREAD;
                } else {
                    GTools.drawWindow(currentGraphics, labelWait, true);
                }
                
                doConnect++;
                
                break;
            case STATE_WAIT_FOR_CONNECT_THREAD:
                GTools.drawWindow(currentGraphics, labelWait, true);
                int cStatus = gbManager.connectFinished();
                
                switch(cStatus) {
                    case  1:
                        netStarted = true;
                        checkNet = true;
                        database.setValue("host", host);
                        // everything ok
                        currentState = STATE_CONNECT;
                        doConnect = 0;
                        break;
                    case -1:
                        netError = true;
                        database.setValue("host", defaultHost);
                        host =  defaultHost;
                        // network down
                        onConnectError();
                        break;
                    case  0:
                        // unknown status, do nothing and wait
                       break;
                }
                
  
                break;
            case STATE_WAIT_FOR_CONNECT_THREAD_PORTAL:
                GTools.drawWindow(currentGraphics, labelWait, true);
                switch(gbManager.connectFinished()) {
                    case  1:
                        netStarted = true;
                        checkNet = true;
                        database.setValue("host", host);
                        // everything ok
                        currentState = STATE_WAIT;
                        // do the login
                        loginSystem(clientName, clientPass);
                        break;
                    case -1:
                        if (!firstConnect) {
                            netError = true;
                            database.setValue("host", defaultHost);
                            host =  defaultHost;
                            // network down
                            currentState = STATE_BLACK;
                            bCommand1 = false;
                            setBottomCommand2("Exit");
                            subStateOKDialog("Server connection failed.\nExit.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                            checkNet = false;
                        } else {
                            doConnect = 0;
                            stopNet();
                            firstConnect = false;   // reset firstConnect value, so we try again, before we are convinced we cannot connect
                            // Start the net again
                            initNet();
                            currentState = STATE_WAIT_FOR_CONNECT_THREAD_PORTAL;
                        }
                        
                        
                        break;
                    case  0:
                        // unknown status, do nothing and wait
                       break;
                }
                
                break;
            case STATE_CONNECT:
                if (bServerList) {
                    // prepare generic list
                    GTools.listRemoveAllEntries(genericList);
                    currentState = STATE_CONNECT_GET_SERVERS;
                    setBottomCommand1("Connect");
                    setBottomCommand2("Game");
                    setMessageWaitTimeout(FWGMessageIDs.MSGID_GAME_SERVER_ENTRY, 45, STATE_INTRO, SUBSTATE_ACTIVE, "Connection failed!\nNetwork timeout.", "Connect", "Exit");
                    requestGameServers();  //get the servers which are available for playing
                } else {
                    this.setWaitLabelText("Checking version..");
                    currentState = STATE_CONNECT_CHECK_VERSION;
                    setMessageWaitTimeout(FWGMessageIDs.MSGID_GAME_VERSION, 45, STATE_FORCED_EXIT, SUBSTATE_NORMAL, "Connection failed!\nNetwork timeout.", "Connect", "Exit");
                    requestVersion();  //get the version which is required by the server
                }
                                
                break;
                
            case STATE_CONNECT_ERROR:
                break;
            
            case STATE_CONNECT_GET_SERVERS:
                if (currentSubState==SUBSTATE_NORMAL) {
                    GTools.drawWindow(currentGraphics, menuList, true);
                }
                break;

            case STATE_CONNECT_CHECK_VERSION:
                if (currentSubState==SUBSTATE_NORMAL) {
                    GTools.drawWindow(currentGraphics, labelWait, true);
                }
                break;

            case STATE_REGISTER_NEW:
                GTools.drawWindow(currentGraphics, menuLogin, true);
                if (passwordWindow.selected) {
                    GTools.drawWindow(currentGraphics, labelWait, true);
                }
                break;

            case STATE_EMAIL_ENTRY:
                switch (currentSubState) {
                    case SUBSTATE_NORMAL:
                        GTools.drawWindow(currentGraphics, menuEmail, true);
                        d1 = menuEmail.x  + (MIN_DISPLAYWIDTH / 2) - 5;
                        d2 = menuEmail.y + 47;
                        currentGraphics.setClip(d1, d2, 10, 10);
                        //currentGraphics.drawImage(GlobalResources.imgIngame, d1-51, d2, anchorTopLeft);    // @ sign
                        break;
                    case SUBSTATE_EMAIL_OPTIONS:
                        GTools.drawWindow(currentGraphics, menuEmail, true);
                        GTools.drawWindow(currentGraphics, menuContextOptions, true);
                        break;
                    case SUBSTATE_EMAIL_CHANGE_WAIT:
                        GTools.drawWindow(currentGraphics, labelWait, true);
                        break;
                }
                break;
            case STATE_GET_PASSWORD_RESET_CODE:
            case STATE_ENTER_NAME_FOR_RESET_CODE:
                GTools.drawWindow(currentGraphics, editBox, true);
                switch(currentSubState) {    
                    case SUBSTATE_RECOVER_PASSWORD_WAIT:
                        GTools.drawWindow(currentGraphics, labelWait, true);
                        break;
                }
                break;
                
            case STATE_ENTER_PASSWORD_RESET_CODE:
                GTools.drawWindow(currentGraphics, menuLogin, true);
                switch (currentSubState) {
                    case SUBSTATE_RECOVER_PASSWORD_WAIT:
                        GTools.drawWindow(currentGraphics, labelWait, true);
                        break;
                }
                break;

            case STATE_LOGIN_MENU:
                GTools.drawWindow(currentGraphics, menuLogin, true);
                break;
                
            case STATE_INVENTORY_LOAD_WAIT:
                GTools.drawWindow(currentGraphics, labelWait, true);

                int numItems = playerObject.inventory.numItems();
                if (numItems > 0) {
                    Item it = playerObject.inventory.getItemAt(numItems-1);
                    if (it != null) {
                        d4 = (DISPLAYWIDTH >> 1) - Item.DEFAULT_WIDTH;
                        d5 = labelWait.y + labelWait.height;
                        d5 = d5 + ((TOTALHEIGHT - d5) >> 1) - Item.DEFAULT_HEIGHT;
                        it.draw(currentGraphics, d4, d5);

                        /*
                        currentGraphics.setClip(d4, d5, ITEMWIDTH, ITEMHEIGHT);


                        currentGraphics.drawImage(itemImage,  d4 - ((itemTmpD.graphicsX*DIM)),
                                                  d5 -((itemTmpD.graphicsY*DIM)),
                                                  anchorTopLeft);
                                                  */
                    }
                }
                break;
                
                
            case STATE_WAIT:
            case STATE_FRIEND_RECEIVE_LIST_WAIT:
            case STATE_REGISTER_NEW_WAIT:
                GTools.drawWindow(currentGraphics, labelWait, true);
                break;
                
            case STATE_CHARACTER_SELECT:
                switch (currentSubState) {
                    case SUBSTATE_NORMAL:
                        GTools.drawWindow(currentGraphics, menuList, true);
                        break;
                    case SUBSTATE_CHARACTER_OPTIONS:
                        GTools.drawWindow(currentGraphics, menuList, true);
                        GTools.drawWindow(currentGraphics, menuContextOptions, true);
                        break;
                    case SUBSTATE_CHARACTER_NEW:
                        GTools.drawWindow(currentGraphics, menuList, true);
                        break;
                    case SUBSTATE_CHARACTER_NEW_NAME:
                        GTools.drawWindow(currentGraphics, editBox, true);
                        break;
                    case SUBSTATE_CHARACTER_DELETE_WAIT:
                    case SUBSTATE_CHARACTER_RENAME_WAIT:
                        GTools.drawWindow(currentGraphics, labelWait, true);
                        break;
                    case SUBSTATE_CHARACTER_DELETE_CONFIRM:
                        //GTools.drawWindow(currentGraphics, confirmWindow, true);
                        break;
                    case SUBSTATE_CHARACTER_RENAME:
                        GTools.drawWindow(currentGraphics, editBox, true);
                        break;
                    case SUBSTATE_LOAD_CHR_GFX:
                        /*doConnect++;
                        GTools.drawWindow(currentGraphics, labelWait, true);
                        if (doConnect > 30) {
                            doConnect = 0;
                            players = getImage("players.png", true);
                            currentSubState = SUBSTATE_NORMAL;
                            setBottomCommand1("Options");
                            prepareContextMenu(0);
                            addCharacterOK = true;
                            initGraphics();
                            initWindows();
                            sendRequestCharactersMessage(user_DB_ID);
                        }*/
                        break;
                        
                }
                break;
            case STATE_LOGIN_ERROR:
            case STATE_REGISTER_OK:
                break;

            case STATE_GAUGE:
                drawLoadingWorld();
                break;
                
            case STATE_WAIT_LOAD_GFX:
                drawLoadingWorld();
                break;

                
            case STATE_RESPAWN_REQUEST:
                sendRequestRespawn();
                setWaitLabelText("Reviving..");
                currentState = STATE_RESPAWN_WAIT;
                break;
            case STATE_RESPAWN_WAIT:
                GTools.drawWindow(currentGraphics, labelWait, true);
                break;  
                
            case STATE_SUBSCRIBE_NEW:
                GTools.drawWindow(currentGraphics, editBox, true);
                if (currentSubState != SUBSTATE_OK_DIALOG) {
                    GTools.drawWindow(currentGraphics, label2, true);
                }
                break;
                
            case STATE_SUBSCRIBE_OPTIONS:
                GTools.drawWindow(currentGraphics, editBox, true);
                GTools.drawWindow(currentGraphics, menuContextOptions, true);
                break;
            
            case STATE_SUBSCRIBE_WAIT_FOR_RESPONSE:
            case STATE_SUBSCRIBE_EXIT_WAIT_FOR_MSG:
                GTools.drawWindow(currentGraphics, labelWait, true);
                break;
                
        } // END SWITCH CURRENTSTATE
        //if (currentSubState==SUBSTATE_OK_DIALOG) {
            //GTools.drawWindow(currentGraphics, infoWindow, true);
        //}  
        if (currentSubState == SUBSTATE_PORTAL_WAIT) {
            GTools.drawWindow(currentGraphics, labelWait, true);
        }
        

//#if MIDP_2_0_GENERIC_KEYS
//#         if (currentState!=STATE_FORCED_EXIT && !confirmOK && !confirmYesNo && overlayState==OVERLAY_NONE && optionState == OPTIONSTATE_NONE) {
//#else
        if (currentState!=STATE_INTRO && currentState!=STATE_FORCED_EXIT && !confirmOK && !confirmYesNo && overlayState==OVERLAY_NONE && optionState == OPTIONSTATE_NONE) {
//#endif
                this.drawBottomCommands();
        }
        
        //draw yes / no message box to confirm exit
        if (confirmOK && overlayState==OVERLAY_NONE && optionState == OPTIONSTATE_NONE) {
            GTools.drawWindow(currentGraphics, confirmWindow, true);
            GTools.drawWindow(currentGraphics, OKButton, true);
        } else if (confirmYesNo && overlayState==OVERLAY_NONE && optionState == OPTIONSTATE_NONE) {
            GTools.drawWindow(currentGraphics, confirmWindow, true);
            GTools.drawWindow(currentGraphics, YESButton, true);
            GTools.drawWindow(currentGraphics, NOButton, true);
        }

        if (optionState != OPTIONSTATE_NONE) {
            switch (optionState) {
                case OPTIONSTATE_EMAIL_ENTRY:
                        switch (optionSubState) {
                            case OPTIONSUBSTATE_NONE:
                                GTools.drawWindow(currentGraphics, menuEmail, true);
                                d1 = menuEmail.x  + (MIN_DISPLAYWIDTH / 2) - 5;
                                d2 = menuEmail.y + 47;
                                currentGraphics.setClip(d1, d2, 10, 10);
                                //currentGraphics.drawImage(GlobalResources.imgIngame, d1-51, d2, anchorTopLeft);    // @ sign
                                break;
                            case OPTIONSUBSTATE_EMAIL_OPTIONS:
                                GTools.drawWindow(currentGraphics, menuEmail, true);
                                GTools.drawWindow(currentGraphics, menuContextOptions, true);
                                break;
                            case OPTIONSUBSTATE_EMAIL_CHANGE_WAIT:
                            case OPTIONSUBSTATE_EMAIL_GET:
                                GTools.drawWindow(currentGraphics, labelWait, true);
                                break;
                        }
                        break;
            }
            if (overlayState == OVERLAY_NONE) {
                if (opCommand1) {
                    GTools.drawWindow(currentGraphics, optionButton1, true);
                }
                if (opCommand2) {
                    GTools.drawWindow(currentGraphics, optionButton2, true);
                }
            }
        }


        if (overlayState != OVERLAY_NONE) {
            db1 = false;
            switch(overlayState) {
                case OVERLAY_MESSAGE:
                    GTools.drawWindow(currentGraphics, confirmWindow, true);
                    if (overlayControlsTimeOut > 0  && overlayControlsTimeOut < curGametime) {
                        overlayControlsTimeOut = 0;
                    } 
                    if (overlayControlsTimeOut <= 0) {
                        GTools.drawWindow(currentGraphics, OKButton, true);
                    }
                    break;
                case OVERLAY_GAMEOPTIONS:   // show game options menu
                    GTools.drawWindow(currentGraphics, menuGameOptions, true);
                    db1 = true;
                    break;
                case OVERLAY_CREDITS:
                    GTools.drawWindow(currentGraphics, creditsWindow, true);
                    db1 = true;
                    break;
                case OVERLAY_HELP:
                    GTools.drawWindow(currentGraphics, confirmWindow, true);
                    db1 = true;
                    break;
                case OVERLAY_HELP_WAIT:
                    GTools.drawWindow(currentGraphics, confirmWindow, true);
                    db1 = true;
                    break;
                case OVERLAY_SOUND:
                    GTools.drawWindow(currentGraphics, menuSound, true);
                    GTools.drawProgress(currentGraphics, gaugeSound, curSoundVolume, 100);
                    db1 = true;
                    break;
                case OVERLAY_DIED:
                    GTools.drawWindow(currentGraphics, confirmWindow, true);
                    db1 = true;
                    break;
            }
            if (db1) {
                if (ovCommand1) {
                    GTools.drawWindow(currentGraphics, overlayButton1, true);
                }
                if (ovCommand2) {
                    GTools.drawWindow(currentGraphics, overlayButton2, true);
                }
            }
            
        }
        

        
        if (showTraffic) {
            GTools.drawWindow(currentGraphics, labelTraffic, true);
        }
        
        /*if (showDebug1) {
            GTools.drawWindow(currentGraphics, labelDebug1, true);
        }
        if (showDebug2) {
            GTools.drawWindow(currentGraphics, labelDebug2, true);
        }
        if (showDebug3) {
            GTools.drawWindow(currentGraphics, labelDebug3, true);
        }*/
        if (showDebug4) {
            GTools.drawWindow(currentGraphics, labelDebug4, true);
        }
        if (showDebug5) {
            GTools.drawWindow(currentGraphics, labelDebug5, true);
        }
        if (showDebug6) {
            GTools.drawWindow(currentGraphics, labelDebug6, true);
        }
        
        
        // do the double buffering
        if (!isDoubleBuffered) {
            flip = !flip;
            if(flip) {
                currentGraphics = graphicsTwo;
            } else {
                currentGraphics = graphicsOne;
            }
        }
        
        //}catch(Exception e) {e.printStackTrace();System.out.println("      CATCHED EXCEPTION   ");}

    }

    private final void drawTriggerFlash(int xStart, int yStart) {
        xStart = xStart +(TILEWIDTH>>1) - 2;
        yStart = yStart +(TILEHEIGHT>>1) - 2;

//#if Series40_MIDP2_0
//#     if (xStart < DISPLAYWIDTH && yStart < TOP_INFOHEIGHT + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT) {
//#else
        if (xStart < DISPLAYWIDTH && yStart < TOP_INFOHEIGHT + DISPLAYHEIGHT) {
//#endif
            switch (curTriggerFlash) {
                case 1:
                    currentGraphics.setClip(xStart, yStart, 3, 3);
                    currentGraphics.drawImage(GlobalResources.imgIngame, xStart - 42, yStart - 23, anchorTopLeft); // trigger flash
                    break;
                case 2:
                    currentGraphics.setClip(xStart-2, yStart-2, 5, 5);
                    currentGraphics.drawImage(GlobalResources.imgIngame, xStart -2 - 44, yStart - 2 - 25, anchorTopLeft);   // trigger flash
                    break;
                case 3:
                    //System.out.println(xStart + ", " + yStart + ", ");
                    currentGraphics.setClip(xStart, yStart, 7, 7);
                    currentGraphics.drawImage(GlobalResources.imgIngame, xStart - 42, yStart - 23, anchorTopLeft); // trigger flash
                    break;
                case 4:
                    currentGraphics.setClip(xStart, yStart - 7, 5, 5);
                    currentGraphics.drawImage(GlobalResources.imgIngame, xStart - 44, yStart - 7 - 25, anchorTopLeft); // trigger flash
                case 5:
                    currentGraphics.setClip(xStart, yStart, 3, 3);
                    currentGraphics.drawImage(GlobalResources.imgIngame, xStart - 42, yStart - 23, anchorTopLeft); // trigger flash
                    break;
            }
        }
    }

    private void drawItemInUse(int yOffset) {
        // draw selected item top left to show player which item he is currently using for the trigger on target action
        if (itemTmpD!=null) {
            d8 = info1Line.y + yOffset;
            inventoryOffset = 0;
            currentGraphics.setClip(0, d8, ITEMWIDTH + 1, ITEMHEIGHT + 1);
            drawItemTypeBackground(itemTmpD, 0, 0, d8, false, 1);
            // draw the item itself
            Image itemImage;

            currentGraphics.setClip(1, d8 + 1, ITEMWIDTH, ITEMHEIGHT);
            if(itemTmpD.graphicsel == 1) {
                itemImage = items[1];
            } else {
                itemImage = items[0];
            }
            currentGraphics.drawImage(itemImage,  1 - ((itemTmpD.graphicsX*DIM)),
                                              d8 + 1 -((itemTmpD.graphicsY*DIM)),
                                              anchorTopLeft);
            // item selection flash
            currentGraphics.setClip(0, d8, ITEMSLOTWIDTH, ITEMSLOTHEIGHT);
            currentGraphics.setColor(255, 255, 255);
            currentGraphics.drawRect(0, d8, ITEMSLOTWIDTH-1, ITEMSLOTHEIGHT-1);
        }
    }
    
    private void checkNetLoadImages() {
        // load any dynamic background or dynamic enemy graphics
        if (currentBackgroundImage_ToLoad < 2) {
            if (currentBackgroundImage_ToLoad == 0 && backgroundImages_ToLoad[0] >= 0) {
                background = getImage("back" + backgroundImages_ToLoad[0] + ".png", true);
                //background = getImageByHTTP("back" + backgroundImages_ToLoad[0] + ".png", true);
            } else if (backgroundImages_ToLoad[1] >= 0) {
                dynamic = getImage("back" + backgroundImages_ToLoad[1] + ".png", true);
                //dynamic = getImageByHTTP("back" + backgroundImages_ToLoad[1] + ".png", true);            
            }
            currentBackgroundImage_ToLoad++;
            if (currentBackgroundImage_ToLoad < 2) {
                GTools.labelSetText(label3, "Loading background gfx 1", false);
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
            } else {
                GTools.labelSetText(label3, "Loading enemy gfx 0", false);
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
            }

        } else if (currentDynamicEnemy_ToLoad < dynamicEnemies_ToLoad_count) {
            int index = graphicSelExtract(dynamicEnemies_ToLoad[currentDynamicEnemy_ToLoad]);    // make sure this is interpreted as an unsigend value;
            if (index >= 0 && enemies[index]==null) {
                enemies[index] = getImage("enemy" + index + ".png", true);
                //enemies[dynamicEnemies_ToLoad[currentDynamicEnemy_ToLoad]] = getImageByHTTP("enemy" + dynamicEnemies_ToLoad[currentDynamicEnemy_ToLoad] + ".png", true);
            }
            
            currentDynamicEnemy_ToLoad++;   // proceed to next dynamic enemy loading (will be loaded in next frame
            if (currentDynamicEnemy_ToLoad < dynamicEnemies_ToLoad_count) {
                GTools.labelSetText(label3, "Loading enemy gfx " + currentDynamicEnemy_ToLoad, false);
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
            } else {
                /*if (items == null) {
                    GTools.labelSetText(label3, "Loading item gfx", false);
                    GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
                } else {*/
                    GTools.labelSetText(label3, "All gfx loaded", false);
                    GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
                    System.gc();
                //}
            }
        /*} else if (items==null) {
            // no dynamic enemies to load
            //load items
            items = getImage("items.png", true);
            //items = getImageByHTTP("items.png", true);
            //items = Image.createImage("/items.png");
            GTools.labelSetText(label3, "All gfx loaded", false);
            GTools.windowCenterX(label3, 0, DISPLAYWIDTH);*/
        } else {
            currentState = STATE_GAUGE;
            currentSubState = SUBSTATE_NORMAL;
            //sendJoinGroupMessage(playfieldName);
            //client left group, join legacyPlayfield
            if (playfieldName!=null && !playfieldName.equals("")) {
                sendJoinGroupMessage(playfieldName);
            }
        }
        
    }
    
    private void checkPlayfieldLoaded() {
        //System.out.println("state in check legacyPlayfield loaded: " + currentState + ", " + currentSubState);
        if (loadingPlayfield && playfieldCounter == playfieldHeight) {  // legacyPlayfield completely loaded, step on it
            allowGameInput = false; //do not allow game keys until own player is added
            currentState = STATE_GAME;
            initGraphics();
            initWindows();
            sendAddMe();

            //System.out.println("sent add me");

            if (isPeaceful((xPos + playerScreenX) + (PLAYERWIDTH_HALF), (yPos + playerScreenY-TOP_INFOHEIGHT) + (PLAYERHEIGHT_HALF))) {
                peacefulDisplay = 1;
                GTools.textWindowSetText(info1Line2, "Peaceful Area");
                GTools.windowSetColors(info1Line2, 0x000000, 0x000000, 0x006600, 0x006600);
                peacefulDisplayTime = 10000;
            } else {
                peacefulDisplay = 2;
                GTools.textWindowSetText(info1Line2, "Fighting Area");
                GTools.windowSetColors(info1Line2, 0x000000, 0x000000, 0x660000, 0x660000);
                peacefulDisplayTime = 10000;
            }
            
            
            /*
            if (soundPossible && soundON && soundPlayer!=null) {
                soundPlayer.stopSound();
                playbackSound(0, -1);
            }
             */

            /* // $-> activate!
            // change sound to normal game sound
            if (soundON && soundPossible) {
                if (isPeaceful(playerObject.x + (PLAYERWIDTH_HALF), playerObject.y + (PLAYERHEIGHT_HALF))) {
                    playbackSound(1, -1);    // peaceful
                } else {
                    playbackSound(0, -1);    // not peaceful
                }
            }
             */

            playfieldCounter = 0;   // reset legacyPlayfield packetcounter
            loadingPlayfield = false;

            /*
            if (firstTime) {
                sendRequestItemsMessage(character_DB_ID);
                firstTime=false;
            }
             */
        }
    }



    private void onConnectError() {
        if (!firstConnect) {
            if (bServerList) {
                subStateOKDialog("Connection to start server failed!\nNetworking could not be started.\n\nPlease try again.", STATE_INTRO, SUBSTATE_ACTIVE);
            } else {
                subStateOKDialog("Connection to selected server failed!\nNetworking could not be started.\n\nPlease try again.", STATE_INTRO, SUBSTATE_ACTIVE);
            }
            setBottomCommand1("Connect");

            currentState = STATE_CONNECT_ERROR;
            doConnect = 0;
            stopNet();
            // System.out.println("FINAL FAILURE");
        } else {
            doConnect = 0;
            stopNet();
            firstConnect = false;   // reset firstConnect value, so we try again, before we are convinced we cannot connect
            currentState = STATE_PRE_CONNECT;
            currentSubState = SUBSTATE_NORMAL;
            // System.out.println("FAILED - TRYING AGAIN");
        }
        netError = false;
        
    }
    
    
    private void drawLoadingWorld() {
        if(currentGraphics != null) {
            GTools.drawWindow(currentGraphics, label1, true);
            //font.drawString(currentGraphics, "loading world..", gaugeWindow.x+10, gaugeWindow.y-8);
            
            if (playfield != null) {
                GTools.drawProgress(currentGraphics, gaugeWindow, playfield.getLoadedCellCount(), playfield.getCellCount());
            } else {
                GTools.drawProgress(currentGraphics, gaugeWindow, 0, 0);
            }
            //if (items==null) {
                //GTools.drawProgress(currentGraphics, gaugeWindow1, currentDynamicEnemy_ToLoad + currentBackgroundImage_ToLoad, dynamicEnemies_ToLoad_count + 3);
            //} else {
            //System.out.println("STATE:_WAIT_LOAD_GFX: loadingCount " + imageManager.loadingCount());
                GTools.drawProgress(currentGraphics, gaugeWindow1, numImagesToLoad - imageManager.loadingCount(), numImagesToLoad);
            //}
            /*
            if (!imgHTTP && nextImageSize > 0 && nextImageSize > nextImageWalker) {
                System.out.println("done");
                currentGraphics.setClip(gaugeWindow1.x, gaugeWindow1.y + 8, gaugeWindow1.width, 2);
                currentGraphics.setColor(255, 255, 255);
                currentGraphics.fillRect(gaugeWindow1.x, gaugeWindow1.y + 8, ((nextImageWalker / nextImageSize) * gaugeWindow1.width), 2);
            }*/
            // SHOW TOP USERS
            GTools.drawWindow(currentGraphics, label2, true);
            GTools.drawWindow(currentGraphics, label3, true);
            GTools.drawWindow(currentGraphics, highScoreWindow, true);

            // $-> currently deactivated
            /*
            if (usersTotal>0 &&  usersOnline<=usersTotal) {
                GTools.drawWindow(currentGraphics, label2, true);
            }*/
        }
    }
    
    private void checkNetworkAlive() {
        if (!gbManager.status()) {
            currentState = STATE_BLACK;
            bCommand1 = false;
            setBottomCommand2("Exit");
            subStateOKDialog("Server connection failed.\nExit.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
            checkNet = false;
        } else {
            curTime = System.currentTimeMillis();
            if (curTime - gbManager.getLastReceive() > RECEIVE_TIMEOUT) {
                //no server response for a long time.. disconnect
                currentState = STATE_BLACK;
                setBottomCommand2("Exit");
                subStateOKDialog("Server connection timed out.\nExit.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                stopNet();
                checkNet = false;
                doConnect = 0;
                netError = false;
            }
            /*
            else if ((curTime - gbManager.getLastReceive() > TRAFFIC_TIMEOUT || curTime - gbManager.getLastSend() > TRAFFIC_TIMEOUT) && curTime - lastPingSent > PINGINTERVAL) {
                sendPing();  //try to get a pong messsage from the server to keep networking alive
                lastPingSent = curTime;
            }*/
        }
    }    
    
    
    /*
    private void setChatActive() {
        
    }
    
    private void setChatPassive() {
        
    }
     */
    
    
    private void drawQueuedMessageIcon() {
        if (GlobalResources.imgIngame==null)
            return;

//#if Series40_MIDP2_0
//#         //draw the icon
//#         currentGraphics.setClip(4, info1Line.height + 1, 9, 13);        
//#         currentGraphics.drawImage(GlobalResources.imgIngame, -1, info1Line.height - 14, anchorTopLeft); // queued message
//#else
        
        if (true || chatRequest) {
            //draw the icon: chat message
            currentGraphics.setClip(4, 1, 9, 13);        
            currentGraphics.drawImage(GlobalResources.imgIngame, -1, -15, anchorTopLeft);   // queued message
        } else {
            //draw the icon: friend request
            currentGraphics.setClip(1, 1, 15, 13);        
            currentGraphics.drawImage(GlobalResources.imgIngame, -27, -59, anchorTopLeft);   // queued friend request
        }
//#endif
    }

    
    
    private void drawSelectedPlayerCursor() {
        
        if (selectedPlayer < 0)
            return;
        
        characterTmpD = playersOnScreen[selectedPlayer];
        if (characterTmpD!=null) {
            d8 = (characterTmpD.graphicsDim*DIM);
            
            GTools.saveGraphicsSettings(currentGraphics);
            
            d1 = characterTmpD.x - xPos;
            d2 = characterTmpD.y - yPos + TOP_INFOHEIGHT;
            
//#if Series40_MIDP2_0
//#             if (d2 >= DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT - 2 || d2 + (characterTmpD.graphicsDim * DIM) <= 7 + TOP_INFOHEIGHT
//#else
            if (d2 >= DISPLAYHEIGHT + TOP_INFOHEIGHT - 2 || d2 + (characterTmpD.graphicsDim * DIM) <= 7 + TOP_INFOHEIGHT
//#endif
             || d1 >= DISPLAYWIDTH - 2 || d1 + (characterTmpD.graphicsDim * DIM) <= 2)
            { // character not fully visible on screen
                d1+=(d8/2);
                d2+=(d8/2);
                if (DISPLAYWIDTH-d1 < 9) {
                    d1 = DISPLAYWIDTH-9;
                    d9 = DirectionInfo.RIGHT; // direction: right
                    
                } else if (d1 < 9) {
                    d1=9;
                    d9 = DirectionInfo.LEFT; // direction: left
                }
                
                if (DISPLAYHEIGHT + TOP_INFOHEIGHT - d2 < 9) {
                    d2 = DISPLAYHEIGHT + TOP_INFOHEIGHT - 9;
                    d9 = DirectionInfo.DOWN; // direction: down  (overrides h-direction)
                } else if (d2 < 13 + TOP_INFOHEIGHT + info1Line.height) {
                    d2= 13 + TOP_INFOHEIGHT + info1Line.height;
                    d9 = DirectionInfo.UP; // direction: up  (overrides h-direction)
                }
                
                if (flash) {
                    GTools.drawArrow(currentGraphics, d9, d1, d2, 5, 0xFFCC00);
                } else {
                    GTools.drawArrow(currentGraphics, d9, d1, d2, 5, 0xFFFFFF);
                }
                return;
            } else {    // character visible on screen
            
                if (currentSubState == SUBSTATE_TRIGGERTARGET_FIND) {
                    d9 = 6;
                    d12 = 12;
                } else {
                    d9 = 3;
                    d12 = 6;
                }

                // xStart
                d4 = d1-d9;
                if (d4 < 0) d4 = 0;

                // yStart
                d10 = d2 - d9;
                if (d10 < 0) d10 = 0;

                // width
                d5 = d8 + d12;
                if (d4 + d5 > DISPLAYWIDTH) d5 = DISPLAYWIDTH - d4;

                // height
                d11 = d8 + d12;
//#if Series40_MIDP2_0
//#                 if (d10 + d11 > DISPLAYHEIGHT + TOP_INFOHEIGHT + BOTTOM_INFOHEIGHT) d11 = DISPLAYHEIGHT  + TOP_INFOHEIGHT - d10;
//#else
                if (d10 + d11 > DISPLAYHEIGHT + TOP_INFOHEIGHT) d11 = DISPLAYHEIGHT  + TOP_INFOHEIGHT - d10;
//#endif
                
                currentGraphics.setClip(d4, d10, d5, d11);
                
                if (currentSubState == SUBSTATE_TRIGGERTARGET_FIND) {
                    
                        if (isInWeaponRange) { // range of spells is twice as far as default weapon range
                            currentGraphics.setColor(0,255,0);  // in range: green
                        } else {
                            currentGraphics.setColor(255,204,0);    // not in range: orange
                        }
                        
                        // draw spell cursor
                        // outer strokes
                        currentGraphics.drawLine( d1 - 3, d2 - 3, d1 + 3, d2 - 6);  //topleft h
                        currentGraphics.drawLine(d1 + d8 + 2, d2 - 3, d1 + d8 - 4, d2 - 6); //topright h
                        currentGraphics.drawLine( d1 - 3, d2 - 3, d1 - 6, d2 + 3);  //topleft v
                        currentGraphics.drawLine( d1 + d8+2, d2 - 3, d1 + d8+5, d2 + 3);    //topright v
                        currentGraphics.drawLine( d1 - 3, d2 + d8 + 2, d1 + 3, d2 + d8 + 5);    //bottomleft h
                        currentGraphics.drawLine( d1 + d8 + 2, d2 + d8 + 2, d1 + d8 - 4, d2 + d8 + 5);  //bottomright h
                        currentGraphics.drawLine(d1 - 3, d2 + d8 + 2, d1 - 6, d2 + d8 - 4); //bottomleft v
                        currentGraphics.drawLine(d1 + d8+2, d2 + d8 + 2, d1 + d8+5, d2 + d8 - 4);   //bottomright v
                        
                        // inner strokes
                        if (playerObject.isRechargingForAttack()) {currentGraphics.setColor(128,192,128);}
                        currentGraphics.drawLine( d1 - 3, d2 - 2, d1 + 3, d2 - 5);  //topleft h
                        currentGraphics.drawLine(d1 + d8 + 2, d2 - 2, d1 + d8 - 4, d2 - 5); //topright h
                        currentGraphics.drawLine( d1 - 2, d2 - 3, d1 - 5, d2 + 3);  //topleft v
                        currentGraphics.drawLine( d1 + d8+1, d2 - 3, d1 + d8+4, d2 + 3);    //topright v
                        currentGraphics.drawLine( d1 - 3, d2 + d8 + 1, d1 + 3, d2 + d8 + 4);    //bottomleft h
                        currentGraphics.drawLine( d1 + d8 + 2, d2 + d8 + 1, d1 + d8 - 4, d2 + d8 + 4);  //bottomright h
                        currentGraphics.drawLine(d1 - 2, d2 + d8 + 2, d1 - 5, d2 + d8 - 4); //bottomleft v
                        currentGraphics.drawLine(d1 + d8+1, d2 + d8 + 2, d1 + d8+4, d2 + d8 - 4);   //bottomright v
                        
                
                } else if (currentSubState == SUBSTATE_FIGHT_FIND) {

                    /*
                    d4 = d1-3;
                    if (d4 < 0) 
                        d4 = 0;

                    d5 = d8 + 6;
                    if (d1-3 + d5 > DISPLAYWIDTH)
                        d5 = DISPLAYWIDTH - d1 + 3;
                    */
                    
                    //System.out.println("setclip : " + d4 + ", " + d10+ ", " + d5+ ", " + d11);
                    
                    d6 = d1 + d8/2;
                    d7 = d2 + d8/2;
                    
                    currentGraphics.setColor(255,255,255);

                    // top
                    currentGraphics.drawLine( d6 - 6, d2 - 3, d6 + 5, d2 - 3);
                    //currentGraphics.drawLine( d6 - 6, d2 - 2, d6 + 5, d2 - 2);

                    // left
                    currentGraphics.drawLine( d1 - 3, d7 - 6, d1 - 3, d7 + 5);
                    //currentGraphics.drawLine( d1 - 2, d7 - 6, d1 - 2, d7 + 5);

                    // right
                    currentGraphics.drawLine( d1 + d8 + 2, d7 - 6, d1 + d8 + 2, d7 + 5);
                    //currentGraphics.drawLine( d1 + d8 + 1, d7 - 6, d1 + d8 + 1, d7 + 5);

                    // bottom
                    currentGraphics.drawLine( d6 - 6, d2 + d8 + 2, d6 + 5, d2 + d8 + 2);
                    //currentGraphics.drawLine( d6 - 6, d2 + d8 + 1, d6 + 5, d2 + d8 + 1);

                    currentGraphics.drawRect(d1-2, d2-2, d8+3, d8+3);                    
                    
                    
                } else if (currentSubState == SUBSTATE_FIGHT_ACTIVE) {
                    if (isInWeaponRange(characterTmpD, equipment[WEAPON],1)) {
                        currentGraphics.setColor(0,255,0);
                        db1 = false;
                    } else {
                        currentGraphics.setColor(255,192,0);
                        db1 = true;
                    }
                    
                    // outer strokes
                    currentGraphics.drawLine( d1 - 3, d2 - 3, d1 + 3, d2 - 3);  //topleft h
                    currentGraphics.drawLine( d1 + d8 - 4, d2 - 3, d1 + d8 + 2, d2 - 3);    //topright h
                    currentGraphics.drawLine( d1 - 3, d2 - 3, d1 - 3, d2 + 3);  //topleft v
                    currentGraphics.drawLine( d1 + d8+2, d2 - 3, d1 + d8+2, d2 + 3);    //topright v
                    currentGraphics.drawLine( d1 - 3, d2 + d8 + 2, d1 + 3, d2 + d8 + 2);    //bottomleft h
                    currentGraphics.drawLine( d1 + d8 - 4, d2 + d8 + 2, d1 + d8 + 2, d2 + d8 + 2);  //bottomright h
                    currentGraphics.drawLine( d1 - 3, d2 + d8 - 4, d1 - 3, d2 + d8 + 2);    //bottomleft v
                    currentGraphics.drawLine( d1 + d8+2, d2 + d8 - 4, d1 + d8+2, d2 + d8 + 2);  //bottomright v
                    
                    // inner strokes
                    if (playerObject.isRechargingForAttack()) {currentGraphics.setColor(128,192,128);}
                    currentGraphics.drawLine( d1 - 2, d2 - 2, d1 + 3, d2 - 2);  //topleft h
                    currentGraphics.drawLine( d1 + d8 - 4, d2 - 2, d1 + d8 + 1, d2 - 2);    //topright h
                    currentGraphics.drawLine( d1 - 2, d2 - 2, d1 - 2, d2 + 3);  //topleft v
                    currentGraphics.drawLine( d1 + d8+1, d2 - 2, d1 + d8+1, d2 + 3);    //topright v
                    currentGraphics.drawLine( d1 - 2, d2 + d8 + 1, d1 + 3, d2 + d8 + 1);    //bottomleft h
                    currentGraphics.drawLine( d1 + d8 - 4, d2 + d8 + 1, d1 + d8 + 1, d2 + d8 + 1);  //bottomright h
                    currentGraphics.drawLine( d1 - 2, d2 + d8 - 4, d1 - 2, d2 + d8 + 1);    //bottomleft v
                    currentGraphics.drawLine( d1 + d8+1, d2 + d8 - 4, d1 + d8+1, d2 + d8 + 1);  //bottomright v


                    if (db1 && isPeaceful(characterTmpD.x + (d8/2), characterTmpD.y + (d8/2))) {
                            d6 = (characterTmpD.graphicsDim * DIM)/2;
                            d1 = characterTmpD.x + d6 - xPos - 5;
                            d2 = characterTmpD.y - yPos - 8  + TOP_INFOHEIGHT;
                            currentGraphics.setClip(d1, d2, 10, 6);
                            currentGraphics.drawImage(GlobalResources.imgIngame, d1-32, d2-23, anchorTopLeft); // peaceful icon

                    }

                } else {
                    if (currentSubState == SUBSTATE_FRIEND_FIND) {
                        currentGraphics.setColor(255,255,255);
                    } else {
                        currentGraphics.setColor(208,255,255);
                    }
                    currentGraphics.drawRect(d1-2, d2-2, d8+3, d8+3);
                    //currentGraphics.setColor(192,192,192);
                    currentGraphics.drawRect(d1-3, d2-3, d8+5, d8+5);
                }
            }

            GTools.restoreGraphicsSettings(currentGraphics);
        } else {
            selectedPlayer = 0;
            subStateNormal();
        }
        characterTmpD = null;
    }
    
    private void drawPlayerStatusTop() {
//#if Series40_MIDP2_0
//#         if (playerObject==null)
//#             return;
//# 
//#         d1 = DISPLAYWIDTH - 12;
//#         d2 = series40_TOP_INFOHEIGHT + info1Line.height + 2;
//#         
//#         // error check values
//#         d3 = (playerObject.curHealth*40)/healthBase;
//#         if (d3 > 40) {
//#             d3 = 40;
//#         }
//#         d4 = (playerObject.curMana*40)/manaBase;
//#         if (d4 > 40) {
//#             d4 = 40;
//#         }
//# 
//#         currentGraphics.setClip(d1, d2, 10, 40);
//#         //currentGraphics.setColor(0,255,0);
//#         //currentGraphics.fillRect(d1,d2, 10, 40);
//#         //curHealth
//#         currentGraphics.setColor(64, 0, 0);
//#         currentGraphics.fillRect(d1, d2, 4, 40-d3);
//#         currentGraphics.setColor(255, 0, 0);
//#         currentGraphics.fillRect(d1, d2+40-d3, 4, d3);
//#         //curMana
//#         d1+=6;
//#         currentGraphics.setColor(0, 0, 64);
//#         currentGraphics.fillRect(d1, d2, 4, 40-d4);
//#         currentGraphics.setColor(0, 0, 255);
//#         currentGraphics.fillRect(d1, d2+40-d4, 4, d4);
//# 
//#         // check if we should display mnaa consume info
//#         if (currentSubState == SUBSTATE_INVENTORY || currentSubState == SUBSTATE_INVITEM_OPTIONS || currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) {
//#             itemTmpD = invItems[selectedInvItem];
//#             if (itemTmpD != null && itemTmpD.curMana < 0 && -itemTmpD.curMana <= manaBase) {
//#                 //db1 = true;
//#                 d5 = d2 + 40 - ((-itemTmpD.curMana*(40)) / manaBase) + 1;  // offset from the end of the curMana bar towards the startPlay of the bar
//# 
//#                 currentGraphics.setColor(255, 255, 255);
//#                 currentGraphics.drawLine(d1, d5, d1 + 4, d5);
//#             }
//#                 itemTmpD = null;
//#         }
//#         
//#         
//#         //left flash icon
//#         if ((genericList.entries.size() > 0  || friendRequestList.entries.size() > 0) && flash) {
//#             drawQueuedMessageIcon();
//#         }
//#         
//#else
        drawTopFrame();
        if (playerObject==null)
            return;

        if (playerObject.levelpoints > 0) {
            playerLevelWindow.backColor = 0x004000;
        } else {
            playerLevelWindow.backColor = 0x000000;
        }
        
        currentGraphics.setClip(0, 0, DISPLAYWIDTH, TOP_INFOHEIGHT-4);
        GTools.drawWindow(currentGraphics, playerLevelWindow, true);
        GTools.drawWindow(currentGraphics, playerExperienceWindow, true);
        GTools.drawWindow(currentGraphics, playerGoldWindow, true);

        d1 = 4 + ITEMWIDTH;
        d2 = playerExperienceWindow.x- d1 - 3;
        

        if (d2 > 11) {
            if (d2 > 50) {
                d2 = 50;
            }

            // error check values
            d3 = (playerObject.curHealth*(d2))/playerObject.getTotalMaxHealth();
            if (d3 > d2) {
                d3 = d2;
            }
            d4 = (playerObject.curMana*(d2))/playerObject.getTotalMaxMana();
            if (d4 > d2) {
                d4 = d2;
            }
            
            //curHealth
            currentGraphics.setColor(64, 0, 0);
            currentGraphics.fillRect(d1, 2, d2, 5);
            currentGraphics.setColor(255, 0, 0);
            currentGraphics.fillRect(d1, 2, d3, 5);
            //curMana
            if ((currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) && playerObject.isRechargingForAttack()) {
                currentGraphics.setColor(80, 80, 80);
            } else {
                currentGraphics.setColor(0, 0, 64);
            }
            currentGraphics.fillRect(d1, 9, d2, 5);
            if ((currentSubState == SUBSTATE_TRIGGERTARGET_FIND  || currentSubState == SUBSTATE_GROUND_FIND) && playerObject.isRechargingForAttack()) {
                currentGraphics.setColor(160, 160, 160);
            } else {
                currentGraphics.setColor(0, 0, 255);
            }
            currentGraphics.fillRect(d1, 9, d4, 5);

            // check if we should display curMana consume info
            if (currentSubState == SUBSTATE_INVENTORY || currentSubState == SUBSTATE_INVITEM_OPTIONS || currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) {
                itemTmpD = invItems[selectedInvItem];
                if (itemTmpD != null && itemTmpD.manaBase < 0 && -itemTmpD.manaBase <= playerObject.getTotalMaxMana()) {
                    //db1 = true;
                    d5 = d1 + ((-itemTmpD.manaBase*(d2)) / playerObject.getTotalMaxMana()) - 1;  // offset from the end of the curMana bar towards the startPlay of the bar
                    
                    currentGraphics.setColor(255, 255, 255);
                    currentGraphics.drawLine(d5, 9, d5, 13);
                }
                itemTmpD = null;
            }
        }
        
        /*
        //curHealth
        currentGraphics.setColor(64, 0, 0);
        currentGraphics.fillRect(j, 2, playerExperienceWindow.x- j - 3, 5);
        currentGraphics.setColor(255, 0, 0);
        currentGraphics.fillRect(j, 2, (playerObject.curHealth*(playerExperienceWindow.x-j-3))/healthBase, 5);
        //curMana
        currentGraphics.setColor(0, 0, 64);
        currentGraphics.fillRect(j, 9, playerGoldWindow.x-j-3, 5);
        currentGraphics.setColor(0, 0, 255);
        currentGraphics.fillRect(j, 9, (playerObject.magicBase*(playerGoldWindow.x-j-3))/manaBase, 5);
        */
        //left flash icon background
        currentGraphics.setColor(0, 0, 96);
        currentGraphics.fillRect(0, 0, ITEMWIDTH + 2, ITEMHEIGHT);
        currentGraphics.setColor(128, 128, 128);

        if (getNumUpdatedConversations()>0) {
            drawQueuedMessageIcon();
        }
        /*
        if ((genericList.entries.size() > 0 || friendRequestList.entries.size() > 0) && flash) {
            drawQueuedMessageIcon();
        }*/

        // frame for blue event icon slot (top left of the screen)
        currentGraphics.setClip(0,0,ITEMWIDTH+2,ITEMHEIGHT);
        currentGraphics.drawRect(0, 0, ITEMWIDTH+1, ITEMHEIGHT-1);
        // ToDo: read out
        //font.drawChar(currentGraphics, '1', 1 + ITEMWIDTH - font.charWidth, TOP_INFOHEIGHT - font.charHeight - 1);
        
//#endif
    }
    

    private void drawDialogueLoadProgress() {
//#if !(Series40_MIDP2_0)
        GTools.drawWindow(currentGraphics, info1Line, true);
//#endif
        
        d2 = menuClientphrases.y - 4;
        if (dialogueCurrent < dialogueTotalCount && dialogueTotalCount > 0) {
            d4 = ((dialogueCurrent + 1) * DISPLAYWIDTH) / dialogueTotalCount;
        } else {
            d4 = 0;
        }
        currentGraphics.setColor(0,0,0);
        currentGraphics.setClip(0, d2, DISPLAYWIDTH, 4);
        currentGraphics.fillRect(0, d2, DISPLAYWIDTH, 4);
        currentGraphics.setColor(64,64,64);
        currentGraphics.fillRect(1, d2 + 1, DISPLAYWIDTH - 2, 2);

    }

    private boolean cellUpdate() {
        i = xPos + playerScreenX;
        j = yPos + playerScreenY - TOP_INFOHEIGHT;
        
        k = cellWindow_XStart;
        l = cellWindow_YStart;
        
        m = curCellX;
        n = curCellY;

        curCellX = (byte)(i / TILEWIDTH);
        curCellY = (byte)(j / TILEHEIGHT);

        
        /*
        if (m != curCellX || n != curCellY) {
            //System.out.println("(" + curCellX + ", " + curCellY + ")");        
            // -- sendFireWallMessage(curCellX, curCellY, 7, 30);    
        }
         */
        
        cellWindow_XStart = (byte)(curCellX - FIREWALL_CELLRANGE);
        cellWindow_YStart = (byte)(curCellY - FIREWALL_CELLRANGE);


        // do not let window slide over right legacyPlayfield border
        if (cellWindow_XStart + (FIREWALL_CELLRANGE<<1) > (playfieldWidth-1)) {
            cellWindow_XStart = (byte)((playfieldWidth-1) - (FIREWALL_CELLRANGE<<1));
        }

        // do not let window slide over bottom legacyPlayfield border
        if (cellWindow_YStart + (FIREWALL_CELLRANGE<<1) > (playfieldHeight-1)) {
            cellWindow_YStart = (byte)((playfieldHeight-1) - (FIREWALL_CELLRANGE<<1));
        }
        
        // do not let window slide over left / top legacyPlayfield border
        if (cellWindow_XStart < 0) {cellWindow_XStart = 0;}
        if (cellWindow_YStart < 0) {cellWindow_YStart = 0;}
        
        k = cellWindow_XStart - k;
        l = cellWindow_YStart - l;

        
        if (k==0 && l==0) {
                return false;	// no cell window change
        } else if (k > FIREWALL_WINDOWSIZE || k < -FIREWALL_WINDOWSIZE || l > FIREWALL_WINDOWSIZE || l < -FIREWALL_WINDOWSIZE) {
                // whole array needs to be cleared
                for (n=0; n < FIREWALL_WINDOWSIZE; n++) {System.arraycopy(emptyFireWallElement, 0, fireWalls[n], 0, FIREWALL_WINDOWSIZE);}
                return true;
        } else {
//System.out.println("k: " + k + " l: " + l);        

                if (k!=0) {shift2DArray(fireWalls, 0, k, FIREWALL_WINDOWSIZE, FIREWALL_WINDOWSIZE);} /* cell xChange */
                if (l!=0) {shift2DArray(fireWalls, 1, l, FIREWALL_WINDOWSIZE, FIREWALL_WINDOWSIZE);} /* cell yChange */

                /*
                System.out.println("AFTER: ");            
                for (i=0; i < FIREWALL_WINDOWSIZE; i++) {
                        System.out.println(fireWalls[i]);
                }
                
                byte[] tmp = null;
                for (i=0; i < FIREWALL_WINDOWSIZE; i++) {
                    tmp = fireWalls[i];
                    for (j=0; j < FIREWALL_WINDOWSIZE; j++) {                    
                        if (tmp == fireWalls[j] && i!=j) {
                            System.out.println(" ================ DUPLICATE ===============");
                            System.out.println(" ================ DUPLICATE ===============");
                            System.out.println(" ================ DUPLICATE ===============");
                            System.out.println(" ================ DUPLICATE ===============");
                        }
                    }
                }
                */
                return true;
        }
    
    }
    
    private void shift2DArray(byte[][] a, int direction, int numElements, int maxElements1D, int maxElements2D) {
        if (direction == 0) {
            k = numElements;
            int wrapPos = 0;
            byte[] previous = null;
            byte[] tmp = null;
            
            System.arraycopy(a, 0, tmpFireWallArray, 0, FIREWALL_WINDOWSIZE);
            
            if (k < 0) {
                
                
                if (k < FIREWALL_CELLRANGE) {
                    wrapPos = maxElements1D+k;
                } else {
                    wrapPos = -k;
                }
                
                /*
                for (j=0; j<FIREWALL_CELLRANGE; j++) {
                    tmpFireWallArray[j] = a[j];
                }
                */
                
                //System.out.println("left: " + k);
                // cell movement to left -> results in right shift of the elements
                tmp = tmpFireWallArray[0];
                for (n=0; n < maxElements1D+k; n++) {
                    //System.out.println("n: " + n);
                    //System.out.print(" old tmp: " + tmp);
                    previous = tmp;
                    //System.out.print(" previous: " + tmp);
                    //System.out.print(" previous: " + tmp);
                    tmp = tmpFireWallArray[n+1];
                    //System.out.println(" new tmp: " + tmp);

                    if (n < -k && wrapPos + n < maxElements1D) {
                        // wrap columns around, so the columns which are "cut off" at the end are assigned to the foremost columns
                        a[n] = tmpFireWallArray[wrapPos + n];
                        //System.out.println(" wrap: " + a[n] + "(" + (wrapPos + n) + ")");
                    }
                    a[n-k] = previous;  // shift right
                    //
                    /*
                    System.out.println("a["+(n-k)+"] " + a[n-k] + " a[" + n + "] " + a[n] + " (" + wrapPos + ", " + n + ")");
                    for (i=0; i < FIREWALL_WINDOWSIZE; i++) {
                        System.out.println(fireWalls[i]);
                    }
                    System.out.println();
                     */
                    
                }
                // zerofill for invalidated cells
                for (n=0; n < -k; n++) {System.arraycopy(emptyFireWallElement, 0, a[n], 0, maxElements1D);}
                
            } else if (k > 0) {
                
                if (k < FIREWALL_CELLRANGE+1) {
                    wrapPos = k-1;
                } else {
                    wrapPos = maxElements1D-k-1;
                }
                
                //System.out.println("right: " + k);
                // cell movement to right -> results in left shift of the elements
                tmp = tmpFireWallArray[FIREWALL_WINDOWSIZE-1];
                for (n=FIREWALL_WINDOWSIZE-1; n > k-1; n--) {
                    previous = tmp;
                    tmp = tmpFireWallArray[n-1];
                    if (FIREWALL_WINDOWSIZE-1-n < k && wrapPos - (FIREWALL_WINDOWSIZE-1-n) >= 0) {
                        // wrap columns around, so the columns which are "cut off" at the front are assigned to the lastmost columns
                        a[n] = tmpFireWallArray[wrapPos - (FIREWALL_WINDOWSIZE-1-n)];
                    }
                    a[n-k] = previous;  // shift left
                    //
                }
                // zerofill for invalidated cells
                for (n=FIREWALL_WINDOWSIZE-1; n > FIREWALL_WINDOWSIZE-1-k; n--) {System.arraycopy(emptyFireWallElement, 0, a[n], 0, maxElements1D);}
            }

        } else if (direction == 1) {
            l = numElements;
            if (l < 0) {
                // up shift
                //System.out.println("up: " + l);
                if (l > -maxElements1D) {
                    for (n=0; n < maxElements1D; n++) {
                        // shift
                        System.arraycopy(a[n], 0, a[n], -l, maxElements1D+l);
                        // zero fill invalidated cells
                        System.arraycopy(emptyFireWallElement, 0, a[n], 0, -l);
                    }
                } else {
                    // zerofill all
                    for (n=0; n < maxElements1D; n++) {
                        System.arraycopy(emptyFireWallElement, 0, a[n], 0, maxElements1D);
                    }
                }
            } else if (l > 0) {
                // down shift
                //System.out.println("down: " + l);
                if (l < maxElements1D) {
                    for (n=0; n < maxElements1D; n++) {
                        // shift
                        System.arraycopy(a[n], l, a[n], 0, maxElements1D-l);
                        // zero fill invalidated cells
                        System.arraycopy(emptyFireWallElement, maxElements1D-l, a[n], maxElements1D-l, l);
                    }
                } else {
                    // zerofill all
                    for (n=0; n < maxElements1D; n++) {
                        System.arraycopy(emptyFireWallElement, 0, a[n], 0, maxElements1D);
                    }
                }
            }

        }
    }

    private boolean touchesFunction(Character character, int function) {
        PlayfieldCell[] cornerCells = new PlayfieldCell[4];
        int x = character.x;
        int y = character.y;

        cornerCells[0] = playfield.cellAt(x+blockTolerance, y+blockTolerance);  // top left
        cornerCells[1] = playfield.cellAt(x+character.graphicsDim-1-blockTolerance, y+blockTolerance); // top right
        cornerCells[2] = playfield.cellAt(x+character.graphicsDim-1-blockTolerance, y+character.graphicsDim-1-blockTolerance);    //bottom right
        cornerCells[3] = playfield.cellAt(x+blockTolerance, y+character.graphicsDim-1-blockTolerance); // bottom left

        if (
                cornerCells[0].hasFunction(function) ||
                cornerCells[1].hasFunction(function) ||
                cornerCells[2].hasFunction(function) ||
                cornerCells[3].hasFunction(function)
           )
        {
            return true;
        }
        return false;
    }


    private void checkPlayerMove () {
        if(blockDuration <= 0 && playerMove) {
            playerObject.checkAnimate(curGametime);
            int pixelDistance = playerSpeed;
            
            int newX = playerObject.x;
            int newY = playerObject.y;
            int setbackX = -1;
            int setbackY = -1;
            int cellX = (playerObject.x + blockTolerance) / PlayfieldCell.defaultWidth;
            int cellY = (playerObject.y + blockTolerance) / PlayfieldCell.defaultHeight;

            PlayfieldCell[] cornerCells = new PlayfieldCell[4];

            switch(playerObject.getDirection()) {
                case DirectionInfo.UP: 
                    newY = playerObject.y - pixelDistance;
                    setbackY = ((cellY) * PlayfieldCell.defaultHeight)-blockTolerance;
                    break;
                case DirectionInfo.DOWN: 
                    newY = playerObject.y + pixelDistance;
                    setbackY = (cellY+1) * PlayfieldCell.defaultHeight - playerObject.graphicsDim+blockTolerance;
                    break;
                case DirectionInfo.LEFT: 
                    newX = playerObject.x - pixelDistance;
                    setbackX = ((cellX) * PlayfieldCell.defaultWidth)-blockTolerance;
                    break;
                case DirectionInfo.RIGHT: 
                    newX = playerObject.x + pixelDistance;
                    setbackX = ((cellX+1) * PlayfieldCell.defaultWidth) - playerObject.graphicsDim + blockTolerance;
                    break;
            }

            // check against playfield bounds
            if (newX < 0) {
                newX = 0;
            }
            else if (newX+playerObject.graphicsDim > playfield.getWidth()) {
                newX = playfield.getWidth() - playerObject.graphicsDim;
            }

            if (newY < 0) {
                newY = 0;
            } else if (newY+playerObject.graphicsDim > playfield.getHeight()) {
                newY = playfield.getHeight() - playerObject.graphicsDim;
            }

            // todo: check if trigger was activated

            // check if walkable, set back if necessarey
            cornerCells[0] = playfield.cellAt(newX+blockTolerance, newY+blockTolerance);  // top left
            cornerCells[1] = playfield.cellAt(newX+playerObject.graphicsDim-1-blockTolerance, newY+blockTolerance); // top right
            cornerCells[2] = playfield.cellAt(newX+playerObject.graphicsDim-1-blockTolerance, newY+playerObject.graphicsDim-1-blockTolerance);    //bottom right
            cornerCells[3] = playfield.cellAt(newX+blockTolerance, newY+playerObject.graphicsDim-1-blockTolerance); // bottom left

            if (
                    cornerCells[0].hasFunction(PlayfieldCell.function_blocked) ||
                    cornerCells[1].hasFunction(PlayfieldCell.function_blocked) ||
                    cornerCells[2].hasFunction(PlayfieldCell.function_blocked) ||
                    cornerCells[3].hasFunction(PlayfieldCell.function_blocked)
               )
            {
                if (setbackX != -1) {
                    newX = setbackX;
                    System.out.println("newY: " + newY + ", setbackX: "+setbackX);
                }
                if (setbackY != -1) {
                    newY = setbackY;
                    //System.out.println("newY: " + newY + ", setbackY: "+setbackY);
                }
            }

            if (newX != playerObject.x || newY != playerObject.y) {
                sendPos = true;
            }

            playerObject.x = newX;
            playerObject.y = newY;


if (true) return;
            /*
            long timeSinceLastMove = 0;
            
            int tempDistance = 0;
            
            
            if (lastMoveDirection == playerDirection && lastMoveDirection != -1) {
                timeSinceLastMove = curGametime - lastMoveTime;
                if (timeSinceLastMove > 1000) {
                    timeSinceLastMove = 1000;
                }
            } else {
                lastMoveTime = curGametime;
                lastMoveDirection = playerDirection;
                return;   // ??
            }
            
            System.out.println("TIME " + timeSinceLastMove + " last: " + lastMoveTime + " current: " + curGametime);
            
            tempDistance = (int)((timeSinceLastMove * playerSpeed) + extraMovePixels);
            pixelDistance = tempDistance/ timeMoveUnit;
            extraMovePixels = tempDistance - (pixelDistance * timeMoveUnit);
            
            if (pixelDistance > 10) {pixelDistance = 10;}
            //if (extraMovePixels > 100000) {pixelDistance = 100000;}
            
            if (pixelDistance == 0) {
                return;
            }
            */
/* --
            l = xPos + playerScreenX; m = yPos + playerScreenY;
            
            if(playerDirection == 0) { //UP
                
                //test if players can walk on legacyPlayfield tile
                if(notBlocked(xPos+playerScreenX, yPos+playerScreenY-TOP_INFOHEIGHT-pixelDistance, true)) {
//#if Series40_MIDP2_0
//#                     if(playerScreenY - pixelDistance >= SCROLL_RANGE + series40_TOP_INFOHEIGHT) {
//#else
                    if(playerScreenY - pixelDistance >= SCROLL_RANGE + TOP_INFOHEIGHT) {
//#endif
                        playerScreenY-=pixelDistance;
                    } else {
                        if(yPos >= 0 + pixelDistance)
                            yPos-= pixelDistance;
                        else {
                            if(playerScreenY - pixelDistance >= TOP_INFOHEIGHT) {
                                playerScreenY-=pixelDistance;
                            } else {
                                playerScreenY = TOP_INFOHEIGHT;
                            }
                        }
                    }
                }
            } else if(playerDirection == 2) { //DOWN  
                //test if players can walk on legacyPlayfield tile

                if (notBlocked(xPos+playerScreenX, yPos+playerScreenY-TOP_INFOHEIGHT+pixelDistance, true)) {
                    if(playerScreenY + pixelDistance <= DISPLAYHEIGHT + TOP_INFOHEIGHT - (SCROLL_RANGE + PLAYERHEIGHT))
                        playerScreenY+=pixelDistance; //move, no scroll
                    else {
//#if Series40_MIDP2_0
//#                         if (yPos + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT + pixelDistance < playfieldHeight * TILEHEIGHT) {
//#else
                        if (yPos + DISPLAYHEIGHT + pixelDistance < playfieldHeight * TILEHEIGHT) {
//#endif
                            yPos+=pixelDistance;  //scroll
                        } else {    //check move only
                            if (playerScreenY + pixelDistance <  DISPLAYHEIGHT + TOP_INFOHEIGHT - PLAYERHEIGHT) {
                                playerScreenY+=pixelDistance;
                            } else {
                                playerScreenY=DISPLAYHEIGHT + TOP_INFOHEIGHT - PLAYERHEIGHT -1;
                            }
                        }
                    }
                }
            } else if(playerDirection == 3) { //LEFT

                if ( notBlocked(xPos+playerScreenX-pixelDistance, yPos+playerScreenY-TOP_INFOHEIGHT, true) ) {
                    if(playerScreenX-pixelDistance >= SCROLL_RANGE )
                        playerScreenX-=pixelDistance;
                    else {
                        if(xPos >= 0 + pixelDistance)
                            xPos-=pixelDistance;
                        else {
                            if(playerScreenX-pixelDistance >= 0) {
                                playerScreenX-=pixelDistance;
                            } else {
                                playerScreenX = 0;
                            }
                        }
                    }
                }
            } else  if(playerDirection == 1) {  //RIGHT
                
                if (notBlocked(xPos+playerScreenX+pixelDistance, yPos+playerScreenY-TOP_INFOHEIGHT, true)) {
                    if(playerScreenX + pixelDistance < DISPLAYWIDTH - (SCROLL_RANGE + PLAYERWIDTH))
                        playerScreenX+=pixelDistance;
                    else {
                        if (xPos + DISPLAYWIDTH + pixelDistance < playfieldWidth * TILEWIDTH) {
                            xPos+=pixelDistance;
                        } else {
                            if (playerScreenX + pixelDistance < DISPLAYWIDTH-PLAYERWIDTH) {
                                playerScreenX+=pixelDistance;
                            } else {
                                playerScreenX=DISPLAYWIDTH-PLAYERWIDTH-1;
                            }
                        }
                    }
                }
            }

            //position has changed, make sure, it is sent next time
            if (xPos + playerScreenX != l || yPos + playerScreenY != m) {

//                lastMoveTime = curGametime;
//                lastMoveDirection = playerDirection;

                sendPos = true;
                if (isPeaceful((xPos + playerScreenX) + (PLAYERWIDTH_HALF), (yPos + playerScreenY-TOP_INFOHEIGHT) + (PLAYERHEIGHT_HALF))) {
                peacefulDisplay = 1;
                    if (!isPeaceful(l + (PLAYERWIDTH_HALF), m -TOP_INFOHEIGHT + (PLAYERHEIGHT_HALF))) {
                        peacefulDisplayTime = 10000;
                        GTools.textWindowSetText(info1Line2, "Peaceful Area");
                        GTools.windowSetColors(info1Line2, 0x000000, 0x000000, 0x006600, 0x006600);
                    }
                } else if (isPeaceful(l + (PLAYERWIDTH_HALF), m -TOP_INFOHEIGHT + (PLAYERHEIGHT_HALF))) {
                    peacefulDisplay = 2;
                    peacefulDisplayTime = 10000;
                    GTools.textWindowSetText(info1Line2, "Fighting Area");
                    GTools.windowSetColors(info1Line2, 0x000000, 0x000000, 0x660000, 0x660000);
                }
 -- */
                // $-> activate!

                /*
                if (soundON && soundPossible) {
                    if (isPeaceful((xPos + playerScreenX) + (PLAYERWIDTH_HALF), (yPos + playerScreenY-TOP_INFOHEIGHT) + (PLAYERHEIGHT_HALF))) {
                        if (curSoundType!=1) {  // not peaceful yet
                            playbackSound(1, -1);
                        }
                    } else {
                        if (curSoundType==1) {  // current sound is peaceful
                            if (currentSubState==SUBSTATE_FIGHT_ACTIVE) {   //next sound: fight
                                playbackSound(2, 7000);
                            } else {                //next sound: normal
                                playbackSound(0, -1);
                            }
                        }
                    }
                }
                 */
            /* --
            }
             -- */
            
        }
/* --
if (true) return;


        if (playerObject!=null) {
            playerObject.direction = playerDirection;
            playerObject.x = xPos + playerScreenX;
            playerObject.y = yPos + playerScreenY-TOP_INFOHEIGHT;
        }
        
        if (cellUpdate()) {
-- */
            /*
            for (int i=0; i<FIREWALL_WINDOWSIZE; i++) {
                System.out.print("\n");
                for (int j=0; j<FIREWALL_WINDOWSIZE; j++) {
                        System.out.print(fireWalls[j][i] + " , ");
                }
            }
             */
        /* --
        }
        -- */
    }



    public boolean isPeaceful(Character c) {       
       return (playfield.hasFunctionAt(PlayfieldCell.function_peaceful, c.xCenter(), c.yCenter()));
    }

    public boolean isPeaceful(int x, int y) {
        if (legacyPlayfield==null) {
            return false;
        }
        /*
        if (x < 0 || x + PLAYERWIDTH > playfieldWidth * TILEWIDTH || y < 0 || y + PLAYERWIDTH > playfieldHeight * TILEHEIGHT) {
            return false;
        }
         */
        
        return (legacyPlayfield[x/TILEWIDTH][y/TILEHEIGHT] & 16) == 16;
    }
    
    /** Collision extract function id. */
    public boolean notBlocked(int x, int y, boolean checkTrigger) {
        boolean blocked = false;
        
        int gap = 3;
        if (legacyPlayfield==null) {
            return false;
        }
        
        if (x < 0 || x + PLAYERWIDTH > playfieldWidth * TILEWIDTH || y < 0 || y + PLAYERWIDTH > playfieldHeight * TILEHEIGHT)
            return false;
        
        int left = (x+gap)/TILEWIDTH;
        int top = (y+gap)/TILEHEIGHT;
        int right = ((x-gap)+PLAYERWIDTH-1)/TILEWIDTH;
        int bottom = ((y-gap)+PLAYERHEIGHT-1)/TILEHEIGHT;
        
        byte leftTop = (byte)(legacyPlayfield[left][top] & 112);
        byte rightTop = (byte)(legacyPlayfield[right][top] & 112);
        byte leftBottom = (byte)(legacyPlayfield[left][bottom] & 112);
        byte rightBottom = (byte)(legacyPlayfield[right][bottom] & 112);
        
        // check the collision
        //if((leftTop & 32)==32 || (rightTop & 32)==32 || (leftBottom & 32)==32 || (rightBottom & 32)==32)
            //blocked = true;
        blocked = ((leftTop & 32)==32 || (rightTop & 32)==32 || (leftBottom & 32)==32 || (rightBottom & 32)==32);
        
        // check, if we touched a trigger
        if (netStarted && !waitingForTrigger && checkTrigger) {
//System.out.println("checking for trigger ...");            
            if((leftTop & 64)==64) {
                functionCellX = (byte)left;
                functionCellY = (byte)top;
                
                requestTrigger(leftTop);
            }
                
            if((rightTop & 64)==64) {
                functionCellX = (byte)right;
                functionCellY = (byte)top;
                
                requestTrigger(rightTop);
            }

            if((leftBottom & 64)==64) {
                functionCellX = (byte)left;
                functionCellY = (byte)bottom;
                
                requestTrigger(leftBottom);
            }
                
            if((rightBottom & 64)==64) {
                functionCellX = (byte)right;
                functionCellY = (byte)bottom;
                
                requestTrigger(rightBottom);
            }
            
        } else {
//System.out.println("NOT checking for trigger ...");                    
        }
        
        return !blocked;
    }
    
    
    

    private void playerFireAction() {
        if (playerObject==null)
            return;

        Item it = playfield.getClosestItem(playerObject);
        if (it != null) {
            pickupItem(it);
        }
    }

    /*
    private int getClosestItem() {
        int distX;
        int distY;
        int shortest=-1;
        
        Item fwgo = null;
        Item local = null;
        Enumeration e = idToItems.elements();
        while(e.hasMoreElements()) {
            fwgo = (Item)e.nextElement();
            distX = (fwgo.x + (ITEMWIDTH/2)) - (playerObject.x + (PLAYERWIDTH_HALF));
            distY = (fwgo.y + (ITEMHEIGHT/2)) - (playerObject.y + (PLAYERHEIGHT_HALF));
            //if (distX * distX + distY * distY < 32*32 
            if (distX * distX + distY * distY < 1024 
            &&  (distX * distX + distY * distY < shortest || shortest==-1)
            ){
                local = fwgo;
                shortest = distX * distX + distY * distY;
            }
        }
        
        if(local != null)
            return local.objectId;
            
        return 0;            
    }*/
    


    
    private int getClosestPlayer(int range) {
        int distX;
        int distY;
        int shortest=-1;
        int xStart =0, yStart=0, xEnd=0, yEnd=0;
        
        switch (playerDirection) {
            case DirectionInfo.UP: //UP
                xStart = playerObject.x;
                yStart = playerObject.y - range + (PLAYERHEIGHT_HALF);
                xEnd = xStart + PLAYERWIDTH;
                yEnd = yStart + range;
                break;
            case DirectionInfo.RIGHT: //RIGHT
                xStart = playerObject.x + (PLAYERWIDTH_HALF);
                yStart = playerObject.y;
                xEnd = xStart + range;
                yEnd = yStart + PLAYERHEIGHT;
                break;
            case 2: //DOWN
                xStart = playerObject.x; 
                yStart = playerObject.y + (PLAYERWIDTH_HALF);
                xEnd = xStart + PLAYERWIDTH;
                yEnd = yStart + range;
                break;
            case DirectionInfo.LEFT: //LEFT
                xStart = playerObject.x - range + (PLAYERWIDTH_HALF);
                yStart = playerObject.y;
                xEnd = xStart + range;
                yEnd = yStart + PLAYERHEIGHT;
                break;
        }
        
        //xst=xStart; yst=yStart; xen=xEnd; yen=yEnd;
        
        Character fwgo = null;
        Character local = null;
        Enumeration e = idToCharacters.elements();
        while(e.hasMoreElements()) {
            fwgo = (Character)e.nextElement();
            if (fwgo.objectId!=character_DB_ID) {
                if (fwgo.x + PLAYERWIDTH_HALF >= xStart && fwgo.x + PLAYERWIDTH_HALF <= xEnd
                 && fwgo.y + PLAYERHEIGHT_HALF >= yStart && fwgo.y + PLAYERHEIGHT_HALF <= yEnd)
                {
                    //distX = (fwgo.x + (PLAYERWIDTH_HALF)) - (playerObject.x + (PLAYERWIDTH_HALF));
                    //distY = (fwgo.y + (PLAYERHEIGHT_HALF)) - (playerObject.y + (PLAYERHEIGHT_HALF));
                    distX = fwgo.x - playerObject.x;
                    distY = fwgo.y - playerObject.y;
                    if (distX * distX + distY * distY <= range*range 
                    &&  (distX * distX + distY * distY < shortest || shortest==-1)) {
                        local = fwgo;
                        shortest = distX * distX + distY * distY;
                    }
                }
            }
        }
        
        if(local != null)
            return local.objectId;
            
        return -1;            
    }

        
    /**
     * Drop an item
     * @param i The index in the inventory
     * @param amount The amount of units to drop
     */

    private void dropSelectedItem(int units) {
        Item it = playerObject.inventory.getSelectedItem();

        if (it!=null) {
            sendDropItemMessage(it.objectId, units);
            playerObject.inventory.removeSelectedItem(units);
            if (it.units > 0) {
                // be sure number of units for sale is valid
                if (it.unitsSell > it.units) {
                    it.unitsSell = it.units;
                }

            }
            atDisplay_Item = null;
        }
    
    }

    private boolean changeSelectedItemEquipped(boolean equipped) {
        Item it = playerObject.inventory.getSelectedItem();

        if (it != null) {
            if (    (equipped && playerObject.inventory.equip(it))
                ||  (!equipped && playerObject.inventory.unequip(it))
                    ) {
                    sendEquipChange(it.objectId, equipped);
                    return true;
            }
        }

        return false;
    }


    /*
    private void dropItem(int i, int amount) {
        //only drop item if inventory element is not empty

        if (invItems[i]!=null) {
            
            sendDropItemMessage(invItems[i].objectId, xPos + playerScreenX, yPos + playerScreenY - TOP_INFOHEIGHT, amount);

            removeItemFromInventory(i, amount, false, false);
            atDisplay_Item = null;
            */
            /*
            if (invItems[i].units - amount <= 0) {
                // remove item from inventory
                //System.out.println("Putting to dropped items...");
                //idToTempDroppedItems.put("" + invItems[i].objectId, invItems[i]);
                System.out.println("Call remove from inv...");
                removeItemFromInventory(i, -1, false, false);  // server will unequip itself, if neccessary
            } else if (invItems[i].units > 0) {    // still units left
                removeItemFromInventory(i, amount, false, false);
            }
            */
            /*
        }
    }
    */
    
    /**
     * @return True if application should switch back to belt substate
     */
    private boolean onBeltUse() {
        if (belt[selectedBeltItem] != null) {
            return useItem(selectedBeltItem, false); 
        }
        
        return false;
    }
    
    
    
    /**
     * Use an item.
     * @i The index in the inventory
     */
    private boolean useItem(int selIndex, boolean fromInventory) {
        boolean returnToInventory = true;
        
        Item it = null;
        int beltIndex = -1;    // used to identify matching slot index in belt or inventory
        int invIndex = -1;    // used to identify matching slot index in belt or inventory
        
        int subStateAfterFailMessage = SUBSTATE_INVENTORY;
        
        if (fromInventory) {
            invIndex = selIndex;
            if (selIndex >= 0 && selIndex < invItemsCount) it = invItems[selIndex];
            // in case the item is also found in the belt, make sure to find its belt index (belt slot)
            if (it != null && it.equipped > 1 && it.equipped - 2 < MAX_BELT_ITEMS) {
                beltIndex = it.equipped - 2;   // retrieve belt slot
            }
        } else {
            // activated from belt
            subStateAfterFailMessage = SUBSTATE_BELT;
            beltIndex = selIndex;
            if (selIndex >= 0 && selIndex < MAX_BELT_ITEMS) it = belt[selIndex];
            // in case the item is valid it must also be found in the inventory
            if (it != null) {
                for (j=0; j<invItemsCount; j++) {
                    if (invItems[j].objectId == it.objectId) {
                        // found item, mark index
                        invIndex = j;
                        // synchronize inv marker with belt marker because ground find / trigger find states use invItems[selectedInvItem]
                        selectedInvItem = j;
                        break;
                    }
                }
            }
        }
        
        if (it!=null && ((it.units > 0 || it.units == -1) && it.requiredSkill * 10 <= skill && it.requiredMagic * 10 <= magic) && it.triggertype>0) {

            if (it.triggertype < 70) {
                // this item is dierctly usable without selection of a target (e.g. curHealth / curMana potion)
                
                if (it.triggertype == 1 && ( (it.healthBase > 0 && playerObject.curHealth >= playerObject.getTotalMaxHealth()) || (it.manaBase > 0 && playerObject.getTotalMaxMana() >= maxmana) ) )  {
                    subStateOKDialog("Already full vitality!", currentState, subStateAfterFailMessage);
                    return false;
                }
                
                // just use the item
                sendRequestItemTrigger(it.triggertype, it.objectId, 0);
                triggerTarget_TriggerType = it.triggertype;
                triggerTarget_ItemID = it.objectId;
                // adjust units / remove from inventory
                if (it.units != -1 && invIndex > -1) {
                    removeItemFromInventory(invIndex, 1, false, false); // server will unequip itself, if neccessary
                }
                atDisplay_Item = null;
                return true; // return to inventory mode
                
            } else if (it.triggertype < 90) {  
                // trigger on target
                triggerTarget_TriggerType = it.triggertype;
                
                // fire wall, mass attackBase, mass heal
                if (triggerTarget_TriggerType >= 76 && triggerTarget_TriggerType <= 78) {    
                    // $-> TODO: possibly change trigger num?
                    if (playerObject.getTotalMaxMana() + it.manaBase < 0) {
                        subStateOKDialog("Not enough mana power! (" + maxmana/10 + "." + maxmana % 10 + ")" , currentState, subStateAfterFailMessage);
                        returnToInventory = false;
                    } else if (playerObject.curMana + it.manaBase < 0) {
                        subStateOKDialog("Not enough mana!", currentState, subStateAfterFailMessage);
                        returnToInventory = false;
                    } else {
                        currentSubState = SUBSTATE_GROUND_FIND;    // set state
                        returnToInventory = false;
                        setBottomCommand1("Select");
                        setBottomCommand2("Back");
                        
                        // the state to return to must be the same as the one which is used after a fail message
                        triggerOrGroundFindReturnState = subStateAfterFailMessage;
                        
                        if (triggerTarget_TriggerType > 76) {
                            if (triggerTarget_TriggerType == 78) {  // mass heal
                                bDrawHealthAll = true;  // show curHealth info at players for mass heal
                            }
                            // ground cursor position for mass action
                            groundCursorX = (xPos + (DISPLAYWIDTH/2)) / TILEWIDTH;
                            groundCursorY = (yPos + (DISPLAYHEIGHT/2)) / TILEHEIGHT;
                            
                            if (groundCursorX > playfieldWidth-1) {groundCursorX = playfieldWidth - 1;}
                            if (groundCursorY > playfieldWidth-1) {groundCursorY = playfieldHeight - 1;}
                        } else {
                            // set most suitable ground cursor position
                            k4 = curCellY * TILEHEIGHT + 11;
                            k3 = curCellX * TILEWIDTH + 11;
                            // check top position
                            if (curCellY > 0  && notBlocked(k3, k4-TILEHEIGHT, false)) {
                                groundCursorX = curCellX;
                                groundCursorY = curCellY-1;
                            // check left position
                            } else if (curCellX > 0 && notBlocked(k3-TILEWIDTH, k4, false)) {
                                groundCursorX = curCellX-1;
                                groundCursorY = curCellY;
                            // check right position
                            } else if (curCellX < (playfieldWidth-1) && notBlocked(k3+TILEWIDTH, k4, false)) {
                                groundCursorX = curCellX+1;
                                groundCursorY = curCellY;
                            // check bottom position
                            } else if (curCellY < (playfieldHeight-1) && notBlocked(k3, k4+TILEHEIGHT, false)) {
                                groundCursorX = curCellX;
                                groundCursorY = curCellY+1;
                            // if no other free cell is left set cursor on same tile as player stands on
                            } else {
                                groundCursorX = curCellX;
                                groundCursorY = curCellY;
                            }
                        }
                   }
                } else {    // trigger on target
                    // the state to return to must be the same as the one which is used after a fail message
                    triggerOrGroundFindReturnState = subStateAfterFailMessage;
                    
                    triggerTarget_TriggerType = it.triggertype;
                    // do not reset target selection if we came from: fightstate->belt and spell effect is attackBase
                    // -> i.e. keep target if we had some player selected for attackBase and the item used in the belt should cause another attackBase
                    boolean resetSelection = !(currentSubState == SUBSTATE_BELT && (beltUseReturnState == SUBSTATE_FIGHT_ACTIVE || beltUseReturnState == SUBSTATE_FIGHT_FIND) && triggerTarget_TriggerType != 70 && triggerTarget_TriggerType != 73);

                    // allow correct selection of players including own
                    currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                    
                    
                    if (getPlayersOnScreen(true, 0, resetSelection, -1)==0) {
                        subStateOKDialog("No one in range.", currentState, subStateAfterFailMessage);
                        returnToInventory = false;                    
                     } else if (playerObject.getTotalMaxMana() + it.manaBase < 0) {
                        subStateOKDialog("Not enough mana power! (" + maxmana/10 + "." + maxmana % 10 + ")" , currentState, subStateAfterFailMessage);
                        returnToInventory = false;                    
                     } else {
                        returnToInventory = false;                    
                        // must select a target for the use of this item
                        setBottomCommand1("Sel. Target");
                        setBottomCommand2("Back");
                        currentSubState = SUBSTATE_TRIGGERTARGET_FIND;

                        if (it.triggertype == 70) {
                            bDrawHealthAll = true;
                        } else {
                            bDrawHealthAll = false;
                        }

                        // save details of this item trigger to send it later
                        triggerTarget_ItemID = it.objectId;

                     }
                }
               lastCheck = curGametime;
               resetInventoryScrollHugeItemSettings();
            }
        } else if ((it.units > 0 || it.units == -1) && (it.requiredSkill * 10 > skill || it.requiredMagic * 10 > magic)) {
            subStateOKDialog("Cannot use item.\n\nCharacter does not meet the requirements.", currentState, subStateAfterFailMessage);
            returnToInventory = false;
        }
        return returnToInventory;
    }
    
    
    /**
     * Remove an item from the inventory, adjusting everything related to it.
     */
    private boolean removeItemFromInventory(int index, int amount, boolean notifyServerOnUnequip, boolean notifyServerOnTradeCancel) {
        boolean removed = false;
        
         if (amount > 0) {
            if (invItems[index].units > 0) { // usable item
                invItems[index].units -= amount;
                if (invItems[index].units < 0) {
                    invItems[index].units = 0;
                }
            } else if (invItems[index].units < 0) {  // item that can be equipped
                invItems[index].units = 0;
            }
        }
        
        if (invItems[index].units==0 || amount == -1) {  // item to be removed completey
            // make sure it is not for sale anymore (in case item was dropped and drop fails)      
            //cancelSale(index, notifyServerOnTradeCancel);
            
            // unequip if necessary
            if (invItems[index].equipped==1) {
                unequip(invItems[index], notifyServerOnUnequip, true);
            } else if (invItems[index].equipped > 1) {
                // item is set in belt
                removeFromBelt(invItems[index], false);
            }
            invItems[index] = null;
            removed = true;
            
        } else  if (invItems[index].units > 0 ) {    // item still usable
            if (invItems[index].unitsSell > invItems[index].units) { // check if trade offer should be cancelled
                // send explicit trade cancel, if neccessary
                cancelSale(notifyServerOnTradeCancel);
            }
        }
        
        if (removed) {
            //rearrange items
            for (int i=index; i<invItemsCount-1; i++) {
                invItems[i]=invItems[i+1];
            }

            if (invItemsCount!=0) {
                invItems[invItemsCount-1]=null;
            }

            invItemsCount--;
            if ((index < selectedInvItem || selectedInvItem==invItemsCount) && selectedInvItem > 0) {
                selectedInvItem--;
            }
        }
        atDisplay_Item = null;
        return removed;
    }




    
    /** 
     * Add a gameobject / item to the player's inventory or tradeoffer display. 
     * @param fwgo The item to add
     * @param inventory True if it should be added to the inventory, 
     * false if it should be added to the tradeOfferItems
     */
    private int addItemToInventory(Item fwgo, boolean inventory) {
        if (inventory) {
            if (invItemsCount < INVENTORY_COLS*INVENTORY_ROWS) {
                //add to inventory
                invItems[invItemsCount]=fwgo;
                invItemsCount++;
                return invItemsCount-1;
            }
        } else {
            if (tradeOfferItemsCount < INVENTORY_COLS*INVENTORY_ROWS) {
                //add to inventory
                tradeOfferItems[tradeOfferItemsCount]=fwgo;
                tradeOfferItemsCount++;
                return tradeOfferItemsCount-1;
            }
        }
        return -1;
    }


    /** 
     * Try to pick up an item.. it will be added to the inventory once the "f_ai" (add item) message
     * is received.
     */
    private void pickupItem(Item it) {
        // todo: stackable items - for usable items
        if (!playerObject.inventory.isFull()) {
            sendPickupItemMessage(it.objectId);
            showBottomInfo("Taking item ..", 2000, false);
        } else {
            overlayMessage("Inventory full!");
        }
    }

    /**
     * Try to find an item in t he ineventory which is suitable for 
     * stacking the given item onto it.
     * @param it The item which should be stacked
     */
    private int findStackableItemInInventoryFor(Item it) {
        if (it.units <= 0) {
            return -1;
        }
        
        for (int i=invItemsCount; --i>=0; ) {
            if (invItems[i]!=null) {
                if (invItems[i].units > 0
                    && invItems[i].triggertype == it.triggertype 
                    && invItems[i].classId == it.classId
                    && invItems[i].subclassId == it.subclassId
                    && invItems[i].graphicsX == it.graphicsX
                    && invItems[i].graphicsY == it.graphicsY
                    && invItems[i].healthBase == it.healthBase
                    && invItems[i].healthregenerateBase == it.healthregenerateBase
                    && invItems[i].manaBase == it.manaBase
                    && invItems[i].manaregenerateBase == it.manaregenerateBase
                    && invItems[i].attackBase == it.attackBase
                    && invItems[i].defenseBase == it.defenseBase
                    && invItems[i].skillBase == it.skillBase
                    && invItems[i].magicBase == it.magicBase
                    && invItems[i].damageBase == it.damageBase
                    && invItems[i].frequency == it.frequency
                    && invItems[i].requiredSkill == it.requiredSkill
                    && invItems[i].requiredMagic == it.requiredMagic
                    && invItems[i].data == it.data
                    && invItems[i].name.equals(it.name)
                    && invItems[i].description.equals(it.description)
                    ) 
                {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    
    private void finishBuyItem(int index) {
        if (invItemsCount == INVENTORY_COLS * INVENTORY_ROWS) { // inventory full
            // inventory is full try to find a stackable item in the inventory
            // for the item the player is trying to buy
            int invIndexOfStackableItem = findStackableItemInInventoryFor(tradeOfferItems[index]);
            if (invIndexOfStackableItem == -1) {
                overlayMessage("Inventory full!");
                return;
            }
        }

        sendBuyObjectMessage(tradeOfferItems[index].objectId, sellerID, tradeOfferItems[index].gold, tradeOfferItems[index].unitsSell);
        // immediately subtract gold (if buying fails gold will be added again)
        playerObject.gold -= tradeOfferItems[index].gold;
//#if Series40_MIDP2_0
//#                 replaceNumber(playerGoldWindow.text, playerObject.gold, 0, 5);
//#else
        replaceNumber(playerGoldWindow.text, playerObject.gold, 2, 7);
//#endif

        // remove item from tadeOfferItems
        tradeOfferItems[index] = null;

        //rearrange items
        for (int i=index; i<tradeOfferItemsCount-1; i++) {
            tradeOfferItems[i]=tradeOfferItems[i+1];
        }

        if (tradeOfferItemsCount!=0) {
            tradeOfferItems[tradeOfferItemsCount-1]=null;
        }

        tradeOfferItemsCount--;
        if ((tradeOfferItemsCount < 3 || selectedTradeOfferItem==tradeOfferItemsCount) && selectedTradeOfferItem > 0) {
            selectedTradeOfferItem--;
        }
    
    }
    
    /**
     * Try to buy an item that was offered for sale.
     * @param index The index of the item in the tradeoffer display
     */
    private void buyItem(int index) {
        if (index>=0 && index<tradeOfferItems.length && tradeOfferItems[index]!=null) {
            if (playerObject.gold < tradeOfferItems[index].gold) {
                overlayMessage("You don't have enough\ngold!");
            } else {
                promptConfirm("Buy this item?\n\nGold: " + tradeOfferItems[index].gold);
                currentSubState = SUBSTATE_TRADE_BUY_CONFIRM;
            }
        }
    }
    
    /*
    private void drawWeaponRecharge() {
        d4 = TOTALHEIGHT - BOTTOM_INFOHEIGHT - 29;
        currentGraphics.setClip(1, d4-1, 7, 28);
        
        //background
        if (isInWeaponRange) {
            currentGraphics.setColor(0,255,0);
        } else {
            currentGraphics.setColor(255,192,0);
        }
        currentGraphics.fillRect(2, d4, 6, 26);
        if (playerObject.weaponRechargeStartTime>0) {
            d1 = (int) ((playerObject.weaponRechargeStartTime * 26) / playerObject.weaponRechargeFullDuration);
            //negative gauge
            currentGraphics.setColor(0,0,0);
            currentGraphics.fillRect(2, d4, 6, d1);
            currentGraphics.setColor(32,64,0);
        } else {
            currentGraphics.setColor(0,80,0);
        }
        currentGraphics.drawRect(1, d4-1, 6, 26);
    }
    */
    private void drawHealthManaState(Character fwgo, long gameTime, int size) {
        
        if (fwgo==null)
            return;
        
        if (fwgo.objectId==character_DB_ID && isPeaceful(fwgo.x + (PLAYERWIDTH_HALF), fwgo.y + (PLAYERHEIGHT_HALF))) {
            d6 = (fwgo.graphicsDim * DIM) >> 1;
            d1 = fwgo.x + d6 - xPos - 5;
            d2 = fwgo.y - yPos - 8  + TOP_INFOHEIGHT;
            currentGraphics.setClip(d1, d2, 10, 6);
            currentGraphics.drawImage(GlobalResources.imgIngame, d1-32, d2-23, anchorTopLeft); // peaceful icon
        } else {
            if (fwgo.objectId!=character_DB_ID) {
                d1=fwgo.x-xPos;
                d2=fwgo.y-yPos+TOP_INFOHEIGHT;
                d7 = fwgo.healthBase;
                d8 = fwgo.manaBase;
                db2 = false;
            } else {
                d1=playerScreenX;
                d2=playerScreenY;
                d7 = maxhealth;
                d8 = maxmana;
                size = 2;
                db2 = true;
            }
            
            
            if (d2 > 3) {
                db1 = false;
                d1 += 4;
                d2 -= (2 + size);
                
                d9 = (fwgo.graphicsDim * DIM) - 8;
                
                currentGraphics.setClip(d1, d2, d9, size);
                if (fwgo.curHealth < d7) {
                    db1 = true;
                    // black ground
                    currentGraphics.setColor(0,0,0);
                    currentGraphics.fillRect(d1, d2, d9, size);
                    /*
                    currentGraphics.drawLine(d1 + 4, d2-4, 
                                         (PLAYERWIDTH-8) + d1 + 4, d2-4);
                    currentGraphics.drawLine(d1 + 4, d2-3, 
                                             (PLAYERWIDTH-8) + d1 + 4, d2-3);
                    */                         
                }
                // red curHealth
                if (d7!=0) {
                    if (!bDrawHealthAll || db1 || db2) {
                        currentGraphics.setColor(255,0,0);
                    } else {
                        currentGraphics.setColor(128,0,0);
                    }
                    currentGraphics.fillRect(d1, d2, (fwgo.curHealth*d9)/d7, size);
                    
                    /*
                    currentGraphics.drawLine(d1 + 4, d2-4, 
                                         ((fwgo.curHealth*(PLAYERWIDTH-8))/d7) + d1 + 4, d2-4);
                    currentGraphics.drawLine(d1 + 4, d2-3, 
                                             ((fwgo.curHealth*(PLAYERWIDTH-8))/d7) + d1 + 4, d2-3);
                     */
                }/* else {
                    //System.out.println("" + fwgo.name);
                }*/
            }
        }
                

        /*         
         if ((fwgo.subState!=0 || ((fwgo.objectId==playerObject.objectId) && chatRequest)) && currentGraphics!=null) {

            
            //System.out.println("Substate: " + fwgo.subState);
            //System.out.println("gametime: " + gameTime + " " + "endanitime: " + fwgo.extraAniEndTime);
            noAction = false;
            if (fwgo.extraAniEndTime > gameTime && items!=null) {
                currentGraphics.setClip(d1+PLAYERWIDTH_HALF, d2-2, ITEMWIDTH, ITEMHEIGHT);
                if (chatRequest) {
                    currentGraphics.drawImage(items, d1+(PLAYERWIDTH_HALF)-ITEMWIDTH, d2-2-(2*ITEMHEIGHT), anchorTopLeft);
                } else {
                    currentGraphics.drawImage(items, d1+PLAYERWIDTH_HALF, d2-2-((3+fwgo.subState)*ITEMHEIGHT), anchorTopLeft);
                } 
            } else {
                fwgo.subState=SUBSTATE_NORMAL;
                chatRequest = false;
            }

        } else if (fwgo.objectId==playerObject.objectId && noAction && currentGraphics!=null) {
            if (fwgo.extraAniEndTime > gameTime) {
                currentGraphics.setClip(d1+ITEMWIDTH, d2, 12, 12);
                currentGraphics.setColor(255,0,0);
                currentGraphics.drawLine(playerScreenX+ITEMWIDTH, playerScreenY, playerScreenX+ITEMWIDTH+12, playerScreenY+12);
                currentGraphics.drawLine(playerScreenX+ITEMWIDTH, playerScreenY+12, playerScreenX+ITEMWIDTH+12, playerScreenY);
            } else {
                noAction = false;
            }
         }
         */
    }


    
    
    
    ///////////////////////////
    // INPUT HANDLING
    ///////////////////////////
    
    /**
     * Key was pressed.
     */
     
    protected synchronized void keyPressed(int keyCode) {
        long start = System.currentTimeMillis();
        
        try { gAction = getGameAction(keyCode); } catch(Exception e) { gAction = 0; }

//System.out.println("KEYCODE:     " + keyCode);
//System.out.println("GAMEACTION:  " + gAction);

        if (overlayState != OVERLAY_NONE) {
            switch(overlayState) {
                case OVERLAY_MESSAGE:
                    if (overlayControlsTimeOut <= 0 && (gAction == FIRE || keyCode == KEY_SOFTKEY1)) {
                        overlayState = OVERLAY_NONE;
                    }
                    break;
                    
                case OVERLAY_GAMEOPTIONS:
                    s = GTools.menuButtonStatus(menuGameOptions, gAction, keyCode);
                    if (keyCode == KEY_SOFTKEY1 || gAction == FIRE) {   // SELECT OPTION
                        switch (s) {
                            case 0: // Sound ..
                                overlayState = OVERLAY_SOUND;
                                GTools.menuSetSelected(menuSound, 0);
                                ovCommand1 = false;
                                break;
                            case 1: // Show traffic
                                overlayMessage("Total Network Traffic:\n\n" + ((trafficCounterReceive + gbManager.getBytesSent())/1000) + " KB");
                                break;
                           case 2: // Credits
                                overlayState = OVERLAY_CREDITS;
                                creditid = 0;
                                if(false && netStarted) {
                                    sendRequestCredits();
                                    //System.out.println("send request credits: " + creditid);
                                    setOverlayCommand1("More");
                                } else {
                                    GTools.textWindowSetText(creditsWindow, "Fantasy Worlds: Rhynn\n\n(c) 2003-2011\n\nby AwareDreams\n\nhttp://rhynn.com\n\n\n" + versionName(true));
                                    ovCommand1 = false;
                                }
                                break;
                           case 3: // change email
                               if (netStarted && user_DB_ID > 0) {
                                    overlayState = OVERLAY_NONE;
                                    optionState = OPTIONSTATE_EMAIL_ENTRY;
                                    optionSubState = OPTIONSUBSTATE_EMAIL_GET;
                                    sendGetEmail();
                                    GTools.textWindowRemoveText(emailField1);
                                    GTools.textWindowRemoveText(emailField2);
                                    GTools.menuSetCaptionOneLine(menuEmail, "Change e-mail", font, 0);
                                    this.setWaitLabelText("Loading ..");
                                    setCommand(CM_OPTION, 1, null);
                                    setCommand(CM_OPTION, 2, "Cancel");
                                    //handleEmailEntry(true, keyCode);
                               } else {
                                   overlayMessage("You must log in to use this option.");
                               }
                               /*
                               overlayState = OVERLAY_CREDITS;
                                creditid = 0;
                                if(this.netStarted) {
                                    sendRequestCredits();
                                    //System.out.println("send request credits: " + creditid);
                                    setOverlayCommand1("More");
                                } else {
                                    GTools.textWindowSetText(creditsWindow, "Fantasy Worlds: Rhynn\n\n(c) 2003-2005\n\nby AwareDreams\n\nhttp://AwareDreams.com");
                                    ovCommand1 = false;
                                }
                                */
                                break;

                            case 4: // Exit
                                promptConfirm("Really Exit?");
                                bExitConfirm = true;
                                overlayState = OVERLAY_NONE;
                                break;
                        }
                    } else if (keyCode == KEY_SOFTKEY2) {   // BACK
                        overlayState = OVERLAY_NONE;
                    }

                    break;
                case OVERLAY_CREDITS:
                    if (keyCode == KEY_SOFTKEY2) {
                        overlayState = OVERLAY_GAMEOPTIONS;
                        setOverlayCommand1("Select");
                        GTools.textWindowSetText(creditsWindow, "\n\nloading credits...\n\n\nplease wait");
                    } else if((keyCode == KEY_SOFTKEY1 || gAction == FIRE) && netStarted) {
                        sendRequestCredits();
                        //System.out.println("send request credits: " + creditid);
                    }
                    break;

                case OVERLAY_HELP:
                    if (keyCode == KEY_SOFTKEY2) {
                        overlayState = OVERLAY_NONE;
                    } else if((keyCode == KEY_SOFTKEY1 || gAction == FIRE) && ovCommand1 && nextHelpID > 0) {
                        overlayState = OVERLAY_HELP_WAIT;
                        sendRequestNextHelpText(nextHelpID);
                    } 
                    break;
                    
                case OVERLAY_HELP_WAIT:
                    if (keyCode == KEY_SOFTKEY2) {
                        overlayState = OVERLAY_NONE;
                    } 
                    break;
                    
                    
                case OVERLAY_SOUND:
                    
                    GTools.handleInput(menuSound, gAction, keyCode);
                    if (soundPlayer==null || !soundPossible) {
                        return;
                    }
                    
                    s = GTools.menuGetSelected(menuSound);
                    if ((keyCode == KEY_SOFTKEY1 || gAction == FIRE) && s == 0) {   // CHANGE MUSIC ON | OFF
                        if (soundON) {
                            GTools.buttonSetText(buttonMusic, "Music:  OFF", false);   // set music off
                            if (soundPlayer!=null) {  // mute sound
                                soundPlayer.stopSound();
                                soundON = false;
                            }
                        } else {
                            GTools.buttonSetText(buttonMusic, "Music:  ON", false);
                            soundON = true;
                            // startPlay sound anew
                            playbackSound(0, -1);
                            /* // $-> activate!
                            if (legacyPlayfield!=null && currentState == STATE_GAME && isPeaceful((xPos + playerScreenX) + (PLAYERWIDTH_HALF), (yPos + playerScreenY-TOP_INFOHEIGHT) + (PLAYERHEIGHT_HALF))) {
                                if (curSoundType!=1) {  // not peaceful yet
                                    playbackSound(1, -1);
                                }
                            } else if (currentSubState==SUBSTATE_FIGHT_ACTIVE) {
                                   //next sound: fight
                                   playbackSound(2, 7000);
                            } else {
                                if (currentState < STATE_GAME) {
                                    playbackSound(1, -1);
                                } else {
                                    playbackSound(0, -1);
                                }
                            }
                             */
                        }
                    } else if (keyCode == KEY_SOFTKEY2) {   // BACK
                        overlayState = OVERLAY_GAMEOPTIONS;
                        ovCommand1 = true;
                    } else if (s==1) {  // SET VOLUME
                        if (gAction==LEFT) {
                            if (curSoundVolume >= 10  && curSoundType >= 0 && curSoundType < soundIDs.length) {
                                curSoundVolume -= 10;
                                if (soundPlayer!=null) {
                                    soundPlayer.setVolume(curSoundVolume);
                                }
                            }
                        } else if (gAction==RIGHT) {
                            if (curSoundVolume <= 90 && curSoundType >= 0 && curSoundType < soundIDs.length) {
                                curSoundVolume +=  10;
                                if (soundPlayer!=null) {
                                    soundPlayer.setVolume(curSoundVolume);
                                }
                            }
                        }

                    }
                    break;
                case OVERLAY_DIED:
                    if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                        overlayState = OVERLAY_NONE;
                        if (currentState==STATE_BLACK) {
                            currentState = STATE_RESPAWN_REQUEST;
                            currentSubState = SUBSTATE_NORMAL;
                        }
                        ovCommand2 = true;
                    }
                    break;
                    
                    
                     
            } // end switch overlayState
            return;
        } else if (optionState != OPTIONSTATE_NONE) {
            switch (optionState) {
                case OPTIONSTATE_EMAIL_ENTRY:
                    handleEmailEntry(true, keyCode);
                    break;
            }
            return;
        }
        
        if (bExitConfirm) {
            if (keyCode == KEY_SOFTKEY1) {    //YES, EXIT
                currentState = STATE_FORCED_EXIT;
                shutdown = true;
                confirmYesNo = false;
                bExitConfirm = false;
            } else if (keyCode == KEY_SOFTKEY2) {  //NO: DO NOT EXIT
                confirmYesNo = false;
                bExitConfirm = false;
            }
            return;
        }

        if (currentSubState == SUBSTATE_OK_DIALOG) {
            if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                if (nextSubState == SUBSTATE_OK_DIALOG) {
                    setWaitLabelText(msgOK_tmp);
                }
                msgOK_tmp = null;
                if (currentState == STATE_GAME && nextState == STATE_GAME && nextSubState == SUBSTATE_NORMAL) {
                    subStateNormal();
                } else {
                    if (nextState!=-1) {
                        currentState = nextState;
                        nextState = -1;
                    }
                    
                    if (nextSubState!=-1) {
                        currentSubState = nextSubState;
                        nextSubState = -1;
                        if (nextSubState == SUBSTATE_INVENTORY || nextSubState == SUBSTATE_BELT) {
                            setBottomCommand1("Select");
                            setBottomCommand2("Close");
                        }
                    } else {
                        currentSubState = SUBSTATE_NORMAL;
                    }
                }
                confirmOK = false;
            }
            return;
        } else if ((gameOptionsAvailable || (optionState != OPTIONSTATE_NONE && gameOptionsAvailableOp)) && keyCode == KEY_SOFTKEY2) {   // GAME OPTIONS ACTIVATED
            //( (keyCode == KEY_SOFTKEY2 && ( (currentState!=STATE_GAME && currentState!=STATE_INTRO) || (currentState==STATE_GAME && currentSubState == SUBSTATE_NORMAL) )) && bCommand2 && gameOptionsAvailable && !confirmOK && !confirmYesNo ) {
            //exit confirm
            overlayState = OVERLAY_GAMEOPTIONS;
            if (!soundPossible) {
                GTools.menuSetSelected(menuGameOptions, 1);
            } else {
                GTools.menuSetSelected(menuGameOptions, 0);
            }

            setOverlayCommand1("Select");
            setOverlayCommand2("Close");
            return;
        }
        
        switch(currentState) {

            
            case STATE_GAME:
                if (playerObject==null)
                    allowGameInput = false;
                
                if (!allowGameInput)
                    break;  //own players was not added yet
                
                if(playfieldCounter == 0) {
                    switch (currentSubState) {
                        
                        //NORMAL MODE
                        case SUBSTATE_NORMAL:
                            if (keyCode==KEY_SOFTKEY1) {    //to ACTION menu
                                currentSubState = SUBSTATE_ACTIONMENU;
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                                selectedActionMenuEntry = 0;
                                GTools.textWindowSetText(info1Line, "Fight (shortcut: 1)");
                                //GTools.menuSetSelected(menuActionSub, 0);
                            } else {
                                if (!handleMoveInput(gAction)) {
                                    //lastMoveDirection =-1;
                                    if (gAction == FIRE) {
                                        playerFireAction();
                                    } else {
                                        if (keyCode == KEY_STAR) {  
                                            // quick talk to all / shortcut
                                            openTalkToAll();
                                            currentSubState = SUBSTATE_TALKTOALL;
                                            setBottomCommand1("Options");
                                            setBottomCommand2("Back");
                                        } else if (keyCode == KEY_POUND) {
                                            // BELT
                                            // show belt, allow belt selection
                                            // !! note that belt can also be invoked from fight / fight_find states
                                            substateBelt(0);
                                            // reset trigger target flag, so we have a clue later which spell type was used from the belt (if any)
                                            // this will determine whether or not the fight selection (selected player) has to be reset when returning 
                                            // to fight / fight_find states
                                            triggerTarget_TriggerType = 0;
                                            beltUseReturnState = SUBSTATE_NORMAL;
                                        } else {
                                            switch (keyCode) {
                                                case 49:
                                                    actionMenuActivated(0);  // fight
                                                    break;
                                                case 51:
                                                    actionMenuActivated(1);  // inventory
                                                    break;
                                                case 55:
                                                    actionMenuActivated(2);  // character
                                                    break;
                                                case 57:
                                                    talkSubMenu_ShowActionMenu = false;
                                                    actionMenuActivated(3);  // talk
                                                    break;
                                            }

                                        }
                                    }
                                }
                            }
                            break;
                        
                        
                        //ACTIONMENU MODE
                        case SUBSTATE_ACTIONMENU:
                            bottomInfo_Foreground = false;
                            s = selectedActionMenuEntry;
                            if (keyCode==KEY_SOFTKEY2) {  //BACK to normal
                                subStateNormal();
                            } else if (keyCode==KEY_SOFTKEY1 || gAction == FIRE) {  //SELECT
                                //s = GTools.menuGetSelected(menuActionSub);
                                if (selectedActionMenuEntry==3) {
                                    talkSubMenu_ShowActionMenu = true;
                                }    
                                actionMenuActivated(selectedActionMenuEntry);
                            } else if (gAction == LEFT || gAction == UP) {
                                selectedActionMenuEntry = selectedActionMenuEntry - 1;
                                if (selectedActionMenuEntry < 0) {
                                    selectedActionMenuEntry = ACTION_ITEMS-1;
                                }
                                //GTools.handleMenuInput(menuActionSub, gAction, keyCode);
                            }  else if (gAction == RIGHT || gAction == DOWN) {
                                selectedActionMenuEntry = (selectedActionMenuEntry + 1) % ACTION_ITEMS;
                            }
                            if (s!=selectedActionMenuEntry && currentSubState==SUBSTATE_ACTIONMENU) {
                                switch (selectedActionMenuEntry) {
                                    case 0:
                                        GTools.textWindowSetText(info1Line, "Fight (shortcut: 1)");
                                        break;
                                    case 1:
                                        GTools.textWindowSetText(info1Line, "Inventory (shortcut: 3)");
                                        break;
                                    case 2:
                                        GTools.textWindowSetText(info1Line, "Character (shortcut: 7)");
                                        break;
                                    case 3:
                                        GTools.textWindowSetText(info1Line, "Talk (shortcut: 9)");
                                        break;
                                    case 4:
                                        GTools.textWindowSetText(info1Line, "Trade");
                                        break;
                                    case 5:
                                        GTools.textWindowSetText(info1Line, "Quests");
                                        break;
                                    case 6:
                                        GTools.textWindowSetText(info1Line, "Friend Options");
                                        break;
                                        
                                }
                            }
                            break;

                        //INVENTORY MODE
                        case SUBSTATE_INVENTORY:
                            k1 = selectedInvItem;
                            if (!inventoryCheckNavigationInput(gAction, playerObject.inventory)) {
                                if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {    // OPTIONS -> Item context options
                                    Item selItem = playerObject.inventory.getSelectedItem();
                                    if (selItem!=null) {
                                        // change to context options, if an item is at the selected slot
                                        if (setItemOptions(selItem)) {
                                            currentSubState = SUBSTATE_INVITEM_OPTIONS;
                                            setBottomCommand2("Back");
                                            // position context menu below the item
                                            int xOff = playerObject.inventory.getSelectedXOffset();
                                            int yOff = playerObject.inventory.getSelectedYOffset();
                                            GTools.windowSetPosition(menuFreeContextOptions, xOff, TOP_INFOHEIGHT + yOff + Item.SLOT_HEIGHT);
                                        }
                                    }
                                } else if (keyCode==KEY_SOFTKEY2) { //CLOSE
                                    playerObject.inventory.selectSlot(0);
                                    selectedInvItem = 0;
                                    atDisplay_Item = null;
                                    subStateNormal();
                                }
                            } else {
                                /*
                                if (selectedInvItem != k1 && selectedInvItem >= 0 && selectedInvItem < invItems.length) { //another item was selected
                                    
                                }
                                 */
                            }
                            break; //END INVENTORY
                        
                        case SUBSTATE_INVITEM_OPTIONS:
                            s = GTools.menuButtonStatus(menuFreeContextOptions, gAction, keyCode);
                            kb1 = false;
                            if (gAction==FIRE || keyCode==KEY_SOFTKEY1) {   // activate option
                                Item it = playerObject.inventory.getSelectedItem();
                                switch (s) {
                                    case 0: // USE
                                        //overlayMessage(NOT_IMPLEMENTED);
                                        sendUseItem(it.objectId);


                                        kb1 = true;
                                        //kb1 = useItem(selectedInvItem, true);
                                        break;
                                    case 1: // EQUIP
                                        if (!changeSelectedItemEquipped(true)) {
                                            overlayMessage("Item could not\nbe equipped!");
                                        }
                                        kb1=true;
                                        break;
                                    case 2: // UNEQUIP
                                        if (!changeSelectedItemEquipped(false)) {
                                            overlayMessage("Item could not\nbe unequipped!");
                                        }

                                        /*
                                        if (it!=null) {
                                            unequip(invItems[selectedInvItem], true, true);
                                        }*/
                                        kb1=true;
                                        break;
                                    case 3:
                                        overlayMessage(NOT_IMPLEMENTED);
                                        kb1 = true;
                                        /*
                                        // add to belt -> select slot
                                        currentSubState = SUBSTATE_BELT_SELECT_SLOT;
                                        selectedBeltItem = 0;
                                        setBottomCommand1("Select");
                                        setBottomCommand2("Cancel");
                                         */
                                        break;
                                    
                                    case 4:
                                        overlayMessage(NOT_IMPLEMENTED);
                                        /*
                                        // remove from belt
                                        if (it!=null) {
                                            removeFromBelt(invItems[selectedInvItem], true);
                                        }*/
                                        kb1=true;
                                        break;
                                        
                                    case 5: // SALE OFFER
                                        overlayMessage(NOT_IMPLEMENTED);
                                        kb1 = true;
                                        /*
                                        if (it!=null) {
                                            currentSubState = SUBSTATE_SET_ITEMOFFER;
                                            //GTools.inputWindowRemoveText(priceInput);
                                            GTools.inputWindowRemoveText(amountInput);
                                            
                                            // prepare price input
                                            */
                                        /*
                                            if (itemTmpK.gold > 0) {
                                                GTools.textWindowSetText(priceInput, charArrayFromInt(itemTmpK.gold));
                                                GTools.inputWindowSetCursorToLineEnd(priceInput);
                                            }

                                            // prepare amount input
                                            if (it.units > 0) {
                                                if (it.units > 1) {
                                                    amountInput.selectable = true;
                                                }
                                                if (it.unitsSell <= it.units && it.unitsSell > 0) {
                                                    GTools.textWindowSetText(amountInput, charArrayFromInt(it.unitsSell));
                                                } else {
                                                    if (it.units < 999) {
                                                        GTools.textWindowSetText(amountInput, charArrayFromInt(it.units));
                                                    } else {
                                                        GTools.textWindowSetText(amountInput, charArrayFromInt(999));
                                                    }
                                                }
                                                
                                            } else {
                                                // fixed amount: 1
                                                GTools.textWindowSetText(amountInput, charArrayFromInt(1));
                                                amountInput.selectable = false;
                                            }
                                            GTools.inputWindowSetCursorToLineEnd(amountInput);
                                            GTools.menuSetSelected(priceBox, 1);
                                            
                                            GTools.menuSetCaptionOneLine(priceBox, "Set item for sale", font, 0);
                                            //GTools.labelSetText(labelPrice, "Price (gold):", false);
                                            GTools.windowCenterXY(priceBox, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);

                                            setBottomCommand1("OK");
                                            setBottomCommand2("CANCEL");
                                            itemTmpK = null;
                                        }*/
                                        break;
                                    case 6: // CANCEL SALE OFFER
                                        overlayMessage(NOT_IMPLEMENTED);
                                        kb1 = true;
                                        /*
                                        cancelSale(true);
                                        kb1=true;
                                         */
                                        break;
                                    case 7: // DROP
                                        if (it!=null) {
                                            if (it.units > 1) {   // Prompt for amount input
                                                currentSubState = SUBSTATE_SET_DROPITEM_AMOUNT;
                                                // prepare amount input
                                                if (it.units < 999) {
                                                    GTools.textWindowSetText(dropAmountInput, charArrayFromInt(it.units));
                                                } else {
                                                    GTools.textWindowSetText(dropAmountInput, charArrayFromInt(999));
                                                }
                                                setBottomCommand1("DROP");
                                                setBottomCommand2("CANCEL");
                                            } else {
                                                dropSelectedItem(1);  // automatic unequip if necessary
                                                kb1=true;
                                            }
                                        } 
                                        break;
                                    case 8: // SORT
                                        overlayMessage(NOT_IMPLEMENTED);
                                        kb1 = true;
                                        /*
                                        // todo: use new algorithm
                                        for(k1=invItemsCount-1; k1>=0; k1--) {
                                              k3 = (invItems[0].classId<<7) + invItems[0].subclassId;
                                            for(k2=0; k2<k1; k2++) {
                                                k5 = (invItems[k2+1].classId<<7) + invItems[k2+1].subclassId;
                                                if(k5 > k3) {
                                                    itemTmpK = invItems[k2];
                                                    invItems[k2] = invItems[k2 + 1];
                                                    invItems[k2 + 1] = itemTmpK;
                                                } else {
                                                    k3 = k5;
                                                }
                                            }
                                        }
                                        kb1=true;
                                         */
                                        break;
                                }
                            } else if (keyCode==KEY_SOFTKEY2) { //BACK
                                kb1=true;
                            } 

                            if (kb1) {
                                currentSubState = SUBSTATE_INVENTORY;
                                resetInventoryScrollHugeItemSettings();
                                setBottomCommand1("Select");
                                setBottomCommand2("Close");
                            }
                            
                            break;


                        case SUBSTATE_BELT_SELECT_SLOT:
                            bottomInfo_Foreground = false;    
                            handleBeltSelection(gAction, keyCode, false);
                            break;
                            
                        case SUBSTATE_BELT:
                            bottomInfo_Foreground = false;        
                            handleBeltSelection(gAction, keyCode, true);
                            break;
                            
                            //
                            // DEFINE A GOLD PRICE
                            //
                        case SUBSTATE_SET_ITEMOFFER:
                            
                            if (!GTools.handleMenuInput(priceBox, gAction, keyCode)) {
                                kb1 = false;
                                if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                                    Item it =playerObject.inventory.getSelectedItem();
                                    if (it!=null) {
                                        // parse the gold amount
                                        //int price = NetTools.intFromCharArray(GTools.inputWindowGetText(priceInput));
                                        int amount = intFromCharArray(GTools.inputWindowGetText(amountInput));

                                        if (amount==0 || (it.units > 0 && amount > it.units)) {
                                            //subStateOKDialog("Invalid amount!\nMin.: 1\nMax.: " + invItems[selectedInvItem].units, currentState, currentSubState);
                                            overlayMessage("Invalid number of units!\n\nMin.: 1\nMax.: "  + it.units);
                                        /*} else if (price==0) {
                                            //subStateOKDialog("Invalid price!", currentState, currentSubState);
                                            overlayMessage("Invalid price!");
                                        */} else {
                                            //invItems[selectedInvItem].gold = price;
                                            it.price = 1;
                                            it.unitsSell = amount;
                                            sendItemOffer(it.objectId, amount);
                                            kb1 = true;
                                        }
                                    } else {
                                        overlayMessage("Could not set for sale.\nItem was removed!");
                                        kb1 = true;
                                    }
                                } else if (keyCode == KEY_SOFTKEY2) {
                                    kb1 = true;
                                }
                                if (kb1) {
                                    setBottomCommand1("Select");
                                    setBottomCommand2("Close");
                                    currentSubState = SUBSTATE_INVENTORY;
                                    resetInventoryScrollHugeItemSettings();
                                }
                            }
                            
                            break;

                            
                            //
                            // DEFINE AMOUNT (UNITS) TO DROP
                            //
                        case SUBSTATE_SET_DROPITEM_AMOUNT:

                            Item it =playerObject.inventory.getSelectedItem();

                            kb1 = false;
                            if (gAction == UP) {
                                k4 = intFromCharArray(GTools.inputWindowGetText(dropAmountInput));
                                if (k4 == it.units || k4 == 999) {
                                    k4 = 5;
                                } else {
                                    if (k4 >= 50) {
                                        k4 += 10;
                                    } else {
                                        k4 += 5;
                                    }

                                    if (k4%5 != 0) {
                                       k4 -= (k4%5);
                                    }
                                }

                                if (k4 > it.units || k4 < 1) {
                                    k4 = it.units;
                                }
                                if (k4 > 999) {
                                    k4 = 999;
                                }
                                
                                tmpCharsK = charArrayFromInt(k4);
                                GTools.textWindowSetText(dropAmountInput, tmpCharsK);
                                tmpCharsK = null;

                            } else if (gAction == DOWN) {
                                k4 = intFromCharArray(GTools.inputWindowGetText(dropAmountInput));
                                if (k4-5 < 5 && k4-5 > 0) {
                                    k4 = 5;
                                } else if (k4 - 5 == 0) {
                                    k4 = it.units;
                                } else {
                                    if (k4 >= 60) {
                                        k4 -= 10;
                                    } else {
                                        k4 -= 5;
                                    }

                                    if (k4%5 != 0) {
                                       k4 += (5-(k4%5));
                                    }
                                }

                                if (k4 < 1 || k4 > it.units) {
                                    k4 = it.units;
                                }
                                if (k4 > 999) {
                                    k4 = 999;
                                }
                                
                                tmpCharsK = charArrayFromInt(k4);
                                GTools.textWindowSetText(dropAmountInput, tmpCharsK);
                                tmpCharsK = null;

                            } else if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                                //if (selectedInvItem >= 0 && selectedInvItem < invItems.length && invItems[selectedInvItem]!=null) {
                                if (it!=null) {
                                    // parse the unit amount
                                    int amount = intFromCharArray(GTools.inputWindowGetText(dropAmountInput));
                                    //dropItem(selectedInvItem, amount);
                                    dropSelectedItem(amount);
                                    /*
                                    if (amount<=0 || (invItems[selectedInvItem].units > 0 && amount > invItems[selectedInvItem].units)) {
                                        //subStateOKDialog("Invalid units!\nMin.: 1\nMax.: " + invItems[selectedInvItem].units, currentState, currentSubState);
                                        if (invItems[selectedInvItem].units >= 5) {
                                            overlayMessage("Invalid units!\nMin.: 5\nMax.: " + invItems[selectedInvItem].units);
                                        } else {
                                            overlayMessage("Invalid units!\nMin.: " + invItems[selectedInvItem].units + "\nMax.: " + invItems[selectedInvItem].units);
                                        }
                                    }
                                     */
                                    kb1 = true;
                                } else {
                                    overlayMessage("Item could not be dropped.");
                                    kb1 = true;
                                }
                            } else if (keyCode == KEY_SOFTKEY2) {
                                kb1 = true;
                            }
                            if (kb1) {
                                setBottomCommand1("Select");
                                setBottomCommand2("Close");
                                currentSubState = SUBSTATE_INVENTORY;
                                resetInventoryScrollHugeItemSettings();
                            }
                            
                            break;                            
                            
                         case SUBSTATE_FIGHT_FIND:
                            if (keyCode == KEY_SOFTKEY1 || gAction == FIRE || keyPressedNoConflict(keyCode, KEY_NUM1)) {
                                if (playfieldView.characterIsPeaceful(playerObject)) {
                                    overlayMessage("Cannot fight in\npeaceful area!");
                                } else {
                                    currentSubState = SUBSTATE_FIGHT_ACTIVE;
                                    // note: we do not reset weapon recharge
                                    setBottomCommand1("Attack");
                                    setBottomCommand2("Back");
                                }
                            } else if (keyCode == KEY_POUND) {
                                // BELT
                                // show belt, allow belt selection
                                // $-> todo: adjust
                                substateBelt(0);
                                // reset trigger target flag, so we have a clue later which spell type was used from the belt (if any)
                                // this will determine whether or not the fight selection (selected player) has to be reset when returning
                                // to fight / fight_find states
                                triggerTarget_TriggerType = 0;
                                beltUseReturnState = SUBSTATE_FIGHT_FIND;
                            } else if (keyCode == KEY_SOFTKEY2) {
                                subStateNormal();
                            } else if (!handleFindInput(gAction, keyCode, true) && playfieldView.getSelectedCharacterId() == 0) {
                                subStateOKDialog("No one in range", STATE_GAME, SUBSTATE_NORMAL);
                            }



                            /*
                            handleFindInput(gAction, keyCode, SUBSTATE_FIGHT_FIND, SUBSTATE_FIGHT_ACTIVE);
                            if (currentSubState == SUBSTATE_FIGHT_ACTIVE) {
                                if (isPeaceful(playerObject.x + ((playerObject.graphicsDim*DIM)/2), playerObject.y + ((playerObject.graphicsDim*DIM)/2))) {
                                    overlayMessage("Cannot fight in\npeaceful area!");
                                    currentSubState = SUBSTATE_FIGHT_FIND;
                                } else {
                                     // $-> activate!
                                    //if (soundON && soundPossible && curSoundType!=2 && !isPeaceful((playerObject.x) + (PLAYERWIDTH_HALF), (playerObject.y) + (PLAYERHEIGHT_HALF))) {
                                      //  playbackSound(2, 7000);  // enable fight sound
                                    //}
                                     
                                    // do not reset weapon recharge
                                    //weaponRechargeStartTime = 0;
                                    setBottomCommand1("Attack");
                                    setBottomCommand2("Back");
                                }
                            } else if (keyCode == KEY_POUND) {
                                // BELT
                                // show belt, allow belt selection
                                // $-> todo: adjust
                                substateBelt(0);
                                // reset trigger target flag, so we have a clue later which spell type was used from the belt (if any)
                                // this will determine whether or not the fight selection (selected player) has to be reset when returning 
                                // to fight / fight_find states
                                triggerTarget_TriggerType = 0;
                                beltUseReturnState = SUBSTATE_FIGHT_FIND;
                            }*/

                            break;
                            
                        //FIGHT
                        case SUBSTATE_FIGHT_ACTIVE:
                            Character target = playfieldView.getSelectedCharacter();
                            if (target == null)
                            {
                                if (playfieldView.selectClosestCharacter(true)!=null) {
                                    // selected character changed - switch to find mode
                                    setBottomCommand1("Sel. Target");
                                    setBottomCommand2("Back");
                                    currentSubState = SUBSTATE_FIGHT_FIND;
                                } else {
                                    subStateNormal();
                                }
                            }

                            // $-> master continue
                            if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {    //ATTACK
                                //System.out.println("time diff: " + (System.currentTimeMillis() - actualTime));
                                if (playfieldView.attackPossible(target, true)) {
                                    sendAttackMessage(target.objectId);
                                    playerObject.startRechargeForAttack();
                                    playfieldView.onAttackCharacter(playerObject.objectId, target.objectId);
                                    /*
                                    target.attackShowDuration = MAX_ATTACKSHOWDURATION;
                                    target.hitShowDuration = 1500;
                                    target.flashPhaseDuration = 450;
                                    target.flashPhase = true;
                                    target.icon = ICON_HITLOCAL;
                                    playerObject.attackAnimate = 2;
                                     */
                                } else {
                                    // not in range, or weapon not recharged
                                    playerFireAction();
                                }
                            } else if ( keyCode == KEY_SOFTKEY2 ) { //BACK
                                setBottomCommand1("Sel. Target");
                                setBottomCommand2("Back");
                                currentSubState = SUBSTATE_FIGHT_FIND;
                            } else if (keyCode == KEY_POUND) {
                                // BELT
                                // show belt, allow belt selection
                                substateBelt(0);
                                // reset trigger target flag, so we have a clue later which spell type was used from the belt (if any)
                                // this will determine whether or not the fight selection (selected player) has to be reset when returning
                                // to fight / fight_find states
                                triggerTarget_TriggerType = 0;
                                beltUseReturnState = SUBSTATE_FIGHT_FIND;
                            } else {
                                handleMoveInput(gAction);
                            }
                            break;
                            
                        case SUBSTATE_FAR_PORTAL_LIST:
                            GTools.handleInput(bigList, gAction, keyCode);
                            if (keyCode==KEY_SOFTKEY2) {  //BACK to game
                                subStateNormal();
                            } else if (keyCode==KEY_SOFTKEY1 || gAction == FIRE) {  //SELECT
                                if (bigList.entries.size() > 0) {   // use far portal
                                    Integer I = (Integer)GTools.listGetData(bigList);
                                    if (I!=null) {
                                        currentSubState = SUBSTATE_PORTAL_WAIT;
                                        setWaitLabelText("Portal Jump..");
                                        bCommand1 = false;
                                        setBottomCommand2("Game");
                                        playerMove = false;
                                        sendPos = false;
                                        sendRequestFarPortalJump(I.intValue());
                                        //setMessageWaitTimeout('a', 'p', 'c', 'i', 12, STATE_FORCED_EXIT, SUBSTATE_NORMAL, "Network timeout\nplease login again.", null, null);
                                    }
                                }
                            }
                            break;
                            
                        // FRIEND SUBMENU
                        case SUBSTATE_FRIEND_SUBOPTIONS:
                            s = GTools.menuButtonStatus(menuActionSub, gAction, keyCode);
                            if (keyCode==KEY_SOFTKEY2) {  //BACK to action menu
                                currentSubState = SUBSTATE_ACTIONMENU;
                            } else if (keyCode==KEY_SOFTKEY1 || gAction == FIRE) {  //SELECT
                                //s = GTools.menuGetSelected(menuActionSub);
                                switch (s) {
                                    case 0: //VIEW FRIEND LIST
                                        prepareOpenFriendList();
                                        if (friendList.entries.size() > 0) {
                                            currentSubState = SUBSTATE_FRIEND_LIST;
                                            setBottomCommand1("Options");
                                            setBottomCommand2("Close");

                                            // set the list for the menu
                                            GTools.menuSetItem(menuBigList, friendList, 0);
                                            GTools.menuSetCaptionOneLine(menuBigList, "Friend List", font, 0);
                                        } else {
                                            currentSubState = SUBSTATE_FRIEND_SUBOPTIONS;
                                            //prepareContextMenu(5);
                                            overlayMessage("Friend list is empty.");
                                        }
                                        
                                        break;
                                    case 1: // ADD FRIEND
                                        //get players on screen
                                        if (getPlayersOnScreen(true, 0, true, 0)==0) {
                                            currentSubState = SUBSTATE_FRIEND_SUBOPTIONS;
                                            //prepareContextMenu(5);
                                            overlayMessage("No player in range.");
                                        } else {
                                            currentSubState = SUBSTATE_FRIEND_FIND;
                                        }
                                        lastCheck = curGametime;
                                        break;
                                    case 2: //FRIEND REQUESTS
                                        if (friendRequestList.entries.size() > 0) {
                                            //display queued message
                                            currentSubState = SUBSTATE_FRIEND_REQUEST_LIST;
                                            GTools.menuSetCaptionOneLine(menuList, "Friend Requests", font, 0);
                                            GTools.menuSetItem(menuList, friendRequestList, 0);
                                            //prepareContextMenu(1);
                                            setBottomCommand1("Options");
                                            setBottomCommand2("Close");
                                        } else {
                                            currentSubState = SUBSTATE_FRIEND_SUBOPTIONS;
                                            //prepareContextMenu(5);
                                            overlayMessage("No new friend requests.");
                                        }
                                        break;
                                }
                            }

                            break;
                            
                        case SUBSTATE_FRIEND_REQUEST_LIST:
                            GTools.handleInput(friendRequestList, gAction, keyCode);
                            if (gAction == FIRE) {  // implicit select
                                // selectEvent()
                                selectFriendRequest();
                            } else if (keyCode == KEY_SOFTKEY1) {   // OPTIONS
                                currentSubState = SUBSTATE_FRIEND_REQUEST_LIST_OPTIONS;
                                prepareContextMenu(5);
                                GTools.menuSetSelected(menuContextOptions, 0);
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                            } else if (keyCode == KEY_SOFTKEY2) {   // CLOSE
                                //close list
                                subStateNormal();   //close chat                                

                                // synchronize button info with number of requests
                                /*
                                GTools.buttonListSetButton(menuActionSub, "Incoming Requests (" + friendRequestList.entries.size() + ")", 2, false, true);
                                currentSubState = SUBSTATE_FRIEND_SUBOPTIONS;
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                                GTools.listSetSelectedIndex(friendRequestList, 0);
                                 */
                            }
                            break;
                        
                        case SUBSTATE_FRIEND_LIST_OPTIONS:
                            GTools.handleInput(menuContextOptions, gAction, keyCode);
                            if (keyCode == KEY_SOFTKEY1 || gAction== FIRE) {    // SELECT
                                switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                                    case 0:
                                        Integer tmpInt = (Integer)GTools.listGetData(friendList);
                                       
                                        if (tmpInt != null) {
                                            k1 = GTools.listGetSelectedIndex(friendList);
                                            // check if this friend is currently online
                                            // todo: this needs remodeling, do not deduct anything from the currently set icon, instead use a compound object for the friend records (name, online, location, status, ..)
                                            if (true) {
                                            // -- if (GTools.listGetIconForEntry(friendList, k1) == 1) {
                                                // friend is online as far as the list indicates
                                                // icon index 1 means online, should be faster to check than hashtable
                                                // Talk to friend
                                                actionPartnerID = tmpInt.intValue();
                                                actionPartnerName = (char[])GTools.listGetEntry(friendList);
                                                setBottomCommand1("Options");
                                                setBottomCommand2("Back");
                                                currentSubState = SUBSTATE_TALKTO;
                                                nextChatSubstate = SUBSTATE_TALKTO;
                                            } else {
                                                // friend is offline, cannot talk to him
                                                setBottomCommand1("Options");
                                                setBottomCommand2("Close");
                                                subStateOKDialog("You cannot talk to a friend who is offline.", STATE_GAME, SUBSTATE_FRIEND_LIST);
                                            }
                                        }
                                        
                                        break;
                                    case 1: // Cancel friendship
                                        tmpCharsK = (char[])GTools.listGetEntry(friendList);
                                        if (tmpCharsK!=null) {
                                            currentSubState = SUBSTATE_FRIENDSHIP_CANCEL_CONFIRM;
                                            promptConfirm("Do you really want to cancel the friendship with:\n\n" + new String(tmpCharsK) + "\n\n?");
                                        }
                                        break;
                                }
                            } else if (keyCode == KEY_SOFTKEY2) {   // BACK
                                currentSubState = SUBSTATE_FRIEND_LIST;
                                setBottomCommand1("Options");
                                setBottomCommand2("Close");
                            }

                            break;
                            
                            
                        case SUBSTATE_FRIENDSHIP_CANCEL_CONFIRM:
                            if (keyCode == KEY_SOFTKEY1 || gAction== FIRE) {    // CONFIRM: Yes
                                Integer tmpInt = (Integer)(GTools.listGetData(friendList));
                                if (tmpInt != null) {
                                    // remove
                                    friendsOnline.removeElement(tmpInt);
                                    friendsOffline.removeElement(tmpInt); 
                                    tmpStringK = (String)(friendsNames.remove(tmpInt));
                                    if (tmpStringK!=null) {
                                        showBottomInfo("friendship cancelled: " + tmpStringK, 4000, true);
                                    }
                                    // remove from currently open list
                                    m1 = GTools.listGetSelectedIndex(friendList);
                                    GTools.listRemoveEntry(friendList, m1);
                                    //send to (former) friend
                                    sendFriendShipCancelledMessage(tmpInt.intValue());
                                }
                                kb1 = true;
                            } else if (keyCode == KEY_SOFTKEY2) {   // CONFIRM: No
                                kb1 = true;
                            }
                            
                            if (kb1) {
                                confirmYesNo = false;
                                // back to previous mode
                                currentSubState = SUBSTATE_FRIEND_LIST;
                                setBottomCommand1("Options");
                                setBottomCommand2("Close");
                            }
                            break;
                            
                            
                        case SUBSTATE_FRIEND_LIST:
                            GTools.handleInput(friendList, gAction, keyCode);
                            if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                                // OPTIONS
                                currentSubState = SUBSTATE_FRIEND_LIST_OPTIONS;
                                prepareContextMenu(5);
                                GTools.menuSetSelected(menuContextOptions, 0);
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                            } else if (keyCode == KEY_SOFTKEY2) {   // CLOSE
                                //close list
                                subStateNormal();
                            }
                            break;
                            
                        case SUBSTATE_FRIEND_REQUEST_LIST_OPTIONS:
                            GTools.handleInput(menuContextOptions, gAction, keyCode);
                            if (keyCode == KEY_SOFTKEY1 || gAction== FIRE) {    // SELECT
                                switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                                    case 0: // SHOW FRIEND REQUEST
                                        selectFriendRequest();
                                        break;
                                    case 1: // DELETE FRIEND REQUEST
                                        //delete entry
                                        int sel = GTools.listGetSelectedIndex(friendRequestList);
                                        GTools.listRemoveEntry(friendRequestList, sel);
                                        currentSubState = SUBSTATE_FRIEND_REQUEST_LIST;
                                        setBottomCommand1("Options");
                                        setBottomCommand2("Close");
                                        break;
                                }
                            } else if (keyCode == KEY_SOFTKEY2) {   // BACK
                                currentSubState = SUBSTATE_FRIEND_REQUEST_LIST;
                                setBottomCommand1("Options");
                                setBottomCommand2("Close");
                            }

                            break;

                        case SUBSTATE_FRIEND_REQUEST_ACCEPT_CONFIRM:
                            if (keyCode == KEY_SOFTKEY1 || gAction== FIRE) {    // ACCEPT
                                // check max friends
                                if (friendsOnline.size() + friendsOffline.size() >= MAX_FRIENDS) {
                                    setBottomCommand1("Options");
                                    setBottomCommand2("Close");
                                    subStateOKDialog("You cannot have more than " + MAX_FRIENDS + " at the same time.\nPlease remove an entry from the friend list before adding more friends.", STATE_GAME, SUBSTATE_FRIEND_REQUEST_LIST);
                                } else {
                                    sendAcceptFriendRequest(actionPartnerID);
                                    // add friend to offline friends
                                    friendsOffline.addElement(new Integer(actionPartnerID));
                                    friendsNames.put(new Integer(actionPartnerID), new String(actionPartnerName));
                                    // server will send online notification as a response to the accept message if and only if 
                                    // the friend - who has just been added is currently online (f_fj)
                                    // if server finds that too many friends were added, a fail message is received from server 
                                    // instead (f_faf)
                                    kb1 = true;
                                }
                            } else if (keyCode == KEY_SOFTKEY2) {   // DECLINE
                                sendDeclineFriendRequest(actionPartnerID);
                                kb1 = true;
                            }
                            
                            if (kb1) {
                                // back to previous mode
                                currentSubState = SUBSTATE_FRIEND_REQUEST_LIST;
                                setBottomCommand1("Options");
                                setBottomCommand2("Close");
                                actionPartnerID = -1;
                            }
                            
                            break;
                            
                        // TALK SUBMENU
                        case SUBSTATE_TALK_SUBOPTIONS:
                            
                            s = GTools.menuButtonStatus(menuActionSub, gAction, keyCode);
                            if (keyCode==KEY_SOFTKEY2) {  //BACK to action menu
                                if (talkSubMenu_ShowActionMenu) {
                                    currentSubState = SUBSTATE_ACTIONMENU;
                                } else {
                                    subStateNormal();
                                    talkSubMenu_ShowActionMenu = true;
                                }
                            } else if (keyCode==KEY_SOFTKEY1 || gAction == FIRE) {  //SELECT
                                //s = GTools.menuGetSelected(menuActionSub);
                                switch (s) {
                                    case 0: //TALK TO PLAYER
                                        //get players on screen
                                        Character sel = playfieldView.selectClosestCharacter(true);
                                        if (sel==null) {
                                            if (talkSubMenu_ShowActionMenu) {
                                                currentSubState = SUBSTATE_ACTIONMENU;
                                            } else {
                                                subStateNormal();
                                                talkSubMenu_ShowActionMenu = true;
                                            }
                                            overlayMessage("No one in range.");
                                        } else {
                                            currentSubState = SUBSTATE_TALKTO_FIND;
                                            GTools.textWindowSetText(info1Line, sel.name + " - L." + sel.level);
                                        }

                                        /*
                                        if (getPlayersOnScreen(true, 0, true, -1)==0) {
                                            //subStateOKDialog("No one in range.", currentState, SUBSTATE_NORMAL);
                                            if (talkSubMenu_ShowActionMenu) {
                                                currentSubState = SUBSTATE_ACTIONMENU;
                                            } else {
                                                subStateNormal();
                                                talkSubMenu_ShowActionMenu = true;
                                            }
                                            overlayMessage("No one in range.");
                                        } else {
                                            currentSubState = SUBSTATE_TALKTO_FIND;
                                        }
                                        lastCheck = curGametime;
                                         */
                                        break;
                                    case 1: // TALK TO ALL
                                        openTalkToAll();
                                        currentSubState = SUBSTATE_TALKTOALL;
                                        setBottomCommand1("Options");
                                        setBottomCommand2("Back");
                                        break;
                                    case 2: //List Conversations
                                            GTools.listRemoveAllEntries(genericList);
                                            currentSubState = SUBSTATE_EVENT_LIST;

                                            checkRemoveEmptyConversations();
                                            Vector cSorted = getConversationsSorted();
                                            for (int i=0; i<cSorted.size(); i++) {
                                                Conversation conv = (Conversation)cSorted.elementAt(i);
                                                String entryName = conv.getChannelName();
                                                int curNew = conv.getNumNewMessages();
                                                if (curNew>0) {
                                                   entryName += " (" + conv.getNumNewMessages() + ")";
                                                }
                                                GTools.listAppendEntry(genericList, entryName, conv);
                                            }

                                            /*
                                            Enumeration e = conversations.elements();
                                            while (e.hasMoreElements()) {
                                                Conversation conv = (Conversation)e.nextElement();
                                                String entryName = conv.getChannelName();
                                                int curNew = conv.getNumNewMessages();
                                                if (curNew>0) {
                                                   entryName += " (" + conv.getNumNewMessages() + ")";
                                                }

                                                // csorted



                                                // sort entry in depending of num new messages and channel name
                                                int i=0;
                                                for (; i<genericList.entries.size(); i++) {
                                                    Conversation other = (Conversation)GTools.listGetDataAt(genericList, i);
                                                    if (other.getNumNewMessages() < curNew || (other.getNumNewMessages() == curNew && other.getTimeLastChanged() > conv.getTimeLastChanged())) {
                                                        break;
                                                    }
                                                }
                                                GTools.listInsertEntry(genericList, entryName, conv, i);
                                            }
                                             */

                                            GTools.menuSetCaptionOneLine(menuList, "Conversations", font, 0);
                                            GTools.menuSetItem(menuList, genericList, 0);
                                            //prepareContextMenu(1);
                                            setBottomCommand1("Options");
                                            setBottomCommand2("Close");
                                        break;
                                    case 3:
                                        // edit chat short messages
                                        openChatShortcutList(true);
                                        break;
                                }
                            }
                            break;
                            
                        case SUBSTATE_CHAT_SHORTCUT_EDIT:
                            if (keyCode == KEY_SOFTKEY1 || gAction == FIRE) {
                                // edit selected
                                GTools.textWindowSetText(editBoxInput, (String)GTools.listGetData(bigList));
                                GTools.inputWindowSetCursorToTextEnd(editBoxInput);
                                currentSubState = SUBSTATE_CHAT_SHORTCUT_EDIT_DETAIL;
                                setBottomCommand1("Save");
                                setBottomCommand2("Cancel");
                            } else if (keyCode == KEY_SOFTKEY2) {
                                // back
                                currentSubState = SUBSTATE_TALK_SUBOPTIONS;
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                            } else {
                                GTools.handleInput(bigList, gAction, keyCode);
                            }
                            
                            break;
                            
                        case SUBSTATE_CHAT_SHORTCUT_EDIT_DETAIL:
                            GTools.handleMenuInput(editBox, gAction, keyCode);
                            if (keyCode == KEY_SOFTKEY1) {
                                // save short msg.
                                tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                                if (tmpCharsK!=null) {
                                    k1 = GTools.listGetSelectedIndex(bigList);
                                    if (k1 > -1 && k1 < chatShortcuts.length) {
                                        tmpStringK = new String(tmpCharsK);
                                        chatShortcuts[k1] = tmpStringK;
                                        
                                        GTools.listSetDataAt(bigList, tmpStringK, k1);
                                        
                                        if (k1 < 9) {
                                            tmpStringK = (k1+1) + "   " + tmpStringK;
                                        } else if (k1 == 9) {
                                            tmpStringK = "*   " + tmpStringK;
                                        } else if (k1 == 10) {
                                             tmpStringK = "0   " + tmpStringK;
                                        } else if (k1 == 11) {
                                            tmpStringK = "#   " + tmpStringK;
                                        }
                                        GTools.listSetEntryAt(bigList, tmpStringK.toCharArray(), k1);
                                    }
                                    
                                    tmpStringK = null;
                                    tmpCharsK = null;
                                    
                                    currentSubState = SUBSTATE_CHAT_SHORTCUT_EDIT;
                                    setBottomCommand1("Edit");
                                    setBottomCommand2("Back");
                                }
                                
                            } else if (keyCode == KEY_SOFTKEY2) {
                                // go back to edit list
                                currentSubState = SUBSTATE_CHAT_SHORTCUT_EDIT;
                                setBottomCommand1("Edit");
                                setBottomCommand2("Back");
                            }
                            break;
                            
                        // FIND
                        case SUBSTATE_FRIEND_FIND:
                            handleFindInput(gAction, keyCode, SUBSTATE_FRIEND_FIND, SUBSTATE_FRIEND_FIND_CONFIRM);
                            if (currentSubState == SUBSTATE_FRIEND_FIND_CONFIRM) {
                                if (playersOnScreen[selectedPlayer]!=null && playersOnScreen[selectedPlayer].classId == 0) {
                                    actionPartnerID = playersOnScreen[selectedPlayer].objectId;
                                    actionPartnerName = (playersOnScreen[selectedPlayer].name).toCharArray();
                                    Integer tmpInt = new Integer(actionPartnerID);
                                    if (friendsOffline.contains(tmpInt) ||friendsOnline.contains(tmpInt)) {
                                        subStateOKDialog("The selected player is already in your friend list." , STATE_GAME, SUBSTATE_FRIEND_FIND);
                                    } else {
                                        promptConfirm("Do you want to request friendship with:\n\n" + playersOnScreen[selectedPlayer].name + "\n\n?");
                                    }
                                } else {
                                    currentSubState = SUBSTATE_FRIEND_FIND;
                                }
                            }
                            break;
                            
                        case SUBSTATE_FRIEND_FIND_CONFIRM:
                            if (keyCode == KEY_SOFTKEY1) {
                                // request should be sent
                                confirmYesNo = false;
                                //finishBuyItem(selectedTradeOfferItem);
                                sendFriendRequest(actionPartnerID);
                                if (actionPartnerName != null) {
                                    tmpStringK = new String(actionPartnerName);
                                } else {
                                    tmpStringK = "UNKNOWN";
                                }
                                
                                subStateOKDialog("Your request has been sent to " + tmpStringK + ".\nYou will be notified when the request is accepted or declined.", STATE_GAME, SUBSTATE_NORMAL);
                                actionPartnerID = -1;
                                tmpStringK = null;
                            } else if (keyCode == KEY_SOFTKEY2) {
                                // request should NOT be sent
                                confirmYesNo = false;
                                //get players on screen
                                if (getPlayersOnScreen(true, 0, true, 0)==0) {
                                    // no players on screen so just return to friend options
                                    currentSubState = SUBSTATE_FRIEND_SUBOPTIONS;
                                    //prepareContextMenu(5);
                                } else {
                                    // still players on screen so display find mode
                                    currentSubState = SUBSTATE_FRIEND_FIND;
                                }
                                lastCheck = curGametime;
                            }
                            break;
                            
                            
                        case SUBSTATE_TALKTO_FIND:
                            if (keyCode == KEY_SOFTKEY1 || gAction == FIRE) {
                                Character selChar = playfieldView.getSelectedCharacter();
                                openChat(selChar.objectId, selChar.name);
                            } else if (keyCode == KEY_SOFTKEY2) {
                                subStateNormal();
                            } else {
                                if (!handleFindInput(gAction, keyCode, true)  && playfieldView.getSelectedCharacterId() == 0) {
                                    subStateNormal();
                                }
                            }

                            /*
                            handleFindInput(gAction, keyCode, SUBSTATE_TALKTO_FIND, SUBSTATE_TALKTO);
                            if (currentSubState == SUBSTATE_TALKTO) {
                                if (playersOnScreen[selectedPlayer]!=null && playersOnScreen[selectedPlayer].classId == 1) {
                                    // bot
                                    actionPartnerID = playersOnScreen[selectedPlayer].objectId;
                                    actionPartnerName = (playersOnScreen[selectedPlayer].name).toCharArray();
                                    getDialogue(playersOnScreen[selectedPlayer].objectId, 0);
                                    GTools.textWindowSetText(info1Line, actionPartnerName);
                                    info1Line_DisplayTime = 0;
                                } else {
                                    // player
                                    setBottomCommand1("Options");
                                    setBottomCommand2("Back");
                                    nextChatSubstate = SUBSTATE_TALKTO;
                                }
                            }*/
                            

                            break;
                            
                        //DIALOGUE INIT
                        case SUBSTATE_DIALOGUE_INIT:
                            if ( keyCode == KEY_SOFTKEY2 ) { //CANCEL
                                subStateNormal();
                            }
                            break;
                            
                        //DIALOGUE ACTIVE
                        case SUBSTATE_DIALOGUE_ACTIVE:
                            if ( keyCode == KEY_SOFTKEY2 ) { //QUIT
                                subStateNormal();
                            } else {
                                // $-> handle dialogue activation
                                GTools.handleInput(menuClientphrases, gAction, keyCode);
                                if (keyCode == KEY_SOFTKEY1 || gAction==FIRE) {
                                    // get activated/selected entry
                                    k1 = GTools.menuGetSelected(menuClientphrases);
                                    if (k1 >= 0) {
                                        k2 = botphraseNextIDs[k1];
                                        if (k2 > -1) {    // request next dialogue element triggered by this clientphrase id
                                            getDialogue(actionPartnerID, k2);
                                        } else {
                                            subStateNormal(); // dialogue end: same as quit
                                        }
                                    }
                                }
                                
                            }
                            break;
                            
                            
                        //TALK TO A SPECIFIC player
                        case SUBSTATE_TALKTO:
                            if (inputChatWindow.textFill == 0 && keyCode == KEY_STAR) {
                                openChatShortcutList(false);
                                nextChatSubstate = SUBSTATE_TALKTO;
                            } else if ( keyCode == KEY_SOFTKEY1 ) {    //Open Chat options
                                currentSubState = SUBSTATE_TALKINPUT_OPTIONS;
                                prepareContextMenu(4);
                                nextChatSubstate = SUBSTATE_TALKTO;
                                GTools.menuSetSelected(menuContextOptions, 0);
                                
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                                
                            } else if ( keyCode == KEY_SOFTKEY2 ) { //CLOSE
                                closeChat();
                            } else if (gAction == UP && keyCode != KEY_NUM2) {
                                //System.out.println("keycode: " + keyCode);
                                GTools.textWindowScrollUpFixed(chatWindow, 1);
                            } else if (gAction == DOWN && keyCode != KEY_NUM8) {
                                //System.out.println("keycode d: " + keyCode);
                                GTools.textWindowScrollDownFixed(chatWindow, 1);
                            } else {
                                //normal menu input
                                GTools.handleInput(menuChat, gAction, keyCode);
                            }

                            
                            break;
                            
                        //TALK TO ALL
                        case SUBSTATE_TALKTOALL:
                            if (inputChatWindow.textFill==0 && keyCode == KEY_STAR) {
                                openChatShortcutList(false);
                                nextChatSubstate = SUBSTATE_TALKTOALL;
                            } else if (keyCode == KEY_SOFTKEY1) {    //OPTIONS
                                currentSubState = SUBSTATE_TALKINPUT_OPTIONS;
                                prepareContextMenu(4);
                                nextChatSubstate = SUBSTATE_TALKTOALL;
                                GTools.menuSetSelected(menuContextOptions, 0);
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                            } else if ( keyCode == KEY_SOFTKEY2 ) { //CLOSE
                                closeTalkToAll();
                                subStateNormal();
                            } else {    //normal menu input
                                GTools.handleInput(menuChat, gAction, keyCode);
                            }

                            break;
                            
                        case SUBSTATE_TALKINPUT_OPTIONS:
                            GTools.handleInput(menuContextOptions, gAction, keyCode);
                            if (keyCode == KEY_SOFTKEY1 || gAction== FIRE) {    // SELECT
                                switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                                    case 0: // Send
                                        if (inputChatWindow.textFill>0) {
                                            if (nextChatSubstate == SUBSTATE_TALKTO) {
                                                // GTools.textWindowAddText(chatWindow, playerObject.name + ": ");
                                                String msg = GTools.inputWindowGetTextStr(inputChatWindow);
                                                if (msg!=null && msg.length()>0) {
                                                    // SEND THE CHAT MESSAGE
                                                    appendMessageToConversation(activeConversation.getChannelId(), playerObject.objectId, playerObject.name, msg);
                                                    sendTalkToMessage(msg, activeConversation.getChannelId());
                                                    GTools.inputWindowRemoveText(inputChatWindow);
                                                }                                               
                                            } else {
                                                String inMsg = GTools.inputWindowGetTextStr(inputChatWindow);
                                                if (inMsg!=null && inMsg.length() > 0) {
                                                    //send message to all
                                                    if (netStarted) {
                                                        sendTalkToAll(inMsg);
                                                    }
                                                    playerObject.setPublicChatMessage(inMsg, MESSAGE_MAXSHOWDURATION);
                                                    //playerObject.msgShowDuration = MESSAGE_MAXSHOWDURATION;
                                                    GTools.inputWindowRemoveText(inputChatWindow);
                                                    //readjustPlayerMsg(playerObject);
                                                }
                                            }
                                        }
                                        currentSubState = nextChatSubstate;
                                        setBottomCommand1("Options");
                                        setBottomCommand2("Back");

                                        break;
                                    case 1: // Insert Short Message -> pick from list
                                        openChatShortcutList(false);
                                        break;
                                }
                            } else if ( keyCode == KEY_SOFTKEY2 ) { //CLOSE
                                currentSubState = nextChatSubstate;
                                setBottomCommand1("Options");
                                setBottomCommand2("Back");
                            }
                            
                            break;
                            
                        case SUBSTATE_CHAT_SHORTCUT_SELECT:
                            k1 = keyCode-48;
                            if (k1 > 0 &&  k1 < 10) {
                                // key 1 - 9 insert
                                insertChatShortMessage((String)GTools.listGetDataAt(bigList, k1-1));
                            } else if (k1 == 0) {
                                insertChatShortMessage((String)GTools.listGetDataAt(bigList, 10));
                            } else if (keyCode == KEY_STAR) {
                                insertChatShortMessage((String)GTools.listGetDataAt(bigList, 9));
                            } else if (keyCode == KEY_POUND) {
                                insertChatShortMessage((String)GTools.listGetDataAt(bigList, 11));
                            } else if (keyCode == KEY_SOFTKEY1 || gAction == FIRE) {
                                // insert selected
                                insertChatShortMessage((String)GTools.listGetData(bigList));
                            } else if (keyCode == KEY_SOFTKEY2) {
                                // back
                                currentSubState = nextChatSubstate;
                                setBottomCommand1("Options");
                                setBottomCommand2("Back");
                            } else {
                                GTools.handleInput(bigList, gAction, keyCode);
                            }
                            break;

                        //TRADE .. FIND
                        case SUBSTATE_TRADE_FIND: 
                            handleFindInput(gAction, keyCode, SUBSTATE_TRADE_FIND, SUBSTATE_TRADE_REQUEST);
                            if (currentSubState==SUBSTATE_TRADE_REQUEST) {
                                // a character has been selected
                                // clear the tradeOfferItems
                                for (int k1=tradeOfferItems.length; --k1>=0; ) {
                                    tradeOfferItems[k1] = null;
                                }
                                selectedTradeOfferItem = 0;
                                tradeOfferItemsCount = 0;
                                resetInventoryScrollHugeItemSettings();
                                setBottomCommand1("Buy");
                                setBottomCommand2("Close");
                                
                                sellerID = actionPartnerID;
                                actionPartnerID=0;
                                sendRequestBuyList(sellerID);
                            }
                            break;

                        // TRADE REQUEST
                        case SUBSTATE_TRADE_REQUEST:
                            // todo refactor to use new inventory
                            k1 = selectedTradeOfferItem;
                            if (!inventoryCheckNavigationInput(gAction, null)) {
                                if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                                    // try to buy this item
                                    buyItem(selectedTradeOfferItem);
                                } else if (keyCode==KEY_SOFTKEY2) { //CLOSE TRADE OFFER
                                    selectedTradeOfferItem = 0;
                                    subStateNormal();
                                }
                            } else {
                                /*
                                if (selectedInvItem != k1 && selectedInvItem >= 0 && selectedInvItem < invItems.length) { //another item was selected
                                    
                                }
                                 */
                            }
                            break;
                        
                        case SUBSTATE_GROUND_FIND:
                            if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                                if (triggerTarget_TriggerType != 78 && isPeaceful(playerObject.x + ((playerObject.graphicsDim*DIM)/2), playerObject.y + ((playerObject.graphicsDim*DIM)/2))) {
                                    // no fire wall or mass attackBase when in peaceful area
                                    overlayMessage("Cannot fight in\npeaceful area!");
                                    currentSubState = SUBSTATE_GROUND_FIND;
                                } else if (triggerTarget_TriggerType == 76 && (legacyPlayfield[groundCursorX][groundCursorY] & 16) == 16) {
                                    overlayMessage("Cannot use on\npeaceful area!");
                                    currentSubState = SUBSTATE_GROUND_FIND;
                                } else if (triggerTarget_TriggerType == 76 && (legacyPlayfield[groundCursorX][groundCursorY] & 32) == 32) {
                                    overlayMessage("Cannot use on\nselected area!");
                                    currentSubState = SUBSTATE_GROUND_FIND;
                                } else if (playerObject.curMana + invItems[selectedInvItem].manaBase < 0) {
                                    subStateOKDialog("Not enough mana!", currentState, triggerOrGroundFindReturnState);
                                } else if (playerObject.isRechargingForAttack()) {
                                    // conditions are ok, send ground trigger
                                    sendGroundTriggerMessage(invItems[selectedInvItem].objectId, (byte)groundCursorX, (byte)groundCursorY);
                                    
                                    // subtract curMana immediately, synchronization is also ensured by server when it receives the trigger
                                    playerObject.curMana += invItems[selectedInvItem].manaBase;
                                    if (playerObject.curMana < 0) {playerObject.curMana = 0;}

                                    // show spell attackBase animation at player
                                    playerObject.extraIconShowDuration = MAX_HITSHOWDURATION / 2;
                                    playerObject.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                                    playerObject.extraFlashPhase = true;
                                    
                                    if (triggerTarget_TriggerType != 78) {
                                        playerObject.extraicon = ICON_ATTACK_SPELL1;    // attackBase spell -> red / orange
                                    } else {
                                        playerObject.extraicon = ICON_ATTACK_SPELL2;    // good spell -> blue
                                    }

                                    // adjust units / remove from inventory
                                    if (invItems[selectedInvItem].units != -1) {
                                        if (triggerTarget_TriggerType > 76) {
                                            k1 = 4000;
                                            k2 = 1000;
                                        } else {
                                            k1 = 0;
                                            k2 = 0;
                                        }
                                        
                                        // do not allow next spell cast immediately
                                        /* $-> TODO: rework to use playerObject.isRechargingForAttack() etc.
                                        playerObject.weaponRechargeFullDuration = k1 + 4500 + (-invItems[selectedInvItem].manaBase*10) - magic * 5;
                                        if (playerObject.weaponRechargeFullDuration < k2 + 2500) {playerObject.weaponRechargeFullDuration = k2 + 2500;}
                                        playerObject.weaponRechargeStartTime = playerObject.weaponRechargeFullDuration;
                                        playerObject.weaponRechargeEndTime = System.currentTimeMillis() + playerObject.weaponRechargeFullDuration;
                                        // System.out.println("weapon recharge time: " + weaponRechargeFullDuration);
                                        */
                                        if (removeItemFromInventory(selectedInvItem, 1, false, false)) { // one unit less, will be removed if neccessary, no direct synch at this point, item units are tracked on server, so no abuse possible
                                            // item was removed
                                            if (triggerOrGroundFindReturnState==SUBSTATE_BELT) {
                                                substateBelt(0);
                                            } else {
                                                substateInventory(0);
                                            }
                                        } else {
                                            // item was not removed, so player may use it again
                                            currentSubState = SUBSTATE_GROUND_FIND;
                                        }
                                    }
                                }
                            } else if (keyCode==KEY_SOFTKEY2) { //CLOSE
                                if (triggerOrGroundFindReturnState==SUBSTATE_BELT) {
                                    substateBelt(selectedBeltItem);
                                } else {
                                    substateInventory(selectedInvItem);
                                }
                                
                            } else {
                                // handle cursor key input
                                
                                // get top left cell coordinates
                                k1 = (xPos+8) / TILEWIDTH;
                                k2 = (yPos+8) / TILEWIDTH;

                                // get bottom right cell coordinates
                                k3 = xPos + DISPLAYWIDTH - 9;

//#if Series40_MIDP2_0
//#                             k4 = yPos + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT - 9;
//#else
                                k4 = yPos + DISPLAYHEIGHT - 9;
//#endif
                                
                                k3 = k3 / TILEWIDTH;
                                k4 = k4 / TILEHEIGHT;
                                
                                if (k4 > playfieldWidth-1) {k4 = playfieldWidth-1;}
                                if (k5 > playfieldHeight-1) {k4 = playfieldHeight-1;}
                                
                                switch (gAction) {
                                    case LEFT:
                                        if (groundCursorX > k1) {groundCursorX--;}
                                        break;
                                    case RIGHT:
                                        if (groundCursorX < k3) {groundCursorX++;}
                                        break;
                                    case UP:
                                        if (groundCursorY > k2) {groundCursorY--;}
                                        break;
                                    case DOWN:
                                        if (groundCursorY < k4) {groundCursorY++;}
                                        break;
                                }                            
                            }
                            break;
                            
                        // FIND TRIGGER TARGET
                        case SUBSTATE_TRIGGERTARGET_FIND: 
                            handleFindInput(gAction, keyCode, SUBSTATE_TRIGGERTARGET_FIND, SUBSTATE_TRIGGERTARGET_ACTIVE);
                            if (currentSubState==SUBSTATE_TRIGGERTARGET_ACTIVE) {

                                if (isPeaceful(playerObject.x + ((playerObject.graphicsDim*DIM)/2), playerObject.y + ((playerObject.graphicsDim*DIM)/2))) {
                                    overlayMessage("Cannot fight in\npeaceful area!");
                                    currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                                } else {
                                    characterTmpK = playersOnScreen[selectedPlayer];
                                    if (characterTmpK==null) {
                                        overlayMessage("Target not in range!");
                                        currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                                    } else if ((isPeaceful(characterTmpK.x + ((characterTmpK.graphicsDim*DIM)/2), characterTmpK.y + ((characterTmpK.graphicsDim*DIM)/2)))) {
                                        overlayMessage("Target is in \npeaceful area!");
                                        currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                                    } else if (!isInWeaponRange(characterTmpK, null, 3)) {
                                        overlayMessage("Target not in range!");
                                        currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                                    } else if (triggerTarget_TriggerType == 70 && ( (characterTmpK == playerObject && characterTmpK.curHealth >= maxhealth) || (characterTmpK != playerObject && characterTmpK.curHealth >= characterTmpK.healthBase))) {
                                        overlayMessage("Already full vitality!");
                                        currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                                    } else {
                                        if (invItems[selectedInvItem]==null) {
                                            subStateOKDialog("Item was removed", currentState, triggerOrGroundFindReturnState);
                                        } else if (playerObject.curMana + invItems[selectedInvItem].manaBase < 0) {
                                            subStateOKDialog("Not enough mana!", currentState, triggerOrGroundFindReturnState);
                                        } else if (playerObject.isRechargingForAttack()) {
                                            // a character has been selected and conditions are ok
                                            characterTmpK.hitShowDuration = 1500;
                                            characterTmpK.flashPhaseDuration = 450;
                                            characterTmpK.flashPhase = true;
                                            sendRequestItemTrigger(triggerTarget_TriggerType, triggerTarget_ItemID, actionPartnerID);

                                            // subtract curMana immediately, synchronization is also ensured by server when it receives the trigger
                                            playerObject.curMana += invItems[selectedInvItem].manaBase;
                                            if (playerObject.curMana < 0) {playerObject.curMana = 0;}
                                            
                                            // show spell attackBase animation at player
                                            playerObject.extraIconShowDuration = MAX_HITSHOWDURATION / 2;
                                            playerObject.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                                            playerObject.extraFlashPhase = true;
                                            if (triggerTarget_TriggerType == 70 || triggerTarget_TriggerType == 73) {
                                                playerObject.extraicon = ICON_ATTACK_SPELL2;    // good spell: heal etc. -> blue flame
                                                // -- characterTmpK.icon = ICON_BONUSLOCAL;
                                            } else {
                                                playerObject.extraicon = ICON_ATTACK_SPELL1;    // attackBase spell -> red / orange
                                                characterTmpK.attackShowDuration = MAX_ATTACKSHOWDURATION;
                                                // -- characterTmpK.icon = ICON_HITLOCAL;
                                            }

                                            
                                            // adjust units / remove from inventory
                                            if (invItems[selectedInvItem].units != -1) {
                                                // do not allow next spell cast immediately

                                                /* $-> TODO: rework using .isRechargingForAttack() etc.
                                                playerObject.weaponRechargeFullDuration = 8500 + (-invItems[selectedInvItem].manaBase*10) - magic * 5;
                                                if (playerObject.weaponRechargeFullDuration < 4000) {playerObject.weaponRechargeFullDuration = 4000;}
                                                playerObject.weaponRechargeStartTime = playerObject.weaponRechargeFullDuration;
                                                playerObject.weaponRechargeEndTime = System.currentTimeMillis() + playerObject.weaponRechargeFullDuration;
                                                // System.out.println("weapon recharge time: " + weaponRechargeFullDuration);
                                                */
                                                if (removeItemFromInventory(selectedInvItem, 1, false, false)) { // one unit less, will be removed if neccessary, no direct synch at this point, item units are tracked on server, so no abuse possible
                                                    // item was removed
                                                    actionPartnerID=0;
                                                    //currentSubState = SUBSTATE_INVENTORY;
                                                    if (triggerOrGroundFindReturnState==SUBSTATE_BELT) {
                                                        substateBelt(0);
                                                    } else {
                                                        substateInventory(0);
                                                    }
                                                    //subStateNormal();
                                                } else {
                                                    // item was not removed, so player may use it again
                                                    currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                                                    
                                                    /*
                                                    if (getPlayersOnScreen(true, 0, true)==0) {
                                                        // no one in range anymore
                                                        subStateNormal();
                                                    }
                                                     */
                                                }
                                            }
                                            
                                            
                                        } else {    // stay with target find mode
                                            currentSubState = SUBSTATE_TRIGGERTARGET_FIND;
                                        }
                                    }
                                }
                                
                            }
                            break;
                            
                            
                        // BUILD CHARACTER / CHARACTER STATS
                        case SUBSTATE_BUILDCHARACTER:
                            if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {  // try adding a point to an attribute
                                if (playerObject!=null  && playerObject.levelpoints > 0) {
                                    switch (characterbuildSelection) {
                                        case 0: // healthBase
                                            playerObject.healthBase += atrCHR_Modifiers[0];
                                            maxhealth += atrCHR_Modifiers[0];
                                            replaceNumberLeftAlign(atrHealth, maxhealth, 12, 16, true);
                                            break;
                                        case 1: // manaBase
                                            playerObject.manaBase += atrCHR_Modifiers[1];
                                            maxmana += atrCHR_Modifiers[1];
                                            replaceNumberLeftAlign(atrMana, maxmana, 12, 16, true);
                                            break;
                                        case 2: // damageBase
                                            playerObject.damageBase += atrCHR_Modifiers[2];
                                            damage += atrCHR_Modifiers[2];
                                            replaceNumberLeftAlign(atrDamage, damage, 12, 16, true);
                                            break;
                                        case 3: // attackBase
                                            playerObject.attackBase += atrCHR_Modifiers[3];
                                            attack += atrCHR_Modifiers[3];
                                            replaceNumberLeftAlign(atrAttack, attack, 12, 16, true);
                                            break;
                                        case 4: // defenseBase
                                            playerObject.defenseBase += atrCHR_Modifiers[4];
                                            defense += atrCHR_Modifiers[4];
                                            replaceNumberLeftAlign(atrDefense, defense, 12, 16, true);
                                            break;
                                        case 5: // skillBase
                                            playerObject.skillBase += atrCHR_Modifiers[5];
                                            skill += atrCHR_Modifiers[5];
                                            replaceNumberLeftAlign(atrSkill, skill, 12, 16, true);
                                            break;
                                        case 6: // magicBase
                                            playerObject.magicBase += atrCHR_Modifiers[6];
                                            magic += atrCHR_Modifiers[6];
                                            replaceNumberLeftAlign(atrMagic, magic, 12, 16, true);
                                            break;
                                        default:
                                            return; // invalid selection
                                    }
                                    playerObject.levelpoints--;
                                    if (playerObject.levelpoints == 0) {
                                        this.bCommand1 = false;
                                    }
                                    replaceNumberLeftAlign(atrCHR_Points, playerObject.levelpoints, 18, 20, false);
                                    // take care of changed order, server receives 
                                    // 2 for attackBase, 3 for defenseBase, 4 for skillBase, 5 for magicBase, 6 for damageBase !!
                                    // (the order was only changed on the client display, so characterbuildSelection must be mapped to the server order)
                                    k1 = characterbuildSelection;
                                    if (k1 == 2) {k1=6;}
                                    else if (k1 > 2) {k1-=1;}
                                    sendAttributeIncrease(k1);
                                }
                            } else if (gAction == DOWN) {
                                characterbuildSelection = (characterbuildSelection+1) % 7;
                            } else if (gAction == UP) {
                                characterbuildSelection = characterbuildSelection-1;
                                if (characterbuildSelection < 0) {
                                    characterbuildSelection = 6;
                                }
                            } else if (keyCode == KEY_SOFTKEY2) {   // back to normal mode
                                subStateNormal();
                            }
                            break;
                            
                        case SUBSTATE_EVENT_LIST:
                            GTools.handleInput(genericList, gAction, keyCode);
                            if (gAction == FIRE) {  // implicit select
                                selectEvent();
                            } else if (keyCode == KEY_SOFTKEY1) {   // OPTIONS
                                currentSubState = SUBSTATE_EVENT_LIST_OPTIONS;
                                prepareContextMenu(1);
                                GTools.menuSetSelected(menuContextOptions, 0);
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                            } else if (keyCode == KEY_SOFTKEY2) {   // CLOSE
                                //close list
                                subStateNormal();
                                GTools.listSetSelectedIndex(genericList, 0);
                            }

                            break;
                            
                        case SUBSTATE_EVENT_LIST_OPTIONS:
                            GTools.handleInput(menuContextOptions, gAction, keyCode);
                            if (keyCode == KEY_SOFTKEY1 || gAction== FIRE) {    // SELECT
                                switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                                    case 0: // SHOW EVENT
                                        selectEvent();
                                        break;
                                    case 1: // DELETE EVENT
                                        //delete entry
                                        // takes only conversations into account at the moment
                                        int sel = GTools.listGetSelectedIndex(genericList);
                                        if (sel > -1) {
                                            Conversation selConv = (Conversation)GTools.listGetDataAt(genericList, sel);
                                            conversations.remove(new Integer(selConv.getChannelId()));
                                        }
                                        GTools.listRemoveEntry(genericList, sel);
                                        currentSubState = SUBSTATE_EVENT_LIST;
                                        setBottomCommand1("Options");
                                        setBottomCommand2("Close");
                                        break;
                                }
                            } else if (keyCode == KEY_SOFTKEY2) {   // BACK
                                currentSubState = SUBSTATE_EVENT_LIST;
                                setBottomCommand1("Options");
                                setBottomCommand2("Close");
                            }
                            break;
                            
                            //
                            // QUEST BOOK OVERVIEW
                            //
                        case SUBSTATE_QUEST_OVERVIEW:
                            GTools.handleInput(listQuests, gAction, keyCode);
                            if (gAction == FIRE) {  // select quest book entry -> set details
                                Integer tmpInt = (Integer)GTools.listGetData(listQuests);
                                if (tmpInt!=null) {
                                    requestQuestDetails(tmpInt.intValue());
                                    setWaitLabelText("Looking up\nquestbook entry ..");
                                    currentSubState = SUBSTATE_QUEST_REQUESTDETAILS;
                                    bCommand1 = false;
                                    setBottomCommand2("Cancel");
                                }
                            } else if (keyCode == KEY_SOFTKEY1) {   // OPTIONS
                                currentSubState = SUBSTATE_QUEST_OVERVIEW_OPTIONS;
                                prepareContextMenu(2); // set context menu entries
                                GTools.menuSetSelected(menuContextOptions, 0);
                                setBottomCommand1("Select");
                                setBottomCommand2("Back");
                            } else if (keyCode == KEY_SOFTKEY2) {   // CLOSE
                                //close list
                                subStateNormal();
                                GTools.listSetSelectedIndex(listQuests, 0);
                            }
                            break;

                            //
                            // QUEST BOOK OPTIONS
                            //
                        case SUBSTATE_QUEST_OVERVIEW_OPTIONS:
                            GTools.handleInput(menuContextOptions, gAction, keyCode);
                            if (keyCode == KEY_SOFTKEY1 || gAction== FIRE) {    // SELECT
                                Integer tmpInt = null;
                                switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                                    case 0: // REQUEST QUEST ENTRY DETAILS
                                        tmpInt = (Integer)GTools.listGetData(listQuests);
                                        if (tmpInt!=null) {
                                            requestQuestDetails(tmpInt.intValue());
                                            setWaitLabelText("Looking up\nquestbook entry ..");
                                            currentSubState = SUBSTATE_QUEST_REQUESTDETAILS;
                                            bCommand1 = false;
                                            setBottomCommand2("Cancel");
                                        }
                                        break;
                                    case 1: // DELETE QUEST ENTRY, ASK FOR CONFIRM
                                        /*
                                        GTools.listSetSelectedIndex(listQuests, 0); // select first entry
                                        setBottomCommand1("Options");
                                        setBottomCommand2("Close");
                                         */
                                        //bCommand1 = false;
                                        tmpInt = (Integer)GTools.listGetData(listQuests);
                                        if (tmpInt!=null) {
                                            promptConfirm("Delete Quest?");
                                            currentSubState = SUBSTATE_QUEST_DELETE_CONFIRM;
                                        }
                                        break;
                                }
                            } else if (keyCode == KEY_SOFTKEY2) {   // BACK
                                changeQuestmenuView(false, true);
                            }
                            break;
                            
                        case SUBSTATE_QUEST_DELETE_CONFIRM:
                            if (keyCode == KEY_SOFTKEY1) {
                                Integer tmpInt = null;
                                tmpInt = (Integer)GTools.listGetData(listQuests);
                                if (tmpInt!=null) {
                                    sendLeaveQuest(tmpInt.intValue());
                                }
                                confirmYesNo = false;
                                bCommand1 = false;
                                setBottomCommand2("Cancel");
                                setWaitLabelText("Deleting quest ..");
                                currentSubState = SUBSTATE_QUEST_DELETE_WAIT;
                                //k1 = GTools.listGetSelectedIndex(listQuests);
                                //GTools.listRemoveEntry(listQuests, k1);
                                //changeQuestmenuView(false, true);
                            } else if (keyCode == KEY_SOFTKEY2) {
                                confirmYesNo = false;
                                changeQuestmenuView(false, true);
                            }
                            break;
                            
                        case SUBSTATE_TRADE_BUY_CONFIRM:
                            if (keyCode == KEY_SOFTKEY1) {
                                //bCommand1 = false;
                                //setBottomCommand2("Cancel");
                                //setWaitLabelText("Deleting quest ..");
                                //currentSubState = SUBSTATE_QUEST_DELETE_WAIT;
                                confirmYesNo = false;
                                finishBuyItem(selectedTradeOfferItem);
                            } else if (keyCode == KEY_SOFTKEY2) {
                                confirmYesNo = false;
                            }
                            currentSubState = SUBSTATE_TRADE_REQUEST;
                            break;
                            
                        case SUBSTATE_QUEST_DELETE_WAIT:
                            if (keyCode == KEY_SOFTKEY2) {   // CANCEL
                                //close list
                                subStateNormal();
                                GTools.listSetSelectedIndex(listQuests, 0);
                            }
                            break;
                            
                        case SUBSTATE_QUEST_REQUESTDETAILS:
                            if (keyCode == KEY_SOFTKEY2) {   // CANCEL
                                //close list
                                subStateNormal();
                                GTools.listSetSelectedIndex(listQuests, 0);
                            }
                            break;
                            
                        case SUBSTATE_QUEST_DETAILS:
                            if (keyCode == KEY_SOFTKEY2) {   // CANCEL
                                // back to overview
                                changeQuestmenuView(false, true);
                            }
                            break;
                            
                            

                            
                        }   //switch(currentSubState)
                        break;

                } //if playfiledCounter==0
                
                break; //END STATE_GAME
            
            
            case STATE_DEFINE_KEYS:
                switch(currentSubState) {
                    case SUBSTATE_DEFINE_KEY_SK1:
                        KEY_SOFTKEY1 = keyCode;
                        database.setValue("skey1", "" + KEY_SOFTKEY1);
                        GWindow.activateKey2 = KEY_SOFTKEY1;
                        currentSubState = SUBSTATE_DEFINE_KEY_SK2;
                        setWaitLabelText("Press RIGHT soft key.");
                        break;

                    case SUBSTATE_DEFINE_KEY_SK2:
                        KEY_SOFTKEY2 = keyCode;
                        database.setValue("skey2", "" + KEY_SOFTKEY2);
                        currentState = STATE_INTRO;
                        currentSubState = SUBSTATE_NORMAL;
                        setBottomCommand1("Connect");
                        setBottomCommand2("Change Keys");
                        break;
                }
                break;
                
            case STATE_INTRO:

//#if MIDP_2_0_GENERIC_KEYS
//#         if(keyCode == KEY_SOFTKEY2) {
//#             currentState = STATE_DEFINE_KEYS;
//#             currentSubState = SUBSTATE_DEFINE_KEY_SK1;
//#             setWaitLabelText("Press LEFT soft key.");
//#             setBottomCommand1(null);
//#             setBottomCommand2(null);
//#         } else if(keyCode == KEY_SOFTKEY1 || gAction==FIRE) {
//#else
        if(keyCode == KEY_SOFTKEY1 || keyCode == KEY_SOFTKEY2 || gAction==FIRE) {
//#endif
            String eulaVal  = null;
            if (!RSForcedOff) {
                eulaVal  = database.getValue("eula-accept");
            }

            if (eulaVal == null || !eulaVal.equals("true")) {
                currentState = STATE_INTRO_EULA;
                setBottomCommand1("Accept");
                setBottomCommand2("Exit");
            } else {
                initAfterIntro();
            }
                    
        }
        /*
        else if (keyCode > 48 && keyCode < 52) {
            playbackSound(keyCode-48-1, -1);
        }*/
        break;

        case STATE_INTRO_EULA:
            if(keyCode == KEY_SOFTKEY1 || gAction==FIRE) {
                database.setValue("eula-accept", "true");
                initAfterIntro();
            } else if (keyCode == KEY_SOFTKEY2) {
                currentState = STATE_FORCED_EXIT;
                currentSubState = SUBSTATE_NORMAL;
            } else {
                switch(gAction) {
                    case UP:
                        GTools.textWindowScrollUpFixed(eulaWindow,1);
                        break;
                    case DOWN:
                        GTools.textWindowScrollDownFixed(eulaWindow,1);
                        break;
                }

            }

           break;


            case STATE_CONNECT_GET_SERVERS:
                GTools.handleMenuInput(menuList, gAction, keyCode);
                    if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                        if (genericList.entries.size() > 0) {
                            tmpStringK = (String)GTools.listGetData(genericList);
                            if (tmpStringK!=null) {
                                if (tmpStringK.equals(host)) {
                                    // already connected to this server
                                    bServerList = false;
                                    currentState = STATE_CONNECT;
                                } else {
                                    // new Server was selected
                                    stopNet();
                                    host = tmpStringK;
                                    currentState = STATE_PRE_CONNECT;
                                    bServerList = false;
                                    setWaitLabelText("Connecting to game\nserver..\n\nPlease be patient.");
                                }
                            }
                        }
                    }
                break;
                
            case STATE_INTRO_LIST:
                GTools.handleMenuInput(menuList, gAction, keyCode);
                if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                    s = GTools.listGetSelectedIndex(genericList);
                    if (s==0) { //GO TO LOGIN SCREEN
                        if (!RSForcedOff) {
                            clientName = database.getValue("clientname");
                            clientPass = database.getValue("clientpass");
                        } else {
                            clientName = null;
                            clientPass = null;

                        }

                        if ( release == false 
                                /*&& (clientName==null || clientName.equals("")) && (clientPass==null || clientPass.equals(""))*/
                                 ) {
                            clientName = "acaca";
                            clientPass = "aaaaa";
                        }

                        if (clientName!=null) {
                            GTools.textWindowSetText(usernameWindow, clientName);
                        } else {
                            GTools.textWindowRemoveText(usernameWindow);
                        }
                        if (clientPass!=null) {
                            GTools.textWindowSetText(passwordWindow, clientPass);
                        } else {
                            GTools.textWindowRemoveText(passwordWindow);
                        }
                        GTools.menuSetCaptionOneLine(menuLogin, "LOGIN", font, 0);
                        GTools.labelSetText((GTextWindow)(menuLogin.items[0]), "User name", false);
                        GTools.labelSetText((GTextWindow)(menuLogin.items[2]), "Password", false);
                        GTools.menuSetSelected(menuLogin, 1);
                        setBottomCommand1("Login");
                        setBottomCommand2("Back");
                        passwordWindow.password = true;
                        currentState = STATE_LOGIN_MENU;
                    } else if (s==1) {    //GO TO REGISTER SCREEN
                        GTools.textWindowRemoveText(usernameWindow);
                        GTools.textWindowRemoveText(passwordWindow);
                        GTools.menuSetCaptionOneLine(menuLogin, "REGISTER", font, 0);
                        GTools.labelSetText((GTextWindow)(menuLogin.items[0]), "User name", false);
                        GTools.labelSetText((GTextWindow)(menuLogin.items[2]), "Password", false);
                        GTools.menuSetSelected(menuLogin, 1);
                        setBottomCommand1("Register");
                        setBottomCommand2("Back");
                        passwordWindow.password = false;
                        currentState = STATE_REGISTER_NEW;
                        setWaitLabelText("Remember the\npassword well!\nIt may only be\nrecovered by\ne-mail!");
                        labelWait.centerTextH = false;
                        labelWait.x = passwordWindow.x;
                        labelWait.y = passwordWindow.y + passwordWindow.height;
                    } else if (s==2) {    // RECOVER PASSWORD                        
                        setListEntriesRecoverPassword();
                        currentState = STATE_RECOVER_PASSWORD_MAIN_OPTIONS;
                        currentSubState = SUBSTATE_NORMAL;
                        setBottomCommand1("Select");
                        setBottomCommand2("Back");
                    }
                }
                break;

            case STATE_RECOVER_PASSWORD_MAIN_OPTIONS:
                GTools.handleMenuInput(menuList, gAction, keyCode);
                if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
                    s = GTools.listGetSelectedIndex(genericList);
                    if (s==0) { //request reset code
                        currentState = STATE_GET_PASSWORD_RESET_CODE;
                        currentSubState = SUBSTATE_NORMAL;
                        setBottomCommand1("Get Reset-Code");
                        setBottomCommand2("Cancel");
                        tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                        if (tmpCharsK == null ||tmpCharsK.length == 0) {
                            if (!RSForcedOff) {
                                tmpStringK = database.getValue("clientname");
                                if (tmpStringK != null) {
                                    GTools.textWindowSetText(editBoxInput, tmpStringK);
                                }
                            }
                        }

                        GTools.menuSetCaptionOneLine(editBox, "Enter user name", font, 0);
                        overlayMessage("You will be prompted to enter your user name to receive a reset-code via e-mail.\n\nThis only works if you provided a valid e-mail for your account.");
                    } else  if (s==1) {
                        //enter name for reset code
                        currentState = STATE_ENTER_NAME_FOR_RESET_CODE;
                        currentSubState = SUBSTATE_NORMAL;
                        setBottomCommand1("Next");
                        setBottomCommand2("Back");
                        tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                        if (tmpCharsK == null ||tmpCharsK.length == 0) {
                            if (!RSForcedOff) {
                                tmpStringK = database.getValue("clientname");
                                if (tmpStringK != null) {
                                    GTools.textWindowSetText(editBoxInput, tmpStringK);
                                }
                            }

                        }
                        //GTools.inputWindowRemoveText(editBoxInput);
                        GTools.menuSetCaptionOneLine(editBox, "Enter user name", font, 0);
                        overlayMessage("You will be prompted to enter your user name. Afterwards you can use the reset-code to define a new password.\n\nIf you have no reset-code, go back and select 'Get Reset-Code'.");
                    }
                } else if (keyCode == KEY_SOFTKEY2) {
                    // back to intro list
                    setListEntriesIntro();
                    currentSubState = SUBSTATE_NORMAL;
                    currentState = STATE_INTRO_LIST;
                    setBottomCommand1("Select");
                    setBottomCommand2("Game");
                }
            break;

            case STATE_REGISTER_NEW:
                if (currentSubState!=SUBSTATE_NORMAL)
                    break;
                
                GTools.handleMenuInput(menuLogin, gAction, keyCode);
                if (keyCode == KEY_SOFTKEY1) {   // REGISTER Button
                    //check input and login if ok
                    if  (loginInputOK()) {
                        // clientName and clientPass are set by loginInputOK
                        sendRegisterPlayerMessage(clientName, clientPass);
                        //loginSystem(clientName, clientPass);
                        currentState = STATE_REGISTER_NEW_WAIT;
                        setWaitLabelText("registering..");
                        labelWait.centerTextH = true;
                        bCommand1 = false;
                        setBottomCommand2("Game");
                    }
                } else if(keyCode == KEY_SOFTKEY2) {
                    setBottomCommand1("Select");
                    setBottomCommand2("Game");
                    currentState = STATE_INTRO_LIST;
                }

                break;

            case STATE_EMAIL_ENTRY:
                handleEmailEntry(false, keyCode);
                break;
            
            case STATE_GET_PASSWORD_RESET_CODE:
                switch (currentSubState) {
                    case SUBSTATE_NORMAL:
                        GTools.handleMenuInput(editBox, gAction, keyCode);
                        if (keyCode == KEY_SOFTKEY1) {
                            // get username
                            tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                            if (tmpCharsK==null || tmpCharsK.length < 4) {
                                subStateOKDialog("User name too short\nMin. 4 characters", -1, -1);
                                //bK1 = false;
                            } else if (containsInvalidChars(tmpCharsK, false)) {
                                subStateOKDialog("User name contains invalid characters", -1, -1);
                                //bK1 = false;
                            } else {
                                // send username to server
                                currentSubState = SUBSTATE_RECOVER_PASSWORD_WAIT;
                                setWaitLabelText("Requesting reset-code ..");
                                sendRecoverPassword(tmpCharsK);
                                setBottomCommand1(null);
                                setBottomCommand2(null);
                            }
                        } else if (keyCode == KEY_SOFTKEY2) {
                            // hit cancel, go back to options
                            currentState = STATE_RECOVER_PASSWORD_MAIN_OPTIONS;
                            currentSubState = SUBSTATE_NORMAL;
                            setBottomCommand1("Select");
                            setBottomCommand2("Back");
                        }
                        break;
                    case SUBSTATE_RECOVER_PASSWORD_WAIT:
                        /*
                        if (keyCode == KEY_SOFTKEY2) {
                            // hit cancel, go back to intro list
                            currentSubState = SUBSTATE_NORMAL;
                            currentState = STATE_INTRO_LIST;
                            setBottomCommand1("Select");
                            setBottomCommand2("Game");
                        }*/
                        break;
                }
                break;

            case STATE_ENTER_NAME_FOR_RESET_CODE:
                switch (currentSubState) {
                    case SUBSTATE_NORMAL:
                        GTools.handleMenuInput(editBox, gAction, keyCode);
                        if (keyCode == KEY_SOFTKEY1) {
                            // get username
                            tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                            if (tmpCharsK==null || tmpCharsK.length < 4) {
                                subStateOKDialog("User name too short\nMin. 4 characters", -1, -1);
                                //bK1 = false;
                            } else if (containsInvalidChars(tmpCharsK, false)) {
                                subStateOKDialog("User name contains invalid characters", -1, -1);
                                //bK1 = false;
                            } else {
                                // store username for next step
                                usernameForResetCode = new String(tmpCharsK);
                                currentState = STATE_ENTER_PASSWORD_RESET_CODE;
                                currentSubState = SUBSTATE_NORMAL;

                                overlayMessage("You may now enter the reset-code and your *new* password.\n\nIf you do not have a reset-code, go two steps back and select 'Get Reset-Code'.");

                                GTools.menuSetCaptionOneLine(menuLogin, "CHANGE PASSWORD", font, 0);
                                GTools.menuSetSelected(menuLogin, 1);
                                setBottomCommand1("Change Password");
                                setBottomCommand2("Back");
                                GTools.labelSetText((GTextWindow)(menuLogin.items[0]), "Reset-Code", false);
                                GTools.labelSetText((GTextWindow)(menuLogin.items[2]), "New Password", false);
                                passwordWindow.password = false;

                            }
                        } else if (keyCode == KEY_SOFTKEY2) {
                            // hit cancel, go back to options
                            currentState = STATE_RECOVER_PASSWORD_MAIN_OPTIONS;
                            currentSubState = SUBSTATE_NORMAL;
                            setBottomCommand1("Select");
                            setBottomCommand2("Back");
                        }
                        break;
                    case SUBSTATE_RECOVER_PASSWORD_WAIT:
                        /*
                        if (keyCode == KEY_SOFTKEY2) {
                            // hit cancel, go back to intro list
                            currentSubState = SUBSTATE_NORMAL;
                            currentState = STATE_INTRO_LIST;
                            setBottomCommand1("Select");
                            setBottomCommand2("Game");
                        }*/
                        break;
                }
                break;


            case STATE_ENTER_PASSWORD_RESET_CODE:
                if (currentSubState!=SUBSTATE_NORMAL)
                    break;

                GTools.handleMenuInput(menuLogin, gAction, keyCode);

                if (keyCode == KEY_SOFTKEY1) {
                    //check reset-code and new password
                    tmpCharsK = GTools.inputWindowGetText(usernameWindow);
                    tmpCharsK1 =  GTools.inputWindowGetText(passwordWindow);

                    boolean bK1 = true;

                    if (tmpCharsK==null || tmpCharsK.length == 0) {
                        subStateOKDialog("Reset-Code must not be empty", -1, -1);
                        bK1 = false;
                    } else if (tmpCharsK1==null || tmpCharsK1.length < 4) {
                        subStateOKDialog("New password is too short\nMinimum 4 characters", -1, -1);
                        bK1 = false;
                    } else if (containsInvalidChars(tmpCharsK1, false)) {
                        subStateOKDialog("New password contains invalid characters", -1, -1);
                        bK1 = false;
                    }

                    if (bK1) {
                        setWaitLabelText("Requesting change of password ..");
                        currentSubState = SUBSTATE_RECOVER_PASSWORD_WAIT;
                        bCommand1 = false;
                        setBottomCommand2("Game");
                        sendRequestPasswordChange(usernameForResetCode, tmpCharsK, tmpCharsK1);
                    }

                } else if (keyCode == KEY_SOFTKEY2) {
                    setBottomCommand1("Next");
                    setBottomCommand2("Back");
                    currentState = STATE_ENTER_NAME_FOR_RESET_CODE;
                }
                break;


            case STATE_LOGIN_MENU:
                if (currentSubState!=SUBSTATE_NORMAL)
                    break;
                
                GTools.handleMenuInput(menuLogin, gAction, keyCode);
                
                if (keyCode == KEY_SOFTKEY1) {   // LOGIN Button
                    //check login input and login 
                    if (loginInputOK()) {
                        // -- loginSystem(clientName, clientPass);
                        loginFW(clientName, clientPass);
                        setWaitLabelText("Logging in..");
                        currentState = STATE_WAIT;
                        bCommand1 = false;
                        setBottomCommand2("Game");
                    } else if (offline) {
                        //prepareGameOffline();
                    }
                } else if (keyCode == KEY_SOFTKEY2) {
                    setBottomCommand1("Select");
                    setBottomCommand2("Game");
                    currentState = STATE_INTRO_LIST;
                }
                break;
                
            case STATE_CHARACTER_SELECT:
                switch (currentSubState) {
                    case SUBSTATE_NORMAL:
                        GTools.handleMenuInput(menuList, gAction, keyCode);
                        if (gAction == FIRE) {  // implicit SELECT
                            selectCharacter();
                        } else if (keyCode == KEY_SOFTKEY1) {  // OPTIONS BUTTON
                            currentSubState = SUBSTATE_CHARACTER_OPTIONS;
                            prepareContextMenu(0);
                            GTools.menuSetSelected(menuContextOptions, 0);
                            setBottomCommand1("Select");
                            setBottomCommand2("Back");
                        }
                        break;

                    case SUBSTATE_CHARACTER_OPTIONS:
                        if (keyCode == KEY_SOFTKEY2) { //BACK to CHARACTER SELECT mode
                            currentSubState = SUBSTATE_NORMAL;
                            setBottomCommand1("Options");
                            setBottomCommand2("Game");
                        } else {
                            switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                                case 0: // SELECT
                                    selectCharacter();
                                    break;
                                case 1: // CREATE NEW
                                    //check if max player limit reached
                                    this.setWaitLabelText("Checking permission..");
                                    currentState = STATE_WAIT;
                                    bCommand1 = false;
                                    this.sendRequestCreateCharacterPermission();
                                    break;
                                /*case 2: //
                                    //addCharacterOK = false;
                                    currentSubState = SUBSTATE_CHARACTER_RENAME;
                                    setBottomCommand1("Rename");
                                    setBottomCommand2("Cancel");
                                    GTools.textWindowSetText(this.editBoxInput, GTools.listGetEntry(genericList));
                                    GTools.menuSetCaptionOneLine(editBox, "Character name", font, 0);
                                    break;                                    */
                                case 2: // DELETE
                                    promptConfirm("Really Delete?");
                                    currentSubState = SUBSTATE_CHARACTER_DELETE_CONFIRM;
                                    initWindows();
                                    break;
                            }
                        }
                        break;
                        
                    case SUBSTATE_CHARACTER_NEW:
                        //SELECT CHARACTER CLASS
                        GTools.handleMenuInput(menuList, gAction, keyCode);
                        if (keyCode == KEY_SOFTKEY1 || gAction == FIRE) {  // SELECT
                            // check if this is a premium only class
                            // $-> todo: must set premium status on client for this to work
                            // this is double-checked on the server to be safe
                            CharacterClass cc = (CharacterClass)GTools.listGetData(genericList);
                            if (cc != null) {
                                if (cc.premiumOnly && !isPremium) {
                                     subStateOKDialog("This character class is only available for premium accounts. See www.rhynn.com", STATE_CHARACTER_SELECT, SUBSTATE_CHARACTER_NEW);
                                } else {
                                    //select this class -> next: define name
                                    currentSubState = SUBSTATE_CHARACTER_NEW_NAME;
                                    GTools.inputWindowRemoveText(editBoxInput);
                                    GTools.menuSetCaptionOneLine(editBox, "Character name", font, 0);
                                }
                            }
                        } else if (keyCode == KEY_SOFTKEY2) {   // BACK
                            currentSubState = SUBSTATE_NORMAL;
                            setBottomCommand1("Options");
                            setBottomCommand2("Game");
                            //initWindows();
                            //addCharacterOK = true;
                            initCharacterSelectionList();
                            //sendRequestCharactersMessage();
                        }
                        break;

                    case SUBSTATE_CHARACTER_NEW_NAME:
                        GTools.handleMenuInput(editBox, gAction, keyCode);
                        if (keyCode==KEY_SOFTKEY1) {   //SELECT - NAME EDITIMG DONE -> CREATE WITH NEW NAME
                            tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                            if (tmpCharsK!=null && tmpCharsK.length >0) {
                                currentSubState = SUBSTATE_NORMAL;
                                // get selected class
                                CharacterClass cc = (CharacterClass)GTools.listGetData(genericList);
                                // send new character request
                                sendCreateNewCharacter(tmpCharsK, cc.classId);
                                //go back to character selection screen
                                setBottomCommand1("Options");
                                setBottomCommand2("Game");
                                //initWindows();
                                //addCharacterOK = true;
                                initCharacterSelectionList();
                                //sendRequestCharactersMessage();
                                
                            } else {    //empty name
                                subStateOKDialog("Character name empty", currentState, currentSubState);
                            }
                            tmpCharsK = null;
                        } else if (keyCode==KEY_SOFTKEY2) { //CANCEL back to character selection screen
                            //break whole new character process
                            currentSubState = SUBSTATE_CHARACTER_NEW;
                            //setBottomCommand1("Select");
                            //setBottomCommand2("Cancel");
                            //initWindows();
                            //addCharacterOK = true;
                            //initCharacterSelectionList();
                            //sendRequestCharactersMessage();
                        }
                        break;

                        
                    case SUBSTATE_CHARACTER_RENAME:
                        GTools.handleMenuInput(editBox, gAction, keyCode);
                        if (keyCode == KEY_SOFTKEY1) {   //NAME EDITIMG DONE -> RENAME
                            tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                            if (tmpCharsK!=null && tmpCharsK.length >0) {
                                //get the object id of the character
                                characterTmpK = (Character)GTools.listGetData(genericList);
                                if (characterTmpK!=null) {
                                    k1 = characterTmpK.objectId;
                                    //send new name
                                    sendRenameCharacter(k1, tmpCharsK);
                                    //characterTmpK.name = new String(tmpCharsK);
                                    //wait for server acknowledgement
                                    currentSubState = SUBSTATE_CHARACTER_RENAME_WAIT;
                                    bCommand1 = false;
                                    setBottomCommand2("Game");

                                    setWaitLabelText("Renaming character ..");

                                    //initWindows();
                                    //addCharacterOK = true;
                                    genericList.activeBackColor = 0xCC6600;
                                    //rename locally
                                    //GTools.listSetEntryAt(genericList, tmpCharsK, GTools.listGetSelectedIndex(genericList));
                                    //sendRequestCharactersMessage();
                                    characterTmpK = null;
                                }else {    //empty name
                                    subStateOKDialog("Unknown error", currentState, SUBSTATE_CHARACTER_OPTIONS);
                                    setBottomCommand1("Select");
                                    GTools.menuSetSelected(menuContextOptions, 0);
                                }
                                
                            } else {    //empty name
                                subStateOKDialog("Character name empty", currentState, currentSubState);
                            }
                            tmpCharsK = null;
                        } else if (keyCode == KEY_SOFTKEY2) { //CANCEL -> back to character selection screen 
                            //break whole new character process
                            currentSubState = SUBSTATE_NORMAL;
                            setBottomCommand1("Options");
                            setBottomCommand2("Game");
//--initWindows();
                            //addCharacterOK = true;
                            //genericList.activeBackColor = 0xCC6600;
//--sendRequestCharactersMessage();
                        }
                        break;
                        
                    //CONFIRM CHARACTER DELETION
                    case SUBSTATE_CHARACTER_DELETE_CONFIRM:
                            if (keyCode==KEY_SOFTKEY1) {    //DELETE CHARACTER
                                if (genericList.entries.size() > 0) {
                                    characterTmpK = (Character)GTools.listGetData(genericList);
                                    if (characterTmpK!=null) {
                                        s = characterTmpK.objectId;
                                        //addCharacterOK = false;
                                        this.setWaitLabelText("Deleting character..");
                                        currentSubState = SUBSTATE_CHARACTER_DELETE_WAIT;
                                        bCommand1 = false;
                                        //REQUEST DELETION
                                        sendDeleteCharacter(s);
                                        characterTmpK = null;
                                    } else {
                                        currentSubState = SUBSTATE_NORMAL;  //no character selected
                                    }
                                } else {
                                    currentSubState = SUBSTATE_NORMAL;  //no character selected
                                }
                                confirmYesNo = false;
                                bCommand1 = true;
                                setBottomCommand1("Options");
                                setBottomCommand2("Game");
                            } else if (keyCode==KEY_SOFTKEY2) {  //NO: BACK TO CHARACTER SELECTION
                                currentSubState = SUBSTATE_NORMAL;
                                confirmYesNo = false;
                                bCommand1 = true;
                                setBottomCommand1("Options");
                                setBottomCommand2("Game");
                            }

                        break;
                }
                break;  // STATE_CHARACTER_SELECT
                
                case STATE_SUBSCRIBE_NEW:
                    GTools.handleMenuInput(editBox, gAction, keyCode);
                    if (keyCode==KEY_SOFTKEY1) {   //SEND THE PHONE NUMBER
                        //show options for dialogue
                        currentState = STATE_SUBSCRIBE_OPTIONS;
                        setBottomCommand1("Select");
                        setBottomCommand2("Back");
                    }
                    
                    break;

            case STATE_SUBSCRIBE_OPTIONS:
                if (keyCode == KEY_SOFTKEY2) { // BACK TO SUBSCRIBE DIALOGUE
                    currentState = STATE_SUBSCRIBE_NEW;
                    setBottomCommand1("Options");
                    bCommand2 = false;
                    // setBottomCommand2("Exit");
                } else {
                    switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                        case 0: // SEND (phone number for subscription request)
                            tmpCharsK = GTools.inputWindowGetText(editBoxInput);
                            if (tmpCharsK!=null && tmpCharsK.length > 7) {
                                bCommand1 = false;
                                setBottomCommand2("Exit");
                                // send phone number for subscription to server
                                sendRequestSubscription(tmpCharsK, clientName, clientPass);
                                // display wait label, wait for server response
                                currentState = STATE_SUBSCRIBE_WAIT_FOR_RESPONSE;
                                setWaitLabelText("Waiting for response ..");
                                
                            } else {    //invalid number was entered
                                setBottomCommand1("Options");
                                bCommand2 = false;
                                currentState = STATE_SUBSCRIBE_NEW;
                                subStateOKDialog("Phone number invalid", STATE_SUBSCRIBE_NEW, SUBSTATE_NORMAL);
                            }
                            tmpCharsK = null;
                            break;
                            
                        case 1: // HELP ..
                            bCommand2 = false;
                            currentState = STATE_SUBSCRIBE_NEW;
                            setBottomCommand1("Options");
                            overlayState = OVERLAY_HELP_WAIT;
                            GTools.labelSetText(confirmWindow, "Requesting help ..", false);
                            GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                            
                            sendRequestHelpModuleFirstText("billing_1");
                            
                            //subStateOKDialog("Your phone number is required for the billing process only and will not be used for any other purposes.\n\nAfter providing your valid phone number you will be sent an SMS. The one-time charging and account activation will not happen until you confirm the link provided within the SMS.", STATE_SUBSCRIBE_NEW, SUBSTATE_NORMAL);
                            nextHelpID = 1;
                            //sendRequestNextHelpText(nextHelpID);
                            //System.out.println("send request credits: " + creditid);
                            ovCommand1 = false;
                            setOverlayCommand2("Close");
                            break;
                        case 2: //EXIT
                            //bCommand2 = false;
                            currentState = STATE_SUBSCRIBE_EXIT_CONFIRM;
                            //currentSubState = SUBSTATE_NORMAL;
                            promptConfirm("Really Exit?");
                            //bExitConfirm = true;
                            //nextState = STATE_SUBSCRIBE_NEW;
                            //nextSubState = SUBSTATE_NORMAL;
                            break;                                    
                    }                    
                }
                break;
                
            case STATE_SUBSCRIBE_EXIT_CONFIRM:
                if (keyCode==KEY_SOFTKEY1) {
                    currentState = STATE_SUBSCRIBE_EXIT_WAIT_FOR_MSG;
                    bCommand1 = false;
                    bCommand2 = false;
                    confirmYesNo = false;
                    setWaitLabelText("Exiting ..");
                    sendSubscriptionExit();
                    // wait for client messsage to receive info on alternate billing
                    // -- setMessageWaitTimeout('f', 'c', 'm', (char)0, 7,  STATE_FORCED_EXIT, SUBSTATE_NORMAL, "See\n\nwww.awaredreams.com\n\nfor alternative ways to renew your account.", "", "Exit");
                } else if (keyCode==KEY_SOFTKEY2) {
                    bCommand2 = false;
                    confirmYesNo = false;
                    currentState = STATE_SUBSCRIBE_NEW;
                }
                break;
                
            case STATE_SUBSCRIBE_WAIT_FOR_RESPONSE:
                if (keyCode == KEY_SOFTKEY2) {
                    promptConfirm("Really Exit?");
                    bExitConfirm = true;
                    nextState = STATE_SUBSCRIBE_WAIT_FOR_RESPONSE;
                    nextSubState = SUBSTATE_NORMAL;
                }
                break;
                
            case STATE_SUBSCRIBE_DONE_EXIT:
                if (keyCode == KEY_SOFTKEY2) {
                    promptConfirm("Really Exit?");
                    bExitConfirm = true;
                    nextState = STATE_SUBSCRIBE_DONE_EXIT;
                    nextSubState = SUBSTATE_NORMAL;
                }
                break;
                    
            
        }  //switch(currentState)
        
        if(showDebug6) {
            this.replaceNumber(labelDebug6.text, System.currentTimeMillis()-start, 0, 4);
        }            
    }

    
    private void removeFriendOnFriendShipCancel(Integer friendCharacterID) {
        // immediately remove from the lists
        friendsOnline.removeElement(friendCharacterID);
        friendsOffline.removeElement(friendCharacterID); 

        // get the name of the friend who cancelled the friendship                    
        String friendName = (String)(friendsNames.remove(friendCharacterID));
        if (friendName != null) {
            showBottomInfo("friendship cancelled: " + friendName, 9000, true);
        }
        
        // in case the friendlist is currently open, make sure the entry in question is removed
        if (currentSubState == SUBSTATE_FRIEND_LIST || currentSubState == SUBSTATE_FRIEND_LIST_OPTIONS) {
            int size = friendList.entries.size();
            Integer I = null;
            for (int i=0; i<size; i++) {
                I = (Integer)GTools.listGetDataAt(friendList, i);
                if (I!=null && I.equals(friendCharacterID)) {
                    GTools.listRemoveEntry(friendList, i);
                    break;
                }
            }
        
        }
    }
    
    
    private void handleEmailEntry(boolean isIngameMode, int keyCode) {
        //int stateEntry = OPTIONSTATE_EMAIL_ENTRY;
        int subStateShowOptions = OPTIONSUBSTATE_EMAIL_OPTIONS;
        int subStateChangeWait = OPTIONSUBSTATE_EMAIL_CHANGE_WAIT;
        int subStateEMailGet = OPTIONSUBSTATE_EMAIL_GET;
        int subStateNormal = OPTIONSUBSTATE_NONE;
        //int mainState = optionState;
        int mainSubState = optionSubState;
        int commandType = CM_OPTION;
        String cancelCommand = "Cancel";

        if (!isIngameMode) {
            //stateEntry = STATE_EMAIL_ENTRY;
            subStateShowOptions = SUBSTATE_EMAIL_OPTIONS;
            subStateChangeWait = SUBSTATE_EMAIL_CHANGE_WAIT;
            subStateNormal = SUBSTATE_NORMAL;
            subStateEMailGet = -1;
            //mainState = currentState;
            mainSubState = currentSubState;
            commandType = CM_BOTTOM;
            cancelCommand = "Skip";
        }
        int newSubState = mainSubState;
        boolean setNewSubState = true;

        if (mainSubState == subStateNormal) {
                GTools.handleMenuInput(menuEmail, gAction, keyCode);
                if (keyCode == KEY_SOFTKEY1) {   // OPTIONS Button
                    prepareContextMenu(6);
                    newSubState = subStateShowOptions;
                    GTools.menuSetSelected(menuContextOptions, 0);
                    setCommand(commandType, 1, "Select");
                    setCommand(commandType, 2, "Back");
                } else if(keyCode == KEY_SOFTKEY2) {
                    // SKIP / CANCEL BUTTON
                    if (!isIngameMode) {
                        setNewSubState = false;
                        prepareRequestCharacters();
                    } else {
                        optionState = OPTIONSTATE_NONE;
                        newSubState = OPTIONSUBSTATE_NONE;
                        overlayState = OVERLAY_GAMEOPTIONS;
                        setOverlayCommand1("Select");
                        setOverlayCommand2("Close");
                    }
                }
        } else if (mainSubState == subStateShowOptions) {
                if (keyCode == KEY_SOFTKEY2) { //BACK to email entry
                    newSubState = subStateNormal;
                    setCommand(commandType, 1, "Options");
                    setCommand(commandType, 2, cancelCommand);
                } else {
                    boolean error = false;
                    switch (GTools.menuButtonStatus(menuContextOptions, gAction, keyCode)) {
                        case 0: // SEND EMAIL
                            tmpCharsK = GTools.inputWindowGetText(emailField1);
                            tmpCharsK1 =  GTools.inputWindowGetText(emailField2);
                            if (tmpCharsK == null || containsInvalidChars(tmpCharsK, true) || tmpCharsK1 == null || containsInvalidChars(tmpCharsK1, true)) {
                                tmpStringK = "E-mail invalid. The E-Mail address may only contain characters, numbers and '.', '-', '_'.";
                                error = true;
                            } else {
                                // check for valid domain extension!!
                                tmpStringK = new String(tmpCharsK1);
                                int dotIndex = tmpStringK.lastIndexOf('.');
                                if (dotIndex == -1 || dotIndex >= tmpStringK.length() - 2) {
                                    error = true;
                                    tmpStringK = "You must provide a valid domain extension like .com, .ru, etc.";
                                } else {
                                    // input format ok, send
                                    sendChangeEMail(tmpCharsK, tmpCharsK1);
                                    // wait for success / fail
                                    this.setWaitLabelText("Storing e-mail address ..");
                                    newSubState = subStateChangeWait;
                                    setCommand(commandType, 1, null);
                                    setCommand(commandType, 2, "Game");
                                }
                            }
                            if (error) {
                                newSubState = subStateNormal;
                                setCommand(commandType, 1, "Options");
                                setCommand(commandType, 2, cancelCommand);
                                overlayMessage(tmpStringK);
                            }
                            break;
                        case 1: // Info
                            newSubState = subStateNormal;
                            setCommand(commandType, 1, "Options");
                            setCommand(commandType, 2, cancelCommand);
                            overlayMessage("By storing your e-mail you will be able to restore your password.\nYou will also receive news about Rhynn and related projects which you can cancel anytime.\n\nYour data will not be disclosed to anyone.");
                            break;
                    }
                }
        } else if (mainSubState == subStateEMailGet) {
            if(keyCode == KEY_SOFTKEY2) {
                // CANCEL BUTTON when waiting for get email (GlobalResources.imgIngame only)
                optionState = OPTIONSTATE_NONE;
                newSubState = OPTIONSUBSTATE_NONE;
                overlayState = OVERLAY_GAMEOPTIONS;
                setOverlayCommand1("Select");
                setOverlayCommand2("Close");
            }
        }

        if (setNewSubState) {
            if (!isIngameMode) {
                currentSubState = newSubState;
            } else {
                optionSubState = newSubState;
            }
        }
    }

    private void initAfterIntro() {
        /*
        if (curSoundType+1 > 2) {
            //soundPlayer.startSound(-1, 70);
            playbackSound(0, -1);
        } else {
            //soundPlayer.startSound(-1, 70);
            //curSoundType = (curSoundType+1)%3;
            playbackSound(curSoundType+1, -1);
        }
        */
        /*
        try {
            System.out.println(soundPlayer.midiPlayer.setMediaTime(130000000));
            soundPlayer.midiPlayer.stopPlay();
            soundPlayer.midiPlayer.startPlay();
        } catch (Exception e) {
            System.out.println(e.toString());
        }*/

        /*soundtrack++;
        if (soundtrack >= soundTimes.length) {
            soundtrack = 0;
        }
        try {
            soundPlayer.midiPlayer.stopPlay();
            System.out.println(soundPlayer.midiPlayer.setMediaTime(soundTimes[soundtrack][0]));
            soundPlayer.midiPlayer.startPlay();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        */

        //currentState = STATE_INTRO_LIST;
        if (netStarted) {
            stopNet();
        }
        currentState = STATE_PRE_CONNECT;
        currentSubState = SUBSTATE_NORMAL;
        bServerList = true;
        //host = defaultHost;

        this.setWaitLabelText("Connecting..\n\nPlease be patient.");

        bCommand1 = false;
        bCommand2 = false;
        setBottomCommand2("Game");
        initGraphics();
    }


    private void setFriendOnlineStatus(Integer friendCharacterID, String friendName, boolean online) {

        boolean bWasListed = true;
        
        friendsOnline.removeElement(friendCharacterID);
        friendsOffline.removeElement(friendCharacterID); 

        
        if (friendName != null) {
            // if a name is supplied, this indicates that this friend has not been listed before
            bWasListed = false;
            if (currentState != STATE_FRIEND_RECEIVE_LIST_WAIT) {
                showBottomInfo("Friend added: " + friendName, 8000, true);
            }
            friendsNames.put(friendCharacterID, friendName);
        } else {
            friendName = (String)(friendsNames.get(friendCharacterID));
            if (friendName == null) {
                friendName = "UNKNOWN";
                friendsNames.put(friendCharacterID, friendName);
            }
        }
        
        if (online) {
            // online
            if (currentState != STATE_FRIEND_RECEIVE_LIST_WAIT && bWasListed) {
                showBottomInfo(friendName + " went online", 7000, false);
            }
            friendsOnline.addElement(friendCharacterID);
        } else {
            // offline
            friendsOffline.addElement(friendCharacterID);
            if (currentState != STATE_FRIEND_RECEIVE_LIST_WAIT && bWasListed) {
                showBottomInfo(friendName + " went offline", 7000, false);
            }
        }


        if (currentSubState == SUBSTATE_FRIEND_LIST) {
            // set icon for the entry in question
            int size = friendList.entries.size();
            Integer I = null;
            for (int i=0; i<size; i++) {
                I = (Integer)GTools.listGetDataAt(friendList, i);
                if (I!=null && I.equals(friendCharacterID)) {
                    if (online) {
                        GTools.listSetIconForEntry(friendList, i, iconFriendOnline);
                    } else {
                        GTools.listSetIconForEntry(friendList, i, iconFriendOffline);
                    }
                    break;
                }
            }
        }
        
        
    }
    
    private void prepareOpenFriendList() {
        GTools.listRemoveAllEntries(friendList);

        k1 = friendsOnline.size();
        k2 = friendsOffline.size();
        k5 = 0; // keeps track of the total number fo friends already added to the list

        Integer tmpInt = null;
        String compareString = null;

        // sort online friends on top
        // for all online friends
        for (k3=0; k3<k1; k3++) {
            tmpInt = (Integer)friendsOnline.elementAt(k3);
            if (tmpInt != null) {
                tmpStringK = (String)friendsNames.get(tmpInt);
                if (tmpStringK==null) tmpStringK = "UNKNOWN";
                // sort in
                kb1 = false;    // this denotes that no insert / sort-in was done (yet)
                for (k4=0; k4<k5;k4++) {    // k5 indicates total number of existing entries
                    compareString = GTools.listGetEntryStringAt(friendList, k4);
                            //new String((char[])friendList.entries.elementAt(k4));
                    int cmp = compareString.compareTo(tmpStringK);
                    if (cmp >= 0) {
                        GTools.listInsertEntry(friendList, tmpStringK, tmpInt, k4);
                        GTools.listSetIconForEntry(friendList, k4, iconFriendOnline);
                        k5++;
                        kb1 = true; // note that we found a suitable insert pos
                        break;

                    }
                }
                if(!kb1) {
                    // we did not sort the friend in, so just append
                    GTools.listAppendEntry(friendList, tmpStringK, tmpInt);
                    GTools.listSetIconForEntry(friendList, k5, iconFriendOnline);
                    k5++;
                }
            }
        }

        // save last index of added online friends
        // we need this to actually add the offline friends below (after) the online friends
        k1 = k5;    

        // sort offline friends below
        // for all offline friends
        for (k3=0; k3<k2; k3++) {
            tmpInt = (Integer)friendsOffline.elementAt(k3);
            if (tmpInt != null) {
                tmpStringK = (String)friendsNames.get(tmpInt);
                if (tmpStringK==null) tmpStringK = "UNKNOWN";
                // sort in
                kb1 = false;    // this denotes that no insert / sort-in was done (yet)
                for (k4=k1; k4<k5;k4++) {    // k5 indicates total number of existing entries
                                             // k1 is the first possible index, first index after the last added online friend
                    compareString = GTools.listGetEntryStringAt(friendList, k4);
                            //new String((char[])friendList.entries.elementAt(k4));
                    int cmp = compareString.compareTo(tmpStringK);
                    if (cmp >= 0) {
                        GTools.listInsertEntry(friendList, tmpStringK, tmpInt, k4);
                        GTools.listSetIconForEntry(friendList, k4, iconFriendOffline);
                        k5++;
                        kb1 = true; // note that we found a suitable insert pos
                        break;

                    }
                }
                if(!kb1) {
                    // we did not sort the friend in, so just append
                    GTools.listAppendEntry(friendList, tmpStringK, tmpInt);
                    GTools.listSetIconForEntry(friendList, k5, iconFriendOnline);
                    k5++;
                }
            }
        }
    } 
    
    
    
    private void handleBeltSelection(int gAction, int keyCode, boolean doUse) {
        kb1 = false;
        if (gAction == FIRE || keyCode == KEY_SOFTKEY1) {
            if (doUse) {
                onBeltUse();
            } else if (selectedInvItem >= 0 && selectedInvItem < invItems.length && invItems[selectedInvItem]!=null) {
                addToBelt(invItems[selectedInvItem], selectedBeltItem, true);
                kb1 = true;
            }
        } else if (gAction == LEFT) {
            // move selection cursor left
            selectedBeltItem = selectedBeltItem - 1;
            if (selectedBeltItem < 0) selectedBeltItem = MAX_BELT_ITEMS - 1;
        } else if (gAction == RIGHT) {
            // move selection cursor right
            selectedBeltItem = (selectedBeltItem + 1) % MAX_BELT_ITEMS;
        } else if (keyCode >= 48 && keyCode <= 51) {
            // direct selection + replace
            selectedBeltItem = keyCode - 48;
            if (doUse) {
                onBeltUse();
            } else if (selectedInvItem >= 0 && selectedInvItem < invItems.length && invItems[selectedInvItem]!=null) {
                addToBelt(invItems[selectedInvItem], selectedBeltItem, true);
                kb1 = true;
            }
        } else if (keyCode == KEY_SOFTKEY2) {
            // close belt
            kb1 = true;
        }
        
        if (kb1) {
            if (doUse) {
                // close belt after use
                currentSubState = beltUseReturnState;
                switch (beltUseReturnState) {
                    case SUBSTATE_NORMAL:
                        subStateNormal();
                        break;
                    case SUBSTATE_FIGHT_FIND:
                        // reset target selection only if spell had a positive effect on the target
                        kb1 = triggerTarget_TriggerType == 70 || triggerTarget_TriggerType == 73;
                        if (getPlayersOnScreen(true, 0, kb1, -1)==0) {
                            subStateNormal();
                        } else {
                            setBottomCommand1("Sel. Target");
                            setBottomCommand2("Back");
                            currentSubState = SUBSTATE_FIGHT_FIND;
                        }
                        lastCheck = curGametime;

                        break;
                    case SUBSTATE_FIGHT_ACTIVE:
                        kb1 = triggerTarget_TriggerType == 70 || triggerTarget_TriggerType == 73;
                        if (getPlayersOnScreen(true, 0, kb1, -1)==0) {
                            subStateNormal();
                        } else {
                            // do not reset weapon recharge
                            setBottomCommand1("Attack");
                            setBottomCommand2("Back");
                            currentSubState = SUBSTATE_FIGHT_ACTIVE;
                        }
                        lastCheck = curGametime;
        
                        break;
                }
            } else {
                // return to inventory after slot selection
                substateInventory(selectedInvItem);
            }
        } else {
            // do nothing
        }

    }
    
    
    

    private void openChatShortcutList(boolean forEdit) {    
        
        GTools.menuSetItem(menuBigList, bigList, 0);
        
        if (forEdit) {
            currentSubState = SUBSTATE_CHAT_SHORTCUT_EDIT;
            GTools.menuSetCaptionOneLine(menuBigList, "Edit Short Msg.", font, 0);
            setBottomCommand1("Edit");
            setBottomCommand2("Back");
        } else {
            currentSubState = SUBSTATE_CHAT_SHORTCUT_SELECT;
            GTools.menuSetCaptionOneLine(menuBigList, "Insert Short Msg.", font, 0);
            setBottomCommand1("Insert");
            setBottomCommand2("Back");
        }
        GTools.listRemoveAllEntries(bigList);
        
        for (k1 = 0; k1 < 9; k1++) {
            GTools.listAppendEntry(bigList, "" + (k1+1) + "   " + chatShortcuts[k1], chatShortcuts[k1]);
        }
        GTools.listAppendEntry(bigList, "*   " + chatShortcuts[9], chatShortcuts[9]);
        GTools.listAppendEntry(bigList, "0   " + chatShortcuts[10], chatShortcuts[10]);
        GTools.listAppendEntry(bigList, "#   " + chatShortcuts[11], chatShortcuts[11]);

        k2 = chatShortcuts.length;
        for (k1 = 12; k1 < k2; k1++) {
            GTools.listAppendEntry(bigList, "    " + chatShortcuts[k1], chatShortcuts[k1]);
        }
        
    }

    
    
    private void insertChatShortMessage(String msg) {
        GTools.textWindowAddText(inputChatWindow, msg);
        GTools.inputWindowSetCursorToTextEnd(inputChatWindow);
        currentSubState = nextChatSubstate;
        setBottomCommand1("Options");
        setBottomCommand2("Back");
    }
    
    
    
    /**
     * Prepare to receive a new dialogue element.
     */
    private void getDialogue(int botID, int botphraseID) {
        // initialize bot dialogue
        bCommand1 = false;
        setBottomCommand2("Cancel");
        GTools.textWindowSetText(botphraseWindow, "... Please wait ...");
        for (int k1=0; k1<3; k1++) {
            GTools.textWindowRemoveText(clientphraseWindows[k1]);
            GTools.menuSetSelected(menuClientphrases, -1); // clear selection
            clientphraseWindows[k1].selectable = false;
            clientphraseWindows[k1].backColor = GTools.TRANSPARENT;
            botphraseNextIDs[k1] = 0;   // reset botphrase next id
        }
        dialogueTotalCount = -1;
        dialogueCurrent = -1;

        currentSubState = SUBSTATE_DIALOGUE_INIT;
        sendGetDialogueMessage(botID, botphraseID);
    }
    
    
    private char[] charArrayFromInt(int i) {
        char[] result = null;
        
        int tmp = i;
        int noOfDigits = 1;
        while ((tmp/=10)>0) {
            noOfDigits++;
        }
        if (i<0) {
            result = new char[noOfDigits + 1];
            result[0] = '-';
        } else {
            result = new char[noOfDigits];
        }
        
        while (noOfDigits>0) {
            result[noOfDigits-1] = (char)((i%10)+48);
            i /= 10;
            noOfDigits--;
        }
        return result;
    }
    
    
    private int intFromCharArray(char[] txt) {
        int i = 0;
        
        if (txt!=null && txt.length > 0) {
            int z = 0;
            if (txt[0]=='-' || txt[0]=='+') {
                z = 1;
            }

            // parse the txt
            for (; z<txt.length; z++) {
                if (txt[z]<48 || txt[z]>57) {
                    return 0;
                }
                i *= 10;
                i += (txt[z]-48)%10;
            }
            if (txt[0]=='-') {
                i *= -1;
            }
            
        }
        return i;
        
    }
    
    private void actionMenuActivated(int entry) {
        switch (entry) {
            case -1: 
                break;   //no entry

            case 6: // Friend Options
                prepareActionSubMenu(1);
                currentSubState = SUBSTATE_FRIEND_SUBOPTIONS;
                //prepareContextMenu(5);
                //setBottomCommand1("Select");
                //setBottomCommand2("Back");
                break;
            case 5:
                if (listQuests.entries.size() > 0) {
                    changeQuestmenuView(false, true);   // go to quests overview
                } else {
                    overlayMessage("There are currently\nno active quests.");
                }
                break;
            case 4: //GET TRADE REQUEST ...
                if (getPlayersOnScreen(true, 0, true, -1)==0) {
                    subStateOKDialog("No one in range.", currentState, SUBSTATE_NORMAL);
                } else {
                    currentSubState = SUBSTATE_TRADE_FIND;
                    //setBottomCommand1("Select");
                    //setBottomCommand2("Back");
                }
                break;
            case 3: //TALK / CHAT SUBMENU
                prepareActionSubMenu(0);
                //(menuActionSub, 3*25, TOTALHEIGHT - BOTTOM_INFOHEIGHT - 26 - menuActionSub.height);
                currentSubState = SUBSTATE_TALK_SUBOPTIONS;
                if (talkSubMenu_ShowActionMenu == false) {
                    setBottomCommand1("Select");
                    setBottomCommand2("Back");
                }
                break;
            case 2: //Character ...
                 if (playerObject!=null) {
                    if (playerObject.levelpoints > 0) {
                        setBottomCommand1("Add Point");
                    } else {
                        this.bCommand1 = false;
                    }
                    setBottomCommand2("Back");
                    prepareCharacterBuildScreen();
                    currentSubState = SUBSTATE_BUILDCHARACTER;
                }
                break;
            case 1: //Inventory ...
                setBottomCommand1("Select");
                setBottomCommand2("Close");
                currentSubState = SUBSTATE_INVENTORY;
                playerObject.inventory.setInitialSelection();
                resetInventoryScrollHugeItemSettings();
                atDisplay_Item = null;
                selectedInvItem = 0;
                prepareFreeContextMenu(0);
                break;
            case 0: //FIGHT ...
                setBottomCommand1("Sel. Target");
                setBottomCommand2("Back");
                //get players on screen
                Character sel = playfieldView.selectClosestCharacter(true);
                if (sel==null) {
                    subStateOKDialog("No one in range.", currentState, SUBSTATE_NORMAL);
                } else {
                    currentSubState = SUBSTATE_FIGHT_FIND;
                    GTools.textWindowSetText(info1Line, sel.name + " - L." + sel.level);
                }
                break;
        } 
    
    
    }

    private int getNumUpdatedConversations() {
        int numNew = 0;
        Enumeration e = conversations.elements();
        while (e.hasMoreElements()) {
            Conversation conv = (Conversation)e.nextElement();
            if (conv.getNumNewMessages() > 0) {
                numNew++;
            }
        }
       return numNew;
    }

    private void prepareActionSubMenu(int index) {
        switch (index) {
            case 0: // Chat Submenu

                GTools.buttonListSetButton(menuActionSub, "Talk to player ..", 0, false, true);
                GTools.buttonListSetButton(menuActionSub, "Talk to all", 1, false, true);
                GTools.buttonListSetButton(menuActionSub, "Conversations (" + getNumUpdatedConversations() + ")", 2, false, true);
                GTools.buttonListSetButton(menuActionSub, "Chat Short Messages", 3, false, true);
                break;
            case 1: // Friend Submenu
                GTools.buttonListSetButton(menuActionSub, "View Friend List", 0, false, true);
                GTools.buttonListSetButton(menuActionSub, "Add Friend ..", 1, false, true);
                GTools.buttonListSetButton(menuActionSub, "Incoming Requests (" + friendRequestList.entries.size() + ")", 2, false, true);
                GTools.buttonListUnsetButton(menuActionSub,  3, false, true);
                break;
        }
        
        
        GTools.menuSetSelected(menuActionSub, 0);
        GTools.windowCenterXY(menuActionSub, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
    
    }
    
    private void selectFriendRequest() {
        if (friendRequestList.entries.size() > 0) {
            // check if friend can be added
            if (friendsOnline.size() + friendsOffline.size() >= MAX_FRIENDS) {
                // too many friends in list
                subStateOKDialog("You cannot have more than " + MAX_FRIENDS + " at the same time.\nPlease remove an entry from the friend list before adding more friends.", STATE_GAME, SUBSTATE_FRIEND_REQUEST_LIST);
            } else {
                // friend is ok to add
                int sel = GTools.listGetSelectedIndex(friendRequestList);
                if (sel >= 0) {
                    Integer tmpInt = (Integer)(GTools.listGetDataAt(friendRequestList, sel));
                    if (tmpInt != null) {
                        actionPartnerID = tmpInt.intValue();
                        actionPartnerName = GTools.listGetEntryAt(friendRequestList, sel);
                        currentSubState = SUBSTATE_FRIEND_REQUEST_ACCEPT_CONFIRM;
                        setBottomCommand1("Accept");
                        setBottomCommand2("Decline");
                        GTools.labelSetText(confirmWindow, "Player " + new String(actionPartnerName) + " requests friendship.\nDo you accept?", false);
                        GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                        GTools.listRemoveEntry(friendRequestList, sel);
                    }
                }
            }
        }
    }


    
    private void selectEvent() {
        //select entry
        if (genericList.entries.size() > 0) {
             Conversation conv = (Conversation)GTools.listGetData(genericList);
             openChat(conv.getChannelId(), conv.getChannelName());
             // todo: be sure to adjust entry to show no new messages in the entry
        }

        /*
        if (genericList.entries.size() > 0) {
            int sel = GTools.listGetSelectedIndex(genericList);
            actionPartnerID = queuedEventsIDs[sel];
            characterTmpK = (Character)idToCharacters.get("" + actionPartnerID);
            if (characterTmpK!=null) {
                actionPartnerName = (characterTmpK.name).toCharArray();
            } else {
                actionPartnerName = null;
            }
            characterTmpK=null;
            // todo: change this, use object info itself
            if (true) {
            // -- if (GTools.listGetIconForEntry(genericList, sel)==0) {
                //TALK
                inputChatWindow.maxChars = MAX_CHATCHARS;
                currentSubState = SUBSTATE_TALKTO;
                nextChatSubstate = SUBSTATE_TALKTO;
                GTools.textWindowSetText(chatWindow, (char[])(GTools.listGetData(genericList)));
                setBottomCommand1("Options");
                setBottomCommand2("Back");
            }
            GTools.listRemoveEntry(genericList, sel);
        } else {
            if (currentSubState == SUBSTATE_EVENT_LIST_OPTIONS) {
                currentSubState = SUBSTATE_EVENT_LIST;
                setBottomCommand1("Options");
                setBottomCommand1("Close");
            }
        }
        */
    }
    
    private void selectCharacter() {
        if (genericList.entries.size() > 0 && GTools.listGetData(genericList)!=null) {   // CHARACTER SELECTED
            currentState = STATE_WAIT;
            setWaitLabelText("Selecting character ..");
            bCommand1 = false;
            // ASSIGN PLAYER OBJECT
            playerObject = (Character)GTools.listGetData(genericList);

            for (int i=0; i<characterClasses.size(); i++) {
                CharacterClass cc = (CharacterClass)characterClasses.elementAt(i);
                if (cc.classId == playerObject.classId) {
                    characterClassName = cc.displayName;
                }
            }

            playerObject.HealthBarDisplay.setOn(true);
            playerObject.useImage(imageManager.getImageFromCache(playerObject.graphicsId));

            playerDirection = DirectionInfo.DOWN;
            playerObject.setDirection(DirectionInfo.DOWN);

            // Save the character details globally
            // System.out.println("playerObject curHealth: " + playerObject.curHealth);
            // System.out.println("playerObject healthBase: " + playerObject.healthBase);

            // todo: check if we still need this, only use the values stored with the object
            maxhealth = playerObject.getTotalMaxHealth();
            maxmana = playerObject.getTotalMaxMana();
            attack = playerObject.getTotalAttack();
            defense = playerObject.getTotalDefense();
            skill =  playerObject.getTotalSkill();
            magic = playerObject.getTotalMagic();
            damage = playerObject.getTotalDamage();
            healthregenerate = playerObject.getTotalHealthregenerate();
            manaregenerate = playerObject.getTotalManaregenerate();
            
            //addCharacterOK = false;
            
            character_DB_ID = playerObject.objectId;
            
            // load chat shortcuts
            for (int i = 0; i < chatShortcuts.length; i++) {
                // write chat shortcuts to RS
                try {
                    tmpStringM = database.getValue("csc" + character_DB_ID + "_" + i);
                    if (tmpStringM!=null) {
                        chatShortcuts[i] = tmpStringM;
                    }
                } catch (Exception e) {}
            }
            
            
            //playfieldName = characterTmpK.name;
            //if (requestInventory) {
                // load inventory images now
                    // todo: don't use the items image array for this any longer as the items know how to draw themselves using the graphics id

                    
                    imageManager.loadImageFromJarToCache(100006, "items01.png", true);
                    imageManager.loadImageFromJarToCache(100007, "items02.png", true);
                    /*
                    items[0] = imageManager.getImageFromCache(2);
                    items[1] = imageManager.getImageFromCache(3);
                     */
                /*
                try {
                    if (items[0]==null) {
                        items[0] = Image.createImage("/items.png");
                    }
                    if (items[1]==null) {
                        items[1] = Image.createImage("/items2.png");
                    }
                } catch(IOException ioe) {
                    if (debugLevel > 0)
                        System.out.println(ioe);
                }
                */
                // allow to display info for each item loaded, so set the packet
                // rate to 1 to have the game loop react on each packet receipt
                // will be reset to default when all inv. items are received
                    PACKETPERLOOP = 1;
                sendSelectCharacter(character_DB_ID);

                //System.out.println("Select character was sent");
                // this implicitly triggers loading of the inventory (to avoid sending back and forth more messages)
                setWaitLabelText("Loading Inventory ..");
                currentState = STATE_INVENTORY_LOAD_WAIT;

                
                
            /*                        
            } else {
                sendChooseCharacterMessage(character_DB_ID);
            }*/
            //characterTmpK = null;
        } else {
            subStateOKDialog("No character selected", STATE_CHARACTER_SELECT, SUBSTATE_NORMAL);
            if (currentSubState==this.SUBSTATE_CHARACTER_OPTIONS) {
                setBottomCommand1("Options");
            }
        }
    }
    
    
    private boolean handleMoveInput(int gameAction) {
        if (blockDuration>0) {
            return true;
        }

        int newDirection = 0;
        
        switch(gameAction) {
            case UP: newDirection = DirectionInfo.UP; break;
            case DOWN: newDirection = DirectionInfo.DOWN; break;
            case LEFT: newDirection = DirectionInfo.LEFT; break;
            case RIGHT: newDirection = DirectionInfo.RIGHT; break;
            default: return false;
        }

        if (newDirection != playerObject.getDirection()) {
            //sendPos = true;
            playerObject.setDirection(newDirection);            
        }
        playerMove = true;

        return true;

        /*
        switch(gameAction) {
            case UP:
                if (direction!=0) {
                   sendPos = true;
                   //lastMoveTime = curGametime;
                }
                direction = 0;
                playerMove = true;
                break;
            case DOWN:
                if (direction!=2) {
                   sendPos = true;
                   //lastMoveTime = curGametime;
                }
                direction = 2;
                playerMove = true;
                break;
            case LEFT:
                if (direction!=3) {
                   sendPos = true;
                   //lastMoveTime = curGametime;
                }
                direction = 3;
                playerMove = true;
                break;
            case RIGHT:
                if (direction!=1) {
                   sendPos = true;
                   //lastMoveTime = curGametime;
                }
                direction = 1;
                playerMove = true;
                break;
            default:
                return false;
        }

        return true;
         */
    }

    private boolean isInWeaponRange(WorldObject fwgo, Item weapon, int factor) {
        int xOwn, yOwn, xOther, yOther;
        int full;
        int reduced;
        boolean inRange = false;
        
        int width_self = playerObject.graphicsDim*DIM;
        int height_self = playerObject.graphicsDim*DIM;
        
        int overSize_H_other = (((fwgo.graphicsDim*DIM) - width_self) >> 1);
        int overSize_V_other = (((fwgo.graphicsDim*DIM) - height_self) >> 1);
        
        
        xOwn = playerObject.x + (width_self >> 1);
        yOwn = playerObject.y + (height_self >> 1);
        xOther = fwgo.x + ((fwgo.graphicsDim*DIM) >> 1);
        yOther = fwgo.y + ((fwgo.graphicsDim*DIM) >> 1);
        
        int xDistance = xOwn - xOther;
        int yDistance = yOwn - yOther;
        
        int xDistanceOS = xDistance;
        int yDistanceOS = yDistance;
        
        // take into account reduced distance if monster is bigger than average
        if (xDistance > 0) {
            xDistanceOS -= overSize_H_other;
            if (xDistanceOS < 0) xDistanceOS = 0;
        } else {
            xDistanceOS += overSize_H_other;
            if (xDistanceOS > 0) xDistanceOS = 0;
        }

        if (yDistance > 0) {
            yDistanceOS -= overSize_V_other;
            if (yDistanceOS < 0) yDistanceOS = 0;
        } else {
            yDistanceOS += overSize_V_other;
            if (yDistanceOS > 0) yDistanceOS = 0;
        }
        
        // do range / peaceful check
        if (factor >= 2) {
            factor = (factor * 26) * (factor * 26);
        }
        
        
        if (weapon!=null) {
            full = weapon.data*weapon.data;
            reduced = weapon.data - GlobalSettings.DEFAULT_WEAPON_RANGE - 4;
            reduced = reduced * reduced * reduced;
            inRange = (fwgo!=null && 
                    xDistanceOS * xDistanceOS 
                  + yDistanceOS * yDistanceOS
                  <= full + reduced + factor && 
                  !isPeaceful(xOther, yOther) && !isPeaceful(xOwn, yOwn));
        } else {
            inRange =  (fwgo!=null && 
                    xDistanceOS * xDistanceOS 
                  + yDistanceOS * yDistanceOS
                  <= GlobalSettings.DEFAULT_WEAPON_RANGE*GlobalSettings.DEFAULT_WEAPON_RANGE + factor &&
                  !isPeaceful(xOther, yOther) && !isPeaceful(xOwn, yOwn));
        }
        
        // if object is in range and neither player nor target is on a peaceful 
        // cell (AND player is not using a long range weapon, make sure a direct)
        // AND player is not using a spell
        // line from player to target does not collide with any blocking field
        //if (true) {
            //((inRange && weapon==null) || (inRange && weapon!=null && weapon.classId!=3)) {
            if (factor < 2 || (triggerTarget_TriggerType!=70 && triggerTarget_TriggerType!=73) ) { // don't check direct line for certain spells which may be used even through walls (heal / raise attr)
                // avoid division by zero
                if (xDistance==0) {xDistance=1;} 
                if (yDistance==0) {yDistance=1;}
            
                int tx, ty;
                int totalX = 0;
                int totalY = 0;
                int xExtra = 0;
                int yExtra = 0;
                int xStep = TILEWIDTH;
                int yStep = TILEHEIGHT;
                yOther = 0;
                xOther = 0;

                // make sure tracing is done in correct directions
                if (xDistance > 0) {xStep *= -1;}
                if (yDistance > 0) {yStep *= -1;}
                
                
                if (xDistance*xDistance > yDistance*yDistance) {
                    yStep = ((yDistance * 100) / xDistance) * xStep;    // gradient of direct line * 100
                    yOther = yStep % 100;   // y line correction value, 2 decimal precision
                    yStep = yStep / 100;    // rounded number of pixels in y per one xStep
                } else if (yDistance*yDistance > xDistance*xDistance) {
                    xStep = ((xDistance * 100) / yDistance) * yStep;    // gradient of direct line * 100
                    xOther = xStep % 100;   // y line correction value, 2 decimal precision
                    xStep = xStep / 100;    // rounded number of pixels in y per one xStep
                }

                while (totalX*totalX < xDistance*xDistance && totalY*totalY < yDistance*yDistance) {
                    
                    xExtra += xOther;
                    yExtra += yOther;
                    if (xExtra*xExtra >= 10000) {totalX += xExtra/100; xExtra = xExtra%100;}
                    if (yExtra*yExtra >= 10000) {totalY += yExtra/100; yExtra = yExtra%100;}

                    totalX += xStep;
                    totalY += yStep;

                    if (totalX*totalX > xDistance * xDistance) {totalX = -xDistance;}
                    if (totalY*totalY > yDistance * yDistance) {totalY = -yDistance;}
                    
                    
                    tx = (xOwn + totalX) / TILEWIDTH;
                    ty = (yOwn + totalY) / TILEHEIGHT;
                    if (tx>=0 && tx<playfieldWidth && ty>=0 && ty<playfieldHeight && ((legacyPlayfield[tx][ty] & 32) == 32))
                    {
                        inRange = false;
                        break;
                    }
                    
                    
                    //save previous gfx settings
                    /*
                    int prevColor = currentGraphics.getColor();
                    int prevClipX = currentGraphics.getClipX();
                    int prevClipY = currentGraphics.getClipY();
                    int prevClipWidth = currentGraphics.getClipWidth();
                    int prevClipHeight = currentGraphics.getClipHeight();
                    
                    currentGraphics.setColor(255, 0, 0);
                    currentGraphics.setClip(playerScreenX + ((playerObject.graphicsDim*DIM)/2) + totalX - 2, playerScreenY  + ((playerObject.graphicsDim*DIM)/2) + totalY - 2, 4, 4);
                    currentGraphics.fillRect(playerScreenX + ((playerObject.graphicsDim*DIM)/2) + totalX - 2, playerScreenY + ((playerObject.graphicsDim*DIM)/2) + totalY - 2, 4, 4);
                    
                    //restore previous settings
                    currentGraphics.setColor(prevColor);
                    currentGraphics.setClip(prevClipX, prevClipY, prevClipWidth, prevClipHeight);
                     */
                }
                
            }
            
        //}
            
        return inRange;
        
    }

    /*
    private class CharacterFindInfo {
        public Character character = null;
        public boolean findRequested = false;
    }*/

    private boolean handleFindInput(int gAction, int keyCode, boolean excludeOwnCharacter) {
        int dir = -1;
        switch(gAction) {
            case LEFT: dir = DirectionInfo.LEFT; break;
            case RIGHT: dir = DirectionInfo.RIGHT; break;
            case UP: dir = DirectionInfo.UP; break;
            case DOWN: dir = DirectionInfo.DOWN; break;
        }

        if (dir != -1) {
            Character c = playfieldView.setSelectedCharacterNextDir(dir, excludeOwnCharacter);
            if (c != null) {
                GTools.textWindowSetText(info1Line, c.name + " - L." + c.level);
                return true;
            }
        }

        return false;
    }

    private Character checkKeepCharacterSelection(boolean allowSelectNext, boolean excludeOwnCharacter) {
        Character selChar = null;
        if (!playfieldView.selectedCharacterInsideView(16)) {
            if (allowSelectNext) {
                selChar = playfieldView.selectClosestCharacter(excludeOwnCharacter);
                if (selChar != null) {
                    GTools.textWindowSetText(info1Line, selChar.name + " - L." + selChar.level);
                }
            }
        } else {
            selChar = playfieldView.getSelectedCharacter();
        }
        return selChar;
    }

    private void handleFindInput(int gAction, int keyCode, int curState, int activeState) {
        int s = selectedPlayer;
        if (gAction==FIRE || keyCode==KEY_SOFTKEY1) {
            //SET THE ACTIVE STATE
            currentSubState = activeState;
            
            if (selectedPlayer >= 0 && selectedPlayer < playersOnScreen.length) {
                //get ID of chat partner
                characterTmpK = playersOnScreen[selectedPlayer];
                if (characterTmpK!=null) {
                    actionPartnerID = characterTmpK.objectId;
                    actionPartnerName = (characterTmpK.name).toCharArray();
                }
                characterTmpK=null;
            }
            
            //selectedPlayer = 0;
        } else {
            k2 = playersOnScreen.length;
            k3 = 0;
            k4 = 0;
            kb1 = false;
            k5 = 0;
            
            switch(gAction) {
                case LEFT: 
                    // find player on the left
                    k4 = -1;
                    k5 = 0; // x change
                    kb1 = true;
                    break;
                case RIGHT:
                    // find player on the right
                    k4 = 1;
                    k5 = 0; // x change
                    kb1 = true;
                    break;
                case UP:
                    // find player upwards
                    k4 = -1;
                    k5 = 1; // y change
                    kb1 = true;
                    break;
                case DOWN:
                    // find player downwards
                    k4 = 1;
                    k5 = 1; // y change
                    kb1 = true;
                    break;
                default:
                    if (keyCode==KEY_SOFTKEY2) {  //BACK to normal
                        if (currentSubState == SUBSTATE_TRIGGERTARGET_FIND) {
                            if (triggerOrGroundFindReturnState==SUBSTATE_BELT) {
                                substateBelt(selectedBeltItem);
                            } else {
                                substateInventory(selectedInvItem);
                            }
                        } else {
                            subStateNormal();
                        }
                        selectedPlayer = 0;
                    }
                    break;
            }
            
            if (kb1) {
                // change of the selected player was requested

                int[] sortedIndizes;
                Character[] sortedPlayers;
                
                if (k5 == 0) {
                    // change in x direction requested, so use x-axis order
                    sortedPlayers = playersOnScreen;
                    sortedIndizes = playersOnScreenXSortedIndex;
                } else {
                    // change in y direction requested, so use y-axis order
                    sortedPlayers = playersOnScreen;
                    sortedIndizes = playersOnScreenYSortedIndex;
                }
                
                k3 = -1;
                for (k1=0; k1<k2; k1++) {
                    if (sortedIndizes[k1] == selectedPlayer) {
                        k3 = k1 + k4; // get index in sorted array, k4 is either +1 or -1 depending if next (right or down) / prev (left or up) should be selected
                        break;
                    }
                }

                if (k3 < 0) {
                    k3 = sortedIndizes.length;
                    do {
                        k3--;
                    } while (sortedIndizes[k3] == -1 && k3 > 0);
                } else if (k3 > k2 || sortedIndizes[k3] == -1) {
                    k3 = -1;
                    do {
                        k3++;
                    } while (sortedIndizes[k3] == -1 && k3 < k2-1);
                }

                if (k3 < 0 || k3 >= k2 || sortedIndizes[k3]==-1) {
                    selectedPlayer += k4; // value is errorchecked further down
                } else {
                    selectedPlayer = sortedIndizes[k3];
                }
                
            }
            
        }

        
        if (selectedPlayer<0) {
            selectedPlayer = playersOnScreen.length-1;
            while (selectedPlayer>0 && playersOnScreen[selectedPlayer]==null) {
                selectedPlayer--;
            }
        } else if (selectedPlayer >= playersOnScreen.length || playersOnScreen[selectedPlayer]==null) {
            selectedPlayer = 0;
            while (selectedPlayer<playersOnScreen.length && playersOnScreen[selectedPlayer]==null) {
                selectedPlayer++;
            }
        }
        if (s!=selectedPlayer && playersOnScreen[selectedPlayer]!=null && currentSubState == curState) {
            GTools.textWindowSetText(info1Line, playersOnScreen[selectedPlayer].name + " (L." + playersOnScreen[selectedPlayer].level + ")");
        }


    }
    
    

        
    /** Check if a game object is visible on the player's screen. */
    private boolean objectVisible(WorldObject fwgo, int tolerance) {
        return (fwgo!=null && 
                 (fwgo.x + (fwgo.graphicsDim*DIM) > xPos - tolerance && fwgo.x < xPos + DISPLAYWIDTH + tolerance
/*#Series40_MIDP2_0#*///<editor-fold>
//#                   && fwgo.y + (fwgo.graphicsDim*DIM) > yPos - tolerance && fwgo.y < yPos + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT + tolerance)
/*$Series40_MIDP2_0$*///</editor-fold>
/*#!Series40_MIDP2_0#*///<editor-fold>
                  && fwgo.y + (fwgo.graphicsDim*DIM) > yPos - tolerance && fwgo.y < yPos + DISPLAYHEIGHT + tolerance)
/*$!Series40_MIDP2_0$*///</editor-fold>
               );
    }
    
    private boolean inventoryCheckNavigationInput(int gameAction, Inventory inventory) {
        boolean handled = true;
        switch(gameAction) {
            case UP:
                inventory.navigatePrevRow();
                break;
            case DOWN:
                inventory.navigateNextRow();
                break;

            case LEFT:
                inventory.navigatePrevSlot();
                break;
            case RIGHT:
                inventory.navigateNextSlot();
                break;
            default:
                handled = false;
                break;
        }
        return handled;
        /*
        int index;
        Item[] itemArray;
        
        if (inventory) {
            index = selectedInvItem;
            itemArray = invItems;
        } else {
            index = selectedTradeOfferItem;
            itemArray = tradeOfferItems;
        }
        
        boolean handled = true;
        switch(gameAction) {
            case UP:
                if (index > INVENTORY_COLS-1)
                    index-=INVENTORY_COLS;
                break;
            case DOWN:
                if (index < (INVENTORY_ROWS-1)*INVENTORY_COLS)
                    index+=INVENTORY_COLS;
                break;
            case LEFT:
                if (index > 0) {
                    index--;
                } else {
                    index = (INVENTORY_ROWS*INVENTORY_COLS)-1; // last slot
                }
                break;
            case RIGHT:
                if (index < (INVENTORY_ROWS*INVENTORY_COLS)-1) {
                    index++;
                } else {
                    index = 0; // first slot
                }
                break;
            default:
                handled = false;
                break;
        }

        if (inventory) {
            selectedInvItem = index;
        } else {
            selectedTradeOfferItem = index;
        }
        
        resetInventoryScrollHugeItemSettings();
        
        return handled;
         */
    }






    private void resetSelectedActionPartner() {
        actionPartnerID = -1;
        playfieldView.deselectSelectedCharacter();
    }
    
    private void closeTalkToAll() {
        menuChat.height += chatWindow.height+1;
        menuChat.y -= chatWindow.height+1;
        inputChatWindow.maxChars = MAX_CHATCHARS;
        GTools.inputWindowRemoveText(inputChatWindow);
        GTools.menuSetItem(menuChat, chatWindow, 0);
        closeChat();
    }



    private void openTalkToAll() {
        menuChat.height -= chatWindow.height+1;
        menuChat.y += chatWindow.height+1;
        inputChatWindow.maxChars = MAX_CHATCHARS_PER_LINE;
        GTools.menuSetItem(menuChat, null, 0);
    }


    /**
     * Switch the quest menu to either show only the quest overcvview or to show 
     * quest details.
     * showDetails if true, switch to details view, else switch to quests overview
     * changeState if true, the game state will be changed accordingly (use 
     * always apart from init)
     */
    private void changeQuestmenuView(boolean showDetails, boolean changeState) {
        if (changeState) {
            if (showDetails) {
                currentSubState = SUBSTATE_QUEST_DETAILS;
                bCommand1 = false;
                setBottomCommand2("Back");
            } else {
                currentSubState = SUBSTATE_QUEST_OVERVIEW;
                GTools.listSetSelectedIndex(listQuests, 0); // select first entry
                setBottomCommand1("Options");
                setBottomCommand2("Close");
            }
        }
        boolean visiblevalue = !showDetails;

        for (int i=0; i<menuQuests.items.length; i++) {
            menuQuests.items[i].visible = visiblevalue;
            if (i==0) {visiblevalue=!visiblevalue;}
        }
    }

    private void prepareLoadingWorldScreen() {
        GTools.textWindowRemoveText(highScoreWindow);
        GTools.labelSetText(label2, "Top 5 Characters:", false);
//#if Series40_MIDP2_0
//#         label2.y = gaugeWindow.y + gaugeWindow.height + 22;
//#else
        label2.y = gaugeWindow.y + gaugeWindow.height + 42;
//#endif
        GTools.windowCenterX(label2, 0, DISPLAYWIDTH);
//#if Series40_MIDP2_0
//#         highScoreWindow.y = label2.y + label2.height + 1;
//#else
        highScoreWindow.y = label2.y + label2.height + 10;
//#endif

        //loading world label
        if (playfieldName!=null && playfieldName.length()>0) {
            GTools.labelSetText(label1, playfieldName, false);
        } else {
            GTools.labelSetText(label1, "Entering world..", false);
        }
//#if Series40_MIDP2_0
//#         label1.y = gaugeWindow.y - 2*font.charHeight - 1;
//#else
        label1.y = gaugeWindow.y - font.charHeight - 2;
//#endif
        GTools.windowCenterX(label1, 0, DISPLAYWIDTH);
        
        usersOnline = 0; usersTotal = 0;
        //requestNumUsersOnline();
    }
    
    

    
    private synchronized int getPlayersOnScreen(boolean setText, int tolerance, boolean resetSelection, int filterClassID) {
        boolean includeOwn = false;
        if (currentSubState == SUBSTATE_TRIGGERTARGET_FIND && (triggerTarget_TriggerType == 70 || triggerTarget_TriggerType == 73)) {
            includeOwn = true;
        }
        
        if (playerObject==null) {
            return 0;
        }
        //boolean topologyOrder = true;
        
        int i=0;
        int l, m;
        int startX = xPos;
        int startY = yPos;
        int k= xPos + DISPLAYWIDTH; //right limit of range
//#if Series40_MIDP2_0
//#         int j= yPos + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT; //bottom limit of range
//#else
        int j= yPos + DISPLAYHEIGHT; //bottom limit of range
//#endif


        int startXNoTolerance = startX;
        int startYNoTolerance = startY + info1Line.height;
        int endXNoTolerance = k;
        int endYNoTolerance = j;

        int smallestDistanceIndex = 0;
        int smallestDistance = -1;
        int curDistance = 0;
        
        // take tolerance into account
        startX -= tolerance;
        startY -= tolerance;
        j += tolerance;
        k += tolerance;
        
        Character fwgoTmp1 = playersOnScreen[selectedPlayer];
        Character fwgoTmp = null;
        selectedPlayer = 0;
        Enumeration e = idToCharacters.elements();
        
        
        if (includeOwn && playerObject != null) {
            playersOnScreen[0]=playerObject;
            playersOnScreenXSortedIndex[0] = 0;
            playersOnScreenYSortedIndex[0] = 0;
            i++;
        }
        
        while(e.hasMoreElements() && i < playersOnScreen.length) {
            fwgoTmp = (Character)e.nextElement();
            if (fwgoTmp!=null && fwgoTmp.objectId!=character_DB_ID && (filterClassID == -1 || fwgoTmp.classId==filterClassID)) {
                l=fwgoTmp.x + fwgoTmp.graphicsDim*DIM;
                m=fwgoTmp.y + fwgoTmp.graphicsDim*DIM;
                //check if in range
                if (l >= startX+3 && fwgoTmp.x <= k-3 && m >= startY+3 && fwgoTmp.y <= j-3) {
                    //add to array
                    playersOnScreen[i]=fwgoTmp;
                    if (!resetSelection && playersOnScreen[i]!=null && playersOnScreen[i] == fwgoTmp1) {
                        selectedPlayer = i; //save new index
                    }

                    // pre-filter x / y sorted player list to only make players actively selectable which are really visible on screen
                    if (tolerance == 0 || (l >= startXNoTolerance+3 && fwgoTmp.x <= endXNoTolerance-3 && m >= startYNoTolerance+3 && fwgoTmp.y <= endYNoTolerance-3) || (!resetSelection && playersOnScreen[i] == fwgoTmp1)) {
                        // this object is directly visible on the screen or it is the selected player
                        playersOnScreenXSortedIndex[i] = i;
                        playersOnScreenYSortedIndex[i] = i;
                    } else {
                        // this object is NOT directly visible on the screen, so make sure it won't be selected via the cursor keys
                        playersOnScreenXSortedIndex[i] = -1;
                        playersOnScreenYSortedIndex[i] = -1;
                    }
                    
                    l = (playerObject.x + ((playerObject.graphicsDim*DIM)/2)) - (fwgoTmp.x + ((fwgoTmp.graphicsDim*DIM)/2));
                    m = (playerObject.y + ((playerObject.graphicsDim*DIM)/2)) - (fwgoTmp.y + ((fwgoTmp.graphicsDim*DIM)/2));
                    // store distance
                    
                    if (!includeOwn) {
                        curDistance = l*l + m*m;
                    
                        if (curDistance < smallestDistance || smallestDistance==-1) {
                            smallestDistanceIndex = i;
                            smallestDistance = curDistance;
                        }
                    }
                    i++;
                }
            }
        }
        //if (topologyOrder) {
            // topology order
            sortPlayersOnScreen(i);
        //}
        if (resetSelection) {
            selectedPlayer= smallestDistanceIndex;
        }
        
        l = i;
        
        while(i<playersOnScreen.length) {
            playersOnScreen[i]=null;
            playersOnScreenXSortedIndex[i]=-1;
            playersOnScreenYSortedIndex[i]=-1;
            i++;
        }

        if (playersOnScreen[selectedPlayer]==null) {
            GTools.textWindowRemoveText(info1Line);
        } else if (setText || playersOnScreen[selectedPlayer] != fwgoTmp1) {
            GTools.textWindowSetText(info1Line, playersOnScreen[selectedPlayer].name  + " (L." + playersOnScreen[selectedPlayer].level + ")");
        }
        
        
        return l;
    }
    
    
    private void sortPlayersOnScreen(int limit) {
        int j;
        int curIndex;
        int nextIndex;

        for (int i=limit; --i >= 0;) {
            for (j=0; j<i; j++) {

                curIndex = playersOnScreenXSortedIndex[j];
                nextIndex = playersOnScreenXSortedIndex[j+1];
                
                if (curIndex == -1 ||  (nextIndex!=-1 && playersOnScreen[curIndex].x > playersOnScreen[nextIndex].x)) {
                    playersOnScreenXSortedIndex[j] = nextIndex;
                    playersOnScreenXSortedIndex[j+1] = curIndex;
                }

                curIndex = playersOnScreenYSortedIndex[j];
                nextIndex = playersOnScreenYSortedIndex[j+1];
                
                if (curIndex == -1 ||  (nextIndex!=-1 && playersOnScreen[curIndex].y > playersOnScreen[nextIndex].y)) {
                    playersOnScreenYSortedIndex[j] = nextIndex;
                    playersOnScreenYSortedIndex[j+1] = curIndex;
                    
                }

            }
        }
        
    }
    
    
    
    private void subStateNormal() {
        playerMove = false;
        sendPos = false;
        currentSubState = SUBSTATE_NORMAL;
        overlayState = OVERLAY_NONE;
        setBottomCommand1("Action");        
        setBottomCommand2("Game");
        playfieldView.deselectSelectedCharacter();
    }
    
    private void substateInventory(int itemIndex) {
        setBottomCommand1("Select");
        setBottomCommand2("Close");
        currentSubState = SUBSTATE_INVENTORY;
        playerObject.inventory.setInitialSelection();
        resetInventoryScrollHugeItemSettings();
        atDisplay_Item = null;
        selectedInvItem = itemIndex;
        prepareFreeContextMenu(0);
    }

    private void substateBelt(int itemIndex) {
        setBottomCommand1("Select");
        setBottomCommand2("Close");
        currentSubState = SUBSTATE_BELT;
        selectedBeltItem = itemIndex;
    }
    

    private void setWaitLabelText(String text) {
        GTools.labelSetText(labelWait, text, true);
        GTools.windowCenterXY(labelWait, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
    }
    
    /**
     * Key was released.
     */
    protected void keyReleased(int keyCode) {
        //if ((currentState==STATE_GAME && currentSubState==SUBSTATE_NORMAL) || currentSubState == SUBSTATE_FIGHT_ACTIVE) {
            playerMove = false;
            // -- lastMoveDirection = -1;
            // -- extraMovePixels = 0;
            //changed = true;
        //}
    }
    
    /**
     * Key repeat.
     */
    protected void keyRepeated(int keyCode) {
    }
    
    
    ///////////////////////////////////
    // NET: RECEIVE / SEND MESSAGES
    ///////////////////////////////////

    
    /** Set a timeout for a specific message. If the message doesn't arrive in time an error message is displayed, 
     *  and the current state/substate is switched thereafter.
     */
    private void setMessageWaitTimeout(int msgId, int waitTime, int stateAfterTimeout, int subStateAfterTimeout, String timeoutMessage, String bottomCommand1AfterTimeout, String bottomCommand2AfterTimeout) {
        /*
        awaitedMessage[0] = msg_ID_1;
        awaitedMessage[1] = msg_ID_2;
        awaitedMessage[2] = msg_ID_3;
        awaitedMessage[3] = msg_ID_4;
         */
        awaitedMessageId = msgId;

        if (timeoutMessage==null) {
            messageTimeoutMessage = "Network timeout!";
        } else {
            messageTimeoutMessage = timeoutMessage;
        }

        this.subStateAfterTimeout = subStateAfterTimeout;
        this.stateAfterTimeout = stateAfterTimeout;
        this.bottomCommand1AfterTimeout = bottomCommand1AfterTimeout;
        this.bottomCommand2AfterTimeout = bottomCommand2AfterTimeout;
        
        messageTimeout = waitTime * 1000;   //in seconds
    }

    
    //
    // HANDLE INCOMING SERVER MESSAGES
    //
    private void handleMessagePong() {
        //System.out.println("Received pong from server");
    }


    private void handleMessageServerEntry(byte[] message) {
        // server ip
        tmpStringM = new String(message, 5, message[4]);
        // server name
        m1 = 5 + message[4];    // pos of the length of the server name
        tmpStringM1 = new String(message,  m1+1, message[m1]);
        GTools.listAppendEntry(genericList, tmpStringM1, tmpStringM);
        tmpStringM = null;
    }


    private void handleMessageVersion(byte[] message) {
        //int serverVersionLowSub = 0;
        int serverVersionHigh = message[4];
        int serverVersionLow = message[5];
        int serverVersionLowSub = message[6];

        if (versionHigh >= serverVersionHigh && versionLow >= serverVersionLow && versionLowSub >= serverVersionLowSub) {
            if (debugLevel > 0) {
                //System.out.println("Using version: " + message[8] + "." + message[9] + " OK.");
            }
            //proceed to login / register
            currentState = STATE_INTRO_LIST;
            prepareListIntro();
            setBottomCommand1("Select");
            initGraphics();
        } else {    //outdated version
            if (debugLevel > 0) {
                //System.out.println("Error: Server requires client version: " + message[8] + "." + message[9]);
            }
            // -- doLogout();
            String versionLowSubString = "" + versionLowSub;
            if (versionLowSub < 10) {
                versionLowSubString="0" + versionLowSubString;
            }
            String serverVersionLowSubString = "" + serverVersionLowSub;
            if (serverVersionLowSub < 10) {
                serverVersionLowSubString="0" + serverVersionLowSubString;
            }


            stopNet();
            subStateOKDialog("Server requires version:\n" + serverVersionHigh + "." + serverVersionLow + "." + serverVersionLowSubString + "\n\nYour version:\n"  + versionHigh + "." + versionLow + "." + versionLowSubString +  "\n\nGet new version at:\nwww.rhynn.com", STATE_INTRO, SUBSTATE_ACTIVE);
            netError = false;
            doConnect = 0;
        }
    }

    private void handleMessageRegisterResult(byte[] message) {

        tmpStringM = new String(message, 6, message[5]);
        if (message[4]==1) {
            // registering was successful
            registerSuccessMessage = tmpStringM;
            proceedToLoginAfterRegister();
        } else {
            // registering failed
            //checkNet = false;
            //doLogout();
            GTools.menuSetSelected(menuLogin, 1);
            setBottomCommand1("Register");
            setBottomCommand2("Back");
            currentState = STATE_REGISTER_NEW;
            setWaitLabelText("Remember the\npassword well!\nIt may only be\nrecovered by\ne-mail!");
            labelWait.centerTextH = false;
            labelWait.x = passwordWindow.x;
            labelWait.y = passwordWindow.y + passwordWindow.height;

            character_DB_ID = 0;
            overlayMessage(tmpStringM);
            // -- lastJoinedGroup = null;
            // -- sendLeaveGroupMessage();
            
            /*
            currentState = STATE_INTRO_LIST;
            setBottomCommand1("Select");
            character_DB_ID = 0;
            lastJoinedGroup = null;
            sendLeaveGroupMessage();
            subStateOKDialog("User name already taken.", STATE_INTRO_LIST, SUBSTATE_NORMAL);
            */
        }


    }

    private void handleMessageForcedLogout(byte[] message) {
        forcedExit(new String(message, 5, message[4]));
    }



    private void handleMessageLoginResult(byte[] message) {
        if(message[4] == 1) {
            // login granted
            //System.out.println("flp: login granted (FW)");
            // Store the name and password for easy login at next startup
            if (clientName!=null && clientPass!=null) {
                database.setValue("clientname", clientName.trim());
                database.setValue("clientpass", clientPass.trim());
            }

            // Save USER DB ID (!=gameobject DB ID, gameobject DB ID is determined via the character on "character selected" (table gameobjects)
            user_DB_ID = NetTools.intFrom4Bytes(message[5], message[6], message[7], message[8]);
//System.out.println("FW login granted, user DB_ID: " + user_DB_ID);
            if (debugLevel >= 2) {
                //System.out.println("FW login granted, user DB_ID: " + user_DB_ID);
            }

           int initValue = NetTools.intFrom4Bytes(message[9], message[10], message[11], message[12]);
           packetValidator.initialize(initValue);
           
           if(!usingServerPortal) {
                // send back the challenge number
                byte[] challengeNumber = new byte[8];
                System.arraycopy(message, 13, challengeNumber, 0, 8);
                crypt(challengeNumber);
                sendChallengeNumber(challengeNumber);
                doConnect = 0;

                if (justRegistered) {
                    //justRegistered = false;
                    overlayMessage(registerSuccessMessage + "\n\nYou may now enter your e-mail address.\nThis step is optional.");
                    changeEmail();
                } else {
                    prepareRequestCharacters();
                }
            } else {

               // $-> MinServer: Need to check this, what is it, when does it occur, if at all=
               // used when changing server accross portals, implement this on server (change login code to allow for select character immediately after login
               // should probably work without great changes
                //
                // Portal Jump
                //
                currentState = STATE_WAIT;
                sendChooseCharacterMessage(character_DB_ID);
            }
        }

        //
        //LOGIN DENIED
        //
        else  {
            //checkNet = false;
            //doLogout();
            currentState = STATE_INTRO_LIST;
            setBottomCommand1("Select");
            character_DB_ID = 0;
            // -- lastJoinedGroup = null;
            // -- sendLeaveGroupMessage();

            if (message[21] > 0) {
                // description available, show it
                tmpStringM = new String(message, 22, message[21]);
                overlayMessage(tmpStringM);
            } else {
                overlayMessage("Login failed.");
            }

        }
    }


    private void handleMessageChangeEmailResult(byte[] message) {
        tmpStringM = new String(message, 6, message[5]);
        if (message[4] == 1) {
            // store succeeded
            if (optionSubState == OPTIONSUBSTATE_EMAIL_CHANGE_WAIT) {
                optionState = OPTIONSTATE_NONE;                
            } else if (currentSubState == SUBSTATE_EMAIL_CHANGE_WAIT) {
                tmpStringM = null;
                prepareRequestCharacters();
            }
        } else {
            // store failed
            if (optionState == OPTIONSTATE_EMAIL_ENTRY) {
                optionSubState = OPTIONSUBSTATE_NONE;
                setOptionCommand1("Options");
                setOptionCommand2("Cancel");
            } else if (currentSubState == SUBSTATE_EMAIL_CHANGE_WAIT) {
                currentState = STATE_EMAIL_ENTRY;
                currentSubState = SUBSTATE_NORMAL;
                setBottomCommand1("Options");
                setBottomCommand2("Skip");
            }
        }
        // show message if applicable
        if (tmpStringM != null) {
            overlayMessage(tmpStringM);
        }
    }

    
    private void handleMessageGetEmail(byte[] message) {
        String part1 = "";
        String part2 = "";


        int part1Length = message[4];
        if (part1Length > 0) {
            part1 = new String(message, 5, part1Length);
        }

        int part2Length = message[5+part1Length];
        if (part2Length > 0) {
            part2 = new String(message, 6+part1Length, part2Length);
        }
        GTools.textWindowSetText(emailField1, part1);
        GTools.textWindowSetText(emailField2, part2);
        optionSubState = OPTIONSUBSTATE_NONE;
        setOptionCommand1("Options");
        setOptionCommand2("Cancel");
    }


    private void handleMessageAddCharacterToList(byte[] message) {
        Character c = new Character();

        c.fillFromListMessage(message);
        c.ownerId = user_DB_ID;

        //add to the character list
        if (currentState == STATE_CHARACTER_SELECT && currentSubState != SUBSTATE_CHARACTER_NEW) {
            // only add to the list if list is actually visible
            int j = 5*c.graphicsDim;    // clip offset for representative frame
            GImageClip gic = imageManager.getImageClipFormCache(c.graphicsId, j + c.graphicsX, c.graphicsY, c.graphicsDim, c.graphicsDim);
            GTools.listAppendEntry(genericList, c.name, c, gic);
        }
        //System.out.println("got character : " + c.name);
        //c.printDetails();
        ownCharacters.addElement(c);
    }

    private void handleMessageCharacterCreatePermissionResult(byte[] message) {
        if (message[4] == 1) {
            //System.out.println("Add character wil be ok");
            // creation of new character will be ok
            currentState = STATE_CHARACTER_SELECT;
            currentSubState = SUBSTATE_CHARACTER_NEW;
            GTools.inputWindowRemoveText(editBoxInput);
            GTools.menuSetCaptionOneLine(editBox, "Character Name", font, 0);
            //addCharacterOK = false;
            initCharacterClassList();
            //initWindows();
        } else {            
            // creation of new character will NOT be ok
            if (currentState == STATE_WAIT) {
                // we will go back to character selection, list is already ok
                String info = new String(message, 6, message[5]);
                subStateOKDialog(info, STATE_CHARACTER_SELECT, SUBSTATE_NORMAL);
                setBottomCommand1("Options");
                setBottomCommand2("Game");
            }
        }
    }


    private void handleMessageCharacterCreateResult(byte[] message) {
        if (message[4] == 1) {
            // creation of new character was be ok
            overlayMessage("Your new character was created.");
        } else {
            currentSubState = SUBSTATE_NORMAL;
            initCharacterSelectionList();
            String info = new String(message, 6, message[5]);
            subStateOKDialog(info, STATE_CHARACTER_SELECT, SUBSTATE_NORMAL);
        }
        // note: if adding the character is not successful server may decide to silently remove the client
    }


    private void handleMessageAddCharacterClass(byte[] message) {        
        CharacterClass newClass = new CharacterClass();
        newClass.fillFromMessage(message);
        characterClasses.addElement(newClass);
    }

    private void handleMessageCharacterRenameResult(byte[] message) {
        if (message[4] != 1) {
            String info = new String(message, 10, message[9]);
            subStateOKDialog(info, STATE_CHARACTER_SELECT, SUBSTATE_NORMAL);
        } else {
            currentState = STATE_CHARACTER_SELECT;
            currentSubState = SUBSTATE_NORMAL;

            int chId = NetTools.intFrom4Bytes(message[5], message[6], message[7], message[8]);
            int chLen = ownCharacters.size();
            for (int i=0; i<chLen; i++) {
                Character c = (Character)ownCharacters.elementAt(i);
                if (c.objectId == chId) {
                    c.name = new String(GTools.inputWindowGetText(editBoxInput));
                    break;
                }
            }            
        }
        // in any case go back to the character selection screen, and re-add all characters (could also simply updaet the name in the list but this is more work)
        setBottomCommand1("Options");
        initCharacterSelectionList();
    }

    private void handleMessageCharacterDeleteResult(byte[] message) {
        if (message[4] != 1) {
            String info = new String(message, 10, message[9]);
            subStateOKDialog(info, STATE_CHARACTER_SELECT, SUBSTATE_NORMAL);
        } else {
            currentState = STATE_CHARACTER_SELECT;
            currentSubState = SUBSTATE_NORMAL;

            int chId = NetTools.intFrom4Bytes(message[5], message[6], message[7], message[8]);
            int chLen = ownCharacters.size();
            for (int i=0; i<chLen; i++) {
                Character c = (Character)ownCharacters.elementAt(i);
                if (c.objectId == chId) {
                    ownCharacters.removeElementAt(i);
                    break;
                }
            }
            setBottomCommand1("Options");
            initCharacterSelectionList();   // re-populate characters in the list
        }
    }

    private void handleMessagePasswordResetCodeResult(byte[] message) {
        if (currentState == STATE_GET_PASSWORD_RESET_CODE && currentSubState == SUBSTATE_RECOVER_PASSWORD_WAIT) {
            if (message[4] == 1) {
                // success
                tmpStringM = "Success!\nThe reset-code will be sent to the e-mail address which you provided for your account.\n\nPlease check your e-mail in a few minutes.";
                GTools.listSetSelectedIndex(genericList, 1);
            } else {
                // failed
                if (message[5] > 0) {
                    tmpStringM = new String(message, 6, message[5]);
                } else {
                    tmpStringM = "Reset-code could not be requested.";
                }
            }
            overlayMessage(tmpStringM);
            currentState = STATE_RECOVER_PASSWORD_MAIN_OPTIONS;
            currentSubState = SUBSTATE_NORMAL;
            setBottomCommand1("Select");
            setBottomCommand2("Back");
        }
    }


    private void handleMessagePasswordResetNewResult(byte[] message) {
        if (currentState == STATE_ENTER_PASSWORD_RESET_CODE && currentSubState == SUBSTATE_RECOVER_PASSWORD_WAIT) {
            if (message[4] == 1) {
                // success
                currentSubState = SUBSTATE_NORMAL;
                currentState = STATE_INTRO_LIST;
                setListEntriesIntro();
                GTools.listSetSelectedIndex(genericList, 0);
                overlayMessage("Your password was changed");
                setBottomCommand1("Select");
                setBottomCommand2("Game");
            } else {
                // failed
                currentSubState = SUBSTATE_NORMAL;
                setBottomCommand1("Change Password");
                setBottomCommand2("Back");
                if (message[5] > 0) {
                    tmpStringM = new String(message, 6, message[5]);
                } else {
                    tmpStringM = "Password could not be changed.";
                }
                overlayMessage(tmpStringM);
            }
        }
    }

    private void handleMessageInventoryEnd(byte[] message) {
        /*
        requestOpenQuests();
        currentState = STATE_OPEN_QUESTLIST_LOAD_WAIT;
         */
        // reset the packet per loop rate to default
        // was set to a low value to update the screen on each item add packet

        PACKETPERLOOP = DEFAULT_PACKETPERLOOP;
        setWaitLabelText("Loading Friend List ..");

        currentState = STATE_FRIEND_RECEIVE_LIST_WAIT;
        sendRequestFriendListMessage();
    }

    private void handleMessageFriendListEnd(byte[] message) {
        currentState = STATE_WAIT;
        setWaitLabelText("Preparing World ..");
        sendEnterWorldMessage();
    }


    private void handleMessagePlayfieldInfo(byte[] message) {
        playfieldHelptextID = 0;
        if (currentSubState==SUBSTATE_PORTAL_WAIT || currentState==STATE_WAIT || usingServerPortal) {

            //should be in STATE_WAIT now!
            // remove all objects ...
            idToCharacters.clear();
            idToItems.clear();
            // ... and add yourself
            idToCharacters.put("" + character_DB_ID, playerObject);

            int newPlayfieldID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);

            if (false && newPlayfieldID == playfieldID && !usingServerPortal) {
                //move locally on same legacyPlayfield
                //System.out.println("local: " + playfieldName + " (" + playfieldID + ") " + message[8] + " " + message[9]);
                adjustScreen(NetTools.intFrom2Bytes(message[13], message[14]), NetTools.intFrom2Bytes(message[15], message[16]));
                playerMove = false;
                sendPos = false;
                //sendMoveObjectMessage();                
                // todo:check as this might need adjustment
                sendAddMe();
                //currentSubState = SUBSTATE_NORMAL;
            } else {
                usingServerPortal = false;
                playfieldID = newPlayfieldID;
                // clear firewall info
                // todo: rework firewall to be part of the playfield / playfieldcell classes
                for (n=0; n < FIREWALL_WINDOWSIZE; n++) {System.arraycopy(emptyFireWallElement, 0, fireWalls[n], 0, FIREWALL_WINDOWSIZE);}
                // clear special fields
                specialFields = null;
                //move to other legacyPlayfield

                int width = NetTools.intFrom2Bytes(message[8],message[9]);
                int height = NetTools.intFrom2Bytes(message[10],message[11]);
                //int x = NetTools.intFrom2Bytes(message[8],message[9]);
                //int y = NetTools.intFrom2Bytes(message[10],message[11]);

                if (message[12] > 0) {  //get playfieldname if available
                    playfieldName = new String(message, 13, message[12]);
                }
                
                if(/*host.equals(playfieldServer) ||*/ clusterDisable) {
                    //System.out.println("joining: " + playfieldName + " (" + playfieldID + ") " + message[8] + " " + message[9]);
                    playerMove = false;
                    sendPos = false;

                    if (playfieldName!=null && !playfieldName.equals("")) {
                        loadPlayfield = true;
                        setWaitLabelText("Travelling to\nworld..");                        
                        prepareLoadingWorldScreen();
                        requestHighscores(1, 5);
                        currentState = STATE_WAIT;
                        currentSubState = SUBSTATE_NORMAL;
                    } else {
                        subStateOKDialog("Cannot load playfield.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                    }
                    //SET NEW PLAYFIELD DETAILS, CREATE NEW BYTE ARRAY IF NECCESSARY!
                    setNewEmptyPlayfield(playfieldName, width, height);
                    GTools.labelSetText(label3, "Loading graphics ..", false);
                    GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
/*#Series40_MIDP2_0#*///<editor-fold>
//#                                 label3.y = gaugeWindow1.y - font.charHeight - 1;
/*$Series40_MIDP2_0$*///</editor-fold>
/*#!Series40_MIDP2_0#*///<editor-fold>
                    label3.y = gaugeWindow1.y - 8;
/*$!Series40_MIDP2_0$*///</editor-fold>
                    //make sure look-at position fits
                    // --adjustScreen(NetTools.intFrom2Bytes(message[11], message[12]), NetTools.intFrom2Bytes(message[13], message[14]));
                    // stopPlay current sound
                    /*
                    if (sound!=null && soundON) {
                        sound.stopPlay();
                    }*/
                } else {
                    // todo: rework server clustering
                    //
                    // We have to switch to another server
                    //
                    usingServerPortal = true;
                    doLogout();
                    stopNet();
                    host = playfieldServer;
                    initNet();
                    currentState = STATE_WAIT_FOR_CONNECT_THREAD_PORTAL;
                }
            }   //end move to other legacyPlayfield

        }
    }


    private void handleMessagePlayfieldGraphicsInfo(byte[] message) {
        if (message[4] == 0) {
            // background graphics
            int numGfx = (int)(message[5]/4);   // each id is 4 bytes long
            int curIndex = 6;
            for (int i=0; i<numGfx; i++) {
                int graphicId = NetTools.intFrom4Bytes(message[curIndex], message[curIndex+1], message[curIndex+2], message[curIndex+3]);
                // try to get the image
                Image image = imageManager.getImageFromCache(graphicId);
                if (image==null) {
                    if (imageManager.loadImageToCache(graphicId, false, true, true, true)) {
                        image = imageManager.getImageFromCache(graphicId);  // image was successfully loaded right away, so assign it
                    }
                }
                // image may be null, which we handle at a later stage when all images have been loaded from the net
                // the imageManager is set to load any missing images over the net
                // see playfield.nextUnloadedTileset() and .Tileset.load(..)
                playfield.addTileset(i, graphicId, image);

                curIndex+=4;
            }
            numImagesToLoad = numGfx;
        } else if (message[4] == 1) {
            // character graphic
            // todo add loop for loading as above
            numImagesToLoad += (int)(message[5]/4);
            if (numImagesToLoad == 0) {
                GTools.labelSetText(label3, "All graphics loaded", false);
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
            } else {
                GTools.labelSetText(label3, "Loading graphic " + (numImagesToLoad - imageManager.loadingCount() + 1), false);
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
            }
            // only now allow to display graphic info
            //System.out.println("switching to STATE_WAIT_LOAD_GFX");
            currentState = STATE_WAIT_LOAD_GFX;
            skipNormalMessagePump = true; // make sure we go to the next frame without executing further messages - important to allow the image manager to kick in receiving the messages
        }
       

    }

    private void handleMessageHighscoreEntry(byte[] message) {
        int rank = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        int experience = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
        int listIndex = message[12];
        int listLength = message[13];

        String name = new String(message, 15, message[14]);

        int m1, m2;

        for (m1=0; m1<10; m1++) {     // copy name
            if (m1<message[14]) {
                highscoreText[3 + m1] = (char)message[15 + m1];
            } else {
                highscoreText[3 + m1] = ' ';
            }
        }

        // copy rank
        replaceNumber(highscoreText, rank, 0, 0);
        // copy experience
        replaceNumberLeftAlign(highscoreText, experience, 15, 20, false);
        /*
        // copy level
        replaceNumber(highscoreText, message[5], 24, 25);
         */
        // message[4] subclassId
        // add the whole line to the highscore window
        GTools.textWindowAddText(highScoreWindow, highscoreText);
    }

    private void handleMessagePlayfieldLoadChunk(byte[] message) {
        //System.out.println("received pf data chunk");
        if (!playfield.setNextCellBaseValues(message, 14)) {
            subStateOKDialog("Cannot load playfield data.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
        } else if (playfield.getLoadedCellCount() == playfield.getCellCount()) {
            //System.out.println("playfield loaded");
            requestEnterPlayfield();

            /*
            if (soundPossible && soundON && soundPlayer!=null) {
                soundPlayer.stopSound();
                playbackSound(0, -1);
            }
             */

            /* // $-> activate!
            // change sound to normal game sound
            if (soundON && soundPossible) {
                if (isPeaceful(playerObject.x + (PLAYERWIDTH_HALF), playerObject.y + (PLAYERHEIGHT_HALF))) {
                    playbackSound(1, -1);    // peaceful
                } else {
                    playbackSound(0, -1);    // not peaceful
                }
            }
             */

            playfieldCounter = 0;   // reset legacyPlayfield packetcounter
            loadingPlayfield = false;
        }

    }

    private void handleMessagePlayfieldEnterResult(byte[] message) {
        if (message[4] == 1) {
            enterPlayfield(false);
        } else {
            String msg = new String(message, 6, message[5]);
            subStateOKDialog(msg, STATE_FORCED_EXIT, SUBSTATE_NORMAL);
        }

        //System.out.println("end of pf enter result");

        // $-> todo: some of this might be needed before switching to game mode
        // should be reworked though

        /*
            ownName = ((playerObject.name).toLowerCase()).toCharArray();
            allowGameInput = true;  //own player now added, receive game input
            idToCharacters.put("" + playerObject.objectId, playerObject);

            if (requestOpenQuests) {
                requestOpenQuests();
                requestOpenQuests = false;
            }

            subStateNormal();
            if (playfieldHelptextID > 0) {
                // fetch help for legacyPlayfield if available
                sendRequestNextHelpText(playfieldHelptextID);
                playfieldHelptextID = 0;
            }


            //currentSubState = SUBSTATE_NORMAL;

//#if !(Series40_MIDP2_0)
            // set the level of the player in the display
            replaceNumber(playerLevelWindow.text, playerObject.level, 0, 1);
            // set the experience points
            replaceNumber(playerExperienceWindow.text, playerObject.experience, 2, 7);
            // set the gold points
            replaceNumber(playerGoldWindow.text, playerObject.gold, 2, 7);
//#else
//#                             // set the gold points
//#                             replaceNumber(playerGoldWindow.text, playerObject.gold, 0, 5);
//#endif
            // set the limit for the next level
            experiencePlusForNextLevel = (((playerObject.level + 1) * (playerObject.level + 2) * 100)) - (((playerObject.level) * (playerObject.level + 1) * 100));
            experienceCurOffset = playerObject.experience - (((playerObject.level) * (playerObject.level + 1) * 100));
        */

            
    }


    private void handleMessageCharacterMoveInfo(byte[] message) {
        int x = NetTools.intFrom2Bytes(message[4], message[5]);
        int y = NetTools.intFrom2Bytes(message[6], message[7]);
        int directionVal = message[8];
        int objectId = NetTools.intFrom4Bytes(message[9], message[10], message[11], message[12]);

        //System.out.println("received move: " + objectId + ": " + x + ", " + y);

        Character c = playfield.getCharacter(objectId);
        if (c!=null) {
            c.setDirection(directionVal);
            c.checkAnimate(curGametime);
            c.x = x;
            c.y = y;
        }
    }

    private void handleMessageCharacterAdd(byte[] message) {
        int objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);

        if(objectId == playerObject.objectId) {
            return; // error
        }

        if (currentSubState!=SUBSTATE_PORTAL_WAIT) {
            Character c = new Character();
            c.objectId = objectId;
            c.clanId = NetTools.intFrom2Bytes(message[8], message[9]);
            c.graphicsId = NetTools.intFrom4Bytes(message[10], message[11], message[12], message[13]);
            c.graphicsX = NetTools.intFrom2Bytes(message[14], message[15]);
            c.graphicsY = NetTools.intFrom2Bytes(message[16], message[17]);
            c.graphicsDim = message[18];
            
            c.useImage(imageManager.getImageFromCache(c.graphicsId));

            c.level = message[19];
            c.x = NetTools.intFrom2Bytes(message[20], message[21]);
            c.y = NetTools.intFrom2Bytes(message[22], message[23]);
            c.setDirection(message[24]);
            c.curHealth = NetTools.intFrom2Bytes(message[25], message[26]);
            c.healthBase = NetTools.intFrom2Bytes(message[27], message[28]);
            c.healthEffectsExtra = 0;

            //ADD TO HASH
            c.name = new String(message, 30, message[29]);
            playfield.addCharacter(c);
        }
    }

    private void handleMessageCharacterRemove(byte[] message) {
        int objectId = NetTools.intFrom4Bytes(message[4],message[5],message[6],message[7]);
        removeCharacter(objectId);
    }

    private void handleMessageItemAdd(byte[] message) {
//System.out.println("add item 1");
        Item it = new Item();
        it.objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        it.graphicsId = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
        it.graphicsX = NetTools.intFrom2Bytes(message[12], message[13]);
        it.graphicsY = NetTools.intFrom2Bytes(message[14], message[15]);
        it.usageType = message[16];
        it.x = NetTools.intFrom2Bytes(message[17], message[18]);
        it.y = NetTools.intFrom2Bytes(message[19], message[20]);

        it.useImage(imageManager.getImageFromCache(it.graphicsId));
        playfield.addItem(it);
//System.out.println("end add item");
    }

    private void handleMessageItemRemove(byte[] message) {
        int objectId = NetTools.intFrom4Bytes(message[4],message[5],message[6],message[7]);
        playfield.removeItem(objectId);
    }

    private void handleMessageItemAddToInventory(byte[] message) {
        Item it = new Item();
        it.fillFromListMessage(message);
        it.useImage(imageManager.getImageFromCache(it.graphicsId));

        playerObject.inventory.addItem(it);
        // needs adjustment for stackable items / when items are replaced
        if (currentState==STATE_GAME /* && itemReplace == -1 */) {
            showBottomInfo("Added item: " + it.name, 8000, true);
        } else if (currentState == STATE_INVENTORY_LOAD_WAIT) {
            setWaitLabelText("     Added item:     \n" + it.name);
        }

    }

    private void handleMessageCharacterChatToAll(byte[] message) {
        // read msg
        String chatMsg = new String(message, 9, message[8]);
        // set for character, replacing any existing message
        int objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        Character c = playfield.getCharacter(objectId);
        if (c!=null) {
            c.setPublicChatMessage(chatMsg, MESSAGE_MAXSHOWDURATION);
        }
    }

    private void handleMessageCharacterChat(byte[] message) {
        // read msg
        // set for character, replacing any existing message
        int senderId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        String senderName = new String(message, 13, message[12]);
        int nextIndex = 13+message[12];
        int msgLen = message[nextIndex];
        String chatMsg = new String(message, nextIndex+1, msgLen);
        appendMessageToConversation(senderId, senderId, senderName, chatMsg);
    }

    private void handleMessageCharacterHit(byte[] message) {
        int attackerId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        int targetId = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
        int hitValue = NetTools.intFrom2Bytes(message[12], message[13]);
        int curHealth = NetTools.intFrom2Bytes(message[14], message[15]);

        Character target = playfieldView.getCharacter(targetId);
        if (target != null) {
            target.curHealth = curHealth;
        }

        if (attackerId != playerObject.objectId) {
            playfieldView.onAttackCharacter(attackerId, targetId);
        }
        playfieldView.onHitCharacter(attackerId, targetId);
    }

    private void handleMessageCharacterHitMiss(byte[] message) {
        int attackerId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        int targetId = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);

        if (attackerId != playerObject.objectId) {
            playfieldView.onAttackCharacter(attackerId, targetId);
        }
        playfieldView.onHitMissCharacter(attackerId, targetId);
    }

    private void handleMessageCharacterKilled(byte[] message) {
        int targetId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        System.out.println("received char killed");

        if (targetId == playerObject.objectId) {
            currentState = STATE_BLACK;
            overlayState = OVERLAY_DIED;
            GTools.labelSetText(confirmWindow, "You died", false);
            GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
            setOverlayCommand1("REVIVE");
            ovCommand2 = false;
        } else {
            playfieldView.onCharacterKilled(targetId);
            removeCharacter(targetId);    // make sure the character is removed
            
        }
    }

    private void handleMessageRespawnResult(byte[] message) {
        if (message[4] == 1) {
            playerObject.x = NetTools.intFrom2Bytes(message[5], message[6]);
            playerObject.y = NetTools.intFrom2Bytes(message[7], message[8]);
            playerObject.curHealth = NetTools.intFrom2Bytes(message[9], message[10]);
            playerObject.curMana = NetTools.intFrom2Bytes(message[11], message[12]);
            enterPlayfield(true);
        } else {
            String msg = new String(message, 14, message[13]);
            subStateOKDialog(msg, STATE_FORCED_EXIT, SUBSTATE_NORMAL);
        }
    }

    private void handleMessageVitalityIncrease(byte[] message) {
        playerObject.curHealth = NetTools.intFrom2Bytes(message[4], message[5]);
        playerObject.curMana = NetTools.intFrom2Bytes(message[6], message[7]);
    }

    // $$ hnew handle

    private void enterPlayfield(boolean forRespawn) {
        // success, prepare game mode
        allowGameInput = true;
        currentState = STATE_GAME;
        adjustScreen(playerObject.x, playerObject.y);
        idToCharacters.put("" + playerObject.objectId, playerObject);

        if (!forRespawn) {
            initGraphics();
            initWindows();
        }
        //sendAddMe();

        if (touchesFunction(playerObject, PlayfieldCell.function_peaceful)) {
            peacefulDisplay = 1;
            GTools.textWindowSetText(info1Line2, "Peaceful Area");
            GTools.windowSetColors(info1Line2, 0x000000, 0x000000, 0x006600, 0x006600);
            peacefulDisplayTime = 10000;
        } else {
            peacefulDisplay = 2;
            GTools.textWindowSetText(info1Line2, "Fighting Area");
            GTools.windowSetColors(info1Line2, 0x000000, 0x000000, 0x660000, 0x660000);
            peacefulDisplayTime = 10000;
        }
    }

    private void appendConversationToWindow(Conversation conv, GTextWindow wnd, boolean clear) {
        appendMessageBatchToWindow(conv.getCachedMessages(), wnd, clear);
        appendConversationNewMessagesToWindow(conv, wnd, false);
    }

    private void appendConversationNewMessagesToWindow(Conversation conv, GTextWindow wnd, boolean clear) {
        appendMessageBatchToWindow(conv.getNewMessages(), wnd, clear);
        conv.markNewMessagesAsRead();
    }


    private void appendMessageBatchToWindow(Vector messages, GTextWindow wnd, boolean clear) {
        for (int i=0; i<messages.size(); i++) {
            ConversationFragment frg = (ConversationFragment)messages.elementAt(i);
            GTools.textWindowAddText(wnd, frg.senderName + ":" + frg.message + "\n");
        }
    }

    private void appendMessageToConversation(int channelId, int senderId, String senderName, String msg) {
        ConversationFragment cf = new ConversationFragment(senderId, senderName, msg);
        Conversation conv = getConversation(channelId, senderName);
        conv.addMessage(cf, true, curGametime);
        if (isActiveConversation(conv.getChannelId())) {
            appendConversationNewMessagesToWindow(activeConversation, chatWindow, false);
        }
    }


    private void openChat(int channelId, String channelName) {
        inputChatWindow.maxChars = MAX_CHATCHARS;

        //openConversation(activeConversation);
        currentSubState = SUBSTATE_TALKTO;
        // todo: check if we should use nextsub
        nextChatSubstate = SUBSTATE_TALKTO;

        setBottomCommand1("Options");
        setBottomCommand2("Back");

        activateConversation(channelId, channelName);
    }


    private void closeChat() {
        GTools.textWindowRemoveText(chatWindow);
        GTools.inputWindowRemoveText(inputChatWindow);
        GTools.menuSetSelected(menuChat, 1);
        activateConversation(null);

        /*
        menuChat.height += chatWindow.height+1;
        menuChat.y -= chatWindow.height+1;
        inputChatWindow.maxChars = MAX_CHATCHARS;
        GTools.inputWindowRemoveText(inputChatWindow);
        GTools.menuSetItem(menuChat, chatWindow, 0);
        */

        subStateNormal();
        //actionPartnerName = null;
        // --resetSelectedActionPartner();
    }



    private boolean isActiveConversation(int channelId) {
        return (activeConversation != null && activeConversation.getChannelId() == channelId);
    }

    private void activateConversation(int channelId, String channelName) {
        activateConversation(getConversation(channelId, channelName));
    }

    private void activateConversation(Conversation conv) {
        if (conv==null) {
            activeConversation = null;
            return;
        }
        if (isActiveConversation(conv.getChannelId())) {
            return;
        }
        activeConversation = conv;
        appendConversationToWindow(conv, chatWindow, true);
    }

    private Conversation getConversation(int channelId) {
        return (Conversation)conversations.get(new Integer(channelId));
    }

    private Conversation getConversation(int channelId, String newChannelName) {
        Conversation conv = getConversation(channelId);
        if (conv == null) {
            conv = addNewConversation(channelId, newChannelName);
        }
        return conv;
    }
    
    private Conversation addNewConversation(int channelId, String newChannelName) {
        Conversation conv = new Conversation(channelId, newChannelName, curGametime);
        conversations.put(new Integer(channelId), conv);

        checkRemoveExceedingConversations();

        return conv;
    }

    private void checkRemoveEmptyConversations() {
        Enumeration e = conversations.elements();
        while (e.hasMoreElements()) {
            Conversation cur = (Conversation)e.nextElement();
            if (cur.getNumMessages() == 0) {
                conversations.remove(new Integer(cur.getChannelId()));
            }
        }
    }

    private void checkRemoveExceedingConversations() {
        if (conversations.size() > MAX_CONVERSATIONS) {
            Vector v = getConversationsSorted();
            for (int i=MAX_CONVERSATIONS-1; i<conversations.size(); i++) {
                Conversation otherConv = (Conversation)v.elementAt(i);
                if (otherConv != activeConversation) {
                    conversations.remove(new Integer(otherConv.getChannelId()));
                }
            }
        }

    }

    private Vector getConversationsSorted() {
        // sort entry in depending of num new messages and channel name
        Vector v = new Vector();
        int i=0;
        
        Enumeration e = conversations.elements();
        while (e.hasMoreElements()) {
            Conversation cur = (Conversation)e.nextElement();
            int curNew = cur.getNumMessages();
            long curLastChanged = cur.getTimeLastChanged();
    
            for (i=0; i<v.size(); i++) {
                Conversation other = (Conversation)v.elementAt(i);
                if (other.getNumNewMessages() < curNew || (other.getNumNewMessages() == curNew && other.getTimeLastChanged() > curLastChanged)) {
                    break;
                }
            }
            v.insertElementAt(cur, i);
        }
        return v;

    }


    /**
     * Take appropriate actions for each incoming message
     */
    private boolean executeMessage(byte[] message) {

        if(message != null) {
        
            byte firstByte = message[0];
            int messageId = 0;

            if (firstByte != 0) {
                messageId = NetTools.intFrom3Bytes(message[1], message[2], message[3]);
                trafficCounterReceive+=message[0];
            } else {
                // for an image chunk the first byte must be 0 as an image chunk is always a long message
                messageId = NetTools.intFrom3Bytes(message[3], message[4], message[5]);
                trafficCounterReceive+=NetTools.intFrom2Bytes(message[1], message[2]);
            }

            if (showTraffic) {
                updateTrafficLabel();
            }

            //int messageId = NetTools.intFrom3Bytes(message[1], message[2], message[3]);
/* // aa
 if (messageId > 1059 && messageId < 1063 || messageId > 2199)
    sendDebug("message: " + messageId);
*/
            //System.out.println("Message: " + messageId);

/* ?
if (messageId == 134656) {
    return true;
}*/
            /*
            if (debugLevel >= 2)
                System.out.println("Message " + (char)message[1] + (char)message[2] + (char)message[3]);
                */
            /*if (showDebug2) {
                labelDebug2.text[0] = (char)message[1];
                labelDebug2.text[1] = (char)message[2];
                labelDebug2.text[2] = (char)message[3];
            }*/
            
            //check message timeout
            if (messageTimeout > 0) {
                if (awaitedMessageId==0 || awaitedMessageId == messageId) { // not waiting for message or message received
                    messageTimeout = -1;    //do not wait for the message anymore
                }

            }

            //------------------------

            switch (messageId) {
                case FWGMessageIDs.MSGID_GAME_PONG:
                    handleMessagePong();
                    break;
                case FWGMessageIDs.MSGID_GAME_SERVER_ENTRY:
                    handleMessageServerEntry(message);
                    break;
               case FWGMessageIDs.MSGID_GAME_VERSION:
                    //compare with current version
                    handleMessageVersion(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_REGISTER_RESULT:
                    handleMessageRegisterResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_LOGIN_RESULT:
                    handleMessageLoginResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_FORCED_LOGOUT:
                    handleMessageForcedLogout(message);
                    break;

                case FWGMessageIDs.MSGID_GAME_USER_EMAIL_CHANGE_RESULT:
                    handleMessageChangeEmailResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_CHARACTER_CLASS_FOR_LIST:
                    handleMessageAddCharacterClass(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_CHARACTER_FOR_LIST:
                    handleMessageAddCharacterToList(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_CHARACTER_CREATE_PERMISSION_RESULT:
                    handleMessageCharacterCreatePermissionResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_CHARACTER_CREATE_RESULT:
                    handleMessageCharacterCreateResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_CHARACTER_RENAME_RESULT:
                    handleMessageCharacterRenameResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_CHARACTER_DELETE_RESULT:
                    handleMessageCharacterDeleteResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_PASSWORD_RESET_CODE_RESULT:
                    handleMessagePasswordResetCodeResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_GET_EMAIL_RESULT:
                    handleMessageGetEmail(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_USER_PASSWORD_RESET_NEW_RESULT:
                    handleMessagePasswordResetNewResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_ITEM_INVENTORY_END:
                    handleMessageInventoryEnd(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_FRIEND_LIST_END:
                    handleMessageFriendListEnd(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_PLAYFIELD_INFO:
                    handleMessagePlayfieldInfo(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_PLAYFIELD_GRAPHICS_INFO:
                    handleMessagePlayfieldGraphicsInfo(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_HIGHSCORE_LIST_ENTRY:
                    handleMessageHighscoreEntry(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_PLAYFIELD_LOAD_CHUNK:
                    handleMessagePlayfieldLoadChunk(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_PLAYFIELD_ENTER_RESULT:
                    handleMessagePlayfieldEnterResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_MOVE_INFO:
                    handleMessageCharacterMoveInfo(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_ADD:
                    handleMessageCharacterAdd(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_REMOVE:
                    handleMessageCharacterRemove(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_ITEM_ADD:
                    handleMessageItemAdd(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_ITEM_REMOVE:
                    handleMessageItemRemove(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_ITEM_INVENTORY_ADD:
                    handleMessageItemAddToInventory(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_CHAT_ALL_INFO:
                    handleMessageCharacterChatToAll(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_CHAT_INFO:
                    handleMessageCharacterChat(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_HIT_INFO:
                    handleMessageCharacterHit(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_HIT_MISS_INFO:
                    handleMessageCharacterHitMiss(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_KILLED:
                    handleMessageCharacterKilled(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_RESPAWN_RESULT:
                    handleMessageRespawnResult(message);
                    break;
                case FWGMessageIDs.MSGID_GAME_CHARACTER_INCREASE_VITALITY:
                    handleMessageVitalityIncrease(message);
                    break;
                // $$ msgCase
            }
        }
        return false;        
    }

    private void changeEmail() {
        GTools.textWindowRemoveText(emailField1);
        GTools.textWindowRemoveText(emailField2);
        setBottomCommand1("Options");
        setBottomCommand2("Skip");
        currentState = STATE_EMAIL_ENTRY;
    }
    
    private void prepareRequestCharacters() {
        currentState = STATE_CHARACTER_SELECT;
        currentSubState = SUBSTATE_NORMAL;
        setBottomCommand1("Options");
        setBottomCommand2("Game");
        prepareContextMenu(0);
        //addCharacterOK = true;
        initGraphics();
        initCharacterSelectionList();
        //initWindows();

        if (justRegistered) {
            justRegistered = false;
            currentSubState = SUBSTATE_CHARACTER_NEW;
            initCharacterClassList();
        } else {
            sendRequestCharactersMessage();
        }
    }

    private void forcedExit(String msg) {
        character_DB_ID = 0;
        user_DB_ID = 0;
        currentState = STATE_BLACK;
        subStateOKDialog(msg, STATE_FORCED_EXIT, SUBSTATE_NORMAL);
        stopNet();
        checkNet = false;
        doConnect = 0;
        netError = false;
    }

    private void proceedToLoginAfterRegister() {
        loginFW(clientName, clientPass);
        setWaitLabelText("Account registered.\nLogging in..");
        currentState = STATE_WAIT;
        bCommand1 = false;
        setBottomCommand2("Game");
        justRegistered = true;
    }

    private void setListEntriesIntro() {
        GTools.listRemoveAllEntries(genericList);
        GTools.listAppendEntry(genericList, "Login", null);
        GTools.listAppendEntry(genericList, "Register", null);
        GTools.listAppendEntry(genericList, "Forgot Password?", null);

        GTools.menuEnsureContainAll(menuList, true, true);
        GTools.windowCenterXY(menuList, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
        GTools.menuSetCaptionOneLine(menuList, "Select an option", font, 0);
    }

    private void setListEntriesRecoverPassword() {
        GTools.listRemoveAllEntries(genericList);
        GTools.listAppendEntry(genericList, "Get reset-code", null);
        GTools.listAppendEntry(genericList, "Enter reset-code", null);

        GTools.menuEnsureContainAll(menuList, true, true);
        GTools.windowCenterXY(menuList, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
        GTools.menuSetCaptionOneLine(menuList, "Select an option", font, 0);
    }


    private void prepareListIntro() {
        //GTools.listSetDimensions(GList wnd, int width, int height, int entryGapY, int xSpace)
        GTools.listSetDimensions(genericList, genericList.width, genericList.height - 48, genericList.entryGapY, genericList.xSpace);
        GTools.windowSetColors(genericList, 0xCC9900, 0xFFCC00, 0x000000, 0xCC6600);
        setListEntriesIntro();
    }
    
    
    /**
     * Fill an item object using the f_ai or f_bl message 
     * (only slight difference -> equipped + explicit reward not sent in f_bl message).
     * @param itemTmpM The item to fill
     * @param message The message containing the item details
     */
    private void fillItemFromMessage(Item itemTmpM, byte[] message) {
        itemTmpM.objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
        itemTmpM.classId = message[8];
        itemTmpM.subclassId = message[9];
        itemTmpM.triggertype = NetTools.intFrom4Bytes(message[10], message[11], message[12], message[13]);
        itemTmpM.graphicsX = message[14];
        itemTmpM.graphicsY = message[15];

        itemTmpM.healthBase = NetTools.intFrom2Bytes(message[18], message[19]);
        itemTmpM.healthregenerateBase = message[20];

        itemTmpM.manaBase = NetTools.intFrom2Bytes(message[23], message[24]);
        itemTmpM.manaregenerateBase = message[25];

        itemTmpM.attackBase = NetTools.intFrom2Bytes(message[26], message[27]);
        itemTmpM.defenseBase = NetTools.intFrom2Bytes(message[28], message[29]);
        itemTmpM.skillBase = NetTools.intFrom2Bytes(message[30], message[31]);
        itemTmpM.magicBase = NetTools.intFrom2Bytes(message[32], message[33]);
        itemTmpM.damageBase = NetTools.intFrom2Bytes(message[34], message[35]);

        itemTmpM.gold = NetTools.intFrom4Bytes(message[36], message[37], message[38], message[39]);

        itemTmpM.units = NetTools.intFrom2Bytes(message[40], message[41]);
        itemTmpM.unitsSell = NetTools.intFrom2Bytes(message[42], message[43]);
        itemTmpM.frequency = message[44];
        itemTmpM.requiredSkill = NetTools.intFrom2Bytes(message[45], message[46]);
        itemTmpM.requiredMagic = NetTools.intFrom2Bytes(message[47], message[48]);

        itemTmpM.data = message[49];

        if (message[2]=='a' && message[3]=='i') {
            itemTmpM.graphicsel = message[52];
            itemTmpM.name = new String(message, 55, message[53]);
            itemTmpM.description = new String(message, 55 + itemTmpM.name.length(), message[54]);
        } else if (message[2]=='b' && message[3]=='l') {
            itemTmpM.graphicsel = message[50];
            itemTmpM.name = new String(message, 53, message[51]);
            itemTmpM.description = new String(message, 53 + itemTmpM.name.length(), message[52]);
        } else {
            itemTmpM.name = "UNKNOWN";
            itemTmpM.description = "-";
        }
        
    }    
    
    

    /**
     * Clear all of the equipment of the player (sets all equipped items to be unequipped).
     * @param synchronizeServer True if the server should be notified
     */
    /*
    private void clearEquipment(boolean synchronizeServer) {
        for (int m1=equipment.length; --m1>=0; ) {
            if (equipment[m1]!=null) {
                equipment[m1].equipped = 0; // item should know it is not equipped anymore
                equipment[m1] = null;
            }
        }
        if (playerObject!=null) {
            healthBase = playerObject.healthBase;
            manaBase = playerObject.manaBase;
            attackBase = playerObject.attackBase;
            defenseBase = playerObject.defenseBase;
            skillBase =  playerObject.skillBase;
            magicBase = playerObject.magicBase;
            damageBase = playerObject.damageBase;
            healthregenerateBase = playerObject.healthregenerateBase;
            manaregenerateBase = playerObject.manaregenerateBase;
        }
        // assert correct curHealth/curMana values
        if (playerObject.curHealth > healthBase) {playerObject.curHealth = healthBase;}
        if (playerObject.curMana > manaBase) {playerObject.curMana = manaBase;}
        
        // System.out.println("after equip: " + healthBase + " playerobject: " + playerObject.healthBase);
        
        if (synchronizeServer) {
            sendClearEquipment();
        }
    }
    */
    

        // %%%
        private boolean addToBelt(Item it, int slotIndex, boolean synchronizeServer) {
            if (slotIndex < 0 || slotIndex >= MAX_BELT_ITEMS) return false;
            // we may currently be using the belt
            /*
            if (currentSubState == SUBSTATE_BELT) {
                itemTmpD = belt[selectedBeltItem];
            }*/

            // make sure an existing item at this slot is removed from the belt
            itemTmpM1 = belt[slotIndex];
            if (itemTmpM1 != null) {
                itemTmpM1.equipped = 0;
                itemTmpM1 = null;
            }

            belt[slotIndex] = it;
            it.equipped = slotIndex + 2;
            if (synchronizeServer) {
                sendAddBeltMessage(it.objectId, slotIndex);
            }
            return true;
        }



        private boolean removeFromBelt(Item it, boolean synchronizeServer) {
            int slotIndex = it.equipped - 2;
            if (slotIndex < 0 || slotIndex >= MAX_BELT_ITEMS) return false;
            // we may currently be using the belt
            /*
            if (currentSubState == SUBSTATE_BELT) {
                itemTmpD = belt[selectedBeltItem];
            }*/
            

            belt[slotIndex] = null;
            it.equipped = 0;
            if (synchronizeServer) {
                sendRemoveBeltMessage(it.objectId);
            }
            return true;
        }
    
    
    
    /**
     * Equip a given item that is currently unequipped, adjust all attributes 
     * according to the bonus values the item has.
     * @param item The item to equip
     * @param synchronizeServer True if the server should be notified
     */
    /*
    private boolean equip(Item item, boolean synchronizeServer, boolean checkRemoveUnsupported) {
        if (item==null || item.equipped > 0) {
            return false;
        }

        int maxHealthBefore;
        int maxHealthAfter;
        int maxManaBefore;
        int maxManaAfter;
        
        int type = -1;
        if (item.classId==2 || item.classId==3) {
            type = 0;   // longrange weapon is a weapon too
        } else {
            type = item.classId - 3;
        }

        if (type >= 0 && type < equipment.length) {
            

            if (checkRemoveUnsupported) {
                if (!doEquipSupportedCheck(item, type)) {    // check if item can be equipped and keep attribute balance (considering skillBase and magicBase)
                    
                    return false;
                }
            }
            
            // first, unequip the current item, if there is one equipped
            if (equipment[type]!=null) {
                maxhealth -= equipment[type].healthBase;
                maxmana -= equipment[type].manaBase;
                attack -= equipment[type].attackBase;
                defense -= equipment[type].defenseBase;
                skill -= equipment[type].skillBase;
                magic -= equipment[type].magicBase;
                damage -= equipment[type].damageBase;
                healthregenerate -= equipment[type].healthregenerateBase;
                manaregenerate -= equipment[type].manaregenerateBase;
                // set flag
                equipment[type].equipped = 0;    
                
                if (checkRemoveUnsupported) {
                    checkRemoveUnsupportedEquipment();
                }
            }

            maxHealthBefore = maxhealth;
            maxHealthAfter = maxHealthBefore;

            maxManaBefore = maxmana;
            maxManaAfter = maxManaBefore;
            
            // equip the new item
            equipment[type] = item;
            // set the new item bonus values
            /*
            if (equipment[type].healthBase > 0) {
                System.out.println(" + " + equipment[type].healthBase + ": " + healthBase + " => " + (healthBase + equipment[type].healthBase));
                System.out.println("current curHealth: " + playerObject.curHealth);
            }*/
            /*
            maxhealth += equipment[type].healthBase;
            
            
            
            maxmana += equipment[type].manaBase;
            attack += equipment[type].attackBase;
            defense += equipment[type].defenseBase;
            skill += equipment[type].skillBase;
            magic += equipment[type].magicBase;
            damage += equipment[type].damageBase;
            healthregenerate += equipment[type].healthregenerateBase;
            manaregenerate += equipment[type].manaregenerateBase;
            // set flag
            equipment[type].equipped = 1;

            if (checkRemoveUnsupported) {
                checkRemoveUnsupportedEquipment();
            }

            maxHealthAfter = maxhealth;
            maxManaAfter = maxmana;
            
            
            if (!checkRemoveUnsupported) {
                // when equipping at the startPlay of the game, gracefully compensate for
                // max curHealth / curMana value increases, same procedure is done on server
                if (maxHealthAfter > maxHealthBefore) {
                    //System.out.println("curHealth inc by: " + (maxHealthAfter - maxHealthBefore) + " before: " + playerObject.curHealth);
                    playerObject.curHealth += maxHealthAfter - maxHealthBefore;
                    //System.out.println("after inc: " + playerObject.curHealth);
                }
                if (maxManaAfter > maxManaBefore) {
                    //System.out.println("curMana inc by: " + (maxManaAfter - maxManaBefore) + " before: " + playerObject.curMana);
                    playerObject.curMana += maxManaAfter - maxManaBefore;
                    //System.out.println("after inc: " + playerObject.curMana);
                }
            }

            // assert correct curHealth/curMana values
            if (playerObject.curHealth > maxhealth) {playerObject.curHealth = maxhealth;}
            if (playerObject.curMana > maxmana) {playerObject.curMana = maxmana;}
//System.out.println("error checked curHealth: " + playerObject.curHealth);
//System.out.println("error checked curMana: " + playerObject.curMana);
            

        } else {
            return false;
        }

        
        if (synchronizeServer) {
            sendEquipAdd(item.objectId);
        }
        return true;
    }
    */
    
    /**
     * Unequip a given item that is currently equipped, adjust all attributes 
     * according to the bonus values the item has.
     * @param item The item to unequip
     * @param synchronizeServer True if the server should be notified
     * @param checkRemoveUnsupported True if any equip change might decrease the 
     * total skillBase/magicBase values in which case equipped items with higher
     * required skillBase/magicBase should be unequipped - false otherwise.
     * Set checkRemoveUnsupported to true for all manual (i.e. caused by the 
     * user) equip changes.
     */
    private boolean unequip(Item item, boolean synchronizeServer, boolean checkRemoveUnsupported) {
        boolean skillMagicChange = false;
        
        if (item==null || !(item.equipped==1)) {
            return false;
        }

        int type = -1;
        if (item.classId==2 || item.classId==3) {
            type = 0;   // longrange weapon is a weapon too
        } else {
            type = item.classId - 3;
        }
        
        if (type >= 0 && type < equipment.length) {
            if (equipment[type]!=null) {
                maxhealth -= equipment[type].healthBase;
                maxmana -= equipment[type].manaBase;
                attack -= equipment[type].attackBase;
                defense -= equipment[type].defenseBase;
                skill -= equipment[type].skillBase;
                magic -= equipment[type].magicBase;
                damage -= equipment[type].damageBase;
                healthregenerate -= equipment[type].healthregenerateBase;
                manaregenerate -= equipment[type].manaregenerateBase;
                // set flag
                equipment[type].equipped = 0;
                
                // see if skillBase/magicBase will change
                if (equipment[type].skillBase > 0 || equipment[type].magicBase > 0) {
                    skillMagicChange = true;
                }
                
                // set null
                equipment[type] = null;
                // assert correct curHealth/curMana values
                if (playerObject.curHealth > maxhealth) {playerObject.curHealth = maxhealth;}
                if (playerObject.curMana > maxmana) {playerObject.curMana = maxmana;}
                
                if (skillMagicChange) { // unequip any item that has too high required skillBase/magicBase
                    checkRemoveUnsupportedEquipment();
                }
            }
            
        } else {
            return false;
        }
        
        if (currentSubState==SUBSTATE_BUILDCHARACTER) {
            prepareCharacterBuildScreen();
        }

        if (synchronizeServer) {
            sendEquipRemove(item.objectId);
        }
        return true;
        
    }
    
    
    /**
     * Unequip any equipped item that cannot be equipped due to its 
     * requiredskill / required magicBase.
     */
    private void checkRemoveUnsupportedEquipment() {
        Item tempItem;
        for (int i=equipment.length; --i>=0; ) {
            tempItem = equipment[i];
            if (tempItem!=null && tempItem.equipped == 1 && (tempItem.requiredSkill * 10 > skill || tempItem.requiredMagic * 10 > magic)) {
                unequip(tempItem, false, true); // might have a chain effect :-D (recursion in unequip)
            }
        }
    }
    
    
    /**
     * Check if the given item can be equipped (check on required skillBase / required magicBase)
     * A simple comparison will NOT suffice here.
     * @param item The item that should be equipped
     * @param type The equipment type of the item used to index into the equipment array
     * @return True if item can be equipped
     */
    private boolean doEquipSupportedCheck(Item item, int type) {
        // save original skillBase / magicBase values
        int tempSkill = skill;
        int tempMagic = magic;

        Item[] tempEquipment = null;
        tempEquipment = new Item[equipment.length];
        System.arraycopy(equipment, 0, tempEquipment, 0, equipment.length);

        if (tempEquipment[type]!=null) {    // an item of this type is already equipped
            tempSkill -= tempEquipment[type].skillBase;
            tempMagic -= tempEquipment[type].magicBase;
        }
        tempEquipment[type] = item;

        tempSkill += item.skillBase;
        tempMagic += item.magicBase;

        // check if equipping the item will have a negative impact on skillBase or magicBase
        if (tempSkill < skill || tempMagic < magic) {
            boolean noChange;
            do {
                noChange = true;
                for (int i=0; i<tempEquipment.length; i++) {
                    if (tempEquipment[i]!=null && (tempEquipment[i].requiredSkill * 10 > tempSkill || tempEquipment[type].requiredMagic * 10 > tempMagic)) {
                        if (i==type) {  // the item that should be equipped will not be ok to add (required skillBase / magicBase)
                            return false;
                        } else {
                            // remove item from tempItems, adjust values
                            tempSkill -= tempEquipment[i].skillBase;
                            tempMagic -= tempEquipment[i].magicBase;
                            tempEquipment[i]=null;
                            noChange = false;
                        }
                    }
                }
            } while (noChange == false);
        }
        return true;
    }
    
    
    
    private WorldObject getObject(long objectid) {
        WorldObject fwgo = null;
        
        //try to get object as a character
        fwgo = (WorldObject)idToCharacters.get("" + objectid);
        
        if (fwgo==null) {   //try to search for it in the items hash
            fwgo = (WorldObject)idToItems.get("" + objectid);
        }
        return fwgo;
    }
    
    
    private boolean isCharacter(WorldObject fwgo) {
        /*if (fwgo.classId < 2) {
            return true;
        } else {
            return false;
        }*/
        return (fwgo.classId < 2);
    }
    
    public void shutDown() {
        //stopNet();
        //if (true)return ;


        if (soundPossible) {            
            // save sound settings
            try {
                database.setValue("soundvolume", "" + curSoundVolume);
                if (soundON) {
                    database.setValue("sound", "ON");
                } else {
                    database.setValue("sound", "OFF");
                }
            } catch (Exception e) {}
        }

        // save short chat messages
        if (character_DB_ID > 0) {
            for (int i = 0; i < chatShortcuts.length; i++) {
                try {                    
                    // write chat shortcuts to RS
                    database.setValue("csc" + character_DB_ID + "_" + i, chatShortcuts[i]);
                } catch (Exception e) {}
            }
        }
        
        
        if (netStarted) {
            doLogout();
        }
        stopNet();
        
        if (soundPlayer!=null) {
            try {
                soundPlayer.stopSound();
            } catch (Exception e) {}
        }

        try {
            database.disconnect();
            database = null;
            if(databaseGfxBack != null) {
                databaseGfxBack.disconnect();
                databaseGfxBack = null;
            }
            if(databaseGfxEnemy != null) {
                databaseGfxEnemy.disconnect();
                databaseGfxEnemy = null;
            }
            tempDatabase = null;
        } catch (Exception e) {
        }
    }
    
   

    /**
     * Cancel a sale offer for a given item, setting gold and unitsSell to 0.
     * @param index The index of the item in the inventory
     * @param synchronizeServer True if the server should be informed about cancelling the sale offer
     */
    private void cancelSale(boolean synchronizeServer) {
        Item it = playerObject.inventory.getSelectedItem();
        if (it!=null) {
            it.unitsSell = 0;
            it.price = 0;
            if (synchronizeServer) {
                sendCancelSale(it.objectId);
            }
        }
        /*
        if (index >= 0 && index < invItems.length && invItems[index]!=null) {
            invItems[index].unitsSell = 0;
            invItems[index].gold = 0;
            if (synchronizeServer) {
                sendCancelSale(invItems[index].objectId);
            }
        }*/
    }    

    
    private void setNewEmptyPlayfield(String name, int fieldWidth, int fieldHeight) {

        /*
        if (playfieldType != type || dynamicType != dynamicT || background==null) {
            playfieldType = type;
            dynamicType = dynamicT;

            backgroundImages_ToLoad[0] = -1;
            backgroundImages_ToLoad[1] = -1;
            currentBackgroundImage_ToLoad = 0;
            
            try {
            
                //always load from net
                //System.out.println("prev type: " + prevType + "   prev dyn type: " + prevDynamicT);
                //System.out.println("type     : " + type     + "   dyn type     : " + dynamicT);
         */
                /*
                if(type == prevType) {
                    //do nothing
                } else if(type == prevDynamicT) {   // use previous dynamic type as new static type
                    tempBack = dynamic; // temp save image
                    dynamic = background;   // switch
                    background = tempBack;
                    tempBack = null;
                    prevDynamicT = prevType;    // also change prev dynamic type to avoid loading again
                } else {
                    if(dynamicT == prevType) {
                        tempBack = dynamic;
                        dynamic = background;
                        background = tempBack;
                        tempBack = null;
                        prevDynamicT = prevType;
                    }
                    background = null;
                    //background = getImageByHTTP("back" + type + ".png", true);
                    backgroundImages_ToLoad[0] = type;
                    System.gc();
                }
                prevType = type;
                
                if(dynamicT >= 0) {
                    if(dynamicT == prevDynamicT) {
                        //do nothing
                    } else if(dynamicT == prevType) {
                        dynamic = background;
                    } else {
                        dynamic = null;
                        //dynamic = getImageByHTTP("back" + dynamicT + ".png", true);
                        backgroundImages_ToLoad[1] = dynamicT;
                        System.gc();
                    }
                    prevDynamicT = dynamicT;
                }
                */

                //System.out.println("x type   : " + prevType + "   x dyn type   : " + prevDynamicT);
                /*
                // check if we have to load from the net
                if(type < 4)
                    background = Image.createImage("/back" + type + ".png");
                else 
                    background = getImageByHTTP("back" + type + ".png");
                    
                if(dynamicT >= 0) {
                    if(dynamicT < 4)
                        dynamic = Image.createImage("/back" + dynamicT + ".png");
                    else
                        dynamic = getImageByHTTP("back" + dynamicT + ".png");
                }
                */
        /*
            } catch (Exception e) {
                if (debugLevel >= 0)
                    System.out.println(e);
            }
        }
         */
        playfield = null;
        System.gc();
        //System.out.println("width: " + fieldWidth + " height: " + fieldHeight);
        
        playfield = new Playfield(name, fieldWidth, fieldHeight);
        playfield.addCharacter(playerObject);
        playfieldView = new PlayfieldActorView(playfield, playerObject, 0, TOP_INFOHEIGHT, DISPLAYWIDTH, DISPLAYHEIGHT);
        //playfield.setScreen(0, TOP_INFOHEIGHT, DISPLAYWIDTH, DISPLAYHEIGHT);
        //playfield.setLookAtPos(0, 0);

        System.gc();

        playfieldWidth = fieldWidth;
        playfieldHeight = fieldHeight;

//--this.legacyPlayfield = null;

        //System.out.println("setting new legacyPlayfield: " + fieldWidth + ", " + fieldHeight);
//--this.legacyPlayfield = new byte[fieldWidth][fieldHeight];
    }
    
    
    
    
    //
    // SEND CLIENT MESSAGES
    //
    
    ////////////////////////////////////
        // Load images with own protocol
    ////////////////////////////////////
    
    /**
     * ToDo:
     */
    private void sendGetImage(String filename) {
        buffer[0] = (byte)(5 + filename.length());
        buffer[1] = (byte) 'a';
        buffer[2] = (byte) 'i';
        buffer[3] = (byte) 's';
        buffer[4] = (byte)filename.length();
        System.arraycopy(filename.getBytes(), 0, buffer, 5, filename.length());
        doSend(buffer);
    }
    
    ////////////////////////////////////
    // Login / Logout public methods
    ////////////////////////////////////
    
    /**
     * Try to join Fabtasy Worlds group.
     */
    public void loginSystem(String user, String pass) {
        //store local login data for FW
        clientName = user;
        clientPass = pass;
        //global / system login
        sendRequestLoginSystem("UNKNOWN", "UNKNOWN");
        //sendRequestLoginSystem(user, pass);
    }
    
    
    /**
     * Do local login: to FW. Called by main FantasyWorlds object, after
     * global login has been granted.
     */
    public void loginFW(String user, String password) {
        sendRequestLogin_FW_Message(user, password);
    }


    /**
     * Do Logout. Called by main Fantasy worlds object.
     */
    public void doLogout() {
        if (netStarted) {
            sendLogoutMessage();

            if(!usingServerPortal) {
                character_DB_ID = 0;
                if (playerObject!=null) {
                    playerObject.objectId = 0;
                }
            }

            
            lastJoinedGroup = null;
        }
    }
    
    private void sendChallengeNumber(byte[] number) {
        buffer[0] = (byte)(12);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_CHALLENGENUMBER, buffer, 1);
        System.arraycopy(number, 0, buffer, 4, number.length);
        doSend(buffer);
    }
    
    private void sendRequestSubscription(char[] phoneNumber, String userName, String userPass) {
        
        /*
        System.out.println("pn: " + phoneNumber);
        System.out.println("clientName: " + userName);
        System.out.println("clientPass: " + userPass);
        */
        int pnLength = phoneNumber.length;
        int unameLength = userName.length();
        int upassLength = userPass.length();
        
        buffer[0] = (byte)(8 + pnLength + unameLength + upassLength);

        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'n';
        buffer[3] = (byte) 's';
        buffer[4] = billingInfo;
        buffer[5] =(byte)pnLength;
        buffer[6] =(byte)unameLength;
        buffer[7] =(byte)upassLength;
        
        // store number
        for (int i=0; i<pnLength; i++) {
            buffer[8 + i] = (byte)phoneNumber[i];
        }
        System.arraycopy(userName.getBytes(), 0, buffer, 8 + pnLength, unameLength);
        System.arraycopy(userPass.getBytes(), 0, buffer, 8 + pnLength + unameLength, upassLength);
        doSend(buffer);
    }
    
    private void sendSubscriptionExit() {
        buffer[0] = (byte)4;
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'e';
        buffer[3] = (byte) 's';
        doSend(buffer);
    }
    
    
    /**
     * Request to add an object, usually because it was not found in throws hash.
     */
    private void sendMessageGetObject(int objectid) {
        buffer[0] = (byte)(8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'g';
        buffer[3] = (byte) 'o';
        NetTools.intTo4Bytes(objectid, buffer, 4);
        doSend(buffer);
    }
    
    
    
    /**
     * Do local login to FW.
     */
    private void sendRequestLogin_FW_Message(String user, String password) {
        buffer[0] = (byte)(6 + user.length() + password.length());
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_LOGIN_REQUEST, buffer, 1);
        buffer[4] = (byte)user.length();

        byte[] temp = user.getBytes();
        
        // crypt(temp);
        // $-> MinServer: implement crypt and use it for register / login messages

        System.arraycopy(temp, 0, buffer, 5, user.length());
        temp = password.getBytes();
        // crypt(temp);
        int nextIndex = 5+user.length();

        buffer[nextIndex] = (byte)password.length();
        System.arraycopy(temp, 0, buffer, nextIndex + 1, password.length());
        doSend(buffer);
    }

    private void sendRecoverPassword(char[] username) {
        buffer[0] = (byte)(5 + username.length);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_PASSWORD_RESET_CODE_REQUEST, buffer, 1);
        buffer[4] = (byte)username.length;

        for (int i=0; i<username.length; i++) {
            buffer[5+i] = (byte)username[i];
        }
        doSend(buffer);
    }

    private void sendRequestPasswordChange(String username, char[] resetCode, char[] newPassword) {
        buffer[0] = (byte)(7 + username.length() + resetCode.length +  newPassword.length);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_PASSWORD_RESET_NEW_REQUEST, buffer, 1);
        
        buffer[4] = (byte)username.length();
        int startPos = 5;
        System.arraycopy(username.getBytes(), 0, buffer, startPos, username.length());

        startPos += username.length();
        buffer[startPos] = (byte)resetCode.length;
        startPos++;
        for (int i=0; i<resetCode.length; i++) {
            buffer[startPos+i] = (byte)resetCode[i];
        }

        startPos += resetCode.length;
        buffer[startPos] = (byte)newPassword.length;
        startPos++;
        for (int i=0; i<newPassword.length; i++) {
            buffer[startPos+i] = (byte)newPassword[i];
        }

        doSend(buffer);

    }

    private void sendChangeEMail(char[] part1, char[] part2) {
        buffer[0] = (byte)(6 + part1.length + part2.length);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_EMAIL_CHANGE_REQUEST, buffer, 1);
        buffer[4] = (byte)part1.length;

        for (int i=0; i<part1.length; i++) {
            buffer[5+i] = (byte)part1[i];
        }
        int start = part1.length;

        buffer[5+start] = (byte)part2.length;
        for (int i=0; i<part2.length; i++) {
            buffer[6+start+i] = (byte)part2[i];
        }
        doSend(buffer);
    }

    private void sendGetEmail() {
        buffer[0] = (byte)(4);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_GET_EMAIL_REQUEST, buffer, 1);
        doSend(buffer);
    }


    private void requestGameServers() {
        buffer[0] = (byte)(4);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_SERVER_LIST_REQUEST, buffer, 1);
        doSend(buffer);
    }
    
    
    private void requestVersion() {
        buffer[0] = (byte)(7);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_VERSION_REQUEST, buffer, 1);
        NetTools.intToUnsignedByte(versionHigh, buffer, 4);
        NetTools.intToUnsignedByte(versionLow, buffer, 5);
        NetTools.intToUnsignedByte(versionLowSub, buffer, 6);
        doSend(buffer);       
    }
    
    /**
     * Request items for players.
     */
    private void sendRequestItemsMessage(int objectID) {
        buffer[0] = (byte)(8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'r';
        buffer[3] = (byte) 'i';
        NetTools.intTo4Bytes(objectID, buffer, 4);
        doSend(buffer);
    }
    
    private void sendRequestFriendListMessage() {
        //System.out.println("SEND FREIND LIST r");
        buffer[0] = (byte)(4);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_FRIEND_LIST_REQUEST, buffer, 1);
        doSend(buffer);
        //System.out.println("END SEND FREIND LIST r");
    }
    
    /** Request the characters of a user. 
     */
    private void sendRequestCharactersMessage() {
        buffer[0] = (byte)(4);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_GET_CHARACTERS_REQUEST, buffer, 1);
        doSend(buffer);
    }

    /** Inform server that client wants to choose a character from the list. */     
    private void sendChooseCharacterMessage(int characterID) {
        buffer[0] = (byte)(8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'c';
        buffer[3] = (byte) 'c';
        NetTools.intTo4Bytes(characterID, buffer, 4);
        doSend(buffer);
    }
    
    private void sendSelectCharacter(int characterID) {
        buffer[0] = (byte)(8);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_CHARACTER_SELECT_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(characterID, buffer, 4);
        doSend(buffer);
    }
    
    private void sendEnterWorldMessage() {
        buffer[0] = (byte)(4);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_PLAYFIELD_ENTER_WORLD_REQUEST, buffer, 1);
        doSend(buffer);
    
    }



    /** Request permission to create a new character. */
    private void sendRequestCreateCharacterPermission() {
        buffer[0] = (byte)(4);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_CHARACTER_CREATE_PERMISSION_REQUEST, buffer, 1);
        doSend(buffer);
    }
    
    /** Create a new character. */     
    private void sendCreateNewCharacter(char[] name, int classId) {
        if (name==null)
            return;
        buffer[0] = (byte)(9 + name.length);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_CHARACTER_CREATE_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(classId, buffer, 4);
        buffer[8] = (byte)name.length;
        //store name
        for (int i=0; i<name.length; i++) {
            buffer[9+i] = (byte)name[i];
        }
        doSend(buffer);
    }    

    /** Rename the character. */     
    private void sendRenameCharacter(int characterID, char[] name) {
        if (name==null)
            return;
        
        buffer[0] = (byte)(9 + name.length);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_CHARACTER_RENAME_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(characterID, buffer, 4);
        buffer[8] = (byte)name.length;
        //store new name
        for (int i=0; i<name.length; i++) {
            buffer[9+i] = (byte)name[i];
        }
        doSend(buffer);
    }    

    /** Rename the character. */     
    private void sendDeleteCharacter(int characterID) {
        buffer[0] = (byte)(8);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_CHARACTER_DELETE_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(characterID, buffer, 4);
        doSend(buffer);
    }    
    
    
    /**
     * Message: Request Login (global/system)
     */
    private void sendRequestLoginSystem(String playerName, String passwd) {
        try {
            buffer[0] = (byte)(6 + playerName.length() + passwd.length());
            buffer[1] = (byte) 's';
            buffer[2] = (byte) 'l';
            buffer[3] = (byte) 'i';
            //length of playername
            buffer[4] = (byte) playerName.length();
            //length of password
            buffer[5 + playerName.length()] = (byte) passwd.length();
            
            //store players name
            System.arraycopy(playerName.getBytes(), 0, buffer, 5, playerName.length());
            //store password
            System.arraycopy(passwd.getBytes(), 0, buffer, 5+(playerName.length()+1), passwd.length());
            doSend(buffer);
            
        } catch(Exception e) {
            //state = ERROR;
            if (debugLevel > 0)
                System.out.println(e);
        }
    }
    
    /**
     * Message: Join Group.
     */
    private void sendJoinGroupMessage(String groupName) {
        try {
            buffer[0] = (byte) (7 + groupName.length());
            buffer[1] = (byte) 'g';
            buffer[2] = (byte) 'j';
            buffer[3] = (byte) 'g';
            buffer[4] = (byte) 0;
            buffer[5] = (byte) 0;
            buffer[6] = (byte) groupName.length();
            System.arraycopy(groupName.getBytes(), 0, buffer, 7, groupName.length());   //store groupName
            doSend(buffer);
        } catch(Exception e) {
            //state = ERROR;
        }
    }
    
    
    /**
     * Message: Leave Group
     */
    private void sendLeaveGroupMessage() {
        try {
            buffer[0] = (byte) (4);
            buffer[1] = (byte) 'g';
            buffer[2] = (byte) 'l';
            buffer[3] = (byte) 'g';
            doSend(buffer);
        } catch(Exception e) {
            //state = ERROR;
        }
    }

    private void sendRegisterPlayerMessage(String user, String password) {
        try {
            buffer[0] = (byte)(6 + user.length() + password.length());
            NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_USER_REGISTER_REQUEST, buffer, 1);
            buffer[4] = (byte)user.length();
            System.arraycopy(user.getBytes(), 0, buffer, 5, user.length());
            int nextIndex = 5 + user.length();
            buffer[nextIndex] = (byte)password.length();
            System.arraycopy(password.getBytes(), 0, buffer, nextIndex+1, password.length());
            doSend(buffer);
        } catch(Exception e) {
            //state = ERROR;
        }
        
    }
    
    
    /**
     * Message: Logout
     */
    private void sendLogoutMessage() {
        try {
            if (sendLogout) {
                buffer[0] = (byte) (8);
                NetTools.intTo4Bytes(user_DB_ID, buffer, 4);
            } else {
                buffer[0] = (byte) (4);
            }
            
            buffer[1] = (byte) 's';
            buffer[2] = (byte) 'l';
            buffer[3] = (byte) 'o';
                
            doSend(buffer);
        } catch(Exception e) {
            //state = ERROR;
        }
    }
    
    /**
     * Message: add me
     */
    private void sendAddMe() {
        buffer[0] = (byte) (8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'a';
        buffer[3] = (byte) 'm';
        NetTools.intTo4Bytes(character_DB_ID, buffer, 4);
        doSend(buffer);
        if (firstTime_SendAddMe) {
            firstTime_SendAddMe = false;
            /*
            if (soundPossible && soundON && soundPlayer!=null) {
                soundPlayer.stopSound();
                playbackSound(0, -1);
            }
             */
        }
    }
    
    /**
     * Message: Request Playfield
     */
    private void sendRequestPlayfieldMessage() {
        buffer[0] = (byte) (4);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_PLAYFIELD_LOAD_REQUEST, buffer, 1);
        doSend(buffer);
    }
    
    private void sendRequestFarPortalJump(int destinationPlayfieldID) {
        buffer[0] = (byte) (9);
        buffer[1] = (byte) 'a';
        buffer[2] = (byte) 'f';
        buffer[3] = (byte) 'p';
        buffer[4] = (byte) 'j';
        NetTools.intTo4Bytes(destinationPlayfieldID, buffer, 5);
        doSend(buffer);
    }
    
    /**
     * Message: Move Game Object
     */
    private void sendMoveObjectMessage() {
        if(playerObject != null && !waitingForTrigger) {

            //System.out.println("sending move message");

            //playerObject.direction = direction;
            //playerObject.x = (short)(xPos + playerScreenX);
            //playerObject.y = (short)(yPos + playerScreenY - TOP_INFOHEIGHT);
            buffer[0] = 9;
            NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_CHARACTER_MOVE, buffer, 1);
            NetTools.intTo2Bytes(playerObject.x, buffer, 4);
            NetTools.intTo2Bytes(playerObject.y, buffer, 6);
            NetTools.intToUnsignedByte(playerObject.getDirection(), buffer, 8);

            packetValidator.processMoveMessage(buffer);
            
            doSend(buffer);
        }
    }

    /**
     * Message: Request item trigger (after use of an item).
     * @param triggerType The type of the trigger
     * @param objectID The item id that activated the trigger
     * @param targetObjectID The valid object id of a target character if available, 
     * 0 otherwise
     */
    private void sendRequestItemTrigger(int triggertype, int objectID, int targetObjectID) {
        buffer[0] = (byte) (17);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'r';
        buffer[3] = (byte) 't';
        buffer[4] = (byte) 'i';
        NetTools.intTo4Bytes(triggertype, buffer, 5);
        NetTools.intTo4Bytes(objectID, buffer, 9);
        NetTools.intTo4Bytes(targetObjectID, buffer, 13);
        
        doSend(buffer);
    }
    
    
    /**
     * Message: Request legacyPlayfield trigger.
     */
    private void sendRequestPlayfieldTrigger() {
        buffer[0] = (byte) (7);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'r';
        buffer[3] = (byte) 't';
        buffer[4] = (byte) 'p';
        buffer[5] = functionCellX;
        buffer[6] = functionCellY;
        doSend(buffer);
    }
    
    private void sendFriendRequest(int targetID) {
        buffer[0] = (byte) (8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'f';
        buffer[3] = (byte) 'r';
        NetTools.intTo4Bytes(targetID, buffer, 4);
        doSend(buffer);
    }
    
    private void sendAcceptFriendRequest(int requestorID) {
        buffer[0] = (byte) (8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'f';
        buffer[3] = (byte) 'a';
        NetTools.intTo4Bytes(requestorID, buffer, 4);
        doSend(buffer);
    }
    
    private void sendDeclineFriendRequest(int requestorID) {
        buffer[0] = (byte) (8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'f';
        buffer[3] = (byte) 'd';
        NetTools.intTo4Bytes(requestorID, buffer, 4);
        doSend(buffer);
    }
    
    private void sendFriendShipCancelledMessage(int friendID) {
        buffer[0] = (byte) (8);
        buffer[1] = (byte) 'f';
        buffer[2] = (byte) 'f';
        buffer[3] = (byte) 'c';
        NetTools.intTo4Bytes(friendID, buffer, 4);
        doSend(buffer);
    }
    
    
    /** Message: Send chat message. */
    private void sendTalkToMessage(String chatMsg, int partnerID) {
        if (chatMsg!=null && chatMsg.length() > 0) {
            buffer[0] = (byte) (9 + chatMsg.length());
            NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_CHARACTER_CHAT_REQUEST, buffer, 1);
            NetTools.intTo4Bytes(partnerID, buffer, 4);
            buffer[8] = (byte) (chatMsg.length());
            System.arraycopy(chatMsg.getBytes(), 0, buffer, 9, chatMsg.length());
            doSend(buffer);
        }
    }

    /** Send a chat message to all players in the same cell. */
    private void sendTalkToAll(String msg) {
        if (msg!=null && msg.length() > 0) {
            buffer[0] = (byte) (5 + msg.length());
            NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_CHARACTER_CHAT_ALL_REQUEST, buffer, 1);
            buffer[4]=(byte)msg.length();
            System.arraycopy(msg.getBytes(), 0, buffer, 5, msg.length());
            doSend(buffer);
        }
    }
    
    /**
     * Request to drop an item (or a specific amount of units).
     * @param objectID The id of the item to drop
     * @param units to drop
     */
    private void sendDropItemMessage(int objectId, int units) {
        buffer[0] = (byte)10;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_ITEM_DROP_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(objectId, buffer, 4);
        NetTools.intTo2Bytes(units, buffer, 8);
        doSend(buffer);
    }

    
    /** Pickup an item from the ground. */
    private void sendPickupItemMessage(int objectID) {
        buffer[0] = (byte)8;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_ITEM_PICKUP_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(objectID, buffer, 4);
        doSend(buffer);
    }

    /** Own an item after a successful trade. */
    private void sendOwnItemMessage(int objectID) {
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'o';
        buffer[3] = (byte)'i';
        NetTools.intTo4Bytes(objectID, buffer, 4);
        doSend(buffer);
    }
    
    
    private void sendAttackMessage(int targetId) {
        buffer[0] = (byte)8;
        packetValidator.processAttackMessage(buffer);
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_CHARACTER_ATTACK_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(targetId, buffer, 4);
        doSend(buffer);
    }
    
    
    
    

    /** Send Ping to server to prevent timeout. */
    private void sendPing() {
        buffer[0] = (byte)4;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_PING, buffer, 1);
        doSend(buffer);
    }

    private void sendUseItem(int itemId) {
        buffer[0] = (byte)4;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_ITEM_USE_REQUEST, buffer, 1);
        doSend(buffer);
    }

    private void sendDebug(String msg) {
        buffer[0] = (byte)(5 + msg.length());
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_DEBUG, buffer, 1);
        buffer[4] = (byte)msg.length();
        //store msg
        System.arraycopy(msg.getBytes(), 0, buffer, 5, msg.length());
        doSend(buffer);
    }


    private void requestNumUsersOnline() {
        buffer[0] = (byte)4;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'u';
        buffer[3] = (byte)'o';
        doSend(buffer);
        
    }

    private void requestHighscores(int lowest, int listLength) {
        buffer[0] = (byte)16;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_CHARACTER_HIGHSCORE_REQUEST, buffer, 1);
        NetTools.intTo4Bytes(lowest, buffer, 4);
        NetTools.intTo2Bytes(listLength, buffer, 8);
        doSend(buffer);
         
    }

    private void requestEnterPlayfield() {
        buffer[0] = (byte)4;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_PLAYFIELD_ENTER_REQUEST, buffer, 1);
        doSend(buffer);
    }
    
    private void sendTimePassedMessage() {
        buffer[0] = (byte)4;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'t';
        buffer[3] = (byte)'p';
        doSend(buffer);
    }
    

    /**
     * Send equip message to the server, used when an item is equipped.
     * @param objectID The item id of the object
     */
    private void sendEquipChange(int objectID, boolean equip) {
        buffer[0] = (byte)8;
        if (equip) {
            NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_ITEM_EQUIP_REQUEST, buffer, 1);
        } else {
            NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_ITEM_UNEQUIP_REQUEST, buffer, 1);
        }
        NetTools.intTo4Bytes(objectID, buffer, 4);
        doSend(buffer);
    }

    /**
     * Send unequip message to the server, used when an item is unequipped.
     * @param objectID The item id of the object
     */
    private void sendEquipRemove(int objectID) {
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';  // e[q]uipment Item minus
        buffer[2] = (byte)'q';
        buffer[3] = (byte)'r';
        NetTools.intTo4Bytes(objectID, buffer, 4);
        doSend(buffer);
    }
    

    /**
     * Request to clear serverside equipment.
     */
    private void sendClearEquipment() {
        buffer[0] = (byte)4;
        buffer[1] = (byte)'f';  // e[q]uipment Item minus
        buffer[2] = (byte)'q';
        buffer[3] = (byte)'c';
        doSend(buffer);
    }
    
    private void sendAddBeltMessage(int objectID, int slotIndex) {
        buffer[0] = (byte)9;
        buffer[1] = (byte)'f';  // [a]dd to [b]elt
        buffer[2] = (byte)'a';
        buffer[3] = (byte)'b';
        NetTools.intTo4Bytes(objectID, buffer, 4);
        buffer[8] = (byte)slotIndex;
        doSend(buffer);
    }
    
    private void sendRemoveBeltMessage(int objectID) {
        // sending the belt slot is not required for removal
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';  // [r]emove from [b]elt
        buffer[2] = (byte)'r';
        buffer[3] = (byte)'b';
        NetTools.intTo4Bytes(objectID, buffer, 4);
        doSend(buffer);
    }

    
    /**
     * Infor the server that the sale offer for the given item has been cancelled.
     * @param itemid The id of the item
     */
    private void sendCancelSale(int itemid) {
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';  // Cancel item sale offer
        buffer[2] = (byte)'o';
        buffer[3] = (byte)'c';
        NetTools.intTo4Bytes(itemid, buffer, 4);    // store the item id
        doSend(buffer);
    }
    
    /**
     * Send an item sale offer to the server.
     * @param itemid The id of the item to sell
     * @param Price The price of the item
     * @param Number of units to sell
     */
    private void sendItemOffer(int itemid, int amount) {
        buffer[0] = (byte)16;
        buffer[1] = (byte)'f';  // Item Offer
        buffer[2] = (byte)'i';
        buffer[3] = (byte)'o';
        NetTools.intTo4Bytes(character_DB_ID, buffer, 4);
        NetTools.intTo4Bytes(itemid, buffer, 8);
        NetTools.intTo4Bytes(amount, buffer, 12);
        doSend(buffer);
    }

    /**
     * Request the buy / tradeOffer list from the selected character.
     * @param characterReceiverID The character id of the selected character
     */
    private void sendRequestBuyList(int characterReceiverID) {
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';  // Item Offer
        buffer[2] = (byte)'b';
        buffer[3] = (byte)'r';
        NetTools.intTo4Bytes(characterReceiverID, buffer, 4);
        doSend(buffer);
    }


    /**
     * Request to buy an object.
     * @param objectID The id of the object
     */ 
    private void sendBuyObjectMessage(int objectID, int sellerID, int amountGold, int amountToBuy) {
        buffer[0] = (byte)20;
        buffer[1] = (byte)'f';  // Item Offer
        buffer[2] = (byte)'b';
        buffer[3] = (byte)'o';
        NetTools.intTo4Bytes(objectID, buffer, 4);
        NetTools.intTo4Bytes(sellerID, buffer, 8);
        NetTools.intTo4Bytes(amountGold, buffer, 12);
        NetTools.intTo4Bytes(amountToBuy, buffer, 16);
        doSend(buffer);
        
    }
    
    /**
     * Inform server that the value for an attribute should be increased.
     */
    private void sendAttributeIncrease(int attributeindex) {
        buffer[0] = (byte)6;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'i';
        buffer[3] = (byte)'a';
        buffer[4] = (byte)attributeindex;
        buffer[5] = (byte)atrCHR_Modifiers[attributeindex];
        doSend(buffer);
    }
    
    /**
     * Request to respawn at the last saved position.
     */
    private void sendRequestRespawn() {
        buffer[0] = (byte)4;
        NetTools.intTo3Bytes(FWGMessageIDs.MSGID_GAME_CHARACTER_RESPAWN_REQUEST, buffer, 1);
        doSend(buffer);
    }
    
    
    /**
     * Request next line of credits.
     */
    private void sendRequestCredits() {
        buffer[0] = (byte)5;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'g';
        buffer[3] = (byte)'c';
        buffer[4] = creditid;
        doSend(buffer);
    }
    
    
    private void sendRequestNextHelpText(int helpID) {
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'n';
        buffer[3] = (byte)'h';
        NetTools.intTo4Bytes(helpID, buffer, 4);
        doSend(buffer);
        bAllowHelpReceive = true;
    }
    
    private void sendRequestHelpModuleFirstText(String moduleName) {
        buffer[0] = (byte)(5 + moduleName.length());
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'h';
        buffer[3] = (byte)'m';
        buffer[4] = (byte)moduleName.length();
        System.arraycopy(moduleName.getBytes(), 0, buffer, 5, moduleName.length());
        doSend(buffer);
        bAllowHelpReceive = true;
    }
    
    
    /**
     * Get dialogue element from bot/server.
     */
    private void sendGetDialogueMessage(int dialoguePartner_ID, int botphraseNext_ID) {
        buffer[0] = (byte)12;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'d';
        buffer[3] = (byte)'g';
        NetTools.intTo4Bytes(dialoguePartner_ID, buffer, 4); // store the id of the dialogue partner
        NetTools.intTo4Bytes(botphraseNext_ID, buffer, 8); // store the id of the nextbotphrase
        doSend(buffer);    // send!
    }

    /**
     * Request details for a quest.
     * @param questID The id of the quest to get the details for.
     */
    private void requestQuestDetails(int questID) {
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'q';
        buffer[3] = (byte)'d';
        NetTools.intTo4Bytes(questID, buffer, 4); // quest ID
        doSend(buffer);    // send!
    }
    

    /**
     * Request all open quests.
     */
    private void requestOpenQuests() {
        GTools.listRemoveAllEntries(listQuests);
        buffer[0] = (byte)4;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'q'; 
        buffer[3] = (byte)'g';
        doSend(buffer);    // send!
    }
    
    
    /**
     * Request to clear a questbook entry.
     * @param questID The id of the quest to get the details for.
     */
    private void sendLeaveQuest(int questID) {
        buffer[0] = (byte)8;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'q';
        buffer[3] = (byte)'l';
        NetTools.intTo4Bytes(questID, buffer, 4); // quest ID
        doSend(buffer);    // send!
    }
    
    
    private void sendGroundTriggerMessage(int itemID, byte cellX, byte cellY) {
        buffer[0] = (byte)10;
        buffer[1] = (byte)'f';
        buffer[2] = (byte)'g';
        buffer[3] = (byte)'t';
        NetTools.intTo4Bytes(itemID, buffer, 4); // item ID
        
        buffer[8] = cellX;
        buffer[9] = cellY;
        doSend(buffer);    // send!
        
        // System.out.println("Sending ground trigger: " + buffer[8] + ", " + buffer[9]);
    }
    
    
    private void removeCharacter(int objectId) {

        int selCharId = playfieldView.getSelectedCharacterId();
        playfield.removeCharacter(objectId);
        if (actionPartnerID == objectId) {
            resetSelectedActionPartner();
        }
        if (selCharId == objectId) {
            // selected character was removed
            // todo: if (currentSubState == SUBSTATE_TRADE_REQUEST || currentSubState == SUBSTATE_TRADE_TRANSFER_WAIT || currentSubState == SUBSTATE_TRADE_BUY_CONFIRM)

            if (currentSubState==SUBSTATE_TRADE_FIND || currentSubState==SUBSTATE_TALKTO_FIND || currentSubState==SUBSTATE_FIGHT_FIND || currentSubState==SUBSTATE_TRIGGERTARGET_FIND || currentSubState==SUBSTATE_FIGHT_ACTIVE) {
                boolean excludeOwnCharacter = !(currentSubState==SUBSTATE_TRIGGERTARGET_FIND && false);   // todo: some trigger target find will allow to select self (e.g. heal)
                Character newSelChar = playfieldView.selectClosestCharacter(excludeOwnCharacter);
                if (newSelChar!=null) {
                        GTools.textWindowSetText(info1Line, newSelChar.name + " (L." + newSelChar.level + ")");
                        if (currentSubState == SUBSTATE_FIGHT_ACTIVE) {
                            setBottomCommand1("Sel. Target");
                            setBottomCommand2("Back");
                            currentSubState = SUBSTATE_FIGHT_FIND;
                        }
                } else if (currentSubState != SUBSTATE_FIGHT_FIND && currentSubState != SUBSTATE_FIGHT_ACTIVE && currentSubState != SUBSTATE_TRIGGERTARGET_FIND && currentSubState != SUBSTATE_TRIGGERTARGET_ACTIVE) {
                    subStateOKDialog("No one in range.", currentState, SUBSTATE_NORMAL);
                } else if (currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_TRIGGERTARGET_ACTIVE) {
                    if (triggerOrGroundFindReturnState==SUBSTATE_BELT) {
                        substateBelt(selectedBeltItem);
                    } else {
                        substateInventory(selectedInvItem);
                    }
                } else {
                    subStateNormal();
                }
            }
        }



        if (true) {
            return;

        }


        /*
        idToCharacters.remove("" + objectid);
        //idToItems.remove("" + objectId);

        //check if we got anything to do with this object / need to change state
        if (objectid == actionPartnerID && (currentSubState == SUBSTATE_TRADE_REQUEST || currentSubState == SUBSTATE_TRADE_TRANSFER_WAIT || currentSubState == SUBSTATE_TRADE_BUY_CONFIRM)) 
        {
            //cancelTrade(true);
            actionPartnerID = -1;
            actionPartnerName = null;
         } else if (objectid != character_DB_ID && (currentSubState==SUBSTATE_TRADE_FIND || currentSubState==SUBSTATE_TALKTO_FIND || currentSubState==SUBSTATE_FIGHT_FIND || currentSubState==SUBSTATE_TRIGGERTARGET_FIND || currentSubState==SUBSTATE_FIGHT_ACTIVE)) {
            if (selectedPlayer >= 0 && selectedPlayer < playersOnScreen.length) {
                fwgoTmpM = playersOnScreen[selectedPlayer];
                boolean selectedPlayerWasRemoved = (fwgoTmpM!=null && fwgoTmpM.objectId==objectid);
                if (!selectedPlayerWasRemoved) {
                    // somebody else but the currently selected player was removed
                    // just remove from the selectable player's array and set referencing
                    // elements in the topology sorted selectable player's array to -1
                    // that should do the trick
                    int len=playersOnScreen.length;
                    int i;
                    int indexFound = -1;
                    // remove from playersOnScreen
                    for(i=0;i<len;i++) {
                        if (playersOnScreen[i]!=null && playersOnScreen[i].objectId == objectid) {
                            playersOnScreen[i]=null;
                            indexFound = i;
                            break;
                        }
                    }
                    // remove from topology sorted players
                    if (indexFound >= -1) {
                        for(i=0;i<len;i++) {
                            if (playersOnScreenXSortedIndex[i] == indexFound) {
                                this.playersOnScreenXSortedIndex[i]=-1;
                                break;
                            }
                        }
                        for(i=0;i<len;i++) {
                            if (playersOnScreenYSortedIndex[i] == indexFound) {
                                this.playersOnScreenYSortedIndex[i]=-1;
                                break;
                            }
                        }

                    }
                
                } else {
                    //selected player was removed
                    // we need to not only remove the selected player but also update the cursor selection
                    
                    //if (fwgoTmpM!=null) {
                    //if (fwgoTmpM.objectId==objectId) {
                        //playersOnScreen[selectedPlayer] = null;
                        //selectedPlayer = 0;

                        // in either case make sure that this object cannot be selected for any interaction
                       
                        
                        if (getPlayersOnScreen(false, 0, selectedPlayerWasRemoved, -1)>0 && playersOnScreen[selectedPlayer]!=null) {
                            if (selectedPlayerWasRemoved) {
                                //selected player was removed
                                GTools.textWindowSetText(info1Line, playersOnScreen[selectedPlayer].name + " (L." + playersOnScreen[selectedPlayer].level + ")");
                                if (currentSubState == SUBSTATE_FIGHT_ACTIVE) {
                                    setBottomCommand1("Sel. Target");
                                    setBottomCommand2("Back");
                                    currentSubState = SUBSTATE_FIGHT_FIND;
                                }
                            }
                        } else if (currentSubState != SUBSTATE_FIGHT_FIND && currentSubState != SUBSTATE_FIGHT_ACTIVE && currentSubState != SUBSTATE_TRIGGERTARGET_FIND && currentSubState != SUBSTATE_TRIGGERTARGET_ACTIVE) {
                            subStateOKDialog("No one in range.", currentState, SUBSTATE_NORMAL);
                        } else if (currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_TRIGGERTARGET_ACTIVE) {
                            if (triggerOrGroundFindReturnState==SUBSTATE_BELT) {
                                substateBelt(selectedBeltItem);
                            } else {
                                substateInventory(selectedInvItem);
                            }
                        } else {
                            subStateNormal();
                        }
                        lastCheck = curGametime;
                        //GTools.menuSetSelected(menuActionSub, 0);
                    //}
                }
            }
        } else if (objectid == character_DB_ID) {
            // own player removed
            allowGameInput = false;
            playerMove = false;
        }
        //find any trade request that belongs to this object id and delete it
         */
        /*
        for (int j=0; j<genericList.entries.size(); j++) {
            if (queuedEventsIDs[j]==objectId && GTools.listGetIconForEntry(genericList, j)==0) {
                GTools.listRemoveEntry(genericList, j); //remove entry from list
            }
        }
         */
    }
    
    
    //
    // UTILITY / GENERAL PURPOSE
    //
    

    /**
     * Convert a byte to its equivalent short value.
     * @param b The byte to convert
     * @return The short value
     */
    public static short b2s(byte b) {
        short s = (short) (b & 127);
        if ((b & 128) == 128) {
            s -= 128;
        }
        return s;
    }


    
    /**
     * Convert a byte to its equivalent integer value.
     * @param b The byte to convert
     * @return The integer value
     */
    public static int b2i(byte b) {
        int i = (b & 127);
        if ((b & 128) == 128) {
            i -= 128;
        }
        return i;
    }

    public static int graphicSelExtract(byte b) {
        if (b == -1) return -1;
        else return (b&0xFF);
        
    }
    
    /////
    

    private void requestTrigger(byte value) {
        
        if(!waitingForTrigger) {
            waitingForTrigger = true;

            // if it is a blocking trigger, do not move
            if((value & 32) == 32) {
                playerMove = false;
                sendPos = false;
                blockDuration = 5000;
                blockTriggerX = (functionCellX * TILEWIDTH) - xPos + 9;
                blockTriggerY = (functionCellY * TILEHEIGHT) - yPos + 6 + TOP_INFOHEIGHT;
            }
            
            // to make sure, we send the current position again
            //sendMoveObjectMessage();
//System.out.println("requesting Trigger: " + functionCellX + ", " + functionCellY);
            // activated a trigger
            sendRequestPlayfieldTrigger();
        }
    }

    private void resetCharArray(char[] a, String init) {    
        for (int i=0; i<a.length; i++) {
            if (init!=null && init.length() > i)
                a[i] = init.charAt(i);
            else
                a[i] = ' ';
        }
    }
    
    private void adjustScreen(int x, int y) {
        int centerPosX = DISPLAYWIDTH/2 - PLAYERWIDTH_HALF;
        int centerPosY = DISPLAYHEIGHT/2 - PLAYERHEIGHT_HALF;
        xPos = x - centerPosX;  // pos of left screen border
        playerScreenX = centerPosX; // position player in h - center
        if (xPos + DISPLAYWIDTH > playfieldWidth * TILEWIDTH) {
            centerPosX = xPos + DISPLAYWIDTH - playfieldWidth * TILEWIDTH;
            xPos -= centerPosX;
            playerScreenX += centerPosX;
        }
        if(xPos < 0) {
            playerScreenX += xPos;
            if (playerScreenX < 0) {
                playerScreenX = 0;
            }
            xPos = 0;
        }
        
        yPos = y - centerPosY;
        playerScreenY = centerPosY;
//#if Series40_MIDP2_0
//#         if (yPos + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT > playfieldHeight * TILEHEIGHT) {
//#             centerPosY = yPos + DISPLAYHEIGHT + BOTTOM_INFOHEIGHT - playfieldHeight * TILEHEIGHT;
//#else
        if (yPos + DISPLAYHEIGHT > playfieldHeight * TILEHEIGHT) {
            centerPosY = yPos + DISPLAYHEIGHT - playfieldHeight * TILEHEIGHT;
//#endif
            yPos -= centerPosY;
            playerScreenY += centerPosY;
        }
        if(yPos < 0) {
            playerScreenY += yPos;
            if (playerScreenY < 0) {
                playerScreenY = 0;
            }
            yPos = 0;
        }
        playerScreenY += TOP_INFOHEIGHT;

        if (playerObject!=null) {
            playerObject.x = (short)(xPos + playerScreenX);
            playerObject.y = (short)(yPos + playerScreenY - TOP_INFOHEIGHT);
            //System.out.println("adjusting: " + playerObject.x + ", " + playerObject.y);                
        }
        
        
        if (cellUpdate()) {
        /*
            for (int i=0; i<FIREWALL_WINDOWSIZE; i++) {
                System.out.print("\n");
                for (int j=0; j<FIREWALL_WINDOWSIZE; j++) {
                        System.out.print(fireWalls[j][i] + " , ");
                }
            }
         */
        }
         
    }
    
    
    
    


     private void initWindows() {
        switch (currentState) {
            case STATE_INTRO:

                try {
                    fontImage = Image.createImage("/chatfont.png");
                } catch (IOException ioe) {
                    if (debugLevel > 0)
                        System.out.println("error loading image");
                }

                font = new GFont(fontImage, 72, 5, 6, 26);
                font.setFontCharTableDefaults(true);
                GlobalSettings.setFont(font);
                GTools.setDisplayDimensions(0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                GTools.createDefaultKeyTable();
                
                
                GTools.setDefaultWindowColors(0xCCCCCC, 0xCCCCCC, 0x000080, 0x000080);

                OKButton = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, " OK ", font, false);
                GTools.windowSetBorder(OKButton, 2, 2);
                OKButton.prepareButtonImage();
                
                YESButton = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, " YES ", font, false);
                GTools.windowSetBorder(YESButton, 2, 2);
                YESButton.prepareButtonImage();
                
                NOButton = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, " NO ", font, false);
                GTools.windowSetBorder(NOButton, 2, 2);
                NOButton.x = DISPLAYWIDTH - NOButton.width - 1;
                NOButton.prepareButtonImage();
                
                
                
                //create overlay buttons
                overlayButton1 = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, "Select", font, false);
                overlayButton2 = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, "Close", font, false);
                GTools.windowSetBorder(overlayButton1, 2, 2);
                GTools.windowSetBorder(overlayButton2, 2, 2);
                overlayButton2.x = DISPLAYWIDTH - overlayButton2.width - 1;
                overlayButton1.prepareButtonImage();
                overlayButton2.prepareButtonImage();
                   
                // create option buttons
                optionButton1 = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, "Select", font, false);
                optionButton2 = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, "Close", font, false);
                GTools.windowSetBorder(optionButton1, 2, 2);
                GTools.windowSetBorder(optionButton2, 2, 2);
                optionButton2.x = DISPLAYWIDTH - optionButton2.width - 1;
                optionButton1.prepareButtonImage();
                optionButton2.prepareButtonImage();
                
                
                //create command buttons
                comButton1 = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, "Select", font, false);
                comButton2 = GTools.buttonCreate(1, TOP_INFOHEIGHT + DISPLAYHEIGHT+1, "Game", font, false);
                GTools.windowSetBorder(comButton1, 2, 2);
                GTools.windowSetBorder(comButton2, 2, 2);
                comButton2.x = DISPLAYWIDTH - comButton2.width - 1;
                comButton1.prepareButtonImage();
                comButton2.prepareButtonImage();
                
                GTools.setDefaultWindowColors(0xCC9900, 0xFFCC00, 0x000000, 0xCC6600);
                
                //GAUGE / PROGRESS WINDOW
                gaugeWindow = GTools.windowCreate(0, 0, MIN_DISPLAYWIDTH, 5);
                GTools.windowSetColors(gaugeWindow, 0x009900, 0x00FF00, 0x000000, 0x000000);
                GTools.windowSetBorder(gaugeWindow, 1,1);
//#if Series40_MIDP2_0
//#                 GTools.windowSetPosition(gaugeWindow, 0, 15);
//#else
                GTools.windowSetPosition(gaugeWindow, 0, 20);
//#endif
                GTools.windowCenterX(gaugeWindow, 0, DISPLAYWIDTH);
                
                //GAUGE / PROGRESS WINDOW
                gaugeWindow1 = GTools.windowCreate(0, 0, MIN_DISPLAYWIDTH, 5);
                GTools.windowSetColors(gaugeWindow1, 0x009900, 0x00FF00, 0x000000, 0x000000);
                GTools.windowSetBorder(gaugeWindow1, 1, 1);
//#if Series40_MIDP2_0
//#                 GTools.windowSetPosition(gaugeWindow1, 0, gaugeWindow.y + gaugeWindow.height + 11);
//#else
                GTools.windowSetPosition(gaugeWindow1, 0, gaugeWindow.y + gaugeWindow.height + 20);
//#endif
                GTools.windowCenterX(gaugeWindow1, 0, DISPLAYWIDTH);

                
                //gaugeWindow.y -= 12;
                
                // E-Mail input form
                menuEmail = GTools.menuCreate(0, 0, MIN_DISPLAYWIDTH, MIN_DISPLAYHEIGHT, 5);
                GTools.windowSetBorder(menuEmail, 1, 2);
                GTools.menuSetCaptionOneLine(menuEmail, "E-MAIL (optional)", font, 0);
                GTools.windowSetColors(menuEmail ,0x999999, 0x999999, 0x666666, 0x666666);
                emailField1 = GTools.inputWindowCreate(1, 13, MIN_DISPLAYWIDTH-7, font.charHeight*2+8, 31, font, 0xCCCCCC);
                GTools.windowSetBorder(emailField1, 2,2);
                emailField1.activeBackColor = 0x800000;
                emailField2 = GTools.inputWindowCreate(1, 58, MIN_DISPLAYWIDTH-7, font.charHeight*2+8, 32, font, 0xCCCCCC);
                emailField2.activeBackColor = 0x800000;
                //emailField2.keyMapPosition = GTools.POSITION_BOTTOM;
                emailField1.emptyInfo = "Enter part 1 of address".toCharArray();
                emailField2.emptyInfo = "Enter part 2 of address".toCharArray();
                GTools.windowSetBorder(emailField2, 2,2);

                atIcon = GTools.imageWindowCreate((MIN_DISPLAYWIDTH-8) / 2 - 5, 40, 10, 10, 51, 0, GlobalResources.imgIngame);

                phoneTemplate = GTools.imageWindowCreate((DISPLAYWIDTH) / 2 - 16, TOTALHEIGHT - TOTALHEIGHT/4 - 33, 32, 67, 0, 0, phoneTemplateImage);

                //GTools.menuSetItem(menuEmail, label1, 0);
                GTools.menuSetItem(menuEmail, atIcon, 0);
                GTools.menuSetItem(menuEmail, emailField1, 1);
                //GTools.menuSetItem(menuEmail, label2, 2);
                GTools.menuSetItem(menuEmail, emailField2, 2);

                GTools.menuSetSelected(menuEmail, 1);
                GTools.windowCenterXY(menuEmail, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);


                
                //LOGIN MENU
                menuLogin = GTools.menuCreate(0, 0, MIN_DISPLAYWIDTH, MIN_DISPLAYHEIGHT, 5);
                GTools.windowSetBorder(menuLogin, 1, 2);
                GTools.menuSetCaptionOneLine(menuLogin, "LOGIN", font, 0);
                GTools.windowSetColors(menuLogin ,0x999999, 0x999999, 0x666666, 0x666666);

                label1 = GTools.labelCreate(13, 5, "User name", font, true);
                label2 = GTools.labelCreate(13, 34, "Password", font, true);
                label2.centerTextH = true;
                label3 = GTools.labelCreate(0, 0, "http://rhynn.com", font, true);
                label3.centerTextH = true;
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
                
                usernameWindow = GTools.inputWindowCreate(13, 13, MIN_DISPLAYWIDTH-32, font.charHeight+8, 10, font, 0xCCCCCC);
                //GTools.inputWindowEnableKeymappingDisplay(usernameWindow, 4, 1, 0x000099, 0xCC9900, true);
                
                GTools.windowSetBorder(usernameWindow, 2,2);
                usernameWindow.activeBackColor = 0x800000;
                passwordWindow = GTools.inputWindowCreate(13, 42, MIN_DISPLAYWIDTH-32, font.charHeight+8, 10, font, 0xCCCCCC);
                passwordWindow.activeBackColor = 0x800000;
                GTools.windowSetBorder(passwordWindow, 2,2);
                passwordWindow.password = true;
                
                GTools.menuSetItem(menuLogin, label1, 0);
                GTools.menuSetItem(menuLogin, usernameWindow, 1);
                GTools.menuSetItem(menuLogin, label2, 2);
                GTools.menuSetItem(menuLogin, passwordWindow, 3);
                GTools.menuSetSelected(menuLogin, 1);

                GTools.windowCenterXY(menuLogin, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                //GTools.menuEnsureContainsAll(menuLogin, true, true);
                
                
                //GTools.setDefaultWindowColors(0xCC9900, 0xFFCC00, 0x000000, 0xCC6600);
                //GTools.listSetDimensions(genericList, genericList.width - 12, genericList.height - 48, genericList.entryGapY - 8, genericList.xOffset);
                
                bigList = GTools.listCreate(0, 0, DISPLAYWIDTH-6, MIN_DISPLAYHEIGHT+2, 2, 2, MAX_FAR_PORTALS, font);
                bigList.acceptInput = true;
                bigList.selectable = false;
                bigList.cycleWrap = true;
                GTools.windowSetColors(bigList, 0xCC9900, 0xFFCC00, 0x000000, 0xCC6600);

                menuBigList = GTools.menuCreate(0, 0, bigList.width + 6, bigList.height + 15 + 2*font.charHeight, 1);
                GTools.windowSetBorder(menuBigList, 1, 2);
                GTools.windowSetColors(menuBigList, 0x999999, 0x999999, 0x666666, 0x666666);
                //listSpinButton = GTools.spinButtonCreate(0, i , 13, 10, genericList);        

                GTools.menuSetItem(menuBigList, bigList, 0);
                GTools.menuSetCaptionOneLine(menuBigList, "Far Portal Jump", font, 0);                
                GTools.menuEnsureContainAll(menuBigList, true, true);
                //GTools.windowCenterXY(menuBigList, 0, 0, DISPLAYWIDTH, DISPLAYHEIGHT);
                GTools.windowSetPosition(menuBigList, 0, TOP_INFOHEIGHT + DISPLAYHEIGHT - menuBigList.height);
                //GTools.windowCenterX(menuBigList, 0, DISPLAYWIDTH);
                GTools.menuSetSelected(menuBigList, 0);
                
                if (ITEMHEIGHT > font.charHeight) {
                    i = ITEMHEIGHT-font.charHeight;
                } else {
                    i = font.charHeight-ITEMHEIGHT;
                }
                
                
                //genericList = GTools.listCreate(0, font.charHeight + 3, MIN_DISPLAYWIDTH + 12, 2*(ITEMHEIGHT) + 4 + 48, i, 2, MAX_QUEUED_EVENTS, font);
                genericList = GTools.listCreate(0, 0, MIN_DISPLAYWIDTH, 3*(ITEMHEIGHT) + 54, i, 8, MAX_QUEUED_EVENTS, font);
                genericList.acceptInput = true;
                genericList.selectable = false;
                GTools.windowSetColors(genericList, 0x0099CC, 0x0099CC, 0x000000, 0x0066CC);
                
                menuList = GTools.menuCreate(0, 0, genericList.width + 6, genericList.height + 15 + 2*font.charHeight, 1);
                GTools.windowSetBorder(menuList, 1, 2);
                GTools.windowSetColors(menuList ,0x999999, 0x999999, 0x666666, 0x666666);
                i = genericList.y + genericList.height + 2;
                //listSpinButton = GTools.spinButtonCreate(0, i , 13, 10, genericList);        

                GTools.menuSetItem(menuList, genericList, 0);
                GTools.menuSetCaptionOneLine(menuList, "Select a Server", font, 0);                
                GTools.menuEnsureContainAll(menuList, true, true);
                GTools.windowCenterXY(menuList, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                GTools.menuSetSelected(menuList, 0);
                
//#if Series40_MIDP2_0
//#                 info1Line = GTools.textWindowCreate(0, series40_TOP_INFOHEIGHT, DISPLAYWIDTH, font.charHeight+4, "Player: ", 30, font, false);
//#else
                info1Line = GTools.textWindowCreate(0, TOP_INFOHEIGHT, DISPLAYWIDTH, font.charHeight+4, "Player: ", 30, font, false);
//#endif
                GTools.windowSetBorder(info1Line, 1, 1);
                GTools.windowSetColors(info1Line, 0x000000, 0xCCCCCC, 0x333399, 0x333399);

                info1Line2 = GTools.textWindowCreate(0, TOP_INFOHEIGHT + DISPLAYHEIGHT - (font.charHeight+4), DISPLAYWIDTH, font.charHeight+4, "Peaceful Area", 15, font, false);
                GTools.windowSetBorder(info1Line2, 1, 1);
                GTools.windowSetColors(info1Line2, 0x000000, 0x000000, 0x006600, 0x006600);
                
                
                highScoreWindow = GTools.textWindowCreate(0, 0, 118, 63, null, 128, font, false);
                GTools.windowSetBorder(highScoreWindow, 1, 3);
                GTools.windowSetColors(highScoreWindow, 0x666666, 0x666666, 0x003300, 0x003300);
                GTools.windowCenterX(highScoreWindow, 0, DISPLAYWIDTH);
                
                confirmWindow = GTools.labelCreate(0, 0, "Really Exit?", font, false);
                GTools.windowSetBorder(confirmWindow, 1, 10);
                GTools.windowSetColors(confirmWindow, 0xCC9900, 0xFFCC00, 0x333366, 0xCC6600);
                
                // TRAFFIC LABEL
                labelTraffic = GTools.labelCreate(0, 0, "bytes:0000000000", font, false);
                GTools.windowSetBorder(labelTraffic, 1, 1);
                GTools.windowSetPosition(labelTraffic, 0, TOTALHEIGHT - BOTTOM_INFOHEIGHT - labelTraffic.height);
                
                //WIAT LABEL
                labelWait = GTools.labelCreate(0, 0, "Please Wait..", font, false);
                GTools.windowSetBorder(labelWait, 1, 2);
                //labelWait = GTools.buttonCreate(0, i, "Please Wait..", font, false);
                GTools.windowCenterXY(labelWait, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                labelWait.centerTextH = true;
                
                
                //TEXT EDIT BOX
                editBox = GTools.menuCreate(0, 0, this.MIN_DISPLAYWIDTH, 36, 1);
                GTools.windowSetBorder(editBox, 1,5);
                GTools.windowSetColors(editBox, 0x999999, 0x999999, 0x666666, 0x666666);
                editBoxInput = GTools.inputWindowCreate(0, 0, MIN_DISPLAYWIDTH - 4, font.charHeight + 4, 12, font, 0xCCCCCC);
                editBoxInput.activeBackColor = 0x800000;

                GTools.menuSetCaptionOneLine(editBox, "Character name", font, 0);
                GTools.menuSetItem(editBox, editBoxInput, 0);
                GTools.menuSetSelected(editBox, 0);
                GTools.menuEnsureContainAll(editBox, false, true);
                GTools.windowCenterXY(editBox, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);

                //Context Options menu
                GTools.setDefaultWindowColors(0x000000, 0xCC6600, 0x000000, 0xCC6600);
                menuContextOptions = GTools.buttonListCreate(0, 0, 1, 2, 4, MAX_CONTEXTOPTIONS, font);
                GTools.windowSetColors(menuContextOptions, 0xFFCC00, 0xFFCC00, 0x000000, 0x000000);
                GTools.windowSetPosition(menuContextOptions, 0, TOTALHEIGHT - BOTTOM_INFOHEIGHT - menuContextOptions.height);
                
                // Free Context Options menu
                menuFreeContextOptions = GTools.buttonListCreate(0, 0, 1, 2, 4, MAX_FREECONTEXTOPTIONS, font);
                GTools.windowSetColors(menuFreeContextOptions, 0xFFCC00, 0xFFCC00, 0x000000, 0x000000);
               
                // PRICE EDIT BOX
                GTools.setDefaultWindowColors(0xCC9900, 0xFFCC00, 0x000000, 0xCC6600);
                
                priceBox = GTools.menuCreate(0, 0, this.MIN_DISPLAYWIDTH, 50, 4);
                GTools.windowSetBorder(priceBox, 1,6);
                GTools.windowSetColors(priceBox, 0xCC9900, 0xFFCC00, 0x333366, 0xCC6600);
                labelAmount = GTools.labelCreate(2, 12, "Units:", font, true);
                amountInput = GTools.inputWindowCreate(0, labelAmount.y + labelAmount.height + 2, MIN_DISPLAYWIDTH - 4, font.charHeight + 4, 3, font, 0xCCCCCC);
                amountInput.activeBackColor = 0x660000;
                amountInput.numeric = true;
                //labelPrice = GTools.labelCreate(2, amountInput.y + amountInput.height + 9, "Price (gold):", font, true);
                //priceInput = GTools.inputWindowCreate(0, labelPrice.y + labelPrice.height + 2, MIN_DISPLAYWIDTH - 4, font.charHeight + 4, 6, font, 0xCCCCCC);
                //priceInput.activeBackColor = 0x660000;
                //priceInput.numeric = true;

                GTools.menuSetCaptionOneLine(priceBox, "Set item for sale", font, 0);
                
                GTools.menuSetItem(priceBox, labelAmount, 0);
                GTools.menuSetItem(priceBox, amountInput, 1);
                //GTools.menuSetItem(priceBox, labelPrice, 2);
                //GTools.menuSetItem(priceBox, priceInput, 3);
                
                GTools.menuEnsureContainAll(priceBox, true, true);
                GTools.windowCenterXY(priceBox, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                
                
                dropItemBox = GTools.menuCreate(0, 0, this.MIN_DISPLAYWIDTH, 50, 3);
                GTools.windowSetBorder(dropItemBox, 1, 6);
                GTools.windowSetColors(dropItemBox, 0xCC9900, 0xFFCC00, 0x333366, 0xCC6600);

                GTools.menuSetCaptionOneLine(dropItemBox, "Drop item", font, 0);
                labelDropAmount = GTools.labelCreate(2, 12, "Units:", font, true);
                dropAmountInput = GTools.inputWindowCreate(0, labelDropAmount.y + labelDropAmount.height + 6, 50, font.charHeight + 8, 3, font, 0xCCCCCC);
                dropAmountInput.activeBackColor = 0x660000;
                dropAmountInput.numeric = true;
                GTools.windowSetBorder(dropAmountInput, 2, 2);
                dropAmountSpinbutton = GTools.spinButtonCreate(dropAmountInput.x + dropAmountInput.width + 7, dropAmountInput.y, dropAmountInput.height, dropAmountInput.height, null);
                GTools.windowSetColors(dropAmountSpinbutton, GTools.TRANSPARENT, 0xFFFFFF, GTools.TRANSPARENT, GTools.TRANSPARENT);
                dropAmountSpinbutton.selectable = false;
                dropAmountSpinbutton.acceptInput = false;
                
                GTools.menuSetItem(dropItemBox, labelDropAmount, 0);
                GTools.menuSetItem(dropItemBox, dropAmountInput, 1);
                GTools.menuSetItem(dropItemBox, dropAmountSpinbutton, 2);
                
                GTools.menuEnsureContainAll(dropItemBox, true, true);
                GTools.windowCenterXY(dropItemBox, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);

                

                // QUEST MENU
                listQuests = GTools.listCreate(0, 0, DISPLAYWIDTH-6, 100-6, 10, 12, 10, font);
                //genericList = GTools.listCreate(0, font.charHeight + 3, MIN_DISPLAYWIDTH, 3*(ITEMHEIGHT) + 4, 0, ITEMWIDTH+2, 10, font);
                listQuests.acceptInput = true;
                listQuests.selectable = false;

                //questNameWindow = GTools.textWindowCreate(0, 0, DISPLAYWIDTH-6, font.charHeight+4, "--", 48, font, false);
                questNameWindow = GTools.listCreate(0, 3, DISPLAYWIDTH-6, QUESTCLASSES_ICON_SIZE+4, 2, 4, 1, font);
                questNameWindow.acceptInput = false;
                questNameWindow.selectable = false;
                GTools.windowSetColors(questNameWindow, 0xFFCC00, 0xFFCC00, 0x404040, 0x404040);
                questDescriptionWindow = GTools.textWindowCreate(0, questNameWindow.height+2, DISPLAYWIDTH-6, 84-(2*(font.charHeight+4)+font.charHeight)-6, "--", 100, font, false);
                GTools.windowSetColors(questDescriptionWindow, 0xFFCC00, 0xFFCC00, 0x500000, 0x500000);
                GTools.windowSetBorder(questDescriptionWindow, 1, 3);
                questLocationWindow = GTools.textWindowCreate(0, questDescriptionWindow.y+questDescriptionWindow.height-1, DISPLAYWIDTH-6, (2*font.charHeight)+4, "--", 36, font, false);
                questLocationWindow.centerTextH = true;
                questLocationWindow.centerTextV = true;
                GTools.windowSetColors(questLocationWindow, 0xFFCC00, 0xFFCC00, 0x404040, 0x404040);
                
                menuQuests = GTools.menuCreate(0, 0, DISPLAYWIDTH, 100 + font.charHeight + 3, 4);
                GTools.windowSetColors(menuQuests, 0xCCCCCC, 0xCCCCCC, 0x000050, 0x000050);
                GTools.windowSetBorder(menuQuests, 1, 2);
                GTools.menuSetCaptionOneLine(menuQuests, "Quest book", font, 0);
                GTools.menuSetItem(menuQuests, listQuests, 0);
                //GTools.menuSetItem(menuQuests, questNameWindow, 1);
                GTools.menuSetItem(menuQuests, questNameWindow, 1);
                GTools.menuSetItem(menuQuests, questDescriptionWindow, 2);
                GTools.menuSetItem(menuQuests, questLocationWindow, 3);
                
                GTools.windowSetPosition(menuQuests, 0, TOTALHEIGHT-BOTTOM_INFOHEIGHT-menuQuests.height);
                
                changeQuestmenuView(false, false);                
                
                
                //Game Options menu
                GTools.setDefaultWindowColors(0x000000, 0x707070, 0x000050, 0x707070);
                menuGameOptions = GTools.buttonListCreate(0, 0, 2, 2, 9, 5, font);
                GTools.windowSetColors(menuGameOptions, 0xCCCCCC, 0xCCCCCC, 0x000050, 0x000050);
                
                GTools.buttonListSetButton(menuGameOptions, "Sound options..", 0, false, true);
                GTools.buttonListSetButton(menuGameOptions, "Network Traffic", 1, false, true);
                GTools.buttonListSetButton(menuGameOptions, "View credits", 2, false, true);
                GTools.buttonListSetButton(menuGameOptions, "Change E-Mail..", 3, false, true);
                GTools.buttonListSetButton(menuGameOptions, "Exit Game", 4, false, true);
                if (!soundPossible) {
                    GTools.buttonListUnsetButton(menuGameOptions, 0, false, true);
                    GTools.menuSetSelected(menuGameOptions, 1);
                } else {
                    GTools.menuSetSelected(menuGameOptions, 0);
                }
                GTools.windowCenterXY(menuGameOptions, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);

                //GTools.setDefaultWindowColors(0xA0A0A0, 0xCCCCCC, 0x000050, 0x707070);
                // SOUND MENU
                GTools.setDefaultWindowColors(0xA0A0A0, 0xCCCCCC, 0x000050, 0x707070);
                menuSound = GTools.menuCreate(0, 0, 103, 48, 2);
                GTools.windowSetColors(menuSound, 0xCCCCCC, 0xCCCCCC, 0x000050, 0x000050);
                GTools.windowSetBorder(menuSound, 1, 2);
                GTools.menuSetCaptionOneLine(menuSound, "Sound options", font, 0);
                
                if (soundON) {
                    buttonMusic = GTools.buttonCreate(4, 3, "Music:  ON", font, false);
                } else {
                    buttonMusic = GTools.buttonCreate(4, 3, "Music:  OFF", font, false);
                }
                buttonVolume = GTools.buttonCreate(4, 19, "Volume:   ", font, false);
                
                
                GTools.menuSetItem(menuSound, buttonMusic, 0);
                GTools.menuSetItem(menuSound, buttonVolume, 1);
                GTools.menuSetSelected(menuSound, 0);
                menuSound.nextAltKey = -200;
                menuSound.prevAltKey = -200;
                //GTools.menuEnsureContainAll(menuSound, false, true);
                GTools.windowCenterXY(menuSound, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                
                gaugeSound = GTools.windowCreate(menuSound.x + 64, menuSound.y + 31, 30, 10);
                GTools.windowSetColors(gaugeSound, 0x009900, 0x00FF00, 0x000000, 0x000000);
                
                //creditsWindow = GTools.textWindowCreate(0, 0, 128, 58, "\n\nFantasy Worlds: Rhynn\n\nAwareDreams 2003-2004\n\nhttp://AwareDreams.com", 128, font, false);
                creditsWindow = GTools.textWindowCreate(0, 0, 128, 96, "\n\nloading credits...\n\n\nplease wait", 128, font, false);
                GTools.windowSetBorder(creditsWindow, 1, 4);
                creditsWindow.centerTextH = true;
                GTools.windowCenterXY(creditsWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                
                /*labelDebug1 = GTools.labelCreate(0, TOP_INFOHEIGHT, "000000", font, true);
                labelDebug2 = GTools.labelCreate(DISPLAYWIDTH, TOP_INFOHEIGHT, "MSG", font, true);
                labelDebug2.x -= labelDebug2.width;

                labelDebug3 = GTools.labelCreate(DISPLAYWIDTH, TOP_INFOHEIGHT + DISPLAYHEIGHT, "T: 000", font, true);
                labelDebug3.x -= labelDebug3.width;
                labelDebug3.y -= labelDebug3.height;
                */
                labelDebug4 = GTools.labelCreate(10, DISPLAYHEIGHT/2-20, "0000 fps\n00000 bytes\n0000 queue", font, true);
                labelDebug5 = GTools.labelCreate(0, DISPLAYHEIGHT-12, "00000000 of 00000000 -> 00000000", font, true);
                labelDebug6 = GTools.labelCreate(0, DISPLAYHEIGHT-12, "00000 ms", font, true);
                
                label2 = GTools.labelCreate(13, 34, "Password", font, true);
                label2.centerTextH = true;
                label3 = GTools.labelCreate(0, 0, versionName(true), font, true);
                label3.centerTextH = true;
                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
                //label3.y = TOTALHEIGHT/2 + TOTALHEIGHT/4;
                

                String eulaText =   "License agreement\n\nYou may not reverse engineer, decompile, or disassemble this software, except and only to the extent that such activity is expressly permitted by applicable law notwithstanding this limitation.\n\n"
                                    + "If you decide to disclose your account information to others the Rhynn adminstration team cannot be held responsible for resulting consequences.\n\n"
                                    + "This software is a beta release, it is provided as is and all warranties are expressly disclaimed.\n\nSelect accept if you agree, or exit otherwise."
                                    + "\n\nThis text is also available in other languages at www.rhynn.com/eula.php";
                eulaWindow = GTools.textWindowCreate(0, 0, DISPLAYWIDTH, DISPLAYHEIGHT, eulaText, eulaText.length(), font, false);
                GTools.windowSetColors(eulaWindow, 0x0099CC, 0x0099CC, 0x000000, 0x000000);


                break; //END STATE INTRO
                

            case STATE_CHARACTER_SELECT:
                switch (currentSubState) {
                    case SUBSTATE_NORMAL:


                        /*
                        GTools.listRemoveAllEntries(genericList);
                        if (!listMenuConstructed) {
                            GTools.listSetDimensions(genericList, genericList.width + 12, genericList.height + 48, genericList.entryGapY + 8, genericList.xSpace);
                            GTools.listSetIconDimensions(genericList, PLAYERWIDTH, PLAYERHEIGHT);
                        }
                        // The character list is empty now, do not allow character specific options
                        GTools.buttonListUnsetButton(menuContextOptions, 2, false, true);  // rename
                        GTools.buttonListUnsetButton(menuContextOptions, 3, false, true);  // delete

                        if (!listMenuConstructed) {
                            GTools.menuEnsureContainAll(menuList, true, true);
                            GTools.windowCenterXY(menuList, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                        }
                        GTools.menuSetCaptionOneLine(menuList, "Select character", font, 0);
                        listMenuConstructed = true;
                         */
                        break;
                
                    case SUBSTATE_CHARACTER_NEW:
                        /*
                        GTools.listRemoveAllEntries(genericList);
                        GTools.listSetIconDimensions(genericList, PLAYERWIDTH, PLAYERHEIGHT);
                        // $-> note: later as more character classes are added the character class images must already be loadd at this stage (from the jar) to be present in the imageManager

                        // append the character classes to the list
                        int ccCount = characterClasses.size();
                        for (int ccIndex=0; ccIndex<ccCount; ccIndex++) {
                            CharacterClass cc = (CharacterClass)characterClasses.elementAt(ccIndex);
                            j = 3*cc.graphicsDim;
                            GImageClip gic = imageManager.getImageClipFormCache(cc.graphicsId, j + cc.graphicsX, cc.graphicsY, cc.graphicsDim, cc.graphicsDim);
                            GTools.listAppendEntry(genericList, cc.displayName, cc, gic);
                        }
                        GTools.menuSetCaptionOneLine(menuList, "Select class", font, 0);
                         */
                        break;
                }
                break;
            case STATE_GAME:
                if (initGameWindows) {
                    initGameWindows = false;
                    //clear unused windows
                    menuLogin = null;
                    usernameWindow = null;
                    passwordWindow = null;
                    System.gc();

                    //left bottom command available
                    setBottomCommand1("Action");
                    setBottomCommand2("Game");
                    GTools.setDefaultWindowColors(0x999999, 0xFFCC00, 0x993333, 0xCC6600);
//#if !(Series40_MIDP2_0)
                    //character level
                    playerLevelWindow = GTools.buttonCreate(DISPLAYWIDTH - (font.charWidth*2)-4 - (16 - font.charHeight - 4), 0, "00", font, false);
                    GTools.windowSetBorder(playerLevelWindow, 2, (16 - font.charHeight - 4) / 2);
                    GTools.windowSetColors(playerLevelWindow, 0x999999, 0x999999, 0x000000, 0x000000);

                    playerExperienceWindow = GTools.textWindowCreate(DISPLAYWIDTH-(font.charWidth*8)-2-playerLevelWindow.width, 0, font.charWidth*8 + 2, font.charHeight + 2, "X:000000", 8, font, false);
                    GTools.windowSetBorder(playerExperienceWindow, 1, 0);
                    GTools.windowSetColors(playerExperienceWindow, 0x994800, 0x994800, GTools.TRANSPARENT, GTools.TRANSPARENT);
                    playerGoldWindow = GTools.textWindowCreate(DISPLAYWIDTH-(font.charWidth*8)-2-playerLevelWindow.width, playerExperienceWindow.y+playerExperienceWindow.height, font.charWidth*8 + 2, font.charHeight + 2, "G:000000", 8, font, false);
                    GTools.windowSetBorder(playerGoldWindow, 1, 0);
                    GTools.windowSetColors(playerGoldWindow, 0x994800, 0x994800, GTools.TRANSPARENT, GTools.TRANSPARENT);
//#else
//#                         playerGoldWindow = GTools.textWindowCreate(DISPLAYWIDTH/2-(font.charWidth*3)-2, TOTALHEIGHT-font.charHeight-2-3, font.charWidth*6 + 2, font.charHeight + 2, "000000", 6, font, false);
//#                         GTools.windowSetBorder(playerGoldWindow, 1, 0);
//#                         GTools.windowSetColors(playerGoldWindow, 0x994800, 0x994800, 0x000033, 0x000033);
//#endif

                    //Action menu
                    GTools.setDefaultWindowColors(0x000000, 0xCC6600, 0x000000, 0xCC6600);
                    menuActionSub = GTools.buttonListCreate(0, 0, 2, 1, 4, 4, font);
                    GTools.windowSetColors(menuActionSub, 0xFFCC00, 0xFFCC00, 0x000000, 0x000000);
                    prepareActionSubMenu(0);
                    //GTools.windowSetPosition(menuActionSub, 0, TOTALHEIGHT - BOTTOM_INFOHEIGHT - menuActionSub.height);

                    
                    //Chat menu
                    GTools.setDefaultWindowColors(0xCC9900, 0xFFCC00, 0x000000, 0xCC6600);

                    menuChat = GTools.menuCreate(0, 0, DISPLAYWIDTH, 40, 2);       
                    GTools.windowSetBorder(menuChat, 1, 1);
                    GTools.windowSetColors(menuChat, 0xCCCCCC, 0xCCCCCC, 0x000000, 0x000000);
                    //
                    chatWindow = GTools.textWindowCreate(0, 0, menuChat.width-4, (font.charHeight*7)+4, null, 256, font, true);
                    chatWindow.activeBackColor = 0x660000;
                    chatWindow.selectable = false;  // : correct scrolling in inputwindow
                    GTools.windowSetBorder(chatWindow,1, 1);
                    
                    //
                    inputChatWindow = GTools.inputWindowCreate(0, chatWindow.y + chatWindow.height + 1, menuChat.width-4, font.charHeight+4, MAX_CHATCHARS, font, 0xCCCCCC);
                    inputChatWindow.activeBackColor = 0x800000;
                    inputChatWindow.emptyInfo = "* = Short Msg. Insert".toCharArray();

                    if (MAX_CHATCHARS*font.charWidth > menuChat.width-8) {    //if less than MAX_CHATCHARS_PER_LINE chars on one line, make it two lines.
                        menuChat.height += font.charHeight;
                        menuChat.y -= font.charHeight;
                        inputChatWindow.height += font.charHeight;
                        GTools.textWindowCalculateTextSettings(inputChatWindow, font);
                    }

                    GTools.menuSetItem(menuChat, chatWindow, 0);
                    GTools.menuSetItem(menuChat, inputChatWindow, 1);
                    GTools.menuSetSelected(menuChat, 1);
                    GTools.menuEnsureContainAll(menuChat, true, true);
                    GTools.windowSetPosition(menuChat, 0, TOTALHEIGHT - BOTTOM_INFOHEIGHT - menuChat.height);

                    
                    //change generic list
                    GTools.listSetIconDimensions(genericList, 9, 13);
                    //GTools.listSetIconForEntry(genericList, 0, ingame, 5, INGAME_ICON_HEIGHT); // queued message
                    //GTools.listSetIconForEntry(genericList, 1, ingame, 5, INGAME_ICON_HEIGHT); // queued message
                    GTools.menuSetCaptionOneLine(menuList, "Messages", font, 0);
                    GTools.menuEnsureContainAll(menuList, true, true);
                    

                    // friend request list just like the generic list
                    friendRequestList = GTools.listCreate(0, 0, genericList.width, genericList.height, genericList.entryGapY, genericList.xSpace, MAX_QUEUED_FRIEND_REQUESTS, font);
                    friendRequestList.acceptInput = true;
                    friendRequestList.selectable = false;
                    GTools.windowSetColors(friendRequestList, 0x0099CC, 0x0099CC, 0x000000, 0x0066CC);
                    GTools.listSetIconDimensions(friendRequestList, 15, 13);

                    //GTools.listSetIconForEntry(friendRequestList, 0, ingame, 28, 60); // queued friend request icon
                    
                    
                    bottomInfoWindow = GTools.textWindowCreate(0, TOP_INFOHEIGHT + DISPLAYHEIGHT - 16, DISPLAYWIDTH, 16, null, 68, font, false);
                    GTools.windowSetColors(bottomInfoWindow, 0xCCCCCC, 0xCCCCCC, 0x004800, 0x004800);
                    bottomInfoWindow.centerTextH = true;
                    bottomInfoWindow.centerTextV = true;
                    
                    j = (DISPLAYWIDTH > 176) ? 176 : DISPLAYWIDTH;
                    itemDescriptionWindow = GTools.textWindowCreate(4, TOP_INFOHEIGHT + 46, j, 2*font.charHeight, null, 48, font, false);
                    GTools.windowSetBorder(itemDescriptionWindow, 0, 0);
                    GTools.windowSetColors(itemDescriptionWindow, GTools.TRANSPARENT, GTools.TRANSPARENT, GTools.TRANSPARENT, GTools.TRANSPARENT);
                  
                    GTools.inputWindowRemoveText(editBoxInput);


                    // clientphrase menu
                    menuClientphrases = GTools.menuCreate(0, 0, DISPLAYWIDTH, 73, 3);
                    GTools.windowSetBorder(menuClientphrases, 1, 1);
                    GTools.windowSetColors(menuClientphrases, 0xCCCCCC, 0xCCCCCC, 0x000000, 0x000000);
                    GTools.windowSetPosition(menuClientphrases, 0, TOTALHEIGHT - BOTTOM_INFOHEIGHT - menuClientphrases.height);
                    
                    // botphrase window
                    if(DISPLAYWIDTH < 176) {
                        botphraseWindow = GTools.textWindowCreate(0, menuClientphrases.y - 36 - 4, DISPLAYWIDTH, 36, null, 96, font, false);
                    } else {
                        botphraseWindow = GTools.textWindowCreate(0, menuClientphrases.y - 30 - 4, DISPLAYWIDTH, 30, null, 96, font, false);
                    }
                    botphraseWindow.centerTextV = true;
                    GTools.windowSetBorder(botphraseWindow, 1, 2);
                    GTools.windowSetColors(botphraseWindow, 0xCCCCCC, 0xCCCCCC, 0x000050, 0x000050);

                    // clientphrase windows
                    for (int i=0; i<3; i++) {
                        clientphraseWindows[2-i] = GTools.textWindowCreate(0, menuClientphrases.height - ((i+1)*23) - 4, DISPLAYWIDTH-4, 23, null, 64, font, false);
                        clientphraseWindows[2-i].centerTextV = true;
                        GTools.windowSetColors(clientphraseWindows[2-i], GTools.TRANSPARENT, GTools.TRANSPARENT, 0x404040, 0xCC6600);
                        GTools.windowSetBorder(clientphraseWindows[2-i], 1, 2);
                        GTools.menuSetItem(menuClientphrases, clientphraseWindows[2-i], 2-i);
                    }
                    GTools.menuSetSelected(menuClientphrases, 0);
                    
                    
                    //TEXT EDIT BOX
                    editBoxInput.numeric = false;
                    GTools.textWindowSetMaxChars(editBoxInput, 32);
                    editBoxInput.height = (font.charHeight*2) + ((editBoxInput.borderSize + editBoxInput.innerOffset) << 1);
                    editBoxInput.width = MIN_DISPLAYWIDTH;
                    GTools.textWindowCalculateTextSettings(editBoxInput, font);

                    GTools.menuSetCaptionOneLine(editBox, "Edit", font, 0);
                    GTools.menuRemoveAllItems(editBox);
                    GTools.menuSetMaxItems(editBox, 1);

                    editBoxInput.xOffset = 0; editBoxInput.yOffset = 0; // important
                    GTools.windowSetPosition(editBoxInput, 0, 5);

                    GTools.menuSetItem(editBox, editBoxInput, 0);
                    GTools.menuSetSelected(editBox, 0);
                    GTools.menuEnsureContainAll(editBox, true, true);
                    GTools.windowCenterXY(editBox, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                    
                    // init friend list (extents like bigList to fit into menuBigList)
                    friendList = GTools.listCreate(0, 0, DISPLAYWIDTH-6, MIN_DISPLAYHEIGHT+2, 4, 6, MAX_FRIENDS, font);
                    friendList.acceptInput = true;
                    friendList.selectable = false;
                    friendList.cycleWrap = true;

                    GTools.listSetIconDimensions(friendList, 5, 5);
                    //GTools.listSetIconForEntry(friendList, 0, ingame, 43, 64); // friend offline
                    //GTools.listSetIconForEntry(friendList, 1, ingame, 43, 60); // friend online
                    
                    GTools.windowSetColors(friendList, 0xCC9900, 0xFFCC00, 0x000000, 0xCC6600);
                    // init message list
                    GTools.listRemoveAllEntries(genericList);
                }
                
                GTools.textWindowSetText(info1Line, "Target: ");
                
                /*
                queuedEventsIDs[genericList.entries.size()] = 20;                                
                tmpStringM = "bogus\n";
                //char[] tmpCharsM = (tmpStringM.getBytes());
                char[] tmpCharsM = new char[tmpStringM.length()]; 
                tmpStringM.getChars(0, tmpStringM.length(), tmpCharsM, 0);
                GTools.listAppendEntry(genericList, tmpStringM, tmpCharsM);    
                GTools.listSetIconForEntry(genericList, genericList.entries.size()-1, 0);
                */
                
                
            break;
        }
        if (debugLevel > 0)
            System.out.println("Memory: " + Runtime.getRuntime().freeMemory() + " bytes");

    }   

     
    private void prepareContextMenu(int contextType) {
        GTools.setDefaultWindowColors(0x000000, 0xCC6600, 0x000000, 0xCC6600);
        GTools.buttonListUnsetAll(menuContextOptions, false, true);
        switch (contextType) {
            case 0: // CHARACTER SELECT OPTIONS
                GTools.buttonListSetButton(menuContextOptions, "Select", 0, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Create New", 1, false, true);
                if (genericList.entries.size() > 0) {
                    // --GTools.buttonListSetButton(menuContextOptions, "Rename", 2, false, true);
                    GTools.buttonListSetButton(menuContextOptions, "Delete", 2, false, true);
                }
                break;
            case 1: // EVENT OPTIONS
                GTools.buttonListSetButton(menuContextOptions, "Show", 0, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Delete", 1, false, true);
                break;
            case 2: // QUEST BOOK OPTIONS
                GTools.buttonListSetButton(menuContextOptions, "Show info ..", 0, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Delete quest", 1, false, true);
                break;
            case 3: // SUBSCRIBE - ENTER PHONE NUMBER OPTIONS
                //menuContextOptions.x = DISPLAYWIDTH - menuContextOptions.width; // these options are displayed to the right
                GTools.buttonListSetButton(menuContextOptions, "Send Number", 0, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Help ..", 1, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Exit", 2, false, true);
                break;
            case 4: // CHAT OPTIONS
                //menuContextOptions.x = DISPLAYWIDTH - menuContextOptions.width; // these options are displayed to the right
                GTools.buttonListSetButton(menuContextOptions, "Send", 0, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Insert Short Msg.", 1, false, true);
                break;
            case 5: // FRIEND LIST OPTIONS
                GTools.buttonListSetButton(menuContextOptions, "Talk to friend", 0, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Cancel friendship", 1, false, true);
                // GTools.buttonListSetButton(menuContextOptions, "Request Peaceful Agreeement", 2, false, true);
                // GTools.buttonListSetButton(menuContextOptions, "Cancel Peaceful Agreeement", 2, false, true);
                break;
            case 6: // E-Mail entry options
                GTools.buttonListSetButton(menuContextOptions, "Store E-Mail", 0, false, true);
                GTools.buttonListSetButton(menuContextOptions, "Info ..", 1, false, true);
                break;
                
                
        }
    }

    private void initCharacterSelectionList() {
            GTools.listRemoveAllEntries(genericList);
            if (!listMenuConstructed) {
                GTools.listSetDimensions(genericList, genericList.width + 12, genericList.height + 48, genericList.entryGapY + 8, genericList.xSpace);
                GTools.listSetIconDimensions(genericList, PLAYERWIDTH, PLAYERHEIGHT);
            }

            if (!listMenuConstructed) {
                GTools.menuEnsureContainAll(menuList, true, true);
                GTools.windowCenterXY(menuList, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
            }
            genericList.activeBackColor = 0xCC6600;
            GTools.menuSetCaptionOneLine(menuList, "Select character", font, 0);
            listMenuConstructed = true;

            // add characters to the list
            int chLen = ownCharacters.size();

            for (int i=0; i<chLen; i++) {
                Character c = (Character)ownCharacters.elementAt(i);
                int j = 5*c.graphicsDim;
                GImageClip gic = imageManager.getImageClipFormCache(c.graphicsId, j + c.graphicsX, c.graphicsY, c.graphicsDim, c.graphicsDim);
                GTools.listAppendEntry(genericList, c.name, c, gic);
            }
            if (chLen == 0) {
                // The character list is empty now, do not allow character specific options
                // --GTools.buttonListUnsetButton(menuContextOptions, 2, false, true);  // rename
                GTools.buttonListUnsetButton(menuContextOptions, 2, false, true);  // delete
            }
    }

    private void initCharacterClassList() {
        GTools.listRemoveAllEntries(genericList);
        GTools.listSetIconDimensions(genericList, PLAYERWIDTH, PLAYERHEIGHT);
        // $-> note: later as more character classes are added the character class images must already be loadd at this stage (from the jar) to be present in the imageManager

        // append the character classes to the list
        int ccCount = characterClasses.size();
        for (int ccIndex=0; ccIndex<ccCount; ccIndex++) {
            CharacterClass cc = (CharacterClass)characterClasses.elementAt(ccIndex);
            j = 3*cc.graphicsDim;
            GImageClip gic = imageManager.getImageClipFormCache(cc.graphicsId, j + cc.graphicsX, cc.graphicsY, cc.graphicsDim, cc.graphicsDim);
            String display = cc.displayName;
            if (cc.premiumOnly) {
                display += " [p]";
            }
            GTools.listAppendEntry(genericList, display, cc, gic);
        }
        genericList.activeBackColor = 0x009900;
        GTools.menuSetCaptionOneLine(menuList, "Select class", font, 0);
        setBottomCommand1("Select");
        setBottomCommand2("Cancel");
    }

    private void prepareFreeContextMenu(int contextType) {
        GTools.setDefaultWindowColors(0x000000, 0xCC6600, 0x000000, 0xCC6600);
        GTools.buttonListUnsetAll(menuFreeContextOptions, false, false);
        switch (contextType) {
            case 0: // Inventory Options
                GTools.buttonListSetButton(menuFreeContextOptions, "Use", 0, false, false);
                GTools.buttonListSetButton(menuFreeContextOptions, "Equip", 1, false, false);
                GTools.buttonListSetButton(menuFreeContextOptions, "UnEquip", 2, false, false);

                GTools.buttonListSetButton(menuFreeContextOptions, "Add to Belt ..", 3, false, false);
                GTools.buttonListSetButton(menuFreeContextOptions, "Rem. from Belt", 4, false, false);
                
                GTools.buttonListSetButton(menuFreeContextOptions, "Sale Offer ..", 5, false, false);
                GTools.buttonListSetButton(menuFreeContextOptions, "Cancel Sale", 6, false, false);
                GTools.buttonListSetButton(menuFreeContextOptions, "Drop", 7, false, false);
                GTools.buttonListSetButton(menuFreeContextOptions, "Sort Inv.", 8, false, false);
                break;
            case 1: // Character game options
                GTools.buttonListSetButton(menuFreeContextOptions, "Character Stats", 0, false, false);
                GTools.buttonListSetButton(menuFreeContextOptions, "Edit Details", 1, false, false);
                GTools.windowCenterXY(menuFreeContextOptions, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                break;
        }
    }
    

    /**
     * Set the option context menu for an item in t he inventory.
     * @param item The item to set the parameters for
     * @return True if the item context menu is not empty
     */
    private boolean setItemOptions(Item item) {
        if (item==null) {
            return false;
        }
        int selectedButton = -1;

        // todo: take into account isPremium, on server too

        if (item.usageType == Item.USAGE_TYPE_USE) {
            GTools.buttonListSetButton(menuFreeContextOptions, null, 0, false, false);   // USE button ACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 1, false, false);   // EQUIP button INACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 2, false, false);   // UNEQUIP button INACTIVE

            if (item.equipped == 0) {    // not in belt
                GTools.buttonListSetButton(menuFreeContextOptions, null, 3, false, false);   // ADD BELT button ACTIVE
                GTools.buttonListUnsetButton(menuFreeContextOptions, 4, false, false);   // REM. BELT button INACTIVE
                selectedButton = 0;
            } else {
                GTools.buttonListSetButton(menuFreeContextOptions, null, 4, false, false);   // REM. BELT button ACTIVE
                GTools.buttonListUnsetButton(menuFreeContextOptions, 3, false, false);   // ADD BELT button ACTIVE
            }
            selectedButton = 0;

        } else {
            GTools.buttonListUnsetButton(menuFreeContextOptions, 0, false, false);   // USE button INACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 3, false, false);   // ADD BELT button INACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 4, false, false);   // REM. BELT button INACTIVE

            if (item.equipped == 0) {
                GTools.buttonListUnsetButton(menuFreeContextOptions, 2, false, false);   // UNEQUIP button INACTIVE
                if (playerObject.meetsItemRequirements(item)) {
                    GTools.buttonListSetButton(menuFreeContextOptions, null, 1, false, false);   // EQUIP button ACTIVE
                    selectedButton = 1;
                } else {
                    GTools.buttonListUnsetButton(menuFreeContextOptions, 1, false, false);   // EQUIP button INACTIVE
                }
            } else {
                GTools.buttonListUnsetButton(menuFreeContextOptions, 1, false, false);   // EQUIP button INACTIVE
                GTools.buttonListSetButton(menuFreeContextOptions, null, 2, false, false);   // UNEQUIP button ACTIVE
                selectedButton = 2;
            }
        }

        if (item.canDrop) {    // item can be dropped
            GTools.buttonListSetButton(menuFreeContextOptions, null, 7, false, false);  // DROP BUTTON ACTIVE
            if (selectedButton == -1) selectedButton = 7;
        } else {
            GTools.buttonListUnsetButton(menuFreeContextOptions, 7, false, false);  // DROP BUTTON INACTIVE
        }

        if (!item.canSell) {   // no sale offer possible
            GTools.buttonListUnsetButton(menuFreeContextOptions, 5, false, false);   // SALE OFFER button INACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 6, false, false);   // CANCEL SALE OFFER button INACTIVE
        } else {    // sale offer possible
            if (item.unitsSell == 0) {   // item is NOT for sale
                GTools.buttonListSetButton(menuFreeContextOptions, null, 5, false, false);   // SALE OFFER button ACTIVE
                GTools.buttonListUnsetButton(menuFreeContextOptions, 6, false, false);   // CANCEL SALE OFFER button INACTIVE
                if (selectedButton == -1) selectedButton = 5;
            } else {
                GTools.buttonListUnsetButton(menuFreeContextOptions, 5, false, false);   // SALE OFFER button INACTIVE
                GTools.buttonListSetButton(menuFreeContextOptions, null, 6, false, false);   // CANCEL SALE OFFER button ACTIVE
                if (selectedButton == -1) selectedButton = 6;
            }
        }

        /*
        if (item.equipped == 0) {    // not equipped
            GTools.buttonListUnsetButton(menuFreeContextOptions, 2, false, false);   // UNEQUIP button INACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 4, false, false);   // REM. BELT button INACTIVE

            if (item.usageType == Item.USAGE_TYPE_USE)) { // usable item
                if (item.units > 0) {
                    GTools.buttonListSetButton(menuFreeContextOptions, null, 0, false, false);   // USE button ACTIVE
                    GTools.buttonListSetButton(menuFreeContextOptions, null, 3, false, false);   // ADD BELT button ACTIVE
                }
                GTools.buttonListUnsetButton(menuFreeContextOptions, 1, false, false);   // EQUIP button INACTIVE
                k3 = 0;
                notEmpty = true;
            } else if (item.units ==-2 && item.requiredSkill *10 <= skill && item.requiredMagic * 10 <= magic) {   // equipment item
                GTools.buttonListUnsetButton(menuFreeContextOptions, 0, false, false);   // USE button INACTIVE
                GTools.buttonListUnsetButton(menuFreeContextOptions, 3, false, false);   // ADD BELT button INACTIVE
                if (playerObject.meetsItemRequirements(item)) {
                    GTools.buttonListSetButton(menuFreeContextOptions, null, 1, false, false);   // EQUIP button ACTIVE
                } else {
                    GTools.buttonListUnsetButton(menuFreeContextOptions, 1, false, false);   // EQUIP button INACTIVE
                }
                k3 = 1;
                notEmpty = true;
            }
        } else {    // equipped
            GTools.buttonListUnsetButton(menuFreeContextOptions, 1, false, false);   // EQUIP button INACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 3, false, false);   // ADD BELT button INACTIVE
            
            if (item.units > 0 || item.units== -1) { // usable item + in belt
                GTools.buttonListSetButton(menuFreeContextOptions, null, 0, false, false);   // USE button ACTIVE
                GTools.buttonListUnsetButton(menuFreeContextOptions, 2, false, false);   // UNEQUIP button INACTIVE
                GTools.buttonListSetButton(menuFreeContextOptions, null, 4, false, false);   // REM. BELT button ACTIVE
                k3 = 0;
            } else {    // wearable item + in equipment (cannot be in belt)
                GTools.buttonListUnsetButton(menuFreeContextOptions, 0, false, false);   // USE button INACTIVE
                GTools.buttonListSetButton(menuFreeContextOptions, null, 2, false, false);   // UNEQUIP button ACTIVE
                GTools.buttonListUnsetButton(menuFreeContextOptions, 4, false, false);   // REM. BELT button INACTIVE
                k3 = 2;
            }
            
            notEmpty = true;
        }
        
        if (!item.canSell < 0) {   // no sale offer possible
            GTools.buttonListUnsetButton(menuFreeContextOptions, 5, false, false);   // SALE OFFER button INACTIVE
            GTools.buttonListUnsetButton(menuFreeContextOptions, 6, false, false);   // CANCEL SALE OFFER button INACTIVE
        } else {    // sale offer possible
            if (item.gold == 0) {   // item is NOT for sale
                GTools.buttonListSetButton(menuFreeContextOptions, null, 5, false, false);   // SALE OFFER button ACTIVE
                GTools.buttonListUnsetButton(menuFreeContextOptions, 6, false, false);   // CANCEL SALE OFFER button INACTIVE
                if (k3 == -1) k3 = 5;
                notEmpty = true;
            } else {
                GTools.buttonListUnsetButton(menuFreeContextOptions, 5, false, false);   // SALE OFFER button INACTIVE
                GTools.buttonListSetButton(menuFreeContextOptions, null, 6, false, false);   // CANCEL SALE OFFER button ACTIVE
                if (k3 == -1) k3 = 6;
                notEmpty = true;
            }
        }
        
        GTools.buttonListSetButton(menuFreeContextOptions, null, 8, false, false);  // DROP BUTTON ACTIVE
        */

        if (selectedButton!=-1) {
            GTools.menuSetSelected(menuFreeContextOptions, selectedButton);
            return true;
        }
        return false;
        
        
    }
    
    

    /**
     * Create and prepare core graphics.
     */
    private void initGraphics() {
        // call the first time
        if(currentImage == null) {
            xOffset = (getWidth() - DISPLAYWIDTH) / 2;
            yOffset = (getHeight() - TOTALHEIGHT) / 2;

            if (!isDoubleBuffered) {
                currentImage = Image.createImage(DISPLAYWIDTH, TOTALHEIGHT);
                graphicsOne = currentImage.getGraphics();
                currentGraphics = graphicsOne;
                currentImage2 = Image.createImage(DISPLAYWIDTH, TOTALHEIGHT);
                graphicsTwo = currentImage2.getGraphics();
            }
            
            if(DISPLAYWIDTH < (INVENTORY_COLS * ITEMSLOTWIDTH)) {
                inventoryNeedsScrolling = true;
            }
        }

        try {
            if (phoneTemplateImage==null) {
                phoneTemplateImage = Image.createImage("/phone_template.png");
            }
            // create repeatedly used icons
            iconMessageNew = new GImageClip(GlobalResources.imgIngame, 5, 16, 9, 13);
            iconFriendRequestNew = new GImageClip(GlobalResources.imgIngame, 28, 60, 15, 13);
            iconFriendOnline = new GImageClip(GlobalResources.imgIngame, 43, 64, 5, 5);
            iconFriendOffline = new GImageClip(GlobalResources.imgIngame, 43, 64, 5, 5);
            inventoryBackgroundSlot = new GImageClip(GlobalResources.imgIngame, 0, 0, 16, 16);
        } catch (IOException ioe) {}


        // delete and free all images
        System.gc();
        
        // init depending on the game state
        try {
            switch(currentState) {
                case STATE_INTRO:
                    background = Image.createImage("/intro.png");
                    break;

                case STATE_INTRO_LIST:
                    background = null;
                    System.gc();
                    break;
                case STATE_LOGIN_MENU:
                    clearScreen();
                    break;
                case STATE_WAIT:
                    break;
                case STATE_LOGIN_ERROR:
                    break;
                case STATE_GAUGE:
                    break;
                case STATE_GAME:
                    if (special==null) {
                        special = Image.createImage("/special.png");
                    }
                    
                    if (menu==null) {
                        menu = Image.createImage("/menu.png");
                    }
                    if (questclasses==null) {
                        questclasses = Image.createImage("/quest.png");
                    }
//#if !(Series40_MIDP2_0)
                    prepareTopBottomBackground();
//#else
//#                     //playfieldImageValid = false;
//#endif
                    System.gc();
                    break;
                case STATE_CHARACTER_SELECT:
                    //players = Image.createImage("/1.png");
                    // image with the player graphics uses id 100005, all of these player graphics should be in the jar, as the client will need them at all times
                    imageManager.loadImageFromJarToCache(100005, "players01.png", true);
                    players = imageManager.getImageFromCache(100005);
                    break;
            }
            
        } catch(IOException ioe) {
            if (debugLevel > 0)
                System.out.println(ioe);
        }
        if (debugLevel > 0)
            System.out.println("Memory: " + Runtime.getRuntime().freeMemory() + " bytes");
    }

    
    
    
    /**
     * Prepare Network.
     */
    private synchronized void initNet() {
        stopNet();
        if(!netStarted && !netError) {
            if (debugLevel > 0) {
                //System.out.println("connecting to: " + host);
            }
            gbManager.setSleepRead(25);
            
//#if __VALIDATOR_IMPL
            packetValidator = new PacketValidatorImpl();
//#else            
//#             packetValidator = new PacketValidator();
//#endif
            
            gbManager.startNetworking(host, SOCKETPORT, SOCKETREAD, SOCKETWRITE);          
            //gbManager.startNetworking(host, gbManager.ALLOW_REAL);
            //gbManager.startNetworking(host, gbManager.ALLOW_UDP);
        
/*            if(gbManager.startNetworking(host, gbManager.ALLOW_SOCKET)) {
                netStarted = true;
                checkNet = true;
                database.setValue("host", host);
                return 1;
            } else {
                netError = true;
                database.setValue("host", defaultHost);
                host =  defaultHost;
                return -1;
            }
*/            
        }

        //return 0;
    }

    public void stopNet() {
        //System.out.println("stopping ......................");
        gbManager.stopNetworking();
        netStarted = false;
        firstConnect = true;
    }
    
    private void doSend(byte[] buffer) {
        //System.out.println("sending id: " + NetTools.intFrom3Bytes(buffer[1], buffer[2], buffer[3]));
        packetValidator.processGenericMessage(buffer);
        gbManager.sendMessage(buffer);
    }
    
    private void subStateOKDialog(String msg, int nextState, int nextSubState) {
        if (currentSubState == SUBSTATE_OK_DIALOG) {
            msgOK_tmp = new String(confirmWindow.text);
        }
        
        GTools.labelSetText(confirmWindow, msg, false);
        GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
        currentSubState = SUBSTATE_OK_DIALOG;
        this.nextState = nextState;
        this.nextSubState = nextSubState;
        confirmOK = true;        
    }
    
    private void promptConfirm(String text) {
        GTools.labelSetText(confirmWindow, text, false);
        GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
        confirmYesNo = true;
    }

    private void overlayMessageTimeoutControls(String message, long timeout) {
        overlayControlsTimeOut = curGametime + timeout;
        overlayMessage(message);
    }

    /**
     * Specify a message that will be displayed as a message box.
     * @param message The message to display
     */
    private void overlayMessage(String message) {
        if(overlayState != OVERLAY_DIED) {
            if (message==null) {
                message = "-UNKNOWN MESSAGE-";
            }
            overlayState = OVERLAY_MESSAGE;
            GTools.labelSetText(confirmWindow, message, false);
            if (confirmWindow.width > DISPLAYWIDTH) {
                confirmWindow.width = DISPLAYWIDTH;
                confirmWindow.height = confirmWindow.height + font.charHeight<<1;
                GTools.textWindowRemoveText(confirmWindow);
                GTools.textWindowCalculateTextSettings(confirmWindow, font);
                GTools.textWindowSetText(confirmWindow, message);
            }
            GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
        }
    }                    
    
    /**
     * Show information at the bottom of the screen for a limited time.
     * @param text The text to show
     * @param duration The duration info will be displayed - in milliseconds
     */
    private void showBottomInfo(String text, long duration, boolean forceForeground) {
        GTools.textWindowSetText(bottomInfoWindow, text);
        bottomInfo_DisplayTime = duration;
        /*if (explicitReward == 1) {
            bottomInfo_Foreground = true;
        } else {
            bottomInfo_Foreground = false;
        }*/
        bottomInfo_Foreground = forceForeground;
    }
    
    
    /** Check if username and password are ok to transfer to the server. */
    public boolean loginInputOK() {
        clientName = null;
        clientPass = null;
        tmpCharsK = GTools.inputWindowGetText(usernameWindow);
        tmpCharsK1 =  GTools.inputWindowGetText(passwordWindow);
        
        boolean bK1 = true;

        if (tmpCharsK==null || tmpCharsK.length < 4) {
            subStateOKDialog("User name too short\nMin. 4 characters", -1, -1);
            bK1 = false;
        } else if (containsInvalidChars(tmpCharsK, false)) {
            subStateOKDialog("User name contains invalid characters.\nAllowed are only a-z, 0-9, _, -, .", -1, -1);
            bK1 = false;
        } else if (tmpCharsK1==null || tmpCharsK1.length < 4) {
            subStateOKDialog("Password too short\nMin. 4 characters", -1, -1);
            bK1 = false;
        } else if (containsInvalidChars(tmpCharsK1, false)) {
            subStateOKDialog("Password contains invalid characters.\nAllowed are only a-z, 0-9, _, -, .", -1, -1);
            bK1 = false;
        }

        if (bK1) {
            clientName = new String(tmpCharsK);
            clientName = clientName.trim();
            clientPass = new String(tmpCharsK1);
            clientPass = clientPass.trim();
        } else {
            return false;
        }
        
        if (clientName.length() < 4) {
            subStateOKDialog("User name too short\nMin. 4 characters", -1, -1);
            bK1 = false;
        } else if (clientPass.length() < 4) {
            subStateOKDialog("Password too short\nMin. 4 characters", -1, -1);
            bK1 = false;
        }
        
        tmpCharsK = null;
        tmpCharsK1 = null;
        return bK1;
    }
    
    
    /** Check if a char array contains chars that might cause trouble (with databases). */
    private boolean containsInvalidChars(char[] text, boolean email) {
        if (text==null) {
            return true;
        }
        
        String tmpString = new String(text);
        
        if (tmpString==null) {
            return false;
        }
        
        tmpString = tmpString.trim();
        if (tmpString==null || tmpString.length() == 0) {
            return false;
        }
        text = tmpString.toCharArray();
        
        for (k1=0; k1<text.length;k1++) {
            if (!((text[k1] >= 'a' && text[k1] <= 'z') || 
                (text[k1] >= 'A' && text[k1] <= 'Z') ||
                (text[k1] >= '0' && text[k1] <= '9') ||
                text[k1] == '_' || text[k1] == '.' || text[k1] == '-'
                || (email && (text[k1] == '+' || text[k1] == '%'))
                ))
            {
                return true;
            }
        }
        return false;
    }


    private void resetInventoryScrollHugeItemSettings() {
        inventoryScrollHugeItemTime = this.curGametime;
        inventoryScrollHugeItemDown = true;
        inventoryScrollHugeItemOffset = 0;
    }    
    
    private void replaceNumber(char[] text, int number, int from, int to) {
        for(int i = to+1; --i >= from; ) {
            text[i] = (char)((number%10)+'0');
            number = number/10;
        }        
    }
    
    
    /**
     * Replace chars to contain the char representation of a number - in the given 
     * interval within the char array. Left align the number, i.e. 
     * clear space in the interval trailing the actual number chars.
     * E.g.: 
     * array = 'h', 'i', ' ', 't', 'h', 'e', 'r', 'e'
     * interval: from = 2, to = 6 (5 chars)
     * number: 19 -> 00019 -> 19___
     * -> array = 'h', 'i', '1', '9', ' ', ' ', ' ', 'e'
     * @param text The text to perform the replacement on
     * @param number The number to represent
     * @param from The startPlay of the interval
     * @param to The end of the interval
     * @param floatingPoint True if value should be fixed point float with one decimal
     */
    private void replaceNumberLeftAlign(char[] text, int number, int from, int to, boolean floatingPoint) {
        
        int interval = to-from + 1;
        int effectivePos = interval - 1;
        int numDigits = 1;
        
        if (floatingPoint) {
            numReplace[effectivePos] = (char)((number%10)+'0');
            effectivePos--;
            numReplace[effectivePos] = '.';
            effectivePos--;
            number= number / 10;
            numDigits = 3;
        }
        
        while (effectivePos >= 0) {
            numReplace[effectivePos] = (char)((number%10)+'0');
            effectivePos--;
            number = number/10;
            if (number==0) {
                break;
            }
            numDigits++;
        }
        int charsToClear = interval-numDigits;

        
        // clear empty part behind the actual digits
        if (charsToClear > 0) {
            System.arraycopy(numReplaceEmpty, 0, text, to - charsToClear + 1, charsToClear);
        }
        // copy the actual digits
        if (numDigits > 0) {
            System.arraycopy(numReplace, charsToClear, text, from, numDigits);
        }
    }
    
    private void replaceNumber(char[] text, long number, int from, int to) {
        for(int i = to+1; --i >= from; ) {
            text[i] = (char)((number%10)+'0');
            number = number/10;
        }        
    }
    
    

    
    private void updateTrafficLabel() {
        replaceNumber(labelTraffic.text, trafficCounterReceive + gbManager.getBytesSent(), 6, 15);
    }

        
    private Image getImage(String filename, boolean flag) {
//System.out.println("request: " + filename);
        Image imageData = null;

        if(useRSreading) {
            reconnectDatabaseGfx(filename);
        }
        
        if(!imageCache.containsKey(filename)) {
            // !!!
            gbManager.setSleepRead(10);
            //i=0;

            //check RS for image
            byte[] temp=null;
            if(useRSreading && (temp = tempDatabase.getBytes(filename))!=null){
                //System.out.println("from RS");
                imageData = Image.createImage(temp, 0, temp.length);
                temp = null;
            } else {

                sendGetImage(filename);

                byte[] message;

                do {

                    message = gbManager.getDataAvailable();
                    if(message != null) {
                        if(message[1] == 'a') {

                            //
                            // Image length
                            //
                            if(message[2] == 'i' && message[3] == 'l') {
                                trafficCounterReceive+=message[0];
                                nextImageSize = NetTools.intFrom2Bytes(message[4], message[5]);
                                /*
                                System.out.println("message 4: " + message[4]);
                                System.out.println("message 5: " + message[5]);
System.out.println("image size: " + nextImageSize);
                                */
                                nextImage = new byte[nextImageSize];
                                nextImageWalker = 0;
                            }
                        }
                        //
                        // Image chunk (watch out: long message, length is at message[1] + message[2])
                        //
                        else if(message[0]==0 && message[3]=='a' && message[4] == 'c' && message[5] == 'i') {
                            // START JP
                            int size = NetTools.intFrom2Bytes(message[1], message[2]) - 6;
                            //message[0] - 4;
                            //System.out.println("aci: " + message[6]);
                            System.arraycopy(message, 6, nextImage, nextImageWalker, size);
                            //System.arraycopy(message, 7, nextImage, 500*message[6], size);
                            nextImageWalker += size;
                            if(nextImageWalker+1 >= nextImageSize) {
                                imageData = Image.createImage(nextImage, 0, nextImageSize);

                                //store image in RS
                                if(useRSwriting) {
                                    //System.out.println("to RS");
                                    tempDatabase.setBytes(filename, nextImage);
                                }

                                nextImage = null;
                            }
                            trafficCounterReceive += (size+6);
                            // END JP
                        } else {
                            this.executeMessage(message);
                        }

                    }

                    try {Thread.sleep(15);} catch (Exception e) {}
                } while(imageData == null);

            //end else check RS for image
            }

            if(!useRSwriting) {
                // if the image is not in the cache
                if(!imageCache.containsKey(filename)) {

                    // add to the cache
                    imageCacheList.insertElementAt(filename, 0);
                    imageCache.put(filename, imageData);

                    // modify the cache
                    if(imageCache.size() > MAX_CACHE_IMAGES) {
                        String fileToRemove = (String)imageCacheList.lastElement();
                        imageCacheList.removeElement(fileToRemove);
                        imageCache.remove(fileToRemove);
                    }
                }
            }
            
            // !!!
            gbManager.setSleepRead(25);
            
        } else {
            // get the image from the cache
            
            imageData = (Image)imageCache.get(filename);
        }
        
        return imageData;

            
    }
    
    private void reconnectDatabaseGfx(String filename) {
        if(filename.startsWith("back")) {
            //DON'T CLOSE , but set to null
            tempDatabase = null;
            tempDatabase = new GDataStore(filename);
            if(tempDatabase != null && tempDatabase.connect()) {
                databaseGfxBack.disconnect();
                databaseGfxBack = null;
                databaseGfxBack = tempDatabase;
            } else {
                useRSreading = false;
                useRSwriting = false;
                tempDatabase = null;
                if(databaseGfxBack != null) {
                    databaseGfxBack.disconnect();
                    databaseGfxBack = null;
                }
                if(databaseGfxEnemy != null) {
                    databaseGfxEnemy.disconnect();
                    databaseGfxEnemy = null;
                }
            }
        } else {
            //DON'T CLOSE , but set to null
            tempDatabase = null;
            tempDatabase = new GDataStore(filename);
            if(tempDatabase != null && tempDatabase.connect()) {
                databaseGfxEnemy.disconnect();
                databaseGfxEnemy = null;
                databaseGfxEnemy = tempDatabase;
            } else {
                useRSreading = false;
                useRSwriting = false;
                tempDatabase = null;
                if(databaseGfxBack != null) {
                    databaseGfxBack.disconnect();
                    databaseGfxBack = null;
                }
                if(databaseGfxEnemy != null) {
                    databaseGfxEnemy.disconnect();
                    databaseGfxEnemy = null;
                }
            }
        }
    }


    private void crypt(byte[] text) {
        for(m1 = 0; m1<text.length; m1++) {
            text[m1] = (byte)((text[m1])^(key>>8));
            m1++;
            if(m1 < text.length) {
                text[m1] = (byte)((text[m1])^(key));
            }
        }
    }

    
    // ---------------------------------------
    // 1.3 message handling, not used anymore but can provide guidance on how message flow was implemented in 1.3
    
    private boolean OLD_1_3_ExecuteMessage(byte[] message) {
    
            //------------------------


            // =================
            // SYSTEM MESSAGES
            // =================
            if(message[1] == 's') {
                // GAME SERVER (list entry)
                if (message[2] == 'g' && message[3] == 's') {
                    if (currentState == STATE_CONNECT_GET_SERVERS) {
                        m1 = message[4];
                        m2 = message[5];
                        m3 = message[6];
                        m4 = message[7];
                        
                        if (m1 < 0) {m1 = m1 + 256;}
                        if (m2 < 0) {m2 = m2 + 256;}
                        if (m3 < 0) {m3 = m3 + 256;}
                        if (m4 < 0) {m4 = m4 + 256;}
                        
                        tmpStringM = m1 + "." + m2 + "." + m3 + "." + m4;
                        //System.out.println((int)((int)0 | message[4]));
                        GTools.listAppendEntry(genericList, new String(message, 9, message[8]), tmpStringM);
                        tmpStringM = null;
                    }
                }
                //
                // GAME INFO
                //
                else if(message[2] == 'g' && message[3] == 'i') {
                    //compare with current version
                    int serverVersionLowSub = 0;
                    int serverVersionHigh = message[8];
                    int serverVersionLow = message[9]/10;
                    if (serverVersionLow == 0) {
                        serverVersionLow = message[9]%10;
                    } else {
                        serverVersionLowSub = message[9]%10;
                    }
                     
                    if (versionHigh >= serverVersionHigh && versionLow >= serverVersionLow && versionLowSub >= serverVersionLowSub) {
                        if (debugLevel > 0) {
                            //System.out.println("Using version: " + message[8] + "." + message[9] + " OK.");
                        }
                        //proceed to login / register
                        currentState = STATE_INTRO_LIST;
                        prepareListIntro();
                        setBottomCommand1("Select");
                        initGraphics();
                        //bCommand1 = true;
                        // prepare http connection
                        //getImageByHTTP("dummy.html", false);
                    } else {    //outdated version
                        if (debugLevel > 0) {
                            //System.out.println("Error: Server requires client version: " + message[8] + "." + message[9]);
                        }
                        
                        //System.out.println("Error: Server requires client version: " + message[8] + "." + message[9]);
                        
                        doLogout();
                        stopNet();
                        //bCommand1 = false;
                        //bCommand2 = false;
                        subStateOKDialog("Server requires version:\n" + serverVersionHigh + "." + serverVersionLow + "." + serverVersionLowSub + "\n\nYour version:\n"  + versionHigh + "." + versionLow + "." + versionLowSub +  "\n\nGet new version at:\nwww.rhynn.com", STATE_INTRO, SUBSTATE_ACTIVE);
                        netError = false;
                        doConnect = 0;
                    }

                }
                
                //
                // GLOBAL PLAYER (SYSTEM) LOGIN GRANTED
                //
                else if(message[2] == 'l' && message[3] == 'g') {
                    sendJoinGroupMessage(gameName);
                    return true;
                } 


                //
                // GLOBAL PLAYER (SYSTEM) LOGIN FAILED
                //
                else if(message[2] == 'l' && message[3] == 'f') {
                    currentState = STATE_LOGIN_MENU;
                    bCommand1 = true;
                    return true;
                }

                //
                // GLOBAL PLAYER (SYSTEM) LOGOUT (KICK PLAYER)
                //
                else if(message[2] == 'l' && message[3] == 'o') {
                    character_DB_ID = 0;
                    user_DB_ID = 0;
                    if (currentState!=STATE_REGISTER_NEW && currentState!=STATE_LOGIN_MENU && currentState!=STATE_INTRO_LIST) {
                        checkNet = false;
                        netStarted = false;
                        currentState = STATE_BLACK;
                        subStateOKDialog("Server disconnected.\nLogin again.\nExit.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                    }
                    return true;
                }

                
                return false;
            }
            
            // =====================
            // GROUP MESSAGES
            // =====================
            else if (message[1] == 'g') {
                //
                // JOIN GROUP OK
                //
                if (message[2] == 'j' && message[3] == 'o') {
                    if (lastJoinedGroup == null) {
                        lastJoinedGroup = gameName;
                        if (currentState == STATE_REGISTER_NEW_WAIT) {
                            //try to register
                            sendRegisterPlayerMessage(clientName, clientPass);
                        } else {
                            //try to login
                            loginFW(clientName, clientPass);
                        }
                    } else {
                        if (loadPlayfield) {
                            if (debugLevel >= 2) {
                                //System.out.println("requesting: " + playfieldID);
                            }
                            /*
                            GTools.textWindowRemoveText(highScoreWindow);
                            GTools.labelSetText(label2, "Top 5 characters:", false);
                            label2.y = gaugeWindow.y + gaugeWindow.height + 42;
                            GTools.windowCenterX(label2, 0, DISPLAYWIDTH);
                            highScoreWindow.y = label2.y + label2.height + 10;
                            */
                            // -- sendRequestPlayfieldMessage(playfieldID);
                            loadingPlayfield = true;
                            currentState = STATE_GAUGE;
                            currentSubState = SUBSTATE_NORMAL;
                            /*
                            //loading world label
                            if (playfieldName!=null && playfieldName.length()>0) {
                                GTools.labelSetText(label1, "Loading " + playfieldName, false);
                             */
                                lastJoinedGroup = playfieldName;
                                /*
                            } else {
                                GTools.labelSetText(label1, "Entering world..", false);
                            }
                            label1.y = gaugeWindow.y - font.charHeight - 2;
                            GTools.windowCenterX(label1, 0, DISPLAYWIDTH);
                                 */
                            loadPlayfield = false;
                        }
                    }
                    return true;
                } 

                //
                // LEFT GROUP
                //
                else if (message[2]=='l' && message[3]=='g') {
                    if (lastJoinedGroup!=null) {
                        prepareLoadingWorldScreen();
                        currentState = STATE_WAIT_LOAD_GFX;
                    } // else: Login or registering failed, user left FantasyWorlds group
                }
                
                return false;
            }
            
            // =====================
            // ADMIN MESSAGES
            // =====================
            else if(message[1] == 'a') {
                
                if (message[2] == 't' && message[3] == 'f' && message[4] == 'c') {
                    if (loadingPlayfield) {
                        int count = message[5];
                        specialFields = new int[count][3];
                    }
                    
                
                } else if (message[2] == 't' && message[3] == 'f' && message[4] == 'd') {
                    if (loadingPlayfield) {
                        int index = message[5];
                        if (specialFields != null && index < specialFields.length) {
                            specialFields[index] = new int[3];
                            specialFields[index][0] = NetTools.intFrom2Bytes(message[6],message[7]);
                            specialFields[index][1] = NetTools.intFrom2Bytes(message[8],message[9]);
                            specialFields[index][2] = NetTools.intFrom4Bytes(message[10],message[11],message[12],message[13]);
                        }
                    }
                    
                    
                //
                // SAVE PLAYFIELD
                //
                } else if(message[2] == 'p' && message[3] == 'd') {
                    playfieldCounter++;

                    //System.out.println("counter: " + playfieldCounter);
                    int packet = ((int)(message[4]))*playfieldWidth;
                    //set graphic + function (all 8 bits)
                    for(int i = 0; i < playfieldWidth; i++) {
                        legacyPlayfield[(i+packet)%playfieldWidth][(i+packet)/playfieldWidth] =  message[5+i];
                    }

                    /*
                    if(playfieldCounter == playfieldHeight) {
                        allowGameInput = false; //do not allow game keys until own players is added
                        currentState = STATE_GAME;
                        initGraphics();
                        initWindows();
                        sendAddMe();

                        // change sound to normal game sound
                        if (soundON && soundPossible) {
                            if (isPeaceful(playerObject.x + (PLAYERWIDTH_HALF), playerObject.y + (PLAYERHEIGHT_HALF))) {
                                playbackSound(1, -1);    // peaceful
                            } else {
                                playbackSound(0, -1);    // not peaceful
                            }
                        }
                     */
                        
                        //playfieldCounter = 0;
                        /*
                        if (firstTime) {
                            sendRequestItemsMessage(character_DB_ID);
                            firstTime=false;
                        }
                         */
                    //}

                    return true;
                }
                
                //
                // PLAYFIELD CLIENT INFO (PORTAL OK, also sent after character choose (if ok))
                //
                else if (message[2] == 'p' && message[3] == 'c' && message[4] == 'i') {
                    playfieldHelptextID = 0;
                    if (currentSubState==SUBSTATE_PORTAL_WAIT || currentState==STATE_WAIT || usingServerPortal) {
                    
                        //should be in STATE_WAIT now!
                        // remove all objects ...
                        idToCharacters.clear();
                        idToItems.clear();
                        // ... and add yourself
                        idToCharacters.put("" + character_DB_ID, playerObject);

                        int newPlayfieldID = NetTools.intFrom2Bytes(message[5], message[6]);

                        if (newPlayfieldID == playfieldID && !usingServerPortal) {
                            //move locally on same legacyPlayfield
                            //System.out.println("local: " + playfieldName + " (" + playfieldID + ") " + message[8] + " " + message[9]);
                            adjustScreen(NetTools.intFrom2Bytes(message[11], message[12]), NetTools.intFrom2Bytes(message[13], message[14]));
                            playerMove = false;
                            sendPos = false;
                            //sendMoveObjectMessage();
                            sendAddMe();
                            //currentSubState = SUBSTATE_NORMAL;
                        } else {
                            usingServerPortal = false;
                            playfieldID = newPlayfieldID;
                            // clear firewall info
                            for (n=0; n < FIREWALL_WINDOWSIZE; n++) {System.arraycopy(emptyFireWallElement, 0, fireWalls[n], 0, FIREWALL_WINDOWSIZE);}
                            // clear special fields
                            specialFields = null;
                            //move to other legacyPlayfield
                            if (message[0] > 15) {  //get playfieldname if available
                                playfieldName = new String(message, 16, message[15]);
                                playfieldServer = new String(message, 17+playfieldName.length(), message[16+playfieldName.length()]);
                            }

                            if(host.equals(playfieldServer) || clusterDisable) {
                                //System.out.println("joining: " + playfieldName + " (" + playfieldID + ") " + message[8] + " " + message[9]);
                                playerMove = false;
                                sendPos = false;
                                
                                if (playfieldName!=null && !playfieldName.equals("")) {
                                    loadPlayfield = true;
                                    setWaitLabelText("Travelling to\nworld..");
                                    currentState = STATE_WAIT;
                                    currentSubState = SUBSTATE_NORMAL;
                                    if (playerObject!=null && idToCharacters!=null) {
                                        // remove all objects ...
                                        idToCharacters.clear();
                                        // ... and add yourself
                                        idToCharacters.put("" + character_DB_ID, playerObject);
                                        if (idToItems!=null) {
                                            idToItems.clear();
                                        }
                                    }
                                    if (lastJoinedGroup!=null && !lastJoinedGroup.equals(gameName)) {
                                        // player has been in a legacyPlayfield, change back to FW group
                                        sendLeaveGroupMessage();
                                    } else {
                                        prepareLoadingWorldScreen();
                                        currentState = STATE_WAIT_LOAD_GFX;
                                    }
                                    
    
                                    // =====================
                                    n = playfieldName.length()+playfieldServer.length();
                                    //i = 18+n; //walk the message entries
                                    //j = 0; //walk the enemies
                                    
                                    m1 = 18 + n;    // first valid index
                                    m2 = m1+message[17+n]-1;    // last valid index
                                    dynamicEnemies_ToLoad_count = 0;
                                    currentDynamicEnemy_ToLoad = 0;
                                    
                                    if (m2 >= m1) {  // dynamic enemies for this legacyPlayfield are required.
                                        dynamicEnemies_ToLoad_count = m2 - m1 + 1;

                                        // copy all dynamic enemy indices
                                        dynamicEnemies_ToLoad = new byte[dynamicEnemies_ToLoad_count];
                                        System.arraycopy(message, m1, dynamicEnemies_ToLoad, 0, dynamicEnemies_ToLoad_count);
                                        
                                        currentDynamicEnemy_ToLoad = 0;
                                        mb1 = false;
                                        
                                        // clear all unused dynamic enemy graphics
                                        for (m1=0; m1<enemies.length; m1++) {
                                            mb1 = false;
                                            for (m2=0; m2<dynamicEnemies_ToLoad.length && !mb1; m2++) {
                                                if (m1==graphicSelExtract(dynamicEnemies_ToLoad[m2])) {
                                                    mb1 = true;  // quit inner loop
                                                }
                                            }
                                            if (!mb1) {
                                                // enemy graphics can be cleared
                                                enemies[m1] = null;
                                            }
                                        }
                                        System.gc();
                                    }
                                    
                                    /* $-> activate!
                                    if (soundPossible) {
                                        m5 = 18+n+message[17+n];
                                        m6 = 0;
                                        while (m5 < message.length && m6 < 3) {
                                            if (soundIDs[m6]!=message[m5]) {
                                                // new sound to load, will stopPlay current sound if necessary
                                                aquireSound(m6, message[m5]);
                                            }
                                            m6++;
                                            m5++;
                                        }
                                    }
                                     */
                                    // =====================
                                     
                                    
                                } else {
                                    subStateOKDialog("Cannot load playfield.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                                }
                                
                                //SET NEW PLAYFIELD DETAILS, CREATE NEW BYTE ARRAY IF NECCESSARY!
                                // -- setNewEmptyPlayfield(graphicSelExtract(message[7]), graphicSelExtract(message[8]), (message[9] & 0xFF), (message[10] & 0xFF));
                                GTools.labelSetText(label3, "Loading background gfx 0", false);
                                GTools.windowCenterX(label3, 0, DISPLAYWIDTH);
/*#Series40_MIDP2_0#*///<editor-fold>
//#                                 label3.y = gaugeWindow1.y - font.charHeight - 1;
/*$Series40_MIDP2_0$*///</editor-fold>
/*#!Series40_MIDP2_0#*///<editor-fold>
                                label3.y = gaugeWindow1.y - 8;
/*$!Series40_MIDP2_0$*///</editor-fold>

                                //make sure look-at position fits
                                adjustScreen(NetTools.intFrom2Bytes(message[11], message[12]), NetTools.intFrom2Bytes(message[13], message[14]));

                                // stopPlay current sound
                                /*
                                if (sound!=null && soundON) {
                                    sound.stopPlay();
                                }*/
                                
                                
                            } else {
                                //
                                // We have to switch to another server
                                //
                                usingServerPortal = true;
                                
                                // do the logout
                                doLogout();

                                // stopPlay the net
                                stopNet();

                                // Store the new host
                                host = playfieldServer;
                                
                                // Start the net again
                                initNet();
                                currentState = STATE_WAIT_FOR_CONNECT_THREAD_PORTAL;
                            }
                        }   //end move to other legacyPlayfield
                        return true;
                    }
                
                
                //
                //  FAR PORTAL DESTINATION
                //
                } else if (message[2] == 'f' && message[3] == 'p' && message[4] == 'd') {            
                    int m1 = NetTools.intFrom4Bytes(message[5],message[6],message[7],message[8]);
                    if (message[9]>0) {
                        tmpStringM = new String(message, 10, message[9]);
                    } else {
                        tmpStringM = "";
                    }
                    
                    if (currentSubState == SUBSTATE_FAR_PORTAL_WAIT) {
                        currentSubState = SUBSTATE_FAR_PORTAL_LIST;
                        setBottomCommand1("Goto");
                        setBottomCommand2("Cancel");
                        GTools.listRemoveAllEntries(bigList);
                        GTools.menuSetCaptionOneLine(menuBigList, "Far Portal Jump", font, 0);
                        GTools.menuSetItem(menuBigList, bigList, 0);
                    }
                    
                    if (currentSubState == SUBSTATE_FAR_PORTAL_LIST) {
                        GTools.listAppendEntry(bigList, tmpStringM, new Integer(m1));
                    }
                    
                }
                return false;
            }
            
            // =====================
            // FANTASY WORLD SPECIFIC
            // =====================
            else if(message[1] == 'f') {
                //
                // LOGIN GRANTED TO FANTASY WORLDS
                //
                if(message[2] == 'l' && message[3] == 'g') {
                    //System.out.println("flp: login granted (FW)");
                    // Store the name and password for easy login at next startup
                    if (clientName!=null && clientPass!=null) {
                        database.setValue("clientname", clientName.trim());
                        database.setValue("clientpass", clientPass.trim());
                    }

                    // Save USER DB ID (!=gameobject DB ID, gameobject DB ID is determined via the character on "character selected" (table gameobjects)
                    user_DB_ID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    if (debugLevel >= 2) {
                        //System.out.println("FW login granted, user DB_ID: " + user_DB_ID);
                    }
                    
                   int initValue = NetTools.intFrom4Bytes(message[16], message[17], message[18], message[19]);
                   packetValidator.initialize(initValue);

                    if(!usingServerPortal) {
                        // send back the challenge number
                        byte[] challengeNumber = new byte[8];
                        System.arraycopy(message, 8, challengeNumber, 0, 8);
                        crypt(challengeNumber);
                        sendChallengeNumber(challengeNumber);
                        doConnect = 0;

                        if (justRegistered) {
                            //justRegistered = false;
                            overlayMessage("Congratulations!\nYour account has been created!\n\nYou may now enter your e-mail address.\nThis step is optional.");
                            changeEmail();
                        } else {
                            prepareRequestCharacters();
                        }
                    } else {
                        //
                        // Portal Jump
                        //
                        currentState = STATE_WAIT;
                        sendChooseCharacterMessage(character_DB_ID);
                    }
                    return true;
                }

                //
                //LOGIN DENIED
                //
                else if (message[2] == 'l' && message[3] == 'd') {
                    //checkNet = false;
                    //doLogout();
                    currentState = STATE_INTRO_LIST;
                    setBottomCommand1("Select");
                    character_DB_ID = 0;
                    lastJoinedGroup = null;
                    sendLeaveGroupMessage();
                    if (message[0] > 5) {
                        // description available, show it
                        tmpStringM = new String(message, 6, message[5]);
                    }

                    /*
                    setBottomCommand1("Login");
                    setBottomCommand2("Back");
                    currentState = STATE_LOGIN_MENU;
                    */
                    if (message[4]>=-1) {   // invalid username / password combination
                        subStateOKDialog("Login failed.\n\nIf you forgot your password, select the 'Forgot Password' option.", STATE_INTRO_LIST, SUBSTATE_NORMAL);
                        bCommand1 = true;
                        //overlayMessageTimeoutControls("Login failed.", 1500);
                        
                    } else if (message[4]==-2) {    // account expired
                        if (message[0] > 5) {
                            // description available, show it
                            subStateOKDialog(tmpStringM, STATE_SUBSCRIBE_NEW, SUBSTATE_NORMAL);
                            tmpStringM = null;
                            
                            // save user / pass to db as user exists
                            if (clientName!=null && clientPass!=null) {
                                database.setValue("clientname", clientName.trim());
                                database.setValue("clientpass", clientPass.trim());
                            }
                            
                            prepareContextMenu(3); // set options: help, exit

                            setBottomCommand1("Options");
                            bCommand2 = false;
                            //setBottomCommand2("Exit");
                            
                            
                            //RESAMPLE TEXT EDIT BOX TO ALLOW PHONE NUMBER INPUT
                            editBoxInput.numeric = true;
                            GTools.textWindowSetMaxChars(editBoxInput, 24);
                            editBoxInput.height += 6;
                            GTools.textWindowCalculateTextSettings(editBoxInput, font);

                            label2.centerTextH =  false;
                            label2.innerOffset = 1;
                            label2.borderSize = 1;
                            label2.borderColor = 0x999999;
                            GTools.labelSetText(label2, "Enter your phone number.\n\nBe sure to include your country code (for example: 1 for the U.S., 49 for Germany etc.)", false);
                            GTools.windowCenterX(label2, 0, DISPLAYWIDTH);
                            m3 = label2.height + 6 + editBox.height;
                            m4 = ((DISPLAYHEIGHT - m3) / 2);
                            label2.y = m4;
                            editBox.y = m4 + label2.height + 6 + 6;
                            
                            GTools.menuSetCaptionOneLine(editBox, "Enter phone number", font, 0);
                            //GTools.menuRemoveAllItems(editBox);
                            GTools.menuSetMaxItems(editBox, 2);

                            GTools.labelSetText(label3, "+", false);
                            GTools.windowSetPosition(label3, 3, 7);

                            editBoxInput.xOffset = 0; editBoxInput.yOffset = 0; // important
                            GTools.windowSetPosition(editBoxInput, 10, 5);

                            GTools.menuSetItem(editBox, label3, 0);
                            GTools.menuSetItem(editBox, editBoxInput, 1);

                            GTools.menuSetSelected(editBox, 1);
                            GTools.menuEnsureContainAll(editBox, false, true);

                            
                            GTools.windowCenterX(editBox, 0, DISPLAYWIDTH);
                            //GTools.windowCenterXY(editBox, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);

                        } else {
                            subStateOKDialog("Your account has expired!\nSee\n\nwww.awaredreams.com\n\nfor information on how to renew your account.", STATE_SUBSCRIBE_NEW, SUBSTATE_NORMAL);
                        }
                        bCommand1 = true;
                    } else if (message[4]==-3) {
                        //System.out.println("GOT -3");
                        if (message[0] > 5 ) {
                            // subscription in progress
                            subStateOKDialog(tmpStringM, STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                        } else {
                            subStateOKDialog("Subscription in progress.\n\n If you do not receive the billing SMS, please see www.awaredreams.com on how to proceed.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                        }
                        // save user / pass to db as user exists
                        if (clientName!=null && clientPass!=null) {
                            database.setValue("clientname", clientName.trim());
                            database.setValue("clientpass", clientPass.trim());
                        }
                        
                        doLogout();
                        stopNet();
                    }
                    return true;
                }

                //
                // GLOBAL PLAYER (SYSTEM) LOGOUT (KICK PLAYER)
                //
                else if(message[2] == 'l' && message[3] == 'l') {
                    character_DB_ID = 0;
                    user_DB_ID = 0;
                    currentState = STATE_BLACK;
                    subStateOKDialog("Someone else has logged in with your user name.\nExit.", STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                    sendLogout = false;
                    return true;
                }
                
                
                //
                // REGISTER player SUCCESS
                //
                else if (message[2] == 'r' && message[3] == 's') {
                    proceedToLoginAfterRegister();
                    return true;
                }

                //
                // REGISTER player FAILED
                //
                else if (message[2] == 'r' && message[3] == 'f') {
                    //checkNet = false;
                    //doLogout();
                    GTools.menuSetSelected(menuLogin, 1);
                    setBottomCommand1("Register");
                    setBottomCommand2("Back");
                    currentState = STATE_REGISTER_NEW;
                    setWaitLabelText("Remember the\npassword well!\nIt may only be\nrecovered by\ne-mail!");
                    labelWait.centerTextH = false;
                    labelWait.x = passwordWindow.x;
                    labelWait.y = passwordWindow.y + passwordWindow.height;

                    character_DB_ID = 0;
                    lastJoinedGroup = null;
                    sendLeaveGroupMessage();
                    overlayMessageTimeoutControls("User name already taken.", 1500);
                    /*
                    currentState = STATE_INTRO_LIST;
                    setBottomCommand1("Select");
                    character_DB_ID = 0;
                    lastJoinedGroup = null;
                    sendLeaveGroupMessage();
                    subStateOKDialog("User name already taken.", STATE_INTRO_LIST, SUBSTATE_NORMAL);
                    */
                    return true;
                }

                // SUBSCRIPTION STATUS
                else if (message[2] == 's' && message[3] == 's') {
                    if (currentState == STATE_SUBSCRIBE_WAIT_FOR_RESPONSE) {
                        bCommand1 = false;
                        setBottomCommand2("Exit");
                        // get and display description
                        tmpStringM = new String(message, 6, message[5]);
                        subStateOKDialog(tmpStringM, STATE_SUBSCRIBE_DONE_EXIT, SUBSTATE_NORMAL);
                        currentState = STATE_SUBSCRIBE_DONE_EXIT;
                        doLogout();
                        stopNet();
                        tmpStringM = null;
                    }
                }
                
                //
                // NEW CHARACTER WILL FAIL
                //
                else if (message[2] == 'c' && message[3] == 'f') {
                    if (currentState == STATE_WAIT) {
                        m1 = message[4];    //number of max characters allowed for this user
                        subStateOKDialog("Action failed.\nMax. characters: " + m1, STATE_CHARACTER_SELECT, SUBSTATE_NORMAL);
                        setBottomCommand1("Options");
                        setBottomCommand2("Game");
                    }
                    return true;
                }

                //
                // NEW CHARACTER WILL BE OK
                //
                else if (message[2] == 'c' && message[3] == 'o') {
                    //genericList.drawSelection = true;
                    genericList.activeBackColor = 0x990000;
                    currentState = STATE_CHARACTER_SELECT;
                    currentSubState = SUBSTATE_CHARACTER_NEW;
                    GTools.inputWindowRemoveText(editBoxInput);
                    GTools.menuSetCaptionOneLine(editBox, "Character name", font, 0);
                    setBottomCommand1("Select");
                    setBottomCommand2("Cancel");
                    addCharacterOK = false;
                    initWindows();
                    return true;
                }
                
                
                
                //
                // ADD CHARACTER
                //
                else if(message[2] == 'c' && message[3] == 'a') {
                    if (!addCharacterOK)
                        return true;    //skip adding if current state is not suited for it

                    Character c = new Character();
                    
                    c.objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    c.subclassId = message[8];   //subtype (human, dwarf, elf, orc, ..)

                    c.playfieldId = NetTools.intFrom2Bytes(message[9], message[10]);
                    c.x = NetTools.intFrom2Bytes(message[11], message[12]);
                    c.y = NetTools.intFrom2Bytes(message[13], message[14]);
                    
                    c.graphicsX = message[15];
                    c.graphicsY = message[16];

                    c.curHealth = NetTools.intFrom2Bytes(message[17], message[18]);
                    
                    c.healthBase = NetTools.intFrom2Bytes(message[19], message[20]);
                    c.healthregenerateBase = message[21];
                    
                    c.curMana = NetTools.intFrom2Bytes(message[22], message[23]);
                    c.manaBase = NetTools.intFrom2Bytes(message[24], message[25]);
                    c.manaregenerateBase = message[26];
                    
                    c.attackBase = NetTools.intFrom2Bytes(message[27], message[28]);
                    c.defenseBase = NetTools.intFrom2Bytes(message[29], message[30]);
                    c.skillBase = NetTools.intFrom2Bytes(message[31], message[32]);
                    c.magicBase = NetTools.intFrom2Bytes(message[33], message[34]);
                    c.damageBase = NetTools.intFrom2Bytes(message[35], message[36]);
                    
                    c.gold = NetTools.intFrom4Bytes(message[37], message[38], message[39], message[40]);

                    c.level = message[41];
                    c.levelpoints = NetTools.intFrom2Bytes(message[42], message[43]);
                    c.experience = NetTools.intFrom4Bytes(message[44], message[45], message[46], message[47]);
                    
                    c.name = new String(message, 50, message[48]);
                    c.description = new String(message, 50 + message[48], message[49]);
                    
                    // ASSIGN AUTO FIELDS:
                    c.ownerId = user_DB_ID;
                    c.classId = 0; //player's character
                    c.graphicsel = 0; //always use static tileset for character
                    c.graphicsDim = 4;

                    //add to the character list
                    GTools.listAppendEntry(genericList, c.name, c);
                    // -- GTools.listSetIconForEntry(genericList, genericList.entries.size()-1, c.subclassId);

                    //printCharacterDetails(c);
                    
                    c = null;
                    return true;
                }
                
                //
                // CHARACTER DELETED
                //
                else if(message[2] == 'c' && message[3] == 'd') {
                    if (currentSubState==SUBSTATE_CHARACTER_DELETE_WAIT) {
                        currentSubState = SUBSTATE_NORMAL;
                        bCommand1 = true;

                        // remove character locally
                        int selIndex = GTools.listGetSelectedIndex(genericList);
                        GTools.listRemoveEntry(genericList, selIndex);
                        
                        // --initWindows();
                        //addCharacterOK = true;
                        // -- sendRequestCharactersMessage();
                        return true;
                    }
                }


                //
                // [M]ODIFIERS FOR [A]TTRIBUTES
                //
                else if(message[2] == 'm' && message[3] == 'a') {
                    // received after sendCharacterLoad f_cl
                    
                    atrCHR_Modifiers[0] = message[4];   // curHealth
                    atrCHR_Modifiers[1] = message[5];   // curMana
                    atrCHR_Modifiers[2] = message[10];   // damageBase
                    atrCHR_Modifiers[3] = message[6];   // attackBase
                    atrCHR_Modifiers[4] = message[7];   // defenseBase
                    atrCHR_Modifiers[5] = message[8];   // skillBase
                    atrCHR_Modifiers[6] = message[9];   // magicBase

                    // LATER: when players are allowed to go back to the character selection screen at any time,
                    // the inventory must be cleared first!
                    sendRequestItemsMessage(character_DB_ID);
                    // -- requestInventory = false;
                    
                    return true;
                }
                
                
                //
                // NUMBER OF [U]SERS [O]NLINE
                //
                else if(message[2] == 'u' && message[3] == 'o') {
                    /*
                    usersOnline = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    usersTotal = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
                    if (!(usersTotal>0) || usersOnline > usersTotal) {
                        usersTotal = 0;
                        usersOnline = 0;
                    } else {
                        GTools.labelSetText(label2, "Players online:\n\n" + usersOnline + " of " + usersTotal, false);
                        label2.y = gaugeWindow.y + gaugeWindow.height + 19;
                        GTools.windowCenterX(label2, 0, DISPLAYWIDTH);                            
                    }
                     */
                    return true;
                }

                //
                // [H]IGHSCORE [L]IST ENTRY
                //
                else if(message[2] == 'h' && message[3] == 'l') {
                    int m1, m2;
                    
                    for (m1=0; m1<10; m1++) {     // copy name
                        if (m1<message[11]) {
                            highscoreText[3 + m1] = (char)message[12 + m1];
                        } else {
                            highscoreText[3 + m1] = ' ';
                        }
                    }
                    
                    
     
                    // copy rank
                    replaceNumber(highscoreText, message[6], 0, 0);
                    // get experience
                    m2 =NetTools.intFrom4Bytes(message[7], message[8], message[9], message[10]);
                    // copy experience
                    replaceNumberLeftAlign(highscoreText, m2, 15, 20, false);
                    /*
                    // copy level
                    replaceNumber(highscoreText, message[5], 24, 25);
                     */

                    // message[4] subclassId
                    
                    // add the whole line to the highscore window
                    GTools.textWindowAddText(highScoreWindow, highscoreText);
                    return true;
                }
                
                
                //
                // ADD OBJECT (also received for own player)
                //
                else if(message[2] == 'a' && message[3] == 'o') {
                    int classid = message[8];
                    int objectid = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    /*System.out.println("AO nd: " + new String(message, 29, message[27]) + " " + new String(message, 29 + message[27], message[28]));
                    System.out.println("AO xy: " + NetTools.intFrom2Bytes(message[18], message[19]) + " " + NetTools.intFrom2Bytes(message[20], message[21]));

                    System.out.println("numObjects: " + idToCharacters.size() + " + " + "numObjects: " + idToItems.size() + " = " + (idToCharacters.size() + idToItems.size()));
                    */
                    if (classid < 2) {  // CHARACTER
                        // See, if it is the own player
                        if(objectid == character_DB_ID) {
                            //$->CHANGE (?) only if on new legacyPlayfield
                            playerSpeed = GlobalSettings.PLAYER_SPEED_PER_FRAME;
                            playerObject.x = NetTools.intFrom2Bytes(message[18], message[19]);
                            playerObject.y = NetTools.intFrom2Bytes(message[20], message[21]);

//System.out.println("RECEIVED CA: " + NetTools.intFrom2Bytes(message[23], message[24]) + "/" + NetTools.intFrom2Bytes(message[25], message[26]));
                            
                            adjustScreen(playerObject.x, playerObject.y);
                            // --direction = playerObject.direction;
                            ownName = ((playerObject.name).toLowerCase()).toCharArray();
                            allowGameInput = true;  //own player now added, receive game input
                            idToCharacters.put("" + playerObject.objectId, playerObject);
                            
                            // request the inventory if necessary
                            /*
                             // in previous versions the inventory was requested after the character
                             // had entered the legacyPlayfield, this is now done on character selection
                            if (requestInventory) {
                                sendRequestItemsMessage(character_DB_ID);                   
                                requestInventory = false;
                            }
                             */
                            if (requestOpenQuests) {
                                requestOpenQuests();
                                requestOpenQuests = false;
                            }
                            
                            subStateNormal();
                            if (playfieldHelptextID > 0) {
                                // fetch help for legacyPlayfield if available
                                sendRequestNextHelpText(playfieldHelptextID);
                                playfieldHelptextID = 0;
                            }
                            
                            
                            //currentSubState = SUBSTATE_NORMAL;                            
                            
//#if !(Series40_MIDP2_0)
                            // set the level of the player in the display
                            replaceNumber(playerLevelWindow.text, playerObject.level, 0, 1);
                            // set the experience points
                            replaceNumber(playerExperienceWindow.text, playerObject.experience, 2, 7);
                            // set the gold points
                            replaceNumber(playerGoldWindow.text, playerObject.gold, 2, 7);
//#else
//#                             // set the gold points
//#                             replaceNumber(playerGoldWindow.text, playerObject.gold, 0, 5);
//#endif
                            // set the limit for the next level
                            experiencePlusForNextLevel = (((playerObject.level + 1) * (playerObject.level + 2) * 100)) - (((playerObject.level) * (playerObject.level + 1) * 100));
                            experienceCurOffset = playerObject.experience - (((playerObject.level) * (playerObject.level + 1) * 100));
                            
                        } else if (currentSubState!=SUBSTATE_PORTAL_WAIT) {
                            Character c = new Character();
                            c.objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                            c.classId = message[8];
                            c.subclassId = message[9];
                            c.triggertype = NetTools.intFrom4Bytes(message[10], message[11], message[12], message[13]);
                            c.graphicsel = graphicSelExtract(message[14]);    // make sure this is interpreted as an unsigend value
                            c.graphicsX = message[15];
                            c.graphicsY = message[16];
                            c.graphicsDim = message[17];
                            c.x = NetTools.intFrom2Bytes(message[18], message[19]);
                            c.y = NetTools.intFrom2Bytes(message[20], message[21]);
                            c.level = message[22];
                            
                            //c.curHealth = message[22];
                            
                            c.curHealth = NetTools.intFrom2Bytes(message[23], message[24]);
                            c.healthBase = NetTools.intFrom2Bytes(message[25], message[26]);
                            
                            c.name = new String(message, 29, message[27]);
                            if (message[28] > 0) {
                                c.description = new String(message, 29 + message[27], message[28]);
                            } else {
                                c.description = "";
                            }
                            c.direction = DirectionInfo.DOWN;
                            if (idToCharacters.containsKey("" + c.objectId)) {
                                idToCharacters.remove("" + c.objectId);
                            }
                            
                            //ADD TO HASH
                            // TODO: if max chars reached -> notify server that this object could not be added
                            //if (idToCharacters.size() > MAX_HASH_CHARACTERS)
                            idToCharacters.put("" + c.objectId, c);
                            
                            /*if(c.graphicsel>-1 && enemies[c.graphicsel] == null ) {
                                enemies[c.graphicsel] = getImageByHTTP("enemy" + c.graphicsel + ".png", true);
                            }*/
                        }
                    } else if (currentSubState!=SUBSTATE_PORTAL_WAIT) {    // ITEM
                        itemTmpM = new Item();
                        itemTmpM.objectId = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                        itemTmpM.classId = message[8];
                        itemTmpM.subclassId = message[9];
                        /*
                        //if(itemTmpM.classId == 12)
                            System.out.println("id: " + itemTmpM.objectId);
                         */
                        itemTmpM.triggertype = NetTools.intFrom4Bytes(message[10], message[11], message[12], message[13]);
                        itemTmpM.graphicsel = message[14];
                        itemTmpM.graphicsX = message[15];
                        itemTmpM.graphicsY = message[16];
                        itemTmpM.graphicsDim = message[17];
                        itemTmpM.x = NetTools.intFrom2Bytes(message[18], message[19]);
                        itemTmpM.y = NetTools.intFrom2Bytes(message[20], message[21]);
                        // no level for items, so ignore message[20]
                        if (message[27] > 0) {
                            itemTmpM.name = new String(message, 29, message[27]);
                        } else {
                            itemTmpM.name = "";
                        }
                        if (message[28] > 0) {
                            itemTmpM.description = new String(message, 29 + message[27], message[28]);
                        } else {
                            itemTmpM.description = "";
                        }
                        
                        
                        // ADD TO HASH
                        if (idToItems.containsKey("" + itemTmpM.objectId)) {
                            idToItems.remove("" + itemTmpM.objectId);
                        }
                        idToItems.put("" + itemTmpM.objectId, itemTmpM);
                        
                        /*
                         if (idToTempDroppedItems.containsKey("" + itemTmpM.objectId)) {
                            idToTempDroppedItems.remove("" + itemTmpM.objectId);
                        }*/
                        
                        itemTmpM = null;
                    }                    
                    
                    return true;
                    
                }

                //
                // REMOVE OBJECT
                //
                else if(message[2] == 'r' && message[3] == 'o') {


                    if (message[8] < 2) {   // character
                        removeCharacter(NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]));
                    } else {    // item
                        itemTmpM = (Item)idToItems.get("" + NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]));
                        // just remove from hash
                        idToItems.remove("" + NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]));
                    }
                    return true;
                }                 

                
                //
                // MOVE OBJECT
                //
                else if(message[2] == 'm' && message[3] == 'o') {
                    int objectid = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
           
                    Character c = (Character)idToCharacters.get("" + objectid);
                    if (c!=null) {
                        c.x = (short)NetTools.intFrom2Bytes(message[9], message[10]);
                        c.y = (short)NetTools.intFrom2Bytes(message[11], message[12]);
                        c.direction = message[8];
                        // -- c.animation = (byte)((c.animation + 1)%2);
                        
                        if (c==playerObject) {
                            // force own character to move!
                            adjustScreen(playerObject.x, playerObject.y);
                            // others are informed by server
                            playerMove = false;
                        } else if (message[0] == 17) {
                            // new curHealth / healthBase info was sent, update accordingly
                            c.curHealth = NetTools.intFrom2Bytes(message[13], message[14]);
                            c.healthBase = NetTools.intFrom2Bytes(message[15], message[16]);
                        }
                    } else if (currentSubState!=SUBSTATE_PORTAL_WAIT) {
                        // object not visible, but move object received get it!
                        // sendMessageGetObject(objectId);
                    }
                    c = null;
                    return true;
                }

                //
                // [H]EALTH [I]NFO
                //
                else if (message[2] == 'h' && message[3] == 'i') {
                    int objectid = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    Character c = (Character)idToCharacters.get("" + objectid);
                    if (c!=null) {
                        // new curHealth / healthBase info was sent, update accordingly
                        c.curHealth = NetTools.intFrom2Bytes(message[8], message[9]);
                        if (c!=playerObject) {
                            c.healthBase = NetTools.intFrom2Bytes(message[10], message[11]);
                            /*if (c.healthBase > c.curHealth) {
                                c.curHealth = c.healthBase;
                            }
                             */
                            if (c.curHealth > c.healthBase) {
                                c.curHealth = c.healthBase;
                            }
                        } else {
                            if (c.curHealth > maxhealth) {
                                c.curHealth = maxhealth;
                            }
                        }
                    }
                    c = null;
                    return true;
                }
                
                //
                // [i]nventory list end
                //
                else if (message[2]=='i' && message[3]=='0') {
                    /*
                    requestOpenQuests();
                    currentState = STATE_OPEN_QUESTLIST_LOAD_WAIT;
                     */
                    // reset the packet per loop rate to default
                    PACKETPERLOOP = DEFAULT_PACKETPERLOOP;
                    setWaitLabelText("Loading Friend List ..");
                    
                    currentState = STATE_FRIEND_RECEIVE_LIST_WAIT;
                    sendRequestFriendListMessage();
                    
                    return true;

                //
                // [f]riend List End
                //
                } else if (message[2]=='f' && message[3]=='0') {
                    currentState = STATE_WAIT;
                    setWaitLabelText("Preparing World ..");
                    // this trigger the world loading procedure
                    //sendChooseCharacterMessage(character_DB_ID);
                    sendEnterWorldMessage();

                        
                //
                // [A]DD [I]TEM TO INVENTORY
                //
                } else if (message[2] == 'a' && message[3] == 'i') {
                    
                    int itemID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    
                    int itemReplace = -1;
                    
                    for (m1=0; m1<invItemsCount; m1++) {
                        if (invItems[m1]!=null && invItems[m1].objectId == itemID) {
                            //item already exists in Inventory -> remove
                            //removeItemFromInventory(m1, -1, false, false);
                            unequip(invItems[m1], false, false);  // make sure item is unequipped
                            itemReplace = m1;
                            break;
                        }
                    }
                    
                    //System.out.println("fai: add item");
                    if (invItemsCount < INVENTORY_COLS*INVENTORY_ROWS || itemReplace > -1) {
                        //create new object
                        itemTmpM = new Item();
                        //fill object

                        fillItemFromMessage(itemTmpM, message);

                        if (message[50] > 0) {
                            if (message[50] == 1) {
                                // -- equip(itemTmpM, false, false);
                            } else if (message[50] > 1 && message[50]-2 < MAX_BELT_ITEMS) {
                                // equipped value > 1 indicates a belt slot (belt index = equipped-2)
                                // server does not need to be notified, as this is a server initiated message 
                                // which is caused at initial player placement in the world, a time at which 
                                // inventory and belt are already stored on the server when these messages are sent
                                addToBelt(itemTmpM, message[50]-2, false);
                            }
                        }
                        
                        
                        itemTmpM.equipped = message[50];

                        //AUTO VALUES
                        itemTmpM.ownerId = character_DB_ID;

                        if (itemReplace > -1) { // replace
                            atDisplay_Item = null;  // make sure the attribute display is updated
                            invItems[itemReplace] = itemTmpM;
                        } else {    // add
                            addItemToInventory(itemTmpM, true);
                        }
                        
//System.out.println("ITEM: " + itemTmpM.classId + " . " + itemTmpM.subclassId + " - " + itemTmpM.name);
                        if (currentState==STATE_GAME && itemReplace == -1) {
                            showBottomInfo("Added item: " + itemTmpM.name, 8000, true);
                        } else if (currentState == STATE_INVENTORY_LOAD_WAIT) {
                            setWaitLabelText("     Added item:     \n" + itemTmpM.name);
                        }
//System.out.println("Added item: " + itemTmpM.name);
                        
                        
                        //System.out.println("GOT ITEM SELL: " + itemTmpM.unitsSell);
                        itemTmpM = null;
                    }

                    return true;
                } 
                
                
                //
                // [A]DD ITEM [F]AILED
                //
                else if (message[2] == 'a' && message[3] == 'f') 
                {
                    if (((message[8]&1)==1) && (currentSubState == SUBSTATE_NORMAL)) {
                        if((message[8]&2)==2) {
                            overlayMessage("Can't take quest item.");
                        } else {
                            overlayMessage("Inventory full!");
                        }
                    }
                    
                    if (debugLevel >= 2) {
                        //System.out.println("add item failed.");
                    }
                    return true;
                }
                
                
                
               
                //
                // [C]LEAR [I]TEM FROM INVENTORY
                //
                else if (message[2] == 'c' && message[3] == 'i') {
                    int itemID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    int gold = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
                    int amountLeft = NetTools.intFrom2Bytes(message[12], message[13]);

                    // increase gold in any case
                    if (gold > 0) {
                        playerObject.gold += gold;
                        if (playerObject.gold > 999999) {
                            playerObject.gold = 999999;
                        }
//#if Series40_MIDP2_0
//#                         replaceNumber(playerGoldWindow.text, playerObject.gold, 0, 5);
//#else
                        replaceNumber(playerGoldWindow.text, playerObject.gold, 2, 7);
//#endif
                    }
                    
                    mb1 = false;
                    for (int i=0; i<invItemsCount; i++) {
                        if (invItems[i]!=null && invItems[i].objectId == itemID) {  // found the item in the invemtory
                            if (i == selectedInvItem) { // special treatment, if the current item was removed
                                if (((currentSubState==SUBSTATE_SET_ITEMOFFER || currentSubState==SUBSTATE_SET_DROPITEM_AMOUNT || currentSubState==SUBSTATE_BELT_SELECT_SLOT) && amountLeft==0) || currentSubState==SUBSTATE_INVITEM_OPTIONS ||currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) {
                                    if (currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) {
                                        currentSubState = triggerOrGroundFindReturnState;
                                    } else {
                                        currentSubState = SUBSTATE_INVENTORY;
                                    }
                                    resetInventoryScrollHugeItemSettings();
                                    setBottomCommand1("Select");
                                    setBottomCommand2("Close");
                                    if (currentSubState!=SUBSTATE_INVITEM_OPTIONS) {
                                        overlayMessage("Item was removed!");
                                    }
                                    //subStateOKDialog("Item was removed!", currentState, SUBSTATE_INVENTORY);
                                }
                            }
                            /*
                            info1Line_DisplayTime = 5000;
                            GTools.textWindowSetText(info1Line, "Sold: " + invItems[i].name);
                             */
                            int curAmount = invItems[i].units;
                            if (curAmount < 0) {
                                curAmount = 1;
                            }
                            int amountRemove = curAmount - amountLeft;
                            if (amountRemove <= 0) {
                                amountRemove = -1;
                            }
                            
                            if (gold > 0) {
                                showBottomInfo("Item was bought:\n" + invItems[i].name + "(" + gold +  " G)", 8000, false);
                                if (amountLeft > 0) {
                                    // item was bought and will not be completely removed -> cancel trade!
                                    invItems[i].gold = 0;
                                    invItems[i].unitsSell = 0;
                                }
                            }
                            
                            removeItemFromInventory(i, amountRemove, false, false);  // no message to server in case of item unequip (server knows already)
                            break;
                        }
                    }
                    
                    
                    return true;
                    
                }
                
                
                //
                // HIT PLAYER
                //
                else if (message[2] == 'h' && (message[3] == 'p' || message[3] == 'f')) {
                    
                    int attackerID = 0;
                    int targetID = 0;

                    if (message[3] == 'f') {    // fire wall, no attacker is sent along with this message
                        targetID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    } else {    // usual attackBase
                        attackerID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                        targetID = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);
                    }
                    Character target = (Character)idToCharacters.get("" + targetID);
                    Character attacker = null;
                    if (attackerID != 0) {
                        attacker = (Character)idToCharacters.get("" + attackerID);
                    }
                    
                    
                    if (target!=null) {
                        int hitvalue;
                        if (message[3] == 'f') {    // fire wall
                            hitvalue = NetTools.intFrom2Bytes(message[8], message[9]);
                            target.curHealth = NetTools.intFrom2Bytes(message[10], message[11]);
                            if (targetID != character_DB_ID) {
                                target.healthBase = NetTools.intFrom2Bytes(message[12], message[13]);
                            }
                        } else {

                            hitvalue = NetTools.intFrom2Bytes(message[12], message[13]);
                            target.curHealth = NetTools.intFrom2Bytes(message[14], message[15]);
                            if (targetID != character_DB_ID) {
                                target.healthBase = NetTools.intFrom2Bytes(message[16], message[17]);
                            }
                        }

                        
                            /*
                            if (soundON && soundPossible) {
                                 // $-> activate!
                                if (curSoundType!=2 && !isPeaceful((playerObject.x) + (PLAYERWIDTH_HALF), (playerObject.y) + (PLAYERHEIGHT_HALF))) {
                                    playbackSound(2, 12000);
                                } else {
                                    nextAutoSoundTypeChange = 12000;
                                }
                                 
                            }*/
                            if (attacker==null && attackerID != 0 && targetID == character_DB_ID) {
                                // attacker not in range, but hit received -> get attacker!
                                sendMessageGetObject(attackerID);
                            } else if (attacker != null) {    // attacker is visible
                                // show icon at attacker
                                if (target.objectId == playerObject.objectId || (attacker.classId == 0 && attacker.objectId != playerObject.objectId)) { // own player was hit or a PC hit someone
                                    attacker.extraIconShowDuration = MAX_HITSHOWDURATION / 2;
                                    attacker.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                                    attacker.extraFlashPhase = true;
                                    attacker.extraicon = ICON_ATTACK;
                                    if (attackerID != playerObject.objectId) {
                                        // enemey, show simple attackBase animation
                                        attacker.attackAnimate = 2;
                                    }
                                }
                        }
                        

                        
                        target.hitShowDuration = MAX_HITSHOWDURATION;
                        target.flashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                        target.flashPhase = true;

                        if (hitvalue > 0) {
                            // -- target.icon = ICON_HIT;
                            if (targetID != character_DB_ID && (message[0] > 18 || (message[3]=='f' && message[0] > 14))) {
                                if (message[3]=='f') {
                                    target.hitDisplayDelay = message[14] * 32;
                                } else {
                                    target.hitDisplayDelay = message[18] * 64;
                                }
                            } else {
                                target.hitDisplayDelay = 0;
                            }
                        } else {
                            // -- target.icon = SPRITE_ICON_DEFEND;
                        }
                        
                    }
                    return true;
                }
                
                
                //
                // CHARACTER KILLED
                //
                else if(message[2] == 'c' && message[3] == 'k') {
/*
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {}
 */                   
                    Character c = (Character)idToCharacters.get("" + NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]));
                    
                    if (c!=null) {
                        c.extraicon = -1;
                        if (c.objectId != playerObject.objectId) {
                            removeCharacter(c.objectId);    // make sure the character is removed
                            //someone else was killed
                            c.curHealth = 0;
                            c.hitShowDuration = MAX_HITSHOWDURATION/3;
                            c.flashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION/6;
                            c.flashPhase = true;
                            // -- c.icon = ICON_HIT;
                            c.hitDisplayDelay = 0;
                            // add to dead characters
                            if (deadBodyCount < MAX_NUMDEADBODIES) {
                                deadBodyCount++;
                            }
                            // Shift bodies
                            for (m1=deadBodyCount; --m1>0; ) {
                                deadCharacters[m1] = deadCharacters[m1-1];
                            }
                            deadCharacters[0] = c;
                            c = null;
                        } else {
                            // player himself was killed
                            allowGameInput = false;
                            playerMove = false;
                            playerObject.curHealth = 0;
                            currentState = STATE_BLACK;
                            overlayState = OVERLAY_DIED;
                            GTools.labelSetText(confirmWindow, "You died", false);
                            GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                            setOverlayCommand1("REVIVE");
                            ovCommand2 = false;
                            // clear fire walls
                            for (n=0; n < FIREWALL_WINDOWSIZE; n++) {System.arraycopy(emptyFireWallElement, 0, fireWalls[n], 0, FIREWALL_WINDOWSIZE);}
                        }
                    }
                }
                
                //
                // PLAYER RESPAWN
                //
                else if (message[2] == 'p' && message[3] == 'r') {
                    currentState = STATE_GAME;
                    subStateNormal();
                    // clear hashes
                    idToCharacters.clear();
                    idToItems.clear();
                    // clear equipment
                    // System.out.println("at respawn palyerobject.healthBase" + playerObject.healthBase);
                    
                    // -- clearEquipment(false);
                    
                    // System.out.println("at respawn after clear equ. palyerobject.healthBase" + playerObject.healthBase);
                    
                    playerObject.experience = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    experiencePlusForNextLevel = 0;
                    experienceCurOffset = 0;

                    // System.out.println("before respawn: curHealth:" + playerObject.curHealth + " curMana: " + playerObject.curMana + "(" + healthBase + ", " + manaBase + ")");
                    
                    playerObject.curHealth = NetTools.intFrom2Bytes(message[8], message[9]);
                    playerObject.curMana = NetTools.intFrom2Bytes(message[10], message[11]);
                    
                    // System.out.println("after respawn: curHealth:" + playerObject.curHealth + " curMana: " + playerObject.curMana + "(" + healthBase + ", " + manaBase + ")");
                    // System.out.println("after respawn palyerobject.healthBase" + playerObject.healthBase);
                    
                    // requestInventory = true;
//#if !(Series40_MIDP2_0)
                    replaceNumber(playerExperienceWindow.text, playerObject.experience, 2, 7);
                    replaceNumber(playerGoldWindow.text, playerObject.gold, 2, 7);
//#else
//#                     replaceNumber(playerGoldWindow.text, playerObject.gold, 0, 5);
//#endif
                    
                    // clear fire walls
                    for (n=0; n < FIREWALL_WINDOWSIZE; n++) {System.arraycopy(emptyFireWallElement, 0, fireWalls[n], 0, FIREWALL_WINDOWSIZE);}
                    
                    // get on legacyPlayfield again
                    sendAddMe();
                }
                

                //
                // SWITCH LOOKS / MORPH / CAMOUFLAGE
                //
                else if (message[2]=='s' && message[3]=='l') {
                    int attackerID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    int targetID = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);

                    Character attacker = (Character)idToCharacters.get("" + attackerID);
                    Character target = (Character)idToCharacters.get("" + targetID);
// System.out.println("received F_SL");                    
                    if (target!=null) {
                        // change looks of target
                        target.graphicsel = graphicSelExtract(message[12]);    // make sure this is interpreted as an unsigend value
                        target.graphicsDim = message[13];
                        target.graphicsX = message[14];
                        target.graphicsY = message[15];
                        
                        target.hitShowDuration = MAX_HITSHOWDURATION;
                        target.flashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                        target.flashPhase = true;
                        // -- target.icon = ICON_HIT;
                        target.hitDisplayDelay = 0;
                        
                        if (message[16] > 0) {
                            if ( (curGametime + ((message[16]+4) * 1000))  > target.spellVisualsEndTime) {
                                target.spellVisualsEndTime = curGametime + ((message[16]+4) * 1000);   // read morph effect duration from message
                            }
                            target.spellVisualsColorType = message[17]; // set the color type in which to display the spell animation

                            // reduce attr.
                            target.spellVisualsEndTime1 = target.spellVisualsEndTime;   // read spell effect duration from message
                            target.spellVisualsColorType1 = 0; 
                        }
                         
                        
                        if (attacker==null && attackerID != 0) {
                            // attacker not in range, but hit received -> get attacker!
                            sendMessageGetObject(attackerID);
                        } else if (attacker != null) {    // attacker is visible
                            // show icon at attacker
                            if (attackerID != playerObject.objectId) {
                                attacker.extraIconShowDuration = MAX_HITSHOWDURATION / 2;
                                attacker.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                                attacker.extraFlashPhase = true;
                                attacker.extraicon = ICON_ATTACK_SPELL1;
                            }
                            /*
                            if (attackerID != playerObject.objectId) {
                                // enemey, show simple attackBase animation
                                attacker.attackAnimate = 2;
                            }
                             */
                        }
                    }
                        
                    return true;
                }
                

                //
                // [F]IRE [W]ALL
                //
                else if (message[2]=='f' && message[3]=='w') {
                    // System.out.println("GOT FIRE WALL " + message[8] + ", " + message[9] + ": " + message[10]);
                    
                    // System.out.println("cellWindow x,y: " + cellWindow_XStart + ", " + cellWindow_YStart);
                    
                    int senderID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    m1 = message[8] - cellWindow_XStart; // cellX
                    m2 = message[9] - cellWindow_YStart; // cellY
                    
                    // System.out.println("Setting FW at: " + m1 + ", " + m2);
                      
                    m4 = message[10] & 15;  // extract the time, 4 lower bits ----tttt
                    m5 = (message[10] & 48) >> 6;  // extract the display offset, 2 medium bits --dd----
                    m6 = (message[10] & 192) >> 6;  // extract the value class, 2 higher bits vv------

                    // System.out.println("time: " + m5 + " value: " + m6);
                    
                    if (m1 >= 0 && m1 < FIREWALL_WINDOWSIZE && m2 >= 0 && m2 < FIREWALL_WINDOWSIZE) {
                        fireWalls[m1][m2] = message[10];
                    }
                    
                    if (senderID != 0 && senderID != playerObject.objectId) {
                        Character sender = (Character)idToCharacters.get("" + senderID);
                        if (sender!=null) {
                            sender.extraIconShowDuration = MAX_HITSHOWDURATION / 2;
                            sender.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                            sender.extraFlashPhase = true;
                            sender.extraicon = ICON_ATTACK_SPELL1;
                        }
                    }
                    
            
                        /*
            for (int i=0; i<FIREWALL_WINDOWSIZE; i++) {
                
                System.out.print("\n");
                for (int j=0; j<FIREWALL_WINDOWSIZE; j++) {
                        System.out.print(fireWalls[j][i] + " , ");
                }
                 
            }
                        */
                        
                    return true;
                }
                
                //
                // [S]PELL [V]ISUALS
                //
                else if (message[2]=='s' && message[3]=='v') {

                    int attackerID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    int targetID = NetTools.intFrom4Bytes(message[8], message[9], message[10], message[11]);

                    Character attacker = (Character)idToCharacters.get("" + attackerID);
                    Character target = (Character)idToCharacters.get("" + targetID);

                    if (target!=null) {
                        // show hit animation at target
                        if (message[13] == 0 || message[14] == 2) {
                            target.hitShowDuration = MAX_HITSHOWDURATION;
                            target.flashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                            target.hitDisplayDelay = 0;
                            target.flashPhase = true;
                            // -- target.icon = ICON_HIT;
                        }
                        
                        // set spell visuals for target
                        if (message[12] > 0) {
                            //if ( (curGametime + ((message[12]+2) * 1000))  > target.spellVisualsEndTime) {
                            //    target.spellVisualsEndTime = curGametime + ((message[12]+2) * 1000);   // read spell effect duration from message
                            //}
                            if (message[14] < 2) {
                                target.spellVisualsEndTime = curGametime + ((message[12]+2) * 1000);   // read spell effect duration from message
                                target.spellVisualsColorType = message[14]; // set the color type in which to display the spell animation
                            } else {
                                // raise / reduce attr.
                                target.spellVisualsEndTime1 = curGametime + ((message[12]+2) * 1000);   // read spell effect duration from message
                                target.spellVisualsColorType1 = (byte)(message[14]-2); // set the color type in which to display the spell animation

                                target.spellVisualsEndTime = curGametime + 4000;   // 
                                target.spellVisualsColorType = (byte)(message[14]-2); // set the color type in which to display the spell animation
                            }
                        }
                    }
                    
                    if (attacker != null) {    // attacker is visible
                        // show icon at attacker
                        if (attackerID != playerObject.objectId) {
                            attacker.extraIconShowDuration = MAX_HITSHOWDURATION / 2;
                            attacker.extraFlashPhaseDuration = GlobalSettings.MAX_FLASHPHASEDURATION;
                            attacker.extraFlashPhase = true;
                            attacker.extraicon = ICON_ATTACK_SPELL1 + message[13];   // e.g. ICON_ATTACK_SPELL1
                        }
                    }

                        
                    return true;
                    
                }
                
                //
                // CHAT MESSAGE (talk to specific game object)
                //
                else if (message[2]=='t' && message[3]=='s') {
                    int i = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);  //get sender id
                    if ((currentSubState==SUBSTATE_TALKTO || currentSubState == SUBSTATE_CHAT_SHORTCUT_SELECT|| (currentSubState == SUBSTATE_TALKINPUT_OPTIONS && nextChatSubstate == SUBSTATE_TALKTO)) && actionPartnerID==i) {
                        // currently in chat mode and message is from chat partner
                        if (actionPartnerName==null) {
                                //get name
                                Character c = (Character)idToCharacters.get("" + actionPartnerID);
                                if (c!=null && c.name!=null) {
                                    actionPartnerName = (c.name).toCharArray();
                                } else {
                                    actionPartnerName = ("UNKNOWN").toCharArray();
                                }
                                c = null;
                        }
                        //get message
                        tmpCharsM = new char[message[12] + 3];
                        for (int k=0; k<message[12] && 13+k<message.length; k++) {
                            tmpCharsM[k+2] = (char)message[13+k];
                        }
                        if (actionPartnerName!=null) {
                            GTools.textWindowAddText(chatWindow, actionPartnerName);
                        }
                        tmpCharsM[0] = ':';
                        tmpCharsM[1] = ' ';
                        tmpCharsM[tmpCharsM.length-1] = '\n';
                        //GTools.textWindowAddText(chatWindow, ": ");
                        GTools.textWindowAddText(chatWindow, tmpCharsM);
                        //GTools.textWindowAddText(chatWindow, "\n");
                        tmpCharsM = null;
                    } else { //not in chat mode or chatting with someone else
                        chatRequest=true;
                        if (genericList.entries.size() < MAX_QUEUED_EVENTS) {
                            Character c = (Character)idToCharacters.get("" + i);
                            if (c!=null && c.name!=null) {
                                tmpChars1M = (c.name + ": ").toCharArray();
                                tmpStringM = c.name;
                            } else {
                                
                                // name not found, check if we have it in the friend name list
                                Integer tmpInt = new Integer(i);
                                tmpStringM = (String)friendsNames.get(tmpInt);
                                if (tmpStringM != null) {
                                    // we found the name of the sender in the friend name list
                                    tmpChars1M = (tmpStringM + ": ").toCharArray();
                                } else {
                                    // check if the name was sent as extra info to this message
                                    m1 = 13 + message[12];  // total length without extra info                                    
                                    if (message[0] > m1+1) {
                                        // the message itself contains the name of the sender
                                        // this means sender is on another legacyPlayfield and is not a friend
                                        // this won't happen too often
                                        // name length is stored before the actual name, as usual
                                        // i.e. name length is at message[m1] and name starts at index m1+1
                                        tmpStringM = new String(message, m1+1, message[m1]);
                                        tmpChars1M = (tmpStringM + ": ").toCharArray();
                                    } else {
                                        // cannot assign the name properly, use UNKNOWN
                                        tmpChars1M = ("UNKNOWN: ").toCharArray();
                                        tmpStringM = "UNKNOWN";
                                    }
                                }
                            }
                            //store the sender id
                            queuedEventsIDs[genericList.entries.size()] = i;                                
                            int j = tmpChars1M.length;
                            tmpCharsM = new char[j + message[12] + 1];
                            System.arraycopy(tmpChars1M, 0, tmpCharsM, 0, j);
                            for (int k=0; k<message[12]; k++) {
                                tmpCharsM[j+k] = (char)message[13+k];
                            }
                            tmpCharsM[tmpCharsM.length-1] = '\n';
                            GTools.listAppendEntry(genericList, tmpStringM, tmpCharsM);
                            GTools.listSetIconForEntry(genericList, genericList.entries.size()-1, iconMessageNew);
                            //GTools.listSetIconForEntry(genericList, genericList.entries.size()-1, 0);
                            tmpCharsM = null; tmpChars1M = null; c = null; tmpStringM = null;
                            
                            // make sure the number of messages is reflected in the open menu
                            if (currentSubState == SUBSTATE_TALK_SUBOPTIONS || currentSubState == SUBSTATE_CHAT_SHORTCUT_EDIT || currentSubState == SUBSTATE_CHAT_SHORTCUT_EDIT_DETAIL) {
                                GTools.buttonListSetButton(menuActionSub, "Incoming Messages (" + genericList.entries.size() + ")", 2, false, true);
                            }
                        } else {
                            //$->TODO
                            //message queue full cannot accept more messages - send error msg to sender
                        }
                        
                    }
                    
                    return true;
                }

                //
                // TALK TO ALL MESSAGE
                //
                else if (message[2] == 't' && message[3] == 'a') {
                    fwgoTmpM = null;
                    int i = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);  //extract sender object id
                    
                    //get the object
                    fwgoTmpM = getObject(i);

                    if (fwgoTmpM!=null) {
                        //object in range
                        if(message[8] == 0) {
                            fwgoTmpM.msgDisplayName = false;
                        } else {
                            fwgoTmpM.msgDisplayName = true;
                        }
                        fwgoTmpM.msgText = new char[message[9]];
                        for (i=0; i<message[9]; i++) {
                            fwgoTmpM.msgText[i] = (char)message[10+i];
                        }
                        
                        readjustPlayerMsg(fwgoTmpM);
                        fwgoTmpM.msgShowDuration = MESSAGE_MAXSHOWDURATION;
                        fwgoTmpM = null;
                    }
                    return true;
                }

                //
                // SETTING TRADE [O]FFER [F]AILED or SET [I]TEM [C]ONSUMETIMES
                //
                else if((message[2] == 'o' && message[3] == 'f') || (message[2] == 'i' && message[3] == 'c')) {
                    // get the id
                    m1 = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    // get units
                    m2 = NetTools.intFrom2Bytes(message[8], message[9]);
                    // locate the item in the inventory and cancel the trade 
                    // + set the units value
                    for (m3 = 0; m3<invItemsCount; m3++) {
                        if (invItems[m3]!=null && invItems[m3].objectId==m1) {
                            // item found
                            if (message[2] == 'o') {    // trade offer failed message
                                cancelSale(false);
                                overlayMessage("Sale offer failed!\n");
                            }
                            invItems[m3].units = m2;
                            atDisplay_Item = null;  // make sure attribut display will be updated
                            break;
                        }
                    }
                    return true;
                    
                }

                //
                // [U]PDATE [I]TEM [C]ONSUMETIMES
                //
                else if(message[2] == 'u' && message[3] == 'i' && message[4] == 'c') {

                    // get item id
                    m1 = NetTools.intFrom4Bytes(message[5], message[6], message[7], message[8]);
                    // get units +/- change
                    m2 = NetTools.intFrom2Bytes(message[9], message[10]);
                    // locate the item in the inventory and cancel the trade 
                    // + update the units value
                    for (m3 = 0; m3<invItemsCount; m3++) {
                        if (invItems[m3]!=null && invItems[m3].objectId==m1) {
                            // item found, change units
                            invItems[m3].units += m2;
                            atDisplay_Item = null;  // make sure attribut display will be updated
                            if (currentState==STATE_GAME && m2 > 0) {
                                showBottomInfo("+" + m2 + " units: " + invItems[m3].name, 8000, false);
                            }
                            break;
                        }
                    }
                    return true;
                }
                
                
                //
                // [B]UY [L]IST ADD ITEM
                //
                else if(message[2] == 'b' && message[3] == 'l') {
                    if (currentSubState == SUBSTATE_TRADE_REQUEST || currentSubState == SUBSTATE_TRADE_BUY_CONFIRM) {
                        itemTmpM = new Item();
                        fillItemFromMessage(itemTmpM, message);   // fill item
                        addItemToInventory(itemTmpM, false);      // add to tradeOfferItems
                        itemTmpM = null;
                    }
                    return true;
                }

                //
                // [B]UY [L]IST END
                //
                else if(message[2] == 'l' && message[3] == 'e') {
                    if (currentSubState == SUBSTATE_TRADE_REQUEST && tradeOfferItemsCount == 0) {
                        subStateNormal();
                        overlayMessage("Character has no item\nfor sale");
                    }
                }
                
                //
                // [B]UY [S]TATUS: Buying failed
                //
                else if(message[2] == 'b' && message[3] == 's') {
                    int goldAmount = NetTools.intFrom4Bytes(message[5],message[6],message[7],message[8]);
                    if (goldAmount > 0) {   // return gold that was already spent
                        playerObject.gold += goldAmount;
                        if (playerObject.gold > 999999) {
                            playerObject.gold = 999999;
                        }
//#if Series40_MIDP2_0
//#                         replaceNumber(playerGoldWindow.text, playerObject.gold, 0, 5);
//#else
                        replaceNumber(playerGoldWindow.text, playerObject.gold, 2, 7);
//#endif
                        showBottomInfo("You received gold: +" + goldAmount, 8000, false);
                    }
                    // react on status
                    if (message[4]==1 || message[4]==3) {
                            overlayMessage("Buying item failed.\nYour gold was returned.");
                    } else if (message[4]==2) {
                        overlayMessage("Buying item failed.\nSeller not available\nYour gold was returned.");
                        if (currentSubState == SUBSTATE_TRADE_REQUEST) {  // close trade offer display
                            subStateNormal();
                        }
                    }
                }
                
                
                //
                // POINTS INCREASE
                //
                else if(message[2] == 'p' && message[3] == 'i') {
                    m1 = NetTools.intFrom2Bytes(message[4], message[5]);
                    playerObject.experience += m1;
                    playerObject.curHealth += NetTools.intFrom2Bytes(message[6], message[7]);
                    playerObject.curMana += NetTools.intFrom2Bytes(message[8], message[9]);
                    
                    experienceCurOffset += m1;
                    
//#if !(Series40_MIDP2_0)
                    // set the experience points
                    replaceNumber(playerExperienceWindow.text, playerObject.experience, 2, 7);
//#endif
                    
                    if ((currentSubState == SUBSTATE_TRIGGERTARGET_FIND || currentSubState == SUBSTATE_GROUND_FIND) && m1 > 1) {
                        replaceNumberLeftAlign(xpInfo, m1, 4, 7, false);
                        xpInfoShowDuration = 4000;
                    } else if (m1 > 1) {
                        showBottomInfo("Received experience: +" + m1, 4000, false);
                    }
                    
                    if (currentSubState==SUBSTATE_BUILDCHARACTER) {
                        replaceNumberLeftAlign(atrCHR_Experience, playerObject.experience, 7, 13, false);
                    }
                    return true;

                
                //
                // LEVEL INCREASE
                //
            } else if (message[2] == 'i' && message[3] == 'l') {
                    // add attribute points
                    playerObject.levelpoints += message[5];    
                    // check if level was really increases
                    if (message[4] > 0) {
                        playerObject.level += message[4];
//#if !(Series40_MIDP2_0)
                        replaceNumber(playerLevelWindow.text, playerObject.level, 0, 1);
//#endif
                        // set the limit for the next level
                        experiencePlusForNextLevel = (((playerObject.level + 1) * (playerObject.level + 2) * 100)) - (((playerObject.level) * (playerObject.level + 1) * 100));
                        experienceCurOffset = playerObject.experience - (((playerObject.level) * (playerObject.level + 1) * 100));
                        showBottomInfo("New character level: " + playerObject.level + "\nAttributepoints: " + playerObject.levelpoints + " (+" + message[5] + ")", 10000, message[6]==1);
                    } else {
                        showBottomInfo("\nReceived attributepoints: " + playerObject.levelpoints + " (+" + message[5] + ")", 12000, true);
                    }
                    
                    if (currentSubState==SUBSTATE_BUILDCHARACTER) {
                        replaceNumberLeftAlign(atrCHR_Level, playerObject.level, 7, 8, false);
                        replaceNumberLeftAlign(atrCHR_Points, playerObject.levelpoints, 18, 20, false);
                        if (playerObject.levelpoints > 0) {
                            setBottomCommand1("Add Point");
                        } else {
                            this.bCommand1 = false;
                        }
                    }
                    return true;
                               

                    //
                    // ATTRIBUTE INCREASE
                    //
                } else if (message[2] == 'i' && message[3] == 'a') {
                    switch (message[4]) {
                        case 0: // healthBase
                            playerObject.healthBase += message[5];
                            maxhealth += message[5];
                            replaceNumberLeftAlign(atrHealth, maxhealth, 12, 16, true);
                            showBottomInfo("Increased max. health: +" + (message[5]/10) + "." + (message[5]%10), 12000, message[6]==1);
                            break;
                        case 1: // manaBase
                            playerObject.manaBase += message[5];
                            maxmana += message[5];
                            replaceNumberLeftAlign(atrMana, maxmana, 12, 16, true);
                            showBottomInfo("Increased max. mana: +" + (message[5]/10) + "." + (message[5]%10), 12000, message[6]==1);
                            break;
                        case 2: // attackBase
                            playerObject.attackBase += message[5];
                            attack += message[5];
                            replaceNumberLeftAlign(atrAttack, attack, 12, 16, true);
                            showBottomInfo("Increased attack: +" + (message[5]/10) + "." + (message[5]%10), 12000, message[6]==1);
                            break;
                        case 3: // defenseBase
                            playerObject.defenseBase += message[5];
                            defense += message[5];
                            replaceNumberLeftAlign(atrDefense, defense, 12, 16, true);
                            showBottomInfo("Increased defense: +" + (message[5]/10) + "." + (message[5]%10), 12000, message[6]==1);
                            break;
                        case 4: // skillBase
                            playerObject.skillBase += message[5];
                            skill += message[5];
                            replaceNumberLeftAlign(atrSkill, skill, 12, 16, true);
                            showBottomInfo("Increased skill: +" + (message[5]/10) + "." + (message[5]%10), 12000, message[6]==1);
                            break;
                        case 5: // magicBase
                            playerObject.magicBase += message[5];
                            magic += message[5];
                            replaceNumberLeftAlign(atrMagic, magic, 12, 16, true);
                            showBottomInfo("Increased magic: +" + (message[5]/10) + "." + (message[5]%10), 12000, message[6]==1);
                            break;
                        case 6: // damageBase
                            playerObject.damageBase += message[5];
                            damage += message[5];
                            replaceNumberLeftAlign(atrDamage, damage, 12, 16, true);
                            showBottomInfo("Increased damage: +" + (message[5]/10) + "." + (message[5]%10), 12000, message[6]==1);
                            break;
                    }
                    
                    
                //
                // UPDATE GOLD
                //
                    
                } else if (message[2]=='u' && message[3]=='g') {
                    m1 = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    if (m1 != 0) {
                        playerObject.gold += m1;
                        if (playerObject.gold > 999999) {
                            playerObject.gold = 999999;
                        } else if (playerObject.gold < 0) {
                            playerObject.gold = 0;
                        }
//#if Series40_MIDP2_0
//#                         replaceNumber(playerGoldWindow.text, playerObject.gold, 0, 5);
//#else
                        replaceNumber(playerGoldWindow.text, playerObject.gold, 2, 7);
//#endif
                        if (m1 > 0) {
                            showBottomInfo("You received gold: +" + m1 + " G", 8000, message[8]==1);
                        } else {
                            showBottomInfo("Gold was removed: " + m1 + " G", 8000, message[8]==1);
                        }
                    }
                    
                    return true;
                    
                    
                //
                // Trigger type
                //    
                } else if(message[2] == 't' && message[3] == 't') {
                    int type = NetTools.intFrom2Bytes(message[4], message[5]);
                    
                    switch(type) {
                        case 512: //one way portal
                            setWaitLabelText("Entering portal..");
                            currentSubState = SUBSTATE_PORTAL_WAIT;
                            //setMessageWaitTimeout('a', 'p', 'c', 'i', 12, STATE_FORCED_EXIT, SUBSTATE_NORMAL, "Network timeout\nplease login again.", null, null);
                            bCommand1 = false;
                            playerMove = false;
                            sendPos = false;
                            break;
                        
                        case 553: //far portal
                            setWaitLabelText("Accessing portal stone..");
                            currentSubState = SUBSTATE_FAR_PORTAL_WAIT;
                            bCommand1 = false;
                            playerMove = false;
                            sendPos = false;
                            break;
                        
                        case 8: // Jump of Escape
                            playerMove = false;
                            sendPos = false;
                            break;
                        
                        case 10: // Mass-heal
                            //showBottomInfo("You casted 'mass heal'!", 4000, (byte)0);
                            break;
                        
                        case 11: // Mass-attackBase
                            //showBottomInfo("You casted 'mass attackBase'!", 4000, (byte)0);
                            break;
                        
                        case 12: // heal
                            //showBottomInfo("You casted 'heal' on yourself!", 4000, (byte)0);
                            break;
                        
                        case 13:    // Scroll / Spell on target
                            showBottomInfo("You casted a spell!", 4000, false);
                            break;
                            
                        case 500: //Button
                            showBottomInfo("You activated a trigger.", 4000, false);
                            break;
                        
                        case 501: //Health Shrine
                            overlayMessage("You received maximum health.");
                            break;
                        
                        case 502: //Mana Shrine
                            overlayMessage("You received maximum mana.");
                            break;
                        
                        case -1:
                            /*
                            playerObject.weaponRechargeStartTime = 0;
                            playerObject.weaponRechargeEndTime = 0;
                             */
                            playerObject.cancelRechargeForAttack();
                            break;
                    }
                    blockDuration = 0;
                    waitingForTrigger = false;

                //
                // [C]lient [m]essage
                //    
                } else if (message[2] == 'c' && message[3] == 'm') {
                    tmpStringM = new String(message, 5, message[4]);
                    if (currentState != STATE_SUBSCRIBE_EXIT_WAIT_FOR_MSG) {
                        overlayMessage(tmpStringM);
                    } else {
                        subStateOKDialog(tmpStringM, STATE_FORCED_EXIT, SUBSTATE_NORMAL);
                        messageTimeout = -1;
                        doLogout();
                        stopNet();

                    }
                    tmpStringM = null;
                    return true;
                    
                //
                // [S]end [C]redits
                //
                } else if (message[2] == 's' && message[3] == 'c') {
                    creditid = message[4];
                    tmpStringM = new String(message, 6, message[5]);
                    GTools.textWindowSetText(creditsWindow, tmpStringM);
                    //System.out.println("received credits: " + tmpStringM);
                    tmpStringM = null;
                    return true;
                    
                //
                // [b]ot [p]hrase
                //
                } else if (message[2] == 'b' && message[3] == 'p') {
                    // get the botphrase
                    String phrase = new String(message, 5, message[4]);
                    GTools.textWindowSetText(botphraseWindow, phrase);
                                    
                //
                // [c]lient [p]hrase
                //
                } else if (message[2] == 'c' && message[3] == 'p') {
                    if (currentSubState == SUBSTATE_DIALOGUE_INIT || currentSubState == SUBSTATE_DIALOGUE_ACTIVE) {
                            dialogueTotalCount = message[4];    // update dialogue load display
                            dialogueCurrent = message[5];
                    }
                    
                    // get the clientphrase
                    if (dialogueCurrent >=0 && dialogueCurrent < clientphraseWindows.length) {
                        botphraseNextIDs[dialogueCurrent] = NetTools.intFrom4Bytes(message[6], message[7], message[8], message[9]);
                        String phrase = new String(message, 11, message[10]);
                        if(botphraseNextIDs[dialogueCurrent] >= -1) {
                            clientphraseWindows[dialogueCurrent].selectable = true;
                            clientphraseWindows[dialogueCurrent].backColor = 0x404040;
                        } else {
                            clientphraseWindows[dialogueCurrent].selectable = false;
                            clientphraseWindows[dialogueCurrent].backColor = 0x800000;
                            phrase = (new String("Quest requires level " + (-botphraseNextIDs[dialogueCurrent]) + ":\n   ")).concat(phrase);
                        }
                        GTools.textWindowSetText(clientphraseWindows[dialogueCurrent], phrase);

                        // ==SUBSTATE_DIALOGUE_INIT
                        if (currentSubState!=SUBSTATE_DIALOGUE_ACTIVE) {
                            currentSubState = SUBSTATE_DIALOGUE_ACTIVE;
                            setBottomCommand1("Select");
                            setBottomCommand2("Quit");
                            GTools.menuSetSelected(menuClientphrases, 0);
                        }
                    }

                //
                // [d]ialogue [f]ailed
                //
                } else if (message[2] == 'd' && message[3] == 'f') {
                    // generic dialogue error
                    // set default dialogue
                    currentSubState = SUBSTATE_DIALOGUE_ACTIVE;
                    dialogueTotalCount = 1;    // update dialogue load display
                    dialogueCurrent = 0;
                    GTools.textWindowSetText(botphraseWindow, "Cannot talk now.");
                    GTools.textWindowSetText(clientphraseWindows[dialogueCurrent], "Bye.");
                    clientphraseWindows[dialogueCurrent].selectable = true;
                    clientphraseWindows[dialogueCurrent].backColor = 0x404040;
                    botphraseNextIDs[dialogueCurrent] = -1;
                    setBottomCommand1("Select");
                    setBottomCommand2("Quit");
                    GTools.menuSetSelected(menuClientphrases, 0);
                

                //
                // [q]uest [o]pen
                //
                } else if (message[2]=='q' && message[3]=='o') {
                    m1 = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    tmpStringM = new String(message, 10, message[9]);

                    // append quest to the list in the questbook
                    if (tmpStringM!=null) {
                        GTools.listAppendEntry(listQuests, tmpStringM, new Integer(m1));
                        if (message[8]==1) {    // inform player if neccessary
                            overlayMessage("A new quest was\nadded to your\n quest book!");
                        }
                    } else {
                        // error!
                    }
                    tmpStringM = null;
                
                //
                // open [q]uest list end
                //
                    /*
                } else if (message[2]=='q' && message[3]=='0') {
                    // $->
                    currentState = STATE_WAIT;
                    setWaitLabelText("Preparing World ..");
                    // this trigger the world loading procedure
                    sendChooseCharacterMessage(character_DB_ID);
                    return true;
                    */
                //
                // [q]uest open [f]ailed
                //
                } else if (message[2]=='q' && message[3]=='f') {
                    overlayMessage("Could not activate\nnew quest!\nMaximum 10 quests\nmay be active");
                    return true;
                    
                //
                // [q]uest [d]etails
                //
                } else if (message[2]=='q' && message[3]=='d') {
                    if (currentSubState==SUBSTATE_QUEST_REQUESTDETAILS) {

                        Integer tmpInt = null;
                        tmpCharsM = null;
                        m1 = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]); // quest id
                        m2 = message[8]; // quest class id
                        if(m2 < 0) {
                            //questUndeleteable
                            m2 = -m2;
                        }
                        m3 = NetTools.intFrom4Bytes(message[9], message[10], message[11], message[12]); // current data
                        m4 = NetTools.intFrom4Bytes(message[13], message[14], message[15], message[16]); //original data
                        //System.out.println("data: " + m3 + " / " + m4);
                        for (m5=0; m5<listQuests.entries.size(); m5++) {    // find the name of the quest
                            tmpInt = (Integer)GTools.listGetDataAt(listQuests, m5);
                            if (tmpInt!=null && tmpInt.intValue() == m1) {
                                tmpCharsM = GTools.listGetEntryAt(listQuests, m5);
                                break;
                            }
                        }
                        if (tmpCharsM!=null) {  // name found
                            // set quest details
                            //GTools.textWindowSetText(questNameWindow, tmpCharsM);
                            GTools.listRemoveAllEntries(questNameWindow);
                            GTools.listSetIconDimensions(questNameWindow, QUESTCLASSES_ICON_SIZE, QUESTCLASSES_ICON_SIZE);
                            //GTools.listSetIconForEntry(questNameWindow, 0, questclasses, 0, 0);
                            switch (m2) {
                                case 10:    // DIALOGUE
                                    i = 0;
                                    j = 0;
                                    break;
                                case 11:    // DELIVER
                                    i = QUESTCLASSES_ICON_SIZE;
                                    j = 0;
                                    break;
                                case 12:    // KILL
                                    i = 2*QUESTCLASSES_ICON_SIZE;
                                    j = 0;
                                    break;
                                case 13:    // FIND ITEM
                                    i = 3*QUESTCLASSES_ICON_SIZE;
                                    j = 0;
                                    break;
                                case 14:    // GET ITEM
                                    i = 0;
                                    j = QUESTCLASSES_ICON_SIZE;
                                    break;
                                case 15:    // USE ITEM
                                    i = QUESTCLASSES_ICON_SIZE;
                                    j = QUESTCLASSES_ICON_SIZE;
                                    break;
                                case 20:    // USE PORTAL
                                    i = 2*QUESTCLASSES_ICON_SIZE;
                                    j = QUESTCLASSES_ICON_SIZE;
                                    break;
                                case 21:    // ACTIVATE TRIGGER
                                    i = 3*QUESTCLASSES_ICON_SIZE;
                                    j = QUESTCLASSES_ICON_SIZE;
                                    break;
                                default:    // SHOW USE ITEM ICON
                                    i = QUESTCLASSES_ICON_SIZE;
                                    j = QUESTCLASSES_ICON_SIZE;
                                    break;
                            }
                            //System.out.println("class: " + m2 + "   i: " + i + "   j: " + j);
                            GTools.listSetIconDimensions(questNameWindow, QUESTCLASSES_ICON_SIZE, QUESTCLASSES_ICON_SIZE);
                            //GTools.listSetIconForEntry(questNameWindow, 0, questclasses, i, j);
                            GTools.listAppendEntry(questNameWindow, tmpCharsM, null);
                            GImageClip gic = new GImageClip(questclasses, i, j, QUESTCLASSES_ICON_SIZE, QUESTCLASSES_ICON_SIZE);
                            GTools.listSetIconForEntry(questNameWindow, 0, gic);
                            // get and set description
                            tmpStringM = new String(message, 19, message[17]);
                            m5 = tmpStringM.indexOf("�");
                            if(m5 != -1) {
                                m6 = tmpStringM.indexOf("�", m5+1);
                                if(m6 != -1) {
                                    tmpStringK = tmpStringM.substring(0, m5).concat(m3 + tmpStringM.substring(m5+1, m6)).concat(m4 + tmpStringM.substring(m6+1, tmpStringM.length()));
                                } else {
                                    tmpStringK = tmpStringM.substring(0, m5).concat(m4 + tmpStringM.substring(m5+1, tmpStringM.length()));
                                }
                            } else {
                                tmpStringK = tmpStringM;
                            }
                            GTools.textWindowSetText(questDescriptionWindow, tmpStringK);
                            // get and set location
                            tmpStringM = new String(message, 19 + message[17], message[18]);
                            GTools.textWindowSetText(questLocationWindow, "Loc.: " + tmpStringM);
                            // go to quest details screen
                            changeQuestmenuView(true, true);
                        }
                        
                        tmpCharsM = null;
                        tmpStringM = null;
                        tmpStringK = null;
                    }
                    
                    return true;
                    
                //
                // [q]uest [u]pdated
                //
                } else if (message[2]=='q' && message[3]=='u') {
                    overlayMessage("Your questbook has\nbeen updated!");
                    return true;
                    
                //
                // [q]uest [l]eft
                //
                } else if (message[2]=='q' && message[3]=='l') {
                    if(message[8]==1 || message[8]==0) {
                        m1 = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]); // quest id
                        Integer tmpInt = null;
                        for (m3=0; m3<listQuests.entries.size(); m3++) {
                            tmpInt = (Integer)GTools.listGetDataAt(listQuests, m3);
                            if (tmpInt!=null && tmpInt.intValue() == m1) {
                                tmpCharsM = GTools.listGetEntryAt(listQuests, m3);
                                if(message[8] == 1) {
                                    overlayMessage("Quest deleted!\n" + new String(tmpCharsM));
                                    // remove the quest entry
                                    GTools.listRemoveEntry(listQuests, m3);
                                } else {
                                    overlayMessage("You have to fulfill the quest!\n" + new String(tmpCharsM));
                                }
                                break;
                            }
                            tmpCharsM = null;
                        }
                    } else {
                        overlayMessage("Couldn't delete quest.");
                    }
                    
                    if (currentSubState==SUBSTATE_QUEST_DELETE_WAIT) {
                        //k1 = GTools.listGetSelectedIndex(listQuests);
                        //GTools.listRemoveEntry(listQuests, k1);
                        changeQuestmenuView(false, true);
                    }

                //
                // [q]uest [c]losed
                //
                } else if (message[2]=='q' && message[3]=='c') {
                    m1 = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]); // quest id
                    Integer tmpInt = null;
                    for (m3=0; m3<listQuests.entries.size(); m3++) {
                        tmpInt = (Integer)GTools.listGetDataAt(listQuests, m3);
                        if (tmpInt!=null && tmpInt.intValue() == m1) {
                            tmpCharsM = GTools.listGetEntryAt(listQuests, m3);
                            overlayMessage("Quest solved!\n" + new String(tmpCharsM));
                            // remove the quest entry
                            GTools.listRemoveEntry(listQuests, m3);
                            break;
                        }
                        tmpCharsM = null;
                    }
                //
                // [p]layfield [h]elptext id
                //
                } else if (message[2]=='p' && message[3]=='h') {
                    playfieldHelptextID = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                
                //
                // [f]riend [r]equest
                //
                } else  if (message[2]=='f' && message[3]=='r') {
                    // extract sender id
                    int i = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);  //get sender id
                    
                    m1 = friendRequestList.entries.size();
                    Integer I = new Integer(i);
                    if (friendsOffline.contains(I) || friendsOnline.contains(I) || friendRequestList.contains(I)) {
                        // friend exists, or request present in friend list
                        // so just do nothing
                   }
                      
                    //make sure the correct icon is flashed
                    if (friendRequestList.entries.size() < MAX_QUEUED_FRIEND_REQUESTS) {
                        if (message[8] > 0) {
                           tmpStringM = new String(message, 9, message[8]);
                        } else {
                           tmpStringM = "UNKNOWN";
                        }
                        
                        GTools.listAppendEntry(friendRequestList, tmpStringM, new Integer(i));    
                        GTools.listSetIconForEntry(friendRequestList, friendRequestList.entries.size()-1, iconFriendRequestNew);
                        // make sure button inscription is synchronized if open
                        if (currentSubState == SUBSTATE_FRIEND_SUBOPTIONS || currentSubState == SUBSTATE_FRIEND_FIND || currentSubState == SUBSTATE_FRIEND_FIND_CONFIRM) {
                            GTools.buttonListSetButton(menuActionSub, "Incoming Requests (" + friendRequestList.entries.size() + ")", 2, false, true);
                        }
                        tmpStringM = null;
                        chatRequest=false;
                    } else {
                        //$->TODO
                        //friend request queue full cannot accept more friend requests - send error msg to sender
                    }

                    return true;

                //
                // [f]riend [a]ccept [f]ailed
                //
                } else  if (message[2]=='f' && message[3]=='a' && message[4]=='f') {
                    overlayMessage("Friend could not be added (maximum number of friends).");
                    
                    i = NetTools.intFrom4Bytes(message[5], message[6], message[7], message[8]);
                    // remove from list
                    Integer I = new Integer(i);
                    friendsOffline.removeElement(I);
                    friendsNames.remove(I);

                    return true;
                
                //
                // [f]riend [j]oined (went online)
                //
                } else  if (message[2]=='f' && message[3]=='j') {
                    
                    // extract character id
                    i = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    Integer I = new Integer(i);
                    tmpStringM = null;
                    if (message[8] > 0) {
                        // a name is sent with the message which indicates that this friend has not been listed before (e.g. because friendship has just been established)
                        tmpStringM = new String(message, 9, message[8]);
                    } 
                    setFriendOnlineStatus(I, tmpStringM, true);
                    tmpStringM = null;
                    return true;
                    
                //
                // [f]riend [l]eft (went offline)
                //
                } else  if (message[2]=='f' && message[3]=='l') {
                    
                    // extract character id
                    i = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    Integer I = new Integer(i);
                    tmpStringM = null;
                    if (message[8] > 0) {
                        // a name is sent with the message which indicates that this friend has not been listed before
                        tmpStringM = new String(message, 9, message[8]);
                    }
                    setFriendOnlineStatus(I, tmpStringM, false);
                    tmpStringM = null;
                    return true;

                //
                // [f]riend request [d]eclined
                //
                } else  if (message[2]=='f' && message[3]=='d') {
                    // extract sender id
                    int i = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    tmpStringM = new String(message, 9, message[8]);
                    
                    showBottomInfo("friend request declined: " + tmpStringM, 9000, true);
                    
                    // remove from friend request list (in case other player had sent a request too)
                    m1 = friendRequestList.entries.size();
                    
                    if (m1 > 0) {
                        for (m2=0; m2<m1; m2++) {
                            Integer I = (Integer)(GTools.listGetDataAt(friendRequestList, m2));
                                    //friendRequestList.entriesData.elementAt(m2));
                            if (I != null && I.intValue() == i) {
                                GTools.listRemoveEntry(friendRequestList, m2);
                                return true;
                            }
                        }
                    }
                    
                    return true;

                //
                // [f]riendship [c]ancel
                //
                } else  if (message[2]=='f' && message[3]=='c') {
                    m1 = NetTools.intFrom4Bytes(message[4], message[5], message[6], message[7]);
                    Integer tmpInt = new Integer(m1);
                    removeFriendOnFriendShipCancel(tmpInt);
                    return true;
                }

                //
                // EMAIL CHANGE SUCCESS
                //
                else if(message[2] == 'e' && message[3] == 's') {
                    if (optionSubState == OPTIONSUBSTATE_EMAIL_CHANGE_WAIT) {
                        optionState = OPTIONSTATE_NONE;
                        overlayMessage("Your e-mail was changed.");
                    } else if (currentSubState == SUBSTATE_EMAIL_CHANGE_WAIT) {
                        prepareRequestCharacters();
                    }
                    return true;
                }

                //
                // EMAIL CHANGE FAILED
                //
                else if(message[2] == 'e' && message[3] == 'f') {
                    if (optionState == OPTIONSTATE_EMAIL_ENTRY) {
                        optionSubState = OPTIONSUBSTATE_NONE;
                        setOptionCommand1("Options");
                        setOptionCommand2("Cancel");
                    } else if (currentSubState == SUBSTATE_EMAIL_CHANGE_WAIT) {
                        currentState = STATE_EMAIL_ENTRY;
                        currentSubState = SUBSTATE_NORMAL;
                        setBottomCommand1("Options");
                        setBottomCommand2("Skip");
                    }
                    if (message[4] > 0) {
                        tmpStringM = new String(message, 5, message[4]);
                        overlayMessage(tmpStringM);
                    } else {
                        overlayMessage("E-mail address could not be stored.");
                    }
                    return true;
                }

                //
                // RECOVER PASSWORD (receive notification if reset-code was sent)
                //
                else if(message[2] == '0' && message[3] == 'p') {
                    if (currentState == STATE_GET_PASSWORD_RESET_CODE && currentSubState == SUBSTATE_RECOVER_PASSWORD_WAIT) {
                        if (message[4] > 0) {
                            // success
                            tmpStringM = "Success!\nThe reset-code will be sent to the e-mail address which you provided for your account.\n\nPlease check your e-mail in a few minutes.";
                            GTools.listSetSelectedIndex(genericList, 1);
                        } else {
                            // failed
                            if (message[5] > 0) {
                                tmpStringM = new String(message, 6, message[5]);
                            } else {
                                tmpStringM = "Reset-code could not be requested.";
                            }
                        }
                        overlayMessage(tmpStringM);
                        currentState = STATE_RECOVER_PASSWORD_MAIN_OPTIONS;
                        currentSubState = SUBSTATE_NORMAL;
                        setBottomCommand1("Select");
                        setBottomCommand2("Back");
                    }
                    return true;
                }

                //
                // Info store password
                //
                else if(message[2] == 's' && message[3] == 'p') {
                    if (currentState == STATE_ENTER_PASSWORD_RESET_CODE && currentSubState == SUBSTATE_RECOVER_PASSWORD_WAIT) {
                        boolean success = false;
                        if (message[4] > 0) {
                            // success
                            success = true;
                            currentSubState = SUBSTATE_NORMAL;
                            currentState = STATE_INTRO_LIST;
                            setListEntriesIntro();
                            GTools.listSetSelectedIndex(genericList, 0);
                            setBottomCommand1("Select");
                            setBottomCommand2("Game");
                        } else {
                            // failed
                            currentSubState = SUBSTATE_NORMAL;
                            setBottomCommand1("Change Password");
                            setBottomCommand2("Back");
                        }
                        if (message[5] > 0) {
                            tmpStringM = new String(message, 6, message[5]);
                        } else {
                            if (success) {
                                tmpStringM = "Your password was changed.";
                            } else {
                                tmpStringM = "Password could not be changed.";
                            }
                        }

                        overlayMessage(tmpStringM);
                    }


                    return true;
                }

                //
                // EMAIL GET
                //
                else if(message[2] == 'e' && message[3] == 'g') {                    
                    String part1 = new String(message, 6, message[4]);
                    String part2 = "";
                    if (message[5] > 0) {
                        part2 = new String(message, 6+message[4], message[5]);
                    }
                    GTools.textWindowSetText(emailField1, part1);
                    GTools.textWindowSetText(emailField2, part2);
                    optionSubState = OPTIONSUBSTATE_NONE;
                    setOptionCommand1("Options");
                    setOptionCommand2("Cancel");
                    return true;
                }
                
                
            }   //END FANTASY WORLDS SPECIFIC MESSAGES
            
            // LONG MESSAGES
            else if (message[0]==0) {

                // [h]elp [t]ext
                if (message[3]=='f' && message[4]=='h' && message[5]=='t') {
                    if (overlayState ==  OVERLAY_HELP_WAIT || bAllowHelpReceive) {
                        nextHelpID = NetTools.intFrom4Bytes(message[6], message[7], message[8], message[9]);
                        m1 = NetTools.intFrom2Bytes(message[10], message[11]);
                        tmpStringM = new String(message, 12, m1);
                        GTools.labelSetText(confirmWindow, tmpStringM, false);
                        GTools.windowCenterXY(confirmWindow, 0, 0, DISPLAYWIDTH, TOTALHEIGHT);
                        overlayState = OVERLAY_HELP;
                        if (nextHelpID > 0) {
                            setOverlayCommand1("More");
                        } else {
                            ovCommand1 = false;
                        }
                        // setOverlayCommand2("Close");
                        bAllowHelpReceive = false;
                    }
                    return true;
                }
            }
            
            return false;
    }
    
}

