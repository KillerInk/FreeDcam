package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.CameraManager;

/**
 * Created by troop on 03.09.13.
 */
public class ManualExposureManager implements  SeekBar.OnSeekBarChangeListener {

    private CameraManager cameramanager;
    public boolean ExternalSet =false;
    private int minValue = 0;
    private int maxValue = 10;
    private int currentValue = 0;

    public  ManualExposureManager(CameraManager cameraManager)
    {
        this.cameramanager = cameraManager;

    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (ExternalSet == false)
        {
            currentValue = progress + minValue;
            //if (maxValue < 61)
            //{
                if (currentValue >= cameramanager.parametersManager.getParameters().getMinExposureCompensation() && currentValue <= cameramanager.parametersManager.getParameters().getMaxExposureCompensation())
                {
                    cameramanager.parametersManager.SetExposureCompensation(currentValue);
                    //cameramanager.parameters.setExposureCompensation(currentValue);
                    //cameramanager.Restart(false);
                }
            /*}
            else
            {
                cameramanager.parameters.set("manual-exposure", currentValue);
                cameramanager.Restart(false);
            }*/
        }
        else
        {
            ExternalSet = false;
        }
    }

    public  void SetMinMax(int min, int max)
    {
        minValue = min;
        maxValue = max;
        //cameramanager.activity.exposureSeekbar.setMax(max + min * -1);
    }

    /*public int GetCurrentValue()
    {
        int val = cameramanager.activity.exposureSeekbar.getProgress() + minValue;
        return val;
    }

    public void SetCurrentValue(int progress)
    {
        int val = progress + maxValue;
        cameramanager.activity.exposureSeekbar.setProgress(val);
    }*/

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }
}
