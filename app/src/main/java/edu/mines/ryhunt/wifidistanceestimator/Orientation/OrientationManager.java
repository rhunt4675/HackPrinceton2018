package edu.mines.ryhunt.wifidistanceestimator.Orientation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashSet;
import java.util.Set;

public class OrientationManager implements SensorEventListener {
    private static Set<OrientationManagerListener> _listeners = new HashSet<>();
    private final float[] _accelerometerReading = new float[3];
    private final float[] _magnetometerReading = new float[3];
    private final float[] _rotationMatrix = new float[9];
    private final float[] _orientationAngles = new float[3];
    private SensorManager _sensorManager;

    public void startPolling(Context context) {
        if (_sensorManager == null)
            _sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        _sensorManager.registerListener(this,
                _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        _sensorManager.registerListener(this,
                _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopPolling(Context context) {
        _sensorManager.unregisterListener(this);
    }

    public void registerListener(OrientationManagerListener listener) {
        _listeners.add(listener);
    }

    public void unregisterListener(OrientationManagerListener listener) {
        _listeners.remove(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, _accelerometerReading,
                    0, _accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, _magnetometerReading,
                    0, _magnetometerReading.length);
        }

        // Notify Listeners of Updates
        updateOrientationAngles();
        for (OrientationManagerListener listener : _listeners)
            listener.onOrientationUpdated(_rotationMatrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void updateOrientationAngles() {
        SensorManager.getRotationMatrix(_rotationMatrix, null,
                _accelerometerReading, _magnetometerReading);
        SensorManager.getOrientation(_rotationMatrix, _orientationAngles);
    }

    public interface OrientationManagerListener {
        void onOrientationUpdated(float[] orientation);
    }
}
