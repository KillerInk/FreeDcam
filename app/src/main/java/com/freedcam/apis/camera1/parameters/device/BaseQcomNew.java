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
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_ManualParameter;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.parameters.manual.AE_Handler_QcomM;
import com.freedcam.apis.camera1.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.parameters.manual.BaseWB_CCT_QC;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomNew extends AbstractDevice
{
    protected AE_Handler_QcomM aeHandlerQcomM;

    public BaseQcomNew(Context context, Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(context, parameters, cameraUiWrapper);
        aeHandlerQcomM = new AE_Handler_QcomM(context, parameters, cameraUiWrapper, parametersHandler);
    }

    @Override
    public boolean IsDngSupported() {
        return true;
    }

    //set by aehandler
    @Override
    public I_ManualParameter getExposureTimeParameter() {
        return aeHandlerQcomM.getShutterManual();
    }

    //set by aehandler
    @Override
    public I_ManualParameter getIsoParameter() {
        return aeHandlerQcomM.getManualIso();
    }

    @Override
    public I_ManualParameter getManualFocusParameter() {
        return new BaseFocusManual(context,parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0, 100,KEYS.KEY_FOCUS_MODE_MANUAL, parametersHandler,1,2);
    }

    @Override
    public I_ManualParameter getCCTParameter() {
        return new BaseWB_CCT_QC(context,parameters, 8000,2000, parametersHandler,100, KEYS.WB_MODE_MANUAL_CCT);
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
}
