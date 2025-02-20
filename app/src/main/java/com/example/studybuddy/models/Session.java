package com.example.studybuddy.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Session implements Serializable {
    private String id;
    private String title;
    private String location;
    private String date;
    private String startTime;
    private String endTime;
    private List<String> participants;
    private String groupId;
    private String createdBy;
    private Date createdAt;

    public Session() {
        participants = new ArrayList<>();
        createdAt = new Date();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public List<String> getParticipants() { return participants; }
    public String getGroupId() { return groupId; }
    public String getCreatedBy() { return createdBy; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setLocation(String location) { this.location = location; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public void addParticipant(String userId) {
        if (!participants.contains(userId)) {
            participants.add(userId);
        }
    }

    public void removeParticipant(String userId) {
        participants.remove(userId);
    }

    public boolean isParticipant(String userId) {
        return participants.contains(userId);
    }
}