package com.example.studybuddy.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private List<String> courses;
    private List<String> groups;
    private String profileImageUrl;
    private boolean isOnline;
    private long lastSeen;

    public User() {
        courses = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public User(String id, String name, String email, List<String> courses) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.courses = courses != null ? courses : new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getCourses() { return courses; }
    public List<String> getGroups() { return groups; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public boolean isOnline() { return isOnline; }
    public long getLastSeen() { return lastSeen; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setCourses(List<String> courses) { this.courses = courses; }
    public void setGroups(List<String> groups) { this.groups = groups; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setOnline(boolean online) { isOnline = online; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }

    // Helper methods
    public void addCourse(String course) {
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    public void removeCourse(String course) {
        courses.remove(course);
    }

    public void addGroup(String groupId) {
        if (!groups.contains(groupId)) {
            groups.add(groupId);
        }
    }

    public void removeGroup(String groupId) {
        groups.remove(groupId);
    }
}