package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.settings.SettingKeys;

public class DistortionCorrectionDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Camera2Util.detectIntMode(cameraCharacteristics, CameraCharacteristics.DISTORTION_CORRECTION_AVAILABLE_MODES, settingsManager.get(SettingKeys.DISTORTION_CORRECTION_MODE), FreedApplication.getStringArrayFromRessource(R.array.distortionmodes),settingsManager);
        }
    }
}
