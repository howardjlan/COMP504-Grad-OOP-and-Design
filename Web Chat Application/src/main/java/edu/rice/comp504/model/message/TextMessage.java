package edu.rice.comp504.model.message;

import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.chatroom.AChatroom;

import java.util.Date;

public class TextMessage extends AMessage {

    /**
     * Constructor.
     *
     * @param messageId          message id
     * @param sender      user who send message
     * @param chatroomId    chatroom which receive the message
     * @param time        timestamp of the message
     * @param messageType type of message
     * @param data        message content
     */
    public TextMessage(int messageId, String sender, int chatroomId, Date time, String messageType, String data) {
        super(messageId, sender, chatroomId, time, messageType, data);
    }
}
