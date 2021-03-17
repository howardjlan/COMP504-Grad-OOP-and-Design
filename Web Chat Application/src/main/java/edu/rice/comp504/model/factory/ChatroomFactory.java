package edu.rice.comp504.model.factory;

import edu.rice.comp504.model.chatroom.AChatroom;
import edu.rice.comp504.model.chatroom.PrivateChatroom;
import edu.rice.comp504.model.chatroom.PublicChatroom;

public class ChatroomFactory {
    private static ChatroomFactory singleton;

    /**
     * Only makes 1 chatroom factory.
     * @return The chatroom factory
     */
    public static ChatroomFactory makeFactory() {
        if (singleton == null ) {
            singleton = new ChatroomFactory();
        }
        return singleton;
    }

    /**
     * Make object of chatroom.
     * @return The chatroom object
     */
    public AChatroom makeChatroom(String type, int id, String name, String description) {
        AChatroom chatroom = null;

        switch (type) {
            case "public":
                chatroom = new PublicChatroom(id, name, description);
                break;
            case "private":
                chatroom = new PrivateChatroom(id, name, description);
                break;
            default:
                break;
        }
        return chatroom;
    }
}
