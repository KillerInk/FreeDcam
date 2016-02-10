package com.troop.freedcam.i_camera;

import com.troop.freedcam.i_camera.interfaces.I_Exposure;

/**
 * Created by George on 1/21/2015.
 */
public abstract class AbstractExposureMeterHandler {

    public void StartExposure(){};
    public void StartTouchToFocus(){};
    public I_Exposure exposureEvent;
}
