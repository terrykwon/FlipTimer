package com.kwonterry.fliptimer;

import android.content.Context;
import android.database.Cursor;
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

    private final String mWorking = "START";
    private final String mNotWorking = "STOP";

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

        Long timeMillis = cursor.getLong(
                    cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_TIME));
        String time = millisToString(timeMillis);

        int status = cursor.getInt(
                    cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_STATUS));

        if (status == 1) {
            view.setBackgroundColor(Color.parseColor("#cdff99"));
            tvStatus.setText(mWorking);
        } else {
            view.setBackgroundColor(Color.parseColor("#ffcccc"));
            tvStatus.setText(mNotWorking);
        }

        tvTime.setText(time);

        long intervalMillis = cursor.getLong(
                cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_WORKTIME));
        tvInterval.setText(millisIntervalToString(intervalMillis));
    }

    public String millisToString(long millis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(millis);
        return timeFormat.format(date);
    }

    public String millisIntervalToString(long millis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        Date date = new Date(millis);
        return timeFormat.format(millis);
    }

}
