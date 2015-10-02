package com.troop.freedcam.i_camera;

import com.troop.freedcam.i_camera.interfaces.I_Focus;
import com.troop.freedcam.i_camera.modules.I_Callbacks;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractFocusHandler
{
    protected boolean hasFocus = false;

    public void StartFocus(){};
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height){};
    public void SetMeteringAreas(FocusRect meteringRect, int width, int height){};
    public abstract void SetAwbAreas(FocusRect awbRect, int width, int height);
    public I_Focus focusEvent;
    public abstract boolean HasFocus();
    public abstract void SetFocusFalse();
    public abstract void SetModuleFocusCallback(I_Callbacks.AutoFocusCallback moduleFocusCallback);
}
