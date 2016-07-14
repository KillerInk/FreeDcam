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

package freed.cam.apis.camera1.parameters.device.mtk;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseMTKDevice;
import freed.dng.DngProfile;

/**
 * Created by troop on 14.07.2016.
 */
public class Mlais_M52_Red_Note extends BaseMTKDevice  {
    public Mlais_M52_Red_Note(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public DngProfile getDngProfile(int filesize)
    {
        switch (filesize)
        {
            case 15980544:
                return new DngProfile(16, 3264, 2448, DngProfile.Plain, DngProfile.BGGR, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}
