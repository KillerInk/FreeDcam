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
    AbstractFeatureDetectorTask()
    {
        parametersToDetect = createParametersToCheckList();
    }

    @Override
    public void detect()
    {
        preDetect();
        List<String> cameraIDs = findCameraIDs();
        int arr[] = new int[cameraIDs.size()];
        for (int i = 0; i<arr.length;i++)
            arr[i] = Integer.parseInt(cameraIDs.get(i));
        SettingsManager.getInstance().setCameraIds(arr);
        SettingsManager.getInstance().SetCurrentCamera(0);
        for (int i = 0; i < cameraIDs.size();i++)
            checkCameraID(i,cameraIDs,parametersToDetect);
        postDetect();
    }

    @Override
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


    }

    protected  <T> T getInstance(Class classtype) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor =  classtype.getConstructor();
        return (T) constructor.newInstance();
    }
}
