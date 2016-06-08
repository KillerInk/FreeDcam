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
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera2.camera.CameraHolder;

import java.util.ArrayList;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{
    private CameraHolder cameraHolder;
    boolean firststart = true;
    private String format = KEYS.JPEG;
    public PictureFormatParameterApi2(CameraHolder cameraHolder)
    {
        super(cameraHolder);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        BackgroundValueHasChanged(valueToSet);
        format = valueToSet;
        if (setToCamera)
        {
            cameraHolder.StopPreview();
            cameraHolder.StartPreview();
        }


    }

    @Override
    public String GetValue() {
        return format;
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public String[] GetValues()
    {
        ArrayList<String> ret = new ArrayList<>();
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW10))
            ret.add(CameraHolder.RAW10);
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
            ret.add(CameraHolder.RAW_SENSOR);
        if(cameraHolder.map.isOutputSupportedFor(ImageFormat.JPEG))
            ret.add(KEYS.JPEG);
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW12))
            ret.add(CameraHolder.RAW12);
        return ret.toArray(new String[ret.size()]);
    }
}
