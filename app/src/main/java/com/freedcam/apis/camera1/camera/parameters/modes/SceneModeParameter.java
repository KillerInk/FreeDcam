package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 27.04.2015.
 */
public class SceneModeParameter extends BaseModeParameter {
    public SceneModeParameter(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, String value, String values) {
        super(uihandler, parameters, cameraHolder, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        super.SetValue(valueToSet, setToCam);
        //cameraHolderApi1.StopPreview();
        //cameraHolderApi1.StartPreview();
    }

    @Override
    public String[] GetValues()
    {
        List<String> Trimmed = new ArrayList<>(Arrays.asList(parameters.get("scene-mode-values").split(",")));

        if(Trimmed.contains("hdr")) {
            Trimmed.remove("hdr");
            return Trimmed.toArray(new String[Trimmed.size()]);
        }

        return Trimmed.toArray(new String[Trimmed.size()]);



    }
}
