package freed.cam.apis.featuredetector.camera2.xiaomi;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.xiaomi.CameraCharacteristicsXiaomi;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.utils.Log;

public class SuperLowLightRawDetector extends BaseParameter2Detector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int superlowlight = cameraCharacteristics.get(CameraCharacteristicsXiaomi.superlowlightraw);
        Log.d(SuperLowLightRawDetector.class.getSimpleName(), "arc superlowlight raw :" +superlowlight);
    }
}
