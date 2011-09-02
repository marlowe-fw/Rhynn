package rhynn;



import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class Conversation {
    public static final int CHANNEL_TYPE_PRIVATE_SINGLE = 0;
    public static final int CHANNEL_TYPE_GLOBAL = 1;
    public static final int CHANNEL_TYPE_GROUP = 2;

    private static final int MAX_CACHED_MESSAGES = 16;

    private int channelId;
    private String channelName;
    private int channelType;

    long timeCreated;
    long timeLastChanged;

    private Vector newMessages = new Vector();
    private Vector cachedMessages = new Vector();

    public Conversation(int newChannelId, String newChannelName, long newTimeCreated) {
        // todo: allow custom channel type
        channelType = CHANNEL_TYPE_PRIVATE_SINGLE;
        channelId = newChannelId;
        channelName = newChannelName;
        timeCreated = timeLastChanged = newTimeCreated;
    }

    public int getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public long getTimeLastChanged() {
        return timeLastChanged;
    }

    public int getNumMessages() {
        return getNumCachedMessages() + getNumNewMessages();
    }

    public int getNumNewMessages() {
        return newMessages.size();
    }

    public int getNumCachedMessages() {
        return cachedMessages.size();
    }

    public Vector getCachedMessages() {
        return cachedMessages;
    }

    public Vector getNewMessages() {
        return newMessages;
    }


    public void addMessage(ConversationFragment frg, boolean isNew, long curTime) {
        if (isNew) {
            newMessages.addElement(frg);
        } else {
            cachedMessages.addElement(frg);
        }
        keepMessageLimit();
        timeLastChanged = curTime;
    }

    private void keepMessageLimit() {
        while (cachedMessages.size() + newMessages.size() > MAX_CACHED_MESSAGES) {
            if (cachedMessages.size() >0) {
                cachedMessages.removeElementAt(0);
            } else {
                newMessages.removeElementAt(0);
            }
        }
    }

    public void markNewMessagesAsRead() {
        for (int i=0; i<newMessages.size(); i++) {
            cachedMessages.addElement((ConversationFragment)newMessages.elementAt(i));
        }
        newMessages.removeAllElements();
    }


}
