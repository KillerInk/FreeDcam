package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;

/**
 * Created by Ingo on 02.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ColorCorrectionModeApi2 extends BaseModeApi2 {
    public ColorCorrectionModeApi2(Handler handler, CameraHolderApi2 cameraHolderApi2) {
        super(handler, cameraHolderApi2);
    }

    public enum ColorCorrectionModes
    {
        TRANSFORM_MATRIX,
        FAST,
        HIGH_QUALITY,
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder != null && cameraHolder.mPreviewRequestBuilder != null && cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_MODE) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        ColorCorrectionModes sceneModes = Enum.valueOf(ColorCorrectionModes.class, valueToSet);
        cameraHolder.SetParameterToCam(CaptureRequest.COLOR_CORRECTION_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
    }


    @Override
    public String GetValue()
    {
        try {
            int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_MODE);
            ColorCorrectionModes sceneModes = ColorCorrectionModes.values()[i];
            return sceneModes.toString();
        }
        catch (NullPointerException ex){}
        return "";


    }

    @Override
    public String[] GetValues()
    {
        String[] retvals = new String[3];
        for (int i = 0; i < 3; i++)
        {
            try {
                final ColorCorrectionModes sceneModes = ColorCorrectionModes.values()[i];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Scene" + i;
            }

        }
        return retvals;
    }
}
