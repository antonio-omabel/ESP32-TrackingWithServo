package com.amit.esp32_trackingwithservoapp.Interfaces;

    public interface IMyAccelerometer {
        public void onNewAccelerometerValuesAvaible(float x, float y, float z);
}
