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

package freed.cam.apis.camera1.parameters.manual.shutter;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import java.text.DecimalFormat;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameterHTC extends BaseManualParameter
{
    private final String TAG = ShutterManualParameterHTC.class.getSimpleName();
    private final DecimalFormat trimfloat = new DecimalFormat("#.######");

    public ShutterManualParameterHTC(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        isSupported = true;
        stringvalues = AppSettingsManager.getInstance().manualExposureTime.getValues();
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }


    @Override
    public void setValue(int valueToSet)
    {
        currentInt = valueToSet;
        String shutterstring = stringvalues[currentInt];
        if(!shutterstring.equals(cameraUiWrapper.getResString(R.string.auto_)))
        {
            if (shutterstring.contains("/")) {
                String[] split = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            try {
                shutterstring = setExposureTimeToParameter(shutterstring);
            }
            catch (Exception ex)
            {
                Log.d("Freedcam", "Shutter Set FAil");
            }
        }
        else
        {
            setShutterToAuto();
        }
        Log.e(TAG, shutterstring);
    }

    private void setShutterToAuto() {
        parameters.set("shutter", "-1");
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }

    private String setExposureTimeToParameter(String shutterstring)
    {
        shutterstring = trimfloat.format(Float.parseFloat(shutterstring));
        parameters.set("shutter", shutterstring);
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        return shutterstring;
    }
}