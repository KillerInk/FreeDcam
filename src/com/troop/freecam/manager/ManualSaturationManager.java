package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 08.09.13.
 */
public class ManualSaturationManager implements IStyleAbleSliderValueHasChanged{

    CameraManager cameraManager;

    public ManualSaturationManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }
    @Override
    public void ValueHasChanged(int value)
    {
        if (cameraManager.parametersManager.getSupportSaturation())
        {
            cameraManager.parametersManager.manualSaturation.set(value);
            //cameraManager.Restart(false);
        }
    }
}
