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

package com.freedcam.apis.camera1.camera.parameters.device.qcom;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_LGG4;
import com.freedcam.apis.camera1.camera.parameters.manual.CCTManualG4;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualParameterLG;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G4 extends LG_G2 {
    public LG_G4(Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        new AE_Handler_LGG4(parameters,cameraHolder, parametersHandler);
        parameters.set("lge-camera","1");
    }

    //set due Ae handler
    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }
    //set due Ae handler
    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new FocusManualParameterLG(parameters,cameraHolder, parametersHandler);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return new CCTManualG4(parameters, parametersHandler);
    }
    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 19976192: //g4 bayer mipi camera1
                return new DngProfile(64, 5312,2988,DngProfile.Mipi16, DngProfile.BGGR,0,
                        matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));
        }
        return null;
    }
}
