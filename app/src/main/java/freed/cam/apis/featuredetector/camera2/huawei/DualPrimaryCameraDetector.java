package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.cam.apis.featuredetector.camera2.BaseParameterDetector;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class DualPrimaryCameraDetector extends BaseParameterDetector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        Camera2Util.detectByteMode(cameraCharacteristics, CameraCharacteristicsHuawei.HUAWEI_AVAILABLE_DUAL_PRIMARY, SettingsManager.get(SettingKeys.dualPrimaryCameraMode), FreedApplication.getStringArrayFromRessource(R.array.dual_camera_mode));

    }
}
