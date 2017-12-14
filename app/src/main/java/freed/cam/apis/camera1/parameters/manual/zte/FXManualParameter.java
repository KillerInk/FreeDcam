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

package freed.cam.apis.camera1.parameters.manual.zte;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class FXManualParameter extends BaseManualParameter {

    public FXManualParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
    }

    @Override
    public boolean IsSupported()
    {
        if(SettingsManager.getInstance().isZteAe())
        {
            isSupported = true;
            isVisible = true;
            stringvalues = createStringArray(0,38,1);
            return true;
        }
        else
            return false;

    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (SettingsManager.getInstance().isZteAe());
                i = 0;
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }

        return i;
    }

    @Override
    public void setValue(int valueToSet, boolean setToCamera)
    {
        parameters.set(cameraUiWrapper.getResString(R.string.morpho_effect_type), String.valueOf(valueToSet));
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);

    }

}