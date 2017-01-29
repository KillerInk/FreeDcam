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
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.camera1.parameters.manual.mtk.AE_Handler_MTK;
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
    public ManualParameterInterface getCCTParameter() {
        return null;
    }


    @Override
    public long getCurrentExposuretime() {
        if(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK)!= null) {
            if (Long.parseLong(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK)) == 0) {
                return 0;
            } else
                return Long.parseLong(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK));
        }
        else if(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK1)!= null)
        {
            if (Long.parseLong(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK1)) == 0) {
                return 0;
            } else
                return Long.parseLong(parameters.get(KEYS.CUR_EXPOSURE_TIME_MTK1));
        }
        else
            return 0;
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
      //  Camera.Area a = new Camera.Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom),1000);
       // ArrayList<Camera.Area> ar = new ArrayList<>();
       // ar.add(a);
      // new ArrayList<Camera.Area>()
        List<Camera.Area> l = new ArrayList<>();
        l.add(new Camera.Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom), 1000));
        parameters.setFocusAreas(l);
        parametersHandler.SetParametersToCamera(parameters);
    }
}
