package edu.rice.comp504.model.chatroom;

import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.message.AMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AChatroom {

    private int id;
    private String name;
    private String type;
    private String description;
    private ArrayList<String> admins = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();
    private ArrayList<String> bans = new ArrayList<>();
    private Map<Integer, AMessage> messages = new ConcurrentHashMap<>();

    /** Constructor.
     * @param id   chatroom id
     * @param name   chatroom name
     * @param description   chatroom description
     */
    public AChatroom(int id, String name, String type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    /**
     * Get the chatroom id.
     * @return chatroom id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the chatroom name.
     * @return chatroom name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the chatroom description.
     * @return chatroom description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the chatroom admins.
     * @return chatroom admins
     */
    public ArrayList<String> getAdmins() {
        if (!admins.isEmpty()) {
            return admins;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Set the chatroom admins.
     * @param admins chatroom admins
     */
    public void setAdmins(ArrayList<String> admins) {
        this.admins = admins;
    }

    /**
     * Get the chatroom users.
     * @return chatroom users
     */
    public ArrayList<String> getUsers() {
        return users;
    }

    /**
     * Add user to chatroom.
     * @param user chatroom user to add.
     */
    public void addUser(String user) {
        this.users.add(user);
    }

    /**
     * Get the chatroom ban users.
     * @return chatroom ban users
     */
    public ArrayList<String> getBans() {
        if (!bans.isEmpty()) {
            return bans;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Add user to chatroom bans.
     * @param user the user to be banned in the chatroom.
     */
    public void addBan(String user) {
        this.bans.add(user);
    }

    /**
     * Get the chatroom type.
     * @return chatroom type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Get the chatroom messages.
     * @return chatroom messages
     */
    public Collection<AMessage> getMessages() {
        return messages.values();
    }

    /**
     * Add messages to the chatroom .
     * @param message message.
     */
    public void addMessage(AMessage message) {
        this.messages.put(message.getId(), message);
    }

    public void deleteMessage(int messageId) {
        this.messages.remove(messageId);
    }

    public void editMessage(int messageId, String text) {
        AMessage message = this.messages.get(messageId);
        message.setData(text);
    }

    /**
     * Remove user.
     * @param username username of the user to be removed.
     */
    public void removeUser(String username) {
        if (users.contains(username)) {
            this.users.remove(username);
        }
    }

    /**
     * Remove admin.
     * @param admin removes the admin.
     */
    public void removeAdmin(String admin) {
        if (this.admins.contains(admin)) {
            this.admins.remove(admin);
        }
    }
}
