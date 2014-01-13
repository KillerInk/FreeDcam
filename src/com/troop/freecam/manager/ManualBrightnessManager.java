package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 07.09.13.
 */
public class ManualBrightnessManager implements SeekBar.OnSeekBarChangeListener
{
    CameraManager cameraManager;

    public ManualBrightnessManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        //cameraManager.parametersManager.SetBrightness(progress);
        cameraManager.parametersManager.Brightness.Set(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
