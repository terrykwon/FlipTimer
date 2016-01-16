package com.kwonterry.fliptimer;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Terry Kwon on 12/30/2015.
 * A CountDownTimer using Handler & Runnable,
 * copied off StackOverflow
 */
public class Stopwatch {
    private long mMillisInFuture;
    private long mCountDownInterval;
    private final String LOG_TAG = Stopwatch.class.getSimpleName();
    private Handler mHandler;
    private Runnable mCounter;

    public Stopwatch(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountDownInterval = countDownInterval;
        mHandler = new Handler();
    }

    public void Start() {
        mCounter = new Runnable() {
            @Override
            public void run() {
                if(mMillisInFuture <= 0) {
                    Log.v(LOG_TAG, "Countdown Done");
                } else {
                    long sec = TimeUnit.MILLISECONDS.toSeconds(mMillisInFuture);
                    Log.v(LOG_TAG, Long.toString(sec));
                    mMillisInFuture -= mCountDownInterval;
                    mHandler.postDelayed(this, mCountDownInterval);
                }
            }
        };
        mHandler.postDelayed(mCounter, mCountDownInterval);
    }


    // Should not update UI thread from this thread?
    public void Start(final TextView display) {
        mCounter = new Runnable() {
            @Override
            public void run() {
                if(mMillisInFuture <= 0) {
                    Log.v(LOG_TAG, "Countdown Done");
                } else {
                    long sec = TimeUnit.MILLISECONDS.toSeconds(mMillisInFuture);
                    Log.v(LOG_TAG, Long.toString(sec));
                    mMillisInFuture -= mCountDownInterval;
                    mHandler.postDelayed(this, mCountDownInterval);

                    updateTextView(display);
                }
            }
        };
        mHandler.postDelayed(mCounter, mCountDownInterval);
    }

    public void updateTextView(TextView textView) {
        textView.setText(Long.toString(TimeUnit.MILLISECONDS.toSeconds(mMillisInFuture)));
    }

    public void Pause() {
        mHandler.removeCallbacks(mCounter);
    }

}
