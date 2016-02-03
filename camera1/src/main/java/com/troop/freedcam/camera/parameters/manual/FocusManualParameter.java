package com.troop.freedcam.camera.parameters.manual;

import android.os.Build;
import android.os.Handler;
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
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234() || DeviceUtils.isRedmiNote()|| DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())

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
        else if(DeviceUtils.isLenovoK920())
        {
            this.isSupported = true;
            this.max_value = "max-focus-pos-index";
            this.value = "manual-focus-position";
            this.min_value = "min-focus-pos-index";
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
            if (DeviceUtils.isMoto_MSM8982_8994() || DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())
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
        try {
            return Integer.parseInt(parameters.get(min_value));
        }
        catch (NumberFormatException ex)
        {
            isSupported = false;
            return  0;
        }

    }
//m8 Step Value
    @Override
    public int GetValue()
    {
        try {
            if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())
                return Integer.parseInt(parameters.get(value))/10;
            else
                return Integer.parseInt(parameters.get(value));
        }
        catch (NumberFormatException ex)
        {
            Log.d(TAG, "get ManualFocus value failed");
            return -1;
        }
    }

    @Override
    protected void setvalue(final int valueToSet)
    {
        //check/set auto/manual mode
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234() || DeviceUtils.isXiaomiMI3W() || DeviceUtils.isRedmiNote()||DeviceUtils.isXiaomiMI4W()
                || DeviceUtils.isLenovoK920())
        {
            if(valueToSet != 0)
            {
                if (!camParametersHandler.FocusMode.GetValue().equals("manual")) //do not set "manual" to "manual"
                    camParametersHandler.FocusMode.SetValue("manual", false);
                parameters.put("manual-focus-pos-type", "1");
                camParametersHandler.SetParametersToCamera();
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);


        }
        else if (DeviceUtils.isAlcatel_Idol3() ||DeviceUtils.isMoto_MSM8982_8994())
        {
            if(valueToSet != 0)
            {
                try {
                    camParametersHandler.FocusMode.SetValue("manual", true);
                    parameters.put("manual-focus-pos-type", "2");
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);

        }

        //set value when no auto mode
        if (value != null && !value.equals("") && valueToSet != 0)
        {
            if(DeviceUtils.isZTEADV())
            {
                setZteadvValue(valueToSet);
            }
            else
            {
                if (DeviceUtils.isXiaomiMI3W() || DeviceUtils.isXiaomiMI4W())
                    parameters.put(value, String.valueOf((valueToSet - 1) * 10));
                else
                    parameters.put(value, (valueToSet - 1) + "");
                camParametersHandler.SetParametersToCamera();
            }
        }

    }

    private void setZteadvValue(final int valueToSet) {
        try
        {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    camParametersHandlerx.setString("manual-focus-position", (valueToSet-1) + "");
                    baseCameraHolder.SetCameraParameters(camParametersHandlerx.getParameters());
                }
            };
            handler.postDelayed(r, 1);

        }
        catch (Exception ex)
        {

        }
    }


}
