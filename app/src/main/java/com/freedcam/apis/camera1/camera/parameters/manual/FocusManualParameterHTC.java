package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;

import java.util.ArrayList;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterHTC extends  BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private final String TAG =FocusManualParameterHTC.class.getSimpleName();

    public FocusManualParameterHTC(Camera.Parameters parameters,I_CameraHolder cameraHolder, ParametersHandler parametersHandler) {
        super(parameters, "", "", "", parametersHandler,1);
        this.baseCameraHolder = cameraHolder;
        this.isSupported = parameters.get(KEYS.MIN_FOCUS) != null && parameters.get(KEYS.MAX_FOCUS) != null;
        this.key_max_value = KEYS.MAX_FOCUS;
        this.key_value = KEYS.FOCUS;
        this.key_min_value = KEYS.MIN_FOCUS;
        parameters.set(key_value,"0");
        isVisible = isSupported;
        if (isSupported)
        {
            stringvalues = createStringArray(Integer.parseInt(parameters.get(key_min_value)),Integer.parseInt(parameters.get(key_max_value)),1);
        }
    }

    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(KEYS.AUTO);
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }


    @Override
    public void SetValue(int valueToSet)
    {
        if(valueToSet != 0)
        {
            parameters.set(key_value, stringvalues[valueToSet]);
            parametersHandler.SetParametersToCamera(parameters);
        }
        else if (valueToSet == 0)
        {
            parameters.set(key_value, valueToSet+"");
            parametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
        }
    }

}
