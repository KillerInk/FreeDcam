package freed.cam.apis.camera2.parameters.modes;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.text.TextUtils;

import com.QTI.SOC;

import java.util.HashMap;

import androidx.annotation.RequiresApi;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class MFNR extends BaseModeApi2 {
    public MFNR(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.MFNR);
        parameterValues = new HashMap<>();
        for (int i = 10; i <= 100; i += 10) {
            parameterValues.put(i + "", i);
        }
        setViewState(ViewState.Visible);
    }


    @Override
    public String GetStringValue() {
        if (TextUtils.isEmpty(SettingsManager.get(SettingKeys.MFNR).get()))
            return "1";
        else
            return SettingsManager.get(SettingKeys.MFNR).get();
    }

    @Override
    public String[] getStringValues() {
        return parameterValues.keySet().toArray(new String[parameterValues.size()]);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setValue(String valueToSet, boolean setToCamera) {
        SettingsManager.get(SettingKeys.MFNR).set(valueToSet);
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY, setToCamera);
        ((Camera2Fragment) cameraUiWrapper).captureSessionHandler.SetParameterRepeating(SOC.MFNR, (byte)1, setToCamera);

        fireStringValueChanged(valueToSet);
    }
}