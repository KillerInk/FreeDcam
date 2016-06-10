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


package com.freedcam.apis.camera1.parameters.manual.mtk;

import android.content.Context;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 6/3/2016.
 */
public class BaseManualParamMTK extends BaseManualParameter
{
    private static String TAG = BaseManualParamMTK.class.getSimpleName();

    private int default_value = 0;

    public BaseManualParamMTK(Context context, Parameters  parameters, String value, String values, ParametersHandler parametersHandler) {
        super(context, parameters,value,"","", parametersHandler,1);
        this.parametersHandler = parametersHandler;
        this.parameters = parameters;
        key_value = value;
        //mtk stores that stuff like that brightness-values=low,middle,high
        if (parameters.get(values)!= null)
        {
            //get values
            stringvalues = parameters.get(values).split(",");
            String val = parameters.get(value);
            //lookup current value
            for (int i = 0; i < stringvalues.length; i++)
            {
                if (val.equals(stringvalues[i]))
                    currentInt = i;
            }
            isSupported = true;
            isVisible =true;
        }
    }

    @Override
    public void SetValue(int valueToset)
    {
        currentInt = valueToset;
        Logger.d(TAG, "set " + key_value + " to " + valueToset);
        if(stringvalues == null || stringvalues.length == 0)
            return;
        parameters.set(key_value, stringvalues[valueToset]);
        ThrowCurrentValueChanged(valueToset);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);
        try
        {
            parametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

}