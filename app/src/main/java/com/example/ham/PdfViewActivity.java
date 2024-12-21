package com.example.ham;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.databinding.ActivityPdfViewBinding;


import com.github.pdfviewer.listener.OnErrorListener;
import com.github.pdfviewer.listener.OnPageChangeListener;
import com.github.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PdfViewActivity extends AppCompatActivity {
    private ActivityPdfViewBinding binding;
    private String bookId;
    private static final String TAG="PDF_VIEW_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding=ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        bookId=intent.getStringExtra("bookId");
        Log.d(TAG,"onCreate: BookID"+bookId);
        loadBookDetails();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadBookDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pdfUrl=""+snapshot.child("pdfUrl").getValue();
                loadBookFromUrl(pdfUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBookFromUrl(String pdfUrl) {
        StorageReference reference= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes( 1000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                binding.pdfView.fromBytes(bytes).swipeHorizontal(false).onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        int currentPage=(page+1);
                        binding.toolbarSubtitleTv.setText(currentPage+"/"+pageCount);
                    }
                }).onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(PdfViewActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        Toast.makeText(PdfViewActivity.this, "خطأ في"+page+""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).load();
                binding.progressBar.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}