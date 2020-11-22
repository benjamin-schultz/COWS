package com.example.cows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void startSetup(View view) {
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
    }
}