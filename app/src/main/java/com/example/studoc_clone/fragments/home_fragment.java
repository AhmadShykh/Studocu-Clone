package com.example.studoc_clone.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studoc_clone.R;
import com.example.studoc_clone.adapters.DocumentAdapter;
import com.example.studoc_clone.models.Document;
import com.example.studoc_clone.ui.ProfileHome;
import com.example.studoc_clone.ui.SettingsHome;
import com.example.studoc_clone.utils.DocumentViewerActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class home_fragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;

    private ImageView settingsIcon;
    private ImageView profileIcon;


    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<Document> documentList = new ArrayList<>();
    private DatabaseReference databaseReference;

    private static final String RECENT_DOCUMENTS_KEY = "recent_documents";
    private List<String> recentDocumentIds = new ArrayList<>();
    private RecyclerView recentRecyclerView;

    private LinearLayout recentViewLayout;
    private LinearLayout recentDocumentLayout;


    private DocumentAdapter recentAdapter;

    View loggedInView;
    View loggedOutView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        settingsIcon = rootView.findViewById(R.id.settingsIcon);
        profileIcon = rootView.findViewById(R.id.profileImg);


        rootView.findViewById(R.id.chevronLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.myFavourites_layout).setVisibility(View.GONE);
                rootView.findViewById(R.id.main_activity).setVisibility(View.VISIBLE);
            }
        });

        rootView.findViewById(R.id.myFavourites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.myFavourites_layout).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.main_activity).setVisibility(View.GONE);
            }
        });


        // Show loading screen while main content is being loaded
        new Handler().postDelayed(() -> {
            View loadingScreen = rootView.findViewById(R.id.loading_screen);
            View mainContent = rootView.findViewById(R.id.main_activity);

            // Hide loading screen and show main content
            if (loadingScreen != null && mainContent != null) {
                loadingScreen.setVisibility(View.GONE);
                mainContent.setVisibility(View.VISIBLE);
            }
        }, 2000);

        settingsIcon.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), SettingsHome.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(view -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(requireContext(), ProfileHome.class);
                intent.putExtra("userName", user.getDisplayName());
                intent.putExtra("userEmail", user.getEmail());
                intent.putExtra("userPhotoUrl", (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        rootView.findViewById(R.id.continueButton).setOnClickListener(v -> signIn());
        rootView.findViewById(R.id.continueButton2).setOnClickListener(v -> signIn());
        rootView.findViewById(R.id.signInButton).setOnClickListener(v -> signIn());

        loggedInView = rootView.findViewById(R.id.logged_in_main_content);
        loggedOutView = rootView.findViewById(R.id.logged_out_main_content);

        databaseReference = FirebaseDatabase.getInstance().getReference("documents");

        recyclerView = rootView.findViewById(R.id.recommendedRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        adapter = new DocumentAdapter(getContext(), documentList, document -> {
            // Save to recent documents
            saveRecentDocumentId(document.getDocId());

            // Handle document click
            Intent intent = new Intent(requireContext(), DocumentViewerActivity.class);
            intent.putExtra("PDF_URL", document.getFileUrl());
            startActivity(intent);


        });

        recyclerView.setAdapter(adapter);

        // Initialize recent RecyclerView
        recentRecyclerView = rootView.findViewById(R.id.recent_documents);
        recentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recentAdapter = new DocumentAdapter(getContext(), new ArrayList<>(), document -> {
            // Handle document click
            Intent intent = new Intent(requireContext(), DocumentViewerActivity.class);
            intent.putExtra("PDF_URL", document.getFileUrl());
            startActivity(intent);
        });
        recentRecyclerView.setAdapter(recentAdapter);

        recentViewLayout = rootView.findViewById(R.id.recent_viewed_layout);
        recentDocumentLayout = rootView.findViewById(R.id.recent_documents_layout);



        return rootView;
    }

    private void loadDocuments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                documentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Document document = dataSnapshot.getValue(Document.class);
                    if (document != null) {
                        documentList.add(document);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            profileIcon.setVisibility(View.VISIBLE);
            settingsIcon.setVisibility(View.GONE);
            updateUserUI(user);
        } else {
            profileIcon.setVisibility(View.GONE);
            settingsIcon.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserUI(FirebaseUser user) {

        if(user != null){
            loggedOutView.setVisibility(View.GONE);
            loggedInView.setVisibility(View.VISIBLE);

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(profileIcon);
            } else {
                profileIcon.setImageResource(R.drawable.profile_logo);
            }

            loadDocuments();
            // Load saved recent documents
            loadRecentDocumentIds();
            loadRecentDocuments();

        }
        else{
            loggedOutView.setVisibility(View.VISIBLE);
            loggedInView.setVisibility(View.GONE);
        }


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            GoogleSignInAccount account = task.getResult();
                            if (account != null) {
                                firebaseAuthWithGoogle(account);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(requireContext(), "Welcome " + (user != null ? user.getDisplayName() : ""), Toast.LENGTH_SHORT).show();


                        onStart();
                    } else {
                        Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveRecentDocumentId(String documentId) {
        // Update the list of recent document IDs
        if (recentDocumentIds.contains(documentId)) {
            recentDocumentIds.remove(documentId);
        }
        recentDocumentIds.add(0, documentId); // Add to the top
        if (recentDocumentIds.size() > 15) {
            recentDocumentIds.remove(recentDocumentIds.size() - 1); // Keep only the last 15
        }

        // Save to SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        editor.putString(RECENT_DOCUMENTS_KEY, gson.toJson(recentDocumentIds));
        editor.apply();
    }

    private void loadRecentDocumentIds() {
        SharedPreferences preferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String json = preferences.getString(RECENT_DOCUMENTS_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            recentDocumentIds = gson.fromJson(json, new TypeToken<List<String>>() {}.getType());
        }

    }

    private void loadRecentDocuments() {
        // Check if there are no recent documents
        if (recentDocumentIds.isEmpty()) {
            recentViewLayout.setVisibility(View.GONE);  // Hide the logged-in content view
            recentDocumentLayout.setVisibility(View.GONE);  // Hide the logged-out content view
            return;
        }



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Document> recentDocuments = new ArrayList<>();
                for (String id : recentDocumentIds) {
                    DataSnapshot child = snapshot.child(id);
                    Document document = child.getValue(Document.class);
                    if (document != null) {
                        recentDocuments.add(document);
                    }
                }

                // If no documents were found, hide both views
                if (recentDocuments.isEmpty()) {
                    loggedInView.setVisibility(View.GONE);
                    loggedOutView.setVisibility(View.GONE);
                } else {
                    // Update the recent documents adapter with the loaded documents
                    recentAdapter.updateData(recentDocuments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load recent documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
