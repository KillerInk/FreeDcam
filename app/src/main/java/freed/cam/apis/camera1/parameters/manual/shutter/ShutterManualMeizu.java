/*
 *
 *     Copyright (C) 2015 George Kiarie
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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by GeorgeKiarie on 6/3/2016.
 */
public class ShutterManualMeizu extends AbstractManualShutter
{
    private final String TAG = ShutterManualMeizu.class.getSimpleName();
    private Parameters parameters;

    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public ShutterManualMeizu(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        this.parameters = parameters;
        stringvalues = AppSettingsManager.getInstance().manualExposureTime.getValues();

        isSupported = true;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public void setValue(int valueToSet)
    {
        currentInt = valueToSet;
        String shutterstring = stringvalues[currentInt];
        if (shutterstring.contains("/")) {
            String[] split = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a*1000000;
        }
        if(!stringvalues[currentInt].equals(cameraUiWrapper.getResString(R.string.auto_)))
        {
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
            shutterstring = setExposureTimeToParameter("0");
        }
        Log.e(TAG, shutterstring);
    }



    private String setExposureTimeToParameter(String shutterstring)
    {
        parameters.set(AppSettingsManager.getInstance().manualExposureTime.getKEY(), shutterstring);
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        cameraUiWrapper.stopPreview();
        cameraUiWrapper.startPreview();

        return shutterstring;
    }
}