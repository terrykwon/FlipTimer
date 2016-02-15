package com.kwonterry.fliptimer;

import android.app.Notification;
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
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kwonterry.fliptimer.data.TimeDbHelper;


/**
 * A foreground Service that monitors device's accelerometer.
 * When device is flipped face up / down, records
 *  1) the current time in database,
 *  2) whether face up (not working) or face down (working)
 * in database.
 *
 * Uses a background thread with a runnable.
 * Sends a BroadcastIntent to notify RecordFragment that new time has been recorded.
 * Uses a Wakelock so time can be recorded while screen is off.
 */
public class FlipService extends Service implements SensorEventListener{

    private final String LOG_TAG = FlipService.class.getSimpleName();
    private Thread mThread;
    private Runnable mRunnable;

    private Sensor mSensor;
    private SensorManager mManager;

    // to stop Thread.
    private volatile boolean threadContinue = true;

    private boolean isFaceUp;

    private Notification mNotification;

    private TimeDbHelper dbHelper;

    LocalBroadcastManager notifyBroadcaster;
    Vibrator vibrator;

    // BroadCastIntent for RecordFragment refresh new data.
    static final public String TIME_RECORDED = "com.kwonterry.flipservice.TIME_RECORDED";

    // BroadcastIntent for notification to stop Service
    static final public String SERVICE_STOPPED = "com.kwonterry.flipservice.SERVICE_STOPPED";

    private final int WORKING = 1;
    private final int NWORKING = 0;

    private PowerManager.WakeLock mWakeLock;
    static final String WAKELOCK_TAG = "com.kwonterry.flipservice.WAKELOG_TAG";

    private static boolean isRunning = false;


    // Detects when stop action is pressed from notification.
    // Sends a BroadCastIntent to TimerFragment to notify that service stopped.
    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent serviceStoppedIntent = new Intent(TimerFragment.FLIPSERVICE_STOPPED);
            notifyBroadcaster.sendBroadcast(serviceStoppedIntent);
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "FlipService onCreate().");

        notifyBroadcaster = LocalBroadcastManager.getInstance(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                WAKELOCK_TAG);
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();
    }

    // Sends time. RecordFragment receives it.
    public void sendResult() {
        Intent intent = new Intent(TIME_RECORDED);
        notifyBroadcaster.sendBroadcast(intent);
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

        isRunning = true;
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
        //Log.v(String.valueOf(z), LOG_TAG);

        if (isFaceUp) {
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

    // Gets current time, converts it to string, and records it in database.
    private void recordTime(int status) {

        Time currentTime = new Time();
        long time = currentTime.getTimeInSeconds();

        dbHelper.insertData(time, status);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadContinue = false;

        mManager.unregisterListener(this, mSensor);
        mWakeLock.release();
        unregisterReceiver(stopServiceReceiver);
        dbHelper.close();

        isRunning = false;

        Log.v(LOG_TAG, "FlipService onDestroy().");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // A notification with an icon, title, description, and stop button.
    public void buildNotification() {

        // Register local BroadcastReceiver.
        registerReceiver(stopServiceReceiver, new IntentFilter(SERVICE_STOPPED));

        // A PendingIntent sent to notify FlipService that stop action has been called.
        PendingIntent serviceStopIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(SERVICE_STOPPED),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent launchActivityIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("Flip Timer Running");
        builder.setContentText("Time recorded when phone flipped face down/up.");
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.addAction(R.drawable.ic_stop_white_48dp, "Stop", serviceStopIntent);
        builder.setContentIntent(launchActivityIntent);
        mNotification = builder.build();
    }

    public static boolean IsRunning() {
        return isRunning;
    }



}
