package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.settings.SettingKeys;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DenoisParameterDetector extends BaseParameter2Detector {

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        Camera2Util.detectIntMode(cameraCharacteristics, CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES, settingsManager.get(SettingKeys.Denoise), FreedApplication.getStringArrayFromRessource(R.array.denoiseModes),settingsManager);
        if (settingsManager.get(SettingKeys.Denoise).contains(FreedApplication.getStringFromRessources(R.string.denoise_zsl)))
        {
            String vals[] = settingsManager.get(SettingKeys.Denoise).getValues();
            String newvals[] = new String[vals.length-1];
            String zsldnoise= FreedApplication.getStringFromRessources(R.string.denoise_zsl);
            int t = 0;
            for (int i = 0; i< vals.length; i++)
            {
                if (!vals[i].contains(zsldnoise))
                    newvals[t++] = vals[i];
            }
            settingsManager.get(SettingKeys.Denoise).setValues(newvals);
        }
    }
}
