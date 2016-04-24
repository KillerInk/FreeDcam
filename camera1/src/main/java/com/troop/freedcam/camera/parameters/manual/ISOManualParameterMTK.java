package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 20/04/2016.
 */
public class ISOManualParameterMTK extends BaseManualParameter
{
    BaseCameraHolder baseCameraHolder;
    MTK_Manual_Handler.AeManualEvent manualEvent;

    public ISOManualParameterMTK(HashMap<String, String> parameters, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler, MTK_Manual_Handler.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler,1);

        this.baseCameraHolder = cameraHolder;

        this.isSupported = true;
        this.isVisible = isSupported;
        ArrayList<String> s = new ArrayList<String>();
        for (int i =100; i <= 2700; i +=50)
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
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public boolean IsVisible() {
        return super.IsSupported();
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
            manualEvent.onManualChanged(MTK_Manual_Handler.AeManual.iso, true, valueToSet);
        }
        else
        {
            manualEvent.onManualChanged(MTK_Manual_Handler.AeManual.iso, false,valueToSet);
        }
    }

    public void setValue(int value)
    {

        if (value == 0)
        {
            parameters.put("m-sr-g", "0");
        }
        else
        {
            currentInt = value;
            //cap-isp-g= 1024 == iso100? cause cap-sr-g=7808 / 1024 *100 = 762,5
            parameters.put("m-sr-g", String.valueOf((Integer.valueOf( stringvalues[value])/100)*1024));
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