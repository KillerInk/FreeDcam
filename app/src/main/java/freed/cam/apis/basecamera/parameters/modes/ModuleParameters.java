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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameter;

/**
 * Created by troop on 04.01.2016.
 */
public class ModuleParameters extends AbstractParameter {

    public ModuleParameters(CameraWrapperInterface cameraUiWrapper) {
        super(null);
        this.cameraUiWrapper = cameraUiWrapper;
        setViewState(ViewState.Visible);
    }

    @Override
    public String[] getStringValues() {
        List<String> mods = new ArrayList<>();

        for (Object module : cameraUiWrapper.getModuleHandler().getModuleList().entrySet()) {

            HashMap.Entry<String, ModuleAbstract> mod = (HashMap.Entry<String, ModuleAbstract>)module;
            mods.add(mod.getValue().LongName());
        }
        return mods.toArray(new String[mods.size()]);
    }

    @Override
    public String getStringValue() {
        if (cameraUiWrapper.getModuleHandler().getCurrentModule() != null)
            return cameraUiWrapper.getModuleHandler().getCurrentModule().ShortName();
        else return "";
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera) {
        for (Object module : cameraUiWrapper.getModuleHandler().getModuleList().entrySet()) {
            HashMap.Entry<String, ModuleAbstract> mod = (HashMap.Entry<String, ModuleAbstract>)module;
            if (valueToSet.equals(mod.getValue().LongName())) {
                settingsManager.SetCurrentModule(mod.getValue().ModuleName());
                cameraUiWrapper.getModuleHandler().setModule(mod.getValue().ModuleName());
                break;
            }

        }
        fireStringValueChanged(valueToSet);
    }

}