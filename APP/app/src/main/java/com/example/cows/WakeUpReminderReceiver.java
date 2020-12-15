package com.example.cows;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class WakeUpReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
            int interval = sharedPreferences.getInt(Constants.PREF_REMINDER_INTERVAL, Constants.REMINDER_INTERVAL_DEFAULT);
            long reminder_time = sharedPreferences.getLong(Constants.PREF_REMINDER_TIME, Constants.REMINDER_TIME_DEFAULT);

            Intent myIntent = new Intent(context, ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.ALARM_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, reminder_time, AlarmManager.INTERVAL_DAY*interval, pendingIntent);
        }
    }
}
