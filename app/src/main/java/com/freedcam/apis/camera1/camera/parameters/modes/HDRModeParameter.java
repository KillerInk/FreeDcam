package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
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

    public HDRModeParameter(Camera.Parameters parameters, CameraHolderApi1 parameterChanged, String values, CameraUiWrapper cameraUiWrapper) {
        super(parameters, parameterChanged, "", "");

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
            if (parameters.get(KEYS.AUTO_HDR_SUPPORTED)!=null)
                this.isSupported = false;
            String autohdr = parameters.get(KEYS.AUTO_HDR_SUPPORTED);
            if (autohdr != null && !autohdr.equals("") && autohdr.equals(KEYS.TRUE)) {
                try {
                    List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(KEYS.SCENE_MODE_VALUES).split(",")));
                    if (Scenes.contains(KEYS.SCENE_MODE_VALUES_HDR)) {
                        supporton = true;
                        this.isSupported = true;
                    }
                    if (Scenes.contains(KEYS.SCENE_MODE_VALUES_ASD)) {
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
            if (valueToSet.equals(KEYS.ON)) {
                cameraHolderApi1.GetParameterHandler().morphoHHT.SetValue(KEYS.FALSE, true);
                cameraHolderApi1.GetParameterHandler().NightMode.BackgroundValueHasChanged(KEYS.OFF);
                cameraHolderApi1.GetParameterHandler().AE_Bracket.SetValue(KEYS.AE_BRACKET_HDR_VALUES_AE_BRACKET, true);
                parameters.set(KEYS.MORPHO_HDR, KEYS.TRUE);
            } else {
                cameraHolderApi1.GetParameterHandler().AE_Bracket.SetValue(KEYS.AE_BRACKET_OFF, true);
                parameters.set(KEYS.MORPHO_HDR, KEYS.FALSE);
            }
        }
        else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3))
        {
            switch (valueToSet)
            {
                case KEYS.ON:
                    parameters.set(KEYS.HDR_MODE, 1);
                    break;
                case KEYS.OFF:
                    parameters.set(KEYS.HDR_MODE, 0);
                    break;
                case KEYS.AUTO:
                    parameters.set(KEYS.HDR_MODE, 2);
            }
        }
        else {
            switch (valueToSet) {
                case KEYS.OFF:
                    parameters.set(KEYS.SCENE_MODE, KEYS.AUTO);
                    parameters.set(KEYS.AUTO_HDR_ENABLE, KEYS.DISABLE);
                    break;
                case KEYS.ON:
                    parameters.set(KEYS.SCENE_MODE, KEYS.SCENE_MODE_VALUES_HDR);
                    parameters.set(KEYS.AUTO_HDR_ENABLE, KEYS.ENABLE);
                    break;
                case KEYS.AUTO:
                    parameters.set(KEYS.SCENE_MODE, KEYS.SCENE_MODE_VALUES_ASD);
                    parameters.set(KEYS.AUTO_HDR_ENABLE, KEYS.ENABLE);
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
    }

    @Override
    public String GetValue() {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)
                ||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro)
                ||DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote)) {
            if (parameters.get(KEYS.MORPHO_HDR).equals(KEYS.TRUE) && parameters.get(KEYS.AE_BRACKET_HDR).equals(KEYS.AE_BRACKET_HDR_VALUES_AE_BRACKET))
                return KEYS.ON;
            else
                return KEYS.OFF;
        }
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
        {
            if (parameters.get(KEYS.HDR_MODE)== null)
                parameters.set(KEYS.HDR_MODE, "0");
            if (parameters.get(KEYS.HDR_MODE).equals("0"))
                return KEYS.OFF;
            else if (parameters.get(KEYS.HDR_MODE).equals("1"))
                return KEYS.ON;
            else
                return KEYS.AUTO;
        }
        else if(parameters.get(KEYS.AUTO_HDR_ENABLE)!= null)
        {
            if (parameters.get(KEYS.AUTO_HDR_ENABLE).equals(KEYS.ENABLE) && parameters.get(KEYS.SCENE_MODE).equals(KEYS.SCENE_MODE_VALUES_HDR))
                return KEYS.ON;
            else if (parameters.get(KEYS.AUTO_HDR_ENABLE).equals(KEYS.ENABLE) && parameters.get(KEYS.SCENE_MODE).equals(KEYS.SCENE_MODE_VALUES_ASD))
                return KEYS.AUTO;
            else
                return KEYS.OFF;
        }
        else
            return KEYS.OFF;
    }

    @Override
    public String[] GetValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(KEYS.OFF);
            if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
            {
                hdrVals.add(KEYS.ON);
            }
            else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV)) {
                hdrVals.add(KEYS.ON);
                hdrVals.add(KEYS.AUTO);
            }
            else  {
                if (supporton)
                    hdrVals.add(KEYS.ON);
                if (supportauto)
                    hdrVals.add(KEYS.AUTO);
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
                    SetValue(KEYS.OFF,true);
                    break;
                default:
                    if (format.contains(KEYS.JPEG)) {
                        Show();
                        BackgroundIsSupportedChanged(true);
                    }
                    else
                    {
                        Hide();
                        SetValue(KEYS.OFF,true);
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
            Hide();
        }
    }

    private void Hide()
    {
        state = GetValue();
        visible = false;
        SetValue(KEYS.OFF,true);
        BackgroundValueHasChanged(KEYS.OFF);
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
