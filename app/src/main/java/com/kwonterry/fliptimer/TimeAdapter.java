package com.kwonterry.fliptimer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kwonterry.fliptimer.data.TimeContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Terry Kwon on 1/1/2016.
 */
public class TimeAdapter extends CursorAdapter{

    private final String mWorking = "STARTED";
    private final String mNotWorking = "STOPPED";

    public TimeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_time, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTime = (TextView) view.findViewById(R.id.list_item_time_textview);
        TextView tvStatus = (TextView) view.findViewById(R.id.list_item_status_textview);
        TextView tvInterval = (TextView) view.findViewById(R.id.list_item_interval_textview);

        Long timeSeconds = cursor.getLong(
                    cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_TIME));
        String time = Time.secondsToTime(timeSeconds);

        int status = cursor.getInt(
                cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_STATUS));

        long intervalSeconds = cursor.getLong(
                cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_WORKTIME));

        if (status == 1) {
            tvStatus.setText(mWorking);
            tvStatus.setTextColor(Color.parseColor("#00cc66"));
            tvInterval.setText("Stop Interval: " + Time.formatInterval(intervalSeconds));
        } else {
            tvStatus.setText(mNotWorking);
            tvStatus.setTextColor(Color.parseColor("#ff0000"));
            tvInterval.setText("Work Interval: " + Time.formatInterval(intervalSeconds));
        }

        tvTime.setText(time);


    }


}
