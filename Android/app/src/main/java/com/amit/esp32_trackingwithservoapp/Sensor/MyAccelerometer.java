package com.amit.esp32_trackingwithservoapp.Sensor;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyAccelerometer;

public class MyAccelerometer implements SensorEventListener {

    private final String TAG = "MyAccelerometer";

    private SensorManager sensorManager = null;
    private Sensor accelerometer = null;

    private IMyAccelerometer iMyAccelerometer = null;

    public MyAccelerometer (Context context, IMyAccelerometer iMyAccelerometer) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            this.iMyAccelerometer=iMyAccelerometer;
        }
    }
    public void start(){
        sensorManager.registerListener(this/*Usa se stessa come listener*/, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "onSensorChanged");

        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        iMyAccelerometer.onNewAccelerometerValuesAvaible(x,y,z);
        Log.i(TAG, "\nX: " + x + "\nY: " + y + "\nZ: " + z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i(TAG,"onAccuracyChanged");
    }
}
