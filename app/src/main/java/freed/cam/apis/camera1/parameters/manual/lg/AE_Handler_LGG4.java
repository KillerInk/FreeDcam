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

package freed.cam.apis.camera1.parameters.manual.lg;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.manual.AE_Handler_Abstract;
import freed.cam.apis.camera1.parameters.manual.shutter.ShutterManualParameterG4;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 27.01.2016.
 */
public class AE_Handler_LGG4 extends AE_Handler_Abstract
{

    final String TAG = AE_Handler_LGG4.class.getSimpleName();

    public AE_Handler_LGG4(Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        super(parameters,cameraUiWrapper);
        Log.d(TAG, "AE_Handler_LGG4");
        iso = new ISOManualParameterG4(parameters,cameraUiWrapper, aeevent);
        shutter = new ShutterManualParameterG4(parameters, cameraUiWrapper, aeevent);
    }

    @Override
    protected void resetManualMode() {
        if (!auto)
            parameters.set(SettingsManager.getInstance().getResString(R.string.lg_manual_mode_reset), "1");
        else
            parameters.set(SettingsManager.getInstance().getResString(R.string.lg_manual_mode_reset), "0");
    }
}
