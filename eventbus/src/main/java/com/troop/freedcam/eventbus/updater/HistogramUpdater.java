package com.troop.freedcam.eventbus.updater;

import android.renderscript.Allocation;

import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.HistogramChangedEvent;
import com.troop.freedcam.eventbus.events.HistogramVisibilityEvent;

import org.greenrobot.eventbus.EventBus;

public class HistogramUpdater
{
    public static void sendHistogram(Allocation histodataR){
        EventBusHelper.post(new HistogramChangedEvent(histodataR));
    }

    public static void updateHistogramVisibility(int visibility)
    {
        EventBusHelper.post(new HistogramVisibilityEvent(visibility));
    }
}
