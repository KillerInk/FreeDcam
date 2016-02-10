package com.troop.freedcam.i_camera.parameters;

import android.os.Handler;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 04.01.2016.
 */
public class ModuleParameters extends AbstractModeParameter {

    private AppSettingsManager appSettingsManager;
    private AbstractCameraUiWrapper cameraUiWrapper;
    public ModuleParameters(Handler uiHandler, AppSettingsManager appSettingsManager, AbstractCameraUiWrapper cameraUiWrapper) {
        super(uiHandler);
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public String[] GetValues() {
        List<String> mods = new ArrayList<String>();
        for (HashMap.Entry<String, AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet()) {
            mods.add(module.getValue().LongName());
        }
        return mods.toArray(new String[mods.size()]);
    }

    @Override
    public String GetValue() {
        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null)
            return cameraUiWrapper.moduleHandler.GetCurrentModule().ShortName();
        else return "";
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        for (HashMap.Entry<String, AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet()) {
            if (valueToSet.equals(module.getValue().LongName())) {
                appSettingsManager.SetCurrentModule(module.getValue().ModuleName());
                cameraUiWrapper.moduleHandler.SetModule(module.getValue().ModuleName());
                break;
            }

        }
    }

    @Override
    public boolean IsSupported() {
        return true;
    }
}