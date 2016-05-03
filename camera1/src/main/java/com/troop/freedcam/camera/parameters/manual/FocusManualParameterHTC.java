package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterHTC extends  BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private final String TAG =FocusManualParameterHTC.class.getSimpleName();

    public FocusManualParameterHTC(HashMap<String, String> parameters, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.baseCameraHolder = cameraHolder;
        this.isSupported = parameters.containsKey("min-focus") && parameters.containsKey("max-focus");
        this.max_value = "max-focus";
        this.value = "focus";
        this.min_value = "min-focus";
        parameters.put("","0");
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
            parameters.put(value, stringvalues[valueToSet]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
        else if (valueToSet == 0)
        {
            parameters.put(value, valueToSet+"");
            camParametersHandler.FocusMode.SetValue("auto", true);
        }
    }

}
