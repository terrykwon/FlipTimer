package com.kwonterry.fliptimer;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kwonterry.fliptimer.data.TimeDbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    private final String LOG_TAG = RecordFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private TimeAdapter mTimeAdapter;
    private TimeDbHelper mTimeDbHelper;

    // to close cursor, declare member
    private Cursor mCursor;

    private BroadcastReceiver receiver;

    public RecordFragment() {
        // Required empty public constructor
    }


    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(LOG_TAG, "Broadcast Intent Received.");
                mTimeAdapter.swapCursor(mTimeDbHelper.getAllData());
                mTimeAdapter.notifyDataSetChanged();
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            Log.v(LOG_TAG, "Log Cleared.");

            // Delete database.
            mTimeDbHelper.removeAll();

            // Clear screen
            mTimeAdapter.swapCursor(mTimeDbHelper.getAllData());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RecordView = inflater.inflate(R.layout.fragment_record, container, false);

        mTimeDbHelper = new TimeDbHelper(getContext());
        mCursor = mTimeDbHelper.getAllData();


        mTimeAdapter = new TimeAdapter(getContext(), mCursor, 0);

        ListView listView = (ListView) RecordView.findViewById(R.id.listview_log);
        listView.setAdapter(mTimeAdapter);

        FloatingActionButton fab = (FloatingActionButton) RecordView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeDbHelper.removeAll();
                mTimeAdapter.swapCursor(mTimeDbHelper.getAllData());
                // doesn't refresh the list!!!!!
            }
        });

        return RecordView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.v(LOG_TAG, "onDetach RecordFragment");
        mCursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((receiver),
                new IntentFilter(FlipService.TIME_RECORDED)
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
