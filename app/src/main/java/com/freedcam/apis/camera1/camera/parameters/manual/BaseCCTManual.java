package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseCCTManual extends BaseManualParameter
{
    final static String TAG = BaseCCTManual.class.getSimpleName();

    private String manual_WbMode;
    /**
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param parametersHandler
     * @param step
     */
    public BaseCCTManual(Camera.Parameters parameters, String value, String maxValue, String MinValue
            , ParametersHandler parametersHandler, float step,
                         String wbmode) {
        super(parameters, value, maxValue, MinValue, parametersHandler, step);
        this.manual_WbMode = wbmode;
    }

    public BaseCCTManual(Camera.Parameters parameters, String value, int max, int min
            , ParametersHandler parametersHandler, float step, String wbmode) {
        super(parameters, value, "", "", parametersHandler, step);
        this.isSupported = true;
        this.isVisible = true;
        this.stringvalues = createStringArray(min,max,step);
        this.manual_WbMode =wbmode;
    }

    @Override
    public void SetValue(int valueToSet) {
        currentInt = valueToSet;
        //set to auto
        if (currentInt == 0) {
            set_to_auto();
        } else //set manual wb mode and key_value
        {
            set_manual();
        }
        parametersHandler.SetParametersToCamera(parameters);
    }

    protected void set_manual()
    {
        if (!parametersHandler.WhiteBalanceMode.GetValue().equals(manual_WbMode) && manual_WbMode != "")
            parametersHandler.WhiteBalanceMode.SetValue(manual_WbMode, true);
        parameters.set(key_value, stringvalues[currentInt]);
        Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);

    }

    protected void set_to_auto()
    {
        parametersHandler.WhiteBalanceMode.SetValue("auto", true);
        Logger.d(TAG, "Set  to : auto");
    }

    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> t = new ArrayList<>();
        t.add(KEYS.AUTO);
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        stringvalues = new String[t.size()];
        t.toArray(stringvalues);
        return stringvalues;
    }
}
