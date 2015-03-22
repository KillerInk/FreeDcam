package com.troop.freedcam.i_camera.interfaces;

import com.troop.freedcam.i_camera.FocusRect;

/**
 * Created by troop on 02.09.2014.
 */
public interface I_Focus
{
    public void FocusStarted(FocusRect rect);
    public void FocusFinished(boolean success);
    public void FocusLocked(boolean locked);
}
