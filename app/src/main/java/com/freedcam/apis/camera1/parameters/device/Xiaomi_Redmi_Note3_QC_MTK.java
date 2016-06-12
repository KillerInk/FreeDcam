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

package com.freedcam.apis.camera1.parameters.device;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.FocusRect;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_ManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.CameraHolder.Frameworks;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.BaseISOManual;
import com.freedcam.apis.camera1.parameters.manual.mtk.AE_Handler_MTK;
import com.freedcam.apis.camera1.parameters.manual.mtk.FocusManualMTK;
import com.freedcam.apis.camera1.parameters.manual.qcom_new.FocusManual_QcomM;
import com.freedcam.apis.camera1.parameters.manual.qcom_new.ShutterManual_ExposureTime_Micro;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;
import com.troop.androiddng.DngProfile;

import java.util.ArrayList;

import static com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter.NEXUS6;

/**
 * Created by troop on 31.05.2016.
 */
public class Xiaomi_Redmi_Note3_QC_MTK extends AbstractDevice
{
    private Frameworks frameworks;
    private AE_Handler_MTK ae_handler_mtk;
    public Xiaomi_Redmi_Note3_QC_MTK(Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        frameworks = cameraHolder.DeviceFrameWork;
        if (frameworks == Frameworks.MTK)
            ae_handler_mtk = new AE_Handler_MTK(parameters, cameraUiWrapper,2700);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    //gets set due ae handler
    @Override
    public I_ManualParameter getExposureTimeParameter()
    {
        if (frameworks == Frameworks.MTK)
            return ae_handler_mtk.shutterPrameter;
        else
            return new ShutterManual_ExposureTime_Micro(parameters, cameraUiWrapper,KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME,false);
    }
    //gets set due ae handler
    @Override
    public I_ManualParameter getIsoParameter() {
        if (frameworks == Frameworks.MTK)
            return ae_handler_mtk.isoManualParameter;
        else
            return new BaseISOManual(parameters,"continuous-iso",parameters.getInt("min-iso"),parameters.getInt("max-iso"), cameraUiWrapper,1);
    }

    @Override
    public I_ManualParameter getManualFocusParameter()
    {
        if (frameworks == Frameworks.MTK)
            return new FocusManualMTK(parameters, cameraUiWrapper);
        else
            return new FocusManual_QcomM(parameters, cameraUiWrapper,1);
    }

    @Override
    public I_ManualParameter getCCTParameter()
    {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        switch (filesize)
        {
            case 26023936://xiaomi redmi note3 mtk
                return new DngProfile(64, 4192, 3104, DngProfile.Plain, DngProfile.GBRG, 0, matrixChooserParameter.GetCustomMatrix(NEXUS6));
            case 20389888: //xiaomi redmi note3 / pro
                return new DngProfile(64, 4632, 3480, DngProfile.Mipi16, DngProfile.GRBG, 0,matrixChooserParameter.GetCustomMatrix(NEXUS6));
        }
        return null;
    }

    @Override
    public AbstractModeParameter getDenoiseParameter() {
        if (frameworks == Frameworks.MTK)
        {
            if(parameters.get(KEYS.MTK_NOISE_REDUCTION_MODE)!=null) {
                if (parameters.get(KEYS.MTK_NOISE_REDUCTION_MODE_VALUES).equals("on,off")) {
                    return new BaseModeParameter(parameters, cameraUiWrapper, KEYS.MTK_NOISE_REDUCTION_MODE, KEYS.MTK_NOISE_REDUCTION_MODE_VALUES);
                }
            }
            return null;
        }
        else
            return new BaseModeParameter(parameters, cameraUiWrapper, KEYS.DENOISE, KEYS.DENOISE_VALUES);
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        if (parameters.get("touch-aec")!= null) {
            parameters.set("touch-aec", "on");
            parameters.set("touch-index-af", focusAreas.x + "," + focusAreas.y);
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
        else
        {
            Camera.Area a = new Camera.Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom),1000);
            ArrayList<Camera.Area> ar = new ArrayList<>();
            ar.add(a);
            parameters.setFocusAreas(ar);
            ((ParametersHandler)parametersHandler).SetParametersToCamera(parameters);
        }
    }
}
