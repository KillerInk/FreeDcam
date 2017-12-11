package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingsManager;

/**
 * Created by troop on 19.06.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FocusMode extends BaseModeApi2 {
    public FocusMode(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    public FocusMode(CameraWrapperInterface cameraUiWrapper, SettingsManager.SettingMode settingMode, CaptureRequest.Key<Integer> parameterKey) {
        super(cameraUiWrapper, settingMode, parameterKey);
    }


    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        int toset = parameterValues.get(valueToSet);
        switch (toset)
        {
            case CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE:
            case CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO:
                captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                break;
        }
    }
}
