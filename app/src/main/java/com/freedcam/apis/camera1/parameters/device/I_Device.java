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

import com.freedcam.apis.basecamera.interfaces.I_ManualParameter;
import com.freedcam.apis.basecamera.interfaces.I_ModeParameter;
import com.troop.androiddng.DngProfile;

/**
 * Created by troop on 10.06.2016.
 */
public interface I_Device
{
    boolean IsDngSupported();
    DngProfile getDngProfile(int filesize);

    I_ManualParameter getExposureTimeParameter();
    I_ManualParameter getIsoParameter();
    I_ManualParameter getManualFocusParameter();
    I_ManualParameter getCCTParameter();
    I_ManualParameter getSkintoneParameter();
    I_ManualParameter getManualSaturation();
    I_ManualParameter getManualSharpness();
    I_ManualParameter getManualBrightness();
    I_ManualParameter getManualContrast();

    I_ModeParameter getVideoProfileMode();
    I_ModeParameter getNonZslManualMode();
    I_ModeParameter getOpCodeParameter();
    I_ModeParameter getDenoiseParameter();
    I_ModeParameter getLensFilter();
    I_ModeParameter getNightMode();

    float GetFnumber();
    float GetFocal();
    float getCurrentExposuretime();
    int getCurrentIso();
    void Set_RAWFNAME(String filename);
}
