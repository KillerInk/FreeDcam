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

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockParameter extends BaseModeParameter
{
    final String TAG = ExposureLockParameter.class.getSimpleName();
    public ExposureLockParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper, SettingKeys.ExposureLock);
    }

    @Override
    public boolean IsSupported() {
        return parameters.isAutoExposureLockSupported();
    }

    @Override
    public void setValue(String valueToSet, boolean setToCam)
    {
        if (parameters.isAutoExposureLockSupported())
            parameters.setAutoExposureLock(Boolean.parseBoolean(valueToSet));

        if (setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue()
    {

        return parameters.getAutoExposureLock()+"";
    }

    @Override
    public String[] getStringValues() {
        return new String[]{cameraUiWrapper.getResString(R.string.true_), cameraUiWrapper.getResString(R.string.false_)};
    }

}
