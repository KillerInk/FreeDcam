package com.troop.freedcam.eventbus.events;

public class DisableViewPagerTouchEvent {
    public final boolean disableIt;
    public DisableViewPagerTouchEvent(boolean disableIt)
    {
        this.disableIt = disableIt;
    }
}
