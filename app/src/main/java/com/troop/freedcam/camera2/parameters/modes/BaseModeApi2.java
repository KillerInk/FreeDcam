package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;

/**
 * Created by troop on 12.12.2014.
 */
public class BaseModeApi2 implements I_ModeParameter
{
    BaseCameraHolderApi2 cameraHolder;
    boolean isSupported = false;

    public BaseModeApi2(BaseCameraHolderApi2 baseCameraHolderApi2)
    {
        this.cameraHolder = baseCameraHolderApi2;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setIntKey(CaptureRequest.Key<Integer> key, int value)
    {
        cameraHolder.mPreviewRequestBuilder.set(key, value);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


}
