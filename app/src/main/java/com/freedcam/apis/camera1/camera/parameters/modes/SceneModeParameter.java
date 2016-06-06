package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.CameraHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by troop on 27.04.2015.
 */
public class SceneModeParameter extends BaseModeParameter {
    public SceneModeParameter(Camera.Parameters parameters, CameraHolder cameraHolder, String value, String values) {
        super(parameters, cameraHolder, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        super.SetValue(valueToSet, setToCam);
        //cameraHolder.StopPreview();
        //cameraHolder.StartPreview();
    }

    @Override
    public String[] GetValues()
    {
        List<String> Trimmed = new ArrayList<>(Arrays.asList(parameters.get(KEYS.SCENE_MODE_VALUES).split(",")));

        if(Trimmed.contains(KEYS.SCENE_MODE_VALUES_HDR)) {
            Trimmed.remove(KEYS.SCENE_MODE_VALUES_HDR);
            return Trimmed.toArray(new String[Trimmed.size()]);
        }

        return Trimmed.toArray(new String[Trimmed.size()]);



    }
}
