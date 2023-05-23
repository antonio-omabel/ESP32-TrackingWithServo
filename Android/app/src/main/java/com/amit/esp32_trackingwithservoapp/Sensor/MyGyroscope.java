package com.amit.esp32_trackingwithservoapp.Sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyGyroscope;

public class MyGyroscope  implements SensorEventListener {

        private final String TAG = "MyGyroscope";

        private SensorManager sensorManager = null;
        private Sensor gyroscope = null;

        private IMyGyroscope iMyGyroscope = null;

        public MyGyroscope (Context context, IMyGyroscope iMyGyroscope) {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null){
                gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                this.iMyGyroscope=iMyGyroscope;
            }
        }
        public void start(){
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
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

            iMyGyroscope.onNewGyroscopeValuesAvaible(x,y,z);
            Log.i(TAG, "\nX: " + x + "\nY: " + y + "\nZ: " + z);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            Log.i(TAG,"onAccuracyChanged");
        }
}
