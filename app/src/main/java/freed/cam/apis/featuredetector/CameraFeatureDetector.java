package freed.cam.apis.featuredetector;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.events.EventBusHelper;
import freed.cam.events.SwichCameraFragmentEvent;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class CameraFeatureDetector {
    private final String TAG = CameraFeatureDetector.class.getSimpleName();

    public void detectFeatures()
    {
        Log.d(TAG, "CameraFeatureRunner process");
        SettingsManager.getInstance().setCamApi(SettingsManager.API_SONY);
        Camera2FeatureDetectorTask task  = null;
        Camera1FeatureDetectorTask task1 = null;

        if (Build.VERSION.SDK_INT >= 21) {
            task =  new Camera2FeatureDetectorTask();
            task.detect();
        }
        task1 = new Camera1FeatureDetectorTask();
        task1.detect();
        if (SettingsManager.getInstance().hasCamera2Features()) {
            if (task.hwlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                SettingsManager.getInstance().setCamApi(SettingsManager.API_1);
            else
                SettingsManager.getInstance().setCamApi(SettingsManager.API_2);
        }
        setGlobalDefaultSettings();
        SettingsManager.getInstance().setAppVersion(BuildConfig.VERSION_CODE);
        SettingsManager.getInstance().setAreFeaturesDetected(true);
        SettingsManager.getInstance().save();
        Log.d(TAG, "Feature Detection done! Start FreeDcam Api: " + SettingsManager.getInstance().getCamApi() + " app version:" + SettingsManager.getInstance().getAppVersion());
        EventBusHelper.post(new SwichCameraFragmentEvent());
    }

    private void setGlobalDefaultSettings()
    {
        if (RenderScriptManager.isSupported()) {
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setValues(new String[]{PreviewPostProcessingModes.off.name(),PreviewPostProcessingModes.RenderScript.name(),PreviewPostProcessingModes.OpenGL.name()});
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).set(PreviewPostProcessingModes.off.name());
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setIsSupported(true);
            SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.focuspeakColors));
            SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).set(SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).getValues()[0]);
            SettingsManager.getGlobal(SettingKeys.FOCUSPEAK_COLOR).setIsSupported(true);
        }
        else
            SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).setIsSupported(false);

        SettingsManager.getGlobal(SettingKeys.GuideList).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.guidelist));
        SettingsManager.getGlobal(SettingKeys.GuideList).set(SettingsManager.getGlobal(SettingKeys.GuideList).getValues()[0]);

        SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).setIsSupported(true);

        SettingsManager.getGlobal(SettingKeys.USE_EXTERNAL_FLASH).set(false);

        String[] mslist = new String[250];
        for (int i =0; i < 250; i++)
            mslist[i] = String.valueOf(i);
        SettingsManager.getGlobal(SettingKeys.FLASH_SIGNAL_TRIGGER_DELAY).setValues(mslist);
        SettingsManager.getGlobal(SettingKeys.FLASH_SIGNAL_TRIGGER_DELAY).set(mslist[0]);
        SettingsManager.getGlobal(SettingKeys.FLASH_SIGNAL_TRIGGER_DELAY).setIsSupported(true);


    }
}
