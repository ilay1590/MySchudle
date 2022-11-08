package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {

    //מחלקה שמטרתה ליצור את כל המאזינים באפליקציה

    private static final String TAG = "MyBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // יוצר את המאזינים ושולח טואסט בהתאם
        StringBuilder sb = new StringBuilder();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            sb.append("Boot completed");
        }
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            if (sb.length() != 0){
                sb.append("\n");
            }
            sb.append("Connectivity changed");
        }
        if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())){
            if (sb.length() != 0){
                sb.append("\n");
            }
            sb.append("Battery low");
        }
        String log = sb.toString();
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
    }


}