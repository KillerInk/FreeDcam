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
        super.SetParameter(cameraUiWrapper.camParametersHandler.Module);
        if (cameraUiWrapper.moduleHandler == null)
            return;
        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null)
            onValueChanged(cameraUiWrapper.moduleHandler.GetCurrentModule().ShortName());
    }

    @Override
    public void ParametersLoaded() {
        if (cameraUiWrapper.moduleHandler == null)
            return;

        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null)
            onValueChanged(cameraUiWrapper.moduleHandler.GetCurrentModule().ShortName());
    }

    @Override
    public String ModuleChanged(String module) {
        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null)
            onValueChanged(cameraUiWrapper.moduleHandler.GetCurrentModule().ShortName());
        return module;
    }
}
