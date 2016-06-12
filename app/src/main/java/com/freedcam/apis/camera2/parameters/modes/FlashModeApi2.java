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

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera2.CameraHolderApi2;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class FlashModeApi2 extends BaseModeApi2 {
    public FlashModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    public enum FlashModes
    {
        off,
        singel,
        torch,
    }

    @Override
    public boolean IsSupported() {
        return ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        FlashModes sceneModes = Enum.valueOf(FlashModes.class, valueToSet);
        ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(CaptureRequest.FLASH_MODE, sceneModes.ordinal());
    }


    @Override
    public String GetValue()
    {
        if (cameraUiWrapper.GetCameraHolder() == null)
            return null;
        if (((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.FLASH_MODE) == null)
            return "error";
        int i = ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.FLASH_MODE);
        FlashModes sceneModes = FlashModes.values()[i];
        return sceneModes.toString();

    }

    @Override
    public String[] GetValues()
    {
        String[] retvals = new String[3];
        for (int i = 0; i < 3; i++)
        {
            try {
                FlashModes sceneModes = FlashModes.values()[i];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Scene" + i;
            }

        }
        return retvals;
    }
}
