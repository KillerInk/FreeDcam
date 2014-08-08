package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 07.09.13.
 */
public class ManualShutterManager implements IStyleAbleSliderValueHasChanged
{

    CameraManager cameraManager;

    public ManualShutterManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void ValueHasChanged(int value)
    {
        if (cameraManager.parametersManager.getSupportManualShutter())
        {
            cameraManager.parametersManager.manualShutter.set(value);
            //cameraManager.ReloadCameraParameters(false);
        }
    }
}
