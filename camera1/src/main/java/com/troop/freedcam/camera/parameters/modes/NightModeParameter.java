package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
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
    public NightModeParameter(Handler handler,HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values, CameraUiWrapper cameraUiWrapper) {
        super(handler, parameters, parameterChanged, value, values);

        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());
        cameraUiWrapper.camParametersHandler.PictureFormat.addEventListner(this);

    }

    @Override
    public boolean IsSupported()
    {
        this.isSupported = false;
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
            this.isSupported = true;
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
        {
            if (visible)
                this.isSupported = true;
            else
                this.isSupported = false;
        }
        BackgroundIsSupportedChanged(isSupported);
        return  isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)||DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
        {
            if (valueToSet.equals("on")) {
                baseCameraHolder.ParameterHandler.morphoHDR.SetValue("false", true);
                baseCameraHolder.ParameterHandler.HDRMode.BackgroundValueHasChanged("off");
                parameters.put("ae-bracket-hdr", "AE-Bracket");
                parameters.put("capture-burst-exposures", "-10,0,10");
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
    public String ModuleChanged(String module) {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)) {
            if (module.equals("module_video")|| module.equals("module_hdr"))
            {  if (visible)
                    Hide();
            } else if (!visible && format.contains("jpeg")){
                Show();
            }
        }
        return null;
    }

    @Override
    public void onValueChanged(String val) {
        format = val;
        if (val.contains("jpeg")&&!visible)
            Show();

        else if (!val.contains("jpeg")&&visible)
            Hide();
    }

    private void Hide()
    {
        state = GetValue();
        visible = false;
        this.isSupported = false;
        SetValue("off",true);
        BackgroundValueHasChanged("off");
        BackgroundIsSupportedChanged(isSupported);
    }
    private void Show()
    {
        visible = true;
        this.isSupported = true;
        SetValue(state,true);
        BackgroundValueHasChanged(state);
        BackgroundIsSupportedChanged(isSupported);
    }

}