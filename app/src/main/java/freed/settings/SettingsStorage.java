package freed.settings;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import freed.settings.mode.SettingInterface;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;

public class SettingsStorage
{
    private static final String TAG = SettingsStorage.class.getSimpleName();
    public final File appdataFolder;
    private MediaProfilesManager mediaProfilesManager;
    // api > camera id > setting
    private SettingLayout settings;

    public SettingsStorage(File appdataFolder)
    {
        this.appdataFolder = appdataFolder;
        mediaProfilesManager = new MediaProfilesManager();
        settings = new SettingLayout();
        settings.api_hashmap.put(SettingsManager.API_1,new SettingLayout.CameraId());
        settings.api_hashmap.put(SettingsManager.API_2,new SettingLayout.CameraId());
        settings.api_hashmap.put(SettingsManager.API_SONY,new SettingLayout.CameraId());
    }

    public SettingInterface get(SettingKeys.Key key) {
        SettingInterface settingInterface = getActiveSettings().get(key);
        if (settingInterface == null) {
            settingInterface = getNewSetting(key);
            getActiveSettings().put(key, settingInterface);
        }
        return settingInterface;
    }

    public SettingInterface getGlobal(SettingKeys.Key key) {
        SettingInterface settingInterface = settings.global_settings.get(key);
        if (settingInterface == null) {
            settingInterface = getNewSetting(key);
            settings.global_settings.put(key, settingInterface);
        }
        return settingInterface;
    }

    public SettingInterface getApiSetting(SettingKeys.Key key) {
        SettingInterface settingInterface = getApiSettings().get(key);
        if (settingInterface == null) {
            settingInterface = getNewSetting(key);
            getApiSettings().put(key, settingInterface);
        }
        return settingInterface;
    }



