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

import android.hardware.Camera.Parameters;

import com.troop.freedcam.camera.R;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.ParameterEvents;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.settings.SettingKeys;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeZTE extends BaseModeParameter implements ParameterEvents
{
    final String TAG = NightModeZTE.class.getSimpleName();
    private final boolean visible = true;
    private final String state = "";
    private final String curmodule = "";
    public NightModeZTE(Parameters parameters, CameraControllerInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper,SettingKeys.NightMode);
        setViewState(ViewState.Visible);
        //cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).addEventListner(this);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.set(ContextApplication.getStringFromRessources(R.string.night_key), valueToSet);
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String GetStringValue() {
            return parameters.get(ContextApplication.getStringFromRessources(R.string.night_key));
    }

    @Override
    public String[] getStringValues() {
        return new String[] {
                ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_),
                ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_),
                ContextApplication.getStringFromRessources(R.string.night_mode_tripod)};
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
    public void onStringValueChanged(String value) {
    }
}