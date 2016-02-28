package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

/**
 * Created by Ingo on 01.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeModeApi2 extends  BaseModeApi2
{
    public enum AEModes
    {
        off,
        on,
        on_auto_flash,
        on_always_flash,
        on_auto_flash_redeye,
    }

    public AeModeApi2(Handler handler, BaseCameraHolderApi2 baseCameraHolderApi2) {
        super(handler, baseCameraHolderApi2);
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
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
        AEModes sceneModes = Enum.valueOf(AEModes.class, valueToSet);
        cameraHolder.SetParameterToCam(CaptureRequest.CONTROL_AE_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {
        if (cameraHolder == null ||cameraHolder.mPreviewRequestBuilder == null)
            return null;
            int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_MODE);
            AEModes sceneModes = AEModes.values()[i];
            return sceneModes.toString();
    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                AEModes sceneModes = AEModes.values()[values[i]];
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
