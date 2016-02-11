package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        //baseCameraHolder.StopPreview();
        //baseCameraHolder.StartPreview();
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
