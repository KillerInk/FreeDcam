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

package com.freedcam.apis.basecamera.parameters.modes;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_Module;
import com.freedcam.apis.basecamera.modules.AbstractModule;
import com.freedcam.utils.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 04.01.2016.
 */
public class ModuleParameters extends AbstractModeParameter {

    private I_CameraUiWrapper cameraUiWrapper;
    private AppSettingsManager appSettingsManager;
    public ModuleParameters(I_CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager) {
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public String[] GetValues() {
        List<String> mods = new ArrayList<>();
        for (HashMap.Entry<String, I_Module> module : cameraUiWrapper.GetModuleHandler().moduleList.entrySet()) {
            mods.add(module.getValue().LongName());
        }
        return mods.toArray(new String[mods.size()]);
    }

    @Override
    public String GetValue() {
        if (cameraUiWrapper.GetModuleHandler().GetCurrentModule() != null)
            return cameraUiWrapper.GetModuleHandler().GetCurrentModule().ShortName();
        else return "";
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        for (HashMap.Entry<String, I_Module> module : cameraUiWrapper.GetModuleHandler().moduleList.entrySet()) {
            if (valueToSet.equals(module.getValue().LongName())) {
                appSettingsManager.SetCurrentModule(module.getValue().ModuleName());
                cameraUiWrapper.GetModuleHandler().SetModule(module.getValue().ModuleName());
                break;
            }

        }
    }

    @Override
    public boolean IsSupported() {
        return true;
    }
}