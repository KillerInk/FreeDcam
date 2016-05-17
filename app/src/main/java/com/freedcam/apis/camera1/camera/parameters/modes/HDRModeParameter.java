package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ar4eR on 02.02.16.
 */
public class HDRModeParameter extends BaseModeParameter
{
    final String TAG = HDRModeParameter.class.getSimpleName();
    private boolean visible = true;
    private boolean supportauto = false;
    private boolean supporton = false;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public HDRModeParameter(Handler handler, Camera.Parameters parameters, CameraHolderApi1 parameterChanged, String values, CameraUiWrapper cameraUiWrapper) {
        super(handler, parameters, parameterChanged, "", "");

        this.isSupported = false;
        if ((DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)
                ||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)
                ||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote)
                || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3)
                || DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV)) || DeviceUtils.IS(DeviceUtils.Devices.Htc_M8))
        {
                this.isSupported = true;
        }
        else
        {
            if (parameters.get("auto-hdr-supported")!=null)
                this.isSupported = false;
            String autohdr = parameters.get("auto-hdr-supported");
            if (autohdr != null && !autohdr.equals("") && autohdr.equals("true")) {
                try {
                    List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get("scene-mode-values").split(",")));
                    if (Scenes.contains("hdr")) {
                        supporton = true;
                        this.isSupported = true;
                    }
                    if (Scenes.contains("asd")) {
                        supportauto = true;
                        this.isSupported = true;
                    }

                } catch (Exception ex) {
                    this.isSupported = false;
                }
            }
            else
                this.isSupported = false;
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
    public void SetValue(String valueToSet, boolean setToCam) {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)
                ||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)
                ||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote))
        {
            if (valueToSet.equals("on")) {
                cameraHolderApi1.GetParameterHandler().morphoHHT.SetValue("false", true);
                cameraHolderApi1.GetParameterHandler().NightMode.BackgroundValueHasChanged("off");
                cameraHolderApi1.GetParameterHandler().AE_Bracket.SetValue("AE-Bracket", true);
                parameters.set("morpho-hdr", "true");
            } else {
                parameters.set("ae-bracket-hdr", "Off");
                parameters.set("morpho-hdr", "false");
            }
        }
        else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
        {
            switch (valueToSet)
            {
                case "on":
                    parameters.set("hdr-mode", "1");
                    break;
                case "off":
                    parameters.set("hdr-mode", "0");
                    break;
                case "auto":
                    parameters.set("hdr-mode", "2");
            }
        }
        else {
            switch (valueToSet) {
                case "off":
                    parameters.set("scene-mode", "auto");
                    parameters.set("auto-hdr-enable", "disable");
                    break;
                case "on":
                    parameters.set("scene-mode", "hdr");
                    parameters.set("auto-hdr-enable", "enable");
                    break;
                case "auto":
                    parameters.set("scene-mode", "asd");
                    parameters.set("auto-hdr-enable", "enable");
                    break;
            }
        }
        try {
            cameraHolderApi1.SetCameraParameters(parameters);
            super.BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        firststart = false;
    }

    @Override
    public String GetValue() {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)
                ||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)
                ||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote)) {
            if (parameters.get("morpho-hdr").equals("true") && parameters.get("ae-bracket-hdr").equals("AE-Bracket"))
                return "on";
            else
                return "off";
        }
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
        {
            if (parameters.get("hdr-mode")== null)
                parameters.set("hdr-mode", "0");
            if (parameters.get("hdr-mode").equals("0"))
                return "off";
            else if (parameters.get("hdr-mode").equals("1"))
                return "on";
            else
                return "auto";
        }
        else if(parameters.get("auto-hdr-enable")!= null)
        {
            if (parameters.get("auto-hdr-enable").equals("enable") && parameters.get("scene-mode").equals("hdr"))
                return "on";
            else if (parameters.get("auto-hdr-enable").equals("enable") && parameters.get("scene-mode").equals("asd"))
                return "auto";
            else
                return "off";
        }
        else
            return "off";
    }

    @Override
    public String[] GetValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add("off");
            if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
            {
                hdrVals.add("on");
            }
            else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV)) {
                hdrVals.add("on");
                hdrVals.add("auto");
            }
            else  {
                if (supporton)
                    hdrVals.add("on");
                if (supportauto)
                    hdrVals.add("auto");
            }
        return hdrVals.toArray(new String[hdrVals.size()]);
    }

    @Override
    public void ModuleChanged(String module)
    {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4) || supportauto || supporton) {
            curmodule = module;
            switch (module)
            {
                case AbstractModuleHandler.MODULE_VIDEO:
                case AbstractModuleHandler.MODULE_HDR:
                    Hide();
                    SetValue("off",true);
                    break;
                default:
                    if (format.contains("jpeg")) {
                        Show();
                        BackgroundIsSupportedChanged(true);
                    }
                    else
                    {
                        Hide();
                        SetValue("off",true);
                    }
            }
        }
    }

    @Override
    public void onValueChanged(String val)
    {
        format = val;
        if (val.contains("jpeg")&&!visible&&!curmodule.equals(AbstractModuleHandler.MODULE_HDR))
            Show();

        else if (!val.contains("jpeg")&&visible) {
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
