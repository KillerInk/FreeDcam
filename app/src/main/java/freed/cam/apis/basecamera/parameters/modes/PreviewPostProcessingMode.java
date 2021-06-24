package freed.cam.apis.basecamera.parameters.modes;


import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;

public class PreviewPostProcessingMode extends AbstractParameter {

    public PreviewPostProcessingMode(SettingKeys.Key cameraUiWrapper) {
        super(cameraUiWrapper);
        setViewState(ViewState.Visible);
        fireStringValueChanged(settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get());
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).set(valueToSet);
        fireStringValueChanged(valueToSet);
        ActivityFreeDcamMain.cameraApiManager().changePreviewPostProcessing();
    }

    @Override
    public String getStringValue() {
        return settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get();
    }

    @Override
    public String[] getStringValues() {
        return settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).getValues();
    }
}
