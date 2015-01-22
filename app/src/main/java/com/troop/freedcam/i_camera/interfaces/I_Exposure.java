package com.troop.freedcam.i_camera.interfaces;

import com.troop.freedcam.i_camera.ExposureRect;
import com.troop.freedcam.i_camera.FocusRect;

/**
 * Created by George on 1/21/2015.
 */
public interface I_Exposure {
    public void ExposureStarted(ExposureRect rect);
    //public void FocusFinished(boolean success);
}
