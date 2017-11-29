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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    final String TAG = PictureSizeParameter.class.getSimpleName();
    public PictureSizeParameter(Parameters  parameters, CameraWrapperInterface parameterChanged) {
        super(parameters, parameterChanged);
        this.cameraUiWrapper = parameterChanged;
        isSupported = true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.set("picture-size" , valueToSet);
        currentString = valueToSet;
        Log.d(TAG, "SetValue : picture-size");
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public String GetStringValue() {
        return AppSettingsManager.getInstance().pictureSize.get();
    }

    @Override
    public String[] getStringValues() {
        return AppSettingsManager.getInstance().pictureSize.getValues();
    }
}
