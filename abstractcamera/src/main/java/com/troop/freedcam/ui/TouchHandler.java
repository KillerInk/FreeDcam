package com.troop.freedcam.ui;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    static final int distance = 350;
    static final int MAX_DURATION = 600;
    public int startX;
    public int startY;
    public int currentX;
    public int currentY;
    public int x;
    public int y;

    public boolean onTouchEvent(MotionEvent event)
    {
        boolean fireagain = true;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startX == 0 && startY == 0)
                {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                x = getDistance(startX, currentX);
                y = getDistance(startY, currentY);
                long duration = event.getEventTime() - event.getDownTime();
                if (x >= distance && duration <= MAX_DURATION || y >= distance && duration <= MAX_DURATION)
                {
                    Log.d("TouchHAndler", "currentX:" + currentX + " X:" + startX);
                    if (x >= y) {
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
                }
                else if (x < distance && y < distance)
                {
                    OnClick((int)event.getX(), (int)event.getY());
                }
                break;
        }
        return fireagain;
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
