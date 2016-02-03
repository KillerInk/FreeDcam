package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.NightModeParameter;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 13.06.2015.
 */
public class UiSettingsChildNight extends UiSettingsChild  {


    AbstractCameraUiWrapper cameraUiWrapper;
    //NightLogic nightLogic;
    boolean isPictureModule = true;

    public UiSettingsChildNight(Context context) {
        super(context);
    }

    public UiSettingsChildNight(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UiSettingsChildNight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;

        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());

    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        if (cameraUiWrapper instanceof CameraUiWrapper )
        {
            super.SetParameter(cameraUiWrapper.camParametersHandler.NightMode);
        }
        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());
    }

    @Override
    public String ModuleChanged(String module)
    {
        if (module.equals(AbstractModuleHandler.MODULE_PICTURE) || module.equals(AbstractModuleHandler.MODULE_INTERVAL))
            this.setVisibility(VISIBLE);
        else {
            cameraUiWrapper.camParametersHandler.NightMode.SetValue("off", true);
            this.setVisibility(GONE);
        }

        return module;
    }

    @Override
    public void ParametersLoaded()
    {
        sendLog("Parameters Loaded");
        if (parameter != null && parameter.IsSupported() &&
                (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_PICTURE)) || cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_INTERVAL))
        {
            setTextToTextBox(parameter);
            onIsSupportedChanged(true);
        }
        else
            onIsSupportedChanged(false);
    }

    /*private class NightLogic extends NightModeParameter
    {
        public NightLogic(Handler uiHandler) {
            super(uiHandler);
        }

        @Override
        public boolean IsSupported() {
            if (cameraUiWrapper instanceof CameraUiWrapper) {


                if (isPictureModule && !(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("bayer") ||
                        appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).contains("raw"))) {
                    return super.IsSupported();
                }
            }

            return false;
        }


        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            if (cameraUiWrapper instanceof CameraUiWrapper) {
                super.SetValue(valueToSet,setToCamera);

            }

        }



        @Override
        public String[] GetValues()
        {
            return super.GetValues();



        }
    }*/
}
