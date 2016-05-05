package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;

import java.util.HashMap;

/**
 * Created by troop on 23.10.2014.
 */
public class AE_Bracket_HdrModeParameter extends BaseModeParameter
{
    public AE_Bracket_HdrModeParameter(Handler handler, HashMap<String, String> parameters, CameraHolderApi1 cameraHolder, String value, String values) {
        super(handler, parameters, cameraHolder, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals("AE-Bracket")) {
            cameraHolderApi1.GetParameterHandler().captureBurstExposures.SetValue("on",true);
            if (cameraHolderApi1.GetParameterHandler().ZSL.GetValue().equals("off")){
                cameraHolderApi1.GetParameterHandler().ZSL.SetValue("on", true);}
        }
        else {
            cameraHolderApi1.GetParameterHandler().ZSL.SetValue("off",true);
        }
        super.SetValue(valueToSet, setToCam);
    }


    @Override
    public String[] GetValues() {
            return new String[] {"Off","AE-Bracket"};
    }
}
