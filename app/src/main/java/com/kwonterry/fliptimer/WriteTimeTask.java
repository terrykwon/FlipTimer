package com.kwonterry.fliptimer;

import android.content.Context;
import android.os.AsyncTask;

import com.kwonterry.fliptimer.data.TimeDbHelper;

/**
 * Created by Terry Kwon on 1/8/2016.
 * An AsyncTask to record stuff on database
 * because it seems like the UI is lagging when I press the start/stop button
 */
public class WriteTimeTask extends AsyncTask<Object, Void, Void> {

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

        long time = (long) params[0];
        int status = (int) params[1];

        mDbHelper.insertData(time, status);


        return null;
    }
}
