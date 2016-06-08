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

package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Build;
import android.os.Build.VERSION;

import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.apis.camera1.camera.renderscript.FocusPeakProcessorAp1;
import com.freedcam.utils.StringUtils;


/**
 * Created by troop on 27.08.2015.
 */
public class FocusPeakModeParameter extends BaseModeParameter {

    private FocusPeakProcessorAp1 focusPeakProcessorAp1;
    public FocusPeakModeParameter(CameraHolder cameraHolder, FocusPeakProcessorAp1 focusPeakProcessorAp1)
    {
        super(null, cameraHolder, "", "");
        this.focusPeakProcessorAp1 = focusPeakProcessorAp1;
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= 18;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(StringUtils.ON))
        {
            cameraHolder.GetParameterHandler().FocusMode.SetValue(cameraHolder.GetParameterHandler().FocusMode.GetValue(),true);
            focusPeakProcessorAp1.Enable(true);
        }
        else
            focusPeakProcessorAp1.Enable(false);
    }

    @Override
    public String GetValue()
    {
        if (focusPeakProcessorAp1.isEnable())
            return StringUtils.ON;
        else
            return StringUtils.OFF;
    }

    @Override
    public String[] GetValues() {
        return new String[] {StringUtils.ON, StringUtils.OFF};
    }

    @Override
    public void addEventListner(I_ModeParameterEvent eventListner)
    {
        super.addEventListner(eventListner);
    }

    @Override
    public void removeEventListner(I_ModeParameterEvent parameterEvent) {
        super.removeEventListner(parameterEvent);
    }

    @Override
    public void BackgroundValueHasChanged(String value)
    {
        if (value.equals("true"))
            super.BackgroundValueHasChanged(StringUtils.ON);
        else if (value.equals("false"))
            super.BackgroundValueHasChanged(StringUtils.OFF);
    }

    @Override
    public void BackgroundValuesHasChanged(String[] value) {

    }

    @Override
    public void BackgroundIsSupportedChanged(boolean value) {

    }

    @Override
    public void BackgroundSetIsSupportedHasChanged(boolean value) {

    }
}
