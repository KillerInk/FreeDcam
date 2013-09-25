package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.CameraManager;

/**
 * Created by troop on 08.09.13.
 */
public class ManualSaturationManager implements  SeekBar.OnSeekBarChangeListener{
    public ManualSaturationManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    CameraManager cameraManager;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        cameraManager.parameters.set("saturation", progress);
        cameraManager.Restart(false);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
