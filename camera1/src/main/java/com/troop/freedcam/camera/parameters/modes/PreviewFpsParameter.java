package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewFpsParameter extends  BaseModeParameter
{
    BaseCameraHolder cameraHolder;

    public PreviewFpsParameter(Handler handler,HashMap<String, String> parameters, String value, String values, BaseCameraHolder holder) {
        super(handler ,parameters, holder, value, values);
        this.cameraHolder = holder;
    }



    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet,setToCam);
        cameraHolder.StopPreview();
        cameraHolder.StartPreview();

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
