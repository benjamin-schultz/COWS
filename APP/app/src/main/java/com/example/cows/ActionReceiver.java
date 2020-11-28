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
        NetworkIntentService.startWateringIntent(context);
    }

    public void snooze() {

    }
}

