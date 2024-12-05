package com.example.studoc_clone.models;

public class Document {
    private String docId;
    private String userId;
    private String title;
    private String description;
    private String universityName;
    private String years;
    private String docType;
    private String folder;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    private String rating;
    private String fileUrl;

    // Default constructor
    public Document() {}

    public Document(String docId, String userId, String title, String description, String fileUrl,String rating,String universityName,String years,String docType,String folder) {
        this.docId = docId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.rating = rating;
        this.years = years;
        this.universityName = universityName;
        this.docType = docType;
        this.folder = folder;
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


    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
