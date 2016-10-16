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

package freed.cam.apis.camera1.parameters.manual.zte;

import android.hardware.Camera.Parameters;
import android.os.Handler;

import com.troop.freedcam.R;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.DeviceUtils.Devices;
import freed.utils.Logger;

/**
 * Created by troop on 25.11.2015.
 */
public class ShutterManualZTE extends AbstractManualShutter
{
    private final String TAG = ShutterManualZTE.class.getSimpleName();
    private Parameters parameters;
    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public ShutterManualZTE(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        this.parameters = parameters;
        if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV)
            stringvalues = cameraUiWrapper.getContext().getResources().getStringArray(R.array.shutter_values_zte_z5s);
        else
            stringvalues = cameraUiWrapper.getContext().getResources().getStringArray(R.array.shutter_values_zte_z7);

        isSupported = true;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public boolean IsSetSupported() {
        return  true;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        String shutterstring = stringvalues[currentInt];
        if (shutterstring.contains("/")) {
            String[] split = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a;
        }
        if(!stringvalues[currentInt].equals(KEYS.AUTO))
        {
            try {
                shutterstring = setExposureTimeToParameter(shutterstring);
            }
            catch (Exception ex)
            {
                Logger.d("Freedcam", "Shutter Set FAil");
            }
        }
        else
        {
            setShutterToAuto();
        }
        Logger.e(TAG, shutterstring);
    }

    private void setShutterToAuto()
    {
        try
        {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetZTE_AE();
                    cameraUiWrapper.StopPreview();
                    cameraUiWrapper.StartPreview();
                }
            };
            //handler.postDelayed(r, 1);
            handler.post(r);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }

    }

    private String setExposureTimeToParameter(final String shutterstring)
    {
        try {

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    parameters.set("slow_shutter", shutterstring);
                    parameters.set("slow_shutter_addition", "1");
                    ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);

                    if(Double.parseDouble(shutterstring) < 1.0 ){
                        cameraUiWrapper.StopPreview();
                        cameraUiWrapper.StartPreview();
                    }
                }
            };
            handler.post(r);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        return shutterstring;
    }
}
