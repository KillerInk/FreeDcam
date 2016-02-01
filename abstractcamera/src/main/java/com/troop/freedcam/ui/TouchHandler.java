package com.troop.freedcam.ui;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    private final String TAG = TouchHandler.class.getSimpleName();
    private final boolean DEBUG = true;

    private void L(String log)
    {
        if (DEBUG)
            Log.d(TAG,log);
    }

    final int distance = 150;
    public int startX;
    public int startY;
    public int currentX;
    public int currentY;
    private boolean swipeDetected = false;
    private boolean newActionBlocked = false;
    private final int blockTime = 500;
    private Handler handler;


    public TouchHandler()
    {
        handler = new Handler();
    }

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
                //reset swipeDetected to false
                swipeDetected = false;
                L("ACTION_DOWN currentX:" + currentX + " X:" + startX);
                break;
            case MotionEvent.ACTION_MOVE:
                //in case action down never happend
                if (startX == 0 && startY == 0)
                {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    //reset swipeDetected to false
                    swipeDetected = false;
                }
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                //detect swipeDetected. if swipeDetected detected return false else true
                fireagain = detectSwipeDirection();
                L("ACTION_MOVE Swipedetected:"+ swipeDetected);
                break;
            case MotionEvent.ACTION_UP:
                L("ACTION_UP Swipedetected:"+ swipeDetected);
                //in case no swipeDetected happen swipeDetected is false and it was a click
                if (!swipeDetected && !newActionBlocked)
                {
                    L("On Click happen");
                    OnClick((int) event.getX(), (int) event.getY());
                    newActionBlocked = true;
                    handler.postDelayed(resetActionBlock,blockTime);
                }
                swipeDetected = false;
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
        //if last swipeDetected is less then 500 ms it blocked
        if (swipeDetected || newActionBlocked)
            return false;
        int x = getDistance(startX, currentX);
        int y = getDistance(startY, currentY);
        //if we have a swipeDetected
        if (x >= distance || y >= distance)
        {
            //its a swipeDetected
            swipeDetected = true;
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
            newActionBlocked = true;
            handler.postDelayed(resetActionBlock,blockTime);
            return false;
        }
        return false;
    }

    private Runnable resetActionBlock = new Runnable() {
        @Override
        public void run() {
            TouchHandler.this.newActionBlocked = false;
        }
    };

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
