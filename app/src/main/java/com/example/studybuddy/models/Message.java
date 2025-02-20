package com.example.studybuddy.models;

import java.util.Date;

public class Message {
    private String id;
    private String content;
    private String senderId;
    private String senderName;
    private Date timestamp;
    private String type; // text, image, file, etc.
    private String fileUrl; // for attachments

    public Message() {
        timestamp = new Date();
        type = "text";
    }

    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public Date getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public String getFileUrl() { return fileUrl; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public void setType(String type) { this.type = type; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}