package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;

/**
 * Created by troop on 23.04.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class OisModeApi2 extends BaseModeApi2
{
    public enum OISModes
    {
        off,
        on,
    }

    public OisModeApi2(Handler handler, CameraHolderApi2 cameraHolderApi2) {
        super(handler, cameraHolderApi2);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean IsSupported()
    {
        return cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION ) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Focus"))
            return;
        OISModes sceneModes = Enum.valueOf(OISModes.class, valueToSet);
        cameraHolder.SetParameterToCam(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {

        int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE);
        OISModes sceneModes = OISModes.values()[i];
        return sceneModes.toString();


    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION );
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                OISModes sceneModes = OISModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Ois mode" + values[i];
            }

        }
        return retvals;
    }
}
