package com.example.studoc_clone.fragments;

import static com.example.studoc_clone.utils.GlobalUtils.saveRecents;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class search_fragment extends Fragment {

    private PackAdapter packsAdapter;
    SearchDocumentAdapter adapter;

    List<Document> documentList = new ArrayList<>();
    ArrayList<Pack> packs = new ArrayList<>();

    RecyclerView packsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_fragment, container, false);


        RecyclerView recyclerView;

        recyclerView = rootView.findViewById(R.id.search_documents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SearchDocumentAdapter(getContext(), documentList, document -> {
            // Save to recent documents
            saveRecents(document.getDocId(),Consts.recentDocumentIds, Consts.RECENT_DOCUMENTS_KEY,15,requireContext());

            // Handle document click
            Intent intent = new Intent(requireContext(), DocumentViewerActivity.class);
            intent.putExtra("PDF_URL", document.getFileUrl());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        RecyclerView booksView;

        booksView = rootView.findViewById(R.id.courses_layout);
        booksView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        packsAdapter = new PackAdapter(getContext(),packs, pack -> {
            // Save to recent documents
            saveRecents(pack.getId(),Consts.recentViewedIds,Consts.RECENT_VIEWED_KEY,15,requireContext());
        });

        booksView.setAdapter(packsAdapter);

        loadDocuments();
        loadPacks();


        EditText searchEditText = rootView.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchDocuments(query);
                    booksView.setVisibility(View.GONE);
                } else {
                    booksView.setVisibility(View.VISIBLE);
                    loadDocuments(); // Reload all documents if search is cleared
                }
            }
        });

        return rootView;
    }

    private void searchDocuments(String query) {
        DatabaseReference databaseReference = FirebaseUtils.getDocumentsRef();

        // Fetch all documents and filter on the client side
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                documentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Document document = dataSnapshot.getValue(Document.class);
                    if (document != null && matchesSearchCriteria(document, query)) {
                        documentList.add(document);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to perform search.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper function to match search criteria
    private boolean matchesSearchCriteria(Document document, String query) {
        query = query.toLowerCase();
        return (document.getTitle() != null && document.getTitle().toLowerCase().contains(query)) ||
                (document.getDescription() != null && document.getDescription().toLowerCase().contains(query)) ||
                (document.getFolder() != null && document.getFolder().toLowerCase().contains(query)) ||
                (document.getDocType() != null && document.getDocType().toLowerCase().contains(query));
    }

    private void loadDocuments() {
        DatabaseReference databaseReference = FirebaseUtils.getDocumentsRef();

        // Limit the query to the first 15 documents
        databaseReference.limitToFirst(15).addValueEventListener(new ValueEventListener() {
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

    private void loadPacks() {
        DatabaseReference databaseReference = FirebaseUtils.getPacksRef();

        // Limit the query to the first 15 documents
        databaseReference.limitToFirst(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packs.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pack pack = dataSnapshot.getValue(Pack.class);
                    if (pack != null) {
                        packs.add(pack);
                    }
                }
                packsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load documents.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}