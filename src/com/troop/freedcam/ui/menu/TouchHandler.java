package com.troop.freedcam.ui.menu;

import android.view.MotionEvent;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    final int distance = 100;
    public int startX;
    public int startY;
    public int currentX;
    public int currentY;
    boolean swipe = false;

    public boolean onTouchEvent(MotionEvent event)
    {
        boolean fireagain = true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                detectSwipeDirection();
                break;
            case MotionEvent.ACTION_UP:
                if (swipe == false)
                {
                    OnClick((int)event.getX(), (int)event.getY());

                }
                swipe = false;
                fireagain = false;
                break;
        }


        return fireagain;
    }

    private void detectSwipeDirection()
    {
        int x = getDistance(startX, currentX);
        int y = getDistance(startY, currentY);
        if (x >= distance || y >= distance)
        {
            swipe = true;
            if (x >= y)
                doHorizontalSwipe();
            else
                doVerticalSwipe();
        }
    }

    protected void doHorizontalSwipe()
    {

    }

    protected void doVerticalSwipe()
    {
    }

    protected void OnClick(int x, int y)
    {

    }

    private int getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        return dis;
    }


}
