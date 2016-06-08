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

package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.camera2.camera.CameraHolder;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 16.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ColorModeApi2 extends BaseModeApi2
{
    /*
    OFF
    MONO
    NEGATIVE
    SOLARIZE
    SEPIA
    POSTERIZE
    WHITEBOARD
    BLACKBOARD
    AQUA
*/
    public enum ColorModes
    {
        off,
        mono,
        negative,
        solarize,
        sepia,
        posterize,
        whiteboard,
        blackboard,
        aqua,

    }

    public ColorModeApi2(CameraHolder cameraHolder) {
        super(cameraHolder);
        int[] values = this.cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        if (values.length > 1)
            isSupported = true;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        ColorModes sceneModes = Enum.valueOf(ColorModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_EFFECT_MODE, sceneModes.ordinal());
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {
        int i = 0;
        try {
            i = cameraHolder.get(CaptureRequest.CONTROL_EFFECT_MODE);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }

        ColorModes sceneModes = ColorModes.values()[i];
        return sceneModes.toString();
    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                ColorModes sceneModes = ColorModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Scene" + values[i];
            }

        }
        return retvals;
    }
}
