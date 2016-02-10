package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 24.08.2014.
 */
public class WhiteBalanceModeParameter extends BaseModeParameter {
    public WhiteBalanceModeParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(handler,parameters, parameterChanged, value, values);
        if (!parameters.get("whitebalance").equals(""))
            isSupported = true;
    }
}
