package com.example.ham;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ham.databinding.ActivityProfileEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG="EDIT_PROGIL";

    private String name="";
    private ProgressDialog progressDialog;
    private Uri imageUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("المرجو الآنتظار");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth=FirebaseAuth.getInstance();
        loadYserInfo();



        binding.profilrIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageAttachMenu();
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatedata();
            }
        });

         binding.BackBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 onBackPressed();
             }
         });

    }

    private void validatedata() {
        name=binding.nameEt.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "أدخل الإسم", Toast.LENGTH_SHORT).show();
        }else {
            if(imageUri==null){
                uploadProfileImage();

            }else{
                    uploadProfileImage();
            }
        }

    }




    private void pickImageGalerie() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

        }


    private void pickImageCamera() {

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample image description");
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                Log.d(TAG,"onactvitu=yresult"+imageUri);
                Intent data=result.getData();
                imageUri=data.getData();
                binding.profilrIv.setImageURI(imageUri);

            }else{
                Toast.makeText(ProfileEditActivity.this, "ملغى", Toast.LENGTH_SHORT).show();
            }

        }
    });
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                Log.d(TAG,"onactvitu=yresult"+imageUri);
                Intent data=result.getData();
                imageUri=data.getData();
                binding.profilrIv.setImageURI(imageUri);

            }else {
                Toast.makeText(ProfileEditActivity.this, "ملغى", Toast.LENGTH_SHORT).show();
            }

        }
    });

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



                binding.nameEt.setText(name);

                Glide.with(ProfileEditActivity.this).load(Profilimage).placeholder(R.drawable.ic_person_gray).into(binding.profilrIv);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void uploadProfileImage() {
        progressDialog.setMessage("تغيير الصّورة الشّخصيّة...");
        progressDialog.show();
        String filePathAndName = "ProfileImages/" + firebaseAuth.getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: Getting url");
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailed : failed to Getting url");
                progressDialog.dismiss();
            }
        });
    }

    private void getDownloadUrl(StorageReference reference) {
        Task<Uri> uriTask = reference.getDownloadUrl();
        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String uploadedImageUrl = uri.toString();
                updateProfileWithImage(uploadedImageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailed : failed to Getting url");
                progressDialog.dismiss();
            }
        });
    }

    private void updateProfileWithImage(String imageUrl) {
        Log.d(TAG, "updateProfileWithImage: updating profile with image");
        progressDialog.setMessage("تغيير الصّورة الشّخصيّة");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "" + name);
        hashMap.put("profileImage", "" + imageUrl);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void showImageAttachMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.profilrIv);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Galery");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int which = item.getItemId();
                if (which == 1) {
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

}