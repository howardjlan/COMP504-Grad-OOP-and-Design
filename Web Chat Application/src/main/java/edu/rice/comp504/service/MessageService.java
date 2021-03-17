package edu.rice.comp504.service;

import com.google.gson.Gson;
import edu.rice.comp504.model.chatroom.AChatroom;
import edu.rice.comp504.model.factory.MessageFactory;
import edu.rice.comp504.model.factory.UserFactory;
import edu.rice.comp504.model.message.AMessage;
import edu.rice.comp504.model.message.DirectMessage;
import edu.rice.comp504.model.user.User;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;

public class MessageService extends AGenericService<AMessage> {
    private final MessageFactory messageFactory = MessageFactory.makeFactory();
    private int messageId = 0;
    private static MessageService singleton;

    /**
     * Only makes 1 MessageService.
     * @return The MessageService
     */
    public static MessageService makeService() {
        if (singleton == null ) {
            singleton = new MessageService();
        }
        return singleton;
    }

    /**
     * Sends a message with the given parameters.
     * @param type message type
     * @param sender sending user
     * @param chatroom chatroom to post in
     * @param data message contents
     * @param directToUser null if all, user if direct message
     */
    public void sendMessage(String type, User sender, AChatroom chatroom, String data, User directToUser) {
        AMessage message = messageFactory.makeMessage(type, ++messageId, sender.getUsername(), chatroom.getId(), data, directToUser);
        Gson gson = new Gson();
        if (directToUser == null) {
            if (!chatroom.getUsers().contains(sender.getUsername()) && !type.equals("leave")) {
                return;
            }
            for (String username : chatroom.getUsers()) {
                try {
                    User user = UserService.makeService().get(username);
                    if (checkBlock(user, sender.getUsername())) {
                        continue;
                    }
                    Session session = UserService.onlineUserHashMap.get(user);
                    if (session != null) {
                        session.getRemote().sendString(gson.toJson(message));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                if (checkBlock(directToUser, sender.getUsername())) {
                    return;
                }
                Session session = UserService.onlineUserHashMap.get(directToUser);
                if (session != null) {
                    session.getRemote().sendString(gson.toJson(message));
                }
                if (message instanceof DirectMessage && !directToUser.getUsername().equals(sender.getUsername())) {
                    Session senderSession = UserService.onlineUserHashMap.get(sender);
                    if (senderSession != null) {
                        senderSession.getRemote().sendString(gson.toJson(message));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!type.equals("ban") && !type.equals("delete")) {
            chatroom.addMessage(message);
        }
    }

    /**
     * Check whether the sender is blocked by receiver.
     * @param sender user sender.
     * @param username string username.
     * @return true/false if it is checked or not
     */
    public boolean checkBlock(User sender, String username) {
        List<String> blockList = sender.getUsersBlocked();
        if (blockList.contains(username)) {
            return true;
        }
        return false;
    }

}
