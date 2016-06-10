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
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.parameters.manual.htc.CCTManualHtc;
import com.freedcam.apis.camera1.parameters.manual.lg.FocusManualParameterHTC;
import com.freedcam.apis.camera1.parameters.manual.htc.ShutterManualParameterHTC;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;
import com.freedcam.apis.camera1.parameters.modes.NonZslManualModeParameter;
import com.freedcam.apis.camera1.parameters.modes.OpCodeParameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class HTC_M8 extends AbstractDevice {


    public HTC_M8(Context context, Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(context, parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManualParameterHTC(context,parameters,"","", parametersHandler);
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new FocusManualParameterHTC(context,parameters, cameraHolder, parametersHandler);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return new CCTManualHtc(context,parameters, parametersHandler);
    }

    @Override
    public AbstractManualParameter getSkintoneParameter() {
        return null;
    }


    @Override
    public DngProfile getDngProfile(int filesize) {
        if (filesize < 6000000 && filesize > 5382641) //qcom
            return new DngProfile(0, 2688, 1520, DngProfile.Qcom, DngProfile.GRBG, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
        else if (filesize <= 5382641 && filesize > 5000000)//M8 mipi
            return new DngProfile(0, 2688, 1520, DngProfile.Mipi16, DngProfile.GRBG, DngProfile.HTCM8_rowSize,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
        return null;
    }

    @Override
    public AbstractModeParameter getNonZslManualMode() {
        return  new NonZslManualModeParameter(parameters, cameraUiWrapper);
    }

    @Override
    public AbstractModeParameter getOpCodeParameter() {
        return new OpCodeParameter(cameraUiWrapper.GetAppSettingsManager());
    }

    @Override
    public AbstractModeParameter getDenoiseParameter() {
        return new BaseModeParameter(parameters, cameraUiWrapper, KEYS.DENOISE, KEYS.DENOISE_VALUES);
    }
}
