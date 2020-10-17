package com.troop.freedcam.eventbus.events;

import com.troop.freedcam.camera.basecamera.modules.ModuleHandlerAbstract;

public class CaptureStateChangedEvent {
    public final ModuleHandlerAbstract.CaptureStates captureState;

    public CaptureStateChangedEvent(ModuleHandlerAbstract.CaptureStates captureState)
    {
        this.captureState = captureState;
    }
}
