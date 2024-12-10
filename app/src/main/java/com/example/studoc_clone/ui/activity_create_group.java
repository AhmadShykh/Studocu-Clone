package com.example.studoc_clone.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studoc_clone.R;
import com.example.studoc_clone.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class activity_create_group extends AppCompatActivity {

    TextView nameField ;
    TextView descriptionField ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);

        nameField = findViewById(R.id.nameField);
        descriptionField = findViewById(R.id.descriptionField);

        findViewById(R.id.crossIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.createText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameField.getText() != null ? nameField.getText().toString().trim() : "";
                String description = descriptionField.getText() != null ? descriptionField.getText().toString().trim() : "";

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Any field should not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    saveGroupToFirebase(name, "Pakistan", description, new ArrayList<String>());
                }
            }
        });

    }


    public void saveGroupToFirebase(String name, String country, String description, List<String> members) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String creatorId = currentUser.getUid();
            members.add(creatorId);
            // Reference to Firebase database
            DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            // Generate a new group ID
            String groupId = groupsRef.push().getKey();

            if (groupId != null) {
                // Create the group object
                Group group = new Group(groupId, creatorId, name, members, country, description);

                // Save the group to the "groups" collection
                groupsRef.child(groupId).setValue(group).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Add the group ID to the creator's group list
                        usersRef.child(creatorId).child("groupIds").get().addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                List<String> groupIds = (List<String>) userTask.getResult().getValue();
                                if (groupIds == null) {
                                    groupIds = new ArrayList<>();
                                }
                                groupIds.add(groupId);

                                // Update the user's groupIds list
                                usersRef.child(creatorId).child("groupIds").setValue(groupIds)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                System.out.println("Group added successfully.");
                                                Toast.makeText(getApplicationContext(), "Group Created", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                System.err.println("Failed to update user's group list.");
                                            }
                                        });
                            } else {
                                System.err.println("Failed to fetch user's current group list.");
                            }
                        });
                    } else {
                        System.err.println("Failed to save group.");
                    }
                });
            } else {
                System.err.println("Failed to generate group ID.");
            }
        } else {
            System.err.println("User is not logged in.");
        }
    }
}