/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.parameters.manual;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseCCTManual extends BaseManualParameter
{
    static final String TAG = BaseCCTManual.class.getSimpleName();

    private String manual_WbMode;
    /**
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param parametersHandler
     * @param step
     */
    public BaseCCTManual(Parameters parameters, String value, String maxValue, String MinValue
            , ParametersHandler parametersHandler, float step,
                         String wbmode) {
        super(parameters, value, maxValue, MinValue, parametersHandler, step);
        manual_WbMode = wbmode;
    }

    public BaseCCTManual(Parameters parameters, String value, int max, int min
            , ParametersHandler parametersHandler, float step, String wbmode) {
        super(parameters, value, "", "", parametersHandler, step);
        isSupported = true;
        isVisible = true;
        stringvalues = createStringArray(min,max,step);
        manual_WbMode =wbmode;
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
