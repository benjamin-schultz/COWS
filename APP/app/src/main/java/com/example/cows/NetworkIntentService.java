package com.example.cows;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkIntentService extends IntentService {

    private static final String ACTION_GET_PUBLIC_IP = "com.example.cows.action.GET_PUBLIC_IP";
    private static final String ACTION_COMPARE_IP = "com.example.cows.action.COMPARE_IP";

    private static final String EXTRA_PUBLIC_IP = "com.example.cows.extra.PUBLIC_IP";

    public NetworkIntentService() {
        super("NetworkIntentService");
    }

    public static void startWateringIntent(Context context) {
        Intent intent = new Intent(context, NetworkIntentService.class);
        intent.setAction(ACTION_GET_PUBLIC_IP);
        context.startService(intent);
    }

    public static void startCompareIP(Context context, String publicIP) {
        Intent intent = new Intent(context, NetworkIntentService.class);
        intent.setAction(ACTION_COMPARE_IP);
        intent.putExtra(EXTRA_PUBLIC_IP, publicIP);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_PUBLIC_IP.equals(action)) {
                getPublicIP();
            } else if (ACTION_COMPARE_IP.equals(action)) {
                final String publicIP = intent.getStringExtra(EXTRA_PUBLIC_IP);
                try {
                    compareIPs(publicIP);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getPublicIP() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.ipify.org/";
        Log.d("DEBUG", "getPublicIP");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DEBUG", "startCompareIP");
                        startCompareIP(NetworkIntentService.this, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private void compareIPs(String publicIP) throws UnknownHostException {
        Log.d("DEBUG", "compareIPs");
        InetAddress COWSInetAddress = InetAddress.getByName(Constants.HTTP_COWS_HOSTNAME);
        String COWSAddress = COWSInetAddress.getHostAddress();
        String address = Constants.HTTP_COWS_HOSTNAME;
        if (COWSAddress.equals(publicIP)) {
            SharedPreferences sharedPreferences = NetworkIntentService.this.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
            address = sharedPreferences.getString(Constants.PREF_IP, Constants.HTTP_DEFAULT_IP);
        }

        sendCOWS(address);
    }

    private void sendCOWS(String address) {
        Log.d("DEBUG", "sendCOWS");
        SharedPreferences sharedPreferences = NetworkIntentService.this.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        String duration = sharedPreferences.getString(Constants.PREF_DURATION, Constants.DURATION_DEFAULT);

        RequestQueue queue = Volley.newRequestQueue(this);

        String username = BuildConfig.COWS_USERNAME;
        String password = BuildConfig.COWS_PASSWORD;

        String url = Constants.HTTP_PREFIX + address + ":" + Constants.HTTP_PORT + "/" + Constants.HTTP_WATER + duration;

        Log.d("DEBUG", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showToast(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void showToast(final String toast) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}