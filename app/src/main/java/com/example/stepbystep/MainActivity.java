package com.example.stepbystep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.health.SystemHealthManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Integer stepCount = 0;
    static final String LOG_TAG = "ServiceActivity";
    private double MagnitudePrevious = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText = (EditText) findViewById(R.id.counter);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        float x_acceleration  = intent.getFloatExtra(MyService.EXTRA_X_VALUE, 0);
                        float y_acceleration  = intent.getFloatExtra(MyService.EXTRA_Y_VALUE, 0);
                        float z_acceleration  = intent.getFloatExtra(MyService.EXTRA_Z_VALUE, 0);
                        System.out.println("x: "+x_acceleration+", y: "+y_acceleration+", z: "+z_acceleration);
                        double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                        double MagnitudeDelta = Magnitude - MagnitudePrevious;
                        MagnitudePrevious = Magnitude;

                        if (MagnitudeDelta > 5){
                            stepCount++;
                        }
                        editText.setText(stepCount+"");
                    }
                }, new IntentFilter(MyService.ACTION_SENSOR_BROADCAST)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, MyService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MyService.class));
    }
}