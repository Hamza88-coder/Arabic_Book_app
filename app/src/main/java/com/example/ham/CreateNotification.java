package com.example.ham;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ham.Services.NotificationActionService;


public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";
    public static final String ACTION_PREVOIUSE = "actionpreviouse";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static Notification notification;


    @SuppressLint("MissingPermission")
    public static void createNotification(Context context, Track track, int playbutton, int pos, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), track.getImage());

            PendingIntent pendingIntentPrevious;
            int drv_previous;
            if (pos == 0) {
                pendingIntentPrevious = null;
                drv_previous = 0;
            } else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVOIUSE);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                drv_previous = R.drawable.baseline_skip_previous_24;
            }

            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            PendingIntent pendingIntentplay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentNext;
            int drv_next;
            if (pos == size) {
                pendingIntentNext = null;
                drv_next = 0;
            } else {
                Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                drv_next = R.drawable.baseline_skip_next_24;
            }


            // create notification
             notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_music_note_24)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getArtiste())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true) // Afficher la notification une seule fois
                    .addAction(drv_previous, "Précédent", pendingIntentPrevious)
                    .addAction(playbutton, "Lire", pendingIntentplay)
                    .addAction(drv_next, "Suivant", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2).setMediaSession(mediaSessionCompat.getSessionToken())) // Afficher l'action "Lire" dans la vue compacte
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();




            notificationManagerCompat.notify(1,notification);
        }
    }

}
