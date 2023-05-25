package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyRotationVector;
import com.amit.esp32_trackingwithservoapp.Sensor.MyRotationVector;
import com.amit.esp32_trackingwithservoapp.Services.MyBackgroundService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements IMyRotationVector {
    private String TAG = "MainActivity";
    private Button bttRotateClockwise=null, bttRotateCounterclockwise=null,bttStart = null, bttStop = null;;
    private TextView tvHorizontalValue = null;
    private OkHttpClient client;
    private String clockwiseUrl = "http://192.168.43.147/H";
    private String counterclockwiseUrl = "http://192.168.43.147/L";
    /*private MyAccelerometer myAccelerometer = null;*/
    private MyRotationVector myRotationVector = null;
    public MainActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        bttRotateClockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Clockwise rotation");
            RotateFunction(clockwiseUrl);
        });
        bttRotateCounterclockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Counterclockwise rotation");
            RotateFunction(counterclockwiseUrl);
        });



        StartBackgroundService();
        StopBackgroundService();


    }

    private void StartBackgroundService() {
        bttStart.setOnClickListener((v)->{
            myRotationVector.start();
            Log.i(TAG, "Start background service");
            if(!MyBackgroundService.isRunning){
                startService(new Intent(this, MyBackgroundService.class));
            }
            else{Log.i(TAG, "Background service already running");}
            Log.i(TAG,"Rotation");
            RotateFunction(clockwiseUrl);
    });

    }

    private void StopBackgroundService() {
        myRotationVector.stop();
        bttStop.setOnClickListener((v)->{
            myRotationVector.stop();
            Log.i(TAG, "Stop background service");
            stopService(new Intent(this, MyBackgroundService.class));
        });
    }

    private void Init(){
        bttRotateClockwise=findViewById(R.id.bttRotateClockwise);
        bttRotateCounterclockwise=findViewById(R.id.bttRotateCounterclockwise);
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);
        client = new OkHttpClient();
        myRotationVector = new MyRotationVector(this, this);
        tvHorizontalValue = findViewById(R.id.tvHorizontalValue);
    }

    private void RotateFunction(String directionUrl){
        Log.i(TAG, "RotateFunction");
        Request request = new Request.Builder()
                .url(directionUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i(TAG,"Http request fail");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {Log.i(TAG,"Http request onResponse success");}
                else{Log.i(TAG,"Http request onResponse fail");}
            }
        });
    }

    @Override
    public void onNewRotationVectorValuesAvaible(double x) {
        tvHorizontalValue.setText("Horizontal value: " + x);
    }
}
