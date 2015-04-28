package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FlashModeApi2 extends BaseModeApi2 {
    public FlashModeApi2(Handler handler,BaseCameraHolderApi2 baseCameraHolderApi2) {
        super(handler,baseCameraHolderApi2);
    }

    public static String OFF = "off";
    public static String ON = "on";
    public static String AUTO = "auto";
    public static String TORCH = "torch";

    String lastvalue = "off";

    String flashvals[] ={ OFF, ON, AUTO, TORCH };

    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (cameraHolder != null && cameraHolder.mPreviewRequestBuilder != null && cameraHolder.mCaptureSession != null) {
            SetToBuilder(cameraHolder.mPreviewRequestBuilder, valueToSet);
            try {
                cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(),
                        cameraHolder.mCaptureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void SetToBuilder(CaptureRequest.Builder builder, String valueToSet)
    {
        if (lastvalue.equals(TORCH))
            builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
        lastvalue = valueToSet;
        if (valueToSet.equals(OFF))
        {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
        }
        else if (valueToSet.equals(ON))
        {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
            builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
        }
        else if (valueToSet.equals(AUTO))
        {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
        }
        else if (valueToSet.equals(TORCH))
        {
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            builder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
        }
    }

    @Override
    public String GetValue()
    {
        return lastvalue;

    }

    @Override
    public String[] GetValues()
    {
        return flashvals;
    }
}
