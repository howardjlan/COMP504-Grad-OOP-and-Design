package edu.rice.comp504.model.chatroom;


public class PublicChatroom extends AChatroom {

    /** Constructor.
     * @param id    chatroom id
     * @param name   chatroom name
     * @param description   chatroom description
     */
    public PublicChatroom(int id, String name, String description) {
        super(id, name, "Public", description);
    }
}
