package com.troop.freedcam.eventbus.events;

import android.renderscript.Allocation;

public class HistogramChangedEvent {

    private Allocation allocation;

    public HistogramChangedEvent(Allocation histodataR)
    {
        this.allocation = histodataR;
    }

    public Allocation getAllocation() {
        return allocation;
    }
}
