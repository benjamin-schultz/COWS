package com.example.cows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;


public class SetupActivity extends AppCompatActivity {

    String newIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        final TextView ipText = (TextView) findViewById(R.id.staticIPText);
        final TextView gatewayText = (TextView) findViewById(R.id.staticGatewayText);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ipAddress = Formatter.formatIpAddress(info.getIpAddress());
        String ipFirstThree = ipAddress.substring(0, ipAddress.lastIndexOf("."));
        newIp = ipFirstThree + Constants.HTTP_IP_OCT;
        ipText.setText(newIp);

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        String gateway = Formatter.formatIpAddress(dhcpInfo.gateway);
        gatewayText.setText(gateway);
    }

    public void onLogIn(View view) {
        SharedPreferences sharedPref = SetupActivity.this.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PREF_IP, newIp);
        editor.apply();

        Intent intent = new Intent(this, LoginWebViewActivity.class);
        startActivity(intent);
    }

    public void onWifiSettings(View view) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}