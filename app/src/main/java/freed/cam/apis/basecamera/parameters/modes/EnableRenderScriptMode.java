package freed.cam.apis.basecamera.parameters.modes;


import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.events.EventBusHelper;
import freed.cam.events.SwichCameraFragmentEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.BooleanSettingModeInterface;

public class EnableRenderScriptMode extends FocusPeakMode implements BooleanSettingModeInterface {


    public EnableRenderScriptMode(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            SettingsManager.getGlobal(SettingKeys.EnableRenderScript).set(true);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.on_));
        }
        else {
            SettingsManager.getGlobal(SettingKeys.EnableRenderScript).set(false);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
        }
        //EventBusHelper.post(new SwichCameraFragmentEvent());
        cameraUiWrapper.restartCameraAsync();
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
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.on_));
        }
        else
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
        SettingsManager.getGlobal(SettingKeys.EnableRenderScript).set(bool);
        EventBusHelper.post(new SwichCameraFragmentEvent());
    }
}
