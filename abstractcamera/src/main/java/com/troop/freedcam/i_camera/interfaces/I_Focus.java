package com.troop.freedcam.i_camera.interfaces;

import com.troop.freedcam.i_camera.FocusRect;

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
}
