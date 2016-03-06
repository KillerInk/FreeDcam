package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    AbstractCameraHolder baseCameraHolder;
    final String TAG = PreviewSizeParameter.class.getSimpleName();

    public PreviewSizeParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, AbstractCameraHolder cameraHolder)
    {
        super(handler, parameters, parameterChanged, value, values);
        this.baseCameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        //if (baseCameraHolder.IsPreviewRunning())
        if (setToCam)
            baseCameraHolder.StopPreview();

        //if(DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234()||DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())
            parameters.put(value, valueToSet);
        baseCameraHolder.SetCameraParameters(parameters);

        /*try
        {
            ((BaseCameraHolder)baseCameraHolder).SetPreviewSize(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.e(TAG, ex.getMessage());

        }*/
        //baseCameraHolder.SetCameraParameters(parameters);
        //if (!baseCameraHolder.IsPreviewRunning())
        try {
            baseCameraHolder.SetCameraParameters(parameters);
            super.BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.e(TAG, ex.getMessage());
        }
        if (setToCam)
            baseCameraHolder.StartPreview();
        firststart = false;
    }
}
