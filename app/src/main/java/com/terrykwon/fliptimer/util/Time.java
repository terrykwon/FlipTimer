package com.terrykwon.fliptimer.util;

import android.content.Context;

import com.terrykwon.fliptimer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * New instance of class gets the current time.
 * Contains utility methods to format time and time intervals.
 */

public class Time {

    private GregorianCalendar currentCalendar;

    public Time() {
        currentCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
    }

    // Returns current time in seconds (since 1970.1.1)
    public long getTimeInSeconds() {
        long timeInMillis = currentCalendar.getTimeInMillis();

        return timeInMillis / 1000;
    }


    // Unused
    public String getTimeString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        return formatter.format(currentCalendar.getTime());
    }

    // Converts seconds to read-friendly String.
    public static String formatInterval(long interval, Context context) {

        if (interval < 60) {
            return String.valueOf(interval) + context.getString(R.string.seconds);
        } else if (interval < 3600){
            long minutes = interval / 60;
            long seconds = interval % 60;
            return String.valueOf(minutes) + context.getString(R.string.minutes)
                    + " " + String.valueOf(seconds) + context.getString(R.string.seconds);
        } else {
            long hours = interval / 3600;
            long minutes = (interval % 3600) / 60;
            return String.valueOf(hours) + context.getString(R.string.hours)
                    + " " + String.valueOf(minutes) + context.getString(R.string.minutes);
        }
    }

    public static String secondsToTime(long seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        return formatter.format(date);
    }
}
