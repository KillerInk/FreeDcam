package freed.cam.apis.featuredetector;

import com.troop.freedcam.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.VendorKeyDetector;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingInterface;
import freed.settings.mode.SettingMode;
import freed.utils.Log;

/**
 * Created by troop on 23.01.2017.
 */

abstract class AbstractFeatureDetectorTask {

    private final  String TAG = AbstractFeatureDetectorTask.class.getSimpleName();
    private List<Class> parametersToDetect;
    AbstractFeatureDetectorTask()
    {
        parametersToDetect = createParametersToCheckList();
    }

    public abstract List<Class> createParametersToCheckList();


    public void detect()
    {
        preDetect();
        List<String> cameraIDs = findCameraIDs();
        for (int i = 0; i < cameraIDs.size();i++)
            checkCameraID(i,cameraIDs,parametersToDetect);
        postDetect();
    }

    public abstract void preDetect();

    public abstract List<String> findCameraIDs();

    public void checkCameraID(int id, List<String> cameraids, List<Class> parametersToDetect)
    {
        SettingsManager.getInstance().SetCurrentCamera(id);

        SettingsManager.get(SettingKeys.orientationHack).setValues(new String[]{"0","90","180","270"});
        SettingsManager.get(SettingKeys.orientationHack).set("0");
        SettingsManager.get(SettingKeys.orientationHack).setIsSupported(true);

        SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).set(false);
        SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).setIsSupported(true);


        SettingsManager.getApi(SettingKeys.Module).set(FreedApplication.getStringFromRessources(R.string.module_picture));


        SettingsManager.get(SettingKeys.selfTimer).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.selftimervalues));
        SettingsManager.get(SettingKeys.selfTimer).set(SettingsManager.get(SettingKeys.selfTimer).getValues()[0]);

        SettingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).set(FreedApplication.getStringFromRessources(R.string.video_audio_source_default));
        SettingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.video_audio_source));
        SettingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).setIsSupported(true);

        if (RenderScriptManager.isSupported()) {
            SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.focuspeakColors));
            SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).set(SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).getValues()[0]);
            SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setIsSupported(true);
        }

        SettingsManager.getGlobal(SettingKeys.GuideList).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.guidelist));
        SettingsManager.getGlobal(SettingKeys.GuideList).set(SettingsManager.getGlobal(SettingKeys.GuideList).getValues()[0]);

        SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).setIsSupported(true);
    }

    public abstract void postDetect();



    protected  <T> T getInstance(Class classtype) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor =  classtype.getConstructor();
        return (T) constructor.newInstance();
    }
}
