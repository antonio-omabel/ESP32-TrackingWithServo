package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

public class ManualControlActivity extends AppCompatActivity {

    final String TAG = "ManualControlActivity";
    Button bttClockwiseRotate = null, bttCounterclockwiseRotate = null;
    ImageButton bttBack=null;
    EditText etDegrees = null;
    SeekBar sbRotate = null;
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
        sbRotateHandler();
    }

    private void sbRotateHandler() {
        sbRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        httpHandler.httpRequest("CONFIG10");
                        httpHandler.httpRequest("CCW");
                        break;
                    case 1:
                        httpHandler.httpRequest("CONFIG5");

                        httpHandler.httpRequest("CCW");
                        break;
                    case 2:
                        httpHandler.httpRequest("CONFIG3");

                        httpHandler.httpRequest("CCW");
                        break;
                    case 3:
                        httpHandler.httpRequest("STOP");
                        break;
                    case 4:
                        httpHandler.httpRequest("CONFIG3");
                        httpHandler.httpRequest("CW");
                        break;
                    case 5:
                        httpHandler.httpRequest("CONFIG5");

                        httpHandler.httpRequest("CW");
                        break;
                    case 6:
                        httpHandler.httpRequest("CONFIG10");

                        httpHandler.httpRequest("CW");
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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

        sbRotate = findViewById(R.id.sbRotate);

        httpHandler = new HttpHandler(this);
        Intent intent = getIntent();
        httpHandler.url = intent.getStringExtra("URL");
        Log.i(TAG,"Url from main: " + httpHandler.url);
    }
}