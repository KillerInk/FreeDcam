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

package com.freedcam.apis.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera2.CameraHolder;
import com.freedcam.apis.camera2.parameters.ParameterHandler;

/**
 * Created by troop on 06.03.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ZoomApi2 extends AbstractManualParameter
{
    final String TAG = ZoomApi2.class.getSimpleName();
    public ZoomApi2(I_CameraUiWrapper cameraUiWrapper)  {
        super(cameraUiWrapper);

        int max = (int)(((CameraHolder)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) *10);
        stringvalues = createStringArray(0,max,1);
    }


    private int zoom = 0;

    @Override
    public boolean IsSupported() {
        return ((CameraHolder)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) > 0;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsVisible() {
        return true;
    }

    @Override
    public int GetValue() {
        return zoom;
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet)
    {
        zoom = valueToSet;
        float maxzoom = ((CameraHolder)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        Rect m = ((CameraHolder)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        int minW = (int) (m.width() / maxzoom);
        int minH = (int) (m.height() / maxzoom);
        int difW = m.width() - minW;
        int difH = m.height() - minH;
        int cropW = difW /100 *zoom;
        int cropH = difH /100 *zoom;
        cropW -= cropW & 3;
        cropH -= cropH & 3;
        Rect zoom = new Rect(cropW, cropH,m.width()-cropW, m.height() - cropH);
        ((CameraHolder)cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(CaptureRequest.SCALER_CROP_REGION, zoom);
    }

    public Rect getZoomRect(float zoom, int imgWidth, int imgHeight)
    {
        int cropWidth = (int) ((imgWidth / 100) * zoom);
        int cropHeight = (int) ((imgHeight / 100)* zoom);
        int newW = imgWidth -cropWidth;
        int newH = imgHeight-cropHeight;
        return new Rect(cropWidth, cropHeight, newW,newH);
    }
}
