package com.example.amit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amit.Interfaces.IMyOrientationSensor;
import com.example.amit.Sensors.MyOrientationSensor;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements IMyOrientationSensor {
    private final String TAG = "MainActivity";
    private MyOrientationSensor myOrientationSensor = null;
    private OkHttpClient client = null;
    private TextView tV = null;
    private Button btt = null;

    public MainActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        btt.setOnClickListener((v)->{
            myOrientationSensor.start();});
    }

    private void Init(){
        client = new OkHttpClient();
        myOrientationSensor = new MyOrientationSensor(this, this);
        tV=findViewById(R.id.TV);
        btt =findViewById(R.id.Btt);
    }

    @Override
    public void onNewOrientationValueAvailable(double x) {
        tV.setText("Value: \n" + x);
    }
}
