package com.example.studoc_clone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studoc_clone.R;
import com.example.studoc_clone.adapters.GroupAdapter;
import com.example.studoc_clone.models.Group;
import com.example.studoc_clone.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ExplorFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;
    private TextView countryName, totalChats;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_explore_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.explore_recycle_view);
        countryName = rootView.findViewById(R.id.countryName);
        totalChats = rootView.findViewById(R.id.totalChats);

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(getContext(), groupList, group -> {
            // Handle group click
            Toast.makeText(getContext(), "Clicked: " + group.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupAdapter);

        fetchUserCountry();

        return rootView;
    }



    private void fetchUserCountry() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseUtils.getUsersRef().child(currentUser.getUid());
            userRef.child("country").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String country = "Pakistan";
                    if (country != null && !country.isEmpty()) {
                        countryName.setText(country);
                        fetchGroupsByCountry(country);
                    }
                }
            });
        }
    }

    private void fetchGroupsByCountry(String country) {
        DatabaseReference groupsRef = FirebaseUtils.getGroupsRef();
        groupsRef.orderByChild("country").equalTo(country).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group != null) {
                        groupList.add(group);
                    }
                }

                totalChats.setText(String.valueOf(groupList.size()));
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch groups: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
