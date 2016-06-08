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

package com.freedcam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.camera2.CameraHolder;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 17.03.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ControlModesApi2 extends BaseModeApi2
{

    public enum ControlModes
    {
        off,
        auto,
        SCENE_MODE,
        OFF_KEEP_STATE
    }

    public ControlModesApi2(CameraHolder cameraHolder) {
        super(cameraHolder);

    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        ControlModes modes = Enum.valueOf(ControlModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_MODE, modes.ordinal());
    }

    @Override
    public String GetValue() {
        int i = 0;
        try {
            i = cameraHolder.get(CaptureRequest.CONTROL_MODE);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);

        }

        ControlModes sceneModes = ControlModes.values()[i];
        return sceneModes.toString();
    }

    @Override
    public String[] GetValues()
    {
        int device = cameraHolder.characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL) {
            return new String[]{"off",
                    "auto",
                    "SCENE_MODE",
                    "OFF_KEEP_STATE"};
        }
        else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
            return new String[]{"auto", "SCENE_MODE"};

        return super.GetValues();
    }
}
