package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

/**
 * Created by troop on 19.06.2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FocusMode extends BaseModeApi2 {

    public FocusMode(Camera2 cameraUiWrapper, SettingKeys.Key key, CaptureRequest.Key<Integer> parameterKey) {
        super(cameraUiWrapper, key, parameterKey);
    }

    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        if (parameterValues == null || parameterValues.get(valueToSet) == null)
            return;
        int toset = parameterValues.get(valueToSet);
        switch (toset)
        {
            case CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE:
            case CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO:
                if (!settingsManager.getIsFrontCamera()) {
                    //captureSessionHandler.SetFocusArea(CaptureRequest.CONTROL_AF_REGIONS, null);
                    if (captureSessionHandler == null || captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_TRIGGER) == null)
                        break;
                    if (captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_TRIGGER) != CaptureRequest.CONTROL_AF_TRIGGER_IDLE) {
                        captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                        captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
                    }
                }
                break;
        }
    }
}
