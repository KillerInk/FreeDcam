package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeParameter extends BaseModeParameter
{

    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";
    public NightModeParameter(Handler handler,HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values, CameraUiWrapper cameraUiWrapper) {
        super(handler, parameters, parameterChanged, value, values);

        this.isSupported = false;
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
            this.isSupported = true;
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
        {
            this.isSupported = true;
        }
        if (isSupported) {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
            cameraUiWrapper.camParametersHandler.PictureFormat.addEventListner(this);
        }

    }

    @Override
    public boolean IsSupported()
    {
        return  isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
        {
            if (valueToSet.equals("on")) {
                baseCameraHolder.ParameterHandler.morphoHDR.SetValue("false", true);
                baseCameraHolder.ParameterHandler.HDRMode.BackgroundValueHasChanged("off");
                baseCameraHolder.ParameterHandler.AE_Bracket.SetValue("AE-Bracket", true);
                parameters.put("morpho-hht", "true");
            } else {
                parameters.put("ae-bracket-hdr", "Off");
                parameters.put("morpho-hht", "false");
            }
        }
        else
            parameters.put("night_key", valueToSet);
        try {
            baseCameraHolder.SetCameraParameters(parameters);
            super.BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        firststart = false;
    }

    @Override
    public String GetValue() {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
        {
            if (parameters.get("morpho-hht").equals("true") && parameters.get("ae-bracket-hdr").equals("AE-Bracket"))
                return "on";
            else
                return "off";
        }
        else
            return parameters.get("night_key");
    }

    @Override
    public String[] GetValues() {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
            return new String[] {"off","on"};
        else
            return new String[] {"off","on","tripod"};
    }

    @Override
    public String ModuleChanged(String module)
    {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
        {
            curmodule = module;
            switch (module)
            {
                case AbstractModuleHandler.MODULE_VIDEO:
                case AbstractModuleHandler.MODULE_HDR:
                    Hide();
                    break;
                default:
                    if (format.contains("jpeg")) {
                        Show();
                        BackgroundIsSupportedChanged(true);
                    }
            }
        }
        return null;
    }

    @Override
    public void onValueChanged(String val)
    {
        format = val;
        if (val.contains("jpeg")&&!visible&&!curmodule.equals(AbstractModuleHandler.MODULE_HDR))
            Show();

        else if (!val.contains("jpeg")&&visible) {
            if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
                Show();
            else
                Hide();
        }
    }

    private void Hide()
    {
        state = GetValue();
        visible = false;
        SetValue("off",true);
        BackgroundValueHasChanged("off");
        BackgroundIsSupportedChanged(visible);
    }
    private void Show()
    {
        visible = true;
        SetValue(state,true);
        BackgroundValueHasChanged(state);
        BackgroundIsSupportedChanged(visible);
    }

}