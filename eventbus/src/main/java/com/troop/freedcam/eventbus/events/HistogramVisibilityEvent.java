package com.troop.freedcam.eventbus.events;

public class HistogramVisibilityEvent {

    private int visibility;
    public HistogramVisibilityEvent(int visibility)
    {
        this.visibility = visibility;
    }

    public int getVisibility() {
        return visibility;
    }
}
