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

/**
 * Created by troop on 05.05.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class HotPixelModeApi2 extends BaseModeApi2
{

    public enum HotPixelModes
    {
        OFF,
        FAST,
        HIGH_QUALITY,
    }

    public HotPixelModeApi2(CameraHolder cameraHolder) {
        super(cameraHolder);
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder != null && cameraHolder.get(CaptureRequest.HOT_PIXEL_MODE) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        HotPixelModes sceneModes = Enum.valueOf(HotPixelModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.HOT_PIXEL_MODE, sceneModes.ordinal());
    }


    @Override
    public String GetValue()
    {
        int i = cameraHolder.get(CaptureRequest.HOT_PIXEL_MODE);
        HotPixelModes sceneModes = HotPixelModes.values()[i];
        return sceneModes.toString();

    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                HotPixelModes sceneModes = HotPixelModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Focus" + values[i];
            }

        }
        return retvals;
    }
}
