package com.troop.freedcam.i_camera;

import com.troop.freedcam.i_camera.interfaces.I_Focus;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractFocusHandler
{
    public void StartFocus(){};
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height){};
    public void SetMeteringAreas(FocusRect meteringRect, int width, int height){};
    public I_Focus focusEvent;
}
