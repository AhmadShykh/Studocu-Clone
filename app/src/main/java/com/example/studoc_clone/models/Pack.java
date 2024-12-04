package com.example.studoc_clone.models;

public class Pack {

    private String name;
    private String subName;
    private Type type;

    // Enum for the Type field
    public enum Type {
        BOOK,
        COURSE,
        STUDYLIST
    }

    // Constructor to initialize the fields
    public Pack(String name, String subName, Type type) {
        this.name = name;
        this.subName = subName;
        this.type = type;
    }

    // Getter and setter for Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for SubName
    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    // Getter and setter for Type
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    // Override the toString method to display the Pack object details
    @Override
    public String toString() {
        return "Pack{" +
                "name='" + name + '\'' +
                ", subName='" + subName + '\'' +
                ", type=" + type +
                '}';
    }


}

