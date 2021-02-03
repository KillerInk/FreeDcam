package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class VideoStabDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        try {
            if (cameraCharacteristics.get(camstring(R.string.video_stabilization_supported)).equals(camstring(R.string.true_)))
            {
                SettingsManager.get(SettingKeys.VideoStabilization).setIsSupported(true);
                SettingsManager.get(SettingKeys.VideoStabilization).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.video_stabilization));
                SettingsManager.get(SettingKeys.VideoStabilization).setValues(new String[]{FreedApplication.getStringFromRessources(R.string.true_), FreedApplication.getStringFromRessources(R.string.false_)});
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            SettingsManager.get(SettingKeys.VideoStabilization).setIsSupported(false);
        }
        catch (NumberFormatException ex)
        {
            Log.WriteEx(ex);
            SettingsManager.get(SettingKeys.VideoStabilization).setIsSupported(false);
        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
            Log.WriteEx(ex);
            SettingsManager.get(SettingKeys.VideoStabilization).setIsSupported(false);
        }
    }


}
