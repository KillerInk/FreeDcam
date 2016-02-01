package com.troop.freedcam.ui;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Date;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    final int distance = 150;
    public int startX;
    public int startY;
    public int currentX;
    public int currentY;
    boolean swipe = false;
    long start;
    private final long timetowait = 500;
    private boolean swipeAllowed = true;
    private boolean swipeWasThrown = false;

    public boolean onTouchEvent(MotionEvent event)
    {
        boolean fireagain = true;

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                //action down resets all already set values and get the new one from the event
                startX = (int) event.getX();
                startY = (int) event.getY();
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                //get start time when down happen.
                start = System.currentTimeMillis();
                //reset swipe to false
                swipe = false;
                swipeWasThrown = false;
                Log.d("TouchHAndler", "currentX:" + currentX + " X:" + startX);
                break;
            // case MotionEvent.A
            case MotionEvent.ACTION_MOVE:
                //in case action down never happend
                if (startX == 0 && startY == 0)
                {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    //get start time when down happen.
                    start = System.currentTimeMillis();
                    //reset swipe to false
                    swipe = false;
                }
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                //detect swipe. if swipe detected return false else true
                fireagain = detectSwipeDirection();
                break;
            case MotionEvent.ACTION_UP:
                //in case no swipe happen swipe is false and it was a click
                if (swipe == false)
                {
                    OnClick((int)event.getX(), (int)event.getY());
                }
                swipe = false;
                fireagain = false;
                currentX = 0;
                startX = 0;
                currentY = 0;
                startY = 0;
                break;
        }


        return fireagain;
    }

    private boolean detectSwipeDirection()
    {
        //if last swipe is less then 500 ms it blocked
        if (!swipeAllowed || swipeWasThrown)
            return true;
        int x = getDistance(startX, currentX);
        int y = getDistance(startY, currentY);
        //if we have a swipe
        if (x >= distance || y >= distance)
        {
            //block swipe for next 500ms
            swipeAllowed = false;
            //its a swipe
            swipe = true;
            Log.d("TouchHAndler", "currentX:" + currentX + " X:" + startX);
            if (x >= y)
            {
                if (currentX > startX)
                    doLeftToRightSwipe();
                else
                    doRightToLeftSwipe();
            }
            else{
                if (currentY > startY)
                    doTopToBottomSwipe();
                else
                    doBottomToTopSwipe();
            }
            //reset all values
            currentX = 0;
            startX = 0;
            currentY = 0;
            startY = 0;
            //release swipeAllowed after 500ms
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    TouchHandler.this.swipeAllowed = true;
                }
            },timetowait);
            return false;
        }
        return true;
    }

    protected void doLeftToRightSwipe()
    {
    }

    protected void doRightToLeftSwipe()
    {
    }

    protected void doTopToBottomSwipe()
    {
    }

    protected void doBottomToTopSwipe()
    {
    }

    protected void OnClick(int x, int y)
    {

    }

    public static int getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        return dis;
    }

    public static int getNegDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        return dis;
    }


}
