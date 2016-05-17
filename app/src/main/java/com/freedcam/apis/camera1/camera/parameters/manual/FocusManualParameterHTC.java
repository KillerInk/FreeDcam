package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterHTC extends  BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private final String TAG =FocusManualParameterHTC.class.getSimpleName();

    public FocusManualParameterHTC(Camera.Parameters parameters, String maxValue, String MinValue, I_CameraHolder cameraHolder, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.baseCameraHolder = cameraHolder;
        this.isSupported = parameters.get("min-focus") != null && parameters.get("max-focus") != null;
        this.max_value = "max-focus";
        this.value = "focus";
        this.min_value = "min-focus";
        parameters.set(value,"0");
        isVisible = isSupported;
        if (isSupported)
        {
            stringvalues = createStringArray(Integer.parseInt(parameters.get(min_value)),Integer.parseInt(parameters.get(max_value)),1);
        }
    }

    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        ar.add("auto");
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }


    @Override
    protected void setvalue(int valueToSet)
    {
        if(valueToSet != 0)
        {
            parameters.set(value, stringvalues[valueToSet]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
        else if (valueToSet == 0)
        {
            parameters.set(value, valueToSet+"");
            camParametersHandler.FocusMode.SetValue("auto", true);
        }
    }

}
