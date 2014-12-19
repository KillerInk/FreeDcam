package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.I_ManualParameter;

/**
 * Created by troop on 17.08.2014.
 */
public abstract class BaseManualParameter extends AbstractManualParameter {
    Camera.Parameters parameters;
    protected String value;
    protected String max_value;
    protected String  min_value;

    boolean isSupported = false;


    public BaseManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
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
        return parameters.getInt(max_value);
    }

    public  int GetMinValue()
    {
        int ret = 0;
        try {
            ret = parameters.getInt(min_value);
        }
        catch (Exception ex)
        {
            ret = 0;
        }
        return ret;
    }

    public int GetValue()
    {
        return parameters.getInt(value);
    }

    @Override
    protected void setvalue(int valueToset) {
        parameters.set(value, valueToset);
        camParametersHandler.SetParametersToCamera();
    }

    protected boolean hasSupport()
    {
        try
        {
            parameters.getInt(value);
            isSupported = true;
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
