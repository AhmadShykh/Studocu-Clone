package com.example.studoc_clone.models;


import java.util.List;

public class Group {
    private String groupId;
    private String creatorId;
    private String name;
    private List<String> members;  // List of user IDs
    private String country;
    private String description;

    // Default constructor required for Firebase deserialization
    public Group() {}

    public Group(String groupId, String creatorId, String name, List<String> members, String country, String description) {
        this.groupId = groupId;
        this.creatorId = creatorId;
        this.name = name;
        this.members = members;
        this.country = country;
        this.description = description;
    }

    // Getters and setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
