package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera1FeatureDetectorTask;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class ManualIsoDetector extends BaseParameter1Detector{

    private final String TAG = ManualIsoDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualIso(cameraCharacteristics);
    }

    private void detectManualIso(Camera.Parameters parameters) {

        Log.d(TAG, "Manual Iso Presetted:" + settingsManager.get(SettingKeys.M_ManualIso).isPresetted());
        if (!settingsManager.get(SettingKeys.M_ManualIso).isPresetted()) {

            if (settingsManager.getFrameWork() == Frameworks.MTK) {
                settingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);
                settingsManager.get(SettingKeys.M_ManualIso).setCamera1ParameterKEY("m-sr-g");
                settingsManager.get(SettingKeys.M_ManualIso).setValues(Camera1FeatureDetectorTask.createIsoValues(100, 1600, 100,false));
                settingsManager.get(SettingKeys.M_ManualIso).setType(settingsManager.ISOMANUAL_MTK);
            }
            else {
                try {
                    if (parameters.get(FreedApplication.getStringFromRessources(R.string.min_iso)) != null && parameters.get(FreedApplication.getStringFromRessources(R.string.max_iso)) != null) {
                        settingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);

                        int min = Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.min_iso)));
                        int max = Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.max_iso)));
                        if (settingsManager.getFrameWork() == Frameworks.Xiaomi) {
                            settingsManager.get(SettingKeys.M_ManualIso).setIsSupported(false);
                        } else {
                            settingsManager.get(SettingKeys.M_ManualIso).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.continuous_iso));
                            settingsManager.get(SettingKeys.M_ManualIso).setType(settingsManager.ISOMANUAL_QCOM);
                            settingsManager.get(SettingKeys.M_ManualIso).setValues(Camera1FeatureDetectorTask.createIsoValues(min, max, 50, false));
                        }
                    } else if (parameters.get(FreedApplication.getStringFromRessources(R.string.hw_sensor_iso_range)) != null) {
                        settingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);
                        String t[] = parameters.get(FreedApplication.getStringFromRessources(R.string.hw_sensor_iso_range)).split(",");
                        int min = Integer.parseInt(t[0]);
                        int max = Integer.parseInt(t[1]);
                        settingsManager.get(SettingKeys.M_ManualIso).setValues(Camera1FeatureDetectorTask.createIsoValues(min, max, 50, false));
                        settingsManager.get(SettingKeys.M_ManualIso).setType(settingsManager.ISOMANUAL_KRILLIN);
                        settingsManager.get(SettingKeys.M_ManualIso).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.hw_sensor_iso));
                    } else if (parameters.get(FreedApplication.getStringFromRessources(R.string.lg_iso)) != null) {
                        settingsManager.get(SettingKeys.M_ManualIso).setIsSupported(true);
                        settingsManager.get(SettingKeys.M_ManualIso).setValues(Camera1FeatureDetectorTask.createIsoValues(0, 2700, 50, false));
                        settingsManager.get(SettingKeys.M_ManualIso).setType(settingsManager.ISOMANUAL_LG);
                        settingsManager.get(SettingKeys.M_ManualIso).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.lg_iso));
                    }
                }catch (NumberFormatException ex)
                {
                    Log.WriteEx(ex);
                    settingsManager.get(SettingKeys.M_ManualIso).setIsSupported(false);
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    Log.WriteEx(ex);
                    settingsManager.get(SettingKeys.M_ManualIso).setIsSupported(false);
                }
            }
        }
    }
}
