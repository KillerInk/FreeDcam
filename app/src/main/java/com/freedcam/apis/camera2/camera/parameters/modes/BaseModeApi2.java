package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;

/**
 * Created by troop on 12.12.2014.
 */
public class BaseModeApi2 extends AbstractModeParameter
{
    private final String TAG = BaseModeApi2.class.getSimpleName();
    CameraHolderApi2 cameraHolder;
    boolean isSupported = false;

    public BaseModeApi2(Handler handler, CameraHolderApi2 cameraHolderApi2)
    {
        super(handler);
        this.cameraHolder = cameraHolderApi2;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        super.SetValue(valueToSet,setToCamera);
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
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.cameraBackroundValuesChangedListner,
                    null);
        } catch (CameraAccessException e) {
            Logger.exception(e);
        }
    }


}
