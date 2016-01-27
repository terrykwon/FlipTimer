package com.kwonterry.fliptimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kwonterry.fliptimer.data.TimeDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FlipService extends Service implements SensorEventListener{

    private final String LOG_TAG = FlipService.class.getSimpleName();
    private Thread mThread;
    private Runnable mRunnable;

    private Sensor mSensor;
    private SensorManager mManager;

    // to stop Thread. No idea.
    private boolean threadContinue;

    private boolean isFaceUp;
    private WriteTimeTask mWriteTimeTask;

    private Notification mNotification;

    private TimeDbHelper dbHelper;

    LocalBroadcastManager broadcaster;
    static final public String TIME_RECORDED = "com.terrykwon.flipservice.TIME_RECORDED";

    private final int WORKING = 1;
    private final int NWORKING = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "FlipService onCreate().");

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public void sendResult() {
        Intent intent = new Intent(TIME_RECORDED);
        broadcaster.sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "FlipService onStartCommand().");

        mManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        isFaceUp = true;

        dbHelper = new TimeDbHelper(this);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                mManager.registerListener(FlipService.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                Log.v(LOG_TAG, "SensorEventListener Registered.");
            }
        };

        mThread = new Thread(mRunnable);
        mThread.start();

        buildNotification();
        startForeground(123, mNotification);

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                recordTime(WORKING);
                sendResult();
            }
        } else {
            if (event.values[2] > 0) {
                Log.v(LOG_TAG, "SENSOR FACE UP");
                isFaceUp = true;
                mWriteTimeTask = new WriteTimeTask(this);
                recordTime(NWORKING);
                sendResult();
            }
        }
    }

    // Gets current time, converts it to string, and records it in DB via a WriteTimeTask.
    private void recordTime(int status) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(calendar.getTime());

        long time = StringToMillis(timeString);
        // pretty inefficient because converting time twice.

        dbHelper.insertData(time, status);
//        mWriteTimeTask.execute(timeString, status);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManager.unregisterListener(this, mSensor);
        Log.v(LOG_TAG, "FlipService onDestroy().");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Build Notification
    public void buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("FlipService Running");
        builder.setContentText("Hi everyone, this is content text.");
        builder.setSmallIcon(R.drawable.ic_launcher);
        mNotification = builder.build();
    }

    public long StringToMillis(String stringTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        long millisTime = 0;
        try {
            Date parsedDate = formatter.parse(stringTime);
            millisTime = parsedDate.getTime();
        } catch (ParseException e) {
            Log.v(LOG_TAG, "Exception Thrown: " + e);
        }
        return millisTime;
    }

}
