package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public abstract class BaseManualParameter extends AbstractManualParameter
{

    private static String TAG = StringUtils.TAG + BaseManualParameter.class.getSimpleName();
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

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    public int GetMaxValue()
    {
        int max = 100;
        try {
            max = Integer.parseInt(parameters.get(max_value));
        }
        catch (Exception ex)
        {}
        Log.d(TAG, "get " + max_value + " to " + parameters.get(max_value));
        return max;
    }

    public  int GetMinValue()
    {
        int ret = 0;
        try
        {
            Log.d(TAG, "get " + min_value + " to " + parameters.get(min_value));
            ret = Integer.parseInt(parameters.get(min_value));
        }
        catch (Exception ex)
        {
            ret = 0;
            Log.d(TAG, "get " + min_value + " to " + 0);
        }
        return ret;
    }

    public int GetValue()
    {
        if (parameters == null || value == null)
            return 0;
        Log.d(TAG, "get " + value + ": " +parameters.get(value));
        try {
            return Integer.parseInt(parameters.get(value));
        }
        catch (NumberFormatException ex)
        {
            ex.printStackTrace();
            return 0;
        }

    }

    @Override
    protected void setvalue(int valueToset)
    {
        Log.d(TAG, "set " + value + " to " + valueToset);
        parameters.put(value, valueToset + "");
        try
        {
            camParametersHandler.SetParametersToCamera();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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
        Log.d(TAG, "issupported " + value + ": " + isSupported);
        return isSupported;
    }

    @Override
    public void RestartPreview() {

    }
}
