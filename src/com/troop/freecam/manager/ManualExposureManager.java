package com.troop.freecam.manager;

import android.widget.SeekBar;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 03.09.13.
 */
public class ManualExposureManager implements IStyleAbleSliderValueHasChanged {

    private CameraManager cameramanager;
    public boolean ExternalSet =false;
    private int minValue = 0;
    private int maxValue = 10;
    private int currentValue = 0;

    public  ManualExposureManager(CameraManager cameraManager)
    {
        this.cameramanager = cameraManager;

    }
    /*@Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (ExternalSet == false && fromUser)
        {

        }
        else
        {
            ExternalSet = false;
        }
    }*/

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

/*    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }*/

    @Override
    public void ValueHasChanged(int value)
    {
        currentValue = value + minValue;
        //if (maxValue < 61)
        //{
        if (currentValue >= cameramanager.parametersManager.manualExposure.getMin() && currentValue <= cameramanager.parametersManager.manualExposure.getMax())
        {
            cameramanager.parametersManager.manualExposure.set(currentValue);
        }
    }
}
