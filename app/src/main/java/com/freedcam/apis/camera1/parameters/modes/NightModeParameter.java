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

package com.freedcam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
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
    public NightModeParameter(Parameters parameters, I_CameraUiWrapper parameterChanged, String values, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, parameterChanged, "", "");
        if (parameterChanged.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote)
        {
            isSupported = true;
        }
        if (isSupported) {
            cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(this);
            cameraUiWrapper.GetParameterHandler().PictureFormat.addEventListner(this);
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
        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote)
        {
            if (valueToSet.equals("on")) {
                cameraUiWrapper.GetParameterHandler().morphoHDR.SetValue("false", true);
                cameraUiWrapper.GetParameterHandler().HDRMode.BackgroundValueHasChanged("off");
                cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue("AE-Bracket", true);
                parameters.set("morpho-hht", "true");
            } else {
                parameters.set("ae-bracket-hdr", "Off");
                parameters.set("morpho-hht", "false");
            }
        }
        else
            parameters.set("night_key", valueToSet);
        try {
            ((ParametersHandler)cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
            BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

    @Override
    public String GetValue() {
        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote)
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
        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote)
            return new String[] {"off","on"};
        else
            return new String[] {"off","on","tripod"};
    }

    @Override
    public void ModuleChanged(String module)
    {
        if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W)
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
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214)
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