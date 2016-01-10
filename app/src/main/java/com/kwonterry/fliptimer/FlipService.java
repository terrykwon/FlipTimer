package com.kwonterry.fliptimer;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Terry Kwon on 1/10/2016.
 */
public class FlipService extends IntentService implements SensorEventListener{

    private final String LOG_TAG = FlipService.class.getSimpleName();
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private boolean isFaceUp;

    private WriteTimeTask mWriteTimeTask;

    public FlipService() {
        super("FlipService");
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
        } else if (!isFaceUp) {
            if (event.values[2] > 0) {
                Log.v(LOG_TAG, "SENSOR FACE UP");
                isFaceUp = true;
                mWriteTimeTask = new WriteTimeTask(this);
                recordTime(0);
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
}