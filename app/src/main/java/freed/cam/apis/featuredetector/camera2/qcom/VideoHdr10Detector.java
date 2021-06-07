package freed.cam.apis.featuredetector.camera2.qcom;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashSet;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.featuredetector.Camera2FeatureDetectorTask;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.VendorKeyDetector;
import freed.settings.SettingKeys;

public class VideoHdr10Detector extends BaseParameter2Detector implements VendorKeyDetector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void checkIfVendorKeyIsSupported(HashSet<String> keys) {
        try {
            if (Camera2FeatureDetectorTask.isKeySupported(keys, CaptureRequestQcom.HDR10_VIDEO))
                settingsManager.get(SettingKeys.QCOM_VIDEO_HDR10).setIsSupported(true);
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            settingsManager.get(SettingKeys.QCOM_VIDEO_HDR10).setIsSupported(false);
        }
    }
}
