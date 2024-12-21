package com.example.ham;

import static com.example.ham.PdfListAdminActivity.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCustomer extends AppCompatActivity {
    private EditText userEmailEditText;
    private EditText bookIdEditText;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button affectter;
    private String ID,email;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        userEmailEditText = findViewById(R.id.titleEt);
        bookIdEditText = findViewById(R.id.descriptionEt);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        affectter=findViewById(R.id.affecter);

        affectter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtenez les valeurs actuelles des EditText au moment du clic sur le bouton.
                 email = userEmailEditText.getText().toString().trim();
                ID = bookIdEditText.getText().toString().trim();

                // Appelez la fonction pour ajouter l'e-mail à la base de données.
                validateData();

                userEmailEditText.setText("");
                bookIdEditText.setText("");
            }
        });

    }
    private void validateData() {


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "أدخل البريد الإلكتروني", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(ID)){
            Toast.makeText(this, "أدخل الرقم التّعريفي الخاص بالكتاب", Toast.LENGTH_SHORT).show();
        }else {
            onAddBookToUserClick(ID, email);
        }
    }

    public void onAddBookToUserClick(String bookID, String email) {
        Log.d(TAG, "Adding email to Access...");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Access");

        // Pour ajouter l'e-mail sous un ID de livre spécifique.
        DatabaseReference bookRef = ref.child(bookID);

        // Créez un nouvel enfant sous l'ID de livre et ajoutez l'e-mail.
        DatabaseReference emailRef = bookRef.push();
        emailRef.setValue(email)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Successfully added email to Access...");
                    Toast.makeText(AddCustomer.this, "تمّت الإضافة بنجاح", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: Failed to add email to Access due to " + e.getMessage());
                    Toast.makeText(AddCustomer.this, "فشل في الإضافة" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
 }

