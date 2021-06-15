package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;

public class MFNR extends BaseModeApi2 {
    public MFNR(Camera2 cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.MFNR);
        if (settingsManager.get(SettingKeys.MFNR).isSupported())
            setViewState(ViewState.Visible);
    }


    @Override
    public String getStringValue() {
        return String.valueOf(settingsManager.get(SettingKeys.MFNR).get());
    }

    @Override
    public String[] getStringValues() {
        return new String[]{"false","true"};
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        settingsManager.get(SettingKeys.MFNR).set(Boolean.parseBoolean(valueToSet));

        if (settingsManager.get(SettingKeys.MFNR).get()) {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY, setToCamera);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.MFNR, (byte) 1, setToCamera);
        }
        else {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).setStringValue(cameraUiWrapper.getParameterHandler().get(SettingKeys.Denoise).getStringValue(),true);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.MFNR, (byte) 0, setToCamera);
        }

        fireStringValueChanged(valueToSet);
    }
}