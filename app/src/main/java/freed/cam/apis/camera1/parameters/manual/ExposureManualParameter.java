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

package freed.cam.apis.camera1.parameters.manual;

import android.hardware.Camera.Parameters;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.Log;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{

    private final String TAG = ExposureManualParameter.class.getSimpleName();
    public ExposureManualParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper, float step) {
        super(parameters,cameraUiWrapper,step);
        stringvalues = createStringArray(parameters.getMinExposureCompensation(),parameters.getMaxExposureCompensation(),parameters.getExposureCompensationStep());
        isSupported = true;
        isVisible = true;
        String TAG = ExposureManualParameter.class.getSimpleName();
        Log.d(TAG, "Is Supported:" + isSupported);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    protected String[] createStringArray(int min,int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        for (int i = min; i <= max; i++)
        {
            String s = String.format("%.1f",i*step );
            ar.add(s);
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public void setValue(int valueToset)
    {
        if(stringvalues == null || stringvalues.length == 0)
            return;
        currentInt = valueToset- stringvalues.length/2;
        parameters.setExposureCompensation(currentInt);
        try
        {
            Log.d(TAG,"SetValue  setExposureCompensation");
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }
        fireIntValueChanged(currentInt);
        fireStringValueChanged(stringvalues[valueToset]);
    }

    @Override
    public int GetValue() {
        return currentInt + stringvalues.length/2;
    }

    @Override
    public String GetStringValue() {
        return stringvalues[currentInt + stringvalues.length/2];
    }
}
