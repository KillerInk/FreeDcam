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

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera1.parameters.manual.BaseCCTManual;
import com.freedcam.apis.camera1.parameters.manual.BaseFocusManual;
import com.freedcam.apis.camera1.parameters.manual.ShutterManual_ExposureTime_FloatToSixty;
import com.freedcam.apis.camera1.parameters.manual.ShutterManual_ExposureTime_Micro;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomDevice extends AbstractDevice {
    public BaseQcomDevice(Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return false;
    }

    @Override
    public AbstractManualParameter getExposureTimeParameter()
    {
        if (parameters.get(KEYS.MAX_EXPOSURE_TIME) != null && parameters.get(KEYS.EXPOSURE_TIME) != null && parameters.get(KEYS.MIN_EXPOSURE_TIME )!= null) {
            if (!parameters.get(KEYS.MAX_EXPOSURE_TIME).contains("."))
                return new ShutterManual_ExposureTime_FloatToSixty(parameters, parametersHandler, true);
            else
                return new ShutterManual_ExposureTime_Micro(parameters, parametersHandler,KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME, true);
        }
        return null;
    }

    @Override
    public AbstractManualParameter getIsoParameter() {
        return null;
    }

    @Override
    public AbstractManualParameter getManualFocusParameter()
    {
        if (parameters.get(KEYS.KEY_MANUAL_FOCUS_POSITION) != null && arrayContainsString(parametersHandler.FocusMode.GetValues(), KEYS.KEY_FOCUS_MODE_MANUAL))
            return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0,1000,KEYS.KEY_FOCUS_MODE_MANUAL, parametersHandler,10,1);
        return null;
    }

    @Override
    public AbstractManualParameter getCCTParameter()
    {
        String wbModeval ="", wbcur ="", wbmax = "",wbmin = "";
        if (parameters.get(KEYS.WB_CURRENT_CCT)!=null)
            wbcur = KEYS.WB_CURRENT_CCT;
        else if (parameters.get(KEYS.WB_CCT) != null)
            wbcur = KEYS.WB_CCT;
        else if (parameters.get(KEYS.WB_CT) != null)
            wbcur = KEYS.WB_CT;
        else if (parameters.get(KEYS.WB_MANUAL_CCT) != null)
            wbcur = KEYS.WB_MANUAL_CCT;
        else if (parameters.get(KEYS.MANUAL_WB_VALUE) != null)
            wbcur = KEYS.MANUAL_WB_VALUE;

        if (parameters.get(KEYS.MAX_WB_CCT) != null) {
            wbmax = KEYS.MAX_WB_CCT;
        }
        else if (parameters.get(KEYS.MAX_WB_CT)!= null)
            wbmax =KEYS.MAX_WB_CT;

        if (parameters.get(KEYS.MIN_WB_CCT)!= null) {
            wbmin =KEYS.MIN_WB_CCT;
        } else if (parameters.get(KEYS.MIN_WB_CT)!= null)
            wbmin =KEYS.MIN_WB_CT;

        if (arrayContainsString(parametersHandler.WhiteBalanceMode.GetValues(), KEYS.WB_MODE_MANUAL))
            wbModeval = KEYS.WB_MODE_MANUAL;
        else if (arrayContainsString(parametersHandler.WhiteBalanceMode.GetValues(), KEYS.WB_MODE_MANUAL_CCT))
            wbModeval = KEYS.WB_MODE_MANUAL_CCT;

        if (!wbcur.equals("") && !wbmax.equals("") && !wbmin.equals("") && wbModeval.equals(""))
            return new BaseCCTManual(parameters,wbcur,wbmax,wbmin, parametersHandler,100,wbModeval);
        else
            return null;
    }

    @Override
    public AbstractManualParameter getSkintoneParameter() {
        return null;
    }

    private boolean arrayContainsString(String[] ar,String dif)
    {
        boolean ret = false;
        for (String s: ar)
            if (s.equals(dif))
                ret = true;
        return ret;
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return null;
    }

    @Override
    public AbstractModeParameter getDenoiseParameter() {
        return new BaseModeParameter(parameters, cameraHolder, KEYS.DENOISE, KEYS.DENOISE_VALUES);
    }
}
