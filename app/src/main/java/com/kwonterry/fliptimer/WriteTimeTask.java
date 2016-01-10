package com.kwonterry.fliptimer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kwonterry.fliptimer.data.TimeDbHelper;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Terry Kwon on 1/8/2016.
 * An AsyncTask to record stuff on database
 * because it seems like the UI is lagging when I press the start/stop button
 */
public class WriteTimeTask extends AsyncTask<Object, Void, Void> {

    private final String LOG_TAG = WriteTimeTask.class.getSimpleName();

    private TimeDbHelper mDbHelper;
    private final Context mContext;

    public WriteTimeTask(Context context) {
        mContext = context;
        mDbHelper = new TimeDbHelper(mContext);
    }

    @Override
    protected Void doInBackground(Object... params) {
        if (params.length == 0) {
            return null;
        }

        // Inserts time, status, + interval
        String timeString = (String) params[0];
        long time = StringToMillis(timeString);

        int status = (int) params[1];

        mDbHelper.insertData(time, status);

        return null;
    }

    // Converts String to Milliseconds
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
