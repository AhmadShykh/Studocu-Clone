package com.example.studoc_clone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.studoc_clone.R;
import com.example.studoc_clone.ui.activity_create_group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class chats_fragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    LinearLayout chatsLayout;
    LinearLayout btnMessages;
    LinearLayout btnExplore;

    FirebaseUser user;

    private boolean onMessages = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chats_fragment, container, false);

        // Reference the FragmentManager
        FragmentManager fragmentManager = getChildFragmentManager();

        user = FirebaseAuth.getInstance().getCurrentUser();

        chatsLayout = rootView.findViewById(R.id.chatsLayout);
        btnMessages = rootView.findViewById(R.id.btnMessages);
        btnExplore = rootView.findViewById(R.id.btnExplore);


        // Load the first fragment by default
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, new messages_fragment())
                .commit();


        // Button to load the first fragment
        btnMessages.setOnClickListener(v -> {
            toggleChats(true);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, new messages_fragment())
                    .commit();
        });

        // Button to load the second fragment
        btnExplore.setOnClickListener(v -> {
            toggleChats(false);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, new ExplorFragment())
                    .commit();
        });

        rootView.findViewById(R.id.addGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), activity_create_group.class);

                startActivity(intent);
            }
        });

        udpateUIForUser();


        toggleChats(true);


        return rootView;
    }

    private void udpateUIForUser() {
        if(user != null){

        }
        else{

        }
    }


    private void toggleChats(boolean val){

        onMessages = val;

        if(val)
        {
            btnMessages.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.lighter_blue));
            chatsLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lighter_blue));
            btnExplore.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));

        }
        else{
            btnMessages.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
            btnExplore.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.lightest_purple));
            chatsLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightest_purple));
        }
    }

}
