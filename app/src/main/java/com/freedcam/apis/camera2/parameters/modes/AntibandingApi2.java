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
 * Created by Ingo on 01.05.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class AntibandingApi2 extends BaseModeApi2
{
    public enum AntibandingModes
    {
        OFF,
        HZ50,
        HZ60,
        AUTO,

    }
    public AntibandingApi2(CameraHolder cameraHolder) {
        super(cameraHolder);
    }

    @Override
    public boolean IsSupported()
    {
        return cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES) != null;
    }


    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Focus"))
            return;
        AntibandingModes sceneModes = Enum.valueOf(AntibandingModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {

        int i = cameraHolder.get(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE);
        AntibandingModes sceneModes = AntibandingModes.values()[i];
        return sceneModes.toString();


    }


    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                AntibandingModes sceneModes = AntibandingModes.values()[values[i]];
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
