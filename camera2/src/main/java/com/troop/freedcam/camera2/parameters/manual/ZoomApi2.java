package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;

/**
 * Created by troop on 06.03.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ZoomApi2 extends AbstractManualParameter
{
    ParameterHandlerApi2 camParametersHandler;
    BaseCameraHolderApi2 cameraHolder;
    public ZoomApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder)  {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
        this.camParametersHandler = camParametersHandler;
    }


    int zoom = 0;

    @Override
    public boolean IsSupported() {
        return (cameraHolder.characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) > 0);
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public int GetMaxValue() {
        return Math.round(cameraHolder.characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) *10);
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue() {
        return zoom;
    }

    @Override
    public String GetStringValue() {
        return null;
    }

    @Override
    public String[] getStringValues() {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet)
    {
        zoom = valueToSet;
        float toset = (float)zoom /10F / 2F;
        Rect zoom = getZoomRect(toset, cameraHolder.textureView.getWidth(), cameraHolder.textureView.getHeight());
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public static Rect getZoomRect(float zoom, int imgWidth, int imgHeight)
    {
        int cropWidth = (int) (imgWidth / zoom);
        int cropHeight = (int) (imgHeight / zoom);
// ensure crop w,h divisible by 4 (SZ requirement)
        cropWidth -= cropWidth & 3;
        cropHeight -= cropHeight & 3;
// crop area for standard frame
        int cropWidthStd = cropWidth;
        int cropHeightStd = cropHeight;
        return new Rect((imgWidth - cropWidthStd) / 2, (imgHeight - cropHeightStd) / 2, (imgWidth + cropWidthStd) / 2,
                (imgHeight + cropHeightStd) / 2);
    }
}
