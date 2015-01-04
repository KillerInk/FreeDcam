package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.I_ManualParameter;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public abstract class BaseManualParameter extends AbstractManualParameter
{
    /**
     * Holds the list of Supported parameters
     */
    HashMap<String, String> parameters;
    /*
     * The name of the current value to get like brightness
     */
    protected String value;

    /**
     * The name of the current value to get like brightness-max
     */
    protected String max_value;
    /**
     * The name of the current value to get like brightness-min
     */
    protected String  min_value;

    /**
     * holds the state if the parameter is supported
     */
    boolean isSupported = false;


    /**
     *
     * @param @parameters
     * @param @value
     * @param @max_value
     * @param @min_value
     * @param @camParametersHandler
     */
    public BaseManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
    {
        super(camParametersHandler);
        this.parameters = parameters;
        this.value = value;
        this.max_value = maxValue;
        this.min_value = MinValue;
    }


    public boolean IsSupported()
    {
        return isSupported;
    }

    public int GetMaxValue()
    {
        return Integer.parseInt(parameters.get(max_value));
    }

    public  int GetMinValue()
    {
        int ret = 0;
        try {
            ret = Integer.parseInt(parameters.get(min_value));
        }
        catch (Exception ex)
        {
            ret = 0;
        }
        return ret;
    }

    public int GetValue()
    {
        return Integer.parseInt(parameters.get(value));
    }

    @Override
    protected void setvalue(int valueToset)
    {
        parameters.put(value, valueToset +"");
        camParametersHandler.SetParametersToCamera();
    }

    protected boolean hasSupport()
    {
        try
        {
            if (parameters.containsKey(value))
                isSupported = true;
            else
                isSupported = false;
        }
        catch (Exception ex)
        {
            isSupported = false;
        }
        return isSupported;
    }

    @Override
    public void RestartPreview() {

    }
}
