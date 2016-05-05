package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.utils.Logger;

import java.util.HashMap;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockParameter extends BaseModeParameter
{
    final String TAG = ExposureLockParameter.class.getSimpleName();
    public ExposureLockParameter(Handler handler, HashMap<String, String> parameters, CameraHolderApi1 parameterChanged, String values) {
        super(handler, parameters, parameterChanged, "", "");
    }

    @Override
    public boolean IsSupported() {
        if (parameters.containsKey("auto-exposure-lock-supported"))
        {
            if (parameters.get("auto-exposure-lock-supported").equals("true"))
            {
                return true;
            }
        }
            return false;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (parameters.get("auto-exposure-lock-supported").equals("true"))
            parameters.put("auto-exposure-lock", valueToSet);
        //if (parameters.get("auto-whitebalance-lock-supported").equals("true"))
            //parameters.put("auto-whitebalance-lock", valueToSet);
        try {
            cameraHolderApi1.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

    @Override
    public String GetValue()
    {
        if (!parameters.containsKey("auto-exposure-lock"))
            parameters.put("auto-exposure-lock","false");
        return parameters.get("auto-exposure-lock");
    }

    @Override
    public String[] GetValues() {
        return new String[]{"true", "false"};
    }

    @Override
    public void BackgroundValueHasChanged(String value) {
            super.BackgroundValueHasChanged(value);
    }
}
