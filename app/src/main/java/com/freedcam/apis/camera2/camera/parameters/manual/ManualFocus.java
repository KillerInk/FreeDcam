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

package com.freedcam.apis.camera2.camera.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera2.camera.CameraHolder;
import com.freedcam.apis.camera2.camera.parameters.ParameterHandler;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualFocus extends AbstractManualParameter
{
    private final String TAG = ManualFocus.class.getSimpleName();
    private CameraHolder cameraHolder;
    public ManualFocus(ParameterHandler camParametersHandler, CameraHolder cameraHolder)
    {
        super(camParametersHandler);
        this.cameraHolder =cameraHolder;
        try {
            int max = (int)(cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)*10);
            stringvalues = createStringArray(0, max,1);
            currentInt = -1;
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
        }

    }

    @Override
    public int GetValue() {
        return (int)(cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE)* 10);
    }

    @Override
    public String GetStringValue()
    {
        if (currentInt == -1)
            return KEYS.AUTO;
        else {
            if (isSupported)
                return StringUtils.TrimmFloatString4Places(cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE) + "");
        }
        return "";
    }


    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if(valueToSet == 0)
        {
            camParametersHandler.FocusMode.SetValue("auto", true);
        }
        else
        {
            if (!camParametersHandler.FocusMode.GetValue().equals("off"))
                camParametersHandler.FocusMode.SetValue("off",true);
            cameraHolder.SetParameterRepeating(CaptureRequest.LENS_FOCUS_DISTANCE, (float) valueToSet / 10);
        }
    }


    @Override
    public boolean IsSupported()
    {
        int af[] = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        isSupported = false;
        for (int i : af)
        {
            if (i == CameraCharacteristics.CONTROL_AF_MODE_OFF)
                isSupported = true;
        }
        try {
            Logger.d(TAG, "LensFocusDistance" + cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){Logger.exception(ex);}
        try {
            Logger.d(TAG, "LensMinFocusDistance" + cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){Logger.exception(ex);}


        if (cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE) == null
                || cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) == 0)
            isSupported = false;
        return  isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }



}
