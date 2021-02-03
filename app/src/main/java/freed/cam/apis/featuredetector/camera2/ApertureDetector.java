package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ApertureDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectManualAperture(cameraCharacteristics);
    }


    private void detectManualAperture(CameraCharacteristics characteristics) {
        float[] apetures = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
        if (apetures.length > 1)
        {
            String[] ar = new String[apetures.length];
            for (int i = 0; i < apetures.length;i++)
            {
                ar[i] = String.valueOf(apetures[i]);
            }
            SettingsManager.get(SettingKeys.M_Aperture).setValues(ar);
            SettingsManager.get(SettingKeys.M_Aperture).setIsSupported(true);
            SettingsManager.get(SettingKeys.M_Aperture).set(String.valueOf(0));

        }
    }
}
