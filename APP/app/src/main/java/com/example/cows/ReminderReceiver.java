package com.example.cows;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, "TEST")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Title")
                .setContentText("Text")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        notificationManager.notify(0, mNotifyBuilder.build());
    }
}

