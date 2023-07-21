package com.amit.esp32_trackingwithservoapp;

import static java.lang.Math.round;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyOrientation;
import com.amit.esp32_trackingwithservoapp.Interfaces.ISettings;
import com.amit.esp32_trackingwithservoapp.Sensor.MyOrientation;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements IMyOrientation, ISettings {
    private final String TAG = "MainActivity";

    private PreviewView previewView;
    private Button bttStart = null, bttStop = null;
    private ImageButton bttSettings = null;
    private TextView orientationValue = null;

    private MyOrientation myOrientation = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private String url = null;
    private HttpHandler httpHandler = null;
    private Boolean firstLoop = true;
    private Long firstValue = null;


    public MainActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
        bttStart.setOnClickListener((v)->{
            myOrientation.start();
            Log.i(TAG, "Start Orientation");
        });

        bttStop.setOnClickListener((v)->{
            myOrientation.stop();
            Log.i(TAG, "Stop Orientation");
        });

        buttonStartSettings();

    }

    private void buttonStartSettings() {
        bttSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.amit.esp32_trackingwithservoapp.SettingsActivity");
                    Log.i(TAG, "Settings opening");
                    startActivity(intent);
                }
            });
    }


    private void Init(){
        Log.i(TAG, "Main init");
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);
        bttSettings=findViewById(R.id.bttSettings);


        myOrientation = new MyOrientation(this, this);
        orientationValue = findViewById(R.id.tvOrientationValue);
        previewView = findViewById(R.id.view_finder);

        httpHandler = new HttpHandler();
        httpHandler.url = getString(R.string.URL);
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        //Cameraselector use case
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        //Preview Use Case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        // Image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();
        
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector,preview, imageCapture);

    }

    @Override
    public void onNewOrientationValuesAvailable(double x, double y, double z) {
        if (firstLoop){
            firstValue = round(x);
            firstLoop = false;
            Log.i(TAG,"The first value is: "+firstValue.toString());
        }
        else if((firstValue - x > 4)) {
            if ((firstValue - x > 180)) {
                httpHandler.httpRequest("-3");
                Log.i(TAG, "Counterclockwise positional adjustament");
            } else {
                httpHandler.httpRequest("3");
                Log.i(TAG, "Clockwise positional adjustament");
              }
            }
        else if (firstValue - x < -4) {
                if ((firstValue - x < -180)) {
                    httpHandler.httpRequest("3");
                    Log.i(TAG, "Clockwise positional adjustament");
                }
                else {
                    httpHandler.httpRequest("-3");
                    Log.i(TAG, "Counterclockwise positional adjustament");
                }
            }
        orientationValue.setText("Values:\n" + round(x) + "\n" + round(y) + "\n" + round(z)+"\n"+firstValue);
        }

    @Override
    public void onNewUrl(String newUrl) {
        httpHandler.url=newUrl;
    }
}

