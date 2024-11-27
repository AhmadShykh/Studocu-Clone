package com.example.studoc_clone.services;

import android.util.Log;

import com.example.studoc_clone.models.Document;
import com.example.studoc_clone.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DocumentService {
    private DatabaseReference docsRef;

    public DocumentService() {
        docsRef = FirebaseUtils.getDocumentsRef();
    }

    public void addDocument(Document document) {
        docsRef.child(document.getDocId()).setValue(document)
                .addOnSuccessListener(aVoid -> Log.d("DocumentService", "Document added successfully"))
                .addOnFailureListener(e -> Log.e("DocumentService", "Failed to add document", e));
    }

    public void getDocuments(OnSuccessListener<List<Document>> successListener, OnFailureListener failureListener) {
        docsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Document> documents = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    documents.add(snapshot.getValue(Document.class));
                }
                successListener.onSuccess(documents);
            } else {
                failureListener.onFailure(task.getException());
            }
        });
    }
}
