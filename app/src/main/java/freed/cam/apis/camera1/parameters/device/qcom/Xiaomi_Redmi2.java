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

package freed.cam.apis.camera1.parameters.device.qcom;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.dng.DngProfile;

/**
 * Created by troop on 25.07.2016.
 */
public class Xiaomi_Redmi2 extends BaseQcomDevice
{

    public Xiaomi_Redmi2(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize) {
            case 9990144:
                return new DngProfile(0, 3264, 2448, DngProfile.Mipi, DngProfile.BGGR, 4080, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 10653696:
                return new DngProfile(16, 3264, 2448, DngProfile.Qcom, DngProfile.BGGR, 0,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));

        }
        return null;
    }

    @Override
    public ManualParameterInterface getIsoParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getExposureTimeParameter() {
        return null;
    }
}
