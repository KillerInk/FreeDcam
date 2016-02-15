package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 23.10.2014.
 */
public class AE_Bracket_HdrModeParameter extends BaseModeParameter
{
    public AE_Bracket_HdrModeParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String value, String values) {
        super(handler, parameters, cameraHolder, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals("AE-Bracket")) {
            baseCameraHolder.ParameterHandler.captureBurstExposures.SetValue("on",true);
            if (baseCameraHolder.ParameterHandler.ZSL.GetValue().equals("off")){
                baseCameraHolder.ParameterHandler.ZSL.SetValue("on", true);}
        }
        else {
            baseCameraHolder.ParameterHandler.ZSL.SetValue("off",true);
        }
        super.SetValue(valueToSet, setToCam);
    }


    @Override
    public String[] GetValues() {
            return new String[] {"Off","AE-Bracket"};
    }
}
