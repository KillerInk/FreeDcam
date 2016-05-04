package com.freedcam.apis.i_camera;

import com.freedcam.apis.i_camera.interfaces.I_Exposure;

/**
 * Created by George on 1/21/2015.
 */
public abstract class AbstractExposureMeterHandler {

    public void StartExposure(){}
    public void StartTouchToFocus(ExposureRect rect, int width, int height){}
    public I_Exposure exposureEvent;
}
