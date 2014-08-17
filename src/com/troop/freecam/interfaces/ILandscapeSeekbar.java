package com.troop.freecam.interfaces;

import com.troop.freecam.camera.old.CameraManager;

/**
 * Created by troop on 12.08.2014.
 */
public interface ILandscapeSeekbar
{
    public void SetCameraManager(CameraManager cameraManager);

    public void SetMinMaxValues(int min, int max);

    public void SetCurrentValue(int current);

    public int GetCurrentValue();

    public void SetText(String text);

}
