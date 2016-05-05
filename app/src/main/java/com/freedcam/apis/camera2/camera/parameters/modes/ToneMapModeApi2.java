package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;

/**
 * Created by troop on 05.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ToneMapModeApi2 extends BaseModeApi2 {
    public ToneMapModeApi2(Handler handler, CameraHolderApi2 cameraHolderApi2) {
        super(handler, cameraHolderApi2);
    }

    public enum ToneMapModes
    {
        CONTRAST_CURVE,
        FAST,
        HIGH_QUALITY,
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        ToneMapModes sceneModes = Enum.valueOf(ToneMapModes.class, valueToSet);
        cameraHolder.SetParameterToCam(CaptureRequest.TONEMAP_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
    }


    @Override
    public String GetValue()
    {
        int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.TONEMAP_MODE);
        ToneMapModes sceneModes = ToneMapModes.values()[i];
        return sceneModes.toString();

    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                ToneMapModes sceneModes = ToneMapModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Focus" + values[i];
            }
        }
        return retvals;
    }
}
