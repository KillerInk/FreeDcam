package com.freedcam.apis.i_camera.interfaces;

import android.view.MotionEvent;

import com.freedcam.apis.i_camera.FocusRect;

/**
 * Created by troop on 02.09.2014.
 */
public interface I_Focus
{
    void FocusStarted(FocusRect rect);
    void FocusFinished(boolean success);
    void FocusLocked(boolean locked);
    void TouchToFocusSupported(boolean isSupported);
    void AEMeteringSupported(boolean isSupported);
    void AWBMeteringSupported(boolean isSupported);
    boolean onTouchEvent(MotionEvent event);
}
