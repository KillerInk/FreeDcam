package freed.cam.apis.featuredetector.camera2.qcom;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashSet;

import camera2_hidden_keys.qcom.CameraCharacteristicsQcom;
import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.featuredetector.Camera2FeatureDetectorTask;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.VendorKeyDetector;
import freed.settings.SettingKeys;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MFNRDetector extends BaseParameter2Detector implements VendorKeyDetector {
    private static final String TAG = MFNRDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int mfnr_type = cameraCharacteristics.get(CameraCharacteristicsQcom.MFNRType);
        Log.d(TAG,"mfnr type:" + mfnr_type);
    }


    @Override
    public void checkIfVendorKeyIsSupported(HashSet<String> keys) {
        if (Camera2FeatureDetectorTask.isKeySupported(keys, CaptureRequestQcom.MFNR))
            settingsManager.get(SettingKeys.MFNR).setIsSupported(true);
        else
            settingsManager.get(SettingKeys.MFNR).setIsSupported(false);
        Log.d(TAG, "MFNR supported:" + settingsManager.get(SettingKeys.MFNR).isSupported());
    }
}
