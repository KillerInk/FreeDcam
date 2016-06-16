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


package freed.cam.apis.camera1.parameters.manual.mtk;

import android.hardware.Camera.Parameters;

import freed.cam.apis.basecamera.interfaces.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.utils.Logger;

/**
 * Created by GeorgeKiarie on 6/3/2016.
 */
public class BaseManualParamMTK extends BaseManualParameter
{
    private final String TAG = BaseManualParamMTK.class.getSimpleName();

    private int default_value;

    public BaseManualParamMTK(Parameters  parameters, String value, String values, CameraWrapperInterface cameraUiWrapper) {
        super(parameters,value,"","", cameraUiWrapper,1);
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
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

}