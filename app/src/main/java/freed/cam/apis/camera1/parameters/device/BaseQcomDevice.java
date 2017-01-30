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

package freed.cam.apis.camera1.parameters.device;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

import java.util.ArrayList;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.camera1.parameters.manual.whitebalance.BaseCCTManual;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomDevice extends AbstractDevice
{
    final String TAG = BaseQcomDevice.class.getName();
    public BaseQcomDevice(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    private boolean arrayContainsString(String[] ar,String dif)
    {
        boolean ret = false;
        for (String s: ar)
            if (s.equals(dif))
                ret = true;
        return ret;
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas)
    {
        if (focusAreas != null) {
            Camera.Area a = new Camera.Area(new Rect(focusAreas.left, focusAreas.top, focusAreas.right, focusAreas.bottom), 1000);
            ArrayList<Camera.Area> ar = new ArrayList<>();
            ar.add(a);
            parameters.setFocusAreas(ar);
        }
        else
            parameters.setFocusAreas(null);
        parametersHandler.SetParametersToCamera(parameters);

    }
}
