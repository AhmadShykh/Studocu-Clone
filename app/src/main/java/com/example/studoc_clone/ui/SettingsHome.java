package com.example.studoc_clone.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studoc_clone.MainActivity;
import com.example.studoc_clone.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SettingsHome extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the splash screen layout
        setContentView(R.layout.settings_main);

        // Set an onClick listener for the button
        findViewById(R.id.terms_of_use_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the URL to open
                String url = "https://www.studocu.com/app/app-terms?hideAppBanner=1";

                // Create an Intent to open the website
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                // Launch the browser with the Intent
                startActivity(intent);
            }
        });

        // Set an onClick listener for the button
        findViewById(R.id.privacy_policy_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the URL to open
                String url = "https://www.studocu.com/app/privacy-policy?hideAppBanner=1";

                // Create an Intent to open the website
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                // Launch the browser with the Intent
                startActivity(intent);
            }
        });

        // Set an onClick listener for the button
        findViewById(R.id.contact_us_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define email details
                String[] recipients = {"support@studocu.com"}; // Recipient email addresses
                String subject = "Studocu - Contact Us";
                String body = "\n\nTemporary Msg";

                // Create an Intent to compose the email
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // Only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);

                // Check if there's an app to handle the intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent); // Open the email app
                } else {
                    // No email app installed
                    // Show a message (optional)
                }
            }
        });


        findViewById(R.id.give_feedback_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the URL to open
                String url = "https://studocu.typeform.com/to/ErGqeM4H?app_version=5.4.0";

                // Create an Intent to open the website
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                // Launch the browser with the Intent
                startActivity(intent);
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        findViewById(R.id.cross_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout delAccnt = findViewById(R.id.delete_account_section);
        LinearLayout logOut = findViewById(R.id.logout_section);

        delAccnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // Reauthenticate the user
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    if (account != null) {
                        String idToken = account.getIdToken();
                        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

                        user.reauthenticate(credential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Delete the user account
                                user.delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        // Account deleted successfully
                                        Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                                        // Redirect to Login or Splash Screen
                                        Intent intent = new Intent(SettingsHome.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Failed to delete the account
                                        Toast.makeText(getApplicationContext(), "Failed to delete account: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                // Reauthentication failed
                                Toast.makeText(getApplicationContext(), "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "No Google account signed in.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Redirect to Login Activity
                Intent intent = new Intent(SettingsHome.this, MainActivity  .class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });


        if(firebaseAuth.getCurrentUser() == null){
            delAccnt.setVisibility(View.GONE);
            logOut.setVisibility(View.GONE);
        }




    }
}
