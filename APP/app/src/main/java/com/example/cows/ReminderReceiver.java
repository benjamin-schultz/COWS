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
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Constants.NOTIFICATION_DEFAULT, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent waterIntent = new Intent(context, ActionReceiver.class);
        waterIntent.putExtra(Constants.NOTIFICATION_ACTION_NAME, Constants.NOTIFICATION_ACTION_WATER);
        waterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent waterPendingIntent = PendingIntent.getBroadcast(context, Constants.NOTIFICATION_WATER, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_description))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.notification_icon, "WATER!", waterPendingIntent);
        notificationManager.notify(Constants.NOTIFICATION_MANAGER_ID, mNotifyBuilder.build());
    }
}

