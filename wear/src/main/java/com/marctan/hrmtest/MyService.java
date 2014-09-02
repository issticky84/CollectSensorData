package com.marctan.hrmtest;

/**
 * Created by ec037 on 2014/9/1.
 */
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service implements SensorEventListener {
    private static final String TAG = "MyService";
    private SensorManager mSensorManager;
    private Sensor linear_accelerometer, gravity, gyroscope, acceleration;
    SensorEventListener listen;
    float old_acc_x,old_acc_y,old_acc_z;
    float old_lacc_x,old_lacc_y,old_lacc_z;
    float old_grav_x,old_grav_y,old_grav_z;
    float old_gyr_x,old_gyr_y,old_gyr_z;
    private FileWriter writer;
    private Timer timer;
    private SensorEvent sensorEvent_global;
    private boolean A=false,B=false,C=false,D=false;

    @Override
    public void onCreate() {
        Log.d("LINCYU", "Service: onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service: onStartCommand");

        //listen = new SensorListen();
        open_file();

        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        linear_accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        acceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, this.linear_accelerometer, 3);
        mSensorManager.registerListener(this, this.gravity, 3);
        mSensorManager.registerListener(this, this.gyroscope, 3);
        mSensorManager.registerListener(this, this.acceleration, 3);

        Thread t = new Thread() {
            @Override
            public void run() {
                /*
                for (int i = 0; i < 1000; i++) {
                    for (int j = 0; j < 10000000; j++);
                    Log.v("LINCYU", "Hello " + i);
                }*/

                timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    public void run(){
                        if(A&&B&&C&&D) {
                            CollectSensorData();
                        }
                        //System.out.println("haha");
                    }
                },0,500);

            }

        };
        // t.run();
        t.start();

        return START_STICKY_COMPATIBILITY;
    }

    public void open_file()
    {
        Log.d(TAG,"open file");

        // File management
        File root = Environment.getExternalStorageDirectory();
        File gpxfile = new File(root, "mydata.csv");

        try {
            writer = new FileWriter(gpxfile);
            //write header
            String line = "ACCELERATION X,ACCELERATION Y,ACCELERATION Z,LINEAR_ACCELERATION X,LINEAR_ACCELERATION Y,LINEAR_ACCELERATION Z," +
                          "GRAVITY X,GRAVITY Y,GRAVITY Z,GYROSCOPE X,GYROSCOPE Y,GYROSCOPE Z,Date\n";
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCsvData3(float d, float e, float f) throws IOException {
        String line = String.format("%f,%f,%f,", d, e, f);
        writer.write(line);
    }

    public void writeTime()
    {
        //write time
        Calendar c;
        c = Calendar.getInstance();
        try {
            String line =  c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " "
                    +  c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + ":" + c.get(Calendar.MILLISECOND);
            writer.write(line);
            writeLineFeed();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeLineFeed()  {
        try{
            String line = String.format("\n");
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("LINCYU", "Service: onDestory");

        //end_flag = true;

        timer.cancel();

        //close file
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //mSensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent i) {
        Log.d("LINCYU", "Service: onBind");
        Binder binder = new Binder();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent i) {
        Log.d("LINCYU", "Service: onUnbind");
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sensorEvent_global = sensorEvent;

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            A = true;
            old_acc_x = sensorEvent_global.values[0];
            old_acc_y = sensorEvent_global.values[1];
            old_acc_z = sensorEvent_global.values[2];
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            B = true;
            old_lacc_x = sensorEvent_global.values[0];
            old_lacc_y = sensorEvent_global.values[1];
            old_lacc_z = sensorEvent_global.values[2];
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            C = true;
            old_grav_x = sensorEvent_global.values[0];
            old_grav_y = sensorEvent_global.values[1];
            old_grav_z = sensorEvent_global.values[2];
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            D = true;
            old_gyr_x = sensorEvent_global.values[0];
            old_gyr_y = sensorEvent_global.values[1];
            old_gyr_z = sensorEvent_global.values[2];
        }
    }

    public void CollectSensorData() {

        Log.d(TAG,"CollectSensorData");

            if (sensorEvent_global.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //Log.d(TAG, "sensor event: " + sensorEvent_global.accuracy + " = " + sensorEvent_global.values[0]);

                try {
                    writeCsvData3(sensorEvent_global.values[0], sensorEvent_global.values[1], sensorEvent_global.values[2]);
                    writeCsvData3(old_lacc_x, old_lacc_y, old_lacc_z);
                    writeCsvData3(old_grav_x,old_grav_y,old_grav_z);
                    writeCsvData3(old_gyr_x, old_gyr_y, old_gyr_z);
                    writeTime();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                old_acc_x = sensorEvent_global.values[0];
                old_acc_y = sensorEvent_global.values[1];
                old_acc_z = sensorEvent_global.values[2];
            }
            if (sensorEvent_global.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                //Log.d(TAG, "sensor event: " + sensorEvent_global.accuracy + " = " + sensorEvent_global.values[0]);

                try {
                    writeCsvData3(old_acc_x, old_acc_y, old_acc_z);
                    writeCsvData3(sensorEvent_global.values[0], sensorEvent_global.values[1], sensorEvent_global.values[2]);
                    writeCsvData3(old_grav_x,old_grav_y,old_grav_z);
                    writeCsvData3(old_gyr_x, old_gyr_y, old_gyr_z);
                    writeTime();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                old_acc_x = sensorEvent_global.values[0];
                old_acc_y = sensorEvent_global.values[1];
                old_acc_z = sensorEvent_global.values[2];
            }
            if (sensorEvent_global.sensor.getType() == Sensor.TYPE_GRAVITY) {
                try {
                    writeCsvData3(old_acc_x, old_acc_y, old_acc_z);
                    writeCsvData3(old_lacc_x, old_lacc_y, old_lacc_z);
                    writeCsvData3(sensorEvent_global.values[0], sensorEvent_global.values[1], sensorEvent_global.values[2]);
                    writeCsvData3(old_gyr_x, old_gyr_y, old_gyr_z);
                    writeTime();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                old_grav_x = sensorEvent_global.values[0];
                old_grav_y = sensorEvent_global.values[1];
                old_grav_z = sensorEvent_global.values[2];
            }
            if (sensorEvent_global.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                try {
                    writeCsvData3(old_acc_x, old_acc_y, old_acc_z);
                    writeCsvData3(old_lacc_x, old_lacc_y, old_lacc_z);
                    writeCsvData3(old_grav_x,old_grav_y,old_grav_z);
                    writeCsvData3(sensorEvent_global.values[0], sensorEvent_global.values[1], sensorEvent_global.values[2]);
                    writeTime();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                old_gyr_x = sensorEvent_global.values[0];
                old_gyr_y = sensorEvent_global.values[1];
                old_gyr_z = sensorEvent_global.values[2];
            }


    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}
