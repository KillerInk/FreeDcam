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

import com.troop.freedcam.R;

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
        if(parameters.get(cameraUiWrapper.getResString(R.string.morpho_hht)) != null
                && parameters.get(cameraUiWrapper.getAppSettingsManager().getResString(R.string.ae_bracket_hdr)) != null) {
            isSupported = true;
            isVisible = true;
            cameraUiWrapper.getModuleHandler().addListner(this);
            cameraUiWrapper.getParameterHandler().PictureFormat.addEventListner(this);
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
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_))) {
            parameters.set(cameraUiWrapper.getResString(R.string.morpho_hdr), cameraUiWrapper.getResString(R.string.false_));
            cameraUiWrapper.getParameterHandler().HDRMode.onValueHasChanged(cameraUiWrapper.getResString(R.string.off_));
            parameters.set("capture-burst-exposures","-10,0,10");
            parameters.set(cameraUiWrapper.getAppSettingsManager().getResString(R.string.ae_bracket_hdr), cameraUiWrapper.getAppSettingsManager().getResString(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(cameraUiWrapper.getResString(R.string.morpho_hht), cameraUiWrapper.getResString(R.string.true_));
        } else {
            parameters.set(cameraUiWrapper.getAppSettingsManager().getResString(R.string.ae_bracket_hdr), cameraUiWrapper.getAppSettingsManager().getResString(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(cameraUiWrapper.getResString(R.string.morpho_hht), cameraUiWrapper.getResString(R.string.false_));
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        onValueHasChanged(valueToSet);

    }

    @Override
    public String GetValue()
    {
        if (parameters.get(cameraUiWrapper.getResString(R.string.morpho_hht)).equals(cameraUiWrapper.getResString(R.string.true_))
                && parameters.get(cameraUiWrapper.getAppSettingsManager().getResString(R.string.ae_bracket_hdr)).equals(cameraUiWrapper.getAppSettingsManager().getResString(R.string.ae_bracket_hdr_values_off)))
            return cameraUiWrapper.getResString(R.string.on_);
        else
            return cameraUiWrapper.getResString(R.string.off_);
    }

    @Override
    public String[] GetValues()
    {
        return new String[] {cameraUiWrapper.getResString(R.string.off_),cameraUiWrapper.getResString(R.string.on_)};
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(cameraUiWrapper.getResString(R.string.module_video))|| curmodule.equals(cameraUiWrapper.getResString(R.string.module_hdr)))
            Hide();
        else
        {
            if (format.contains(cameraUiWrapper.getResString(R.string.jpeg_))) {
                Show();
                onIsSupportedChanged(true);
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
