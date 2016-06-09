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

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.CameraHolder.Frameworks;
import com.freedcam.apis.camera1.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.parameters.manual.FocusManualParameterLG;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;
import com.freedcam.apis.camera1.parameters.modes.OpCodeParameter;
import com.freedcam.apis.camera1.parameters.modes.VideoProfilesG3Parameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class LG_G2 extends AbstractDevice
{
    public LG_G2(Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        if (cameraHolder.DeviceFrameWork == Frameworks.LG)
            parameters.set("lge-camera","1");
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return null;
    }

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
        return null;
    }

    @Override
    public AbstractManualParameter getSkintoneParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 16224256:
                return new DngProfile(64, 4208, 3082, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16424960:
                return new DngProfile(64, 4212, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }



    @Override
    public AbstractModeParameter getVideoProfileMode()
    {
        if (cameraHolder.DeviceFrameWork == Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
            return new VideoProfilesG3Parameter(parameters,cameraUiWrapper);
        else
            return super.getVideoProfileMode();
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
