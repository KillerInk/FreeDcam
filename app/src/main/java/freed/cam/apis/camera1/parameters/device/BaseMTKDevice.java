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
import freed.cam.apis.camera1.parameters.manual.mtk.AE_Handler_MTK;
import freed.cam.apis.camera1.parameters.manual.mtk.BaseManualParamMTK;
import freed.cam.apis.camera1.parameters.manual.mtk.FocusManualMTK;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.dng.DngProfile;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 01.06.2016.
 * this class represent a basic mtk device and int/loads the parameters for that
 *
 */
public class BaseMTKDevice extends AbstractDevice
{
    protected AE_Handler_MTK ae_handler_mtk;

    public BaseMTKDevice(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        ae_handler_mtk = new AE_Handler_MTK(parameters, cameraUiWrapper,1600);
        parameters.set("afeng_raw_dump_flag", "1");
        parameters.set("rawsave-mode", "2");
        parameters.set("isp-mode", "1");
        parameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/test."+ FileEnding.BAYER);
    }

    @Override
    public boolean IsDngSupported() {
        return false;
    }

    //set by aehandler to camparametershandler direct
    @Override
    public ManualParameterInterface getExposureTimeParameter() {
        return ae_handler_mtk.getShutterManual();
    }
    //set by aehandler to camparametershandler direct
    @Override
    public ManualParameterInterface getIsoParameter() {
        return ae_handler_mtk.getManualIso();
    }

    @Override
    public ManualParameterInterface getManualFocusParameter()
    {
        if(parameters.get("afeng-max-focus-step")!=null)
            return new FocusManualMTK(parameters, cameraUiWrapper);
       /* else  if(parameters.get("focus-fs-fi-max") != null)
            return new FocusManualMTK(parameters,"focus-fs-fi","focus-fs-fi-max","focus-fs-fi-min", parametersHandler,10,0);*/
        else
            return null;
    }

    @Override
    public ManualParameterInterface getCCTParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getSkintoneParameter() {
        return null;
    }

    @Override
    public ManualParameterInterface getManualSaturation() {
        if (parameters.get(KEYS.SATURATION)!= null && parameters.get(KEYS.SATURATION_VALUES)!= null)
                return new BaseManualParamMTK(parameters,KEYS.SATURATION, KEYS.SATURATION_VALUES, cameraUiWrapper);
        return null;
    }

    @Override
    public ManualParameterInterface getManualSharpness() {
        return new BaseManualParamMTK(parameters,"edge","edge-values", cameraUiWrapper);
    }

    @Override
    public ManualParameterInterface getManualBrightness() {
        return new BaseManualParamMTK(parameters,"brightness", "brightness-values", cameraUiWrapper);
    }

    @Override
    public ManualParameterInterface getManualContrast() {
        return  new BaseManualParamMTK(parameters,"contrast","contrast-values", cameraUiWrapper);
    }

    @Override
    public DngProfile getDngProfile(int filesize) {
        return null;
    }

    @Override
    public ModeParameterInterface getDenoiseParameter() {
        if(parameters.get(KEYS.MTK_NOISE_REDUCTION_MODE)!=null) {
            if (parameters.get(KEYS.MTK_NOISE_REDUCTION_MODE_VALUES).equals("on,off")) {
                return new BaseModeParameter(parameters, cameraUiWrapper, KEYS.MTK_NOISE_REDUCTION_MODE, KEYS.MTK_NOISE_REDUCTION_MODE_VALUES);
            }
        }
        return null;
    }

    @Override
    public float getCurrentExposuretime() {
        if(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK)!= null) {
            if (Float.parseFloat(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK)) == 0) {
                return 0.0f;
            } else
                return Float.parseFloat(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK)) / 1000000;
        }
        else if(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK1)!= null)
        {
            if (Float.parseFloat(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK1)) == 0) {
                return 0.0f;
            } else
                return Float.parseFloat(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK1)) / 1000000;
        }
        else
            return 0.0f;
    }

    @Override
    public int getCurrentIso() {
        if(parameters.get(KEYS.CUR_ISO_MTK)!= null) {
            if (Integer.parseInt(parameters.get(KEYS.CUR_ISO_MTK)) == 0) {
                return 0;
            }
            return Integer.parseInt(parameters.get(KEYS.CUR_ISO_MTK)) / 256 * 100;
        }
        else if(parameters.get(KEYS.CUR_ISO_MTK2)!= null)
        {
            if (Integer.parseInt(parameters.get(KEYS.CUR_ISO_MTK2)) == 0) {
                return 0;
            }
            return Integer.parseInt(parameters.get(KEYS.CUR_ISO_MTK2)) / 256 * 100;
        }
        else
            return 0;
    }

    @Override
    public void Set_RAWFNAME(String filename) {
        parameters.set("rawfname", filename);
        cameraHolder.SetCameraParameters(parameters);
    }

    @Override
    public void SetFocusArea(FocusRect focusAreas) {
        Camera.Area a = new Camera.Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom),1000);
        ArrayList<Camera.Area> ar = new ArrayList<>();
        ar.add(a);
        parameters.setFocusAreas(ar);
        parametersHandler.SetParametersToCamera(parameters);
    }
}
