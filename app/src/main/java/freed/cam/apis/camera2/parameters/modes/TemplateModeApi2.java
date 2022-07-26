package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;

import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

public class TemplateModeApi2 extends BaseModeApi2 {
    public TemplateModeApi2(Camera2 cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    public TemplateModeApi2(Camera2 cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Integer> parameterKey) {
        super(cameraUiWrapper, key, parameterKey);
    }

    @Override
    protected void setValue(String valueToSet, boolean setToCamera) {
        settingMode.set(valueToSet);
        fireStringValueChanged(valueToSet);
        CameraThreadHandler.restartPreviewAsync();
    }

}
