package com.troop.freedcam.camera.modules;

/**
 * Created by troop on 26.11.2014.
 */
public interface I_RecorderStateChanged
{
    public static int STATUS_RECORDING_START = 1;
    public static int STATUS_RECORDING_STOP = 0;
    public void RecordingStateChanged(int status);
}
