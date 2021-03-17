package edu.rice.comp504.model.factory;

import edu.rice.comp504.model.chatroom.AChatroom;
import edu.rice.comp504.model.chatroom.PrivateChatroom;
import edu.rice.comp504.model.chatroom.PublicChatroom;
import edu.rice.comp504.model.user.User;

import java.util.ArrayList;

public class UserFactory {
    private static UserFactory singleton;

    /**
     * Only makes 1 user factory.
     * @return The user factory
     */
    public static UserFactory makeFactory() {
        if (singleton == null ) {
            singleton = new UserFactory();
        }
        return singleton;
    }

    /**
     * Make object of user.
     * @return The user object
     */
    public User makeUser(String username, String name, String pwd, int age, String gender,
                                  String school, String department, String major, ArrayList interests) {
        User user = new User(username,name,pwd,age,gender,school,department,major,interests);
        return user;
    }
}
