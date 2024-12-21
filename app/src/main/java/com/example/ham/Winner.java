package com.example.ham;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ham.databinding.ActivityWinnerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Winner extends AppCompatActivity {

    private ActivityWinnerBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG="EDIT_PROGIL";

    private String name="";
    private ProgressDialog progressDialog;
    private Uri imageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("المرجو الآنتظار");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth=FirebaseAuth.getInstance();
        loadYserInfo();
        binding.profileeditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();

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

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Winner");
        reference.child("KcToNlubQCWSHo5BzgaNtFEmvlF3").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name=""+snapshot.child("name").getValue();
                String Profilimage=""+snapshot.child("profileImage").getValue();





                binding.namev.setText(name);

                Glide.with(Winner.this).load(Profilimage).placeholder(R.drawable.ic_person_gray).into(binding.profilrIv);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void checkUser() {
        progressDialog.setMessage("التأكّد من المستخدم....");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressDialog.dismiss();
                String userType=""+snapshot.child("userType").getValue();
                if (userType.equals("user")){
                    Toast.makeText(Winner.this, "هذه الخاصيّة غير متاحة لك", Toast.LENGTH_SHORT).show();


                }else if(userType.equals("admin")){
                    startActivity(new Intent(Winner.this,WinnerEdit.class));

                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }


}