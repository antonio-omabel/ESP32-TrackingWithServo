package com.example.amit.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.amit.Interfaces.IMyOrientationSensor;

public class MyOrientationSensor implements SensorEventListener {

    private final String TAG = "MyOrientationSensor";

    private SensorManager sensorManager = null;
    private Sensor orientationSensor = null;

    private IMyOrientationSensor iMyOrientationSensor = null;

    public MyOrientationSensor(Context context, IMyOrientationSensor iMyOrientationSensor) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            this.iMyOrientationSensor = iMyOrientationSensor;
        }
    }

    public void start() {
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "onSensorChanged");
        double value = sensorEvent.values[1];

        iMyOrientationSensor.onNewOrientationValueAvailable(value);
        Log.i(TAG, "Value: " + value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i(TAG,"onAccuracyChanged");
    }
}