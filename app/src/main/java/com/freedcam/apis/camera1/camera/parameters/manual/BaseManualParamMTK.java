package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.PictureFormatHandler;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

/**
 * Created by GeorgeKiarie on 6/3/2016.
 */
public class BaseManualParamMTK extends BaseManualParameter
{
    private static String TAG = BaseManualParamMTK.class.getSimpleName();

    private int default_value = 0;

    public BaseManualParamMTK(Camera.Parameters  parameters, String value, String values, CamParametersHandler camParametersHandler) {
        super(parameters,value,"","",camParametersHandler,1);
        this.camParametersHandler = camParametersHandler;
        this.parameters = parameters;
        this.value = value;
        //mtk stores that stuff like that brightness-values=low,middle,high
        if (parameters.get(values)!= null)
        {
            //get values
            stringvalues = parameters.get(values).split(",");
            String val = parameters.get(value);
            //lookup current value
            for (int i = 0; i < stringvalues.length; i++)
            {
                if (val.equals(stringvalues[i]))
                    currentInt = i;
            }
            isSupported = true;
            isVisible =true;
        }
    }

    @Override
    public void SetValue(int valueToset)
    {
        currentInt = valueToset;
        Logger.d(TAG, "set " + value + " to " + valueToset);
        if(stringvalues == null || stringvalues.length == 0)
            return;
        parameters.set(value, stringvalues[valueToset]);
        ThrowCurrentValueChanged(valueToset);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);
        try
        {
            camParametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

}