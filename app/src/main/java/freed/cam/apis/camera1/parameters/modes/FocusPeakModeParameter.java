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

package freed.cam.apis.camera1.parameters.modes;

import android.os.Build.VERSION;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.renderscript.FocusPeakProcessorAp1;


/**
 * Created by troop on 27.08.2015.
 */
public class FocusPeakModeParameter extends BaseModeParameter {

    private final FocusPeakProcessorAp1 focusPeakProcessorAp1;
    public FocusPeakModeParameter(CameraWrapperInterface cameraUiWrapper, FocusPeakProcessorAp1 focusPeakProcessorAp1)
    {
        super(null, cameraUiWrapper);
        this.focusPeakProcessorAp1 = focusPeakProcessorAp1;
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= 18 && cameraUiWrapper.getRenderScriptHandler().isSucessfullLoaded();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(KEYS.ON))
        {
            //set foucs mode at same stage again else on some devices the camera preview gets green
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
