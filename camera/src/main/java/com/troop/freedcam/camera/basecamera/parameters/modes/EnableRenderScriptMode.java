package com.troop.freedcam.camera.basecamera.parameters.modes;


import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.events.SwichCameraFragmentEvent;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.settings.mode.BooleanSettingModeInterface;
import com.troop.freedcam.utils.ContextApplication;

public class EnableRenderScriptMode extends FocusPeakMode implements BooleanSettingModeInterface {


    public EnableRenderScriptMode(CameraControllerInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.on_)))
        {
            SettingsManager.getGlobal(SettingKeys.EnableRenderScript).set(true);
            fireStringValueChanged(ContextApplication.getStringFromRessources(R.string.on_));
        }
        else {
            SettingsManager.getGlobal(SettingKeys.EnableRenderScript).set(false);
            fireStringValueChanged(ContextApplication.getStringFromRessources(R.string.off_));
        }
        EventBusHelper.post(new SwichCameraFragmentEvent());
        //cameraUiWrapper.getActivityInterface()..restartCameraAsync();

    }

    @Override
    public boolean get() {
        return SettingsManager.getGlobal(SettingKeys.EnableRenderScript).get();
    }

    @Override
    public void set(boolean bool) {
        if (bool)
        {
            fireStringValueChanged(ContextApplication.getStringFromRessources(R.string.on_));
        }
        else
            fireStringValueChanged(ContextApplication.getStringFromRessources(R.string.off_));
        SettingsManager.getGlobal(SettingKeys.EnableRenderScript).set(bool);
        EventBusHelper.post(new SwichCameraFragmentEvent());
    }
}
