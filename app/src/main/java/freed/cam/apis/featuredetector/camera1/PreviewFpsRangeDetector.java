package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class PreviewFpsRangeDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectPreviewFpsRanges(cameraCharacteristics);
    }

    private void detectPreviewFpsRanges(Camera.Parameters parameters) {
        if (parameters.get(camstring(R.string.preview_fps_range_values))!= null)
        {
            try {
                SettingsManager.get(SettingKeys.PreviewFpsRange).setIsSupported(true);
                SettingsManager.get(SettingKeys.PreviewFpsRange).setValues(parameters.get(camstring(R.string.preview_fps_range_values)).split(","));
                SettingsManager.get(SettingKeys.PreviewFpsRange).setCamera1ParameterKEY(camstring(R.string.preview_fps_range));
                SettingsManager.get(SettingKeys.PreviewFpsRange).set(parameters.get(camstring(R.string.preview_fps_range)));
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.PreviewFpsRange).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.PreviewFpsRange).setIsSupported(false);
            }
        }
    }
}
