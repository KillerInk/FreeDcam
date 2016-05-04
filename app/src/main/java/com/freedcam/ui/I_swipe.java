package com.freedcam.ui;

import android.view.MotionEvent;

/**
 * Created by troop on 29.09.2014.
 */
public interface I_swipe
{

    void doLeftToRightSwipe();

    void doRightToLeftSwipe();

    void doTopToBottomSwipe();

    void doBottomToTopSwipe();


    /**
     * Gets called when a click is detected
     * @param x the x axis
     * @param y the y axis
     */
    void onClick(int x, int y);
    void onMotionEvent(MotionEvent event);
}
