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

package com.troop.freedcam.camera.basecamera.parameters.modes;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.eventbus.events.ValueChangedEvent;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.ContextApplication;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by troop on 10.09.2015.
 */
public class FocusPeakMode extends AbstractParameter {
    public FocusPeakMode(CameraControllerInterface cameraUiWrapper)
    {
        super(cameraUiWrapper,SettingKeys.Focuspeak);
    }


    @Override
    public ViewState getViewState() {
        if (RenderScriptManager.isSupported() && cameraUiWrapper.getRenderScriptManager().isSucessfullLoaded() && SettingsManager.getGlobal(SettingKeys.EnableRenderScript).get())
            return ViewState.Visible;
        else
            return ViewState.Hidden;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)))
        {
            cameraUiWrapper.getFocusPeakProcessor().setFocusPeakEnable(true);
            fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().setFocusPeakEnable(false);
            fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        }

    }

    @Override
    public String GetStringValue() {
        if (cameraUiWrapper.getFocusPeakProcessor().isEnabled())
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_);
        else
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        return new String[] {ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_), ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_)};
    }



   /* @Subscribe
    public void onStringValueChanged(ValueChangedEvent<String> valueob)
    {
        if (valueob.key == SettingKeys.EnableRenderScript) {
            String value = valueob.newValue;
            if (value.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_)))
                setViewState(ViewState.Hidden);
            else
                setViewState(ViewState.Visible);
        }
    }*/
}
