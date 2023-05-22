package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private Button bttRotateClockwise=null, bttRotateCounterclockwise=null;
    URL url = null;

    public MainActivity() throws MalformedURLException {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        bttRotateClockwise.setOnClickListener((v)->{
            Log.i(TAG, "Clockwise rotation");
            try {
                url = new URL("http://stackoverflow.com");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //readStream(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                urlConnection.disconnect();
            }
        });
        bttRotateCounterclockwise.setOnClickListener((v)->{
            Log.i(TAG, "Counterclockwise rotation");
            try {
                url = new URL("192.168.43.147/L");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                //readStream(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                urlConnection.disconnect();
            }
        });
    }

    private void Init(){
        bttRotateClockwise=findViewById(R.id.bttRotateClockwise);
        bttRotateCounterclockwise=findViewById(R.id.bttRotateCounterclockwise);

    }

}