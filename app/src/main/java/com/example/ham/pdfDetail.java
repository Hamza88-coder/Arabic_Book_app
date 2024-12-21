package com.example.ham;

import static com.example.ham.MyApplication.TAG_DOWNLOD;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ham.databinding.ActivityPdfDetail2Binding;
import com.example.ham.databinding.DialogueCommentAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class pdfDetail extends AppCompatActivity {
    private ActivityPdfDetail2Binding binding;
    private String bookId,imageUrl,pdfUrl, category,bookTitle;;
    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;
    private adapterPdfFavorite adapterPdfFavorite;
     boolean isMyfavorite =false;
     private ProgressDialog progressDialog;
     private FirebaseAuth firebaseAuth;
     private ArrayList<ModelComment> commentArrayList;
     private AdapterComment adapterComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=com.example.ham.databinding.ActivityPdfDetail2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;

        Intent intent=getIntent();
        bookId=intent.getStringExtra("bookId");
        imageUrl=intent.getStringExtra("imageUrl");
        pdfUrl=intent.getStringExtra("pdfUrl");
        loadBookDetails();
        loadComment();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("رجاءا إنتظر")
        ;
        progressDialog.setCanceledOnTouchOutside(false);



        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.downoaldBtn.setVisibility(View.GONE);

        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            checkidFavorite();
        }
        binding.readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(pdfDetail.this,PdfViewActivity.class);
                intent1.putExtra("bookId",bookId);
                startActivity(intent1);
            }
        });
        binding.downoaldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_DOWNLOD,"onClick: Checking permission");
                if(ContextCompat.checkSelfPermission(pdfDetail.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                        PackageManager.PERMISSION_GRANTED){

                    Log.d(TAG_DOWNLOD,"permission granted wz can downoledd ");
                    MyApplication.downoald(pdfDetail.this,""+bookId,""+bookTitle,""+pdfUrl);
                }else{
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

            }
        });

        binding.favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser()==null){
                    Toast.makeText(pdfDetail.this, "لم تقم بتسجيل الدّخول", Toast.LENGTH_SHORT).show();
                }else{
                    if(isMyfavorite){
                        MyApplication.removeFromFav(pdfDetail.this,bookId);

                    }else{
                        MyApplication.addTOFavorite(pdfDetail.this,bookId);

                    }
                }
            }
        });
        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser()==null){
                    Toast.makeText(pdfDetail.this, "لم تقم بتسجيل الدّخول", Toast.LENGTH_SHORT).show();

                }else{
                      addCommentDialoque();
                }
            }
        });
    }

    private void loadComment() {
        commentArrayList=new ArrayList<>();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Books");

        ref.child(bookId).child("Comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelComment modelComment=ds.getValue(ModelComment.class);

                    commentArrayList.add(modelComment);
                }
                adapterComment=new AdapterComment(pdfDetail.this,commentArrayList);
                binding.CommentsRv.setAdapter(adapterComment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String comment="";

    private void addCommentDialoque() {

        DialogueCommentAddBinding commentAddBinding=DialogueCommentAddBinding.inflate(LayoutInflater.from(this));
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.CustomDialog);
        builder.setView(commentAddBinding.getRoot());
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment=commentAddBinding.commentEt.getText().toString().trim();
                if(TextUtils.isEmpty(comment)){
                    Toast.makeText(pdfDetail.this, "أدخل التعليق", Toast.LENGTH_SHORT).show();

                }else{
                    alertDialog.dismiss();
                    addComment();

                }
            }
        });

    }

    private void addComment() {

        progressDialog.setTitle("إضافة التعليق");
        progressDialog.show();
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,Object> hashmap=new HashMap<>();
        hashmap.put("id",""+timestamp);
        hashmap.put("bookId",""+bookId);
        hashmap.put("timestamp",""+timestamp);
        hashmap.put("comment",""+comment);
        hashmap.put("uid",""+firebaseAuth.getUid());

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(timestamp).setValue(hashmap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();



            }
        });



    }


    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG_DOWNLOD, "Permission granted");
                    MyApplication.downoald(this, "" + bookId, "" + bookTitle, "" + pdfUrl);
                }else {
                    Log.d(TAG_DOWNLOD,"Permission Denied....:");
                    Toast.makeText(this, "permission was denied", Toast.LENGTH_SHORT).show();
                }
            }
    );


    private void loadBookDetails() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                bookTitle=""+snapshot.child("title").getValue();
                String description=""+snapshot.child("description").getValue();
                String categoryId=""+snapshot.child("categoryId").getValue();
                pdfUrl=""+snapshot.child("pdfUrl").getValue();
                String timestamp=""+snapshot.child("timestamp").getValue();

                binding.downoaldBtn.setVisibility(View.VISIBLE);
                String date=MyApplication.formatTimestamp(Long.parseLong(timestamp));


               binding.Titletv.setText(bookTitle);
               binding.descriptionTv.setText(description);
               binding.dateTv.setText(date);
               binding.categoryTv.setText(bookTitle);

               loadImageFromUrl(imageUrl);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadImageFromUrl(String pdfUrl) {
        try {
            Picasso.get().load(pdfUrl).into(binding.pdfView);
        } catch (Exception e) {
            // Gérez les erreurs ici
            e.printStackTrace();
        }
    }
    private void checkidFavorite(){

        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Toast.makeText(context, "لم تقم بتسجيل الدّخول", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
            ref.child((firebaseAuth.getUid())).child("Favorites").child(bookId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    isMyfavorite=snapshot.exists();
                    if(isMyfavorite){
                        binding.favouriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_white,0,0);

                    }else{
                        binding.favouriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_border_white,0,0);



                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }



}