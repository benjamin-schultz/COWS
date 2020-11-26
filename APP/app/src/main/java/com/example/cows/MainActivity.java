package com.example.cows;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
    }

    public void sendWater(View view) {
        final TextView waterState = (TextView) findViewById(R.id.wateringStateText);
        final EditText durationText = (EditText) findViewById(R.id.durationText);

        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(getString(R.string.pref_file),Context.MODE_PRIVATE);
        String ipAddress = sharedPref.getString(getString(R.string.ip_address), "0.0.0.0");

        RequestQueue queue = Volley.newRequestQueue(this);
        String duration = durationText.getText().toString();
        if (duration.equals("")) {
            waterState.setText("Please enter a duration!");
        } else {
            String url = "http://" + ipAddress + "/duration=" + duration;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            waterState.setText(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    waterState.setText(error.toString());
                }
            });

            queue.add(stringRequest);
        }
    }

    public void setReminder(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TEST")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(0, builder.build());
    }

    public void setAlarm(View view) {
        Calendar cur = Calendar.getInstance();
        cur.add(Calendar.SECOND, 10);

        Intent myIntent = new Intent(this, ReminderReceiver.class);
        int ALARM1_ID = 10000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cur.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "R.string.channel_name";
            String description = "R.string.channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TEST", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startSetup(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }
}

