package edu.rice.comp504.model.message;

import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.chatroom.AChatroom;

import java.util.Date;

public class NotificationMessage extends AMessage {
    // = true (user left chat room), false (warning to user or request to join to admin)
    private String directToUser;
    private String notificationType;

    /**
     * Constructor.
     *
     * @param messageId                  message id
     * @param sender              user who send message
     * @param chatroomId            chatroom which receive the message
     * @param time                timestamp of the message
     * @param messageType         type of message
     * @param directToUser        user who receive message
     * @param data                message content
     */
    public NotificationMessage(int messageId, String sender, int chatroomId, Date time, String messageType, String directToUser, String data, String notificationType) {
        super(messageId, sender, chatroomId, time, messageType, data);
        this.directToUser = directToUser;
        this.notificationType = notificationType;
    }

    /**
     * Get notification type.
     * @return notification type
     */
    public String getNotificationType() {
        return notificationType;
    }

    /**
     * Set notification type.
     * @return notification type
     */
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
