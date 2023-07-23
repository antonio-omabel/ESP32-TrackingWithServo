package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private final String TAG = "AboutActivity";
    private ImageButton bttBack = null;
    private TextView tvGitHubLink=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Init();
        buttonStartHome();
    }

    private void buttonStartHome() {
        setupHyperlink();
        bttBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Home opening");
                finish();
            }
        });
    }
    private void setupHyperlink() {
        tvGitHubLink = findViewById(R.id.tvGitHubLink);
        tvGitHubLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void Init() {
        bttBack = findViewById(R.id.bttBack);
    }


}