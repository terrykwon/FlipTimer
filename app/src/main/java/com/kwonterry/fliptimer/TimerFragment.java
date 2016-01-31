package com.kwonterry.fliptimer;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {

    private final String LOG_TAG = TimerFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private TextClock mTextClock;
    private View mBackground;
    private TextView motivateText;
    private BroadcastReceiver receiver;

    private ToggleButton startServiceToggle;
    static final public String FLIPSERVICE_STOPPED = "com.terrykwon.fliptimer.FLIPSERVICE_STOPPED";

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((receiver),
                new IntentFilter("com.terrykwon.fliptimer.FLIPSERVICE_STOPPED"));
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
        super.onCreate(savedInstanceState);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(LOG_TAG, FLIPSERVICE_STOPPED);
                startServiceToggle.setChecked(false);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View TimerView = inflater.inflate(R.layout.fragment_timer, container, false);
        mTextClock = (TextClock) TimerView.findViewById(R.id.textClock);
        mBackground = TimerView.findViewById(R.id.layout_timer);
        motivateText = (TextView) TimerView.findViewById(R.id.motivation_text);

        startServiceToggle = (ToggleButton) TimerView.findViewById(R.id.toggle_service);
        startServiceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(getActivity(), FlipService.class);
                if (isChecked) {
                    getActivity().startService(intent);
                    whenStartFlipService();
                } else {
                    getActivity().stopService(intent);
                    whenStopFlipService();
                }
            }
        });

        return TimerView;
    }

    public void whenStartFlipService() {
        fadeBackgroundColor(true);
        motivateText.setText("flip phone face down and work.");
        changeTextColor(true);
    }

    public void whenStopFlipService() {
        fadeBackgroundColor(false);
        motivateText.setText("Just do it.");
        changeTextColor(false);
    }

    public void fadeBackgroundColor(boolean turnOn) {
        ObjectAnimator colorFade;
        if (turnOn) {
            colorFade = ObjectAnimator.ofObject(
                    mBackground, "backgroundColor",
                    new ArgbEvaluator(),0xffF9F9F9
                    , Color.argb(255, 37, 49, 55));

        } else {
            colorFade = ObjectAnimator.ofObject(
                    mBackground, "backgroundColor",
                    new ArgbEvaluator(), Color.argb(255, 37, 49, 55), 0xffF9F9F9);
        }
        colorFade.setDuration(3000);
        colorFade.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    public void changeTextColor(boolean turnOn) {
        if (turnOn) {
            mTextClock.setTextColor(Color.parseColor("#FFFFFF"));
            motivateText.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            mTextClock.setTextColor(Color.parseColor("#000000"));
            motivateText.setTextColor(Color.parseColor("#000000"));
        }
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
