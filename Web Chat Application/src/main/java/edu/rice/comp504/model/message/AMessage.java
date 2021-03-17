package edu.rice.comp504.model.message;

import java.util.Date;

public abstract class AMessage {
    private int messageId;
    private String sender;
    private int chatroomId;
    private Date time;
    private String messageType;
    private String data;

    /**
     * Constructor.
     * @param messageId message id
     * @param sender user who send message
     * @param chatroomId chatroom which receive the message
     * @param time timestamp of the message
     * @param messageType type of message
     * @param data message content
     */
    public AMessage(int messageId, String sender, int chatroomId, Date time, String messageType, String data) {
        this.messageId = messageId;
        this.sender = sender;
        this.chatroomId = chatroomId;
        this.time = time;
        this.messageType = messageType;
        this.data = data;
    }

    /**
     * Get message id.
     * @return message id
     */
    public int getId() {
        return messageId;
    }

    /**
     * Get message sender.
     * @return message sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Get message receiver.
     * @return message receiver chatroom id
     */
    public int getChatroomId() {
        return chatroomId;
    }

    /**
     * Get timestamp of message.
     * @return timestamp of message
     */
    public Date getTime() {
        return time;
    }

    /**
     * Get message type.
     * @return message type
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Get message data.
     * @return message data
     */
    public String getData() {
        return data;
    }

    /**
     * Set message data.
     * @return message data
     */
    public void setData(String data) {
        this.data = data;
    }

}
