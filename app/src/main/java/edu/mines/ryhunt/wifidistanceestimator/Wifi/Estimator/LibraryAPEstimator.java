package edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator;

import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.support.annotation.DrawableRes;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mines.ryhunt.wifidistanceestimator.R;

public class LibraryAPEstimator extends APEstimator {
    private static final String TARGET_SSID = "puvisitor";
    private static final Map<String /* BSSID */, Point3D /* Location */> _aps = new HashMap<>();

    // Setup AP Locations
    static {
        _aps.put("a8:bd:27:ef:7f:a1", new Point3D(0, 0, 3.65f));
        _aps.put("a8:bd:27:f0:13:81", new Point3D(7f, 0.33f, 3.65f));
        _aps.put("a8:bd:27:ef:7c:e1", new Point3D(12f, -4f, 3.65f));
        _aps.put("a8:bd:27:ef:73:e1", new Point3D(-0.66f, -5f, 3.65f));
        _aps.put("a8:bd:27:ef:9d:81", new Point3D(14f, -6.33f, 3.65f));
    }

    @Override
    public void sortAndFilter(List<ScanResult> results) {

        // Sort by Signal Strength
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult r1, ScanResult r2) {
                return Integer.compare(r2.level, r1.level);
            }
        });

        // Filter by SSID
        for (int i = 0; i < results.size(); i++) {
            if (!results.get(i).SSID.equals(TARGET_SSID)) {
                results.remove(i);
                i--;
            }
        }

        // Filter by Frequency
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).frequency > 3000) {
                results.remove(i);
                i--;
            }
        }
    }

    @Override
    public Map<String, Point3D> getAPs() {
        return _aps;
    }

    @Override
    public @DrawableRes
    int getFloorplan() {
        return R.drawable.basement;
    }

    @Override
    public Point getOriginOffset() {
        return new Point(110, 135);
    }

    @Override
    public float getMScale() {
        return 12.222222222f;
    }
}
