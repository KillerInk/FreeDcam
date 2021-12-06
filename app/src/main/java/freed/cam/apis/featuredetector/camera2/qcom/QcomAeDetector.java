package freed.cam.apis.featuredetector.camera2.qcom;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashSet;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import camera2_hidden_keys.qcom.CaptureResultQcom;
import freed.cam.apis.featuredetector.Camera2FeatureDetectorTask;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.VendorKeyDetector;
import freed.settings.SettingKeys;

public class QcomAeDetector extends BaseParameter2Detector implements VendorKeyDetector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void checkIfVendorKeyIsSupported(HashSet<String> keys) {
        if (Camera2FeatureDetectorTask.isKeySupported(keys, CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_select_priority))
            settingsManager.get(SettingKeys.USE_QCOM_AE).set(true);
    }
}
