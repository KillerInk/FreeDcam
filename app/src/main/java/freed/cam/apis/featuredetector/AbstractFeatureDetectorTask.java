package freed.cam.apis.featuredetector;

import com.troop.freedcam.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 23.01.2017.
 */

abstract class AbstractFeatureDetectorTask implements FeatureDetectorTask {
    private List<Class> parametersToDetect;
    protected SettingsManager settingsManager;
    AbstractFeatureDetectorTask()
    {
        parametersToDetect = createParametersToCheckList();
        settingsManager = FreedApplication.settingsManager();
    }

    @Override
    public void detect()
    {
        preDetect();
        List<String> cameraIDs = findCameraIDs();
        int arr[] = new int[cameraIDs.size()];
        for (int i = 0; i<arr.length;i++)
            arr[i] = Integer.parseInt(cameraIDs.get(i));
        settingsManager.setCameraIds(arr);
        settingsManager.SetCurrentCamera(0);
        for (int i = 0; i < cameraIDs.size();i++)
            checkCameraID(i,cameraIDs,parametersToDetect);
        postDetect();
    }

    @Override
    public void checkCameraID(int id, List<String> cameraids, List<Class> parametersToDetect)
    {
        settingsManager.SetCurrentCamera(id);

        settingsManager.get(SettingKeys.orientationHack).setValues(new String[]{"0","90","180","270"});
        settingsManager.get(SettingKeys.orientationHack).set("0");
        settingsManager.get(SettingKeys.orientationHack).setIsSupported(true);

        settingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).set(false);
        settingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).setIsSupported(true);


        settingsManager.getApi(SettingKeys.Module).set(FreedApplication.getStringFromRessources(R.string.module_picture));


        settingsManager.get(SettingKeys.selfTimer).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.selftimervalues));
        settingsManager.get(SettingKeys.selfTimer).set(settingsManager.get(SettingKeys.selfTimer).getValues()[0]);

        settingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).set(FreedApplication.getStringFromRessources(R.string.video_audio_source_default));
        settingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.video_audio_source));
        settingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).setIsSupported(true);


    }

    protected  <T> T getInstance(Class classtype) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor =  classtype.getConstructor();
        return (T) constructor.newInstance();
    }
}
