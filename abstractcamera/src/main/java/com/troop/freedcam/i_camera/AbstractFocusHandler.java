package com.troop.freedcam.i_camera;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.interfaces.I_Focus;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractFocusHandler
{
    final String TAG = AbstractFocusHandler.class.getSimpleName();
    public void StartFocus(){};
    public void StartTouchToFocus(FocusRect rect, FocusRect meteringarea, int width, int height){};
    public void SetMeteringAreas(FocusRect meteringRect, int width, int height){};
    public abstract void SetAwbAreas(FocusRect awbRect, int width, int height);
    public I_Focus focusEvent;
    public abstract boolean isAeMeteringSupported();
    public abstract boolean isWbMeteringSupported();
    public abstract void SetMotionEvent(MotionEvent event);

    protected void logFocusRect(FocusRect rect)
    {
        Logger.d(TAG, "left:" + rect.left + "top:" + rect.top + "right:" + rect.right + "bottom:" + rect.bottom);
    }

    protected void logRect(Rect rect)
    {
        Logger.d(TAG, "left:" + rect.left + "top:" + rect.top + "right:" + rect.right + "bottom:" + rect.bottom);
    }
}
