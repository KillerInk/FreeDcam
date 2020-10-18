package com.troop.freedcam.eventbus.events;


import com.troop.freedcam.eventbus.enums.CaptureStates;

public class CaptureStateChangedEvent {
    public final CaptureStates captureState;

    public CaptureStateChangedEvent(CaptureStates captureState)
    {
        this.captureState = captureState;
    }
}
