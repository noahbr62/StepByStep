package com.example.stepbystep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String COUNTER_STATE = "stepCount";

    private static final String CHANNEL_ID = "defaultChannel";
    private static final String CHANNEL_NAME = "Default Channel";
    private NotificationManager notificationManager;

    private Integer stepCount = 0;
    private Integer setStepCount = 0;
    private double MagnitudePrevious = 0;

    AnimationDrawable animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText editText = (EditText) findViewById(R.id.counter);
        ImageView loading = (ImageView) findViewById(R.id.imageView);
        animation = (AnimationDrawable) loading.getDrawable();
        this.notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        float x_acceleration = intent.getFloatExtra(MyService.EXTRA_X_VALUE, 0);
                        float y_acceleration = intent.getFloatExtra(MyService.EXTRA_Y_VALUE, 0);
                        float z_acceleration = intent.getFloatExtra(MyService.EXTRA_Z_VALUE, 0);
                        double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                        double MagnitudeDelta = Magnitude - MagnitudePrevious;
                        MagnitudePrevious = Magnitude;

                        if (MagnitudeDelta > 5) {
                            animation.start();
                            stepCount++;
                            if (stepCount - 100 > setStepCount) {
                                setStepCount = stepCount;
                                sendNotification();
                            }
                        }
                        editText.setText(stepCount + "");
                    }
                }, new IntentFilter(MyService.ACTION_SENSOR_BROADCAST)
        );
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("You moved a lot")
                .setContentText("you moved around " + stepCount + " steps")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(0, builder.build());
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
        savedInstanceState.putInt("stepCount", stepCount);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stepCount = savedInstanceState.getInt("stepCount");
    }
}