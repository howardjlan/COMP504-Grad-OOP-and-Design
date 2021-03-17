package edu.rice.comp504.service;

import edu.rice.comp504.model.factory.UserFactory;
import edu.rice.comp504.model.user.User;
import org.eclipse.jetty.websocket.api.Session;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserService extends AGenericService<User> {
    static Map<String, User> userHashMap = new ConcurrentHashMap<>();
    static Map<User, Session> onlineUserHashMap = new ConcurrentHashMap<>();

    private final UserFactory userFactory = UserFactory.makeFactory();
    private static UserService singleton;

    /**
     * Initialize the UserService class.
     */
    public UserService() {
        createAccount("admin", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        createAccount("test", "test", "test", "test", "0",
                "Male", "Rice University", "CS", "CS", "CS");
    }

    /**
     * Only makes 1 UserService.
     * @return The UserService
     */
    public static UserService makeService() {
        if (singleton == null ) {
            singleton = new UserService();
        }
        return singleton;
    }

    @Override
    public User get(String name) {
        return userHashMap.get(name);
    }

    /**
     * Create a new account for the user.
     * @param username The username
     * @param pwd The password
     * @return new user account
     */
    public User createAccount(String username, String pwd, String firstName, String lastName,
                              String age, String gender, String school, String department,
                              String major,String interests) {
        if (userHashMap.containsKey(username)) {
            return null;
        }
        int a = 0;
        if (!age.equals("")) {
            a = Integer.parseInt(age);
        }
        String name = firstName + " " + lastName;
        ArrayList<String> interestList = new ArrayList<>(Arrays.asList(interests.split(",")));
        User user = userFactory.makeUser(username, name, pwd, a, gender, school, department, major, interestList);
        userHashMap.put(username,user);
        //Add user to the general room.
        user.addMyChatrooms(1);
        return user;
    }

    /**
     * Check if user's login information is correct and return all user's information.
     * @param username login username
     * @param pwd      login password
     * @return         User object
     */
    public User login(String username, String pwd) {
        User user = userHashMap.get(username);
        if (user == null) {
            return null;
        }
        if (user.getPwd().equals(pwd)) {
            return user;
        }
        return null;
    }

    /**
     * Add username to the user's block list.
     * @param username user's username
     * @param blockUser blocked user's username.
     */
    public List<String> blockUser(String username, String blockUser) {
        if (userHashMap.containsKey(username) && userHashMap.containsKey(blockUser)) {
            User user = userHashMap.get(username);
            if (!user.getUsersBlocked().contains(blockUser)) {
                user.setUsersBlocked(blockUser);
                return user.getUsersBlocked();
            }
        }
        return null;
    }

    /**
     * Remove a user from the user's block list.
     * @param username user's username
     * @param unblockUser unblocked user's username
     * @return user's block list
     */
    public List<String> unblockUser(String username, String unblockUser) {
        if (userHashMap.containsKey(username) && userHashMap.containsKey(unblockUser)) {
            User user = userHashMap.get(username);
            if (user.getUsersBlocked().contains(unblockUser)) {
                user.removeUsersBlocked(unblockUser);
                return user.getUsersBlocked();
            }
        }
        return null;
    }

    /**
     * Get all chat rooms that the user could join.
     * @param username user's username
     * @return the list of joined rooms id.
     */
    public List<Integer> allJoinedRooms(String username) {
        if (userHashMap.containsKey(username)) {
            User u = userHashMap.get(username);
            List<Integer> roomIdList = u.getMyChatrooms();
            return roomIdList;
        }
        return null;
    }

    /**
     * User join a chat room.
     * @param username user trying to join.
     * @param chatRoomId room id to join to.
     */
    public void joinRoom(String username, int chatRoomId) {
        if (userHashMap.containsKey(username)) {
            User u = userHashMap.get(username);
            if (!u.getMyChatrooms().contains(chatRoomId)) {
                u.addMyChatrooms(chatRoomId);
            }
        }
    }

    /**
     * User leaves a chat room.
     * @param username user trying to leave.
     * @param chatRoomId room id to leave from.
     */
    public void leaveRoom(String username, int chatRoomId) {
        User user = userHashMap.get(username);
        if (user.getMyChatrooms().contains(chatRoomId)) {
            user.removeMyChatroom(chatRoomId);
        }
    }

}


