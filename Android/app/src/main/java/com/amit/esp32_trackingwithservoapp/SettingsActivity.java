package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amit.esp32_trackingwithservoapp.Interfaces.ISettings;

public class SettingsActivity extends AppCompatActivity implements ISettings {

    private final String TAG = "SettingsActivity";
    private EditText etIP = null,etIPpart1 = null, etIPpart2=null,etIPpart3=null,etIPpart4=null, etDegrees=null;

    public HttpHandler httpHandler = null;
    private Button bttSetIP = null, bttGo = null, bttHome = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Init();
        bttSetIP.setOnClickListener((v) -> {
            Log.i(TAG, "New IP Setted");
            httpHandler.url=getIP();
        });

        bttGo.setOnClickListener((v)->{
            Log.i(TAG, "User Defined Rotation");
            httpHandler.httpRequest(etDegrees.getText().toString());
        });

        buttonStartHome();
    }

    private void buttonStartHome() {
        bttHome.setOnClickListener((v)->{
            Log.i(TAG, "Home opening. Url: " + httpHandler.url);
            Intent intent = getIntent();
            intent.putExtra("URL", httpHandler.url);
            finish();
        });
    }

    public void Init(){
        etIPpart1 =  findViewById(R.id.etIPPart1);
        etIPpart2 =  findViewById(R.id.etIPPart2);
        etIPpart3 =  findViewById(R.id.etIPPart3);
        etIPpart4 =  findViewById(R.id.etIPPart4);
        etDegrees = findViewById(R.id.etDegrees);

        bttSetIP = findViewById(R.id.bttSetIP);
        bttGo = findViewById(R.id.bttGo);
        bttHome = findViewById(R.id.bttHome);

        httpHandler = new HttpHandler();
        httpHandler.url=getIP();
        Log.i(TAG, "Getting IP");

    }

    private String getIP () {
        String iP = "http://" + etIPpart1.getText().toString() + "." + etIPpart2.getText().toString() + "." + etIPpart3.getText().toString() + "." + etIPpart4.getText().toString() + "/get?data=";
        return iP;
    }

    @Override
    public void onNewUrl(String newUrl) {
        newUrl=getIP();
    }
}