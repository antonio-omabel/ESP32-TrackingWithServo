package com.amit.esp32_trackingwithservoapp.Interfaces;

public interface IMyGyroscope {
    public void onNewGyroscopeValuesAvaible(float x, float y, float z);
}
