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

import com.example.ham.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivityLoginBinding binding;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("المرجو الآنتظار");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatedata();
            }
        });

        binding.forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });
    }
    public   static String email="",password="";
    private void validatedata() {
        email=binding.emilEt.getText().toString().trim();
        password=binding.passwordEt.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "البريد الإلكتروني غير صحيح", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty((password))){
            Toast.makeText(this,"أدخل كلمة المرور",Toast.LENGTH_SHORT).show();
        }else{
            loginUser();
        }
    }

    private void loginUser() {
        progressDialog.setMessage("جاري تسجيل الدّخول...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        progressDialog.setMessage("التأكّد من المستخدم....");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressDialog.dismiss();
                String userType=""+snapshot.child("userType").getValue();
                if (userType.equals("user")){
                    startActivity(new Intent(LoginActivity.this,DashboardUserActivity.class));
                    finish();

                }else if(userType.equals("admin")){
                    startActivity(new Intent(LoginActivity.this,DashboardAdminActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }
}