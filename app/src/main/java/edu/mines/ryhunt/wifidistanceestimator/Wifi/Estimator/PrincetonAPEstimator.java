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

public class PrincetonAPEstimator extends APEstimator {
    private static final String TARGET_SSID = "puvisitor";
    private static final Map<String /* BSSID */, Point3D /* Location */> _aps = new HashMap<>();

    private static final @DrawableRes
    int _floorplan = R.drawable.basement;
    private static final Point _originOffset = new Point(437, 150);
    private static final float _mScale = 12.2222f;

    // Setup AP Locations
    static {
        //_aps.put("a8:bd:27:ef:77:01", new Point3D(0, 0, 3.5f));
        _aps.put("a8:bd:27:ef:7d:81", new Point3D(0, 4.5f, 3.5f));
        _aps.put("a8:bd:27:ef:82:01", new Point3D(7f, 4.5f, 3.5f));
        _aps.put("a8:bd:27:ef:8a:41", new Point3D(6f, -4f, 3.5f));
        _aps.put("a8:bd:27:ef:49:c1", new Point3D(12f, -4f, 3.5f));
        //_aps.put("a8:bd:27:ef:5c:61", new Point3D(18f, -10f, 3.5f));
        _aps.put("a8:bd:27:ef:47:81", new Point3D(13f, 4.5f, 3.5f));
        _aps.put("a8:bd:27:ef:91:e1", new Point3D(-2f, -4f, 3.5f));
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
        return _floorplan;
    }

    @Override
    public Point getOriginOffset() {
        return _originOffset;
    }

    @Override
    public float getMScale() {
        return _mScale;
    }
}
