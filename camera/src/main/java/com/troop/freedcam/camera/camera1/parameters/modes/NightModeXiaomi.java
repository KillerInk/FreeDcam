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

package com.troop.freedcam.camera.camera1.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.ParameterEvents;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.ContextApplication;

/**
 * Created by troop on 10.06.2016.
 */
public class NightModeXiaomi extends BaseModeParameter implements ParameterEvents
{
    final String TAG = NightModeZTE.class.getSimpleName();
    private boolean visible = true;
    private String state = "";
    private String format = "";
    private String curmodule = "";

    public NightModeXiaomi(Camera.Parameters parameters, CameraControllerInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper,SettingKeys.NightMode);
        if(parameters.get(ContextApplication.getStringFromRessources(R.string.morpho_hht)) != null
                && parameters.get(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr)) != null) {
            setViewState(ViewState.Visible);
            //cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).addEventListner(this);
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_))) {
            parameters.set(ContextApplication.getStringFromRessources(R.string.morpho_hdr), ContextApplication.getStringFromRessources(R.string.false_));
            cameraUiWrapper.getParameterHandler().get(SettingKeys.HDRMode).fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
            parameters.set("capture-burst-exposures","-10,0,10");
            parameters.set(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr), ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(ContextApplication.getStringFromRessources(R.string.morpho_hht), ContextApplication.getStringFromRessources(R.string.true_));
        } else {
            parameters.set(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr), ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_aebracket));
            parameters.set(ContextApplication.getStringFromRessources(R.string.morpho_hht), ContextApplication.getStringFromRessources(R.string.false_));
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String GetStringValue()
    {
        if (parameters.get(ContextApplication.getStringFromRessources(R.string.morpho_hht)).equals(ContextApplication.getStringFromRessources(R.string.true_))
                && parameters.get(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr)).equals(ContextApplication.getStringFromRessources(R.string.ae_bracket_hdr_values_off)))
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_);
        else
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
    }

    @Override
    public String[] getStringValues()
    {
       return new String[] {ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)};
    }

    @Override
    public void onModuleChanged(String module)
    {
        curmodule = module;
        if (curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_video))|| curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_hdr)))
            Hide();
        else
        {
            if (format.contains(ContextApplication.getStringFromRessources(R.string.jpeg_))) {
                Show();
                setViewState(ViewState.Visible);
            }
        }
    }

    @Override
    public void onViewStateChanged(ViewState value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String val) {
        format = val;
        if (val.contains(ContextApplication.getStringFromRessources(R.string.jpeg_))&&!visible &&!curmodule.equals(ContextApplication.getStringFromRessources(R.string.module_hdr)))
            Show();

        else if (!val.contains(ContextApplication.getStringFromRessources(R.string.jpeg_))&& visible) {
            Hide();
        }
    }

    private void Hide()
    {
        state = GetStringValue();
        visible = false;
        SetValue(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),true);
        fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        setViewState(ViewState.Hidden);
    }

    private void Show()
    {
        visible = true;
        SetValue(state,true);
        fireStringValueChanged(state);
        setViewState(ViewState.Visible);
    }
}
