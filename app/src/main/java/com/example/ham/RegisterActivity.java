package com.example.ham;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.databinding.ActivityReisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    private ActivityReisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityReisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait ");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatedata();
            }
        });
    }
    private String name="";String email="",password="";
    private void validatedata(){
        name=binding.nameEt.getText().toString().trim();
        email=binding.emilEt.getText().toString().trim();
        password=binding.passwordEt.getText().toString().trim();
        String cPassword=binding.cpasswordEt.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"أدخل الاسم...",Toast.LENGTH_SHORT).show();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "بريد غير مقبول...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty((password))){
            Toast.makeText(this,"أدخل كلمة المرور...!",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(cPassword)){
            Toast.makeText(this,"أكد كلمة المرور...!",Toast.LENGTH_SHORT).show();
        }else if(!password.equals(cPassword)){
            Toast.makeText(this,"كلمتا المرور غير متطابقتين",Toast.LENGTH_SHORT).show();
        }else{
            createuserAccount();
        }

    }

    private void createuserAccount() {
        progressDialog.setMessage("إنشاء الحساب...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                updateUserInfo();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("حفظ معلومات المستخدم ...");
        long timestamp=System.currentTimeMillis();
        String uid = firebaseAuth.getUid();
        HashMap<String,Object> hashMap= new HashMap<String,Object>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("name",name);
        hashMap.put("profileImage","");
        hashMap.put("userType","user");
        hashMap.put("timestamp",timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,"تم إنشاء الحساب",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this,DashboardUserActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}