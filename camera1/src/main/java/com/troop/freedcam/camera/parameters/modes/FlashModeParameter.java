package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FlashModeParameter extends BaseModeParameter
{
    private static String TAG = FlashModeParameter.class.getSimpleName();
    public FlashModeParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value) {
        super(handler,parameters,parameterChanged, "flash-mode", "flash-mode-values");
    }
}
