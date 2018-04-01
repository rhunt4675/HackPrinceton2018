package edu.mines.ryhunt.wifidistanceestimator;

import android.graphics.PointF;
import android.hardware.Camera;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import edu.mines.ryhunt.wifidistanceestimator.Camera.CameraFeed;
import edu.mines.ryhunt.wifidistanceestimator.Orientation.OrientationManager;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.APEstimator;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.PrincetonAPEstimator;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.ScanManager;

public class FeedFragment extends Fragment
        implements ScanManager.ScanManagerListener, OrientationManager.OrientationManagerListener {
    private static final APEstimator _estimator = new PrincetonAPEstimator();
    private static final OrientationManager _orientationManager = new OrientationManager();

    private CameraFeed _cameraFeed;
    private ImageView _arrow;
    private Camera _camera;
    private MapView _mapView;

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.feed_fragment, container, false);

        _camera = getCameraInstance();
        if (_camera != null) {
            _cameraFeed = new CameraFeed(getActivity(), _camera);
            FrameLayout preview = v.findViewById(R.id.feedFragment_cameraPreview);
            preview.addView(_cameraFeed);
        }

        _arrow = v.findViewById(R.id.feedFragment_arrow);
        _mapView = v.findViewById(R.id.feedFragment_minimap);
        _mapView.setupMap(_estimator.getOriginOffset(), _estimator.getMScale(),
                new ArrayList<>(_estimator.getAPs().values()));

        _orientationManager.registerListener(this);
        return v;
    }

    @Override
    public void onScanResults(List<ScanResult> scanResults) {
        // Skip Scan Results before Attached
        if (getActivity() == null) return;

        // Sort and Filter Results
        _estimator.sortAndFilter(scanResults);

        // Show Position on Minimap
        PointF estimate = _estimator.estimatePosition(scanResults);
        _mapView.setUserLocation(estimate);
    }

    @Override
    public void onOrientationUpdated(float[] orientation) {
        _arrow.setRotation((orientation[2] + 90.f) * (orientation[1] > 0 ? 90.f : -90.f) + (orientation[1] > 0 ? 0 : 180.f));
        _arrow.setRotationY(-60.f);
    }

    @Override
    public void onStart() {
        super.onStart();
        _orientationManager.startPolling(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (_camera != null) {
            _camera.release();
            _camera = null;
        }

        _orientationManager.stopPolling(getActivity());
    }
}
