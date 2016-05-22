package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 16.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ColorModeApi2 extends BaseModeApi2
{
    /*
    OFF
    MONO
    NEGATIVE
    SOLARIZE
    SEPIA
    POSTERIZE
    WHITEBOARD
    BLACKBOARD
    AQUA
*/
    public enum ColorModes
    {
        off,
        mono,
        negative,
        solarize,
        sepia,
        posterize,
        whiteboard,
        blackboard,
        aqua,

    }

    public ColorModeApi2(Handler handler, CameraHolderApi2 cameraHolderApi2) {
        super(handler, cameraHolderApi2);
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        if (values.length > 1)
            this.isSupported = true;
    }

    @Override
    public boolean IsSupported()
    {
        return this.isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        ColorModes sceneModes = Enum.valueOf(ColorModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_EFFECT_MODE, sceneModes.ordinal());
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {
        int i = 0;
        try {
            i = cameraHolder.get(CaptureRequest.CONTROL_EFFECT_MODE);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }

        ColorModes sceneModes = ColorModes.values()[i];
        return sceneModes.toString();
    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                ColorModes sceneModes = ColorModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Scene" + values[i];
            }

        }
        return retvals;
    }
}
