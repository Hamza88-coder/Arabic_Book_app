package com.example.ham;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ham.databinding.RowPdfAdminBinding;

import com.github.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategoryUser adapterCategory;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private RowPdfAdminBinding binding;

    private FilterPdfAdmin filter;
    private static final String TAG ="PDF_ADAPTER_TAG";
    private ProgressDialog progressDialog;

    int i,k;





    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList=pdfArrayList;
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("المرجو الآنتظار");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        ModelPdf model=pdfArrayList.get(position);
        String title=model.getTitle();
        String categoryId=model.getCategoryId();
        String description= model.getDescription();
        String ID=model.getId();
        long timestamp= model.getTimestamp();

        String formatteDate=MyApplication.formatTimestamp(timestamp);
        String isChild=model.getIsChild();
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formatteDate);
        holder.bookId.setText(ID);






        MyApplication.loadCategory(""+categoryId,holder.categoryTv);
        loadImageFromUrl(model,holder);
        progressDialog.dismiss();
        holder.progressBar.setVisibility(View.INVISIBLE);


      checkUser();
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i==1 && isChild.equals("false")){

                    moreOptionsDialogue(model,holder);

                }

              else if(i==0 && isChild.equals("false"))
                  moreOptionsDialogueUser(model,holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent =new Intent(context,pdfDetail.class);
               intent.putExtra("bookId",model.getId());
                intent.putExtra("imageUrl",model.getImageUrl());
                intent.putExtra("pdfUrl",model.getPdfUrl());



               context.startActivity(intent);
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
                    i=0;



                }else if(userType.equals("admin")){
                    i=1;

                }

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
    }









    private void moreOptionsDialogue(ModelPdf model, HolderPdfAdmin holder) {
        String BookId=model.getId();

        String BookUrl= model.getPdfUrl();
        String BookAudio =model.getAudioUrl();
        String Bookimage=model.getPdfUrl();
        String BookTitle=model.getTitle();
        String bookGroup=model.getGroup();
        String options []={"تعديل الكتاب","حذف الكتاب","الإستماع إلى المقطع الصّوتي" ,"النّهايات المقترحة ","إضافة نهاية"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("إختر").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  if(i==0){
                      Intent intent=new Intent(context,PdfEditActivity.class);
                      intent.putExtra("BookId",BookId);


                      context.startActivity(intent);



                  }else if(i==1){
                      MyApplication.deleteBook(context,""+BookId,""+BookUrl,""+BookTitle);

                  }else if(i==2){
          

                      Intent intent=new Intent(context,MainActivity1.class);
                     intent.putExtra("BookId",BookId);
                      intent.putExtra("BookTitle",BookTitle);
                      intent.putExtra("BookAudio",BookAudio);
                      intent.putExtra("Bookimage",Bookimage);
                      context.startActivity(intent);


                }else if(i==3){
                      Intent intent=new Intent(context,pdfListeAdmin111.class);
                      intent.putExtra("BookId",BookId);
                      intent.putExtra("group",bookGroup);
                      context.startActivity(intent);


                  }else if(i==4){
                    Intent intent=new Intent(context,pdfAddChild.class);
                    intent.putExtra("BookId",BookId);
                    intent.putExtra("group",bookGroup);
                    context.startActivity(intent);

                }
            }
        }).show();
    }
    private void moreOptionsDialogueUser(ModelPdf model, HolderPdfAdmin holder) {
        String BookId=model.getId();

        String BookUrl= model.getPdfUrl();
        String email= LoginActivity.email;
        String categoryId=model.categoryId;
        String BookAudio =model.getAudioUrl();
        String Bookimage=model.getPdfUrl();
        String BookTitle=model.getTitle();
        String bookGroup=model.getGroup();
        String options []={"الإستماع إلى المقطع الصّوتي","عرض النّهايات المقترحة ","إضافة نهاية"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("إختر").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    EmailChecker.checkEmailExistence(BookId, email, new ExistCallback() {
                        @Override
                        public void onExist(boolean exists) {
                            if (!categoryId.equals("1693754558164")) {

                                Intent intent=new Intent(context,MainActivity1.class);
                                intent.putExtra("BookId",BookId);
                                intent.putExtra("BookTitle",BookTitle);
                                intent.putExtra("BookAudio",BookAudio);
                                intent.putExtra("Bookimage",Bookimage);
                                context.startActivity(intent);
                                Toast.makeText(context, "Exist", Toast.LENGTH_SHORT).show();
                               
                            } else {
                                if(exists){
                                    Intent intent=new Intent(context,MainActivity1.class);
                                    intent.putExtra("BookId",BookId);
                                    intent.putExtra("BookTitle",BookTitle);
                                    intent.putExtra("BookAudio",BookAudio);
                                    intent.putExtra("Bookimage",Bookimage);
                                    context.startActivity(intent);
                                    Toast.makeText(context, "Exist", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(context, "ادفع ثلاث دراهم واستمتع بالتّسجيل الصّوتي", Toast.LENGTH_SHORT).show();

                                }



                            }
                        }
                    });


                }else if(i==1){
                    Intent intent=new Intent(context,pdfListeAdmin111.class);
                    intent.putExtra("BookId",BookId);
                    intent.putExtra("group",bookGroup);
                    context.startActivity(intent);


                }else if(i==2){
                    Intent intent=new Intent(context,pdfAddChild.class);
                    intent.putExtra("BookId",BookId);
                    intent.putExtra("group",bookGroup);
                    context.startActivity(intent);

                }
            }
        }).show();
    }



    private void loadImageFromUrl(ModelPdf model, HolderPdfAdmin holder) {
        try {



            // Obtenez l'URL de l'image à partir du modèle
            String imageUrl = model.getImageUrl(); // Remplacez par le chemin de votre image

            // Utilisez la variable imageView pour charger l'image
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imageView); // Utilisez holder.imageView pour référencer votre ImageView
            progressDialog.dismiss();


        } catch (Exception e) {
            // Gérez les erreurs ici
            e.printStackTrace();
            Log.e(TAG,  e.getMessage());
        }}














    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterPdfAdmin(filterList,this);

        }
        return filter;
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder{
        PDFView pdfView;
        ProgressBar progressBar;
        ImageButton moreBtn;
        ImageView imageView;
        TextView titleTv,descriptionTv,categoryTv,SizeTv,dateTv,bookId;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);


            progressBar=binding.progressbar;
            titleTv=binding.titletv;

            descriptionTv=binding.descriptionTv;

            imageView=binding.imageView;
            dateTv=binding.dateTv;
            moreBtn=binding.moreBtn;
            bookId=binding.bookID;
        }
    }


}
