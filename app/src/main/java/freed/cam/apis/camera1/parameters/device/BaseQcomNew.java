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
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.camera1.parameters.manual.qcom.BaseISOManual;

/**
 * Created by troop on 02.06.2016.
 */
public class BaseQcomNew extends BaseQcomDevice
{
    public BaseQcomNew(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
    }

    @Override
    public ManualParameterInterface getIsoParameter() {
        return new BaseISOManual(parameters,KEYS.CONTINUOUS_ISO, parameters.getInt(KEYS.MIN_ISO), parameters.getInt(KEYS.MAX_ISO), cameraUiWrapper,1);
    }

    @Override
    public long getCurrentExposuretime()
    {
        return (long)Float.parseFloat(cameraHolder.GetParamsDirect("cur-exposure-time"))*1000000;
    }

    @Override
    public int getCurrentIso() {
        return Integer.parseInt(cameraHolder.GetParamsDirect("cur-iso"));
    }

}
