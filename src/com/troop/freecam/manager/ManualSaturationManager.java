package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 08.09.13.
 */
public class ManualSaturationManager implements IStyleAbleSliderValueHasChanged{
    public ManualSaturationManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    CameraManager cameraManager;

    @Override
    public void ValueHasChanged(int value)
    {
        cameraManager.parametersManager.getParameters().set("saturation", value);
        cameraManager.Restart(false);
    }
}
