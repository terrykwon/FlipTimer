package com.kwonterry.fliptimer;

import android.os.Handler;
import android.widget.TextClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Terry Kwon on 12/30/2015.
 * A Timer class that returns current time & a timer that counts up from current time.
 */
public class SimpleClock {

    private final String LOG_TAG = SimpleClock.class.getSimpleName();
    private Calendar mCurrentDateTime;
    private Handler mHandler;

    private TextClock clock;

    public SimpleClock() {
        mCurrentDateTime = new GregorianCalendar();
        mHandler = new Handler();
    }

    public String getCurrentDateTime() {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss");
        return dateTimeFormat.format(mCurrentDateTime.getTime());
    }

    public String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar time = Calendar.getInstance();
        return timeFormat.format(time.getTime());
    }

    public long getTimeInMillis() {
        Calendar time = Calendar.getInstance();
        return time.getTimeInMillis();
    }

}

