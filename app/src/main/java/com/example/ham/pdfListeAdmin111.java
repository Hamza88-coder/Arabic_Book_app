package com.example.ham;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.databinding.ActivityPdfListAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pdfListeAdmin111 extends AppCompatActivity {

    private ActivityPdfListAdminBinding binding;
    private ArrayList<ModelPdf> pdfArrayList;
    private AdapterPdfAdmin adapterPdfAdmin;
    public static final String TAG="PDF_LIST_TAG";
    private String categoryId,categoryTitle,bookID,bookGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        categoryId=intent.getStringExtra("categoryId");
        bookID=intent.getStringExtra("BookId");
        bookGroup=intent.getStringExtra("group");
        categoryTitle=intent.getStringExtra("categoryTitle");
        binding.subTitleTv.setText(categoryTitle);


        loadPdfList();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    adapterPdfAdmin.getFilter().filter(charSequence);
                }catch (Exception e){
                    Log.d(TAG,"onTextChanged: "+e.getMessage());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        pdfArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

        ref.orderByChild("group").equalTo(bookGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("Début de la méthode onDataChange");
                pdfArrayList.clear();



                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d(TAG, "Entré dans la boucle for");
                    ModelPdf model = ds.getValue(ModelPdf.class);

                     // Comparaison de chaînes avec equals()
                        pdfArrayList.add(model);
                        Log.d(TAG, "onDataChanged: " + model.getId() + " " + model.getTitle());

                }


                // Il est préférable de créer et définir l'adaptateur en dehors de la boucle for
                adapterPdfAdmin = new AdapterPdfAdmin(pdfListeAdmin111.this, pdfArrayList);
                binding.bookRv.setAdapter(adapterPdfAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gestion des erreurs d'accès à la base de données
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }



    }


