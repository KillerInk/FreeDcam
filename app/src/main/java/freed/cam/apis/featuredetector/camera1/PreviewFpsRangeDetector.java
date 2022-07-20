package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
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
                settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).setIsSupported(true);
                settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).setValues(parameters.get(camstring(R.string.preview_fps_range_values)).split(","));
                settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).setCamera1ParameterKEY(camstring(R.string.preview_fps_range));
                settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).set(parameters.get(camstring(R.string.preview_fps_range)));
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).setIsSupported(false);
            }
        }
    }
}
