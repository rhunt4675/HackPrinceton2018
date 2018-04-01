package edu.mines.ryhunt.wifidistanceestimator.Wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import edu.mines.ryhunt.wifidistanceestimator.R;
import edu.mines.ryhunt.wifidistanceestimator.Wifi.Estimator.APEstimator;

public class ScanResultListAdapter extends ArrayAdapter<ScanResult> {
    private APEstimator _estimator = null;

    public ScanResultListAdapter(Context context, ScanResult[] results, APEstimator estimator) {
        super(context, -1, results);
        _estimator = estimator;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Inflate New View (if necessary)
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.scan_item, null);
        }

        ScanResult result = getItem(position);
        TextView bssid = convertView.findViewById(R.id.scanItem_bssid);
        TextView rssi = convertView.findViewById(R.id.scanItem_rssi);
        TextView distance = convertView.findViewById(R.id.scanItem_distance);

        bssid.setText(result.BSSID);
        rssi.setText(getContext().getString(R.string.rssi_units, result.level));
        distance.setText(getContext().getString(R.string.distance_units,
                _estimator.estimateDistance(result.level)));
        return convertView;
    }
}