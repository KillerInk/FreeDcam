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

package freed.cam.apis.basecamera.parameters.modes;


import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingsManager;

/**
 * Created by troop on 10.09.2015.
 */
public class FocusPeakMode extends AbstractParameter implements ParameterEvents {
    public FocusPeakMode(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper,null);
    }


    @Override
    public boolean IsSupported()
    {
        return RenderScriptManager.isSupported() && cameraUiWrapper.getRenderScriptManager().isSucessfullLoaded();
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
        {
            cameraUiWrapper.getFocusPeakProcessor().setFocusPeakEnable(true);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.on_));
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().setFocusPeakEnable(false);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
        }

    }

    @Override
    public String GetStringValue() {
        if (cameraUiWrapper.getFocusPeakProcessor().isEnabled())
            return cameraUiWrapper.getResString(R.string.on_);
        else
            return cameraUiWrapper.getResString(R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        return new String[] {cameraUiWrapper.getResString(R.string.on_), cameraUiWrapper.getResString(R.string.off_)};
    }

    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String value) {
        if (value.equals(SettingsManager.getInstance().getResString(R.string.off_)))
            fireIsSupportedChanged(false);
        else
            fireIsSupportedChanged(true);
    }
}
