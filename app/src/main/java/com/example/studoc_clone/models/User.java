package com.example.studoc_clone.models;

import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private String profilePicture;
    private List<String> groupIds;  // List of group IDs the user is part of

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {}

    public User(String userId, String name, String email, String profilePicture,List<String> groupIds) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.groupIds = groupIds;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public List<String> getGroupIds() { return groupIds; }
    public void setGroupIds(List<String> groupIds) { this.groupIds = groupIds; }
}
