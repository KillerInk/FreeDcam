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
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.camera1.parameters.manual.focus.BaseFocusManual;
import freed.cam.apis.camera1.parameters.manual.qcom.ShutterManual_ExposureTime_Micro;
import freed.cam.apis.camera1.parameters.manual.shutter.ShutterManual_ExposureTime_FloatToSixty;
import freed.cam.apis.camera1.parameters.manual.whitebalance.BaseCCTManual;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.dng.DngProfile;
import freed.utils.Logger;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomDevice extends AbstractDevice
{
    final String TAG = BaseQcomDevice.class.getName();
    public BaseQcomDevice(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public boolean IsDngSupported() {
        return false;
    }

    @Override
    public ManualParameterInterface getExposureTimeParameter()
    {
        if (parameters.get(KEYS.MAX_EXPOSURE_TIME) != null && parameters.get(KEYS.MIN_EXPOSURE_TIME )!= null) {
            if (!parameters.get(KEYS.MAX_EXPOSURE_TIME).contains("."))
                return new ShutterManual_ExposureTime_FloatToSixty(parameters, cameraUiWrapper, true);
            else
                return new ShutterManual_ExposureTime_Micro(parameters, cameraUiWrapper,KEYS.EXPOSURE_TIME, KEYS.MAX_EXPOSURE_TIME, KEYS.MIN_EXPOSURE_TIME, true);
        }
        return null;
    }

    @Override
    public ManualParameterInterface getIsoParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getManualFocusParameter()
    {
        if (parameters.get(KEYS.MAX_FOCUS_POS_INDEX) != null && arrayContainsString(parametersHandler.FocusMode.GetValues(), KEYS.KEY_FOCUS_MODE_MANUAL))
            return new BaseFocusManual(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION,0,1000,KEYS.KEY_FOCUS_MODE_MANUAL, cameraUiWrapper,10,1);
        return null;
    }

    @Override
    public ManualParameterInterface getCCTParameter()
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

        if (!wbcur.equals("") && !wbmax.equals("") && !wbmin.equals("") && !wbModeval.equals("")) {
            Logger.d(TAG, "Found all wbct values:" + wbcur + " " +wbmax + " " + wbmin + " " +wbModeval);
            return new BaseCCTManual(parameters, wbcur, wbmax, wbmin, cameraUiWrapper, 100, wbModeval);
        }
        else {
            Logger.d(TAG, "Failed to lookup wbct:" + wbcur + " " +wbmax + " " + wbmin + " " +wbModeval);
            return null;
        }
    }

    @Override
    public ManualParameterInterface getSkintoneParameter() {
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
    public ModeParameterInterface getDenoiseParameter() {
        return new BaseModeParameter(parameters, cameraUiWrapper, KEYS.DENOISE, KEYS.DENOISE_VALUES);
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas)
    {
        if (focusAreas != null) {
            Camera.Area a = new Camera.Area(new Rect(focusAreas.left, focusAreas.top, focusAreas.right, focusAreas.bottom), 1000);
            ArrayList<Camera.Area> ar = new ArrayList<>();
            ar.add(a);
            parameters.setFocusAreas(ar);
        }
        else
            parameters.setFocusAreas(null);
        parametersHandler.SetParametersToCamera(parameters);

    }
}
