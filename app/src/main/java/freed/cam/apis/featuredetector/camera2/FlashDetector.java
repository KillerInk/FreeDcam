package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.HashMap;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.utils.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FlashDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        if(!settingsManager.getIsFrontCamera())
            detectFlash(cameraCharacteristics);
    }


    private void detectFlash(CameraCharacteristics characteristics) {
        if (settingsManager.hasCamera2Features()) {
            //flash mode
            boolean flashavail = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            settingsManager.get(SettingKeys.FlashMode).setIsSupported(flashavail);
            if (flashavail) {
                String[] lookupar = FreedApplication.getContext().getResources().getStringArray(R.array.flashextModes);
                settingsManager.get(SettingKeys.FlashMode).setValues(lookupar);
                settingsManager.get(SettingKeys.FlashMode).set(lookupar[0]);
            }
        }
    }
}
