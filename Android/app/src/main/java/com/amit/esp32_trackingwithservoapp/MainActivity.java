package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyAccelerometer;
import com.amit.esp32_trackingwithservoapp.Interfaces.IMyGyroscope;
import com.amit.esp32_trackingwithservoapp.Sensor.MyAccelerometer;
import com.amit.esp32_trackingwithservoapp.Sensor.MyGyroscope;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements IMyAccelerometer, IMyGyroscope {
    private String TAG = "MainActivity";
    private Button bttRotateClockwise=null, bttRotateCounterclockwise=null,bttStart = null, bttStop = null;;
    private TextView tvX = null, tvY = null, tvZ = null;
    private OkHttpClient client;
    private String clockwiseUrl = "http://192.168.43.147/H";
    private String counterclockwiseUrl = "http://192.168.43.147/L";
    private MyAccelerometer myAccelerometer = null;
    private MyGyroscope myGyroscope = null;
    public MainActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        bttRotateClockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Clockwise rotation");
            Request request = new Request.Builder()
                    .url(clockwiseUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {Log.i(TAG,"Success");}
                }
            });
        });
        bttRotateCounterclockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Clockwise rotation");
            Request request = new Request.Builder()
                    .url(counterclockwiseUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {Log.i(TAG,"Success");}
                }
            });
        });
        /*myAccelerometer = new MyAccelerometer(this, this);
        bttStart.setOnClickListener((v)->{
            myAccelerometer.start();
        });

        bttStop.setOnClickListener((v)->{
            myAccelerometer.stop();
        });*/
        myGyroscope = new MyGyroscope(this, this);
        bttStart.setOnClickListener((v)->{
            myGyroscope.start();
        });

        bttStop.setOnClickListener((v)->{
            myGyroscope.stop();
        });
    }

    private void Init(){
        bttRotateClockwise=findViewById(R.id.bttRotateClockwise);
        bttRotateCounterclockwise=findViewById(R.id.bttRotateCounterclockwise);
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);
        client = new OkHttpClient();
        tvX = findViewById(R.id.tvX);
        tvY = findViewById(R.id.tvY);
        tvZ = findViewById(R.id.tvZ);
    }

    @Override
    public void onNewAccelerometerValuesAvaible(float x, float y, float z) {
        tvX.setText("X: " + x);
        tvY.setText("Y: " + y);
        tvZ.setText("Z: " + z);
    }

    @Override
    public void onNewGyroscopeValuesAvaible(float x, float y, float z) {
        tvX.setText("X: " + x);
        tvY.setText("Y: " + y);
        tvZ.setText("Z: " + z);
    }
}
