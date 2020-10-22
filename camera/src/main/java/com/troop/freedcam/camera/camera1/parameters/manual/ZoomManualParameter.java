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

package com.troop.freedcam.camera.camera1.parameters.manual;


import android.hardware.Camera.Parameters;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.ContextApplication;

/**
 * Created by troop on 01.09.2014.
 */
public class ZoomManualParameter extends  BaseManualParameter
{
    public ZoomManualParameter(Parameters parameters, CameraControllerInterface cameraUiWrapper, SettingKeys.Key key)
    {
        super(parameters,cameraUiWrapper,key);
        key_value = ContextApplication.getStringFromRessources(R.string.zoom);
        if (parameters.get(ContextApplication.getStringFromRessources(R.string.zoom_supported))!= null)
            if (parameters.get(ContextApplication.getStringFromRessources(R.string.zoom_supported)).equals(ContextApplication.getStringFromRessources(R.string.true_))) {
                setViewState(ViewState.Visible);
                stringvalues = createStringArray(0,Integer.parseInt(parameters.get(ContextApplication.getStringFromRessources(R.string.zoom_max))),1);
                try {
                    currentInt = Integer.parseInt(parameters.get(key_value));
                }
                catch (NullPointerException ex)
                {
                    currentInt = 0;
                }

            }
    }

    @Override
    public void setValue(int valueToset, boolean setToCamera) {
        currentInt = valueToset;
        parameters.set(key_value, valueToset);
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }
}
