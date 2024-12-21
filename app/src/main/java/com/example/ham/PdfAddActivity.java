package com.example.ham;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.databinding.ActivityPdfAddBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {
    private static final int PDF_PICK_CODE = 12;
    private ActivityPdfAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;
    private static final int AUDIO_PICK_CODE =1000;
    private static final int IMAGE_PICK_CODE =10;
    private static final String TAG ="ADD_PDF_TAG";
    private ProgressDialog progressDialog;
    private Uri imageUri;
    private Uri audioUri,pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        loadPdfCategories();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("رجاء إنتظر ");
        progressDialog.setCanceledOnTouchOutside(false);


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

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioPickIntent();
            }
        });
        
        binding.iconLivre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickIntent();
            }
        });
        
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                categoriePickDialog();
            }
        });
        binding.attachBtnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
    }

    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: Starting PDF pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf"); // Spécifiez le type de fichier PDF
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }


    private void imagePickIntent() {
        Log.d(TAG, "imagePickIntent: Starting image pick intent");
        Intent intent = new Intent();
        intent.setType("image/*"); // Sélectionner tous les types d'images (JPEG, PNG, etc.)
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_PICK_CODE);
    }




    private String title="",description="";
    private void validateData() {
        Log.d(TAG,"validateData : validating data...");
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "أدخل العنوان", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "أدخل الوصف", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedTitleCategory)) {
            Toast.makeText(this, "لم تحدد القسم", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null) {
            Toast.makeText(this, "لم تحدد الصورة", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "لم تحدد الملف", Toast.LENGTH_SHORT).show();
        } else {
            uploadFilesToStorage();
        }
    }


    private void uploadFilesToStorage() {
        Log.d(TAG, "uploadFilesToStorage: uploading files to Storage...");

        // Affichez la ProgressDialog avant de commencer le téléchargement
        progressDialog.setMessage("تحميل ...");
        progressDialog.show();

        // Générer des noms de fichiers uniques pour le fichier audio, l'image et le PDF
        long timestamp = System.currentTimeMillis();
        String audioFilePathAndName = "Audio/" + timestamp + ".mp3";
        String imageFilePathAndName = "Images/" + timestamp + ".jpg";
        String pdfFilePathAndName = "PDFs/" + timestamp + ".pdf";

        // Obtenir une référence à l'emplacement Firebase Storage où vous souhaitez télécharger l'audio
        StorageReference audioStorageReference = FirebaseStorage.getInstance().getReference(audioFilePathAndName);

        // Obtenir une référence à l'emplacement Firebase Storage où vous souhaitez télécharger l'image
        StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference(imageFilePathAndName);

        // Obtenir une référence à l'emplacement Firebase Storage où vous souhaitez télécharger le PDF
        StorageReference pdfStorageReference = FirebaseStorage.getInstance().getReference(pdfFilePathAndName);

        // Charger le fichier audio
        audioStorageReference.putFile(audioUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "onSuccess: Audio upload to Storage..");

                    // Obtenez l'URL de téléchargement du fichier audio
                    audioStorageReference.getDownloadUrl().addOnSuccessListener(audioUri -> {
                        String uploadedAudioUrl = audioUri.toString();

                        // Charger l'image après avoir chargé l'audio
                        imageStorageReference.putFile(imageUri)
                                .addOnSuccessListener(imageTaskSnapshot -> {
                                    Log.d(TAG, "onSuccess: Image upload to Storage..");

                                    // Obtenez l'URL de téléchargement de l'image
                                    imageStorageReference.getDownloadUrl().addOnSuccessListener(imageUri -> {
                                        String uploadedImageUrl = imageUri.toString();

                                        // Charger le PDF après avoir chargé l'audio et l'image
                                        pdfStorageReference.putFile(pdfUri)
                                                .addOnSuccessListener(pdfTaskSnapshot -> {
                                                    Log.d(TAG, "onSuccess: PDF upload to Storage..");

                                                    // Obtenez l'URL de téléchargement du PDF
                                                    pdfStorageReference.getDownloadUrl().addOnSuccessListener(pdfUri -> {
                                                        String uploadedPdfUrl = pdfUri.toString();

                                                        // Appeler une méthode pour enregistrer les URL dans la base de données ou effectuer d'autres opérations

                                                        // Masquer la ProgressDialog après le succès
                                                        progressDialog.dismiss();
                                                        Toast.makeText(PdfAddActivity.this, "Files Upload with Success", Toast.LENGTH_SHORT).show();
                                                        uploadFilesIntoDB(uploadedImageUrl, uploadedAudioUrl, uploadedPdfUrl, timestamp);
                                                    });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.d(TAG, "onFailure: PDF upload failed due to " + e.getMessage());
                                                    Toast.makeText(PdfAddActivity.this, "PDF Upload Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                    // Masquer la ProgressDialog en cas d'échec
                                                    progressDialog.dismiss();
                                                });
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "onFailure: Image upload failed due to " + e.getMessage());
                                    Toast.makeText(PdfAddActivity.this, "Image Upload Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                    // Masquer la ProgressDialog en cas d'échec
                                    progressDialog.dismiss();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: Audio upload failed due to " + e.getMessage());
                    Toast.makeText(PdfAddActivity.this, "Audio Upload Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Masquer la ProgressDialog en cas d'échec
                    progressDialog.dismiss();
                });
    }


    void uploadFilesIntoDB(String uploadedImageUrl, String uploadedAudioUrl, String uploadedPdfUrl, long timestamp) {
        Log.d(TAG, "uploadFilesIntoDB: Uploading URLs into Firebase DB...");
        progressDialog.setMessage("تحميل ...");

        String uid = firebaseAuth.getUid();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("categoryId", selectedCategoryId);
        hashMap.put("pdfUrl", uploadedPdfUrl); // Ajoutez l'URL du PDF
        hashMap.put("audioUrl", uploadedAudioUrl); // Ajoutez l'URL de l'audio
        hashMap.put("imageUrl", uploadedImageUrl); // Ajoutez l'URL de l'image
        hashMap.put("timestamp", timestamp);
        hashMap.put("group","aaa"+timestamp);
        hashMap.put("isChild","false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child("" + timestamp).setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Successfully uploaded URLs...");
                    Toast.makeText(PdfAddActivity.this, "تمت العملية بنجاح", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onFailure: Failed to upload to the DB due to " + e.getMessage());
                    Toast.makeText(PdfAddActivity.this, "Failed to upload to the DB due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




    private void loadPdfCategories() {
        Log.d(TAG,"loadPdfCategories: Loading pdf categories ...");
        categoryTitleArrayList=new ArrayList<>();
        categoryIdArrayList=new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){

                   String categoryId=""+ds.child("id").getValue();
                   String categoryTitle=""+ds.child("category").getValue();
                   categoryTitleArrayList.add(categoryTitle);
                   categoryIdArrayList.add(categoryId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
      private String selectedCategoryId,selectedTitleCategory;
    private void categoriePickDialog() {

        Log.d(TAG,"categoryPickDialog : showing categorie pick dialoque");
        String[] categoriesARRAY=new String[categoryTitleArrayList.size()];
        for(int i=0;i<categoryTitleArrayList.size();i++){
            categoriesARRAY[i]=categoryTitleArrayList.get(i);
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick catrgorie").setItems(categoriesARRAY, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int w) {
                    selectedTitleCategory=categoryTitleArrayList.get(w);
                    selectedCategoryId=categoryIdArrayList.get(w);
                     binding.categoryTv.setText(selectedTitleCategory);
                     Log.d(TAG,"onClick :Selected Category:"+selectedCategoryId+""+selectedTitleCategory);
            }
        }).show();


    }

    private void audioPickIntent() {
        Log.d(TAG, "audioPickIntent: Starting audio pick intent");
        Intent intent = new Intent();
        intent.setType("audio/*"); // Sélectionner tous les types de fichiers audio
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                Log.d(TAG, "onActivityResult: URI: " + fileUri);

                String fileExtension = getFileExtension(fileUri);

                if (requestCode == AUDIO_PICK_CODE && isAudioFile(fileExtension)) {
                    // C'est un fichier audio
                    audioUri = fileUri;
                } else if (requestCode == IMAGE_PICK_CODE && isImageFile(fileExtension)) {
                    // C'est un fichier image
                    imageUri = fileUri;
                } else if (requestCode == PDF_PICK_CODE && isPdfFile(fileExtension)) {
                    // C'est un fichier PDF
                    pdfUri = fileUri;
                } else {
                    // Fichier non pris en charge
                    Toast.makeText(this, "نوعيّة الملّف غير مقبولة", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.d(TAG, "onActivityResult: File picking cancelled");
            Toast.makeText(this, "ملغى", Toast.LENGTH_SHORT).show();
        }
    }


    // Méthode pour vérifier si l'extension du fichier correspond à une image prise en charge
    private boolean isImageFile(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension)  || "pdf".equals(extension);
    }

    // Méthode pour vérifier si l'extension du fichier correspond à un fichier audio pris en charge
    private boolean isAudioFile(String extension) {
        return "mp3".equals(extension) || "wav".equals(extension) || "ogg".equals(extension);
    }

    // Méthode pour obtenir l'extension du fichier à partir du chemin du fichier
// Méthode pour obtenir l'extension du fichier depuis l'URI
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    // Méthode pour obtenir le chemin du fichier depuis l'URI
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(column_index);
            }
            cursor.close();
        }
        return filePath;
    }
    private boolean isPdfFile(String extension) {
        return "pdf".equals(extension);
    }

}