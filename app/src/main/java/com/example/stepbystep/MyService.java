package com.example.stepbystep;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class MyService extends Service {

    public static final String
            ACTION_SENSOR_BROADCAST = MyService.class.getName() + "sensorBroadcast",
            EXTRA_X_VALUE = "extra_x_value",
            EXTRA_Y_VALUE = "extra_y_value",
            EXTRA_Z_VALUE = "extra_z_value";

    private static final int
            MIN_TIME = 2000,
            MIN_DISTANCE = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                sendBroadcastMessage(sensorEvent);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void sendBroadcastMessage(SensorEvent sensorEvent) {
        if (sensorEvent != null) {
            Intent intent = new Intent(ACTION_SENSOR_BROADCAST);
            intent.putExtra(EXTRA_X_VALUE, sensorEvent.values[0]);
            intent.putExtra(EXTRA_Y_VALUE, sensorEvent.values[1]);
            intent.putExtra(EXTRA_Z_VALUE, sensorEvent.values[2]);
            System.out.println("x: " + sensorEvent.values[0] + ", y: " + sensorEvent.values[1] + ", z: " + sensorEvent.values[2]);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}