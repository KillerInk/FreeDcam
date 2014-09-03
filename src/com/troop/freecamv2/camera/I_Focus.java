package com.troop.freecamv2.camera;

import android.graphics.Rect;

/**
 * Created by troop on 02.09.2014.
 */
public interface I_Focus
{
    public void FocusStarted(Rect rect);
    public void FocusFinished(boolean success);
}
