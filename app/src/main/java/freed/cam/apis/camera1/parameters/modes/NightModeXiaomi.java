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

import android.hardware.Camera;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 10.06.2016.
 */
public class NightModeXiaomi extends BaseModeParameter
{
    final String TAG = NightModeZTE.class.getSimpleName();
    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public NightModeXiaomi(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        if(parameters.get(KEYS.MORPHO_HHT) != null && parameters.get(KEYS.AE_BRACKET_HDR) != null) {
            isSupported = true;
            isVisible = true;
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
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals(KEYS.ON)) {
            cameraUiWrapper.GetParameterHandler().morphoHDR.SetValue(KEYS.FALSE, true);
            cameraUiWrapper.GetParameterHandler().HDRMode.BackgroundValueHasChanged(KEYS.OFF);
            cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue(KEYS.AE_BRACKET_HDR, true);
            parameters.set(KEYS.MORPHO_HHT, KEYS.TRUE);
        } else {
            parameters.set(KEYS.AE_BRACKET_HDR, KEYS.AE_BRACKET_HDR_VALUES_OFF);
            parameters.set(KEYS.MORPHO_HHT, KEYS.FALSE);
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        BackgroundValueHasChanged(valueToSet);

    }

    @Override
    public String GetValue()
    {
        if (parameters.get(KEYS.MORPHO_HHT).equals(KEYS.TRUE) && parameters.get(KEYS.AE_BRACKET_HDR).equals(KEYS.AE_BRACKET_HDR_VALUES_OFF))
            return KEYS.ON;
        else
            return KEYS.OFF;
    }

    @Override
    public String[] GetValues()
    {
        return new String[] {KEYS.OFF,KEYS.ON};
    }

    @Override
    public void onModuleChanged(String module)
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
