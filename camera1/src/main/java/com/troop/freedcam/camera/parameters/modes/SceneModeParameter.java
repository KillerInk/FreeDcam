package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 27.04.2015.
 */
public class SceneModeParameter extends BaseModeParameter {
    public SceneModeParameter(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String value, String values) {
        super(uihandler, parameters, cameraHolder, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        super.SetValue(valueToSet, setToCam);
        baseCameraHolder.StopPreview();
        baseCameraHolder.StartPreview();
    }
}
