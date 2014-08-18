package com.troop.freecamv2.ui.menu;

import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by troop on 18.08.2014.
 */
public class SwipeMenuListner
{
    LinearLayout settingsLayout;
    LinearLayout manualSettingsLayout;

    boolean isMoving;
    final int distance = 100;
    int startX;
    int startY;

    public SwipeMenuListner(LinearLayout settingsLayout, LinearLayout manualSettingsLayout)
    {
        this.manualSettingsLayout = manualSettingsLayout;
        this.settingsLayout = settingsLayout;
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }
}
