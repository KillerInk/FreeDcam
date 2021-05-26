package freed.cam.apis.basecamera.parameters.modes;


import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.events.EventBusHelper;
import freed.cam.events.SwichCameraFragmentEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class EnableRenderScriptMode extends AbstractParameter {

    public EnableRenderScriptMode(SettingKeys.Key cameraUiWrapper) {
        super(cameraUiWrapper);
        setViewState(ViewState.Visible);
        fireStringValueChanged(settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get());
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).set(valueToSet);
        fireStringValueChanged(valueToSet);
        EventBusHelper.post(new SwichCameraFragmentEvent());
        //cameraUiWrapper.restartCameraAsync();
        //cameraUiWrapper.getActivityInterface()..restartCameraAsync();

    }

    @Override
    public String GetStringValue() {
        return settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get();
    }

    @Override
    public String[] getStringValues() {
        return settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).getValues();
    }
}
