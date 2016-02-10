package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 23.10.2014.
 */
public class AE_Bracket_HdrModeParameter extends BaseModeParameter
{
    public AE_Bracket_HdrModeParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder) {
        super(handler, parameters, cameraHolder, "ae-bracket-hdr", "ae-bracket-hdr-values");
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals("AE-Bracket")) {
            parameters.put("capture-burst-exposures", "-10,0,10");
            try {
                baseCameraHolder.SetCameraParameters(parameters);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        super.SetValue(valueToSet, setToCam);
    }


    @Override
    public String[] GetValues() {
            return new String[] {"Off","AE-Bracket"};
    }
}
