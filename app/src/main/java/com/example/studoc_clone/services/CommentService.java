package com.example.studoc_clone.services;

import android.util.Log;

import com.example.studoc_clone.models.Comment;
import com.example.studoc_clone.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CommentService {
    private DatabaseReference commentsRef;

    public CommentService() {
        commentsRef = FirebaseUtils.getCommentsRef();
    }

    public void addComment(Comment comment) {
        commentsRef.child(comment.getCommentId()).setValue(comment)
                .addOnSuccessListener(aVoid -> Log.d("CommentService", "Comment added successfully"))
                .addOnFailureListener(e -> Log.e("CommentService", "Failed to add comment", e));
    }

    public void getComments(String docId, OnSuccessListener<List<Comment>> successListener, OnFailureListener failureListener) {
        commentsRef.orderByChild("docId").equalTo(docId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    comments.add(snapshot.getValue(Comment.class));
                }
                successListener.onSuccess(comments);
            } else {
                failureListener.onFailure(task.getException());
            }
        });
    }
}
