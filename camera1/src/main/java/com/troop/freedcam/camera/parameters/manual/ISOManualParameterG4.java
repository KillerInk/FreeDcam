package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class ISOManualParameterG4 extends BaseManualParameter
{
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
        stringvalues = new String[s.size()];
        s.toArray(stringvalues);
        this.manualEvent = manualevent;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }


    @Override
    public int GetValue() {
        return  currentInt;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        currentInt = valueToSet;
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
            currentInt = value;
            parameters.put("lg-iso", stringvalues[value]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[value]);
    }

    @Override
    public String GetStringValue() {
        try {
            return stringvalues[currentInt];
        } catch (NullPointerException ex) {
            return "Auto";
        }
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }
}


