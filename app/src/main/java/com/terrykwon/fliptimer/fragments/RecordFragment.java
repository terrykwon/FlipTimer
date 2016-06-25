package com.terrykwon.fliptimer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.terrykwon.fliptimer.services.FlipService;
import com.terrykwon.fliptimer.R;
import com.terrykwon.fliptimer.adapters.TimeAdapter;
import com.terrykwon.fliptimer.data.TimeDbHelper;


/**
 * A Fragment with a ListView that displays recorded times.
 */
public class RecordFragment extends Fragment {

    private final String LOG_TAG = RecordFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private TimeAdapter mTimeAdapter;
    private TimeDbHelper mTimeDbHelper;

    // to close cursor, declare member
    private Cursor mCursor;
    private ListView recordListView;

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
//        setHasOptionsMenu(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(LOG_TAG, "Broadcast Intent Received.");
                mTimeAdapter.swapCursor(mTimeDbHelper.getAllData());
                recordListView.smoothScrollToPosition(0);
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mTimeDbHelper = TimeDbHelper.getInstance(getContext());
        mCursor = mTimeDbHelper.getAllData();

        mTimeAdapter = new TimeAdapter(getContext(), mCursor, 0);

        recordListView = (ListView) recordView.findViewById(R.id.listview_log);
        recordListView.setAdapter(mTimeAdapter);

        FloatingActionButton fab = (FloatingActionButton) recordView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Log Cleared", Snackbar.LENGTH_SHORT).show();
                mTimeDbHelper.removeAll();
                mTimeAdapter.swapCursor(mTimeDbHelper.getAllData());

            }
        });

        return recordView;
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
        mTimeAdapter.swapCursor(mTimeDbHelper.getAllData());
        recordListView.smoothScrollToPosition(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimeDbHelper.close();
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
