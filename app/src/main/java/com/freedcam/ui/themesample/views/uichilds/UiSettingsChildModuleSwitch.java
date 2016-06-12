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

package com.freedcam.ui.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildModuleSwitch extends UiSettingsChild {
    private CameraWrapperInterface cameraUiWrapper;

    public UiSettingsChildModuleSwitch(Context context) {
        super(context);
    }

    public UiSettingsChildModuleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if(cameraUiWrapper.GetModuleHandler().moduleEventHandler != null)
            cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(this);
            cameraUiWrapper.GetParameterHandler().AddParametersLoadedListner(this);
        SetParameter(cameraUiWrapper.GetParameterHandler().Module);
        if (cameraUiWrapper.GetModuleHandler() == null)
            return;
        if (cameraUiWrapper.GetModuleHandler().GetCurrentModule() != null)
            onValueChanged(cameraUiWrapper.GetModuleHandler().GetCurrentModule().ShortName());
    }

    @Override
    public void ParametersLoaded() {
        if (cameraUiWrapper.GetModuleHandler() == null)
            return;

        if (cameraUiWrapper.GetModuleHandler().GetCurrentModule() != null)
            onValueChanged(cameraUiWrapper.GetModuleHandler().GetCurrentModule().ShortName());
    }

    @Override
    public void ModuleChanged(String module)
    {
        valueText.post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.GetModuleHandler().GetCurrentModule() != null)
                    valueText.setText(cameraUiWrapper.GetModuleHandler().GetCurrentModule().ShortName());
            }
        });
    }
}
