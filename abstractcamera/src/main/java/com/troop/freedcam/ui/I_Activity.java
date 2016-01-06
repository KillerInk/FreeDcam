package com.troop.freedcam.ui;

import android.content.Context;
import android.view.SurfaceView;

import java.io.File;

/**
 * Created by troop on 22.03.2015.
 */
public interface I_Activity
{
    void SwitchCameraAPI(String Api);
    void SetTheme(String Theme);
    SurfaceView GetSurfaceView();
    int GetPreviewWidth();
    int GetPreviewHeight();
    int GetPreviewLeftMargine();
    int GetPreviewRightMargine();
    int GetPreviewTopMargine();
    int[] GetScreenSize();
    void ShowHistogram(boolean enable);
    void loadImageViewerFragment(File file);
    void loadCameraUiFragment();
    void closeActivity();
}
