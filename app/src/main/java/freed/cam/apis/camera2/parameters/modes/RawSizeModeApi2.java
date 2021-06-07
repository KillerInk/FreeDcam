package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;

import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

public class RawSizeModeApi2 extends BaseModeApi2 {
    public RawSizeModeApi2(Camera2 cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    public RawSizeModeApi2(Camera2 cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Integer> parameterKey) {
        super(cameraUiWrapper, key, parameterKey);
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        settingsManager.get(SettingKeys.RawSize).set(valueToSet);
        if (setToCamera)
        {
            CameraThreadHandler.restartPreviewAsync();
        }
    }

    @Override
    public String getStringValue()
    {
        return  settingsManager.get(SettingKeys.RawSize).get();
    }

    @Override
    public String[] getStringValues()
    {
        return settingsManager.get(SettingKeys.RawSize).getValues();
    }
}
