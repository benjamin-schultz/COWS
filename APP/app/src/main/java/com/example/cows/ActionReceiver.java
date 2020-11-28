package com.example.cows;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.InetAddresses;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra(Constants.NOTIFICATION_ACTION_NAME);
        if (action.equals(Constants.NOTIFICATION_ACTION_WATER)) {
            water(context);
        } else if (action.equals(Constants.NOTIFICATION_ACTION_SNOOZE)) {
            snooze();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICATION_MANAGER_ID);

        Intent clearIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(clearIntent);
    }

    public void water(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        String ipAddress = sharedPreferences.getString(Constants.PREF_IP, Constants.HTTP_DEFAULT_IP);
        String duration = sharedPreferences.getString(Constants.PREF_DURATION, Constants.DURATION_DEFAULT);

        String username = BuildConfig.COWS_USERNAME;
        String password = BuildConfig.COWS_PASSWORD;

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Constants.HTTP_PREFIX + ipAddress + ":" + Constants.HTTP_PORT + "/" + Constants.HTTP_WATER + duration;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, response, Toast.LENGTH_SHORT);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT);
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

    public void snooze() {

    }
}

