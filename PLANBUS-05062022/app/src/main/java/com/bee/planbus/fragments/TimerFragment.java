package com.bee.planbus.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;

import com.bee.planbus.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimerFragment extends Fragment {

    @BindView(R.id.timer)
    Chronometer timer;
    @BindView(R.id.btnStart)
    ImageButton btnStart;
    @BindView(R.id.btnStop)
    ImageButton btnStop;

    private boolean isResume;
    Handler handler;
    long tMiliSecond, tStart, tBuff, tUpdate = 0L;
    int second, min, miliSecond;

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, view);

        handler = new Handler();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isResume){
                    tStart = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    timer.start();
                    isResume = true;
                    btnStop.setVisibility(View.GONE);
                    btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                }else{
                    tBuff += tMiliSecond;
                    handler.removeCallbacks(runnable);
                    timer.stop();
                    isResume = false;
                    btnStop.setVisibility(View.VISIBLE);
                    btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_start));
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isResume){
                    btnStart.setImageDrawable(getResources().getDrawable(R.drawable.ic_start));
                    tMiliSecond = 0L;
                    tStart = 0L;
                    tBuff = 0L;
                    tUpdate = 0L;
                    second = 0;
                    min = 0;
                    miliSecond = 0;
                    timer.setText("00:00:00");
                }
            }
        });


        return view;
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMiliSecond = SystemClock.uptimeMillis()- tStart;
            tUpdate = tBuff + tMiliSecond;
            second = (int) (tUpdate/1000);
            min = second/60;
            second = second%60;
            miliSecond = (int) (tUpdate%100);
            timer.setText(String.format("%02d", min)+ ":"+ String.format("%02d", second)+ ":" + String.format("%2d", miliSecond));
            handler.postDelayed(this, 60);
        }
    };
}