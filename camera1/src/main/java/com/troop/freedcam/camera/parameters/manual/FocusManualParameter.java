package com.troop.freedcam.camera.parameters.manual;

import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.DeviceUtils.Devices;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameter extends  BaseManualParameter
{
    I_CameraHolder baseCameraHolder;

    CamParametersHandler camParametersHandlerx;

    private static String TAG ="freedcam.ManualFocus";

    private String manualFocusModeString;

    public FocusManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler,1);
        this.baseCameraHolder = cameraHolder;

        camParametersHandlerx = (CamParametersHandler) camParametersHandler;

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES)  || DeviceUtils.IS(Devices.RedmiNote)|| DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) )
        {
            this.isSupported = true;
            this.max_value = null;
            this.value = "manual-focus-position";
            this.min_value = null;
        }
        else if( DeviceUtils.IS(DeviceUtils.Devices.Moto_MSM8982_8994))
        {
            this.isSupported = true;
        }
        else if (DeviceUtils.IS(Devices.Alcatel_Idol3))
        {
            this.isSupported = true;
            this.max_value = "max-focus-pos-ratio";
            this.value = "cur-focus-scale";
            this.min_value = "min-focus-pos-ratio";
        }
        else if(DeviceUtils.isLenovoK920() || DeviceUtils.IS(Devices.SonyM4_QC))
        {
            this.isSupported = true;
            this.max_value = "max-focus-pos-index";
            this.value = "manual-focus-position";
            this.min_value = "min-focus-pos-index";
        }
        else
            this.isSupported = false;
        isVisible = isSupported;

        if (isSupported)
        {
            int max = 0;
            step = 1;
            if (max_value == null)
            {
                if (DeviceUtils.IS_DEVICE_ONEOF(new DeviceUtils.Devices[]{DeviceUtils.Devices.Moto_MSM8982_8994, DeviceUtils.Devices.XiaomiMI3W, DeviceUtils.Devices.XiaomiMI4W})) {
                    max = 1000;
                    step = 10;
                }
                else
                    max = 79;
            }
            else {
                try {
                    max = Integer.parseInt(parameters.get(max_value));
                } catch (NumberFormatException ex) {
                    max = 0;
                }
            }
            stringvalues = createStringArray(0,max,step);
        }
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add("Auto");
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public boolean IsVisible() {
        return super.IsSupported();
    }

    @Override
    protected void setvalue(final int valueToSet)
    {
        currentInt = valueToSet;
        //check/set auto/manual mode
        if (DeviceUtils.IS_DEVICE_ONEOF(new DeviceUtils.Devices[]
                {
                        Devices.ZTE_ADV, Devices.ZTEADVIMX214, Devices.ZTEADV234, Devices.XiaomiMI3W, Devices.XiaomiMI4W, Devices.RedmiNote, Devices.LenovoK920,Devices.SonyM4_QC
                }))
        {
            if(valueToSet != 0)
            {
                if (!camParametersHandler.FocusMode.GetValue().equals("manual")) //do not set "manual" to "manual"
                    camParametersHandler.FocusMode.SetValue("manual", false);
                if (DeviceUtils.IS(Devices.SonyM4_QC))
                    parameters.put("manual-focus-pos-type", "0");
                else
                    parameters.put("manual-focus-pos-type", "1");
                camParametersHandler.SetParametersToCamera(parameters);
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);


        }
        else if (DeviceUtils.IS_DEVICE_ONEOF(new Devices[]{ Devices.Alcatel_Idol3, Devices.Moto_MSM8982_8994}))
        {
            if(valueToSet != 0)
            {
                try {
                    camParametersHandler.FocusMode.SetValue("manual", true);
                    parameters.put("manual-focus-pos-type", "2");
                }
                catch (Exception ex)
                {
                    Logger.e(TAG, ex.getMessage());
                }
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);

        }

        //set value when no auto mode
        if (value != null && !value.equals("") && valueToSet != 0)
        {
            if(DeviceUtils.IS(Devices.ZTE_ADV))
            {
                setZteadvValue(valueToSet);
            }
            else
            {
                parameters.put(value, stringvalues[currentInt]);
                camParametersHandler.SetParametersToCamera(parameters);
            }
        }

    }

    private void setZteadvValue(final int valueToSet) {
        try
        {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    camParametersHandlerx.setString("manual-focus-position", (stringvalues[currentInt]) + "");
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
