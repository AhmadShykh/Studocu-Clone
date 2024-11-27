package com.example.studoc_clone.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studoc_clone.R;

public class SettingsHome extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the splash screen layout
        setContentView(R.layout.settings_home);

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


        findViewById(R.id.cross_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
