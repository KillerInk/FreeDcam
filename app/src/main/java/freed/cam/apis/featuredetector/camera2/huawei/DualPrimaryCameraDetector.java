package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.settings.SettingKeys;

public class DualPrimaryCameraDetector extends BaseParameter2Detector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        Camera2Util.detectByteMode(cameraCharacteristics, CameraCharacteristicsHuawei.HUAWEI_AVAILABLE_DUAL_PRIMARY, settingsManager.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE), FreedApplication.getStringArrayFromRessource(R.array.dual_camera_mode),settingsManager);

    }
}
