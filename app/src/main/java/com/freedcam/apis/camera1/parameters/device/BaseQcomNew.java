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
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.FocusRect;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_ManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.parameters.manual.BaseISOManual;
import com.freedcam.apis.camera1.parameters.manual.BaseWB_CCT_QC;
import com.freedcam.apis.camera1.parameters.manual.qcom_new.ShutterManual_ExposureTime_Micro;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomNew extends AbstractDevice
{
    public BaseQcomNew(Context context, Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(context, parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    //set by aehandler
    @Override
    public I_ManualParameter getExposureTimeParameter() {
        return new ShutterManual_ExposureTime_Micro(parameters, cameraUiWrapper,KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME,false);
    }

    //set by aehandler
    @Override
    public I_ManualParameter getIsoParameter() {
        return new BaseISOManual(parameters,"continuous-iso",parameters.getInt("min-iso"),parameters.getInt("max-iso"), cameraUiWrapper,1);
    }

    @Override
    public I_ManualParameter getManualFocusParameter() {
        return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0, 100,KEYS.KEY_FOCUS_MODE_MANUAL, cameraUiWrapper,1,2);
    }

    @Override
    public I_ManualParameter getCCTParameter() {
        return new BaseWB_CCT_QC(parameters, 8000,2000, cameraUiWrapper,100, KEYS.WB_MODE_MANUAL_CCT);
    }

    @Override
    public I_ManualParameter getSkintoneParameter() {
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
