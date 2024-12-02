package com.example.studoc_clone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.studoc_clone.R;
import com.example.studoc_clone.ui.ProfileHome;
import com.example.studoc_clone.ui.SettingsHome;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class home_fragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;

    private ImageView settingsIcon;
    private ImageView profileIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_fragment, container, false);

        settingsIcon = rootView.findViewById(R.id.settingsIcon);
        profileIcon = rootView.findViewById(R.id.profileImg);

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
        rootView.findViewById(R.id.signInButton).setOnClickListener(v -> signIn());

        return rootView;
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

    private void updateUserUI(@NonNull FirebaseUser user) {
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileIcon);
        } else {
            profileIcon.setImageResource(R.drawable.profile_logo);
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
                    } else {
                        Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
