package edu.rice.comp504.service;

import edu.rice.comp504.model.chatroom.AChatroom;
import edu.rice.comp504.model.message.AMessage;
import edu.rice.comp504.model.user.User;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;
import java.util.SplittableRandom;

public interface IMediatorService {

    /**
     * Register the new user.
     * @param username String username.
     * @param pwd String password.
     * @param firstName String first name.
     * @param lastName String last name.
     * @param age String age.
     * @param gender String gender.
     * @param school String school.
     * @param department String department.
     * @param major String major.
     * @param interests String interests.
     * @return the new user.
     */
    public User register(String username, String pwd, String firstName, String lastName,
                         String age, String gender, String school, String department,
                         String major, String interests);

    /**
     * Login the user.
     * @param username username.
     * @param pwd password.
     * @return the user object.
     */
    public User login(String username, String pwd);

    /**
     * Get Profile.
     * @param username username.
     * @return the user profile.
     */
    public User getProfile(String username);

    /**
     * Block a user.
     * @param username username who wants to block the other
     * @param blockUser username who will be blocked
     */
    public List<String> block(String username, String blockUser);

    /**
     * Unblock a user.
     * @param username username who wants to unblock others.
     * @param unblockUser username who will be unblocked.
     * @return block list or null.
     */
    public List<String> unblock(String username, String unblockUser);

    /**
     * Get the chatroom.
     * @param chatroomId chatroom id.
     * @return the chatroom object.
     */
    public AChatroom getChatroom(int chatroomId);

    /**
     * Get all users in the chatroom.
     * @param chatroomId chatroom id.
     * @return a list of all the user in the room.
     */
    public List<String> getAllUsers(int chatroomId);

    /**
     * Get all the chatroom that user joined.
     * @param username username.
     * @return a list of all the chatroom that user joined.
     */
    public List<AChatroom> getJoinedRooms(String username);

    /**
     * Get all the chatroom.
     * @return a list of all the chatroom.
     */
    public List<AChatroom> getAllRooms();

    /**
     * Create a chatroom.
     * @param username username.
     * @param chatroomName chatroom name.
     * @param type chatroom type.
     * @param description chatroom description.
     * @return a new chatroom object.
     */
    public AChatroom createChatroom(String username, String chatroomName, String type, String description);

    /**
     * Request to join a chatroom.
     * @param username username who wants to join.
     * @param chatroomId chatroom name.
     * @return if join successfully.
     */
    public boolean requestJoin(String username, String chatroomId);

    /**
     * Respond to a request to join.
     * @param username username who wants to join.
     * @param chatroomId chatroom name.
     * @param response response action.
     * @return if join successfully.
     */
    public boolean respondToJoin(String username, String chatroomId, String response);

    /**
     * Set admin to chatroom.
     * @param username username who will be set as admin.
     * @param chatroomId chatroom id.
     */
    public void setAdmin(String username, int chatroomId);

    /**
     * Get admins of the room.
     * @param chatroomId chatroom id.
     * @return List of admins.
     */
    public List<String> getAdmin(int chatroomId);

    /**
     * Send message.
     * @param type message type.
     * @param data message data.
     * @param sender message sender.
     * @param chatroomId chatroom id that receive the message.
     * @param directTo user that directly receive the message.
     */
    public void sendMessage(String type, String data, String sender, int chatroomId, String directTo);


    /**
     * Get all chatroom messages.
     * @param roomId messages from roomID.
     */
    public List<AMessage> getMessages(int roomId);

    /**
     * Invite user to chatroom.
     * @param usernames username.
     * @param chatroomId chatroom id.
     * @param inviter inviter.
     * @return the result of invite.
     */
    public boolean invite(String usernames, int chatroomId, String inviter);

    /**
     * Ban a user in a chatroom.
     * @param banUser username.
     * @param chatroomId chatroom id.
     * @return the result of ban.
     */
    public boolean ban(String banUser, int chatroomId, String adminUser);

    /**
     * Ban a user in a chatroom.
     * @param deleteUser username.
     * @param chatroomId chatroom id.
     * @return the result of delete.
     */
    public boolean deleteUser(String deleteUser, int chatroomId, String adminUser);

    /**
     * Warn a user.
     * @param username username
     */
    public void warn(String username, int chatroomId);

    /**
     * User is baned from all rooms.
     * @param user the user to ban.
     */
    public void banAll(User user);

    /**
     * A user is online.
     * @param username username
     */
    public void online(Session user, String username);

    /**
     * Notifies room & removes user from online list when they go offline.
     * @param username user's username
     */
    public void offline(String username);

    /**
     * A user leave the room.
     * @param username username
     * @param chatroomId chatroom id
     * @param reasonCode reason for leaving
     */
    public void leave(String username, int chatroomId,  int reasonCode);

    /**
     * A user leave all the rooms.
     * @param username username
     * @param reasonCode reason for leaving
     */
    public void leaveAll(String username, int reasonCode);

    /**
     * Report a user.
     * @param sender user who report the other
     * @param receiver user who get reported
     * @param chatroomId chatroom that receive report
     */
    public void reportUser(String sender, String receiver, int chatroomId);

    /**
     * Recall a message.
     * @param messageId message id
     */
    public void recallMessage(int messageId, int chatroomId, String username);

    /**
     * Edit a message.
     * @param messageId message id
     */
    public void editMessage(int messageId, int chatroomId, String user, String data);

    /**
     * Delete a message.
     * @param messageId message id
     */
    public boolean deleteMessage(int messageId, int chatroomId, String username);

    /**
     * Get the block user list.
     * @param username user to get list for.
     * @return blocked users list.
     */
    public List<String> getBlockList(String username);




}
