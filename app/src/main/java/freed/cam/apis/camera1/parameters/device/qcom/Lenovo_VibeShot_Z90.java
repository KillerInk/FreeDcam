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

import com.troop.freedcam.R;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.parameters.device.BaseQcomNew;
import freed.cam.apis.camera1.parameters.manual.qcom.BaseISOManual;
import freed.cam.apis.camera1.parameters.manual.qcom.ShutterManual_ExposureTime_Micro;
import freed.dng.DngProfile;

/**
 * Created by troop on 16.06.2016.
 */
public class Lenovo_VibeShot_Z90 extends BaseQcomNew {
    public Lenovo_VibeShot_Z90(Camera.Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
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
            case 19992576:  //lenovo k920
                return new DngProfile(64, 5328,3000, DngProfile.Mipi, DngProfile.GBRG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }


    @Override
    public ManualParameterInterface getExposureTimeParameter()
    {
        return new ShutterManual_ExposureTime_Micro(parameters, cameraUiWrapper,cameraUiWrapper.getContext().getResources().getStringArray(R.array.mtk_shutter), KEYS.EXPOSURE_TIME);
    }

    @Override
    public ManualParameterInterface getIsoParameter() {
        return new BaseISOManual(parameters,KEYS.CONTINUOUS_ISO, 100, 1600, cameraUiWrapper,1);
    }

}
