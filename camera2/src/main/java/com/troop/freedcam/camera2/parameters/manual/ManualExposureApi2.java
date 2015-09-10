package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
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
public class ManualExposureApi2 extends AbstractManualParameter
{
    ParameterHandlerApi2 camParametersHandler;
    BaseCameraHolderApi2 cameraHolder;
    public ManualExposureApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.camParametersHandler = camParametersHandler;
        this.cameraHolder = cameraHolder;
    }

    @Override
    public int GetMaxValue() {
        return cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getUpper();
    }

    @Override
    public int GetMinValue() {
        return cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getLower();
    }

    @Override
    public int GetValue() {
        return cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION);
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
    public void SetValue(int valueToSet) {
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, valueToSet);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE) != null;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }
}
