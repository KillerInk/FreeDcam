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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by Ar4eR on 05.02.16.
 */
public class CupBurstExpModeParameter extends BaseModeParameter
{
    final String TAG = CupBurstExpModeParameter.class.getSimpleName();
    private final AppSettingsManager appSettingsManager;
    public CupBurstExpModeParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper, AppSettingsManager appSettingsManager) {
        super(parameters, cameraUiWrapper);
        this.appSettingsManager = appSettingsManager;
        isSupported = null != parameters.get("capture-burst-exposures") && null != parameters.get(KEYS.AE_BRACKET_HDR);
        isVisible = isSupported;
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues() {
        return new String[] {parameters.get("ae-bracket-hdr-values")};
    }


    @Override
    public void SetValue(String valueToSet, boolean setToCam) {

        parameters.set("ae-bracket-hdr","Off");
        try {
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        } catch (Exception ex) {
            Logger.exception(ex);
        }




        parameters.set("capture-burst-exposures",
                        appSettingsManager.getString(AppSettingsManager.SETTING_AEB1)+","+
                        appSettingsManager.getString(AppSettingsManager.SETTING_AEB2)+","+
                        appSettingsManager.getString(AppSettingsManager.SETTING_AEB3)+","+
                        appSettingsManager.getString(AppSettingsManager.SETTING_AEB4)+","+
                        appSettingsManager.getString(AppSettingsManager.SETTING_AEB5)+","+
                        appSettingsManager.getString(AppSettingsManager.SETTING_AEB6)+","+
                        appSettingsManager.getString(AppSettingsManager.SETTING_AEB7)
                );
        try {
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);

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
