package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewFpsParameter extends  BaseModeParameter
{
    private BaseCameraHolder cameraHolder;

    public PreviewFpsParameter(Handler handler, HashMap<String, String> parameters, String values, BaseCameraHolder holder) {
        super(handler ,parameters, holder, "preview-frame-rate", "preview-frame-rate-values");
        this.cameraHolder = holder;
    }



    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);
        if (setToCam) {
            cameraHolder.StopPreview();
            cameraHolder.StartPreview();
        }
        firststart = false;

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
