package com.troop.freedcam.camera.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManualSony extends BaseManualParameter
{
    final String TAG = ShutterManualSony.class.getSimpleName();
    /**
     * @param parameters
     * @param maxValue
     * @param MinValue
     * @param camParametersHandler
     */
    public ShutterManualSony(HashMap<String, String> parameters, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        try {
            if (!parameters.get("sony-max-shutter-speed").equals(""))
            {
                try {
                    int min = Integer.parseInt(parameters.get("sony-min-shutter-speed"));
                    int max = Integer.parseInt(parameters.get("sony-max-shutter-speed"));
                    stringvalues = StringUtils.getSupportedShutterValues(min, max,true);
                    this.isSupported = true;
                } catch (NumberFormatException ex) {
                    Logger.exception(ex);
                    isSupported = false;
                }
            }
        }
        catch (NullPointerException ex)
        {
            isSupported = false;
        }
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        currentInt = valueToSet;
        parameters.put("sony-ae-mode", "manual");
        parameters.put("sony-shutter-speed", stringvalues[currentInt]);
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
