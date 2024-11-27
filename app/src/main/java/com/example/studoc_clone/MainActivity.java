package com.example.studoc_clone;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;  // Request code for Google sign-in

    ImageView settingsIcon ;
    ImageView profileIcon ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for MainActivity
        setContentView(R.layout.activity_home);

        settingsIcon = findViewById(R.id.settingsIcon);
        profileIcon = findViewById(R.id.profileImg);

        // Show loading screen while main content is being loaded
        new Handler().postDelayed(() -> {
            // Simulate content loading
            View loadingScreen = findViewById(R.id.loading_screen);
            View mainContent = findViewById(R.id.main_activity);

            // Hide loading screen and show main content
            loadingScreen.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
        }, 2000); // Adjust loading delay as needed (e.g., 2 seconds)

        findViewById(R.id.settingsIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsHome.class);
                startActivity(intent);

            }
        });
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, ProfileHome.class);
                    // Pass user details
                    intent.putExtra("userName", user.getDisplayName());
                    intent.putExtra("userEmail", user.getEmail());
                    intent.putExtra("userPhotoUrl", (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id)) // Your web client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up the sign-in button click listener
        findViewById(R.id.continueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    // Sign-in method
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Google Sign-In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult();
                            firebaseAuthWithGoogle(account);


                        } else {
                            // Google Sign-In failed, update UI appropriately
                            Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    // Firebase authentication with Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        recreate();
                        
                        // Navigate to another activity or home screen
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            profileIcon.setVisibility(View.VISIBLE);
            settingsIcon.setVisibility(View.GONE);
            updateUserUI(user);
            //loadProfileImage(user.getPhotoUrl().toString(),profileIcon);
        }
        else{
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
            // Set a placeholder image if no profile picture is available
            profileIcon.setImageResource(R.drawable.profile_logo);
        }
    }
}