package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 07.09.13.
 */
public class ManualSharpnessManager implements IStyleAbleSliderValueHasChanged
{

    CameraManager cameraManager;

    public ManualSharpnessManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
 public void ValueHasChanged(int value)
{
    if (cameraManager.parametersManager.getSupportSharpness())
    {
        cameraManager.parametersManager.manualSharpness.set(value);
        //cameraManager.Restart(false);
    }
}
}
