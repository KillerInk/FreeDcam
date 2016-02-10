package com.troop.freedcam.ui;

import android.view.SurfaceView;

import java.io.File;

/**
 * Created by troop on 22.03.2015.
 */
public interface I_Activity
{
    void SwitchCameraAPI();
    void SetTheme();
    SurfaceView GetSurfaceView();
    int GetPreviewWidth();
    int GetPreviewHeight();
    int GetPreviewLeftMargine();
    int GetPreviewRightMargine();
    int GetPreviewTopMargine();
    int[] GetScreenSize();
    void ShowHistogram();
    void loadImageViewerFragment();
    void loadCameraUiFragment();
    void closeActivity();
}
