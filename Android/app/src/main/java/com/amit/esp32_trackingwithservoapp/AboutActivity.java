package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class AboutActivity extends AppCompatActivity {

    private final String TAG = "AboutActivity";
    private ImageButton bttBack = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Init();
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
        bttBack = findViewById(R.id.bttBack);
    }


}