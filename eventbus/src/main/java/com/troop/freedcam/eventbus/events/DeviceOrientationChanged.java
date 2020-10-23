package com.troop.freedcam.eventbus.events;

public class DeviceOrientationChanged {

    public final int deviceOrientation;

    public DeviceOrientationChanged(int deviceOrientation)
    {
        this.deviceOrientation = deviceOrientation;
    }
}
