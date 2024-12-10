package com.example.studoc_clone.ui;

import static com.example.studoc_clone.utils.GlobalUtils.saveRecents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studoc_clone.R;
import com.example.studoc_clone.adapters.DocumentAdapter;
import com.example.studoc_clone.adapters.PackAdapter;
import com.example.studoc_clone.adapters.SearchDocumentAdapter;
import com.example.studoc_clone.models.Document;
import com.example.studoc_clone.models.Pack;
import com.example.studoc_clone.utils.Consts;
import com.example.studoc_clone.utils.DocumentViewerActivity;
import com.example.studoc_clone.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SeeAllActivity extends AppCompatActivity {



    SearchDocumentAdapter recentAdapter;
    PackAdapter recentViewedAdapter;

    SearchDocumentAdapter adapter;

    List<Document> documentList = new ArrayList<>();


    private ArrayList<Pack> booksList = new ArrayList<>();
    private ArrayList<Pack> modulesList = new ArrayList<>();
    private ArrayList<Pack> studylistList = new ArrayList<>();


    private PackAdapter booksAdapter;
    private PackAdapter modulesAdapter;
    private PackAdapter studyListAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.recently_viewed);

        Intent tempIntent = getIntent();
        String name = tempIntent.getStringExtra("LISTNAME");

        TextView txtView = findViewById(R.id.recently_viewed_heading);
        txtView.setText(name);

        findViewById(R.id.chevron_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        if ("Recent Documents".equals(name)) {
            // Recent Documents List
            RecyclerView recentRecyclerView;

            // Initialize recent RecyclerView
            recentRecyclerView = findViewById(R.id.recyleView);
            recentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recentAdapter = new SearchDocumentAdapter(getApplicationContext(), new ArrayList<>(), document -> {
                // Handle document click
                Intent intent = new Intent(SeeAllActivity.this, DocumentViewerActivity.class);
                intent.putExtra("PDF_URL", document.getFileUrl());
                startActivity(intent);
            });
            recentRecyclerView.setAdapter(recentAdapter);

            loadRecentDocuments();

        } else if ("Recent Viewed".equals(name)) {
            // Recent Viewed List
            RecyclerView recentViewedRecyclerView;
            recentViewedRecyclerView = findViewById(R.id.recyleView);
            recentViewedRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recentViewedAdapter = new PackAdapter(getApplicationContext(), new ArrayList<>(), pack -> {
                // Handle pack click
            });
            recentViewedRecyclerView.setAdapter(recentViewedAdapter);

            loadRecentViewed();

        } else if ("Recommended".equals(name)) {
            // Documents Views
            RecyclerView recyclerView;

            recyclerView = findViewById(R.id.recyleView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            adapter = new SearchDocumentAdapter(getApplicationContext(), documentList, document -> {
                // Save to recent documents
                saveRecents(document.getDocId(), Consts.recentDocumentIds, Consts.RECENT_DOCUMENTS_KEY, 15, getApplicationContext());

                // Handle document click
                Intent intent = new Intent(getApplicationContext(), DocumentViewerActivity.class);
                intent.putExtra("PDF_URL", document.getFileUrl());
                startActivity(intent);
            });

            recyclerView.setAdapter(adapter);

            loadDocuments();

        } else if ("Popular Books".equals(name)) {
            // Books List
            RecyclerView booksView;

            booksView = findViewById(R.id.recyleView);
            booksView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            booksAdapter = new PackAdapter(getApplicationContext(), booksList, pack -> {
                // Save to recent documents
                saveRecents(pack.getId(), Consts.recentViewedIds, Consts.RECENT_VIEWED_KEY, 15, getApplicationContext());
            });

            booksView.setAdapter(booksAdapter);

            fetchPacksByType("BOOK", booksList, () -> booksAdapter.notifyDataSetChanged());

        } else if ("Popular Studylist".equals(name)) {
            // Study List
            RecyclerView studyListView;

            studyListView = findViewById(R.id.recyleView);
            studyListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            studyListAdapter = new PackAdapter(getApplicationContext(), studylistList, pack -> {
                // Save to recent documents
                saveRecents(pack.getId(), Consts.recentViewedIds, Consts.RECENT_VIEWED_KEY, 15, getApplicationContext());
            });

            studyListView.setAdapter(studyListAdapter);

            fetchPacksByType("STUDYLIST", studylistList, () -> studyListAdapter.notifyDataSetChanged());

        } else if ("Popular Modules".equals(name)) {
            // Modules List
            RecyclerView modulesView;

            modulesView = findViewById(R.id.recyleView);
            modulesView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            modulesAdapter = new PackAdapter(getApplicationContext(), modulesList, pack -> {
                // Save to recent documents
                saveRecents(pack.getId(), Consts.recentViewedIds, Consts.RECENT_VIEWED_KEY, 15, getApplicationContext());
            });

            modulesView.setAdapter(modulesAdapter);

            fetchPacksByType("COURSE", modulesList, () -> modulesAdapter.notifyDataSetChanged());
        }

    }



    private void loadDocuments() {
        DatabaseReference databaseReference = FirebaseUtils.getDocumentsRef();
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
                Toast.makeText(getApplicationContext(), "Failed to load documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPacksByType(String packType, ArrayList<Pack> packs, Runnable onComplete) {
        DatabaseReference databaseReferenceForPacks = FirebaseUtils.getPacksRef();

        // Query packs by type
        Query query = databaseReferenceForPacks.orderByChild("type").equalTo(packType);

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


    private void loadRecents(List<String> recentList, String KEY) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
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
        DatabaseReference databaseReference = FirebaseUtils.getDocumentsRef();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Document> recentDocuments = new ArrayList<>();
                for (String id : Consts.recentDocumentIds) {
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
                Toast.makeText(getApplicationContext(), "Failed to load recent documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadRecentViewed( ) {

        DatabaseReference databaseReference = FirebaseUtils.getPacksRef();



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Pack> recentPacks = new ArrayList<>();
                for (String id : Consts.recentViewedIds) {
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
                Toast.makeText(getApplicationContext(), "Failed to load recent documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
