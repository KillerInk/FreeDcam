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

package freed.cam.apis.camera1.parameters.manual.qcom;

import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.Logger;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_Micro extends AbstractManualShutter
{
    private final String TAG = ShutterManual_ExposureTime_Micro.class.getSimpleName();

    protected Parameters  parameters;
    /*
     * The name of the current key_value to get like brightness
     */
    protected String key_value;

    /**
     * The name of the current key_value to get like brightness-max
     */
    protected String key_max_value;
    /**
     * The name of the current key_value to get like brightness-min
     */
    protected String key_min_value;

    public ShutterManual_ExposureTime_Micro(Parameters parameters, CameraWrapperInterface cameraUiWrapper, String[] shuttervalues, String key_value)
    {
        super(cameraUiWrapper);
        stringvalues = shuttervalues;
        this.key_value = key_value;
        this.parameters = parameters;
        parameters.set(key_value, "0");
        isSupported = true;
        isVisible = true;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public ShutterManual_ExposureTime_Micro(Parameters parameters, CameraWrapperInterface cameraUiWrapper, String key_value, String maxval , String minval, boolean withauto) {
        super(cameraUiWrapper);
        this.parameters = parameters;
        this.key_value = key_value;
        key_max_value = maxval;
        key_min_value = minval;
        try {

            Logger.d(TAG, "minexpo = "+parameters.get(key_min_value) + " maxexpo = " + parameters.get(key_max_value));
            int min,max;
            if (!parameters.get(key_min_value).contains("."))
            {
                Logger.d(TAG, "Micro does not contain . load int");
                min = Integer.parseInt(parameters.get(key_min_value));
                max = Integer.parseInt(parameters.get(key_max_value));
                Logger.d(TAG, "min converterd = "+min + " max converterd = " + max);
            }
            else
            {
                Logger.d(TAG, "Micro contain .  *1000");
                double tmpMin = Double.parseDouble(parameters.get(key_min_value))*1000;
                double tmpMax = Double.parseDouble(parameters.get(key_max_value))*1000;
                min = (int)tmpMin;
                max = (int)tmpMax;
                Logger.d(TAG, "min converterd = "+min + " max converterd = " + max);

            }
            stringvalues = getSupportedShutterValues(min, max, withauto);

            parameters.set(key_value, "0");
            isSupported = true;

        } catch (NumberFormatException ex) {
            Logger.exception(ex);
            isSupported = false;
        }
        Logger.d(TAG, "isSupported:" + isSupported);
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public void SetValue(int valueToset)
    {
        currentInt = valueToset;
        if(!stringvalues[currentInt].equals(KEYS.AUTO))
        {
            String shutterstring = FormatShutterStringToDouble(stringvalues[currentInt]);
            Logger.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            shutterstring = getMicroSecFromMilliseconds(shutterstring);
            Logger.d(TAG, " StringUtils.getMicroSecFromMilliseconds"+ shutterstring);
            parameters.set(key_value, shutterstring);
        }
        else
        {
            parameters.set(key_value, "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
