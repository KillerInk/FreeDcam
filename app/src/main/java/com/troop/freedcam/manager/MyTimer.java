package com.troop.freedcam.manager;

import android.os.Handler;
import android.widget.TextView;

/**
 * Created by troop on 13.10.13.
 */
public class MyTimer
{
    TextView textView;
    Integer secondsDone = 0;
    boolean stop = false;

    private final int interval = 1000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            secondsDone++;
            int hour = secondsDone / 3600;
            int min = secondsDone / 60;
            int sec = secondsDone % 60;
            textView.setText(String.format("%d:%02d:%02d", hour, min, sec));
            if(!stop)
                handler.postDelayed(runnable, interval);
        }
    };

    public MyTimer(TextView textView)
    {
        this.textView = textView;
    }


    public  void Start()
    {
        //handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        stop = false;
        handler.postDelayed(runnable, interval);
    }

    public void Stop()
    {
        stop = true;
        secondsDone = 0;
    }


}
