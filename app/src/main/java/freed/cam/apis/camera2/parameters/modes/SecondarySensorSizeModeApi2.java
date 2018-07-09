package freed.cam.apis.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.os.Build;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class SecondarySensorSizeModeApi2 extends BaseModeApi2 {

    private String size = "1920x1080";
    public SecondarySensorSizeModeApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.secondarySensorSize);
        setViewState(ViewState.Visible);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        fireStringValueChanged(valueToSet);
        SettingsManager.get(SettingKeys.secondarySensorSize).set(valueToSet);
        size = valueToSet;
        if (setToCamera)
        {
            cameraUiWrapper.stopPreviewAsync();
            cameraUiWrapper.startPreviewAsync();
        }
    }

    @Override
    public String GetStringValue()
    {
        return size;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String[] getStringValues()
    {
        return SettingsManager.get(SettingKeys.secondarySensorSize).getValues();
    }
}
