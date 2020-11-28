package com.example.cows;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
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

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.cows.action.FOO";
    private static final String ACTION_BAZ = "com.example.cows.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.cows.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.cows.extra.PARAM2";

    public NetworkIntentService() {
        super("NetworkIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetworkIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NetworkIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                try {
                    handleActionFoo(param1, param2);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                try {
                    handleActionBaz(param1, param2);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) throws IOException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.ipify.org/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        startActionBaz(NetworkIntentService.this, response, "");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NetworkIntentService.this, error.toString(), Toast.LENGTH_SHORT);
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
                        Toast.makeText(NetworkIntentService.this, response, Toast.LENGTH_SHORT);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NetworkIntentService.this, error.toString(), Toast.LENGTH_SHORT);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) throws UnknownHostException {
        // TODO: Handle action Baz
        compareIPs(param1);
    }
}