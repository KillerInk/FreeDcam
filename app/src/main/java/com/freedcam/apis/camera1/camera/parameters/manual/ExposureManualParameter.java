package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    private final String TAG = ExposureManualParameter.class.getSimpleName();
    public ExposureManualParameter(Camera.Parameters parameters, CamParametersHandler camParametersHandler, float step) {
        super(parameters,"", "", "", camParametersHandler,step);
        stringvalues = createStringArray(parameters.getMinExposureCompensation(),parameters.getMaxExposureCompensation(),parameters.getExposureCompensationStep());
        isSupported = true;
        isVisible = true;
        Logger.d(TAG, "Is Supported:" + isSupported);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    protected String[] createStringArray(int min,int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        for (int i = min; i <= max; i++)
        {
            String s = String.format("%.1f",i*step );
            ar.add(s);
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public void SetValue(int valueToset)
    {
        if(stringvalues == null || stringvalues.length == 0)
            return;
        currentInt = valueToset-(stringvalues.length/2);
        parameters.setExposureCompensation(currentInt);
        try
        {
            camParametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        ThrowCurrentValueChanged(currentInt);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);
    }

    @Override
    public int GetValue() {
        return currentInt+stringvalues.length/2;
    }

    @Override
    public String GetStringValue() {
        return stringvalues[currentInt+stringvalues.length/2];
    }
}
