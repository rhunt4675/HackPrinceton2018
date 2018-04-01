package edu.mines.ryhunt.wifidistanceestimator.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScanManager {
    private static WifiManager _wifiManager;
    private static Runnable _runScan = new Runnable() {
        @Override
        public void run() {
            _wifiManager.startScan();
        }
    };
    private List<ScanManagerListener> _listeners = new ArrayList<>();
    private ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean _scanning = false;
    private BroadcastReceiver _scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            for (ScanManagerListener listener : _listeners)
                listener.onScanResults(_wifiManager.getScanResults());

            // Schedule Future Scan
            if (_scanning) _scheduler.schedule(_runScan, 0, TimeUnit.MILLISECONDS);
        }
    };

    public ScanManager registerListener(@NonNull ScanManagerListener listener) {
        _listeners.add(listener);
        return this;
    }

    public ScanManager unregisterListener(@NonNull ScanManagerListener listener) {
        for (int i = 0; i < _listeners.size(); i++) {
            if (_listeners.get(i) == listener) {
                _listeners.remove(i);
                i--;
            }
        }
        return this;
    }

    public void startScanning(Context context) {
        // Register Scan Listener
        _wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(_scanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // Initiate Scan
        _scheduler.schedule(_runScan, 0, TimeUnit.MILLISECONDS);
        _scanning = true;
    }

    public void stopScanning(Context context) {
        // Stop Scanning
        _scanning = false;

        // Unregister Receiver
        context.unregisterReceiver(_scanReceiver);
    }

    public interface ScanManagerListener {
        void onScanResults(List<ScanResult> scanResults);
    }
}
