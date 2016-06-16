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

import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.focus.BaseFocusManual;
import freed.cam.apis.camera1.parameters.manual.qcom.BaseISOManual;
import freed.cam.apis.camera1.parameters.manual.whitebalance.BaseWB_CCT_QC;
import freed.cam.apis.camera1.parameters.manual.qcom.ShutterManual_ExposureTime_Micro;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.dng.DngProfile;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomNew extends AbstractDevice
{
    public BaseQcomNew(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    //set by aehandler
    @Override
    public ManualParameterInterface getExposureTimeParameter() {
        return new ShutterManual_ExposureTime_Micro(parameters, cameraUiWrapper,KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME,false);
    }

    //set by aehandler
    @Override
    public ManualParameterInterface getIsoParameter() {
        return new BaseISOManual(parameters,KEYS.CONTINUOUS_ISO, parameters.getInt(KEYS.MIN_ISO), parameters.getInt(KEYS.MAX_ISO), cameraUiWrapper,1);
    }

    @Override
    public ManualParameterInterface getManualFocusParameter() {
        return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0, 100,KEYS.KEY_FOCUS_MODE_MANUAL, cameraUiWrapper,1,2);
    }

    @Override
    public ManualParameterInterface getCCTParameter() {
        return new BaseWB_CCT_QC(parameters, 8000,2000, cameraUiWrapper,100, KEYS.WB_MODE_MANUAL_CCT);
    }

    @Override
    public ManualParameterInterface getSkintoneParameter() {
        return null;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return null;
    }

    @Override
    public AbstractModeParameter getDenoiseParameter() {
        return new BaseModeParameter(parameters, cameraUiWrapper, KEYS.DENOISE, KEYS.DENOISE_VALUES);
    }

    @Override
    public float getCurrentExposuretime()
    {
        return Float.parseFloat(cameraHolder.GetParamsDirect("cur-exposure-time"));
    }

    @Override
    public int getCurrentIso() {
        return Integer.parseInt(cameraHolder.GetParamsDirect("cur-iso"));
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        parameters.set("touch-aec", "on");
        parameters.set("touch-index-af", focusAreas.x + "," + focusAreas.y);
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
