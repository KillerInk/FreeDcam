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

package com.freedcam.apis.camera1.parameters.modes;

import android.os.Build.VERSION;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.camera1.renderscript.FocusPeakProcessorAp1;


/**
 * Created by troop on 27.08.2015.
 */
public class FocusPeakModeParameter extends BaseModeParameter {

    private FocusPeakProcessorAp1 focusPeakProcessorAp1;
    public FocusPeakModeParameter(I_CameraUiWrapper cameraUiWrapper, FocusPeakProcessorAp1 focusPeakProcessorAp1)
    {
        super(null, cameraUiWrapper, "", "");
        this.focusPeakProcessorAp1 = focusPeakProcessorAp1;
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= 18;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(KEYS.ON))
        {
            cameraUiWrapper.GetParameterHandler().FocusMode.SetValue(cameraUiWrapper.GetParameterHandler().FocusMode.GetValue(),true);
            focusPeakProcessorAp1.Enable(true);
        }
        else
            focusPeakProcessorAp1.Enable(false);
    }

    @Override
    public String GetValue()
    {
        if (focusPeakProcessorAp1.isEnable())
            return KEYS.ON;
        else
            return KEYS.OFF;
    }

    @Override
    public String[] GetValues() {
        return new String[] {KEYS.ON, KEYS.OFF};
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
        if (value.equals(KEYS.TRUE))
            super.BackgroundValueHasChanged(KEYS.ON);
        else if (value.equals(KEYS.FALSE))
            super.BackgroundValueHasChanged(KEYS.OFF);
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
