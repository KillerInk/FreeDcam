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

package com.troop.freedcam.camera.camera1.parameters.manual.focus;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.Log;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class FocusManualHuawei extends BaseFocusManual
{
    private final String TAG = FocusManualHuawei.class.getSimpleName();
    public FocusManualHuawei(Parameters parameters, CameraControllerInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(parameters,cameraUiWrapper,settingMode);
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            parameters.set(ContextApplication.getStringFromRessources(R.string.hw_hwcamera_flag),ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
            parameters.set(ContextApplication.getStringFromRessources(R.string.hw_manual_focus_mode),ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        }
        else
        {
            parameters.set(ContextApplication.getStringFromRessources(R.string.hw_hwcamera_flag),ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
            parameters.set(ContextApplication.getStringFromRessources(R.string.hw_manual_focus_mode),ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
            parameters.set(key_value, stringvalues[currentInt]);
            Log.d(TAG, "Set " + key_value + " to : " + stringvalues[currentInt]);
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);

        }
    }
}