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

package freed.cam.apis.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.utils.Log;

/**
 * Created by troop on 06.03.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ZoomApi2 extends AbstractParameter
{
    private final String TAG = ZoomApi2.class.getSimpleName();
    private float maxzoom;
    private Rect sensorSize;
    private int minCropWidth;
    private int minCropHeight;
    private int zoom;

    private final int ZOOM_LIMITER = 100;

    public ZoomApi2(CameraWrapperInterface cameraUiWrapper)  {
        super(cameraUiWrapper);
        maxzoom = ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        sensorSize = ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        //1000 - (1000 / 4) = 750 /100 = 7,5
        int minCropW = (int)(sensorSize.width() - (sensorSize.width() / maxzoom));
        int minCropH = (int)(sensorSize.height() - (sensorSize.height() / maxzoom));

        minCropWidth =  (int) (minCropW/ZOOM_LIMITER);
        minCropHeight = (int) (minCropH/ZOOM_LIMITER);

        stringvalues = createStringArray(0,ZOOM_LIMITER,1);
    }

    @Override
    public boolean IsSupported() {
        return ((CameraHolderApi2) cameraUiWrapper.getCameraHolder()).characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) > 0;
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
    public void setValue(int valueToSet, boolean setToCamera)
    {
        zoom = valueToSet;
        // diff values /2 cause we set it foreach side.
        int cropW = (int)((minCropWidth * zoom)/2);
        int cropH = (int)((minCropHeight * zoom)/2);
        Log.d(TAG, "maxZoomSize: " + minCropWidth +"/"+ minCropHeight + " cropSize: " + cropW+"/"+cropH);
        Rect zoom = new Rect(cropW, cropH,sensorSize.width()-cropW, sensorSize.height() - cropH);
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.SCALER_CROP_REGION, zoom,setToCamera);
    }

}
