package com.freedcam.apis.basecamera.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.modules.AbstractModule;
import com.freedcam.utils.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 04.01.2016.
 */
public class ModuleParameters extends AbstractModeParameter {

    private AbstractCameraUiWrapper cameraUiWrapper;
    private AppSettingsManager appSettingsManager;
    public ModuleParameters(Handler uiHandler, AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager) {
        super(uiHandler);
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public String[] GetValues() {
        List<String> mods = new ArrayList<>();
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