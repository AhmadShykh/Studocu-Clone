package com.example.studoc_clone.models;

public class Comment {
    private String commentId;
    private String userId;
    private String docId;
    private String text;
    private long timestamp;

    // Default constructor
    public Comment() {}

    public Comment(String commentId, String userId, String docId, String text, long timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.docId = docId;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
