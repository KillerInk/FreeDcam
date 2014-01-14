package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 07.09.13.
 */
public class ManualContrastManager implements SeekBar.OnSeekBarChangeListener
{
    public ManualContrastManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public boolean ExternalSet = false;

    CameraManager cameraManager;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (ExternalSet == false && cameraManager.parametersManager.getSupportContrast() && fromUser)
        {
            cameraManager.parametersManager.SetContrast(progress);
        }
        else
            ExternalSet = false;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
