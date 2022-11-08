package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class UserNotification extends BroadcastReceiver {


    public UserNotification(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyUser")
                .setSmallIcon(R.drawable.ic_baseline_event_note_24)
                .setContentTitle("You have an event now!")
                .setContentText("Check the app to check on event details.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        /*Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contextIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contextIntent);*/
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200,builder.build());
    }
}
