package com.socio.sociosphere.Messagenew;

public class MessageModel {

    private String messageId;
    private String senderId;
    private String message;
    private long timestamp;


    public MessageModel(String message, String senderId, String messageId,Long timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.messageId = messageId;
        this.timestamp = timestamp;
    }

    public MessageModel(){

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
