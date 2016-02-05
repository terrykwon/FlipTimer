package com.kwonterry.fliptimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
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

    private Notification mNotification;

    private TimeDbHelper dbHelper;

    LocalBroadcastManager broadcaster;
    Vibrator vibrator;

    // for RecordFragment
    static final public String TIME_RECORDED = "com.terrykwon.flipservice.TIME_RECORDED";

    // Broadcast intent to stop Service
    static final public String SERVICE_STOPPED = "com.terrykwon.flipservice.SERVICE_STOPPED";

    private final int WORKING = 1;
    private final int NWORKING = 0;


    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent serviceStoppedIntent = new Intent(TimerFragment.FLIPSERVICE_STOPPED);
            broadcaster.sendBroadcast(serviceStoppedIntent);
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "FlipService onCreate().");

        broadcaster = LocalBroadcastManager.getInstance(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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
        float z = event.values[2];

        //debug
        Log.v(String.valueOf(z), LOG_TAG);

        if (isFaceUp) {

            // TODO: BETTER RANGE
            if (z > -11 && z < -8.5) {
                vibrator.vibrate(500);
                Log.v(LOG_TAG, "SENSOR FACE DOWN");
                isFaceUp = false;
                recordTime(WORKING);
                sendResult();
            }
        } else {
            if (z > 8.5 && z < 11) {
                vibrator.vibrate(500);
                Log.v(LOG_TAG, "SENSOR FACE UP");
                isFaceUp = true;
                recordTime(NWORKING);
                sendResult();
            }
        }
    }

    // Gets current time, converts it to string, and records it in DB via a WriteTimeTask.
    private void recordTime(int status) {
        Time currentTime = new Time();

        long time = currentTime.getTimeInSeconds();

        dbHelper.insertData(time, status);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManager.unregisterListener(this, mSensor);
        unregisterReceiver(stopServiceReceiver);
        Log.v(LOG_TAG, "FlipService onDestroy().");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Build Notification
    public void buildNotification() {
        registerReceiver(stopServiceReceiver, new IntentFilter(SERVICE_STOPPED));
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                new Intent(SERVICE_STOPPED),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("Flip Timer Running");
        builder.setContentText("Time recorded when phone flipped face down/up.");
        builder.setSmallIcon(R.drawable.ic_alarm_white);
        builder.addAction(R.drawable.ic_stop_white_48dp, "Stop", pi);
        mNotification = builder.build();
    }



}
