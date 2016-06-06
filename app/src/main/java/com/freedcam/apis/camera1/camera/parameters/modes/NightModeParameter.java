package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeParameter extends BaseModeParameter
{
    final String TAG = NightModeParameter.class.getSimpleName();
    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";
    public NightModeParameter(Camera.Parameters parameters, CameraHolderApi1 parameterChanged, String values, CameraUiWrapper cameraUiWrapper) {
        super(parameters, parameterChanged, "", "");

        this.isSupported = DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES);
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote))
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
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote))
        {
            if (valueToSet.equals("on")) {
                cameraHolderApi1.GetParameterHandler().morphoHDR.SetValue("false", true);
                cameraHolderApi1.GetParameterHandler().HDRMode.BackgroundValueHasChanged("off");
                cameraHolderApi1.GetParameterHandler().AE_Bracket.SetValue("AE-Bracket", true);
                parameters.set("morpho-hht", "true");
            } else {
                parameters.set("ae-bracket-hdr", "Off");
                parameters.set("morpho-hht", "false");
            }
        }
        else
            parameters.set("night_key", valueToSet);
        try {
            cameraHolderApi1.SetCameraParameters(parameters);
            super.BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

    @Override
    public String GetValue() {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote))
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
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote))
            return new String[] {"off","on"};
        else
            return new String[] {"off","on","tripod"};
    }

    @Override
    public void ModuleChanged(String module)
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
                    if (format.contains(KEYS.JPEG)) {
                        Show();
                        BackgroundIsSupportedChanged(true);
                    }
            }
        }
    }

    @Override
    public void onValueChanged(String val)
    {
        format = val;
        if (val.contains(KEYS.JPEG)&&!visible&&!curmodule.equals(AbstractModuleHandler.MODULE_HDR))
            Show();

        else if (!val.contains(KEYS.JPEG)&&visible) {
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