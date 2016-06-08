/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.DeviceUtils.Devices;
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
    public NightModeParameter(Parameters parameters, CameraHolder parameterChanged, String values, CameraUiWrapper cameraUiWrapper) {
        super(parameters, parameterChanged, "", "");
        if (cameraHolder.appSettingsManager.getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.appSettingsManager.getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.appSettingsManager.getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.appSettingsManager.getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.appSettingsManager.getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.appSettingsManager.getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraUiWrapper.appSettingsManager.getDevice() == Devices.Xiaomi_RedmiNote)
        {
            isSupported = true;
        }
        if (isSupported) {
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
            cameraUiWrapper.parametersHandler.PictureFormat.addEventListner(this);
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
        if (cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI3W
                || cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI4W
                || cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraHolder.appSettingsManager.getDevice() == Devices.Xiaomi_RedmiNote)
        {
            if (valueToSet.equals("on")) {
                cameraHolder.GetParameterHandler().morphoHDR.SetValue("false", true);
                cameraHolder.GetParameterHandler().HDRMode.BackgroundValueHasChanged("off");
                cameraHolder.GetParameterHandler().AE_Bracket.SetValue("AE-Bracket", true);
                parameters.set("morpho-hht", "true");
            } else {
                parameters.set("ae-bracket-hdr", "Off");
                parameters.set("morpho-hht", "false");
            }
        }
        else
            parameters.set("night_key", valueToSet);
        try {
            cameraHolder.SetCameraParameters(parameters);
            BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

    @Override
    public String GetValue() {
        if (cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI3W
                || cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI4W
                || cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraHolder.appSettingsManager.getDevice() == Devices.Xiaomi_RedmiNote)
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
        if (cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI3W
                || cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI4W
                || cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraHolder.appSettingsManager.getDevice() == Devices.Xiaomi_RedmiNote)
            return new String[] {"off","on"};
        else
            return new String[] {"off","on","tripod"};
    }

    @Override
    public void ModuleChanged(String module)
    {
        if(cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI3W
                || cameraHolder.appSettingsManager.getDevice() == Devices.XiaomiMI4W)
        {
            curmodule = module;
            switch (module)
            {
                case KEYS.MODULE_VIDEO:
                case KEYS.MODULE_HDR:
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
        if (val.contains(KEYS.JPEG)&&!visible&&!curmodule.equals(KEYS.MODULE_HDR))
            Show();

        else if (!val.contains(KEYS.JPEG)&&visible) {
            if (cameraHolder.appSettingsManager.getDevice() == Devices.ZTEADVIMX214
                    || cameraHolder.appSettingsManager.getDevice() == Devices.ZTE_ADV
                    || cameraHolder.appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
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