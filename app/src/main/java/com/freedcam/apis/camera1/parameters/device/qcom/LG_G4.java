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

import android.content.Context;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.parameters.manual.AE_Handler_LGG4;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.manual.CCTManualG4;
import com.freedcam.apis.camera1.parameters.manual.FocusManualParameterLG;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G4 extends LG_G2
{
    private AE_Handler_LGG4 ae_handler_lgg4;
    public LG_G4(Context context, Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(context,parameters,cameraUiWrapper);
        ae_handler_lgg4 = new AE_Handler_LGG4(parameters,cameraHolder, parametersHandler);
        parameters.set("lge-camera","1");
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return ae_handler_lgg4.getShutterManual();
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return ae_handler_lgg4.getManualIso();
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

    @Override
    public AbstractManualParameter getManualSaturation() {
        return new BaseManualParameter(parameters, KEYS.LG_COLOR_ADJUST,KEYS.LG_COLOR_ADJUST_MAX,KEYS.LG_COLOR_ADJUST_MIN,parametersHandler,1);
    }

    @Override
    public float getCurrentExposuretime() {
        return Float.parseFloat(cameraHolder.GetParamsDirect(KEYS.CUR_EXPOSURE_TIME));
    }

    @Override
    public int getCurrentIso() {
        return Integer.parseInt(cameraHolder.GetParamsDirect(KEYS.CUR_ISO));
    }
}
