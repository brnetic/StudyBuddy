package com.example.studybuddy.models;

import java.util.Date;

public class Resource {
    private String id;
    private String name;
    private String url;
    private String uploaderId;
    private String uploaderName;
    private Date uploadDate;
    private String type;
    private long size;
    private String description;

    public Resource() {
        uploadDate = new Date();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getUrl() { return url; }
    public String getUploaderId() { return uploaderId; }
    public String getUploaderName() { return uploaderName; }
    public Date getUploadDate() { return uploadDate; }
    public String getType() { return type; }
    public long getSize() { return size; }
    public String getDescription() { return description; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUrl(String url) { this.url = url; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    public void setUploaderName(String uploaderName) { this.uploaderName = uploaderName; }
    public void setUploadDate(Date uploadDate) { this.uploadDate = uploadDate; }
    public void setType(String type) { this.type = type; }
    public void setSize(long size) { this.size = size; }
    public void setDescription(String description) { this.description = description; }
}