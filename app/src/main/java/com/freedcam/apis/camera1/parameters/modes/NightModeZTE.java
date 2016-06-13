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
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeZTE extends BaseModeParameter
{
    final String TAG = NightModeZTE.class.getSimpleName();
    private final boolean visible = true;
    private final String state = "";
    private String format = "";
    private final String curmodule = "";
    public NightModeZTE(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        isSupported = true;
        isVisible =true;
        cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(this);
        cameraUiWrapper.GetParameterHandler().PictureFormat.addEventListner(this);
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.set(KEYS.NIGHT_KEY, valueToSet);
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
            return parameters.get(KEYS.NIGHT_KEY);
    }

    @Override
    public String[] GetValues() {
        return new String[] {KEYS.OFF,KEYS.ON,KEYS.NIGHT_MODE_TRIPOD};
    }

    @Override
    public void onValueChanged(String val)
    {
        format = val;
    }
}