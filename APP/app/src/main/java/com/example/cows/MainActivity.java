package com.example.cows;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Calendar datetime;

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
        String ipAddress = sharedPref.getString(getString(R.string.ip_address), Constants.HTTP_DEFAULT_IP);

        String duration = durationText.getText().toString();
        if (duration.equals("")) {
            waterState.setText(getString(R.string.enter_duration);
        } else {
            water(duration, ipAddress);
        }
    }

    public void water(String duration, String ipAddress) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.HTTP_PREFIX + ipAddress + ":" + Constants.HTTP_PORT + "/" + Constants.HTTP_WATER + duration;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setWaterText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setWaterText(error.toString());
            }
        });

        queue.add(stringRequest);
    }

    public void setWaterText(String text) {
        final TextView waterState = findViewById(R.id.wateringStateText);
        waterState.setText(text);
    }

    public void setAlarm(View view) {
        EditText intervalText = findViewById(R.id.intervalText);
        int interval = Integer.parseInt(intervalText.getText().toString());
        Calendar cur = Calendar.getInstance();
        cur.add(Calendar.SECOND, 10);

        Intent myIntent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.ALARM_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cur.getTimeInMillis(), 1000, pendingIntent);
        Toast.makeText(getApplicationContext(), R.string.reminder_toast, Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(View view) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.ALARM_ID, new Intent(this, ReminderReceiver.class), PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), R.string.cancel_reminder_toast, Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void onReminderTimeClick(View view) {

        final Calendar cur = Calendar.getInstance();
        int mHour = cur.get(Calendar.HOUR_OF_DAY);
        int mMinute = cur.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view1, hourOfDay, minute) -> onTimeSet(view1, hourOfDay, minute), mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        EditText reminderTimeText = findViewById(R.id.reminderTimeText);
        String am_pm = "";

        datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        if (datetime.get(Calendar.AM_PM) == Calendar.AM) {
            am_pm = "AM";
        } else if (datetime.get(Calendar.AM_PM) == Calendar.PM) {
            am_pm = "PM";
        }

        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0)?"12":datetime.get(Calendar.HOUR)+"";

        reminderTimeText.setText(strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm);
    }

    public void startSetup(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }
}

