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
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.parameters.device.BaseQcomDevice;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseCCTManual;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.SkintoneManualPrameter;
import com.freedcam.apis.camera1.camera.parameters.modes.OpCodeParameter;
import com.freedcam.utils.DeviceUtils;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class Xiaomi_Mi3W extends BaseQcomDevice {
    public Xiaomi_Mi3W(Handler uihandler, Camera.Parameters parameters, CameraUiWrapper cameraUiWrapper) {
        super(uihandler, parameters, cameraUiWrapper);
    }

    @Override
    public AbstractManualParameter getCCTParameter() {
        if(!DeviceUtils.isCyanogenMod()) {
            if (Build.VERSION.SDK_INT < 23) {
                return new BaseCCTManual(parameters, KEYS.WB_MANUAL_CCT, 7500, 2000, parametersHandler, 100, KEYS.WB_MODE_MANUAL);
            } else
                return new BaseCCTManual(parameters, KEYS.WB_MANUAL_CCT, 8000, 2000, parametersHandler, 100, KEYS.WB_MODE_MANUAL_CCT);
        }
        else
            return super.getCCTParameter();
    }

    @Override
    public AbstractManualParameter getSkintoneParameter() {
        AbstractManualParameter Skintone = new SkintoneManualPrameter(parameters,parametersHandler);
        parametersHandler.PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Skintone).GetModuleListner());
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
            case 17522688:
                return new DngProfile(0, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16424960:
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE,matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 2969600:
                return new DngProfile(64,1976,1200,DngProfile.Mipi16,DngProfile.RGGB,0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 3170304://Xiaomi_mi3 front Qcom
                return new DngProfile(0, 1976, 1200, DngProfile.Qcom, DngProfile.RGGB, 0, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
        }
        return null;
    }

    @Override
    public AbstractModeParameter getOpCodeParameter() {
        return new OpCodeParameter();
    }
}
