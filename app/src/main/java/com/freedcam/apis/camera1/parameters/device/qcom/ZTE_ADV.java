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
import com.freedcam.apis.camera1.parameters.device.BaseQcomDevice;
import com.freedcam.apis.camera1.parameters.manual.BaseCCTManual;
import com.freedcam.apis.camera1.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.manual.ShutterManualZTE;
import com.freedcam.apis.camera1.parameters.manual.SkintoneManualPrameter;
import com.freedcam.apis.camera1.parameters.modes.OpCodeParameter;
import com.freedcam.apis.camera1.parameters.modes.VirtualLensFilter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class ZTE_ADV extends BaseQcomDevice {
    public ZTE_ADV(Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter() {
        return new ShutterManualZTE(parameters, cameraHolder, parametersHandler);
    }

    @Override
    public AbstractManualParameter getManualFocusParameter() {
        return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0,79,KEYS.KEY_FOCUS_MODE_MANUAL, parametersHandler,1,1);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        return new BaseCCTManual(parameters,KEYS.WB_MANUAL_CCT,8000,2000, parametersHandler,100, KEYS.WB_MODE_MANUAL_CCT);
    }

    @Override
    public AbstractManualParameter getSkintoneParameter() {
        AbstractManualParameter Skintone = new SkintoneManualPrameter(parameters,parametersHandler);
        parametersHandler.PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
        cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(((BaseManualParameter) Skintone).GetModuleListner());
        return Skintone;
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
            case 6721536:
                return new DngProfile(64,2592,1296,DngProfile.Qcom,DngProfile.BGGR,0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.OmniVision));
            case 16424960:
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.BGGR, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));
            case 17522688:
                return new DngProfile(64, 4212, 3120, DngProfile.Qcom, DngProfile.BGGR, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.G4));
        }
        return null;
    }

    @Override
    public AbstractModeParameter getOpCodeParameter() {
        return new OpCodeParameter(cameraUiWrapper.GetAppSettingsManager());
    }

    @Override
    public AbstractModeParameter getLensFilter() {
        return new VirtualLensFilter(parameters, cameraUiWrapper);
    }

    @Override
    public float GetFnumber() {
        return 14f;
    }

    @Override
    public float GetFocal() {
        return 28.0f;
    }
}
