package com.troop.freedcam.camera.parameters.manual;

import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameter extends  BaseManualParameter
{
    I_CameraHolder baseCameraHolder;

    CamParametersHandler camParametersHandlerx;

    private static String TAG ="freedcam.ManualFocus";

    public FocusManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.baseCameraHolder = cameraHolder;

        camParametersHandlerx = (CamParametersHandler) camParametersHandler;

        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234() || DeviceUtils.isRedmiNote()|| DeviceUtils.isXiaomiMI3W())
        {
            this.isSupported = true;
            this.max_value = null;
            this.value = "manual-focus-position";
            this.min_value = null;
        }
        else if( DeviceUtils.isMoto_MSM8982_8994())
        {
            this.isSupported = true;
        }
        else if (DeviceUtils.isAlcatel_Idol3() )
        {
            this.isSupported = true;
            this.max_value = "max-focus-pos-ratio";
            this.value = "cur-focus-scale";
            this.min_value = "min-focus-pos-ratio";
        }
        else
            this.isSupported = false;
}

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public int GetMaxValue()
    {
        if (max_value == null)
            if (DeviceUtils.isMoto_MSM8982_8994() || DeviceUtils.isXiaomiMI3W())
                return 100;
            else
                return 79;
        else {
            try {
                return Integer.parseInt(parameters.get(max_value));
            } catch (NumberFormatException ex) {
                return 0;
            }
        }

    }
// HTC Focus Step "focus-step"
    @Override
    public int GetMinValue()
    {
        if (min_value == null)
            if (DeviceUtils.isMoto_MSM8982_8994())
                return 100;
            else
                return -1;
        else
            return Integer.parseInt(parameters.get(min_value));
    }
//m8 Step Value
    @Override
    public int GetValue()
    {
        try {
            if (DeviceUtils.isXiaomiMI3W())
                return 100-(Integer.parseInt(parameters.get(value))/10);
            else
                return Integer.parseInt(parameters.get(value));
        }
        catch (Exception ex)
        {
            Log.e(TAG, "get ManualFocus value failed");
            return 0;
        }
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234() || DeviceUtils.isXiaomiMI3W() || DeviceUtils.isRedmiNote())
        {
            if(valueToSet != -1)
            {
                if (!camParametersHandler.FocusMode.GetValue().equals("manual")) //do not set "manual" to "manual"

                    camParametersHandler.FocusMode.SetValue("manual", true);
                    //if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234())

//                if(!parameters.get("manual-focus-pos-type").equals("1"))
                    parameters.put("manual-focus-pos-type", "1");

            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);
            camParametersHandler.SetParametersToCamera();

        }
        else if (DeviceUtils.isAlcatel_Idol3() ||DeviceUtils.isMoto_MSM8982_8994())
        {
            if(valueToSet != -1)
            {
                try {
                    camParametersHandler.FocusMode.SetValue("manual", true);
                    parameters.put("manual-focus-pos-type", "2");
                }
                catch (Exception ex)
                {
                    System.out.println("Freedcam Error Settings Manual Focus SD64 HAL trying test 2"+ ex.toString());
                    try {
                        System.out.println("Freedcam Error Settings Manual Focus SD64 HAL trying test 2"+ ex.toString());
                    }
                    catch (Exception e)
                    {
                        System.out.println("Freedcam Error Settings Manual Focus SD64 HAL Test 2 Failure"+ ex.toString());
                    }
                }
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);

        }
        if (value != null && !value.equals("") && valueToSet != -1)
        {
            if (DeviceUtils.isXiaomiMI3W())
                parameters.put(value, String.valueOf((100-valueToSet)*10));
            else
                parameters.put(value, valueToSet + "");

        }

        if(DeviceUtils.isZTEADV()) {
            camParametersHandlerx.setString("manual-focus-position", valueToSet + "");
            baseCameraHolder.SetCameraParameters(camParametersHandlerx.getParameters());
        }
        else
            camParametersHandler.SetParametersToCamera();

    }
    /* HTC M8 value -1 disable mf
     *  May have to use this key "non-zsl-manual-mode" set to true
     * 
     * 
     * 
     * 
     */
}
