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

package freed.cam.apis.camera1.parameters.manual;


import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 01.09.2014.
 */
public class ZoomManualParameter extends  BaseManualParameter
{
    public ZoomManualParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        super(parameters,cameraUiWrapper,1);
        key_value = cameraUiWrapper.getResString(R.string.zoom);
        isSupported = false;
        if (parameters.get(cameraUiWrapper.getResString(R.string.zoom_supported))!= null)
            if (parameters.get(cameraUiWrapper.getResString(R.string.zoom_supported)).equals(cameraUiWrapper.getResString(R.string.true_))) {
                isSupported = true;
                isVisible = true;
                Set_Default_Value(GetValue());
                stringvalues = createStringArray(0,Integer.parseInt(parameters.get(cameraUiWrapper.getResString(R.string.zoom_max))),1);
                currentInt = Integer.parseInt(parameters.get(key_value));
            }
    }

    @Override
    public void setValue(int valueToset) {
        currentInt = valueToset;
        parameters.set(key_value, valueToset);
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }
}
