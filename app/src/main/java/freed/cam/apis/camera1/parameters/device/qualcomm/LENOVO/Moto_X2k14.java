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

package freed.cam.apis.camera1.parameters.device.qualcomm.LENOVO;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.camera1.parameters.device.AbstractDevice;

/**
 * Created by troop on 01.06.2016.
 */
public class Moto_X2k14 extends AbstractDevice {


    public Moto_X2k14(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        Camera.Area a = new Camera.Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom),1000);
        ArrayList<Camera.Area> ar = new ArrayList<>();
        ar.add(a);
        parameters.setFocusAreas(ar);
        parametersHandler.SetParametersToCamera(parameters);
    }
}
