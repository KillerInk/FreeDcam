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
    LG_G4AeHandler.AeManualEvent manualEvent;

    public ISOManualParameterG4(HashMap<String, String> parameters, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler, LG_G4AeHandler.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler);

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
        this.manualEvent = manualevent;
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
        if (valueToSet == 0)
        {
            manualEvent.onManualChanged(LG_G4AeHandler.AeManual.iso, true, valueToSet);
        }
        else
        {
            manualEvent.onManualChanged(LG_G4AeHandler.AeManual.iso, false,valueToSet);
        }
    }

    public void setValue(int value)
    {

        if (value == 0)
        {
            parameters.put("lg-iso", "auto");
        }
        else
        {
            current = value;
            parameters.put("lg-iso", isovalues[value]);
        }
        ThrowCurrentValueStringCHanged(isovalues[value]);
    }

    @Override
    public String GetStringValue() {
        try {
            return isovalues[current];
        } catch (NullPointerException ex) {
            return "Auto";
        }
    }

    @Override
    public String[] getStringValues() {
        return isovalues;
    }
}


