package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualFocus extends ManualExposureTimeApi2
{

    int current = -1;
    public ManualFocus(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder)
    {
        super(camParametersHandler, cameraHolder);
    }

    @Override
    public int GetMaxValue() {
        return (int)(cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) * 100);
    }

    @Override
    public int GetMinValue() {
        return -1;
    }

    @Override
    public int GetValue() {
        return (int)(cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE)* 100);
    }

    @Override
    public String GetStringValue()
    {
        if (current == -1)
            return "auto";
        else
            return cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE) +"" ;
    }

    @Override
    public String[] getStringValues() {
        return null;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        current = valueToSet;
        if (valueToSet < cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE))
        {
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        }
        else
        {
            if (cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_MODE) != CaptureRequest.CONTROL_AF_MODE_OFF)
                cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, (float)valueToSet /100);
        }
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean IsSupported()
    {
        int af[] = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        boolean supported = false;
        for (int i : af)
        {
            if (i == CameraCharacteristics.CONTROL_AF_MODE_OFF)
                supported = true;
        }
        if (cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE) == null)
            supported = false;
        return  supported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }
}
