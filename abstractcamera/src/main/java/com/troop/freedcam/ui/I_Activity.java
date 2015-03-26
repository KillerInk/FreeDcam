package com.troop.freedcam.ui;

import android.graphics.Bitmap;
import android.view.SurfaceView;

/**
 * Created by troop on 22.03.2015.
 */
public interface I_Activity
{
    void ActivateSonyApi(String Api);
    void SetTheme(String Theme);
    SurfaceView GetSurfaceView();
    int GetPreviewWidth();
    int GetPreviewHeight();
    int GetPreviewLeftMargine();
    int GetPreviewRightMargine();
    int GetPreviewTopMargine();
    void SetPreviewSizeChangedListner(I_PreviewSizeEvent event);
    int[] GetScreenSize();

}
