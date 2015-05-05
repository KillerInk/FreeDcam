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
        return cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE) != null;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        ImageStabsValues sceneModes = Enum.valueOf(ImageStabsValues.class, valueToSet);
        cameraHolder.setIntKeyToCam(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, sceneModes.ordinal());
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
        String[] retvals = new String[2];
        for (int i = 0; i < 2; i++)
        {
            try {
                final ImageStabsValues sceneModes = ImageStabsValues.values()[i];
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
