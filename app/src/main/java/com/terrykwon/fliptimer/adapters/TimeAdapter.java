package com.terrykwon.fliptimer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.terrykwon.fliptimer.R;
import com.terrykwon.fliptimer.util.Time;
import com.terrykwon.fliptimer.data.TimeContract;

/**
 * A CursorAdapter that is bound the the ListView in RecordFragment.
 * Displays
 *  1) Time
 *  2) Status (started / stopped)
 *  3) Work Interval
 *
 */
public class TimeAdapter extends CursorAdapter{

    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public TimeAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_FIRST) {
            layoutId = R.layout.list_item_time_first;
        } else {
            layoutId = R.layout.list_item_time;
        }

        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FIRST : VIEW_TYPE_NORMAL;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTime = (TextView) view.findViewById(R.id.list_item_time_textview);
        TextView tvStatus = (TextView) view.findViewById(R.id.list_item_status_textview);
        TextView tvInterval = (TextView) view.findViewById(R.id.list_item_interval_textview);
        View timeContainer = view.findViewById(R.id.container_time_view);
        View statusContainer = view.findViewById(R.id.container_status_view);

        Long timeSeconds = cursor.getLong(
                    cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_TIME));
        String time = Time.secondsToTime(timeSeconds);

        int status = cursor.getInt(
                cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_STATUS));

        long intervalSeconds = cursor.getLong(
                cursor.getColumnIndexOrThrow(TimeContract.TimeEntry.COLUMN_WORKTIME));


        if (cursor.isFirst() && cursor.isLast()) {
            tvInterval.setText(R.string.session_started);
            statusContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

            if (status == 1) {
                tvStatus.setText(R.string.started);
            } else {
                tvStatus.setText(R.string.stopped);
            }

        } else if (cursor.isFirst()) {
            statusContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkGrey));
            tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));

            if (status == 1) {
                tvStatus.setText(R.string.started);
                tvInterval.setText(context.getString(R.string.stop_interval,
                        Time.formatInterval(intervalSeconds, context)));
            } else {
                tvStatus.setText(R.string.stopped);
                tvInterval.setText(context.getString(R.string.work_interval,
                        Time.formatInterval(intervalSeconds, context)));
            }

        } else if (cursor.isLast()) {
            tvInterval.setText(R.string.session_started);
            statusContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

            if (status == 1) {
                tvStatus.setText(R.string.started);
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            } else {
                tvStatus.setText(R.string.stopped);
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            }

        } else {
            statusContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey));

            if (status == 1) {
                tvStatus.setText(R.string.started);
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));

                // use Strings with placeholders instead.
                tvInterval.setText(context.getString(R.string.stop_interval,
                        Time.formatInterval(intervalSeconds, context)));
            } else {
                tvStatus.setText(R.string.stopped);
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                tvInterval.setText(context.getString(R.string.work_interval,
                        Time.formatInterval(intervalSeconds, context)));
            }
        }

        tvTime.setText(time);

    }


}
