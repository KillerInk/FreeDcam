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
import android.os.Handler;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;
import freed.utils.Log;

/**
 * Created by troop on 25.11.2015.
 */
public class ShutterManualZTE extends AbstractParameter
{
    private final String TAG = ShutterManualZTE.class.getSimpleName();
    private Parameters parameters;
    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public ShutterManualZTE(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper,SettingKeys.M_ExposureTime);
        this.parameters = parameters;
        setViewState(ViewState.Visible);
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        String shutterstring = stringvalues[currentInt];
        if (shutterstring.contains("/")) {
            String[] split = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a;
        }
        if(!stringvalues[currentInt].equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_)))
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
            setShutterToAuto();
        }
        Log.e(TAG, shutterstring);
    }

    private void setShutterToAuto()
    {
        try
        {
            Handler handler = new Handler();
            Runnable r = () -> {
                ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetZTE_AE();
                cameraUiWrapper.stopPreviewAsync();
                cameraUiWrapper.startPreviewAsync();
            };
            //handler.postDelayed(r, 1);
            handler.post(r);
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }

    }

    private String setExposureTimeToParameter(final String shutterstring)
    {
        try {

            Handler handler = new Handler();
            Runnable r = () -> {

                parameters.set("slow_shutter", shutterstring);
                parameters.set("slow_shutter_addition", "1");
                ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);

                if(Double.parseDouble(shutterstring) < 1.0 ){
                    cameraUiWrapper.stopPreviewAsync();
                    cameraUiWrapper.startPreviewAsync();
                }
            };
            handler.post(r);
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }
        return shutterstring;
    }
}
