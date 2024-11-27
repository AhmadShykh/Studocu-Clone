package com.example.studoc_clone.models;

public class Document {
    private String docId;
    private String userId;
    private String title;
    private String description;
    private String fileUrl;

    // Default constructor
    public Document() {}

    public Document(String docId, String userId, String title, String description, String fileUrl) {
        this.docId = docId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
    }

    // Getters and setters
    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
}
