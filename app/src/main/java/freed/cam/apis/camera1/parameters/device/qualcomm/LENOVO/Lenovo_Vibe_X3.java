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

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.dng.DngProfile;

/**
 * Created by troop on 25.08.2016.
 */
public class Lenovo_Vibe_X3 extends BaseQcomNew {
    public Lenovo_Vibe_X3(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 26935296:
                return new DngProfile(64,5344,4017,DngProfile.Mipi,DngProfile.BGGR, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX230));
            case 10186752:
                return new DngProfile(16,3264,2472,DngProfile.Mipi,DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.IMX230));
        }
        return null;
    }

    @Override
    public ModeParameterInterface getDigitalImageStabilisation() {
        return null;
    }
}
