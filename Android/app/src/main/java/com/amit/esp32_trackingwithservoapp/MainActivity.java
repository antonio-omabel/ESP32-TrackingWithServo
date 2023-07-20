package com.amit.esp32_trackingwithservoapp;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyRotationVector;
import com.amit.esp32_trackingwithservoapp.Sensor.MyRotationVector;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements IMyRotationVector {
    private String TAG = "MainActivity";

    private PreviewView previewView;
    private Button bttRotateClockwise=null, bttRotateCounterclockwise=null,bttStart = null,
            bttStop = null, bttGO=null, bttSlowSpeed = null,bttNormalSpeed = null, bttFastSpeed=null;
    private TextView tvHorizontalValue = null;
    private EditText etIP = null,etIPpart1 = null, etIPpart2=null,etIPpart3=null,etIPpart4=null, etDegrees=null;
    private OkHttpClient client;
 
    /*private MyAccelerometer myAccelerometer = null;*/
    private MyRotationVector myRotationVector = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    public MainActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

        bttGO.setOnClickListener((v) -> {
            Log.i(TAG, "User Defined Rotation");
            httpRequest(etDegrees.getText().toString());
        });

        bttRotateClockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Clockwise rotation");
            httpRequest("90");
        });
        bttRotateCounterclockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Counterclockwise rotation");
            httpRequest("-90");
        });
        bttSlowSpeed.setOnClickListener((v) -> {
            Log.i(TAG, "Change servo speed to Slow");
            httpRequest("CONFIG3");
        });
        bttNormalSpeed.setOnClickListener((v) -> {
            Log.i(TAG, "Change servo speed to Normal");
            httpRequest("CONFIG5");
        });
        bttFastSpeed.setOnClickListener((v) -> {
            Log.i(TAG, "Change servo speed to Fast");
            httpRequest("CONFIG10");
        });

        bttStart.setOnClickListener((v)->{
            myRotationVector.start();
            Log.i(TAG, "Start Rotation Vector");
        });

        bttStop.setOnClickListener((v)->{
            myRotationVector.stop();
            Log.i(TAG, "Stop Rotation Vector");
        });
    }

    private void Init(){
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



        bttRotateClockwise=findViewById(R.id.bttRotateClockwise);
        bttRotateCounterclockwise=findViewById(R.id.bttRotateCounterclockwise);
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);

        client = new OkHttpClient();

        myRotationVector = new MyRotationVector(this, this);
        tvHorizontalValue = findViewById(R.id.tvHorizontalValue);

        previewView = findViewById(R.id.view_finder);

        etIP =  findViewById(R.id.etIP);
        etIPpart1 =  findViewById(R.id.etIPPart1);
        etIPpart2 =  findViewById(R.id.etIPPart2);
        etIPpart3 =  findViewById(R.id.etIPPart3);
        etIPpart4 =  findViewById(R.id.etIPPart4);
        etDegrees = findViewById(R.id.etDegrees);


        bttGO = findViewById(R.id.bttGo);
        bttSlowSpeed = findViewById(R.id.bttSlowSpeed);
        bttNormalSpeed = findViewById(R.id.bttNormalSpeed);
        bttFastSpeed = findViewById(R.id.bttFastSpeed);
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

    private void httpRequest(String data){
        //TODO: fix error "http request fail when rotation works (happens with 900+° degrees rotation)
        //TODO: move in separate class
        String url = getIP() + data;
        Log.i(TAG, "RotateFunction");
        Request request = new Request.Builder()
                .url(url)
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
    
    //Reads IP from the 4 edit text in the menu and returns it as a string
    private String getIP (){
        String iP = "http://" + etIPpart1.getText().toString() + "." + etIPpart2.getText().toString() + "." + etIPpart3.getText().toString() + "." + etIPpart4.getText().toString() + "/get?data=";
        return iP;
    }

    @Override
    public void onNewRotationVectorValuesAvailable(double x) {
        tvHorizontalValue.setText("Horizontal value: \n" + x);
    }
}
