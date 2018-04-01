package edu.mines.ryhunt.wifidistanceestimator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.APEstimator;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.PrincetonAPEstimator;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.ScanManager;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.ScanResultListAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final APEstimator _estimator = new PrincetonAPEstimator();

    private ScanManager _scanner = null;
    private boolean _scanning = false;
    private ListView _scanList;
    private TextView _positionEstimate;
    private MapView _mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _scanList = findViewById(R.id.activityMain_scanResultListView);
        _positionEstimate = findViewById(R.id.activityMain_positionEstimate);
        _mapView = findViewById(R.id.activityMain_floorplan);
        _mapView.setImageResource(R.drawable.basement);
        _mapView.setupMap(_estimator.getOriginOffset(), _estimator.getMScale(),
                new ArrayList<>(_estimator.getAPs().values()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        runScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    runScan();
            }
        }
    }

    private void runScan() {
        // Verify Permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request User's Permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }

        // Execute Scan
        _scanner = new ScanManager().registerListener(new ScanManager.ScanManagerListener() {
            @Override
            public void onScanResults(List<ScanResult> scanResults) {

                // Sort and Filter Results
                _estimator.sortAndFilter(scanResults);

                // Display Results in ListView
                _scanList.setAdapter(new ScanResultListAdapter(MainActivity.this,
                        scanResults.toArray(new ScanResult[scanResults.size()]), _estimator));

                // Estimate Location
                PointF estimate = _estimator.estimatePosition(scanResults);
                _positionEstimate.setText(estimate == null ? "N/A" : estimate.toString());
                _mapView.setUserLocation(estimate);
            }
        });
        _scanner.startScanning(MainActivity.this);
    }

    private void stopScan() {
        if (_scanner != null)
            _scanner.stopScanning(MainActivity.this);
    }
}
