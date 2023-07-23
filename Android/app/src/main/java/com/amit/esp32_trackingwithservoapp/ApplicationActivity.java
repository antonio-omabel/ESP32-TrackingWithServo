package com.amit.esp32_trackingwithservoapp;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyOrientation;
import com.amit.esp32_trackingwithservoapp.Sensor.MyOrientation;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class ApplicationActivity extends AppCompatActivity implements IMyOrientation {
    private final String TAG = "ApplicationActivity";

    private Boolean commandIssued = false;

    private Boolean isStopped = true;

    private PreviewView previewView;
    private Button bttStart = null, bttStop = null;
    private ImageButton bttBack = null;
    private TextView orientationValue = null;

    private MyOrientation myOrientation = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    private String url = null;
    private HttpHandler httpHandler = null;
    private Boolean firstLoop = true;
    private Long targetValue = null;
    private boolean clockwiseRotation = false, counterclockwiseRotation = false;


    public ApplicationActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);


        Init();
        CameraStarter();
        bttStart.setOnClickListener((v)->{
            isStopped = false;
            firstLoop = true;
            myOrientation.start();
            Log.i(TAG, "Start Orientation");
        });

        bttStop.setOnClickListener((v)->{
            isStopped = false;
            myOrientation.stop();
            httpHandler.httpRequest("STOP");
            firstLoop = true;
            Log.i(TAG, "Stop Orientation");
        });


        buttonStartHome();

    }

    private void CameraStarter() {
        //CameraX initialization
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },getExecutor());
    }

    private void buttonStartHome() {
        bttBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Home opening");
                    myOrientation.stop();
                    httpHandler.httpRequest("STOP");
                    finish();
                }
        });
    }


    private void Init(){
        Log.i(TAG, "Init");
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);
        bttBack =findViewById(R.id.bttBack);

        myOrientation = new MyOrientation(this, this);
        orientationValue = findViewById(R.id.tvOrientationValue);
        previewView = findViewById(R.id.view_finder);

        httpHandler = new HttpHandler(this);
        Intent intent = getIntent();
        httpHandler.url = intent.getStringExtra("URL");
        Log.i(TAG,"Url from main: "+httpHandler.url);
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
    public void onNewOrientationValuesAvailable(double x) {
        if (!isStopped) {


            if (firstLoop) {
                targetValue = round(x);
                firstLoop = false;
                Log.i(TAG, "The target value is: " + targetValue.toString());
            }

            //int difference = (int) (targetValue - x);

            //calcola la differenza per ruotare l'obiettivo fino a 180
            long delta = 180 - targetValue;
            // sets the new target to 180° (useless but explains the delta)
            long newTargetValue = targetValue + delta;
            long newActualValue = 0;

            if (delta >0){
            //applica la trasformazione anche al valore attuale
            if (x + delta > 360) {
                newActualValue = (long) (x + delta - 360);
            } else if (x + delta < 360) {
                newActualValue = (long) (x + delta);
            }
            }else if (delta < 0) {
                if (x + delta < 0) {
                    //se il valore ritorna nel semicerchio sx correggiamo il val negativo
                    newActualValue = (long)(360-x+delta);
                } else if (x + delta > 0){
                    newActualValue = (long)(x+delta);
                }
            }



            //controlla se il comando è partito
            if (!commandIssued) {
                if (newActualValue < 175) {
                    //se (nel nuovo sistema di rif) il valore è < 180 dobbiamo ruotare CW
                    httpHandler.httpRequest("CW");
                    commandIssued = true;
                } else if (newActualValue > 185) {
                    //se (nel nuovo sistema di rif) il valore è > 180 dobbiamo ruotare CCW
                    httpHandler.httpRequest("CCW");
                    commandIssued = true;
                } else {
                    httpHandler.httpRequest("STOP");


                }
            }

            if (newActualValue <= 185 && newActualValue >= 175) {
                httpHandler.httpRequest("STOP");
                commandIssued = false;

            }
            orientationValue.setText("Current old value: \n" + round(x) + "°" + "\nold Target value:\n" + targetValue + "°" +
                    "\nCurrent newvalue: \n" + round(newActualValue) + "°" + "\n"
                    + "\nDelta:" + round(delta) + "\n");
        }
    }
}

