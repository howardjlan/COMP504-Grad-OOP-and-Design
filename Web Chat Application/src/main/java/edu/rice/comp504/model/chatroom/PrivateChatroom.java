package edu.rice.comp504.model.chatroom;

public class PrivateChatroom extends AChatroom {
    /** Constructor.
     * @param id   chatroom id
     * @param name   chatroom name
     * @param description   chatroom description
     */
    public PrivateChatroom(int id, String name, String description) {
        super(id, name, "Private", description);
    }
}
