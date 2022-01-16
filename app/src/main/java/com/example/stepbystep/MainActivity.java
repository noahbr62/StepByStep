package com.example.stepbystep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.health.SystemHealthManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Observer;

public class MainActivity extends AppCompatActivity {

    private static final String COUNTER_STATE = "stepCount";
    private Integer stepCount = 0;
    private double MagnitudePrevious = 0;
    AnimationDrawable animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText = (EditText) findViewById(R.id.counter);
        ImageView loading = (ImageView) findViewById(R.id.imageView);
        animation = (AnimationDrawable) loading.getDrawable();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        float x_acceleration  = intent.getFloatExtra(MyService.EXTRA_X_VALUE, 0);
                        float y_acceleration  = intent.getFloatExtra(MyService.EXTRA_Y_VALUE, 0);
                        float z_acceleration  = intent.getFloatExtra(MyService.EXTRA_Z_VALUE, 0);
                        double Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
                        double MagnitudeDelta = Magnitude - MagnitudePrevious;
                        MagnitudePrevious = Magnitude;

                        if (MagnitudeDelta > 5){
                            stepCount++;
                            animation.start();
                        } else {
                            animation.stop();
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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("stepCount", this.stepCount);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.stepCount = savedInstanceState.getInt("stepCount");
    }
}