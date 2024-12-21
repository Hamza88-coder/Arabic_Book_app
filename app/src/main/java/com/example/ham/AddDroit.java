package com.example.ham;

import static com.example.ham.PdfListAdminActivity.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDroit extends AppCompatActivity {
    private com.example.ham.databinding.ActivityAddDroitBinding binding;
    private FirebaseUser currentUser; // Utilisateur actuellement connecté
    private EditText email;
    private EditText password;
    private String email1;
    private String password1;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.ham.databinding.ActivityAddDroitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        email = binding.emilEt;
        password = binding.passwordEt;
        login=binding.loginBtn;
        // Récupérer l'utilisateur actuellement connecté
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });


    }

    private void validateData() {
        email1 = email.getText().toString().trim();
        password1 = password.getText().toString().trim();

        if (TextUtils.isEmpty(email1)) {
            Toast.makeText(this, "أدخل البريد الإلكتروني", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password1)) {
            Toast.makeText(this, "أدخل كلمة المرور", Toast.LENGTH_SHORT).show();
        } else {
            addDroitMethode(email1, password1);
        }
    }


    private void addDroitMethode(String email, String password) {
        if (currentUser != null) {
            // Vérifier si l'utilisateur actuellement connecté est un administrateur (vous devez avoir une manière de vérifier cela dans votre base de données)
            // Si oui, procédez à la mise à jour du rôle
            String currentUserRole = "admin"; // Remplacez ceci par la logique pour obtenir le rôle de l'utilisateur actuel depuis votre base de données

            if (currentUserRole.equals("admin")) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // L'authentification a réussi, obtenir l'utilisateur à mettre à jour
                                FirebaseUser userToUpdate = mAuth.getCurrentUser();
                                if (userToUpdate != null) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    // Mettez à jour le champ 'role' dans Firestore
                                    // Par exemple, vous devez avoir l'ID de l'utilisateur (userId) et son nouveau rôle
                                    String userId = userToUpdate.getUid();

                                    // Mettez à jour le champ 'role' dans Firestore
                                    // Par exemple, vous devez avoir l'ID de l'utilisateur (userId) et son nouveau rôle
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("userType", "admin"); // Nouveau rôle

                                    db.collection("Users").document(userId)
                                            .update(updates)
                                            .addOnSuccessListener(aVoid -> {

                                                Toast.makeText(this, "تمّ تغيير الصّفة  بنجاح", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {

                                                Toast.makeText(this, "فشل غي التغيير", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onFailure: failed to update due to " + e.getMessage());
                                            });

                                    // Mettez à jour également la base de données en temps réel de Firebase
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("uid", userId);

                                    hashMap.put("userType", "admin");

                                    ref.child(userId).updateChildren(hashMap)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "بنجاح", Toast.LENGTH_SHORT).show();

                                            })
                                            .addOnFailureListener(e -> {

                                                Log.d(TAG, "onFailure: failed to update due to " + e.getMessage());
                                            });
                                }
                            } else {
                                // L'authentification a échoué

                                Toast.makeText(this, "خطأ في التعرّف على الشّخص " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // L'utilisateur actuel n'est pas un administrateur

                Toast.makeText(this, "Vous n'êtes pas autorisé à effectuer cette action.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
