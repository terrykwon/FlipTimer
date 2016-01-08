package com.kwonterry.fliptimer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kwonterry.fliptimer.data.TimeDbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    private final String LOG_TAG = TimerFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private TextView mTimerTextView;
    private SimpleClock mClock;
    private TimeDbHelper mTimeDbHelper;

    //debug
    private TimeRecordActivity mRecord;

    public TimerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TimerFragment newInstance(String param1, String param2) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mClock = new SimpleClock();
        mTimeDbHelper = new TimeDbHelper(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View TimerView = inflater.inflate(R.layout.fragment_timer, container, false);
        mTimerTextView = (TextView) TimerView.findViewById(R.id.timerText);

        //Debug
        mTimerTextView.setText("Time");

        ToggleButton toggleButton = (ToggleButton) TimerView.findViewById(R.id.toggleButton);
        //what does this refer to?
        toggleButton.setOnCheckedChangeListener(this);

        Button viewLogButton = (Button) TimerView.findViewById(R.id.button_log);
        viewLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TimeRecordActivity.class);
                startActivity(intent);

                // TODO: Interact with MainActivity, add RecordFragment to backstack
            }
        });

        return TimerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        WriteTimeTask writeTimeTask = new WriteTimeTask(getContext());

        if (isChecked) {
            mTimerTextView.setText(mClock.getCurrentTime());
            mTimerTextView.setTextColor(Color.BLUE);
            writeTimeTask.execute(mClock.getTimeInMillis(), 1);
        } else {
            mTimerTextView.setText(mClock.getCurrentTime());
            mTimerTextView.setTextColor(Color.RED);
            writeTimeTask.execute(mClock.getTimeInMillis(), 0);
        }
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
