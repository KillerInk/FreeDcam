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
import android.hardware.camera2.CaptureRequest.Key;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera2.CameraHolder;

/**
 * Created by troop on 12.12.2014.
 */
public class BaseModeApi2 extends AbstractModeParameter
{
    private final String TAG = BaseModeApi2.class.getSimpleName();
    protected I_CameraUiWrapper cameraUiWrapper;
    boolean isSupported = false;

    public BaseModeApi2(I_CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        super.SetValue(valueToSet,setToCamera);
    }

    @Override
    public String GetValue()
    {
        return null;
    }

    @Override
    public String[] GetValues() {
        return new String[0];
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void setIntKey(Key<Integer> key, int value)
    {
        ((CameraHolder)cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(key, value);
    }


}
