package edu.mines.ryhunt.wifidistanceestimator;

import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.APEstimator;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.PrincetonAPEstimator;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.ScanManager;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.ScanResultListAdapter;


public class DebugFragment extends Fragment implements ScanManager.ScanManagerListener {
    private static final APEstimator _estimator = new PrincetonAPEstimator();
    private ListView _scanList;
    private TextView _positionEstimate;
    private MapView _mapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.debug_fragment, container, false);

        _mapView = v.findViewById(R.id.debugFragment_floorplan);
        _mapView.setupMap(_estimator.getOriginOffset(), _estimator.getMScale(),
                new ArrayList<>(_estimator.getAPs().values()));

        _scanList = v.findViewById(R.id.debugFragment_scanResultListView);
        _positionEstimate = v.findViewById(R.id.debugFragment_positionEstimate);
        return v;
    }

    @Override
    public void onScanResults(List<ScanResult> scanResults) {
        // Skip Scan Results before Attached
        if (getActivity() == null) return;

        // Sort and Filter Results
        _estimator.sortAndFilter(scanResults);

        // Display Results in ListView
        _scanList.setAdapter(new ScanResultListAdapter(getActivity(),
                scanResults.toArray(new ScanResult[scanResults.size()]), _estimator));

        // Estimate Location
        PointF estimate = _estimator.estimatePosition(scanResults);
        _positionEstimate.setText(estimate == null ? "N/A" : estimate.toString());
        _mapView.setUserLocation(estimate);
    }
}
