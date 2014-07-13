package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 07.09.13.
 */
public class ManualFocusManager implements IStyleAbleSliderValueHasChanged
{

    CameraManager cameraManager;

    public ManualFocusManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void ValueHasChanged(int value)
    {
        if (cameraManager.parametersManager.getSupportManualFocus())
        {
            cameraManager.parametersManager.manualFocus.set(value);
            //cameraManager.Restart(false);
        }
    }
}
