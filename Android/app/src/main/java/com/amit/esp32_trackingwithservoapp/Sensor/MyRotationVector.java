package com.amit.esp32_trackingwithservoapp.Sensor;

import static java.lang.Math.round;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyRotationVector;

public class MyRotationVector implements SensorEventListener {

    private final String TAG = "MyRotationVector";

    private SensorManager sensorManager = null;
    private Sensor rotationVector = null;

    private IMyRotationVector iMyRotationVector = null;

    public MyRotationVector(Context context, IMyRotationVector iMyRotationVector) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            this.iMyRotationVector = iMyRotationVector;
        }
    }

    public void start() {
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "onSensorChanged");
        double horizontalValue = sensorEvent.values[0];

        /////////////////////////////Sistemare////////////////////////
        iMyRotationVector.onNewRotationVectorValuesAvaible(horizontalValue);
        Log.i(TAG, "Horizontal value: " + horizontalValue);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i(TAG,"onAccuracyChanged");
    }
}