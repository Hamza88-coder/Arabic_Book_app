package com.example.ham;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ham.databinding.ActivityConcurenceBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Concurence extends AppCompatActivity {
    private ActivityConcurenceBinding binding;
    private String pdfUrl;
    private FirebaseAuth firebaseAuth;
    boolean isMyfavorite =false;
   boolean Btn1=true;
   boolean Btn2=true;
    Dialog progressDialog;
   boolean isVotant=false;
   public String email1="",email2="",bookId1="",bookId2="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityConcurenceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        loadYserInfo2();


        loadConcurrenceDetails();
        loadYserInfo1();
        retrieveConcurrenceData();



        binding.readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(Concurence.this,PdfViewActivity.class);
                intent1.putExtra("bookId",bookId1);
                startActivity(intent1);
            }
        });

        binding.BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.profileeditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkUser();

            }
        });

        binding.readBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(Concurence.this,PdfViewActivity.class);
                intent1.putExtra("bookId",bookId2);
                startActivity(intent1);
            }
        });
        binding.favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Btn2=false;

                if(firebaseAuth.getCurrentUser()==null || Btn1==false){
                    if(firebaseAuth.getCurrentUser()==null){
                        Toast.makeText(Concurence.this, "رجاء قم بتسجيل الدّخول", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Concurence.this, "لقد قمت بالتّصويت من قبل", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String bookId="1";
                    addTOLike(Concurence.this, bookId);

                    view.setBackgroundResource(R.drawable.blu_fav);
                }
            }
        });

        binding.favouriteBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Btn1=false;

                if(firebaseAuth.getCurrentUser()==null || Btn2==false){
                    if(firebaseAuth.getCurrentUser()==null){
                        Toast.makeText(Concurence.this, "رجاء قم بتسجيل الدّخول", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Concurence.this, "لقد قمت بالتّصويت من قبل", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String bookId="2";
                    addTOLike(Concurence.this, bookId);
                    view.setBackgroundResource(R.drawable.baseline_favorite_24);

                }
            }
        });


    }


    private void loadConcurrenceDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Winner");

        // Obtenez une référence au nœud spécifique sous "nombre" avec l'identifiant de livre (bookId) pour nbr1
        DatabaseReference nbr1Ref = ref.child("nombre").child("1");

        nbr1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtenez la valeur du champ "nombre" pour nbr1
                    Long valeurNombre1 = (Long) snapshot.getValue();

                    // Mettez à jour l'interface utilisateur avec la valeur de "nombre" pour nbr1
                    binding.nbr1.setText(String.valueOf(valeurNombre1));
                } else {
                    // Le nœud ou le champ spécifié n'existe pas pour nbr1
                    // Gérez cette situation selon vos besoins
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Une erreur s'est produite lors de la récupération des données pour nbr1
                // Gérez cette situation selon vos besoins
            }
        });

        // Obtenez une référence au nœud spécifique sous "nombre" avec l'identifiant de livre (bookId) pour nbr2
        DatabaseReference nbr2Ref = ref.child("nombre").child("2");

        nbr2Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Obtenez la valeur du champ "nombre" pour nbr2
                    Long valeurNombre2 = (Long) snapshot.getValue();

                    // Mettez à jour l'interface utilisateur avec la valeur de "nombre" pour nbr2
                    binding.nbr2.setText(String.valueOf(valeurNombre2));
                } else {
                    // Le nœud ou le champ spécifié n'existe pas pour nbr2
                    // Gérez cette situation selon vos besoins
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Une erreur s'est produite lors de la récupération des données pour nbr2
                // Gérez cette situation selon vos besoins
            }
        });
    }
    public void addTOLike(Context context, String bookId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // Vérifier d'abord si l'utilisateur a déjà voté
        checkIfUserVoted(context, new OnCheckVoteListener() {
            @Override
            public void onChecked(boolean hasVoted) {
                if (!hasVoted) {
                    // L'utilisateur n'a pas encore voté, procédez à l'incrémentation
                    if (firebaseAuth.getCurrentUser() == null) {
                        Toast.makeText(context, "You are not logged in", Toast.LENGTH_SHORT).show();
                    } else {
                        // Obtenez une référence vers le nœud "Winner" dans votre base de données Firebase
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Winner");

                        // Obtenez une référence au nœud spécifique sous "Likes" avec l'identifiant de livre (bookId)
                        DatabaseReference bookRef = ref.child("nombre").child(bookId);

                        // Utilisez un ValueEventListener pour obtenir la valeur actuelle et l'incrémenter de 1
                        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Obtenir la valeur actuelle
                                    Long valeurActuelle = (Long) dataSnapshot.getValue();

                                    // Incrémenter la valeur de 1
                                    Long nouvelleValeur = valeurActuelle + 1;

                                    // Mettre à jour la valeur dans la base de données
                                    bookRef.setValue(nouvelleValeur);
                                    loadConcurrenceDetails();
                                    DatabaseReference winnerRef = FirebaseDatabase.getInstance().getReference("Winner");

                                    // Obtenez une référence au nœud spécifique sous "Votans"
                                    DatabaseReference votansRef = winnerRef.child("Votans");

                                    // Ajoutez l'utilisateur actuel à l'ensemble des gens ayant voté
                                    String userId = firebaseAuth.getCurrentUser().getUid();
                                    votansRef.child(userId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // La mise à jour a réussi
                                            // Vous pouvez ajouter du code supplémentaire ici si nécessaire
                                        }
                                    });
                                } else {
                                    // Le nœud avec l'identifiant de livre spécifié n'existe pas
                                    // Gérez cette situation selon vos besoins
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Une erreur s'est produite lors de la récupération des données
                                // Gérez cette situation selon vos besoins
                            }
                        });
                    }
                } else {
                    // L'utilisateur a déjà voté, affichez un message ou prenez d'autres mesures
                    Toast.makeText(context, "لقد قمت بالتّصويت من قبل", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkIfUserVoted(Context context, OnCheckVoteListener listener) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            // L'utilisateur n'est pas connecté
            Toast.makeText(context, "لم تقم بتسجيل الدّخول", Toast.LENGTH_SHORT).show();
            // Indiquez que l'utilisateur n'a pas voté (false)
            listener.onChecked(false);
        } else {
            // Obtenez l'ID de l'utilisateur actuel
            String userId = firebaseAuth.getCurrentUser().getUid();

            // Obtenez une référence vers le nœud "Winner" dans votre base de données Firebase
            DatabaseReference winnerRef = FirebaseDatabase.getInstance().getReference("Winner");

            // Obtenez une référence au nœud spécifique sous "Votans"
            DatabaseReference votansRef = winnerRef.child("Votans");

            // Utilisez un ValueEventListener pour récupérer les données des utilisateurs ayant voté
            votansRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Vérifiez si l'utilisateur actuel est présent dans l'ensemble des votants
                        boolean userVoted = dataSnapshot.child(userId).exists();

                        if (userVoted) {
                            // L'utilisateur a déjà voté
                            Toast.makeText(context, "لقد قمت بالتّصويت من قبل", Toast.LENGTH_SHORT).show();
                        }
                        // Indiquez si l'utilisateur a voté ou non au listener
                        listener.onChecked(userVoted);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Une erreur s'est produite lors de la récupération des données
                    // Gérez cette situation selon vos besoins
                    // Indiquez que l'utilisateur n'a pas voté (false)
                    listener.onChecked(false);
                }
            });
        }
    }


    private void loadYserInfo1() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Winner");
        reference.child("Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String Profilimage=""+snapshot.child("1").getValue();
                Glide.with(Concurence.this).load(Profilimage).placeholder(R.drawable.ic_person_gray).into(binding.profilrIv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void loadYserInfo2() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Winner");
        reference.child("Image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String Profilimage2=""+snapshot.child("2").getValue();
                Glide.with(Concurence.this).load(Profilimage2).placeholder(R.drawable.b_fav2).into(binding.profilrIv2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void retrieveConcurrenceData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Winner");

        // Récupérer les données pour concurent1
        DatabaseReference concurrence1Ref = ref.child("concurent1");
        concurrence1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Les données pour concurent1 existent, récupérez-les


                    bookId1 = snapshot.child("bookId").getValue(String.class);
                    email1 = snapshot.child("email").getValue(String.class);

                    // Utilisez les données récupérées comme vous le souhaitez
                    // Par exemple, mettez-les dans des TextViews ou affichez-les d'une autre manière
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
                    // Les données pour concurent2 existent, récupérez-les
                     email2 = snapshot.child("email").getValue(String.class);
                    bookId2 = snapshot.child("bookId").getValue(String.class);

                    // Utilisez les données récupérées comme vous le souhaitez
                    // Par exemple, mettez-les dans des TextViews ou affichez-les d'une autre manière
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Gérez cette situation selon vos besoins
            }
        });
    }
    private void checkUser() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                String userType=""+snapshot.child("userType").getValue();
                if (userType.equals("user")){
                    Toast.makeText(Concurence.this, "هذه الخاصيّة غير متاحة ", Toast.LENGTH_SHORT).show();


                }else if(userType.equals("admin")){
                    Intent intent1=new Intent(Concurence.this,edit_concurence.class);

                    startActivity(intent1);

                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }






}
