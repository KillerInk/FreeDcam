package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 07.09.13.
 */
public class ManualContrastManager implements IStyleAbleSliderValueHasChanged
{
    public ManualContrastManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public boolean ExternalSet = false;

    CameraManager cameraManager;


    @Override
    public void ValueHasChanged(int value)
    {

        if (cameraManager.parametersManager.getSupportContrast())
        {
            cameraManager.parametersManager.SetContrast(value);
        }
    }
}
