package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.devices.pocof2.CaptureRequestDump;
import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;
import freed.settings.mode.BooleanSettingModeInterface;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class XiaomiMfnr extends BaseModeApi2 implements BooleanSettingModeInterface {
    public XiaomiMfnr(Camera2 cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.XIAOMI_MFNR);
        if (settingsManager.get(SettingKeys.XIAOMI_MFNR).isSupported())
            setViewState(ViewState.Visible);
    }

    @Override
    public boolean get() {
        return settingsManager.get(SettingKeys.XIAOMI_MFNR).get();
    }


    @Override
    public void set(boolean bool) {
        settingsManager.get(SettingKeys.XIAOMI_MFNR).set(bool);
        if (bool) {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY, true);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestDump.xiaomi_mfnr_enabled, (byte) 1, true);
        }
        else
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).setStringValue(cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).getStringValue(),true);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestDump.xiaomi_mfnr_enabled, (byte) 0, true);
        }
    }
}
