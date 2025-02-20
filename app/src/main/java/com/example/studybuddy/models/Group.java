package com.example.studybuddy.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group implements Serializable {
    private String id;
    private String name;
    private String courseName;
    private List<String> members;
    private Date createdAt;
    private String createdBy;

    public Group() {
        members = new ArrayList<>();
        createdAt = new Date();
    }

    public Group(String name, String courseName) {
        this();
        this.name = name;
        this.courseName = courseName;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCourseName() { return courseName; }
    public List<String> getMembers() { return members; }
    public Date getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setMembers(List<String> members) { this.members = members; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    // Helper methods
    public void addMember(String userId) {
        if (!members.contains(userId)) {
            members.add(userId);
        }
    }

    public void removeMember(String userId) {
        members.remove(userId);
    }

    public boolean isMember(String userId) {
        return members.contains(userId);
    }

    public boolean isAdmin(String userId) {
        return members.size() > 0 && members.get(0).equals(userId);
    }
}