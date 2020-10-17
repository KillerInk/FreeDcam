package com.troop.freedcam.eventbus.updater;

import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.FocusCoordinatesEvent;

public class TouchToFocusUpdater {

    public static void updateTouchToFocusCoordinates(FocusCoordinatesEvent event)
    {
        EventBusHelper.post(event);
    }
}
