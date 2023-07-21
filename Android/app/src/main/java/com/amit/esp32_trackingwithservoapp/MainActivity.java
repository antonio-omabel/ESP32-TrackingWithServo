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

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amit.esp32_trackingwithservoapp.Interfaces.IMyOrientation;
import com.amit.esp32_trackingwithservoapp.Sensor.MyOrientation;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements IMyOrientation{
    private String TAG = "MainActivity";

    private PreviewView previewView;
    private Button bttRotateClockwise=null, bttRotateCounterclockwise=null,bttStart = null,
            bttStop = null, bttGO=null, bttSlowSpeed = null,bttNormalSpeed = null, bttFastSpeed=null;
    private TextView orientationValue = null;
    private EditText etIP = null,etIPpart1 = null, etIPpart2=null,etIPpart3=null,etIPpart4=null, etDegrees=null;

 
    /*private MyAccelerometer myAccelerometer = null;*/
    private MyOrientation myOrientation = null;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private String url = null;
    private HttpHandler httpHandler= new HttpHandler();
    private Boolean firstLoop = true;
    private Long firstValue = null;


    public MainActivity() throws MalformedURLException, UnsupportedEncodingException {
    }

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

        bttGO.setOnClickListener((v) -> {
            Log.i(TAG, "User Defined Rotation");
            //TODO: Change Button to "SetIP"
            url = getIP();
            httpHandler.url=url;
            //httpHandler = new HttpHandler(url);
            httpHandler.httpRequest(etDegrees.getText().toString());
        });

        bttRotateClockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Clockwise rotation");
            httpHandler.httpRequest("90");
        });
        bttRotateCounterclockwise.setOnClickListener((v) -> {
            Log.i(TAG, "Counterclockwise rotation");
            httpHandler.httpRequest("-90");
        });
        bttSlowSpeed.setOnClickListener((v) -> {
            Log.i(TAG, "Change servo speed to Slow");
            httpHandler.httpRequest("CONFIG3");
        });
        bttNormalSpeed.setOnClickListener((v) -> {
            Log.i(TAG, "Change servo speed to Normal");
            httpHandler.httpRequest("CONFIG5");
        });
        bttFastSpeed.setOnClickListener((v) -> {
            Log.i(TAG, "Change servo speed to Fast");
            httpHandler.httpRequest("CONFIG10");
        });

        bttStart.setOnClickListener((v)->{
            myOrientation.start();
            Log.i(TAG, "Start Orientation");
        });

        bttStop.setOnClickListener((v)->{
            myOrientation.stop();
            Log.i(TAG, "Stop Orientation");
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
        etIP =  findViewById(R.id.etIP);
        etIPpart1 =  findViewById(R.id.etIPPart1);
        etIPpart2 =  findViewById(R.id.etIPPart2);
        etIPpart3 =  findViewById(R.id.etIPPart3);
        etIPpart4 =  findViewById(R.id.etIPPart4);
        etDegrees = findViewById(R.id.etDegrees);
        url = getIP();
        httpHandler.url=url;

        bttRotateClockwise=findViewById(R.id.bttRotateClockwise);
        bttRotateCounterclockwise=findViewById(R.id.bttRotateCounterclockwise);
        bttStart = findViewById(R.id.bttStart);
        bttStop = findViewById(R.id.bttStop);


        myOrientation = new MyOrientation(this, this);
        orientationValue = findViewById(R.id.tvHorizontalValue);

        previewView = findViewById(R.id.view_finder);




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



    //Reads IP from the 4 edit text in the menu and returns it as a string
    public String getIP () {
        String iP = "http://" + etIPpart1.getText().toString() + "." + etIPpart2.getText().toString() + "." + etIPpart3.getText().toString() + "." + etIPpart4.getText().toString() + "/get?data=";
        return iP;
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
                httpHandler.httpRequest("-1");
                Log.i(TAG, "Counterclockwise positional adjustament");
            } else {
                httpHandler.httpRequest("1");
                Log.i(TAG, "Clockwise positional adjustament");
              }
            }
        else if (firstValue - x < -4) {
                if ((firstValue - x < -180)) {
                    httpHandler.httpRequest("1");
                    Log.i(TAG, "Clockwise positional adjustament");
                }
                else {
                    httpHandler.httpRequest("-1");
                    Log.i(TAG, "Counterclockwise positional adjustament");
                }
            }
        orientationValue.setText("Values:\n" + round(x) + "\n" + round(y) + "\n" + round(z)+"\n"+firstValue);
        }

    }

