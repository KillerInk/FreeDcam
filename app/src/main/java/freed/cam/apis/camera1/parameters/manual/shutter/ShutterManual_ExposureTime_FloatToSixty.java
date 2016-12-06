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

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.qcom.ShutterManual_ExposureTime_Micro;
import freed.utils.DeviceUtils;

import android.util.Log;

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
            Log.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            System.out.println("Exposure Time #1"+shutterstring);


            if(cameraUiWrapper.GetAppSettingsManager().getDevice() == DeviceUtils.Devices.OnePlusX)
            {
                shutterstring = OnePlus(shutterstring);
                System.out.println("Exposure Time #2"+shutterstring);
            }else {
                shutterstring = FLOATtoSixty4(shutterstring);
            }



        try {

            parameters.set("exposure-time", shutterstring);

            }catch (Exception e){e.printStackTrace();}
        }
        else
        {
            parameters.set("exposure-time", "0");
            Log.d(TAG, "set exposure time to auto");
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
