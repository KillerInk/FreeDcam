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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.Settings;
import freed.settings.SettingsManager;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManualSony extends AbstractManualShutter
{
    final String TAG = ShutterManualSony.class.getSimpleName();
    private final Parameters parameters;
    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public ShutterManualSony(Parameters parameters,CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        this.parameters = parameters;
        stringvalues = SettingsManager.get(Settings.M_ExposureTime).getValues();
        isSupported = true;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        currentInt = valueToSet;
        ParameterInterface miso =cameraUiWrapper.getParameterHandler().get(Settings.M_ManualIso);
        if (currentInt == 0)
        {
            if (miso.GetValue() == 0)
                parameters.set("sony-ae-mode", "auto");
            else if (miso.GetValue() > 0)
                parameters.set("sony-ae-mode", "iso-prio");
        }
        else {
            if (miso.GetValue() == 0 && !parameters.get("sony-ae-mode").equals("shutter-prio"))
                parameters.set("sony-ae-mode", "shutter-prio");
            else if (miso.GetValue() > 0 && !parameters.get("sony-ae-mode").equals("manual"))
                parameters.set("sony-ae-mode", "manual");
            parameters.set(SettingsManager.get(Settings.M_ExposureTime).getKEY(), currentInt-1);
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }
}
