package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ManualControlActivity extends AppCompatActivity {

    final String TAG = "ManualControlActivity";
    Button bttClockwiseRotate = null, bttCounterclockwiseRotate = null;
    ImageButton bttBack=null;
    EditText etDegrees = null;
    HttpHandler httpHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);
        Init();

        bttClockwiseRotate.setOnClickListener(v -> {
            String degrees = etDegrees.getText().toString();
            int degreesInt=0;
            try {
                degreesInt = Integer.parseInt(degrees);
            }
            catch (NumberFormatException e) {
                Toast.makeText(this,"Invalid angle", Toast.LENGTH_SHORT).show();
            }
            if(degreesInt>=0) {
                httpHandler.httpRequest(degrees);
            }
            else{
                Log.i(TAG,"Toasting");
                Toast.makeText(this,"Invalid angle", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG,"Clockwise rotation");
        });

        bttCounterclockwiseRotate.setOnClickListener(v -> {
            String degrees = etDegrees.getText().toString();
            int degreesInt=0;
            try {
                degreesInt = Integer.parseInt(degrees);
            }
            catch (NumberFormatException e) {
                Toast.makeText(this,"Invalid angle", Toast.LENGTH_SHORT).show();
            }
            if(degreesInt>=0) {
                httpHandler.httpRequest("-" + degrees);
            }
            else{
                Log.i(TAG,"Toasting");
                Toast.makeText(this,"Invalid angle", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG,"Counterlockwise rotation");
        });

        buttonStartHome();
    }

    private void buttonStartHome() {
        bttBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Log.i(TAG, "Home opening");
               finish();
            }
        });
    }

    private void Init() {
        bttClockwiseRotate = findViewById(R.id.bttClockwiseRotate);
        bttCounterclockwiseRotate = findViewById(R.id.bttCounterclockwiseRotate);
        bttBack = findViewById(R.id.bttBack);
        etDegrees = findViewById(R.id.etDegrees);

        httpHandler = new HttpHandler(this);
        Intent intent = getIntent();
        httpHandler.url = intent.getStringExtra("URL");
        Log.i(TAG,"Url from main: " + httpHandler.url);
    }
}