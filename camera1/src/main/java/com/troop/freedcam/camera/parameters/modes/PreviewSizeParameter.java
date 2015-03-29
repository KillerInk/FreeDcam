package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    AbstractCameraHolder baseCameraHolder;

    public PreviewSizeParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewSizeParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, AbstractCameraHolder cameraHolder)
    {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        //if (baseCameraHolder.IsPreviewRunning())
            //baseCameraHolder.StopPreview();

        //parameters.put(value, valueToSet);

        try
        {
            ((BaseCameraHolder)baseCameraHolder).SetPreviewSize(valueToSet);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

        }
        //baseCameraHolder.SetCameraParameters(parameters);
        //if (!baseCameraHolder.IsPreviewRunning())
            //baseCameraHolder.StartPreview();
    }
}
