package edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator;

import android.graphics.Point;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class APEstimator {
    private static final double GAMMA = 3.3;
    private static final double SIGNAL_STRENGTH = -45;
    private static final float ALPHA = 0.8f;
    private float[] _position = new float[2];

    public abstract void sortAndFilter(List<ScanResult> results);

    public abstract Map<String, Point3D> getAPs();

    public abstract @DrawableRes
    int getFloorplan();

    public abstract Point getOriginOffset();

    public abstract float getMScale();

    public PointF estimatePosition(List<ScanResult> results) {
        // Sort Copy of APs
        List<ScanResult> myResults = new ArrayList<>(results);
        sortAndFilter(myResults);

        // Lookup Known APs
        List<Point3D> points = new ArrayList<>();
        List<Float> distances = new ArrayList<>();
        Map<String, Point3D> knownHosts = getAPs();
        for (ScanResult result : myResults) {

            // Credit: Dr. Han - Check Only Strong Signals (>=70dBm)
            if (knownHosts.containsKey(result.BSSID) && result.level >= -70) {
                points.add(knownHosts.get(result.BSSID));
                distances.add(estimateDistance(result.level));

                // Stop with 3 Known APs
                if (points.size() >= 3 && distances.size() >= 3)
                    break;
            }
        }

        // Trilateration Failed
        if (points.size() < 3 || distances.size() < 3)
            return null;

        // Make Points Accessible
        Point3D a = points.get(0), b = points.get(1), c = points.get(2);
        float A = distances.get(0), B = distances.get(1), C = distances.get(2);

        /*// Perform Trilateration
        Vector3D ex = (b.minus(a)).scale(1.f/b.minus(a).norm());
        float i = ex.dot(c.minus(a));
        Vector3D ey = (c.minus(a).minus(ex.scale(i))).scale(1.f/c.minus(a).minus(ex.scale(i)).norm());
        Vector3D ez = ex.cross(ey);
        float d = b.minus(a).norm();
        float j = ey.dot(c.minus(a));

        // Choose (x,y,z) Values
        float x = (float) (Math.pow(A, 2) - Math.pow(B, 2) + Math.pow(d, 2)) / (2*d);
        float y = (float) ((Math.pow(A, 2) - Math.pow(C, 2) + Math.pow(i, 2) + Math.pow(j, 2))/(2*j)) - ((i/j)*x);
        float z = (float) Math.sqrt(((Math.pow(A, 2) - Math.pow(x, 2) - Math.pow(y, 2))));
        return new Point3D(x, y, z);*/

        float x = (float) (Math.pow(c.getX(), 2) - Math.pow(a.getX(), 2) + Math.pow(c.getY(), 2) - Math.pow(a.getY(), 2) + Math.pow(A, 2) - Math.pow(C, 2));
        float y = (float) (Math.pow(c.getX(), 2) - Math.pow(b.getX(), 2) + Math.pow(c.getY(), 2) - Math.pow(b.getY(), 2) + Math.pow(B, 2) - Math.pow(C, 2));
        float m_a = 2 * (c.getX() - a.getX()), m_b = 2 * (c.getY() - a.getY()), m_c = 2 * (c.getX() - b.getX()), m_d = 2 * (c.getY() - b.getY());

        // Apply Matrix Multiplication
        float scale = m_a * m_d - m_b * m_c;
        x = (m_d * x - m_b * y) / scale;
        y = (-m_c * x + m_a * y) / scale;

        // Low-Pass Filter Signal
        _position[0] = ALPHA * _position[0] + (1 - ALPHA) * x;
        _position[1] = ALPHA * _position[1] + (1 - ALPHA) * y;

        return new PointF(_position[0], _position[1]);
    }

    public float estimateDistance(double rssi) {
        return (float) Math.pow(10, ((rssi - SIGNAL_STRENGTH) / (-10 * GAMMA)));
    }
}
