package com.troop.freecam.manager;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 24.01.14.
 */
public class ManualConvergenceManager implements IStyleAbleSliderValueHasChanged
{
    private int minValue = 0;
    private int maxValue = 10;
    private int currentValue = 0;
    CameraManager cameramanager;

    public ManualConvergenceManager(CameraManager cameraManager)
    {
        this.cameramanager = cameraManager;

    }

    @Override
    public void ValueHasChanged(int value)
    {
        currentValue = value + minValue;
        //if (maxValue < 61)
        //{
        if (currentValue >= cameramanager.parametersManager.manualConvergence.getMin() && currentValue <= cameramanager.parametersManager.manualConvergence.getMax())
        {
            cameramanager.parametersManager.manualConvergence.set(currentValue);
        }

    }

    public  void SetMinMax(int min, int max)
    {
        minValue = min;
        maxValue = max;
        //cameramanager.activity.exposureSeekbar.setMax(max + min * -1);
    }
}
