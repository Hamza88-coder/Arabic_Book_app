package com.example.ham;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EmailChecker {

    public EmailChecker() {
    }

    public static void checkEmailExistence(String bookID, String email, ExistCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Access").child(bookID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le nœud existe, vérifiez s'il contient des enfants (emails).
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String childEmail = childSnapshot.getValue(String.class);
                            if (childEmail.equals(email)) {
                                // L'email existe sous cet ID de livre.
                                callback.onExist(true);
                                return;
                            }
                        }
                    }
                }
                // L'email n'existe pas sous cet ID de livre.
                callback.onExist(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // En cas d'erreur, considérez que l'email n'existe pas.
                callback.onExist(false);
            }
        });
    }
}
