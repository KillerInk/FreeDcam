package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;

/**
 * Created by troop on 06.03.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ZoomApi2 extends AbstractManualParameter
{
    final String TAG = ZoomApi2.class.getSimpleName();
    ParameterHandlerApi2 camParametersHandler;
    BaseCameraHolderApi2 cameraHolder;
    public ZoomApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder)  {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
        this.camParametersHandler = camParametersHandler;
        int max = (int)(cameraHolder.characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) *10);
        stringvalues = createStringArray(0,max,1);
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
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            Logger.e(TAG, e.getMessage());
        }
        catch (NullPointerException ex)
        {
            Logger.e(TAG, ex.getMessage());
        }
    }

    public Rect getZoomRect(float zoom, int imgWidth, int imgHeight)
    {

        int cropWidth = (int) ((imgWidth / 100) * zoom);
        int cropHeight = (int) ((imgHeight / 100)* zoom);
        int newX = cropWidth;
        int newW = imgWidth -cropWidth;
        int newY = cropHeight;
        int newH = imgHeight-cropHeight;
// ensure crop w,h divisible by 4 (SZ requirement)
        //cropWidth -= cropWidth & 3;
        //cropHeight -= cropHeight & 3;
// crop area for standard frame
        int cropWidthStd = cropWidth;
        int cropHeightStd = cropHeight;
        return new Rect(newX, newY, newW,newH);
    }
}
