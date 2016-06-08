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
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.util.Size;

import com.freedcam.apis.camera2.camera.CameraHolder;

/**
 * Created by troop on 26.11.2015.
 */
public class VideoSizeModeApi2 extends BaseModeApi2 {
    public VideoSizeModeApi2(CameraHolder cameraHolder) {
        super(cameraHolder);
    }

    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        BackgroundValueHasChanged(valueToSet);
        cameraHolder.VideoSize = valueToSet;
        if (setToCamera)
        {
            cameraHolder.StopPreview();
            cameraHolder.StartPreview();
        }
    }

    @Override
    public String GetValue()
    {

        return cameraHolder.VideoSize;
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public String[] GetValues()
    {
        Size[] sizes = cameraHolder.map.getOutputSizes(MediaRecorder.class);
        String[] ret = new String[sizes.length];
        for(int i = 0; i < sizes.length; i++)
        {
            ret[i] = sizes[i].getWidth()+ "x" + sizes[i].getHeight();
        }

        return ret;
    }
}
