package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class ManualControlActivity extends AppCompatActivity {

    final String TAG = "ManualControlActivity";
    Button bttClockwiseRotate = null, bttCounterclockwiseRotate = null;
    EditText etDegrees = null;
    HttpHandler httpHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);
        Init();

        bttClockwiseRotate.setOnClickListener(v -> {
            httpHandler.httpRequest(etDegrees.getText().toString());
            Log.i(TAG,"Clockwise rotation");
        });

        bttCounterclockwiseRotate.setOnClickListener(v -> {
            httpHandler.httpRequest("-" + etDegrees.getText().toString());
            Log.i(TAG,"Clockwise rotation");
        });
    }
    private void Init() {
        bttClockwiseRotate = findViewById(R.id.bttClockwiseRotate);
        bttCounterclockwiseRotate = findViewById(R.id.bttCounterclockwiseRotate);
        etDegrees = findViewById(R.id.etDegrees);

        httpHandler = new HttpHandler(this);
        Intent intent = getIntent();
        httpHandler.url = intent.getStringExtra("URL");
        Log.i(TAG,"Url from main: " + httpHandler.url);
    }
}