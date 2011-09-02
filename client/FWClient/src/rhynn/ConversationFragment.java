package rhynn;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marlowe
 */
public class ConversationFragment {
    public int senderId;
    public String senderName;
    public String message;

    public ConversationFragment(int newSenderId, String newSenderName, String newMessage) {
        senderId = newSenderId;
        senderName = newSenderName;
        message = newMessage;
    }
}
