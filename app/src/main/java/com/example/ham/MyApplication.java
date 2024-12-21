package com.example.ham;


import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyApplication extends Application {
    public static final String TAG_DOWNLOD="DOWNOALD_TAG";
    public boolean isVote=false;




    @Override
    public void onCreate() {
        super.onCreate();
    }

    public  static final String formatTimestamp(long timestamp){
        Calendar cal =Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/MM/yyyy",cal).toString();
        return date;

    }
    public static void deleteBook(Context context, String bookId, String bookUrl, String bookTitle) {
        String TAG="DELETE_BOOK_TAG";

        Log.d(TAG,"deleteBook: Deleting...");
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("المرجو الآنتظار");
        progressDialog.setTitle("حذف "+bookTitle+"...");
        progressDialog.show();
        Log.d(TAG,"deleteBook: Deleting from Storage ..");
        StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"on Succes: Deleted from Storage ..");
                Log.d(TAG,"on Succes: Now deleted from the db ..");

                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Books");
                reference.child(bookId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"on Succes: Deleted ffrom db too ..");
                        progressDialog.dismiss();
                        Toast.makeText(context,"تم الحذف...",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: Failed to delete from db due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFailure: Failed to delete from storage due to "+e.getMessage());
                progressDialog.dismiss();
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
    public static void loadCategory(String categoryId, TextView categoryTv) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String category=""+snapshot.child("category").getValue();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public static void downoald(Context context,String bookId,String bookTitle,String bookUrl){
        Log.d(TAG_DOWNLOD,"downoald book:downoalding book...");
        String nameWithExtension=bookTitle+ ".pdf";
        Log.d(TAG_DOWNLOD,"downoald book Name:"+nameWithExtension);
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("المرجو الآنتظار");

        progressDialog.show();
        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.getBytes(1000000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG_DOWNLOD,"onSucees:book downoalded");
                Log.d(TAG_DOWNLOD,"downoald book:Saving book...");
                saveDownoaldBook(context,progressDialog,bytes,nameWithExtension,bookId);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG_DOWNLOD,"onFailure:Failed to downoald"+e.getMessage());
                
                progressDialog.dismiss();
                Toast.makeText(context, "Fialde to downoald", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private static void saveDownoaldBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String bookId) {
        Log.d(TAG_DOWNLOD,"save downoald book: saving downoalding book...");
        try {
           File downoaldFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
           downoaldFolder.mkdirs();
           String filepath=downoaldFolder.getPath() + "/" + nameWithExtension;
            FileOutputStream out=new FileOutputStream(filepath);
            out.write(bytes);
            out.close();

            Log.d(TAG_DOWNLOD,"saveDownoaldBook:saved to downaold folder");
            progressDialog.dismiss();

        }catch (Exception e){
            Log.d(TAG_DOWNLOD,"saveDownoaldBook:Failed saving "+e.getMessage());
            Toast.makeText(context, "failed  ", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();



        }
    }

    public static void addTOFavorite(Context context,String bookId){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Toast.makeText(context, "you are not logging in ", Toast.LENGTH_SHORT).show();
        }else{
            long timestamp=System.currentTimeMillis();
            HashMap<String,Object>  hashMap=new HashMap<>();
            hashMap.put("bookId",""+bookId);
            hashMap.put("timestamp",""+timestamp);
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
            ref.child((firebaseAuth.getUid())).child("Favorites").child(bookId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Add to your favor", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "failed to add to your favor"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    
                }
            });

        }
    }
    public static void removeFromFav(Context context, String bookId){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "You are not logged in", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Removed from your favorites", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to remove from your favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    }




