package com.example.studoc_clone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.studoc_clone.R;
import com.example.studoc_clone.adapters.GroupAdapter;
import com.example.studoc_clone.models.Group;
import com.example.studoc_clone.ui.activity_create_group;
import com.example.studoc_clone.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class messages_fragment extends Fragment {

    FirebaseUser user;
    List<Group> groupList = new ArrayList<>();
    GroupAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_messages_fragment, container, false);

        fetchUserGroups();




        RecyclerView recyclerView = rootView.findViewById(R.id.messages_fragment);

        adapter = new GroupAdapter(getContext(), groupList, group -> {
            // Handle group item click
            Toast.makeText(getContext(), "Clicked: " + group.getName(), Toast.LENGTH_SHORT).show();
        });

        rootView.findViewById(R.id.createGroupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), activity_create_group.class);

                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    public void fetchUserGroups() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userGroupsRef = FirebaseUtils.getUsersRef()
                    .child(currentUser.getUid())
                    .child("groupIds");

            userGroupsRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<String> groupIds = (List<String>) task.getResult().getValue();
                    if (groupIds != null) {
                        // Fetch each group details using the group IDs
                        fetchGroupsByIds(groupIds);
                    }
                }
            });
        }
    }

    private void fetchGroupsByIds(List<String> groupIds) {
        DatabaseReference groupsRef = FirebaseUtils.getGroupsRef();
        int[] remainingGroups = {groupIds.size()}; // Counter for remaining groups to fetch

        for (String groupId : groupIds) {
            groupsRef.child(groupId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Group group = task.getResult().getValue(Group.class);
                    if (group != null) {
                        groupList.add(group);
                    }
                }
                // Decrement the counter and notify adapter when all groups are fetched
                remainingGroups[0]--;
                if (remainingGroups[0] == 0) {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

}