package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

/**
 * Created by troop on 05.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class HotPixelModeApi2 extends BaseModeApi2
{

    public enum HotPixelModes
    {
        OFF,
        FAST,
        HIGH_QUALITY,
    }

    public HotPixelModeApi2(Handler handler, BaseCameraHolderApi2 baseCameraHolderApi2) {
        super(handler, baseCameraHolderApi2);
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.HOT_PIXEL_MODE) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        HotPixelModes sceneModes = Enum.valueOf(HotPixelModes.class, valueToSet);
        cameraHolder.setIntKeyToCam(CaptureRequest.HOT_PIXEL_MODE, sceneModes.ordinal());
    }


    @Override
    public String GetValue()
    {
        int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.HOT_PIXEL_MODE);
        HotPixelModes sceneModes = HotPixelModes.values()[i];
        return sceneModes.toString();

    }

    @Override
    public String[] GetValues()
    {
        String[] retvals = new String[3];
        for (int i = 0; i < 3; i++)
        {
            try {
                final HotPixelModes sceneModes = HotPixelModes.values()[i];
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
