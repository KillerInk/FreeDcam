package freed.cam.apis.featuredetector.camera2.xiaomi;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashSet;

import camera2_hidden_keys.xiaomi.CaptureRequestXiaomi;
import freed.cam.apis.featuredetector.Camera2FeatureDetectorTask;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.VendorKeyDetector;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class ProVideoLogDetector extends BaseParameter2Detector implements VendorKeyDetector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void checkIfVendorKeyIsSupported(HashSet<String> keys) {
        try {
            if (Camera2FeatureDetectorTask.isKeySupported(keys, CaptureRequestXiaomi.PRO_VIDEO_LOG_ENABLED))
                SettingsManager.get(SettingKeys.XIAOMI_PRO_VIDEO_LOG).setIsSupported(true);
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            SettingsManager.get(SettingKeys.XIAOMI_PRO_VIDEO_LOG).setIsSupported(false);
        }
    }
}
