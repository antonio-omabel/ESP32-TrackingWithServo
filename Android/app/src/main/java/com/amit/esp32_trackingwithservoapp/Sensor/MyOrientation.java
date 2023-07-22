package com.amit.esp32_trackingwithservoapp.Sensor;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.amit.esp32_trackingwithservoapp.HttpHandler;
import com.amit.esp32_trackingwithservoapp.Interfaces.IMyOrientation;


public class MyOrientation implements SensorEventListener {

    private final String TAG = "MyOrientation";

    private SensorManager sensorManager = null;
    private Sensor orientation = null;

    private IMyOrientation iMyOrientation = null;


    public MyOrientation(Context context, IMyOrientation iMyOrientation) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            this.iMyOrientation = iMyOrientation;
        }
    }

    public void start() {
        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "onSensorChanged");
        double x = sensorEvent.values[0];

        iMyOrientation.onNewOrientationValuesAvailable(x);
        Log.i(TAG, "Values\n" + x);




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i(TAG,"onAccuracyChanged");
    }
}