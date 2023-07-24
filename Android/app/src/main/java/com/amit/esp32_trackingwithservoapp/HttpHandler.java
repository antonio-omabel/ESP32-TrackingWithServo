package com.amit.esp32_trackingwithservoapp;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHandler{

    private final String TAG = "Http Handler";
    public OkHttpClient client = null;
    public String url = null;
    public Context context = null;

    public HttpHandler(Context context){
        client = new OkHttpClient();
        this.context = context;
    }
    public void httpRequest(String data){
        Log.i(TAG, "RotateFunction. Url: "+url+data);
        Request request = new Request.Builder()
                .url(url+data)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i(TAG,"Http request failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i(TAG,"Http request onResponse success");
                    }
                else{
                    Log.i(TAG,"Http request onResponse fail");}
            }
        });
    }
}
