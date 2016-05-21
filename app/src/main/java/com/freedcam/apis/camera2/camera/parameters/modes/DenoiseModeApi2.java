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
public class DenoiseModeApi2 extends BaseModeApi2
{
    public enum DeNoiseModes
    {
        OFF,
        FAST,
        HIGH_QUALITY,

    }

    public DenoiseModeApi2(Handler handler, CameraHolderApi2 cameraHolderApi2) {
        super(handler, cameraHolderApi2);
    }

    @Override
    public boolean IsSupported() {
        return cameraHolder != null && cameraHolder.get(CaptureRequest.NOISE_REDUCTION_MODE) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        DeNoiseModes sceneModes = Enum.valueOf(DeNoiseModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.NOISE_REDUCTION_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
    }


    @Override
    public String GetValue()
    {
        int i = cameraHolder.get(CaptureRequest.NOISE_REDUCTION_MODE);
        DeNoiseModes sceneModes = DeNoiseModes.values()[i];
        return sceneModes.toString();

    }


    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                DeNoiseModes sceneModes = DeNoiseModes.values()[values[i]];
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
