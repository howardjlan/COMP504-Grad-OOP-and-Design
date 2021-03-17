package edu.rice.comp504.model.message;

import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.chatroom.AChatroom;

import java.util.Date;

public class DirectMessage extends AMessage {

    // = username of who to send the direct message to
    private String directToUser;

    /**
     * Constructor.
     *
     * @param messageId           message id
     * @param sender       user who send message
     * @param chatroomId     chatroom which receive the message
     * @param time         timestamp of the message
     * @param messageType  type of message
     * @param directToUser user who receive message
     * @param data         message content
     */
    public DirectMessage(int messageId, String sender, int chatroomId, Date time, String messageType, String directToUser, String data) {
        super(messageId, sender, chatroomId, time, messageType, data);
        this.directToUser = directToUser;
    }
}
