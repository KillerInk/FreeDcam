package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 07.09.13.
 */
public class ManualSharpnessManager implements SeekBar.OnSeekBarChangeListener
{

    CameraManager cameraManager;

    public ManualSharpnessManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (cameraManager.parametersManager.getSupportSharpness())
        {
            cameraManager.parametersManager.getParameters().set("sharpness", progress);
            cameraManager.Restart(false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
