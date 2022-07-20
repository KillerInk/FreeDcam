package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;
import freed.settings.mode.BooleanSettingModeInterface;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MFNR extends BaseModeApi2 implements BooleanSettingModeInterface {
    public MFNR(Camera2 cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.MFNR);
        if (settingsManager.get(SettingKeys.MFNR).isSupported())
            setViewState(ViewState.Visible);
    }

    @Override
    public boolean get() {
        return settingsManager.get(SettingKeys.MFNR).get();
    }

    @Override
    public void set(boolean bool) {
        settingsManager.get(SettingKeys.MFNR).set(bool);
        if (bool) {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY, true);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.MFNR, (byte) 1, true);
        }
        else {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).setStringValue(cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).getStringValue(),true);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.MFNR, (byte) 0, true);
        }
    }
}