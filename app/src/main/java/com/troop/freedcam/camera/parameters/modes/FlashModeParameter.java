package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FlashModeParameter extends BaseModeParameter
{
    private static String TAG = FlashModeParameter.class.getSimpleName();
    public FlashModeParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(parameters,parameterChanged, value, values);
    }
}
