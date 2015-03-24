package com.troop.freedcam.ui;

import android.graphics.Bitmap;

/**
 * Created by troop on 22.03.2015.
 */
public interface I_Activity
{
    void ActivateSonyApi(String Api);
    void SetTheme(String Theme);
    Bitmap GetBackground();
}
