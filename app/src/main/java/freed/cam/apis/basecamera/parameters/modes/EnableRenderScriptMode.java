package freed.cam.apis.basecamera.parameters.modes;


import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.BooleanSettingModeInterface;

public class EnableRenderScriptMode extends FocusPeakMode implements BooleanSettingModeInterface {

    protected ApiBooleanSettingMode settingMode;

    public EnableRenderScriptMode(CameraWrapperInterface cameraUiWrapper, ApiBooleanSettingMode settingMode) {
        super(cameraUiWrapper);
        this.settingMode = settingMode;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_)))
        {
            settingMode.set(true);
            fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_));
        }
        else {
            settingMode.set(false);
            fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_));
        }
        cameraUiWrapper.restartCameraAsync();

    }

    @Override
    public boolean get() {
        return settingMode.get();
    }

    @Override
    public void set(boolean bool) {
        if (bool)
        {
            fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_));
        }
        else
            fireStringValueChanged(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_));
        settingMode.set(bool);
        cameraUiWrapper.restartCameraAsync();
    }
}
