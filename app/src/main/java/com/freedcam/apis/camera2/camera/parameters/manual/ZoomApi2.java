package com.freedcam.apis.camera2.camera.parameters.manual;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.camera2.camera.parameters.ParameterHandlerApi2;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;

/**
 * Created by troop on 06.03.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ZoomApi2 extends AbstractManualParameter
{
    final String TAG = ZoomApi2.class.getSimpleName();
    private ParameterHandlerApi2 camParametersHandler;
    private CameraHolderApi2 cameraHolder;
    public ZoomApi2(ParameterHandlerApi2 camParametersHandler, CameraHolderApi2 cameraHolder)  {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
        this.camParametersHandler = camParametersHandler;
        int max = (int)(cameraHolder.characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) *10);
        stringvalues = createStringArray(0,max,1);
    }


    private int zoom = 0;

    @Override
    public boolean IsSupported() {
        return (cameraHolder.characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) > 0);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet)
    {
        zoom = valueToSet;
        float maxzoom = cameraHolder.characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
        Rect m = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        int minW = (int) (m.width() / maxzoom);
        int minH = (int) (m.height() / maxzoom);
        int difW = m.width() - minW;
        int difH = m.height() - minH;
        int cropW = difW /100 *zoom;
        int cropH = difH /100 *zoom;
        cropW -= cropW & 3;
        cropH -= cropH & 3;
        Rect zoom = new Rect(cropW, cropH,m.width()-cropW, m.height() - cropH);
        cameraHolder.SetParameterRepeating(CaptureRequest.SCALER_CROP_REGION, zoom);
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
