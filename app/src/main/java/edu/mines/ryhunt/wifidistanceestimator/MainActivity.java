package edu.mines.ryhunt.wifidistanceestimator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import edu.mines.ryhunt.wifidistanceestimator.Wifi.ScanManager;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private final FeedFragment _feedFragment = new FeedFragment();
    private final DebugFragment _debugFragment = new DebugFragment();
    private ScanManager _scanner = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = findViewById(R.id.activityMain_viewPager);
        pager.setAdapter(new WifiFragmentPagerAdapter(getSupportFragmentManager()));

        // Acquire Mandatory Permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request User's Permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Request User's Permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        runScan();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScan();
    }

    private void runScan() {
        // Execute Scan
        _scanner = new ScanManager();
        _scanner.registerListener(_debugFragment);
        _scanner.registerListener(_feedFragment);
        _scanner.startScanning(this);
    }

    private void stopScan() {
        if (_scanner != null)
            _scanner.stopScanning(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    // Crash the Application if Not Acquired
                    Toast.makeText(this, R.string.permissions_required,
                            Toast.LENGTH_LONG).show();
                    System.exit(0);
                }
            }
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    // Crash the Application if Not Acquired
                    Toast.makeText(this, R.string.permissions_required,
                            Toast.LENGTH_LONG).show();
                    System.exit(0);
                }
            }
        }
    }

    private class WifiFragmentPagerAdapter extends FragmentPagerAdapter {
        private final String[] _titles = {"Live Feed", "Debug"};

        WifiFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) return MainActivity.this._feedFragment;
            else return MainActivity.this._debugFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return _titles[position];
        }
    }
}
