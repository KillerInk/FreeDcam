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

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.qcom_new.ShutterManual_ExposureTime_Micro;
import freed.utils.Logger;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_FloatToSixty extends ShutterManual_ExposureTime_Micro
{

    private final String TAG = ShutterManual_ExposureTime_FloatToSixty.class.getSimpleName();
    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public ShutterManual_ExposureTime_FloatToSixty(Parameters parameters, CameraWrapperInterface cameraUiWrapper, boolean withauto) {
        super(parameters, cameraUiWrapper, "exposure-time", "max-exposure-time", "min-exposure-time",withauto);
    }

    @Override
    public void SetValue(int valueToset)
    {
        currentInt = valueToset;
        if(!stringvalues[currentInt].equals(KEYS.AUTO))
        {
            String shutterstring = FormatShutterStringToDouble(stringvalues[currentInt]);
            Logger.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            shutterstring = FLOATtoSixty4(shutterstring);
            parameters.set("exposure-time", shutterstring);
        }
        else
        {
            parameters.set("exposure-time", "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
