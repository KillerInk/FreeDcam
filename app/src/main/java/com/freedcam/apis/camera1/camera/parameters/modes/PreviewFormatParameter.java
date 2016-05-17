package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 24.08.2014.
 */
public class PreviewFormatParameter extends BaseModeParameter
{
    private I_CameraHolder cameraHolder;

    public PreviewFormatParameter(Handler handler, Camera.Parameters parameters, CameraHolderApi1 parameterChanged) {
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
