package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

/**
 * Created by troop on 05.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ImageStabApi2 extends BaseModeApi2
{
    public enum ImageStabsValues
    {
        off,
        on,
    }
    public ImageStabApi2(Handler handler, BaseCameraHolderApi2 baseCameraHolderApi2) {
        super(handler, baseCameraHolderApi2);
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder != null && cameraHolder.mPreviewRequestBuilder != null && cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        ImageStabsValues sceneModes = Enum.valueOf(ImageStabsValues.class, valueToSet);
        cameraHolder.SetParameterToCam(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
    }


    @Override
    public String GetValue()
    {
        int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE);
        ImageStabsValues sceneModes = ImageStabsValues.values()[i];
        return sceneModes.toString();

    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                ImageStabsValues sceneModes = ImageStabsValues.values()[values[i]];
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
