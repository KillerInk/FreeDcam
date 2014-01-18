package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 07.09.13.
 */
public class ManualBrightnessManager implements IStyleAbleSliderValueHasChanged
{
    CameraManager cameraManager;

    public ManualBrightnessManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }


    @Override
    public void ValueHasChanged(int value) {
        if (cameraManager.parametersManager.getSupportWhiteBalance())
            cameraManager.parametersManager.Brightness.Set(value);
    }
}
