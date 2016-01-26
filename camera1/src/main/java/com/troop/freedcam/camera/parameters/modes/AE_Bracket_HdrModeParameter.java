package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

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
        if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()||DeviceUtils.isXiaomiMI_Note_Pro()||DeviceUtils.isRedmiNote()) {
            if (valueToSet == "HDR") {
                parameters.put("capture-burst-exposures", "-10,0,10");
                parameters.put("morpho-hdr", "true");
                parameters.put("ae-bracket-hdr", "AE-Bracket");
            } else if (valueToSet == "AE-Bracket") {
                parameters.put("capture-burst-exposures", "-10,0,10");
                parameters.put("morpho-hdr", "false");
                parameters.put("ae-bracket-hdr", "AE-Bracket");
            } else {
                parameters.put("morpho-hdr", "false");
                //parameters.put("capture-burst-exposures", "-10,0,10");
                parameters.put("ae-bracket-hdr", "Off");
            }
            try {
                baseCameraHolder.SetCameraParameters(parameters);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else
        if (valueToSet.equals("AE-Bracket")) {
            parameters.put("capture-burst-exposures", "-10,0,10");
            try {
                baseCameraHolder.SetCameraParameters(parameters);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            super.SetValue(valueToSet, setToCam);
        }
    }

    @Override
    public String GetValue() {
        if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()||DeviceUtils.isXiaomiMI_Note_Pro()||DeviceUtils.isRedmiNote()) {
            if (parameters.get("morpho-hdr").equals("true"))
                return "HDR";
            else if (parameters.get("ae-bracket-hdr").equals("AE-Bracket"))
                return "AE-Bracket";
            else return "Off";
        }
        else
            return parameters.get("ae-bracket-hdr");
    }

    @Override
    public String[] GetValues() {
        if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()||DeviceUtils.isXiaomiMI_Note_Pro()||DeviceUtils.isRedmiNote())
            return new String[] {"Off","HDR","AE-Bracket"};
        else
           return new String[] {parameters.get("ae-bracket-hdr")};
    }
}
