package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class RawSizeModeApi2 extends BaseModeApi2 {
    public RawSizeModeApi2(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    public RawSizeModeApi2(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Integer> parameterKey) {
        super(cameraUiWrapper, key, parameterKey);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        SettingsManager.get(SettingKeys.RawSize).set(valueToSet);
        if (setToCamera)
        {
            cameraUiWrapper.stopPreviewAsync();
            cameraUiWrapper.startPreviewAsync();
        }
    }

    @Override
    public String GetStringValue()
    {
        return  SettingsManager.get(SettingKeys.RawSize).get();
    }

    @Override
    public String[] getStringValues()
    {
        return SettingsManager.get(SettingKeys.RawSize).getValues();
    }
}
