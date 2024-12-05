package com.example.studoc_clone.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studoc_clone.R;
import com.example.studoc_clone.models.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SearchDocumentAdapter extends RecyclerView.Adapter<SearchDocumentAdapter.DocumentViewHolder> {

    private Context context;
    private List<Document> documentList;
    private OnDocumentClickListener listener;

    public interface OnDocumentClickListener {
        void onDocumentClick(Document document);
    }

    public SearchDocumentAdapter(Context context, List<Document> documentList, OnDocumentClickListener listener) {
        this.context = context;
        this.documentList = documentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documentList.get(position);

        holder.title.setText(document.getTitle());
        holder.rating.setText(document.getRating());
        holder.folder.setText(document.getFolder());
        holder.years.setText(document.getYears());
        holder.universityName.setText(document.getUniversityName());
        holder.docType.setText(document.getDocType());

        // Load the first page of the document as an image
        loadPdfFirstPage(document.getFileUrl(), holder.image);

        holder.itemView.setOnClickListener(v -> listener.onDocumentClick(document));
    }


    private void loadPdfFirstPage(String pdfUrl, ImageView imageView) {
        new Thread(() -> {
            try {
                // Download the PDF from the URL
                URL url = new URL(pdfUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                // Get the InputStream from the connection
                InputStream inputStream = connection.getInputStream();

                // Save the InputStream as a temporary file
                File tempFile = File.createTempFile("temp_pdf", ".pdf", context.getCacheDir());
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();

                // Now open the downloaded PDF file using PdfRenderer
                ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);

                // Render the first page
                PdfRenderer.Page page = pdfRenderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();

                // Set the first page as an image
                ((Activity) context).runOnUiThread(() -> imageView.setImageBitmap(bitmap));

                // Clean up
                pdfRenderer.close();
                fileDescriptor.close();
                tempFile.delete(); // Delete the temporary file after use
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



    @Override
    public int getItemCount() {
        return documentList.size();
    }

    // Function to update the dataset and refresh the RecyclerView
    public void updateData(List<Document> newDocumentList) {
        this.documentList.clear();
        this.documentList.addAll(newDocumentList);
        notifyDataSetChanged();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView title, rating,folder,universityName,years,docType;
        ImageView image;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.document_name);
            image = itemView.findViewById(R.id.document_image);
            rating = itemView.findViewById(R.id.rating);
            folder = itemView.findViewById(R.id.folder_name);
            universityName = itemView.findViewById(R.id.university_name);
            years = itemView.findViewById(R.id.years);
            docType = itemView.findViewById(R.id.document_type);
        }
    }
}
