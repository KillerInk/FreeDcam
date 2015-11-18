package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ISOManualParameterG4 extends BaseManualParameter
{
    String[] isovalues;
    int current = 0;
    BaseCameraHolder baseCameraHolder;
    public ISOManualParameterG4(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
    public ISOManualParameterG4(HashMap<String, String> parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;

        this.isSupported = true;
        ArrayList<String> s = new ArrayList<String>();
        for (int i =0; i <= 2700; i +=50)
        {
            if (i == 0)
                s.add("Auto");
            else
                s.add(i + "");
        }
        isovalues = new String[s.size()];
        s.toArray(isovalues);
    }

    @Override
    public boolean IsSupported()
    {

        return isSupported;

    }

    @Override
    public int GetMaxValue() {

        return isovalues.length -1;

    }

    @Override
    public int GetMinValue() {

            return 0;
        }

    @Override
    public int GetValue() {
        return  current;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        current = valueToSet;
        if (valueToSet == 0) {

            parameters.put("lg-iso", "auto");
            parameters.put("shutter-speed", "0");
            parameters.put("lg-manual-mode-reset", "0");
        }
        else {
            parameters.put("lg-manual-mode-reset", "1");
            parameters.put("lg-iso", isovalues[valueToSet]);
        }
        baseCameraHolder.ParameterHandler.SetParametersToCamera();
    }

    @Override
    public String GetStringValue() {
        try {
            return isovalues[current];
        } catch (NullPointerException ex) {
            return "Auto";
        }
    }
}


