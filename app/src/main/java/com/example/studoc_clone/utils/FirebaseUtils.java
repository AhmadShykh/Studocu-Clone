package com.example.studoc_clone.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    public static FirebaseDatabase getDatabase() {
        return FirebaseDatabase.getInstance();
    }

    public static DatabaseReference getUsersRef() {
        return getDatabase().getReference("users");
    }

    public static DatabaseReference getDocumentsRef() {
        return getDatabase().getReference("documents");
    }

    public static DatabaseReference getCommentsRef() {
        return getDatabase().getReference("comments");
    }
}
