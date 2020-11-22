package com.example.cows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.InetAddresses;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.text.Format;

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
        newIp = ipFirstThree + ".250";
        ipText.setText(newIp);

        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        String gateway = Formatter.formatIpAddress(dhcpInfo.gateway);
        gatewayText.setText(gateway);


    }

    public void onLogIn(View view) {
        SharedPreferences sharedPref = SetupActivity.this.getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.ip_address), newIp);
        editor.apply();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.5.20/"));
        startActivity(browserIntent);
    }

    public void onWifiSettings(View view) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}