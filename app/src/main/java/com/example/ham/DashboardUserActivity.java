package com.example.ham;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.databinding.ActivityDashboardUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {
    private ActivityDashboardUserBinding binding; // Fix 1: Use correct binding class


    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategoryUser adapterCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        loadCategories();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    adapterCategory.getFilter().filter( charSequence);
                }catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();

            }
        });
        binding.winner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //
                //startActivity(new Intent(DashboardUserActivity.this, Winner.class));

            }
        });
        binding.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(DashboardUserActivity.this, dic.class));
            }
        });
        binding.concurence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(DashboardUserActivity.this, Concurence.class));

            }
        });

        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardUserActivity.this, ProfilActivity.class));
            }
        });

    }

    private void loadCategories() {
        categoryArrayList=new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for(DataSnapshot ds :snapshot.getChildren()){
                    ModelCategory model=ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);
                }
                adapterCategory=new AdapterCategoryUser(DashboardUserActivity.this,categoryArrayList);
                binding.categoriesRv.setAdapter(adapterCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }else{
            String email =firebaseUser.getEmail();

        }
    }
}