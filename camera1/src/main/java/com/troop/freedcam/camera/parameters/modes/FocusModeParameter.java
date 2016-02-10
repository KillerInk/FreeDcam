package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusModeParameter extends BaseModeParameter
{
    public FocusModeParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value) {
        super(handler, parameters, parameterChanged, "focus-mode", "focus-mode-values");
    }
}
