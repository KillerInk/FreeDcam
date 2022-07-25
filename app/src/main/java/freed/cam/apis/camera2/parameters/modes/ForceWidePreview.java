package freed.cam.apis.camera2.parameters.modes;

import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;
import freed.settings.mode.BooleanSettingModeInterface;

public class ForceWidePreview extends AbstractParameter implements BooleanSettingModeInterface {
    BooleanSettingModeInterface booleanSettingModeInterface;

    public ForceWidePreview(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper, SettingKeys.FORCE_WIDE_PREVIEW);
        this.booleanSettingModeInterface = settingsManager.get(SettingKeys.FORCE_WIDE_PREVIEW);
    }

    @Override
    public boolean get() {
        if (booleanSettingModeInterface != null)
            return booleanSettingModeInterface.get();
        return false;
    }

    @Override
    public void set(boolean bool) {
        booleanSettingModeInterface.set(bool);
        CameraThreadHandler.restartPreviewAsync();
    }
}
