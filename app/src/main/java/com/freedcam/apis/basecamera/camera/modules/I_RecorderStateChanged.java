package com.freedcam.apis.basecamera.camera.modules;

/**
 * Created by troop on 26.11.2014.
 */
public interface I_RecorderStateChanged
{
    int STATUS_RECORDING_START = 1;
    int STATUS_RECORDING_STOP = 0;
    void RecordingStateChanged(int status);
}
