package com.troop.freedcam.ui;

import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;

import javax.xml.parsers.FactoryConfigurationError;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    static final int distance = 60;
    static final int MAX_DURATION = 600;
    private int startX;
    private int startY;
    private int currentX;
    private int currentY;
    private float x;
    private float y;
    private MotionEvent event;
    public boolean LeftToRight;
    public boolean RightToLeft;
    public boolean TopToBottom;
    public boolean BottomToTop;

    public MotionEvent getEvent(){return event;}

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
                LeftToRight = false;
                RightToLeft = false;
                TopToBottom = false;
                BottomToTop = false;
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                x = getDistance(startX, currentX);
                y = getDistance(startY, currentY);
                long duration = event.getEventTime() - event.getDownTime();
                if (x >= distance && duration <= MAX_DURATION || y >= distance && duration <= MAX_DURATION)
                {
                    Log.d("TouchHAndler", "currentX:" + currentX + " X:" + startX);
                    if (x >= y) {
                        if (currentX > startX) {
                            LeftToRight = true;
                            RightToLeft = false;
                            TopToBottom = false;
                            BottomToTop = false;
                            fireagain = false;
                            doLeftToRightSwipe();
                        }
                        else {
                            LeftToRight = false;
                            RightToLeft = true;
                            TopToBottom = false;
                            BottomToTop = false;
                            fireagain = false;
                            doRightToLeftSwipe();
                        }
                    }
                    else{
                        if (currentY > startY) {
                            LeftToRight = false;
                            RightToLeft = false;
                            TopToBottom = true;
                            BottomToTop = false;
                            fireagain = false;
                            doTopToBottomSwipe();
                        }
                        else {
                            LeftToRight = false;
                            RightToLeft = false;
                            TopToBottom = false;
                            BottomToTop = true;
                            fireagain = false;
                            doBottomToTopSwipe();
                        }
                    }
                }
                else if (x < distance && y < distance)
                {
                    fireagain = false;
                    OnClick((int)event.getX(), (int)event.getY());

                }
                startX = 0;
                currentX = 0;
                startY = 0;
                currentY = 0;
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

    public static float getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        float dm = Resources.getSystem().getDisplayMetrics().density;
        return dis/dm;

    }

    public static int getNegDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        return dis;
    }
}
