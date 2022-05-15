package freed.cam.apis.basecamera.parameters.modes;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;

public class SelfTimerParameter extends AbstractParameter {
    public SelfTimerParameter(SettingKeys.Key key) {
        super(key);
    }

    public SelfTimerParameter(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    @Override
    public String getStringValue() {
        return settingMode.get();
    }

    @Override
    public String[] getStringValues() {
        return settingMode.getValues();
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        settingMode.set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public ViewState getViewState() {
        return ViewState.Visible;
    }
}
