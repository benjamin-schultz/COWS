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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.InetAddresses;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import android.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    Calendar datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        String duration = sharedPref.getString(Constants.PREF_DURATION, Constants.DURATION_DEFAULT);
        int interval = sharedPref.getInt(Constants.PREF_REMINDER_INTERVAL, Constants.REMINDER_INTERVAL_DEFAULT);
        long reminder_time = sharedPref.getLong(Constants.PREF_REMINDER_TIME, Constants.REMINDER_TIME_DEFAULT);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(reminder_time);

        SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);

        final EditText durationText = findViewById(R.id.durationText);
        durationText.setText(duration);

        final EditText intervalText = findViewById(R.id.intervalText);
        intervalText.setText(Integer.toString(interval));

        final EditText reminderTimeText = findViewById(R.id.reminderTimeText);
        reminderTimeText.setText(format.format(cal.getTime()));
    }

    public void sendWater(View view) {
        final TextView waterState = (TextView) findViewById(R.id.wateringStateText);
        final EditText durationText = (EditText) findViewById(R.id.durationText);

        SharedPreferences sharedPref = MainActivity.this.getSharedPreferences(Constants.PREF_FILE,Context.MODE_PRIVATE);

        String duration = durationText.getText().toString();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PREF_DURATION, duration);
        editor.apply();

        if (duration.equals("")) {
            waterState.setText(getString(R.string.enter_duration));
        } else {
            getPublicIP(duration);
        }
    }

    public void water(String duration, String ipAddress) throws UnknownHostException {
        InetAddress COWSaddressInet = InetAddress.getByName(Constants.HTTP_COWS_HOSTNAME);
        String COWSaddress = COWSaddressInet.getHostAddress();
        if (COWSaddress.equals(ipAddress)) {
            SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
            ipAddress = sharedPreferences.getString(Constants.PREF_IP, Constants.HTTP_DEFAULT_IP);
        } else {
            ipAddress = Constants.HTTP_COWS_HOSTNAME;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String username = BuildConfig.COWS_USERNAME;
        String password = BuildConfig.COWS_PASSWORD;

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

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    public void setWaterText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT);
    }

    public void getPublicIP(String duration) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.ipify.org/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            water(duration, response);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT);
            }
        });

        queue.add(stringRequest);
    }

    public void onWaterClick(View view) {
        NetworkIntentService.startActionFoo(this, "test1", "test2");
    }

    public void setAlarm(View view) {
        EditText intervalText = findViewById(R.id.intervalText);
        int interval = Integer.parseInt(intervalText.getText().toString());

        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(Constants.PREF_REMINDER_TIME, datetime.getTimeInMillis());
        editor.putInt(Constants.PREF_REMINDER_INTERVAL, interval);
        editor.apply();

        Intent myIntent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.ALARM_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, datetime.getTimeInMillis(), AlarmManager.INTERVAL_DAY*interval, pendingIntent);
        enableWakeUpReceiver(true);
        Toast.makeText(getApplicationContext(), R.string.reminder_toast, Toast.LENGTH_SHORT).show();
    }

    public void enableWakeUpReceiver(boolean enable) {
        PackageManager pm = this.getPackageManager();
        ComponentName componentName = new ComponentName(this, WakeUpReminderReceiver.class);
        if (enable) {
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public void cancelAlarm(View view) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.ALARM_ID, new Intent(this, ReminderReceiver.class), PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        enableWakeUpReceiver(false);
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

        datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);

        reminderTimeText.setText(format.format(datetime.getTime()));
    }

    public void startSetup(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }
}

