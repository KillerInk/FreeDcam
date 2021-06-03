package freed.cam.apis.featuredetector;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.update.ReleaseChecker;
import freed.utils.Log;

public class CameraFeatureDetector {
    private final String TAG = CameraFeatureDetector.class.getSimpleName();
    private SettingsManager settingsManager;

    public CameraFeatureDetector()
    {
        settingsManager = FreedApplication.settingsManager();
    }

    public void detectFeatures()
    {
        Log.d(TAG, "CameraFeatureRunner process");
        settingsManager.setCamApi(SettingsManager.API_SONY);
        Camera2FeatureDetectorTask task  = null;
        Camera1FeatureDetectorTask task1 = null;

        if (Build.VERSION.SDK_INT >= 21) {
            task =  new Camera2FeatureDetectorTask();
            task.detect();
        }
        task1 = new Camera1FeatureDetectorTask();
        task1.detect();
        if (settingsManager.hasCamera2Features()) {
            if (task.hwlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                settingsManager.setCamApi(SettingsManager.API_1);
            else
                settingsManager.setCamApi(SettingsManager.API_2);
        }
        setGlobalDefaultSettings();
        settingsManager.setAppVersion(BuildConfig.VERSION_CODE);
        settingsManager.setAreFeaturesDetected(true);
        settingsManager.save();
        Log.d(TAG, "Feature Detection done! Start FreeDcam Api: " + settingsManager.getCamApi() + " app version:" + settingsManager.getAppVersion());
        FreedApplication.cameraFragmentManager().switchCameraFragment();
    }

    private void setGlobalDefaultSettings()
    {
        if (RenderScriptManager.isSupported()) {
            settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setValues(new String[]{PreviewPostProcessingModes.off.name(),PreviewPostProcessingModes.RenderScript.name(),PreviewPostProcessingModes.OpenGL.name()});
            settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).set(PreviewPostProcessingModes.off.name());
            settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setIsSupported(true);
            settingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.focuspeakColors));
            settingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).set(settingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).getValues()[0]);
            settingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).setIsSupported(true);
        }
        else
            settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setIsSupported(false);

        settingsManager.getGlobal(SettingKeys.GuideList).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.guidelist));
        settingsManager.getGlobal(SettingKeys.GuideList).set(settingsManager.getGlobal(SettingKeys.GuideList).getValues()[0]);
        if (ReleaseChecker.isGithubRelease)
            settingsManager.getGlobal(SettingKeys.CHECKFORUPDATES).set(true);

        settingsManager.getGlobal(SettingKeys.SHOWMANUALSETTINGS).set(true);

        settingsManager.getGlobal(SettingKeys.LOCATION_MODE).setIsSupported(true);
    }
}
