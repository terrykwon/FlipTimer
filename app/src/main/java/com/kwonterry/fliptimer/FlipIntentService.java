package com.kwonterry.fliptimer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Terry Kwon on 1/10/2016.
 */
public class FlipIntentService extends IntentService implements SensorEventListener{

    private final String LOG_TAG = FlipIntentService.class.getSimpleName();
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private boolean isFaceUp;

    private WriteTimeTask mWriteTimeTask;

    public FlipIntentService() {
        super("FlipIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.v(LOG_TAG, "Service started.");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        isFaceUp = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        if (isFaceUp) {
            if (event.values[2] < 0) {
                Log.v(LOG_TAG, "SENSOR FACE DOWN");
                isFaceUp = false;
                mWriteTimeTask = new WriteTimeTask(this);
                recordTime(1);
            }
        } else {
            if (event.values[2] > 0) {
                Log.v(LOG_TAG, "SENSOR FACE UP");
                isFaceUp = true;
                mWriteTimeTask = new WriteTimeTask(this);
                recordTime(0);
                showNotification();
            }
        }
    }

    // Gets current time, converts it to string, and records it in DB via a WriteTimeTask.
    private void recordTime(int status) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(calendar.getTime());
        mWriteTimeTask.execute(timeString, status);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("FlipIntentService Running");
        builder.setContentText("Hi everyone, this is content text.");
        builder.setSmallIcon(R.drawable.ic_launcher);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(8, notification);
    }


}