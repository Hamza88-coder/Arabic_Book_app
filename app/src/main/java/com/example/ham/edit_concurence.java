package com.example.ham;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.databinding.ActivityEditConcurenceBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class edit_concurence extends AppCompatActivity {
    private ActivityEditConcurenceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEditConcurenceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }








    private String email1="",email2="";
    private String bookId1="",bookId2="";
    private void validateData() {
        email1=binding.titleEt.getText().toString().trim();
        email2=binding.descriptionEt.getText().toString().trim();
        bookId1=binding.bookTilID.getText().toString().trim();
       bookId2=binding.BookId2.getText().toString().trim();


        if(TextUtils.isEmpty(email1)){
            Toast.makeText(this, " أدخل البريد الإلكتروني الأوّل", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email2)){
            Toast.makeText(this, " أدخل البريد الإلكتروني الثاني", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(bookId1)) {
            Toast.makeText(this, " أدخل رقم القصّة الأولى", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(bookId2)) {
            Toast.makeText(this, " أدخل رقم القصّة الثانية", Toast.LENGTH_SHORT).show();

        } else{
            saveConcurrence();
            updateWinnerImage1();
            updateWinnerImage2();
            initialiserCompteur();
            clearVotingEmails();
        }
    }

    private void initialiserCompteur() {
        DatabaseReference winnerNombre1Ref = FirebaseDatabase.getInstance().getReference("Winner/nombre/1");
        DatabaseReference winnerNombre2Ref = FirebaseDatabase.getInstance().getReference("Winner/nombre/2");

        // Set value à 0 pour le nœud "Winner/nombre/1"
        winnerNombre1Ref.setValue(0);

        // Set value à 0 pour le nœud "Winner/nombre/2"
        winnerNombre2Ref.setValue(0);


    }


    private void saveConcurrence() {
        // Obtenez une référence à votre base de données Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Winner");

        // Obtenez une référence au nœud spécifique sous "concurent1" avec l'identifiant de livre (bookId1)
        DatabaseReference concurrence1Ref = ref.child("concurent1");

        concurrence1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Le nœud concurent1 existe déjà, mettez à jour les valeurs
                    concurrence1Ref.child("email").setValue(email1);
                    concurrence1Ref.child("bookId").setValue(bookId1);
                } else {
                    // Le nœud concurent1 n'existe pas, vous pouvez le créer ici si nécessaire
                    // concurrence1Ref.child("email").setValue(email1);
                    // concurrence1Ref.child("bookId").setValue(bookId1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Gérez cette situation selon vos besoins
            }
        });

        // Répétez le processus pour concurent2
        DatabaseReference concurrence2Ref = ref.child("concurent2");

        concurrence2Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Le nœud concurent2 existe déjà, mettez à jour les valeurs
                    concurrence2Ref.child("email").setValue(email2);
                    concurrence2Ref.child("bookId").setValue(bookId2);
                } else {
                    // Le nœud concurent2 n'existe pas, vous pouvez le créer ici si nécessaire
                    // concurrence2Ref.child("email").setValue(email2);
                    // concurrence2Ref.child("bookId").setValue(bookId2);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Gérez cette situation selon vos besoins
            }
        });

        Toast.makeText(this, "لقد تمّ تغيير معلومات المسابقة بنجاح", Toast.LENGTH_SHORT).show();
    }
    private void clearVotingEmails() {
        DatabaseReference votantRef = FirebaseDatabase.getInstance().getReference("Winner/Votant");

        // Utilisez setValue(null) pour vider le contenu du nœud "Votant"
        votantRef.setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La mise à jour a réussi

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void updateWinnerImage1() {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("Books");
        DatabaseReference winnerImageRef = FirebaseDatabase.getInstance().getReference("Winner/Image/1");

        // Recherchez le livre correspondant à bookId1
        booksRef.child(bookId1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le livre correspondant à bookId1 a été trouvé
                    // Accédez aux informations du livre
                    String profileImageLink = dataSnapshot.child("imageUrl").getValue(String.class);

                    // Mettez à jour la valeur dans le chemin "Winner/Image/1" avec le nouveau lien
                    winnerImageRef.setValue(profileImageLink);

                    // Vous pouvez ajouter ici un code supplémentaire pour traiter le reste de votre logique
                    // par exemple, afficher un message de succès, etc.
                    Toast.makeText(edit_concurence.this, "لقد تمّ تغيير صورة المشارك الأوّل", Toast.LENGTH_SHORT).show();
                } else {
                    // Aucun livre correspondant à bookId1 n'a été trouvé
                    // Gérez cette situation selon vos besoins
                    Toast.makeText(edit_concurence.this, "ليس هناك كتاب بالرقم المعرّف المحدد", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Une erreur s'est produite lors de la récupération des données
                // Affichez le message d'erreur pour le débogage
                Toast.makeText(edit_concurence.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWinnerImage2() {
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("Books");
        DatabaseReference winnerImageRef = FirebaseDatabase.getInstance().getReference("Winner/Image/2");

        // Recherchez le livre correspondant à bookId1
        booksRef.child(bookId1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le livre correspondant à bookId1 a été trouvé
                    // Accédez aux informations du livre
                    String profileImageLink = dataSnapshot.child("imageUrl").getValue(String.class);

                    // Mettez à jour la valeur dans le chemin "Winner/Image/1" avec le nouveau lien
                    winnerImageRef.setValue(profileImageLink);

                    // Vous pouvez ajouter ici un code supplémentaire pour traiter le reste de votre logique
                    // par exemple, afficher un message de succès, etc.
                    Toast.makeText(edit_concurence.this, "لقد تمّ تغيير صورة المشارك الأوّل", Toast.LENGTH_SHORT).show();
                } else {
                    // Aucun livre correspondant à bookId1 n'a été trouvé
                    // Gérez cette situation selon vos besoins
                    Toast.makeText(edit_concurence.this, "ليس هناك كتاب بالرقم المعرّف المحدد", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Une erreur s'est produite lors de la récupération des données
                // Affichez le message d'erreur pour le débogage
                Toast.makeText(edit_concurence.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}