package com.example.studoc_clone.services;

import android.util.Log;

import com.example.studoc_clone.models.User;
import com.example.studoc_clone.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserService {
    private DatabaseReference usersRef;

    public UserService() {
        usersRef = FirebaseUtils.getUsersRef();
    }

    public void addUser(User user) {
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d("UserService", "User added successfully"))
                .addOnFailureListener(e -> Log.e("UserService", "Failed to add user", e));
    }

    public void getUser(String userId, OnSuccessListener<User> successListener, OnFailureListener failureListener) {
        usersRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                successListener.onSuccess(user);
            } else {
                failureListener.onFailure(task.getException());
            }
        });
    }

    public void updateUser(String userId, String key, String value) {
        usersRef.child(userId).child(key).setValue(value)
                .addOnSuccessListener(aVoid -> Log.d("UserService", "User updated successfully"))
                .addOnFailureListener(e -> Log.e("UserService", "Failed to update user", e));
    }

    public void deleteUser(String userId) {
        usersRef.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> Log.d("UserService", "User deleted successfully"))
                .addOnFailureListener(e -> Log.e("UserService", "Failed to delete user", e));
    }
}
