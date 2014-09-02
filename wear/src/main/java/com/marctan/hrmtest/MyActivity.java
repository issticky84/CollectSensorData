/*
 * Copyright (C) 2014 Marc Lester Tan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marctan.hrmtest;

import android.app.Activity;
import android.content.ComponentName;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class MyActivity extends Activity {

    private static final String TAG = MyActivity.class.getName();

    private TextView rate;
    private TextView accuracy;
    private TextView sensorInformation;
    private static final int SENSOR_TYPE_HEARTRATE = 65562;
    private Sensor mHeartRateSensor;

    private CountDownLatch latch;

    ServiceConnection serviceConn;
    boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        serviceConn = new MyServiceConn();
        isBound = false;
        Button btn_startS = (Button)findViewById(R.id.startS);
        Button btn_stopS = (Button)findViewById(R.id.stopS);


        latch = new CountDownLatch(1);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //rate = (TextView) stub.findViewById(R.id.rate);
                //rate.setText("Sensor Event");

                accuracy = (TextView) stub.findViewById(R.id.accuracy);
                sensorInformation = (TextView) stub.findViewById(R.id.sensor);

                latch.countDown();
            }
        });


        accuracy = (TextView) stub.findViewById(R.id.accuracy);
        sensorInformation = (TextView) stub.findViewById(R.id.sensor);

        Log.d(TAG,"onCreate");

    }

    public void startS_1(View v) {
        Intent intent = new Intent();
        intent.setClass(MyActivity.this, MyService.class);
        startService(intent);
        Toast.makeText(v.getContext(), "Start collecting data", Toast.LENGTH_LONG).show();
    }

     public void stopS_l(View v) {
        Intent intent = new Intent();
        intent.setClass(MyActivity.this, MyService.class);
        stopService(intent);
        Toast.makeText(v.getContext(), "Stop collecting data", Toast.LENGTH_LONG).show();
    }


    class MyServiceConn implements ServiceConnection {
        public void onServiceConnected(ComponentName classname,
                                       IBinder service) {
            isBound = true;
            Log.d("LINCYU", "Activity: onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName classname) {
        }
    }

    /*
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");

    }


    /*
    boolean first_flag = false;
    boolean second_flag = true;
    public void timer()
    {
        Calendar c;
        c = Calendar.getInstance();
        int millsec =  c.get(Calendar.MILLISECOND);
        if(millsec<400 && first_flag==false && second_flag==true)
        {
            //CollectSensorData();
            first_flag = true;
            second_flag = false;
        }
        if(millsec>=400 && first_flag==true && second_flag==false)
        {
            //CollectSensorData();
            second_flag = true;
            first_flag = false;
        }
    }*/


    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onRestart() {
        super.onStop();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }


}
