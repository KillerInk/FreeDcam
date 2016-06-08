/*
 *
 *     Copyright (C) 2015 Maxim
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

package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by Ar4eR on 05.02.16.
 */
public class CupBurstExpModeParameter extends BaseModeParameter
{
    final String TAG = CupBurstExpModeParameter.class.getSimpleName();
    private AppSettingsManager appSettingsManager;
    public CupBurstExpModeParameter(Parameters parameters, CameraHolder cameraHolder, String values, AppSettingsManager appSettingsManager) {
        super(parameters, cameraHolder, "capture-burst-exposures", "");
        this.appSettingsManager = appSettingsManager;

        isSupported = false;
        try {
            String cbe =  parameters.get("capture-burst-exposures");
            if (cbe != null || !cbe.equals(""))
                isSupported = true;
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        try {
            String aeb =  parameters.get("ae-bracket-hdr");
            if (aeb != null || !aeb.equals(""))
                isSupported = true;
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues() {
        return new String[] {"off","on"};
    }


    @Override
    public void SetValue(String valueToSet, boolean setToCam) {

        parameters.set("ae-bracket-hdr","Off");
        try {
            cameraHolder.SetCameraParameters(parameters);
        } catch (Exception ex) {
            Logger.exception(ex);
        }
        String[] newvalue = "0,0,0".split(",");
        if (valueToSet.equals("on")) {
            //if (cameraHolder.ParameterHandler.aeb1.GetValue() != null) {
                //newvalue[0] = cameraHolder.ParameterHandler.aeb1.GetValue();
            newvalue[0] = appSettingsManager.getString(AppSettingsManager.SETTING_AEB1);
            if(newvalue[0] == null || newvalue[0].equals(""))
                newvalue[0] = "5";
            newvalue[1] = appSettingsManager.getString(AppSettingsManager.SETTING_AEB2);
            if(newvalue[1] == null || newvalue[1].equals(""))
                newvalue[1] = "0";
            newvalue[2] = appSettingsManager.getString(AppSettingsManager.SETTING_AEB3);
            if(newvalue[2] == null || newvalue[2].equals(""))
                newvalue[2] = "-5";
        }

        parameters.set("capture-burst-exposures",newvalue[0]+","+newvalue[1]+","+newvalue[2]);
        try {
            cameraHolder.SetCameraParameters(parameters);
            //super.BackgroundValueHasChanged(newvalue[0]+","+newvalue[1]+","+newvalue[2]);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    @Override
    public String GetValue() {
        String tmp = parameters.get(key_value);
        if (tmp == null || tmp == "")
            return "off";
        else
            return parameters.get(key_value);
    }
}
