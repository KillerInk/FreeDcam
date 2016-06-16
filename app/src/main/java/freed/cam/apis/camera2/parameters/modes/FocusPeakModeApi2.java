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

package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by troop on 10.09.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class FocusPeakModeApi2 extends BaseModeApi2 {
    public FocusPeakModeApi2(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }


    @Override
    public boolean IsSupported()
    {
        return true;//cameraHolder.characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(KEYS.ON))
        {
            cameraUiWrapper.getFocusPeakProcessor().Enable(true);
            BackgroundValueHasChanged("true");
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().Enable(false);
            BackgroundValueHasChanged("false");
        }

    }

    @Override
    public String GetValue() {
        if (cameraUiWrapper.getFocusPeakProcessor().isEnabled())
            return KEYS.ON;
        else
            return KEYS.OFF;
    }

    @Override
    public String[] GetValues() {
        return new String[] {KEYS.ON, KEYS.OFF};
    }


    @Override
    public void BackgroundValueHasChanged(String value)
    {
        if (value.equals("true"))
            super.BackgroundValueHasChanged(KEYS.ON);
        else if (value.equals("false"))
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
