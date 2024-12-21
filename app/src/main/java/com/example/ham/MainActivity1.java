package com.example.ham;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ham.Services.OnClearFromRecentService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends AppCompatActivity implements Playable{

    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageButton play;

    ImageButton   previousBtnPA;
    ImageButton nextBtnPA,backBtnPA, repeatBtn;
    private int currentposition;
    TextView title;
    NotificationManager notificationManager;
    List<Track> tracks;
    int position=0;
    boolean isPlaying=false;
    private String BookTitle ;
    private String BookAudio;
    private String bookId;


    private int currentPosition;

    private Handler handler = new Handler();

    private TextView tvSeekBarStart;
    private ExtendedFloatingActionButton playPauseBtn;
    private SeekBar seekBar;
    private TextView songNamePA;



    private MediaPlayer mediaPlayer =new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_player);

        play=findViewById(R.id.play);
        title=findViewById(R.id.songNamePA);
        seekBar=findViewById(R.id.seekBarPA);
        tvSeekBarStart=findViewById(R.id.tvSeekBarStart);
        previousBtnPA=findViewById(R.id.previousBtnPA);
        nextBtnPA=findViewById(R.id.nextBtnPA);
        songNamePA=findViewById(R.id.songNamePA);
        backBtnPA=findViewById(R.id.backBtnPA);
        repeatBtn=findViewById(R.id.repeatBtnPA);
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartAudio();
            }
        });
        backBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        bookId = getIntent().getStringExtra("BookId");
        BookTitle = getIntent().getStringExtra("BookTitle");
        BookAudio = getIntent().getStringExtra("BooKAudio");
        songNamePA.setText(BookTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
            // Si l'application n'a pas la permission, demandez-la à l'utilisateur
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            // L'application a la permission ou l'utilisateur l'a accordée précédemment
            // Vous pouvez maintenant afficher la notification et démarrer le service
            createChanel();
            registerReceiver(broadcastReceiver,new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
            populuateTracks();

            startAudioBook();
        }



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                    onTrackPause();
                else {
                    try {
                        onTrackPlay();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        previousBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int newPosition = currentPosition - 5000; // Déplacez la position de 5 secondes en arrière (5000 millisecondes)

                    // Vérifiez que la nouvelle position n'est pas en dessous de 0
                    if (newPosition < 0) {
                        newPosition = 0;
                    }

                    mediaPlayer.seekTo(newPosition); // Définissez la nouvelle position
                }
                currentPosition = mediaPlayer.getCurrentPosition();
            }
        });
        nextBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int newPosition = currentPosition + 5000; // Avancez la position de 5 secondes en avant (5000 millisecondes)

                    int duration = mediaPlayer.getDuration();

                    // Vérifiez que la nouvelle position n'est pas supérieure à la durée totale de l'audio
                    if (newPosition > duration) {
                        newPosition = duration;
                    }

                    mediaPlayer.seekTo(newPosition); // Définissez la nouvelle position
                }
                currentPosition = mediaPlayer.getCurrentPosition();
            }
        });


    }

    private void createChanel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CreateNotification.CHANNEL_ID,"KOD_DEV",NotificationManager.IMPORTANCE_LOW);

            notificationManager =getSystemService(NotificationManager.class);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);

            }
        }

    }

    //populate List with tracks
    private void populuateTracks(){
        tracks=new ArrayList<>();
        tracks.add(new Track("اسم مؤسستنا \" اقرأ \" فمن المخجل أن تكون واحدا منّا وأنت لا تقرأ",BookTitle,R.drawable.mal));



    }

    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getExtras().getString("actionname");
            switch (action){
                case CreateNotification.ACTION_PREVOIUSE:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if(isPlaying){
                        onTrackPause();
                    }
                    else{
                        try {
                            onTrackPlay();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;

            }
        }
    };

    @Override
    public void onTrackPrevious() {
        position--;
        CreateNotification.createNotification(MainActivity1.this,tracks.get(position),R.drawable.baseline_pause_24,
                position,tracks.size()-1);
        title.setText(tracks.get(position).getTitle());

    }

    @Override
    public void onTrackPlay() throws IOException {

        CreateNotification.createNotification(MainActivity1.this,tracks.get(position),R.drawable.baseline_pause_24,
                position,tracks.size()-1);
        play.setImageResource(R.drawable.baseline_pause_24);
        title.setText(tracks.get(position).getArtiste());
        isPlaying=true;
        currentposition=mediaPlayer.getCurrentPosition();
        mediaPlayer.start();






    }

    @Override
    public void onTrackPause() {

        CreateNotification.createNotification(MainActivity1.this,tracks.get(position),R.drawable.baseline_play_arrow_24,
                position,tracks.size()-1);
        play.setImageResource(R.drawable.baseline_play_arrow_24);
        title.setText(tracks.get(position).getTitle());
        isPlaying=false;
        mediaPlayer.pause();
        currentposition=mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();


    }

    @Override
    public void onTrackNext() {
        position++;
        CreateNotification.createNotification(MainActivity1.this,tracks.get(position),R.drawable.baseline_pause_24,
                position,tracks.size()-1);
        title.setText(tracks.get(position).getTitle());

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }
    private void startAudioBook() {
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Books").child(bookId);

        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le livre avec l'ID spécifié existe dans la base de données
                    ModelPdf book = dataSnapshot.getValue(ModelPdf.class);

                    if (book != null) {
                        // Vous avez maintenant le livre dans l'objet 'book', vous pouvez obtenir l'URL audio
                        String audioUrl = book.getAudioUrl();

                        if (audioUrl != null && !audioUrl.isEmpty()) {
                            // Créez un MediaPlayer


                            try {
                                // Définissez la source audio à partir de l'URL
                                mediaPlayer.setDataSource(audioUrl);

                                // Préparez le lecteur
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                isPlaying=true;
                                updateSeekBar(mediaPlayer);
                                updateSeekBarTime(mediaPlayer);

                                // Commencez la lecture de l'audio


                            } catch (IOException e) {
                                // Gérez les erreurs ici (par exemple, URL audio incorrecte)
                                e.printStackTrace();
                            }
                        } else {
                            // L'URL audio est vide ou nulle
                            // Gérez cette situation en conséquence
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Gérez les erreurs de Firebase ici, si nécessaire
            }
        });
    }
    private void restartAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0); // Revenir au début de l'audio
            mediaPlayer.start();   // Commencer à jouer
            isPlaying = true;
            play.setImageResource(R.drawable.baseline_pause_24); // Mettez à jour l'icône du bouton
            updateSeekBar(mediaPlayer);
            updateSeekBarTime(mediaPlayer);
        }
    }



    private void updateSeekBarTime(MediaPlayer mediaPlayer) {


        if (mediaPlayer != null && tvSeekBarStart != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int minutes = currentPosition / 60000;
            int seconds = (currentPosition % 60000) / 1000;
            String elapsedTime = String.format("%02d:%02d", minutes, seconds);
            tvSeekBarStart.setText(elapsedTime);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        updateSeekBarTime(mediaPlayer); // Planifiez la prochaine mise à jour du temps écoulé
                    }
                }
            }, 1000); // Mettez à jour le temps toutes les 1 seconde
        }
    }

    private void updateSeekBar(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            seekBar.setMax(mediaPlayer.getDuration()); // Définit la valeur maximale de la SeekBar sur la durée totale de l'audio

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition); // Définit la position actuelle de la SeekBar
                        updateSeekBar(mediaPlayer); // Planifie la prochaine mise à jour de la position
                    }
                }
            }, 1000); // Mettez à jour la position de la SeekBar toutes les 1 seconde (ajustez selon vos besoins)
        }

    }



}