package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FlashModeApi2 extends BaseModeApi2 {
    public FlashModeApi2(Handler handler,CameraHolderApi2 cameraHolderApi2) {
        super(handler, cameraHolderApi2);
    }

    public enum FlashModes
    {
        off,
        singel,
        torch,
    }

    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        FlashModes sceneModes = Enum.valueOf(FlashModes.class, valueToSet);
        cameraHolder.SetParameterRepeating(CaptureRequest.FLASH_MODE, sceneModes.ordinal());
    }


    @Override
    public String GetValue()
    {
        if (cameraHolder == null)
            return null;
        if (cameraHolder.get(CaptureRequest.FLASH_MODE) == null)
            return "error";
        int i = cameraHolder.get(CaptureRequest.FLASH_MODE);
        FlashModes sceneModes = FlashModes.values()[i];
        return sceneModes.toString();

    }

    @Override
    public String[] GetValues()
    {
        String[] retvals = new String[3];
        for (int i = 0; i < 3; i++)
        {
            try {
                final FlashModes sceneModes = FlashModes.values()[i];
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
