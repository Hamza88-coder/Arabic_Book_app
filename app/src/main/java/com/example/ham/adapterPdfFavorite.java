package com.example.ham;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ham.databinding.RowPdfFavoriteBinding;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class adapterPdfFavorite extends RecyclerView.Adapter< adapterPdfFavorite.HolderPdfFavorite> {
    private Context context;
    private static final String TAG="FAV_BOOK";
    private ArrayList<ModelPdf> pdfArrayList;
    private RowPdfFavoriteBinding binding;

    public adapterPdfFavorite(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowPdfFavoriteBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfFavorite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfFavorite holder, int position) {
        ModelPdf model=pdfArrayList.get(position);
        loadPdfDetails(model,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,pdfDetail.class);
                intent.putExtra("bookId",model.getId());
                context.startActivity(intent);


            }
        });
        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.removeFromFav(context,model.getId());

            }
        });


    }

    private void loadPdfDetails(ModelPdf model, HolderPdfFavorite holder) {
        String bookId =model.getId();
        Log.d(TAG,"loadBookDEtails:...."+bookId);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Books");
        reference.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String bookTitle=""+snapshot.child("title").getValue();
                String description=""+snapshot.child("description").getValue();
                String categoryId=""+snapshot.child("categoryId").getValue();
                String BookUrl=""+snapshot.child("pdfUrl").getValue();
                String uid=""+snapshot.child("uid").getValue();
                String timestamp=""+snapshot.child("timestamp").getValue();
                String audioUrl=""+snapshot.child("audioUrl").getValue();
                String imageUrl=""+snapshot.child("imageUrl").getValue();


               model.setFavorite(true);
               model.setTitle(bookTitle);
               model.setDescription(description);
               model.setTimestamp(Long.parseLong(timestamp));
               model.setCategoryId(categoryId);
               model.setUid(uid);
               model.setPdfUrl(BookUrl);
               model.setAudioUrl(audioUrl);
               model.setImageUrl(imageUrl);
               String date=MyApplication.formatTimestamp(Long.parseLong(timestamp));

               MyApplication.loadCategory(categoryId,holder.categoryTv);
               

               holder.titleTv.setText(bookTitle);
               holder.descriptionTv.setText(description);
               holder.dateTv.setText(date);
                 // Replace with your image URL
                Glide.with(context).load(imageUrl).into(holder.imageView);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return pdfArrayList.size();

    }


    class HolderPdfFavorite extends RecyclerView.ViewHolder{
        PdfViewActivity pdfView;
        ProgressBar progressBar;
        ImageButton  removeFavBtn;
        ImageView imageView;
        TextView titleTv,descriptionTv,categoryTv,SizeTv,dateTv,bookId;

        public HolderPdfFavorite(@NonNull View itemView) {
            super(itemView);



            titleTv=binding.titletv;

            descriptionTv=binding.descriptionTv;
            removeFavBtn=binding.removeFavBtn;

            imageView=binding.imageView;
            dateTv=binding.dateTv;

            bookId=binding.bookID;
        }
    }

}
