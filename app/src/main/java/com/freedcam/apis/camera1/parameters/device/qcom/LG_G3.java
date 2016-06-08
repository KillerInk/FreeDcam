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

package com.freedcam.apis.camera1.parameters.device.qcom;

import android.hardware.Camera.Parameters;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.CameraUiWrapper;
import com.freedcam.apis.camera1.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.parameters.manual.FocusManualParameterLG;
import com.troop.androiddng.CustomMatrix;
import com.troop.androiddng.DngProfile;
import com.troop.androiddng.Matrixes;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G3 extends LG_G2
{

    public LG_G3(Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        parameters.set("lge-camera","1");
    }

    //not supported by device
    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }

    //not supported by device
    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    public boolean IsDngSupported() {
        return true;
    }
    @Override
    public AbstractManualParameter getManualFocusParameter() {
        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0,1023,KEYS.KEY_FOCUS_MODE_MANUAL, parametersHandler,10,1);
        }
        else if (VERSION.SDK_INT < 21)
            return new FocusManualParameterLG(parameters,cameraHolder, parametersHandler);
        else
            return null;
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 2658304: //g3 front mipi
                return new DngProfile(64, 1212, 1096, DngProfile.Mipi, DngProfile.BGGR, 2424,
                        new CustomMatrix(Matrixes.CC_A_FRONT,
                                Matrixes.CC_D65_FRONT,
                                Matrixes.neutral_light_front,
                                Matrixes.G4_foward_matrix1,
                                Matrixes.G4_foward_matrix2,
                                Matrixes.G4_reduction_matrix1,
                                Matrixes.G4_reduction_matrix2,
                                Matrixes.G4_noise_3x1_matrix));
            case 2842624://g3 front qcom
                //TODO somethings wrong with it;
                return new DngProfile(64, 1296, 1096, DngProfile.Qcom, DngProfile.BGGR, 0,
                        new CustomMatrix(Matrixes.CC_A_FRONT,
                                Matrixes.CC_D65_FRONT,
                                Matrixes.neutral_light_front,
                                Matrixes.G4_foward_matrix1,
                                Matrixes.G4_foward_matrix2,
                                Matrixes.G4_reduction_matrix1,
                                Matrixes.G4_reduction_matrix2,
                                Matrixes.G4_noise_3x1_matrix));
            case 16224256:
				return new DngProfile(64, 4208, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16424960:
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 17326080://qcom g3
                return new DngProfile(64, 4164, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 17522688:
                return new DngProfile(64, 4212, 3082, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }
}
