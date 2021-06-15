package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.settings.SettingKeys;

public class ShadingModesDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Camera2Util.detectIntMode(cameraCharacteristics, CameraCharacteristics.SHADING_AVAILABLE_MODES, settingsManager.get(SettingKeys.LensShade), FreedApplication.getStringArrayFromRessource(R.array.shadingmodes),settingsManager);
        }
    }
}
