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

package com.freedcam.apis.camera1.parameters.manual.lg;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperEventInterface;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.interfaces.ModuleInterface;
import com.freedcam.apis.basecamera.parameters.I_ParametersLoaded;
import com.freedcam.apis.camera1.Camera1Fragment;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.AE_Handler_Abstract;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 27.01.2016.
 */
public class AE_Handler_LGG4 extends AE_Handler_Abstract
{

    final String TAG = AE_Handler_LGG4.class.getSimpleName();

    public AE_Handler_LGG4(Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        super(parameters,cameraUiWrapper);
        iso = new ISOManualParameterG4(parameters,cameraUiWrapper, aeevent);
        shutter = new ShutterManualParameterG4(parameters, cameraUiWrapper, aeevent);
    }

    @Override
    protected void resetManualMode() {
        parameters.set(KEYS.LG_MANUAL_MODE_RESET, "1");
        parameters.set(KEYS.LG_MANUAL_MODE_RESET, "0");
    }
}
