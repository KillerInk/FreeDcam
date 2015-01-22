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
    long start;
    long duration;
    static final int MAX_DURATION = 3500;


    public boolean onTouchEvent(MotionEvent event)
    {
        boolean fireagain = true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                start = System.currentTimeMillis();

                break;
           // case MotionEvent.A
            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                detectSwipeDirection();
                break;
            case MotionEvent.ACTION_UP:
                long time = System.currentTimeMillis() - start;
                duration = duration+time;
                if (duration >= MAX_DURATION)
                    System.out.println("Long Press Time: "+ duration);
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
