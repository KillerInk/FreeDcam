package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;

/**
 * Created by troop on 24.08.2014.
 */
public class PreviewFormatParameter extends BaseModeParameter
{
    private I_CameraHolder cameraHolder;

    public PreviewFormatParameter(Camera.Parameters parameters, CameraHolderApi1 parameterChanged) {
        super(parameters, parameterChanged, "preview-format", "preview-format-values");
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
