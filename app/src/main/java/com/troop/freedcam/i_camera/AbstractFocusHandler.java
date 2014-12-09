package com.troop.freedcam.i_camera;

import android.graphics.Rect;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractFocusHandler
{
    public void StartFocus(){};
    public void StartTouchToFocus(Rect rect, int width, int height){};
    public I_Focus focusEvent;
}
