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

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;

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
            if (parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_supported))!=null)
                isSupported = false;
            String autohdr = parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_supported));
            if (autohdr != null && !autohdr.equals("") && autohdr.equals(cameraUiWrapper.getResString(R.string.true_)) && parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable)) != null) {

                List<String> Scenes = new ArrayList<>(Arrays.asList(parameters.get(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode_values)).split(",")));
                if (Scenes.contains(cameraUiWrapper.getResString(R.string.scene_mode_hdr))) {
                    supporton = true;
                    isSupported = true;
                }
                if (Scenes.contains(cameraUiWrapper.getResString(R.string.scene_mode_asd))) {
                    supportauto = true;
                    isSupported = true;
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
            if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_))) {
                parameters.set(cameraUiWrapper.getResString(R.string.morpho_hht), cameraUiWrapper.getResString(R.string.false_));
                cameraUiWrapper.GetParameterHandler().NightMode.onValueHasChanged(cameraUiWrapper.getResString(R.string.off_));
                parameters.set("capture-burst-exposures","-10,0,10");
                cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.ae_bracket_hdr_values_aebracket), true);
                parameters.set(cameraUiWrapper.getResString(R.string.morpho_hdr), cameraUiWrapper.getResString(R.string.true_));
            } else {
                cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.ae_bracket_hdr_values_off), true);
                parameters.set(cameraUiWrapper.getResString(R.string.morpho_hdr), cameraUiWrapper.getResString(R.string.false_));
            }
        }
        else if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3)
        {
            if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
                parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), 1);
            else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.off_)))
                parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), 0);
            else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.auto_)))
                parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), 2);
        }
        else {

            if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
            {
                parameters.set(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.auto));
                parameters.set(cameraUiWrapper.getResString(R.string.auto_hdr_enable), cameraUiWrapper.getResString(R.string.disable_));

            }
            else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.off_)))
            {
                parameters.set(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.auto));
                parameters.set(cameraUiWrapper.getResString(R.string.auto_hdr_enable), cameraUiWrapper.getResString(R.string.disable_));
            }
            else if (valueToSet.equals(cameraUiWrapper.getResString(R.string.auto_)))
            {
                parameters.set(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.scene_mode), cameraUiWrapper.getResString(R.string.scene_mode_asd));
                parameters.set(cameraUiWrapper.getResString(R.string.auto_hdr_enable), cameraUiWrapper.getResString(R.string.enable_));
            }
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        onValueHasChanged(valueToSet);
    }

    @Override
    public String GetValue() {
        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI_Note_Pro
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Xiaomi_RedmiNote) {
            if (parameters.get(cameraUiWrapper.getResString(R.string.morpho_hdr)).equals(cameraUiWrapper.getResString(R.string.true_))
                    && parameters.get(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.ae_bracket_hdr)).equals(cameraUiWrapper.GetAppSettingsManager().getResString(R.string.ae_bracket_hdr_values_aebracket)))
                return cameraUiWrapper.getResString(R.string.on_);
            else
                return cameraUiWrapper.getResString(R.string.off_);
        }
        else if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV)
        {
            if (parameters.get(cameraUiWrapper.getResString(R.string.hdr_mode))== null)
                parameters.set(cameraUiWrapper.getResString(R.string.hdr_mode), "0");
            if (parameters.get(cameraUiWrapper.getResString(R.string.hdr_mode)).equals("0"))
                return cameraUiWrapper.getResString(R.string.off_);
            else if (parameters.get(cameraUiWrapper.getResString(R.string.hdr_mode)).equals("1"))
                return cameraUiWrapper.getResString(R.string.on_);
            else
                return cameraUiWrapper.getResString(R.string.auto_);
        }
        else if(parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable))!= null)
        {
            if (parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable)).equals(cameraUiWrapper.getResString(R.string.enable_))
                    && parameters.get(cameraUiWrapper.getResString(R.string.scene_mode)).equals(cameraUiWrapper.getResString(R.string.scene_mode_hdr)))
                return cameraUiWrapper.getResString(R.string.on_);
            else if (parameters.get(cameraUiWrapper.getResString(R.string.auto_hdr_enable)).equals(cameraUiWrapper.getResString(R.string.enable_))
                    && parameters.get(cameraUiWrapper.getResString(R.string.scene_mode)).equals(cameraUiWrapper.getResString(R.string.scene_mode_asd)))
                return cameraUiWrapper.getResString(R.string.auto_);
            else
                return cameraUiWrapper.getResString(R.string.off_);
        }
        else
            return cameraUiWrapper.getResString(R.string.off_);
    }

    @Override
    public String[] GetValues() {
        List<String> hdrVals =  new ArrayList<>();
        hdrVals.add(cameraUiWrapper.getResString(R.string.off_));
            if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W)
            {
                hdrVals.add(cameraUiWrapper.getResString(R.string.on_));
            }
            else if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV) {
                hdrVals.add(cameraUiWrapper.getResString(R.string.on_));
                hdrVals.add(cameraUiWrapper.getResString(R.string.auto_));
            }
            else  {
                if (supporton)
                    hdrVals.add(cameraUiWrapper.getResString(R.string.on_));
                if (supportauto)
                    hdrVals.add(cameraUiWrapper.getResString(R.string.auto_));
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
            if (curmodule.equals(cameraUiWrapper.getResString(R.string.module_video))|| curmodule.equals(cameraUiWrapper.getResString(R.string.module_video)))
            {
                Hide();
                SetValue(cameraUiWrapper.getResString(R.string.off_),true);
            }
            else
            {
                if (format.contains(cameraUiWrapper.getResString(R.string.jpeg_))) {
                    Show();
                    onIsSupportedChanged(true);
                }
                else
                {
                    Hide();
                    SetValue(cameraUiWrapper.getResString(R.string.off_),true);
                }
            }
        }
    }

    @Override
    public void onParameterValueChanged(String val)
    {
        format = val;
        if (val.contains(cameraUiWrapper.getResString(R.string.jpeg_))&&!visible &&!curmodule.equals(cameraUiWrapper.getResString(R.string.module_hdr)))
            Show();

        else if (!val.contains(cameraUiWrapper.getResString(R.string.jpeg_))&& visible) {
            Hide();
        }
    }

    private void Hide()
    {
        state = GetValue();
        visible = false;
        SetValue(cameraUiWrapper.getResString(R.string.off_),true);
        onValueHasChanged(cameraUiWrapper.getResString(R.string.off_));
        onIsSupportedChanged(visible);
    }
    private void Show()
    {
        visible = true;
        SetValue(state,true);
        onValueHasChanged(state);
        onIsSupportedChanged(visible);
    }


}
