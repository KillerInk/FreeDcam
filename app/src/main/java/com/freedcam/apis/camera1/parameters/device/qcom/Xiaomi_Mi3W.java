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

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.interfaces.ManualParameterInterface;
import com.freedcam.apis.basecamera.interfaces.ModeParameterInterface;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.camera1.parameters.device.BaseQcomDevice;
import com.freedcam.apis.camera1.parameters.manual.BaseCCTManual;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.manual.SkintoneManualPrameter;
import com.freedcam.apis.camera1.parameters.modes.NightModeXiaomi;
import com.freedcam.apis.camera1.parameters.modes.OpCodeParameter;
import com.freedcam.utils.DeviceUtils;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 01.06.2016.
 */
public class Xiaomi_Mi3W extends BaseQcomDevice {


    public Xiaomi_Mi3W(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public ManualParameterInterface getCCTParameter() {
        if(!DeviceUtils.isCyanogenMod()) {
            if (VERSION.SDK_INT < 23) {
                return new BaseCCTManual(parameters, KEYS.WB_MANUAL_CCT, 7500, 2000, cameraUiWrapper, 100, KEYS.WB_MODE_MANUAL);
            } else
                return new BaseCCTManual(parameters, KEYS.WB_MANUAL_CCT, 8000, 2000, cameraUiWrapper, 100, KEYS.WB_MODE_MANUAL_CCT);
        }
        else
            return super.getCCTParameter();
    }

    @Override
    public ManualParameterInterface getSkintoneParameter() {
        AbstractManualParameter Skintone = new SkintoneManualPrameter(parameters,cameraUiWrapper);
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
    public ModeParameterInterface getOpCodeParameter() {
        return new OpCodeParameter(cameraUiWrapper.GetAppSettingsManager());
    }

    @Override
    public ModeParameterInterface getNightMode() {
        return new NightModeXiaomi(parameters,cameraUiWrapper);
    }
}
