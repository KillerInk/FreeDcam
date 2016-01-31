package com.troop.freedcam.ui;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    static final int distance = 300;
    static final int MAX_DURATION = 600;
    private int startX;
    private int startY;
    private int currentX;
    private int currentY;
    private int x;
    private int y;
    public boolean LeftToRight;
    public boolean RightToLeft;
    public boolean TopToBottom;
    public boolean BottomToTop;

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
                            doLeftToRightSwipe();
                        }
                        else {
                            LeftToRight = false;
                            RightToLeft = true;
                            TopToBottom = false;
                            BottomToTop = false;
                            doRightToLeftSwipe();
                        }
                    }
                    else{
                        if (currentY > startY) {
                            LeftToRight = false;
                            RightToLeft = false;
                            TopToBottom = true;
                            BottomToTop = false;
                            doTopToBottomSwipe();
                        }
                        else {
                            LeftToRight = false;
                            RightToLeft = false;
                            TopToBottom = false;
                            BottomToTop = true;
                            doBottomToTopSwipe();
                        }
                    }
                }
                else if (x < distance && y < distance)
                {
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