    private <T extends SettingInterface> T getNewSetting(SettingKeys.Key key)
    {
        Constructor ctr = key.getType().getConstructors()[0];
        T settingInterface = null;
        try {
            settingInterface = (T)ctr.newInstance(key);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return settingInterface;
    }

    private Object waitlock = new Object();

    public synchronized void save()
    {
        new SettingsSaver().saveSettings(settings, appdataFolder);
        mediaProfilesManager.save(appdataFolder);
    }

    public synchronized void load()
    {
        new SettingsLoader().loadSettings(settings, appdataFolder);
        mediaProfilesManager.load(appdataFolder);
    }

    public void reset()
    {
        Log.d(TAG, "reset");
        settings.areFeaturesDetected = false;
       /* if (settingStore != null)
            settingStore.clear();*/
        mediaProfilesManager.reset();
    }

    public void setApiVideoMediaProfiles(HashMap<String,VideoMediaProfile> value)
    {
        mediaProfilesManager.addMediaProfilesToApiAndCamera(settings.active_api,settings.api_hashmap.get(settings.active_api).active_camera,value);
    }

    public HashMap<String,VideoMediaProfile> getApiVideoMediaProfiles()
    {
        return mediaProfilesManager.getMediaProfilesForApiAndCamera(settings.active_api,settings.api_hashmap.get(settings.active_api).active_camera);
    }

    public void setDevice(String device)
    {
        settings.device = device;
    }

    public String getDevice()
    {
        return settings.device;
    }

    public void setFramework(Frameworks framework)
    {
        settings.framework = framework;
    }

    public Frameworks getFramework()
    {
        return settings.framework;
    }

    public void setAppVersion(int appVersion)
    {
        settings.app_version = appVersion;
    }

    public int getAppVersion()
    {
        return settings.app_version;
    }

    public void setApi(String api)
    {
        settings.active_api = api;
    }

    public String getApi()
    {
        return settings.active_api;
    }

    public int getActiveCamera()
    {
        return settings.api_hashmap.get(settings.active_api).active_camera;
    }

    public void setActiveCamera(int id)
    {
        settings.api_hashmap.get(settings.active_api).active_camera = id;
        if (settings.api_hashmap.get(settings.active_api).cameraid_settings == null)
            settings.api_hashmap.get(settings.active_api).cameraid_settings = new HashMap<>();
        if (settings.api_hashmap.get(settings.active_api).cameraid_settings.get(id) == null)
            settings.api_hashmap.get(settings.active_api).cameraid_settings.put(id,new SettingLayout.CameraId.CameraSettings());
    }

    public int[] getActiveCameraIds()
    {
        return settings.api_hashmap.get(settings.active_api).camera_ids;
    }

    public void setActiveCameraIds(int[] ids)
    {
        settings.api_hashmap.get(settings.active_api).camera_ids = ids;
        if (settings.api_hashmap.get(settings.active_api).cameraid_settings == null)
            settings.api_hashmap.get(settings.active_api).cameraid_settings = new HashMap<>();
        for (int i = 0; i< ids.length;i++)
            if (settings.api_hashmap.get(settings.active_api).cameraid_settings.get(i) == null)
                settings.api_hashmap.get(settings.active_api).cameraid_settings.put(i,new SettingLayout.CameraId.CameraSettings());
    }

    public boolean isFrontCamera()
    {
        return settings.api_hashmap.get(settings.active_api).cameraid_settings.get(getActiveCamera()).isFrontCamera;
    }

    public boolean isFrontCamera(int id)
    {
        return settings.api_hashmap.get(settings.active_api).cameraid_settings.get(id).isFrontCamera;
    }

    public void setIsFrontCamera(boolean isFrontCamera)
    {
        SettingLayout.CameraId api = settings.api_hashmap.get(settings.active_api);
        SettingLayout.CameraId.CameraSettings settings = api.cameraid_settings.get(getActiveCamera());
        settings.isFrontCamera = isFrontCamera;
        //settings.api_hashmap.get(settings.active_api).cameraid_settings.get(getActiveCamera()).isFrontCamera = isFrontCamera;
    }

    public boolean hasCamera2Features()
    {
        return settings.hasCamera2Features;
    }

    public void setHasCamera2Features(boolean supported)
    {
        settings.hasCamera2Features = supported;
    }

    public boolean overrideDngProfile()
    {
        return settings.api_hashmap.get(getActiveCamera()).overrideDngProfile;
    }

    public void setOverrideDngProfile(boolean supported)
    {
        settings.api_hashmap.get(getActiveCamera()).overrideDngProfile = supported;
    }

    public HashMap<SettingKeys.Key, SettingInterface> getActiveSettings()
    {
        return settings.api_hashmap.get(settings.active_api).cameraid_settings.get(getActiveCamera()).cameraid_settings;
    }

    public HashMap<SettingKeys.Key, SettingInterface> getApiSettings()
    {
        return settings.api_hashmap.get(settings.active_api).api_settings;
    }

    public long getCameraMaxExposureTime()
    {
        return settings.api_hashmap.get(settings.active_api).maxCameraExposureTime;
    }

    public void setCameraMaxExposureTime(long expotime)
    {
        settings.api_hashmap.get(settings.active_api).maxCameraExposureTime = expotime;
    }

    public int getCameraMaxIso()
    {
        return settings.api_hashmap.get(settings.active_api).maxCameraIso;
    }

    public void setCameraMaxIso(int iso)
    {
        settings.api_hashmap.get(settings.active_api).maxCameraIso = iso;
    }

    public long getCameraMinExposureTime()
    {
        return settings.api_hashmap.get(settings.active_api).minCameraExposureTime;
    }

    public void setCameraMinExposureTime(long expotime)
    {
        settings.api_hashmap.get(settings.active_api).minCameraExposureTime = expotime;
    }

    public float getCameraMinFocus()
    {
        return settings.api_hashmap.get(settings.active_api).minCameraFocus;
    }

    public void setCameraMinFocus(float focus)
    {
        settings.api_hashmap.get(settings.active_api).minCameraFocus = focus;
    }

    public boolean writeToExternalSD()
    {
        return settings.writeToExternalSD;
    }

    public void setWriteToExternalSD(boolean write)
    {
        settings.writeToExternalSD = write;
    }

    public boolean showHelpOverlayOnStart()
    {
        return settings.showHelpOverlayOnStart;
    }

    public void setShowHelpOverlayOnStart(boolean write)
    {
        settings.showHelpOverlayOnStart = write;
    }

    public boolean isZteAE()
    {
        return settings.isZteAE;
    }

    public void setIsZteAE(boolean write)
    {
        settings.isZteAE = write;
    }

    public boolean areFeaturesDetected()
    {
        return settings.areFeaturesDetected;
    }

    public void setFeaturesAreDetected(boolean write)
    {
        settings.areFeaturesDetected = write;
    }

    public String getExtSDFolderUri()
    {
        return settings.extSdFolderUri;
    }

    public void setExtSDFolderUri(String write)
    {
        settings.extSdFolderUri = write;
    }
}
