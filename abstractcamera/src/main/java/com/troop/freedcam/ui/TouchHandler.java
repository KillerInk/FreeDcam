package com.troop.freedcam.ui;

import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    private final int distance = 90;
    private final String TAG = TouchHandler.class.getSimpleName();
    private final boolean DEBUG = true;

    private void L(String log)
    {
        if (DEBUG)
            Log.d(TAG,log);
    }

    private int startX;
    private int startY;
    private int currentX;
    private int currentY;
    private boolean swipeDetected = false;
    private boolean newActionBlocked = false;
    private final int blockTime = 500;
    private Handler handler;


    TouchHandler()
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
                break;
        }


        return fireagain;
    }

    private boolean detectSwipeDirection()
    {
        //if last swipeDetected is less then 500 ms it blocked
        if (swipeDetected || newActionBlocked)
            return false;
        float x = getDistance(startX, currentX);
        float y = getDistance(startY, currentY);
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

    void doLeftToRightSwipe()
    {
    }

    void doRightToLeftSwipe()
    {
    }

    void doTopToBottomSwipe()
    {
    }

    void doBottomToTopSwipe()
    {
    }

    void OnClick(int x, int y)
    {

    }

    private static float getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        float density = Resources.getSystem().getDisplayMetrics().density;
        return dis / density;
    }

    public static float getNegDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        float density = Resources.getSystem().getDisplayMetrics().density;
        return dis / density;
    }


}
