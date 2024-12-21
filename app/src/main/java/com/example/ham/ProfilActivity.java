package com.example.ham;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ham.databinding.ActivityProfilBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfilActivity extends AppCompatActivity {
    private ActivityProfilBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelPdf> pdfArrayList;
    private adapterPdfFavorite adapterPdfFavorite;
    private static final String TAG="PROFILE_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        loadYserInfo();
        loadFavoriteBooks();


        binding.profileeditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilActivity.this, ProfileEditActivity.class));
            }
        });
        binding.BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadYserInfo() {
        Log.d(TAG,"loadUserInfo" +firebaseAuth.getUid());
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email =""+snapshot.child("email").getValue();
                String name=""+snapshot.child("name").getValue();
                String Profilimage=""+snapshot.child("profileImage").getValue();
                String timestamp=""+snapshot.child("timestamp").getValue();
                String uid=""+snapshot.child("userType").getValue();

                String formatDate =MyApplication.formatTimestamp(Long.parseLong(timestamp));

                binding.emailTv.setText(email);
                binding.namev.setText(name);
                binding.membreDateTv.setText(formatDate);
                binding.AccountTypeTv.setText(uid);

              //Glide.with(ProfilActivity.this).load(Profilimage).placeholder(R.drawable.ic_person_gray).into(binding.profilrIv);


                    Glide.with(ProfilActivity.this)
                            .load(Profilimage)
                            .placeholder(R.drawable.ic_person_gray)
                            .into(binding.profilrIv);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void loadFavoriteBooks() {

        pdfArrayList=new ArrayList<>();

        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfArrayList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    String bookId=""+ds.child("bookId").getValue();
                    ModelPdf modelPdf=new ModelPdf();
                    modelPdf.setId(bookId);
                    pdfArrayList.add(modelPdf);


                }
                 if(pdfArrayList.size() !=0){
                     binding.favouriteBookCountTv.setText(""+pdfArrayList.size());

                 }

                adapterPdfFavorite=new adapterPdfFavorite(ProfilActivity.this,pdfArrayList);
                binding.BookRv.setAdapter(adapterPdfFavorite);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}