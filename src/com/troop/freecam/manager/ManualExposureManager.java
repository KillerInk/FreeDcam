package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.CameraManager;

/**
 * Created by troop on 03.09.13.
 */
public class ManualExposureManager implements  SeekBar.OnSeekBarChangeListener {

    private CameraManager cameramanager;
    public boolean ExternalSet =false;
    public  ManualExposureManager(CameraManager cameraManager)
    {
        this.cameramanager = cameraManager;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (ExternalSet == false)
        {
            int truevalue = progress + cameramanager.parameters.getMinExposureCompensation();
            if (truevalue >= cameramanager.parameters.getMinExposureCompensation() && truevalue <= cameramanager.parameters.getMaxExposureCompensation())
            {
                cameramanager.parameters.setExposureCompensation(truevalue);
                cameramanager.Restart(false);
            }
        }
        else
        {
            ExternalSet = false;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }
}
