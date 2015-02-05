package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewFpsParameter extends  BaseModeParameter
{
    BaseCameraHolder cameraHolder;
    public PreviewFpsParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewFpsParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, BaseCameraHolder holder) {
        super(parameters, parameterChanged, value, values);
        this.cameraHolder = holder;
    }



    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet,setToCam);

    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }
}
