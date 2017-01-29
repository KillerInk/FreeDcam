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

package freed.cam.apis.camera1.parameters.device.qualcomm.LG;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.camera1.CameraHolder.Frameworks;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G2 extends BaseQcomDevice
{


    public LG_G2(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        if (cameraHolder == null)
            return;
        if (cameraHolder.DeviceFrameWork == Frameworks.LG)
            parameters.set("lge-camera","1");
    }


    @Override
    public ManualParameterInterface getIsoParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getCCTParameter() {
        return null;
    }


    @Override
    public AbstractModeParameter getOpCodeParameter() {
        return new OpCodeParameter(cameraUiWrapper.GetAppSettingsManager());
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        if (focusAreas != null) {
            Camera.Area a = new Camera.Area(new Rect(focusAreas.left, focusAreas.top, focusAreas.right, focusAreas.bottom), 1000);
            ArrayList<Camera.Area> ar = new ArrayList<>();
            ar.add(a);
            parameters.setFocusAreas(ar);
        }
        else
            parameters.setFocusAreas(null);
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
