package com.example.studoc_clone.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.studoc_clone.adapters.PackAdapter;
import com.example.studoc_clone.models.Document;
import com.example.studoc_clone.models.Pack;
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
import com.google.firebase.database.Query;
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



    private List<Document> documentList = new ArrayList<>();

    private ArrayList<Pack> booksList = new ArrayList<>();
    private ArrayList<Pack> modulesList = new ArrayList<>();
    private ArrayList<Pack> studylistList = new ArrayList<>();


    private DatabaseReference databaseReference;

    private DocumentAdapter adapter;

    private PackAdapter booksAdapter;
    private PackAdapter modulesAdapter;
    private PackAdapter studyListAdapter;


    private static final String RECENT_DOCUMENTS_KEY = "recent_documents";
    private static final String RECENT_VIEWED_KEY = "recent_views";
    private List<String> recentDocumentIds = new ArrayList<>();
    private List<String> recentViewedIds = new ArrayList<>();

    private LinearLayout recentViewLayout;
    private LinearLayout recentDocumentLayout;


    private DocumentAdapter recentAdapter;

    private PackAdapter recentViewedAdapter;
    private PackAdapter books;
    private PackAdapter modules;
    private PackAdapter studyList;


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


        // Documents Views
        RecyclerView recyclerView;

        recyclerView = rootView.findViewById(R.id.recommendedRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        adapter = new DocumentAdapter(getContext(), documentList, document -> {
            // Save to recent documents
            saveRecents(document.getDocId(),recentDocumentIds,RECENT_DOCUMENTS_KEY,15);

            // Handle document click
            Intent intent = new Intent(requireContext(), DocumentViewerActivity.class);
            intent.putExtra("PDF_URL", document.getFileUrl());
            startActivity(intent);


        });

        recyclerView.setAdapter(adapter);


        // Recent Documents List
        RecyclerView recentRecyclerView;

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


        // Recent Viewed List
        RecyclerView recentViewedRecyclerView;
        recentViewedRecyclerView = rootView.findViewById(R.id.recent_viewed);
        recentViewedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recentViewedAdapter = new PackAdapter(getContext(),new ArrayList<>(), pack -> {

        });
        recentViewedRecyclerView.setAdapter(recentViewedAdapter);

        // Books List
        RecyclerView booksView;

        booksView = rootView.findViewById(R.id.books_recycle_view);
        booksView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        booksAdapter = new PackAdapter(getContext(),booksList, pack -> {
            // Save to recent documents
            saveRecents(pack.getId(),recentViewedIds,RECENT_VIEWED_KEY,15);
        });

        booksView.setAdapter(booksAdapter);

        // Modules List
        RecyclerView modulesView;

        modulesView = rootView.findViewById(R.id.modules_recycle_view);
        modulesView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        modulesAdapter = new PackAdapter(getContext(),modulesList, pack -> {
            // Save to recent documents
            saveRecents(pack.getId(),recentViewedIds,RECENT_VIEWED_KEY,15);
        });

        modulesView.setAdapter(modulesAdapter);

        // Books List
        RecyclerView studyListView;

        studyListView = rootView.findViewById(R.id.studylist_recycle_view);
        studyListView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        studyListAdapter = new PackAdapter(getContext(),studylistList, pack -> {
            // Save to recent documents
            saveRecents(pack.getId(),recentViewedIds,RECENT_VIEWED_KEY,15);
        });

        studyListView.setAdapter(studyListAdapter);



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

    private void fetchPacksByType(String packType, ArrayList<Pack> packs, Runnable onComplete) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("packs");

        // Query packs by type
        Query query = databaseReference.orderByChild("type").equalTo(packType);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packs.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot child : snapshot.getChildren()) {
                    Pack pack = child.getValue(Pack.class);
                    if (pack != null) {
                        packs.add(pack); // Add fetched packs to the provided list
                    }
                }
                if (onComplete != null) {
                    onComplete.run(); // Notify when the operation is complete
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchPacksError", error.getMessage()); // Log the error
            }
        });
    }




    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUserUI(user);

    }

    private void updateUserUI(FirebaseUser user) {

        if(user != null){
            profileIcon.setVisibility(View.VISIBLE);
            settingsIcon.setVisibility(View.GONE);
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
            loadRecents(recentDocumentIds,RECENT_DOCUMENTS_KEY);
            loadRecents(recentViewedIds,RECENT_VIEWED_KEY);
            loadRecentDocuments();
            loadRecentViewed();

            fetchPacksByType("BOOK", booksList, () -> booksAdapter.notifyDataSetChanged());
            fetchPacksByType("COURSE", modulesList, () -> modulesAdapter.notifyDataSetChanged());
            fetchPacksByType("STUDYLIST", studylistList, () -> studyListAdapter.notifyDataSetChanged());



        }
        else{
            loggedOutView.setVisibility(View.VISIBLE);
            loggedInView.setVisibility(View.GONE);
            profileIcon.setVisibility(View.GONE);
            settingsIcon.setVisibility(View.VISIBLE);
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


    private void saveRecents(String id,List<String> recentIds, String KEY,int limit ) {
        // Update the list of recent document IDs
        if (recentIds.contains(id)) {
            recentIds.remove(id);
        }
        recentIds.add(0, id); // Add to the top
        if (recentIds.size() > limit) {
            recentIds.remove(recentIds.size() - 1); // Keep only the last 15
        }

        // Save to SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        editor.putString(KEY, gson.toJson(recentIds));
        editor.apply();
    }



    private void loadRecents(List<String> recentList, String KEY) {
        SharedPreferences preferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String json = preferences.getString(KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            List<String> newList = gson.fromJson(json, new TypeToken<List<String>>() {}.getType());

            // Clear the original list and add all items from the new list
            recentList.clear();
            if (newList != null) {
                recentList.addAll(newList);
            }
        }
    }



    private void loadRecentDocuments( ) {
        // Check if there are no recent documents
        if (recentDocumentIds.isEmpty()) {
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
                if (!recentDocuments.isEmpty()) {
                    recentAdapter.updateData(recentDocuments);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load recent documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadRecentViewed( ) {
        // Check if there are no recent documents
        if (recentViewedIds.isEmpty()) {
            recentViewLayout.setVisibility(View.GONE);  // Hide the logged-out content view
            return;
        }



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Pack> recentPacks = new ArrayList<>();
                for (String id : recentViewedIds) {
                    DataSnapshot child = snapshot.child(id);
                    Pack pack = child.getValue(Pack.class);
                    if (pack != null) {
                        recentPacks.add(pack);
                    }
                }

                // If no documents were found, hide both views
                if (!recentPacks.isEmpty()) {
                    recentViewedAdapter.updateData(recentPacks);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load recent documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }





}
