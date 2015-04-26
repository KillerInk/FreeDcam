package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 19.08.2014.
 */
public class ImagePostProcessingParameter extends BaseModeParameter
{
    public ImagePostProcessingParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(handler, parameters,parameterChanged, value, values);
        try {
            String ipps = parameters.get("ipp-values");
            if (!ipps.isEmpty())
                this.isSupported = true;
        }
        catch (Exception ex)
        {
            this.isSupported = false;
        }
    }

    @Override
    public boolean IsSupported() {
        return this.isSupported;
    }
}
