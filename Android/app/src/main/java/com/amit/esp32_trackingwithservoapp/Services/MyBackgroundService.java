package com.amit.esp32_trackingwithservoapp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyBackgroundService extends Service {


    private final String TAG= "MyBackgroundService";

    public static boolean isRunning= false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}//la useremo quando faremo bounded

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"Background service-onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Background service-onStartCommand");
        isRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background service-onDestroy");
        isRunning = false;
    }
}