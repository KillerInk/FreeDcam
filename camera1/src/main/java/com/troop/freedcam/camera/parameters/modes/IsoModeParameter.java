package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class IsoModeParameter extends BaseModeParameter
{
    I_CameraHolder baseCameraHolder;

    public IsoModeParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values)
    {
        super(handler, parameters, parameterChanged, value, values);
        isIso();
    }

    public IsoModeParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder)
    {
        super(handler, parameters, parameterChanged, value, values);
        isIso();
        this.baseCameraHolder = baseCameraHolder;
    }

    private void isIso()
    {
       // DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994)



            try {
                String isomodes = parameters.get("sony-iso-values");
                if (isomodes != null && !isomodes.equals("")) {
                    this.value = "sony-iso";
                    this.values = "sony-iso-values";
                    isSupported = true;
                }
            } catch (Exception ex) {}

        if(!isSupported)
        {
            try {

                String isomodes = parameters.get("lg-iso-values");
                if (isomodes != null && !isomodes.equals("")) {
                    this.value = "iso";
                    this.values = "lg-iso-values";
                    isSupported = true;
                }
            } catch (Exception ex) {}
        }


        if(!isSupported)
        {
            try {
                String isomodes = parameters.get("iso-speed-values");
                if (isomodes != null && !isomodes.equals("")) {
                    this.value = "iso-speed";
                    this.values = "iso-speed-values";
                    isSupported = true;
                }
            } catch (Exception ex) {}
        }

        if (!isSupported)
        {
            try {
                String isomodes = parameters.get("iso-values");
                if (isomodes != null && !isomodes.equals("")) {
                    this.value = "iso";
                    this.values = "iso-values";
                    isSupported = true;
                }
            } catch (Exception ex) {}
        }
        if(!isSupported)
        {
            try
            {
                String isomodes = parameters.get("iso-mode-values");
                if (isomodes != null && !isomodes.equals("")) {
                    this.value = "iso";
                    this.values = "iso-mode-values";
                    isSupported = true;
                }
            }
            catch (Exception ex){}
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam)
        {
           // if(DeviceUtils.isG4())
                //need to add "lge-camera"
            super.SetValue(valueToSet, setToCam);
            if (!firststart) {
                baseCameraHolder.StopPreview();
                baseCameraHolder.StartPreview();

            }
            firststart = false;
        }
        else
            super.SetValue(valueToSet, setToCam);
    }
}
