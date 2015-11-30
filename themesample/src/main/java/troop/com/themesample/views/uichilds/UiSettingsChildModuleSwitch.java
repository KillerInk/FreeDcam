package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildModuleSwitch extends UiSettingsChild {
    AbstractCameraUiWrapper cameraUiWrapper;

    public UiSettingsChildModuleSwitch(Context context) {
        super(context);
    }

    public UiSettingsChildModuleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if(cameraUiWrapper.moduleHandler.moduleEventHandler != null)
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        if (cameraUiWrapper.camParametersHandler.ParametersEventHandler != null)
            cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        super.SetParameter(new ModuleParamters(this.getHandler()));
    }

    @Override
    public void ParametersLoaded() {
        if (cameraUiWrapper.moduleHandler == null)
            return;
        if (cameraUiWrapper.moduleHandler.GetCurrentModule() == null) {
            cameraUiWrapper.moduleHandler.SetModule(appSettingsManager.GetCurrentModule());

        }
        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null)
            onValueChanged(cameraUiWrapper.moduleHandler.GetCurrentModule().ShortName());
    }

    private class ModuleParamters extends AbstractModeParameter
    {

        public ModuleParamters(Handler uiHandler) {
            super(uiHandler);
        }

        @Override
        public String[] GetValues() {
            List<String> mods = new ArrayList<String>();
            for (HashMap.Entry<String,AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet())
            {
                mods.add(module.getValue().LongName());
            }
            return mods.toArray(new String[mods.size()]);
        }

        @Override
        public String GetValue()
        {
            if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null)
                return cameraUiWrapper.moduleHandler.GetCurrentModule().ShortName();
            else return "";
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            for (HashMap.Entry<String,AbstractModule> module : cameraUiWrapper.moduleHandler.moduleList.entrySet())
            {
                if (valueToSet.equals(module.getValue().LongName()))
                {
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
}
