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

import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.dng.DngProfile;

/**
 * Created by troop on 10.06.2016.
 */
public interface I_Device
{
    boolean IsDngSupported();
    DngProfile getDngProfile(int filesize);

    ManualParameterInterface getExposureTimeParameter();
    ManualParameterInterface getIsoParameter();
    ManualParameterInterface getManualFocusParameter();
    ManualParameterInterface getCCTParameter();
    ManualParameterInterface getSkintoneParameter();
    ManualParameterInterface getManualSaturation();
    ManualParameterInterface getManualSharpness();
    ManualParameterInterface getManualBrightness();
    ManualParameterInterface getManualContrast();

    ModeParameterInterface getVideoProfileMode();
    ModeParameterInterface getNonZslManualMode();
    ModeParameterInterface getOpCodeParameter();
    ModeParameterInterface getDenoiseParameter();
    ModeParameterInterface getLensFilter();
    ModeParameterInterface getNightMode();

    boolean isLegacy();

    float GetFnumber();
    float GetFocal();
    float getCurrentExposuretime();
    int getCurrentIso();
    void Set_RAWFNAME(String filename);
    void SetFocusArea(FocusRect focusAreas);
}
