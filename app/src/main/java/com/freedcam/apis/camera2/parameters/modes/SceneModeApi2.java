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
 * Created by troop on 13.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class SceneModeApi2 extends  BaseModeApi2
{

    public enum SceneModes
    {
        disable,
        face,
        action,
        portrait,
        landscape,
        night,
        night_portrait,
        theatre,
        beach,
        snow,
        sunset,
        steadyphoto,
        fireworks,
        sports,
        party,
        candlelight,
        barcode,
        high_speed_video,

    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public SceneModeApi2(CameraHolder cameraHolder)
    {
        super(cameraHolder);
        int[] values = this.cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
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
        SceneModes sceneModes = Enum.valueOf(SceneModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_SCENE_MODE, sceneModes.ordinal());
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {

            int i = cameraHolder.get(CaptureRequest.CONTROL_SCENE_MODE);
            SceneModes sceneModes = SceneModes.values()[i];
            return sceneModes.toString();


    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                SceneModes sceneModes = SceneModes.values()[values[i]];
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
