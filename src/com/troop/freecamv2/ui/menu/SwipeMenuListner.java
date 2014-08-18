package com.troop.freecamv2.ui.menu;

import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by troop on 18.08.2014.
 */
public class SwipeMenuListner
{
    LinearLayout settingsLayout;
    LinearLayout manualSettingsLayout;

    final int distance = 100;
    int startX;
    int startY;
    int currentX;
    int currentY;

    public SwipeMenuListner(LinearLayout settingsLayout, LinearLayout manualSettingsLayout)
    {
        this.manualSettingsLayout = manualSettingsLayout;
        this.settingsLayout = settingsLayout;
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        boolean fireagain = true;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startX = (int)event.getX();
                startY = (int)event.getY();
                currentX = (int)event.getX();
                currentY = (int)event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                dectectSwipe();
                break;
            case MotionEvent.ACTION_UP:
                fireagain = false;
                break;
        }

        return fireagain;
    }

    private void dectectSwipe()
    {
        if (startX < currentX && getDistance(startX, currentX) > 100)
            doLeftToRightSwipe();
        else if (startX > currentX && getDistance(startX, currentX) > 100)
            doRightToLeftSwipe();
        else if (startY > currentY && getDistance(startY, currentY) > 100)
            doBottomToTopSwipe();
        else if (startY < currentY && getDistance(startY, currentY) > 100)
            doTopToBottomSwipe();

    }


    private int getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        return dis;
    }

    private void doLeftToRightSwipe()
    {
        settingsLayout.setVisibility(View.VISIBLE);
    }
    private void doRightToLeftSwipe()
    {
        settingsLayout.setVisibility(View.GONE);
    }
    private void doTopToBottomSwipe()
    {
        manualSettingsLayout.setVisibility(View.VISIBLE);
    }
    private void doBottomToTopSwipe()
    {
        manualSettingsLayout.setVisibility(View.GONE);
    }
}
