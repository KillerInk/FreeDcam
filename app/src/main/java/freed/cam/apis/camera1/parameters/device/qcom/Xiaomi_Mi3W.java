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

import android.hardware.Camera.Parameters;
import android.os.Build.VERSION;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.device.BaseQcomDevice;
import freed.cam.apis.camera1.parameters.manual.BaseCCTManual;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.SkintoneManualPrameter;
import freed.cam.apis.camera1.parameters.modes.NightModeXiaomi;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;
import freed.dng.DngProfile;
import freed.utils.DeviceUtils;

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
        AbstractManualParameter Skintone = new SkintoneManualPrameter(parameters, cameraUiWrapper);
        parametersHandler.PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
        cameraUiWrapper.GetModuleHandler().addListner(((BaseManualParameter) Skintone).GetModuleListner());
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
                return new DngProfile(0, 4212, 3120, DngProfile.Qcom, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
            case 16424960:
                return new DngProfile(64, 4208, 3120, DngProfile.Mipi, DngProfile.RGGB, DngProfile.ROWSIZE, matrixChooserParameter.GetCustomMatrix(MatrixChooserParameter.NEXUS6));
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
        return new NightModeXiaomi(parameters, cameraUiWrapper);
    }
}
