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

package freed.cam.apis.camera1.parameters.device;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import java.util.ArrayList;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera1.CameraHolder.Frameworks;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.mtk.AE_Handler_MTK;
import freed.cam.apis.camera1.parameters.manual.qcom.BaseISOManual;
import freed.cam.apis.camera1.parameters.manual.qcom.ShutterManual_ExposureTime_Micro;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.dng.DngProfile;


/**
 * Created by troop on 31.05.2016.
 */
public class Xiaomi_Redmi_Note3_QC_MTK extends AbstractDevice
{
    private final Frameworks frameworks;
    private AE_Handler_MTK ae_handler_mtk;
    public Xiaomi_Redmi_Note3_QC_MTK(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        frameworks = cameraHolder.DeviceFrameWork;
        if (frameworks == Frameworks.MTK)
            ae_handler_mtk = new AE_Handler_MTK(parameters, cameraUiWrapper,2700);
    }

    //gets set due ae handler
    @Override
    public ManualParameterInterface getExposureTimeParameter()
    {
        if (frameworks == Frameworks.MTK)
            return ae_handler_mtk.getShutterManual();
        else
            return new ShutterManual_ExposureTime_Micro(parameters, cameraUiWrapper,KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME,true);
    }
    //gets set due ae handler
    @Override
    public ManualParameterInterface getIsoParameter() {
        if (frameworks == Frameworks.MTK)
            return ae_handler_mtk.getManualIso();
        else
            return new BaseISOManual(parameters,KEYS.CONTINUOUS_ISO, parameters.getInt(KEYS.MIN_ISO), parameters.getInt(KEYS.MAX_ISO), cameraUiWrapper,1);
    }

    @Override
    public ManualParameterInterface getCCTParameter()
    {
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
            parametersHandler.SetParametersToCamera(parameters);
        }
    }

}
