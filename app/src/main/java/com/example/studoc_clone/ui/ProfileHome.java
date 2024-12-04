package com.example.studoc_clone.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.studoc_clone.R;

public class ProfileHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_home);

        // Get views
        TextView nameTextView = findViewById(R.id.username_text);
        //TextView emailTextView = findViewById(R.id.emailTextView);
        //TextView universityNameTextView = findViewById(R.id.college_name_text);
        ImageView profileImageView = findViewById(R.id.profile_logo);

        // Retrieve data from Intent
        String userName = getIntent().getStringExtra("userName");
        //String userEmail = getIntent().getStringExtra("userEmail");
        String userPhotoUrl = getIntent().getStringExtra("userPhotoUrl");

        // Set name and email
        nameTextView.setText(userName != null ? userName : "No name available");
        //emailTextView.setText(userEmail != null ? userEmail : "No email available");

        // Extract university name from the email
//        if (userEmail != null && userEmail.contains("@")) {
//            String universityName = userEmail.substring(userEmail.indexOf("@") + 1, userEmail.lastIndexOf("."));
//            universityNameTextView.setText(universityName);
//        } else {
//            universityNameTextView.setText("No university information available");
//        }

        // Load the profile image
        if (userPhotoUrl != null) {
            Uri photoUri = Uri.parse(userPhotoUrl);
            Glide.with(this)
                    .load(photoUri)
                    .placeholder(R.drawable.profile_logo) // Placeholder image while loading
                    .error(R.drawable.profile_logo)      // Default image on error
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.profile_logo); // Use a default image
        }

        findViewById(R.id.settingsIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileHome.this, SettingsHome.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.arrow_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
