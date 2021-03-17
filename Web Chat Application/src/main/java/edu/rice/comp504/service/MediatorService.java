package edu.rice.comp504.service;

import com.google.gson.Gson;

import edu.rice.comp504.model.message.AMessage;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.model.chatroom.AChatroom;
import org.eclipse.jetty.websocket.api.Session;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MediatorService implements IMediatorService {
    private UserService userService = UserService.makeService();
    private ChatroomService chatroomService = ChatroomService.makeService();
    private MessageService messageService = MessageService.makeService();

    /**
     * Initialize the MediatorService class.
     */
    public MediatorService() {
        chatroomService.addUser("admin", 1);
        chatroomService.setAdmin("admin",1);
        userService.joinRoom("admin",1);
    }

    @Override
    public User register(String username, String pwd, String firstName, String lastName,
                         String age, String gender, String school, String department,
                         String major,String interests) {
        User newUser =  userService.createAccount(username, pwd, firstName, lastName, age, gender, school,
                department, major, interests);
        if (newUser != null) {
            chatroomService.addUser(username, 1);
        }
        return newUser;
    }

    /**
     * Login the user.
     * @param username username.
     * @param pwd password.
     * @return the user object.
     */
    @Override
    public User login(String username, String pwd) {
        User user = userService.login(username,pwd);
        if (user != null) {
            if (UserService.onlineUserHashMap.get(user) == null) {
                String joinedMsg = username + " joined the room.";
                messageService.sendMessage("join", user, chatroomService.get(1), joinedMsg, null);
                return user;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Get Profile.
     * @param username username.
     * @return the user profile.
     */
    @Override
    public User getProfile(String username) {
        if (userService.get(username) != null) {
            return userService.get(username);
        }
        return null;
    }

    /**
     * Block a user.
     * @param username username who wants to block the other
     * @param blockUser username who will be blocked
     */
    @Override
    public List<String> block(String username, String blockUser) {
        return userService.blockUser(username,blockUser);
    }

    /**
     * Unblock a user.
     * @param username username who wants to unblock others.
     * @param unblockUser username who will be unblocked.
     * @return block list or null.
     */
    @Override
    public List<String> unblock(String username, String unblockUser) {
        return userService.unblockUser(username,unblockUser);
    }

    /**
     * Get the chatroom.
     * @param chatroomId chatroom id.
     * @return the chatroom object.
     */
    @Override
    public AChatroom getChatroom(int chatroomId) {
        return chatroomService.get(chatroomId);
    }

    /**
     * Get all users in the chatroom.
     * @param chatroomId chatroom id.
     * @return a list of all the user in the room.
     */
    @Override
    public List<String> getAllUsers(int chatroomId) {
        if (chatroomService.get(chatroomId) != null) {
            return chatroomService.get(chatroomId).getUsers();
        }
        return null;
    }

    /**
     * Get all the chatroom that user joined.
     * @param username username.
     * @return a list of all the chatroom that user joined.
     */
    @Override
    public List<AChatroom> getJoinedRooms(String username) {
        List<Integer> roomIdList = userService.allJoinedRooms(username);
        List<AChatroom> roomList = new ArrayList<>();
        for (int id: roomIdList) {
            roomList.add(chatroomService.get(id));
        }
        return roomList;
    }

    /**
     * Get all the chatroom.
     * @return a list of all the chatroom.
     */
    @Override
    public List<AChatroom> getAllRooms() {
        return chatroomService.getAll();
    }

    /**
     * Create a chatroom.
     * @param username username.
     * @param chatroomName chatroom name.
     * @param type chatroom type.
     * @param description chatroom description.
     * @return a new chatroom object.
     */
    @Override
    public AChatroom createChatroom(String username, String chatroomName, String type, String description) {
        AChatroom newRoom = chatroomService.createChatroom(chatroomName.trim(), type, description);
        chatroomService.setAdmin(username, newRoom.getId());
        chatroomService.addUser(username,newRoom.getId());
        userService.joinRoom(username,newRoom.getId());
        return newRoom;
    }

    /**
     * Request to join a chatroom.
     * @param username username who wants to join.
     * @param chatroomId chatroom name.
     * @return if join successfully.
     */
    @Override
    public boolean requestJoin(String username, String chatroomId) {
        int chatId;
        if (chatroomId != null) {
            chatId = Integer.parseInt(chatroomId);
            if (userService.get(username) != null && chatroomService.get(chatId) != null) {
                AChatroom receiverChatroom = chatroomService.get(chatId);
                User user = userService.get(username);
                if (receiverChatroom.getBans().contains(username)) {
                    return false;
                }
                if (receiverChatroom.getUsers().contains(username)) {
                    return true;
                }
                if (receiverChatroom.getType().equals("Private")) {
                    List<String> adminList = chatroomService.get(chatId).getAdmins();
                    String requestMsg = username + " requests to join ";

                    // Sends direct message to each chatroom admin.
                    for (String adminName : adminList) {
                        messageService.sendMessage("requestJoin", user, receiverChatroom,
                                requestMsg, userService.get(adminName));
                    }
                } else {
                    // Immediately adds user if public room & sends notification.
                    userService.joinRoom(username, chatId);
                    chatroomService.addUser(username, chatId);
                    String joinedMsg = username + " joined the room.";
                    messageService.sendMessage("join", user, receiverChatroom, joinedMsg, null);
                }
            }
        }
        return true;
    }

    /**
     * Respond to a request to join.
     * @param username username who wants to join.
     * @param chatroomId chatroom name.
     * @param response response action.
     * @return if join successfully.
     */
    @Override
    public boolean respondToJoin(String username, String chatroomId, String response) {
        int chatId;
        if (chatroomId != null) {
            chatId = Integer.parseInt(chatroomId);
            if (userService.get(username) != null && chatroomService.get(chatId) != null && response != null) {
                AChatroom receiverChatroom = chatroomService.get(chatId);
                User user = userService.get(username);
                if (response.equals("accept")) {
                    userService.joinRoom(username, chatId);
                    chatroomService.addUser(username, chatId);
                    String joinedMsg = username + " joined the room.";
                    messageService.sendMessage("join", user, receiverChatroom, joinedMsg, null);
                }
            }
        }
        return true;
    }

    /**
     * Set admin to chatroom.
     * @param username username who will be set as admin.
     * @param chatroomId chatroom id.
     */
    @Override
    public void setAdmin(String username, int chatroomId) {
        if (userService.get(username) != null) {
            if (chatroomService.setAdmin(username,chatroomId)) {
                List<String> adminList = chatroomService.get(chatroomId).getAdmins();
                String adminContent = username + " is set as admin.";
                AChatroom receiverChatroom = chatroomService.get(chatroomId);
                User user = userService.get(username);
                messageService.sendMessage("admin", user, receiverChatroom,
                        adminContent , null);
            }
        }
    }

    /**
     * Get admins of the room.
     * @param chatroomId chatroom id.
     * @return List of admins.
     */
    @Override
    public List<String> getAdmin(int chatroomId) {
        if (chatroomService.get(chatroomId) != null) {
            return chatroomService.get(chatroomId).getAdmins();
        }
        return null;
    }

    /**
     * Send message.
     * @param type message type.
     * @param data message data.
     * @param sender message sender.
     * @param chatroomId chatroom id that receive the message.
     * @param directTo user that directly receive the message.
     */
    @Override
    public void sendMessage(String type, String data, String sender, int chatroomId, String directTo) {
        User senderUser = userService.get(sender);
        AChatroom receiverChatroom = chatroomService.get(chatroomId);
        User direcToUser = userService.get(directTo);
        if (type.equals("text") && direcToUser != null) {
            type = "direct";
        }
        messageService.sendMessage(type, senderUser, receiverChatroom, data, direcToUser);
    }

    /**
     * Get all chatroom messages.
     * @param roomId messages from roomID.
     */
    @Override
    public List<AMessage> getMessages(int roomId) {
        return chatroomService.getMessages(roomId);
    }

    /**
     * Invite user to chatroom.
     * @param usernames username.
     * @param chatroomId chatroom id.
     * @param inviter inviter.
     * @return the result of invite.
     */
    @Override
    public boolean invite(String usernames, int chatroomId, String inviter) {
        AChatroom chatroom = chatroomService.get(chatroomId);
        if (usernames == null || chatroom == null || !chatroom.getType().equals("Private") || !chatroom.getAdmins().contains(inviter)) {
            return false;
        }
        String[] arrOfUsers = usernames.split(",");
        User inviterUser = userService.get(inviter);
        List<String> usersInChatroom = chatroom.getUsers();
        List<String> successInvited = new ArrayList<>();
        for (String username : arrOfUsers) {
            username = username.trim();
            if (!usersInChatroom.contains(username) && userService.userHashMap.containsKey(username)) {
                chatroom.addUser(username);
                successInvited.add(username);
                userService.get(username).addMyChatrooms(chatroomId);
            }
        }
        String inviteContent = "";
        if (successInvited.size() == 0) {
            return false;
        } else if (successInvited.size() == 1) {
            inviteContent = successInvited.get(0) + " is invited to the chatroom";
        } else {
            for (String user : successInvited) {
                user += " ";
                inviteContent += user;
            }
            inviteContent += "are invited to the chatroom";
        }
        messageService.sendMessage("invite", inviterUser, chatroom,
                inviteContent, null);
        return true;
    }

    /**
     * Ban a user in a chatroom.
     * @param banUser username.
     * @param chatroomId chatroom id.
     * @return the result of ban.
     */
    @Override
    public boolean ban(String banUser, int chatroomId, String adminUser) {
        AChatroom chatroom = chatroomService.get(chatroomId);
        User user = userService.get(banUser);
        List<String> bans = chatroom.getBans();
        if (user == null || chatroom == null || !chatroom.getAdmins().contains(adminUser) || bans.contains(banUser)) {
            return false;
        }

        String banContent = banUser + " is banned by admin";
        messageService.sendMessage("ban", user, chatroom,
                banContent, null);

        chatroom.addBan(banUser);
        chatroom.getUsers().remove(banUser);
        user.getMyChatrooms().remove(Integer.valueOf(chatroomId));
        return true;
    }

    /**
     * Ban a user in a chatroom.
     * @param deleteUser username.
     * @param chatroomId chatroom id.
     * @return the result of delete.
     */
    @Override
    public boolean deleteUser(String deleteUser, int chatroomId, String adminUser) {
        AChatroom chatroom = chatroomService.get(chatroomId);
        User user = userService.get(deleteUser);
        List<String> bans = chatroom.getBans();
        if (user == null || chatroom == null || !chatroom.getAdmins().contains(adminUser) || !chatroom.getUsers().contains(deleteUser)) {
            return false;
        }

        String deleteContent = deleteUser + " is forced to leave";
        messageService.sendMessage("delete", user, chatroom,
                deleteContent, null);

        chatroom.getUsers().remove(deleteUser);
        user.getMyChatrooms().remove(Integer.valueOf(chatroomId));
        return true;
    }

    /**
     * Warn a user.
     * @param username username
     */
    @Override
    public void warn(String username, int chatroomId) {
        String warnContent = null;
        AChatroom receiverChatroom = chatroomService.get(chatroomId);
        User warnUser = userService.get(username);
        int status = warnUser.getStatus();
        if (status < 2) {
            warnUser.setStatus(2);
            warnContent = username + " is warned because of using hate word.";
            messageService.sendMessage("warn", warnUser, receiverChatroom,
                     warnContent , null);
        } else {
            warnUser.setStatus(3);
            banAll(warnUser);
            leaveAll(username,1);
        }
    }

    /**
     * User is baned from all rooms.
     * @param user the user to ban.
     */
    @Override
    public void banAll(User user) {
        String username = user.getUsername();
        List<AChatroom> roomList = chatroomService.getAll();
        for (int i = 0; i < roomList.size(); i++) {
            AChatroom chatroom = roomList.get(i);
            List<String> bans = chatroom.getBans();
            if (!bans.contains(username)) {
                if (user.getMyChatrooms().contains(Integer.valueOf(chatroom.getId()))) {
                    user.getMyChatrooms().remove(Integer.valueOf(chatroom.getId()));
                    String warnContent = username + " is baned because of using hate word.";
                    messageService.sendMessage("ban", user, chatroom,
                            warnContent, null);
                }
                chatroom.addBan(username);
                chatroom.getUsers().remove(username);
            }
        }
    }

    /**
     * A user is online.
     * @param username username
     */
    @Override
    public void online(Session userSession, String username) {
        User user = userService.get(username);
        UserService.onlineUserHashMap.put(user, userSession);
    }

    /**
     * Notifies room & removes user from online list when they go offline.
     * @param username user's username
     */
    @Override
    public void offline(String username) {
        User user = userService.get(username);
        if (UserService.onlineUserHashMap.containsKey(user)) {
            UserService.onlineUserHashMap.remove(user);
        }
        String msg = "user " + username + " leaves because of connection close";
        for (int chatroomId : user.getMyChatrooms()) {
            AChatroom chatRoom = chatroomService.get(chatroomId);
            messageService.sendMessage("offline", user, chatRoom, msg, null);
        }
    }

    /**
     * A user leave the room.
     * @param username username
     * @param chatroomId chatroom id
     * @param reasonCode reason for leaving
     */
    @Override
    public void leave(String username, int chatroomId, int reasonCode) {
        if (userService.get(username) != null && chatroomService.get(chatroomId) != null ) {
            String msg = null;
            User user = userService.get(username);
            switch (reasonCode) {
                case 0 : // voluntarily left
                    msg = "user " + username + " leaves room voluntarily.";
                    break;
                case 1: // being baned
                    msg = "user " + username + " leaves room because of being baned.";
                    break;
                default:
                    break;
            }

            userService.leaveRoom(username,chatroomId);
            chatroomService.removeUser(username,chatroomId);
            //Adjust admin
            chatroomService.removeAdmin(username,chatroomId);
            List<String> adminList = getAdmin(chatroomId);
            AChatroom chatRoom = chatroomService.get(chatroomId);
            if (adminList.size() == 0 && !chatRoom.getUsers().isEmpty()) {
                String newAdmin = chatRoom.getUsers().get(0);
                setAdmin(newAdmin,chatroomId);
            }
            messageService.sendMessage("leave", user, chatRoom, msg, null);
        }
    }

    /**
     * User leave all the joined room.
     * @param username username
     * @param reasonCode reason for leaving
     */
    @Override
    public void leaveAll(String username, int reasonCode) {
        if (userService.get(username) != null) {
            List<Integer> roomList = new ArrayList<>(userService.allJoinedRooms(username));
            for (int i  = 0; i < roomList.size(); i++) {
                leave(username, roomList.get(i), reasonCode);
            }
        }
    }

    /**
     * Report a user.
     * @param sender user who report the other
     * @param receiver user who get reported
     * @param chatroomId chatroom that receive report
     */
    @Override
    public void reportUser(String sender, String receiver, int chatroomId) {
        User senderUser = userService.get(sender);
        AChatroom receiverChatroom = chatroomService.get(chatroomId);
        for (String username : receiverChatroom.getAdmins()) {
            User directToUser = userService.get(username);
            messageService.sendMessage("notification", senderUser, receiverChatroom,
                    sender + " reports " + receiver, directToUser);
        }
    }

    /**
     * Recall a message.
     * @param messageId message id
     */
    @Override
    public void recallMessage(int messageId, int chatroomId, String username) {
        AChatroom chatroom = chatroomService.get(chatroomId);
        List<String> users = chatroom.getUsers();
        chatroom.deleteMessage(messageId);
        Gson gson = new Gson();
        for (String receiver : users) {
            try {
                User user = UserService.makeService().get(receiver);
                Session session = UserService.onlineUserHashMap.get(user);
                if (session != null) {
                    session.getRemote().sendString(gson.toJson(Integer.toString(messageId)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Edit a message.
     * @param messageId message id
     */
    @Override
    public void editMessage(int messageId, int chatroomId, String username, String text) {
        AChatroom chatroom = chatroomService.get(chatroomId);
        List<String> users = chatroom.getUsers();
        Collection<AMessage> set = chatroom.getMessages();
        chatroom.editMessage(messageId, text);
        for (AMessage msg : set) {
            if (msg.getId() == messageId) {
                msg.setData(text);
                break;
            }
        }
        Gson gson = new Gson();
        for (String receiver : users) {
            try {
                User user = UserService.makeService().get(receiver);
                Session session = UserService.onlineUserHashMap.get(user);
                if (session != null) {
                    session.getRemote().sendString(gson.toJson(messageId + "&" + text));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete a message.
     * @param messageId message id
     */
    @Override
    public boolean deleteMessage(int messageId, int chatroomId, String username) {
        AChatroom chatroom = chatroomService.get(chatroomId);
        List<String> admins = chatroom.getAdmins();
        List<String> users = chatroom.getUsers();
        chatroom.deleteMessage(messageId);
        if (admins.contains(username)) {
            Gson gson = new Gson();
            for (String receiver : users) {
                try {
                    User user = UserService.makeService().get(receiver);
                    Session session = UserService.onlineUserHashMap.get(user);
                    if (session != null) {
                        session.getRemote().sendString(gson.toJson(Integer.toString(messageId)));
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Get the block user list.
     * @param username user to get list for.
     * @return blocked users list.
     */
    @Override
    public List<String> getBlockList(String username) {
        User user = userService.get(username);
        ArrayList<String> blockUsers = user.getUsersBlocked();
        return blockUsers;
    }
}
