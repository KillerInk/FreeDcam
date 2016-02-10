package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 24.08.2014.
 */
public class PreviewFormatParameter extends BaseModeParameter
{
    private I_CameraHolder cameraHolder;

    public PreviewFormatParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, I_CameraHolder cameraHolder) {
        super(handler,parameters, parameterChanged, "preview-format", "preview-format-values");
        this.cameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);


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
