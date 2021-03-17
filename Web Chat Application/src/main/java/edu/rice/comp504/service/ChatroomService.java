package edu.rice.comp504.service;

import edu.rice.comp504.model.factory.ChatroomFactory;
import edu.rice.comp504.model.factory.UserFactory;
import edu.rice.comp504.model.message.AMessage;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.chatroom.AChatroom;
import org.eclipse.jetty.client.AbstractConnectorHttpClientTransport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatroomService extends AGenericService<AChatroom> {
    static Map<Integer, AChatroom> chatroomHashMap = new ConcurrentHashMap<>();
    private int chatroomId = 0;
    private static ChatroomService singleton;

    /**
     * Constructor for chatroom service.
     */
    public ChatroomService() {
        AChatroom general = ChatroomFactory.makeFactory().makeChatroom("public", ++chatroomId, "General",
                "Welcome to the general room of the chat app where you can meet all the users!");
        chatroomHashMap.put(chatroomId, general);
    }

    /**
     * Only makes 1 ChatroomService.
     * @return The ChatroomService
     */
    public static ChatroomService makeService() {
        if (singleton == null ) {
            singleton = new ChatroomService();
        }
        return singleton;
    }

    @Override
    public AChatroom get(int id) {
        return chatroomHashMap.get(id);
    }

    @Override
    public AChatroom get(String name) {
        for (Map.Entry<Integer, AChatroom> entry: chatroomHashMap.entrySet()) {
            if (name.equals(entry.getValue().getName())) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public List<AChatroom> getAll() {
        List<AChatroom> all = new ArrayList<>(chatroomHashMap.values());
        return all;
    }

    /**
     * Gets a list of all id in the chatroom.
     * @return list of ids
     */
    public List<Integer> getAllId() {
        List<Integer> all = new ArrayList<>(chatroomHashMap.keySet());
        return all;
    }

    /**
     * Creates the chatroom.
     * @param name chatroom's username
     * @param type chatroom type
     * @param description chatroom description
     */
    public AChatroom createChatroom(String name, String type, String description) {
        ChatroomFactory cf = ChatroomFactory.makeFactory();
        AChatroom chatroom = cf.makeChatroom(type, ++chatroomId, name, description);
        chatroomHashMap.put(chatroomId, chatroom);
        return chatroom;
    }

    /**
     * Add user to the chatroom.
     * @param username user's username
     * @param chatroomId chatroom Id
     */
    public void addUser(String username, int chatroomId) {
        AChatroom chatRoom = chatroomHashMap.get(chatroomId);
        List<String> users = chatRoom.getUsers();
        if (!users.contains(username)) {
            chatRoom.addUser(username);
        }
    }

    /**
     * Set user to admin to the chatroom.
     * @param username user's username
     * @param chatroomId chatroomId
     */
    public boolean setAdmin(String username, int chatroomId) {
        boolean isAdd = false;
        if (chatroomHashMap.containsKey(chatroomId)) {
            AChatroom chatRoom = chatroomHashMap.get(chatroomId);
            List<String> adminList = chatRoom.getAdmins();
            if (adminList.isEmpty()) {
                chatRoom.setAdmins(new ArrayList<>(Arrays.asList(username)));
                isAdd = true;
            } else if (!adminList.contains(username)) {
                chatRoom.getAdmins().add(username);
                isAdd = true;
            }
        }
        return isAdd;
    }

    /**
     * Remove user from admin list.
     * @param username username of the admin.
     * @param chatroomId chatroom id of the admin.
     */
    public void removeAdmin(String username, int chatroomId) {
        if (chatroomHashMap.containsKey(chatroomId)) {
            AChatroom chatRoom = chatroomHashMap.get(chatroomId);
            List<String> adminList = chatRoom.getAdmins();
            if (adminList.contains(username)) {
                chatRoom.removeAdmin(username);
            }
        }
    }

    /**
     * Ban user.
     * @param username username
     * @param chatroomId chatroom id
     */
    public void banUser(String username, int chatroomId) {
        chatroomHashMap.get(chatroomId).getBans().add(username);
    }

    /**
     * Remove user from the chatroom.
     * @param username username of the user.
     * @param chatroomId chatroom id.
     */
    public void removeUser(String username, int chatroomId) {
        if (chatroomHashMap.get(chatroomId) != null) {
            AChatroom room = chatroomHashMap.get(chatroomId);
            room.removeUser(username);
        }

    }

    /**
     * Get all chatroom messages.
     * @param chatroomId chatroom id.
     */
    public List<AMessage> getMessages(int chatroomId) {
        List msgs = new ArrayList(chatroomHashMap.get(chatroomId).getMessages());
        return msgs;
    }
}
