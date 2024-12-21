package com.example.ham;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ham.databinding.RowCommentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment>{
    public AdapterComment(Context context , ArrayList<ModelComment> commentArrayList) {
        this.context = context;
        this.firebaseAuth = firebaseAuth;
        this.commentArrayList = commentArrayList;
       firebaseAuth=FirebaseAuth.getInstance();
    }

    private Context context;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelComment> commentArrayList;
    private RowCommentBinding binding;

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowCommentBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        ModelComment modelComment=commentArrayList.get(position);
        String bookId=modelComment.getBookId();
        String id=modelComment.getId();
        String comment=modelComment.getComment();
        String uid=modelComment.getUid();
        String timestamp=modelComment.getTimestamp();

        String date=MyApplication.formatTimestamp(Long.parseLong(timestamp));

        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        loadUserDetaild(modelComment,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(firebaseAuth.getCurrentUser()!= null && uid.equals(firebaseAuth.getUid())){
                    deleteComment(modelComment,holder);
                }

            }
        });

    }

    private void deleteComment(ModelComment modelComment, HolderComment holder) {
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setTitle("حذف التّعليق").setMessage("هل أنت متأكّد من أنّك تريد حذف هذا التّعليق ؟ ")
                .setPositiveButton("حذف ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(modelComment.getBookId()).child("Comments").child(modelComment.getId())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(context, "حذف ", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "فشل في الحذف", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                }).setNegativeButton("ملغى", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      dialogInterface.dismiss();
                    }
                });
    }

    private void loadUserDetaild(ModelComment modelComment, HolderComment holder) {
        String uid=modelComment.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=""+snapshot.child("name").getValue();
                String profileImage=""+snapshot.child("profileImage").getValue();
                holder.nameTv.setText(name);
                try{
                    Glide.with(context).load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileIv);
                }catch (Exception e){
                    holder.profileIv.setImageResource(R.drawable.ic_person_gray);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public int getItemCount() {

        return commentArrayList.size();
    }


    class HolderComment extends RecyclerView.ViewHolder{

        ShapeableImageView profileIv;
        TextView nameTv,dateTv,commentTv;
        public HolderComment(@NonNull View itemView) {
            super(itemView);
            profileIv=binding.profileIv;
            nameTv=binding.nameTv;
            dateTv=binding.dateTv;
            commentTv=binding.commentTv;



        }
    }
}
