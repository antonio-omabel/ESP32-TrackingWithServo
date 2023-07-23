package com.amit.esp32_trackingwithservoapp;

import static java.lang.Math.round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyOrientation;
import com.amit.esp32_trackingwithservoapp.Sensor.MyOrientation;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.Nullable;

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
    private ImageButton bttBack = null, bttShutter = null;
    private TextView orientationValue = null;

    private MyOrientation myOrientation = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    private String url = null;
    private HttpHandler httpHandler = null;
    private Boolean firstLoop = true;
    private Long targetValue = null;
    private int skipValue = 5;
    private VideoCapture videoCapture;


    public ApplicationActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);


        Init();
        CameraStarter();
        bttStart.setOnClickListener((v) -> {
            isStopped = false;
            firstLoop = true;
            myOrientation.start();
            Log.i(TAG, "Start Orientation");
        });

        bttStop.setOnClickListener((v) -> {
            isStopped = false;
            myOrientation.stop();
            httpHandler.httpRequest("STOP");
            firstLoop = true;
            Log.i(TAG, "Stop Orientation");
            videoCapture.stopRecording();
        });

        bttShutter.setOnClickListener((v) -> {
            try{
            recordVideo();
            Toast.makeText(ApplicationActivity.this, "Recording video.", Toast.LENGTH_SHORT).show();}
            catch (Exception e){
                Toast.makeText(ApplicationActivity.this,"Recording video failed.", Toast.LENGTH_SHORT).show();
            }
        });


        buttonStartHome();

    }

    @SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {

            long timestamp = System.currentTimeMillis();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

            try {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                videoCapture.startRecording(
                        new VideoCapture.OutputFileOptions.Builder(
                                getContentResolver(),
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                        ).build(),
                        getExecutor(),
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                                Toast.makeText(ApplicationActivity.this, "Video has been saved successfully.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                Toast.makeText(ApplicationActivity.this, "Error saving video: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

        }}

    private void CameraStarter() {
        //CameraX initialization
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());
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


    private void Init() {
        Log.i(TAG, "Init");
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);
        bttBack = findViewById(R.id.bttBack);

        myOrientation = new MyOrientation(this, this);
        orientationValue = findViewById(R.id.tvOrientationValue);
        previewView = findViewById(R.id.view_finder);

        httpHandler = new HttpHandler(this);
        Intent intent = getIntent();
        httpHandler.url = intent.getStringExtra("URL");
        Log.i(TAG, "Url from main: " + httpHandler.url);

        bttShutter = findViewById(R.id.bttShutter);

    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
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
        // Video Capture use case
        videoCapture = new VideoCapture.Builder()
                        .setVideoFrameRate(30)
                                .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture, videoCapture);

    }

    @Override
    public void onNewOrientationValuesAvailable(double x) {
        if (!isStopped) {
            if (firstLoop) {
                targetValue = round(x);
                firstLoop = false;
                Log.i(TAG, "The target value is: " + targetValue.toString());
            }
            rotationHandler(x);
        }
    }
    

    private void rotationHandler(double x){
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
                }
                else if (newActualValue > 185) {
                    //se (nel nuovo sistema di rif) il valore è > 180 dobbiamo ruotare CCW
                    httpHandler.httpRequest("CCW");
                    commandIssued = true;
                }
            }
            if (newActualValue <= 185 && newActualValue >= 175) {
                httpHandler.httpRequest("STOP");
                commandIssued = false;

            }
            orientationValue.setText("Current value:\n" + round(x) + "°\n" +
                    "Target value:\n" + targetValue + "°\n" + "Difference:\n" +
                    (targetValue-round(x)));
        }
    }
