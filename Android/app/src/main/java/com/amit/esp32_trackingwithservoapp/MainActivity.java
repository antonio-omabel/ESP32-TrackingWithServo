package com.amit.esp32_trackingwithservoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private EditText etIP = null,etIPpart1 = null, etIPpart2=null,etIPpart3=null,etIPpart4=null;
    private TextView tvSpeed = null;
    public HttpHandler httpHandler = null;
    private Button bttSetIP = null, bttCheckIP = null, bttHome = null, bttManual = null, bttAbout = null;

    private SeekBar sbSpeed = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");
        Init();
        bttSetIP.setOnClickListener(v -> {
            try{
                httpHandler.url = getIP();
                Log.i(TAG, "IP set:" + httpHandler.url);
                Toast.makeText(this, "IP set.", Toast.LENGTH_SHORT).show();}
            catch(Exception e){
                Log.i(TAG, "IP not set:" + httpHandler.url);
                Toast.makeText(this, "IP not set.", Toast.LENGTH_SHORT).show();
            }
        });

        bttCheckIP.setOnClickListener(v -> {
            try{
                httpHandler.httpRequest("TEST");
                Log.i(TAG, "Connection test sent");
                Toast.makeText(this, "Connection test sent.", Toast.LENGTH_SHORT).show();}
            catch (Exception e){
                Log.i(TAG, "Connection test not sent");
                Toast.makeText(this, "Connection test not sent.", Toast.LENGTH_SHORT).show();
            }
        });

        sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        httpHandler.httpRequest("CONFIG3");
                        tvSpeed.setText("Slow");
                        break;
                    case 1:
                        httpHandler.httpRequest("CONFIG5");
                        tvSpeed.setText("Normal");
                        break;
                    case 2:
                        httpHandler.httpRequest("CONFIG10");
                        tvSpeed.setText("Fast");
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

        buttonStartApplication();
        buttonStartManual();
        buttonStartAbout();
    }

    private void buttonStartAbout() {
        bttAbout.setOnClickListener(v -> {
            Log.i(TAG, "Manual control opening. Url: " + httpHandler.url);
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void buttonStartManual() {
        bttManual.setOnClickListener(v -> {
            Log.i(TAG, "Manual control opening. Url: " + httpHandler.url);
            Intent intent = new Intent(MainActivity.this, ManualControlActivity.class);
            intent.putExtra("URL", httpHandler.url);
            startActivity(intent);
        });
    }

    private void buttonStartApplication() {
        bttHome.setOnClickListener(v -> {
            Log.i(TAG, "Home opening. Url: " + httpHandler.url);
            Intent intent = new Intent(MainActivity.this, ApplicationActivity.class);
            intent.putExtra("URL", httpHandler.url);
            startActivity(intent);
        });
    }

    public void Init(){
        etIPpart1 =  findViewById(R.id.etIPPart1);
        etIPpart2 =  findViewById(R.id.etIPPart2);
        etIPpart3 =  findViewById(R.id.etIPPart3);
        etIPpart4 =  findViewById(R.id.etIPPart4);

        tvSpeed = findViewById(R.id.tvSpeed);

        bttCheckIP = findViewById(R.id.bttCheckIP);
        bttSetIP = findViewById(R.id.bttSetIP);

        sbSpeed = findViewById(R.id.sbSpeed);

        bttHome = findViewById(R.id.bttHome);
        bttManual = findViewById(R.id.bttManual);
        bttAbout = findViewById(R.id.bttAbout);

        httpHandler = new HttpHandler(this);
        httpHandler.url=getIP();
        Log.i(TAG, "Getting IP");

    }

    private String getIP () {
        String iP = "http://" + etIPpart1.getText().toString() + "." + etIPpart2.getText().toString() + "." + etIPpart3.getText().toString() + "." + etIPpart4.getText().toString() + "/get?data=";
        return iP;
    }
}