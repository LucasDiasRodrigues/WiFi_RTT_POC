package com.everis.wifirtttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.LinkedList;
import java.util.List;

public class WifiConfig {
    private class ScanWifiNetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final List<ScanResult> wifiNetworks = wifiManager.getScanResults();
            final List<ScanResult> rttSupportedNetworks = new LinkedList<>();
            for (ScanResult network : wifiNetworks) {
                // Check if Wifi network supports ranging
                if (network.is80211mcResponder()) {
                    rttSupportedNetworks.add(network);
                }
            }
            // TODO Show networks in a list, pic a specific one, ...
        }
    }

    private ScanWifiNetworkReceiver wifiNetworkReceiver = new ScanWifiNetworkReceiver();

    public void start(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiNetworkReceiver, filter);
        wifiManager.startScan();
    }

    public void stop(Context context) {
        context.unregisterReceiver(wifiNetworkReceiver);
    }



}
