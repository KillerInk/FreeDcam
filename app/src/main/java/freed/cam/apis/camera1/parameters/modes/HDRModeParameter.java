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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.DeviceUtils.Devices;
import freed.utils.Logger;

/**
 * Created by Ar4eR on 02.02.16.
 */
public class HDRModeParameter extends BaseModeParameter
{
    final String TAG = HDRModeParameter.class.getSimpleName();
    private boolean visible = true;
    private boolean supportauto;
    private boolean supporton;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public HDRModeParameter(Parameters parameters,CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        this.cameraUiWrapper = cameraUiWrapper;
        isSupported = false;
        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                ||cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI_Note_Pro
                ||cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Htc_M8)
        {
            isSupported = true;
        }
        else
        {
            if (parameters.get(KEYS.AUTO_HDR_SUPPORTED)!=null)
                isSupported = false;
            String autohdr = parameters.get(KEYS.AUTO_HDR_SUPPORTED);
            if (autohdr != null && !autohdr.equals("") && autohdr.equals(KEYS.TRUE)) {
                try {
                    List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(KEYS.SCENE_MODE_VALUES).split(",")));
                    if (Scenes.contains(KEYS.SCENE_MODE_VALUES_HDR)) {
                        supporton = true;
                        isSupported = true;
                    }
                    if (Scenes.contains(KEYS.SCENE_MODE_VALUES_ASD)) {
                        supportauto = true;
                        isSupported = true;
                    }

                } catch (Exception ex) {
                    isSupported = false;
                }
            }
            else
                isSupported = false;
        }
        if (isSupported) {
            cameraUiWrapper.GetModuleHandler().addListner(this);
            cameraUiWrapper.GetParameterHandler().PictureFormat.addEventListner(this);
        }

    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote)
        {
            if (valueToSet.equals(KEYS.ON)) {
                parameters.set(KEYS.MORPHO_HHT, KEYS.FALSE);
                cameraUiWrapper.GetParameterHandler().NightMode.BackgroundValueHasChanged(KEYS.OFF);
                parameters.set("capture-burst-exposures","-10,0,10");
                cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue(KEYS.AE_BRACKET_HDR_VALUES_AE_BRACKET, true);
                parameters.set(KEYS.MORPHO_HDR, KEYS.TRUE);
            } else {
                cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue(KEYS.AE_BRACKET_HDR_VALUES_OFF, true);
                parameters.set(KEYS.MORPHO_HDR, KEYS.FALSE);
            }
        }
        else if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3)
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
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
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
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote) {
            if (parameters.get(KEYS.MORPHO_HDR).equals(KEYS.TRUE) && parameters.get(KEYS.AE_BRACKET_HDR).equals(KEYS.AE_BRACKET_HDR_VALUES_AE_BRACKET))
                return KEYS.ON;
            else
                return KEYS.OFF;
        }
        else if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV)
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
            if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W)
            {
                hdrVals.add(KEYS.ON);
            }
            else if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV) {
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
    public void onModuleChanged(String module)
    {
        if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || supportauto
                || supporton) {
            curmodule = module;
            switch (module)
            {
                case KEYS.MODULE_VIDEO:
                case KEYS.MODULE_HDR:
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
    public void onParameterValueChanged(String val)
    {
        format = val;
        if (val.contains(KEYS.JPEG)&&!visible &&!curmodule.equals(KEYS.MODULE_HDR))
            Show();

        else if (!val.contains(KEYS.JPEG)&& visible) {
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
