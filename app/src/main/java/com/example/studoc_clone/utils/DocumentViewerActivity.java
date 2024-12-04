package com.example.studoc_clone.utils;

import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studoc_clone.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DocumentViewerActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File tempFile;
    private ProgressBar progressBar;
    private TextView pageNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);
        pageNumberText = findViewById(R.id.pageNumberText);

        // Show the loading screen initially
        progressBar.setVisibility(ProgressBar.VISIBLE);
        pageNumberText.setVisibility(TextView.GONE);

        // Get the PDF URL from the Intent
        String pdfUrl = getIntent().getStringExtra("PDF_URL");
        if (pdfUrl != null) {
            downloadPdf(pdfUrl);
        } else {
            Toast.makeText(this, "Failed to load PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadPdf(String pdfUrl) {
        new Thread(() -> {
            try {
                // Create a unique file name based on the PDF URL
                String fileName = generateFileName(pdfUrl);

                // Check if the PDF already exists in internal storage
                File pdfDirectory = new File(getFilesDir(), "pdfs");
                if (!pdfDirectory.exists()) {
                    pdfDirectory.mkdirs(); // Create the directory if it doesn't exist
                }

                File pdfFile = new File(pdfDirectory, fileName);

                if (!pdfFile.exists()) {
                    URL url = new URL(pdfUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    // Get the input stream from the connection
                    InputStream inputStream = connection.getInputStream();

                    // Write the input stream to the internal storage file
                    FileOutputStream outputStream = new FileOutputStream(pdfFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();

                }
                runOnUiThread(() -> {
                    pdfView.fromFile(pdfFile)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .onPageChange((page, pageCount) -> {
                                // Update page number
                                pageNumberText.setText((page + 1) + "/" + pageCount);
                            })
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    // Hide loading screen after the PDF is loaded
                                    progressBar.setVisibility(ProgressBar.GONE);
                                    pageNumberText.setVisibility(TextView.VISIBLE);
                                    Log.d("PDFView", "PDF loaded with " + nbPages + " pages.");
                                }
                            })
                            .load();
                });

            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to download PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String generateFileName(String pdfUrl) {
        // Generate a unique file name based on the URL
        return String.valueOf(pdfUrl.hashCode()) + ".pdf"; // Using hash code of URL as file name
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Delete the temporary file if it exists
//        if (tempFile != null && tempFile.exists()) {
//            boolean deleted = tempFile.delete();
//            if (!deleted) {
//                Toast.makeText(this, "Failed to delete temporary file.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
