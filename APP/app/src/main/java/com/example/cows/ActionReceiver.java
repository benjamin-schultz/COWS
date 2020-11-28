package com.example.cows;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.InetAddresses;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
        String ipAddress = "192.168.1.4:1250";
        String duration = "3";

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
        });

        queue.add(stringRequest);
    }

    public void snooze() {

    }
}

