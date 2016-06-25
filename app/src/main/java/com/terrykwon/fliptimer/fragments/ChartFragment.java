package com.terrykwon.fliptimer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.terrykwon.fliptimer.R;
import com.terrykwon.fliptimer.util.Time;
import com.terrykwon.fliptimer.data.TimeDbHelper;

import java.util.ArrayList;


/**
 * Displays a chart using MPAndroidChart library.
 */
public class ChartFragment extends Fragment {

    private TimeChart timeChart;

    private TextView workTimeText;
    private TextView stopTimeText;
    private TextView totalTimeText;
    private TextView refreshText;

    private static final String LOG_TAG = ChartFragment.class.getSimpleName();

    private TimeDbHelper dbHelper;

    public ChartFragment() {
        // required empty public constructor
    }

    public static ChartFragment newInstance() {
        ChartFragment fragment = new ChartFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View chartView = inflater.inflate(R.layout.fragment_chart, container, false);
        workTimeText = (TextView) chartView.findViewById(R.id.text_work_time);
        stopTimeText = (TextView) chartView.findViewById(R.id.text_stop_time);
        totalTimeText = (TextView) chartView.findViewById(R.id.text_total_time);
        refreshText = (TextView) chartView.findViewById(R.id.text_refresh_time);
        FrameLayout chartContainer = (FrameLayout) chartView.findViewById(R.id.container);

        updateTextViews();

        FloatingActionButton fab = (FloatingActionButton) chartView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Refreshed", Snackbar.LENGTH_SHORT).show();
                refresh();
            }
        });

        timeChart = new TimeChart(getContext());
        chartContainer.addView(timeChart);

        fab.callOnClick();

        return chartView;
    }

    private void updateTextViews() {
        totalTimeText.setText(getString(R.string.in_interval,
                Time.formatInterval(getTotalWorkTime() + getTotalStopTime(), getContext())));
        workTimeText.setText(getString(R.string.worked_for,
                Time.formatInterval(getTotalWorkTime(), getContext())));
        stopTimeText.setText(getString(R.string.stopped_for,
                Time.formatInterval(getTotalStopTime(), getContext())));

        Time currentTime = new Time();
        refreshText.setText(getString(R.string.time_refreshed,
                currentTime.getTimeString()));
    }

    private void refresh() {

        updateTextViews();

        timeChart.addData(getTotalWorkTime(), getTotalStopTime());
        timeChart.animateXY(1500, 1500);
    }

    private int getTotalWorkTime() {
        dbHelper = TimeDbHelper.getInstance(getContext());
        int totalWorkTime = 0;
        Integer[] array = dbHelper.getWorkTime();

        for (Integer workTime : array) {
            totalWorkTime += workTime;
        }

        dbHelper.close();

        return totalWorkTime;
    }

    private int getTotalStopTime() {
        dbHelper = TimeDbHelper.getInstance(getContext());
        int totalStopTime = 0;
        Integer[] array = dbHelper.getStopTime();

        for (Integer stopTime : array) {
            totalStopTime += stopTime;
        }

        dbHelper.close();

        return totalStopTime;
    }

    private class TimeChart extends PieChart {

        public TimeChart(Context context) {
            super(context);
            this.setUsePercentValues(false);
            this.setDescription(null);
            this.getLegend().setEnabled(false);
            this.setTouchEnabled(false);
            this.setDrawHoleEnabled(false);
        }

        public void addData(int workSeconds, int stopSeconds) {
            ArrayList<Entry> yVals = new ArrayList<Entry>();
            yVals.add(new Entry((float) workSeconds, 0));


            ArrayList<String> xVals = new ArrayList<String>();
            xVals.add("Work");
            if (stopSeconds != 0) {
                xVals.add("Stop");
                yVals.add(new Entry((float) stopSeconds, 1));
            }

            PieDataSet dataSet = new PieDataSet(yVals, "Total Time");
            PieData data = new PieData(xVals, dataSet);

            data.setDrawValues(false);
            int[] colors = new int[2];
            colors[0] = ContextCompat.getColor(getContext(), R.color.colorGreen);
            colors[1] = ContextCompat.getColor(getContext(), R.color.colorRed);

            dataSet.setColors(colors);
            dataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            dataSet.setValueTextSize(14);

            this.setData(data);
            this.invalidate();
        }

    }


}
