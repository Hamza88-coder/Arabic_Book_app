package com.example.ham;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.databinding.ActivityPdfEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfEditActivity extends AppCompatActivity {

    private ActivityPdfEditBinding binding;
    private String bookId;
    private ProgressDialog progressDialog;
    private ArrayList<String> categoryTitleArrayList,getCategoryIdArrayList;
    private static final String TAG ="BOOK_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bookId=getIntent().getStringExtra("BookId");
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("المرجو الآنتظار");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadBookInfo();
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();

            }
        });

    }
    private String title="",description="";
    private void validateData() {
        title=binding.titleEt.getText().toString().trim();
        description=binding.descriptionEt.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, " أدخل العنوان...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, " أدخل الوصف...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(this, " حدد القسم ...", Toast.LENGTH_SHORT).show();

        }else{
            updatePdf();
        }
    }

    private void updatePdf() {
        Log.d(TAG,"updatePdf:Starting updating pdf into to db ...");
        progressDialog.setMessage("تجديد الكتاب ...");
        progressDialog.show();

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"onFaiure : failed to update due to "+e.getMessage());

               progressDialog.dismiss();
                Toast.makeText(PdfEditActivity.this, "تم بنجاح...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadBookInfo() {
        Log.d(TAG,"loadBookInfo:Loading book info ");
        DatabaseReference refBooks=FirebaseDatabase.getInstance().getReference("Books");
        refBooks.child("BookId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedCategoryId=""+snapshot.child("categoryId").getValue();
                String description =""+ snapshot.child("description").getValue();
                String title=""+ snapshot.child("title").getValue();

                binding.titleEt.setText(title);
                binding.descriptionEt.setText(description);
                Log.d(TAG,"onDataChange : Loading Book category info");
                DatabaseReference refBookCategory=FirebaseDatabase.getInstance().getReference("Categories");
                refBookCategory.child(selectedCategoryId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category =""+ snapshot.child("category").getValue();
                        binding.categoryTv.setText(category);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String selectedCategoryId="",getSelectedCategoryTitle="";
    private void categoryDialog(){
        String[] categoriesArray=new String[categoryTitleArrayList.size()];
        for(int i=0;i<categoryTitleArrayList.size();i++){
            categoriesArray[i]=categoryTitleArrayList.get(i);
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("إختر القسم").setItems(categoriesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedCategoryId=getCategoryIdArrayList.get(i);
                getSelectedCategoryTitle=categoryTitleArrayList.get(i);

                binding.categoryTv.setText(getSelectedCategoryTitle);
            }
        }).show();
    }
    private void loadCategories() {
        Log.d(TAG,"loadCategories: Loading categories...");
        getCategoryIdArrayList=new ArrayList<>();
        categoryTitleArrayList=new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                getCategoryIdArrayList.clear();
                for(DataSnapshot ds :snapshot.getChildren()){
                    String id=""+ds.child("id").getValue();
                    String category=""+ds.child("category").getValue();
                    getCategoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);
                    Log.d(TAG,"onDataCHange:ID"+id);
                    Log.d(TAG,"onDatachange:category ");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}