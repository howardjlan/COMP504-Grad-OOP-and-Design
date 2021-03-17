package edu.rice.comp504.model.factory;

import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.chatroom.AChatroom;
import edu.rice.comp504.model.message.*;

import java.util.Date;

public class MessageFactory {
    private static MessageFactory singleton;

    /**
     * Only makes 1 message factory.
     * @return The message factory
     */
    public static MessageFactory makeFactory() {
        if (singleton == null ) {
            singleton = new MessageFactory();
        }
        return singleton;
    }

    /**
     * Make object of message.
     * @return The message object
     */
    public AMessage makeMessage(String type, int id, String sender, int chatroomId, String data, User directToUser) {
        Date time = new Date();
        String messageType = type;
        AMessage message = null;

        switch (type) {
            case "text":
                message = new TextMessage(id, sender, chatroomId, time, messageType, data);
                break;
            case "requestJoin":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", directToUser.getUsername(), data, "requestJoin");
                break;
            case "notification":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", directToUser.getUsername(), data, "");
                break;
            case "join":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", null, data, "join");
                break;
            case "invite":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", null, data, "invite");
                break;
            case "ban":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", null, data, "ban");
                break;
            case "delete":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", null, data, "delete");
                break;
            case "leave":
            case "offline":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", null, data, "leave");
                break;
            case "warn":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", null, data, "warn");
                break;
            case "admin":
                message = new NotificationMessage(id, sender, chatroomId, time, "notification", null, data, "admin");
                break;
            case "direct":
                message = new DirectMessage(id, sender, chatroomId, time, messageType, directToUser.getUsername(), data);
                break;
            default:
                message = new TextMessage(id, sender, chatroomId, time, messageType, data);
                break;
        }
        return message;
    }
}
