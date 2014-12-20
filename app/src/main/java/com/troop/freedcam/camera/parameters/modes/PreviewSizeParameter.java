package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.List;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    AbstractCameraHolder baseCameraHolder;

    public PreviewSizeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewSizeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, AbstractCameraHolder cameraHolder)
    {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        //if (baseCameraHolder.IsPreviewRunning())
            baseCameraHolder.StopPreview();
        String[] widthHeight = valueToSet.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPreviewSize(w,h);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        //baseCameraHolder.SetCameraParameters(parameters);
        //if (!baseCameraHolder.IsPreviewRunning())
            baseCameraHolder.StartPreview();
    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }

    public List<Camera.Size> GetSizes()
    {
        return parameters.getSupportedPreviewSizes();
    }

    public int GetWidth()
    {
        return  parameters.getPreviewSize().width;
    }

    public int GetHeight()
    {
        return parameters.getPreviewSize().height;
    }
}
