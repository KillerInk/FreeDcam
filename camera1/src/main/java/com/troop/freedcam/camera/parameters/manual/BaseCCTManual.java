package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseCCTManual extends BaseManualParameter
{
    private String manual_WbMode;
    /**
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param camParametersHandler
     * @param step
     */
    public BaseCCTManual(HashMap<String, String> parameters, String value, String maxValue, String MinValue
            , AbstractParameterHandler camParametersHandler, float step,
                         String wbmode) {
        super(parameters, value, maxValue, MinValue, camParametersHandler, step);
        this.manual_WbMode = wbmode;
    }

    public BaseCCTManual(HashMap<String, String> parameters, String value, int max, int min
            , AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, "", "", camParametersHandler, step);
        this.isSupported = true;
        this.isVisible = true;
        this.stringvalues = createStringArray(min,max,step);
        this.manual_WbMode =wbmode;
    }

    @Override
    protected void setvalue(int valueToSet) {
        currentInt = valueToSet;
        //set to auto
        if (currentInt == 0) {
            set_to_auto();
        } else //set manual wb mode and value
        {
            set_manual();
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }

    protected void set_manual()
    {
        if (!camParametersHandler.WhiteBalanceMode.GetValue().equals(manual_WbMode) && manual_WbMode != "")
            camParametersHandler.WhiteBalanceMode.SetValue(manual_WbMode, true);
        parameters.put(value, stringvalues[currentInt]);
    }

    protected void set_to_auto()
    {
        camParametersHandler.WhiteBalanceMode.SetValue("auto", true);
    }

    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> t = new ArrayList<String>();
        t.add("Auto");
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        stringvalues = new String[t.size()];
        t.toArray(stringvalues);
        return stringvalues;
    }
}
