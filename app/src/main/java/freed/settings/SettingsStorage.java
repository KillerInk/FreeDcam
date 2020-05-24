package freed.settings;

import android.app.Application;
import android.util.Xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import freed.FreedApplication;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.GlobalBooleanSettingMode;
import freed.settings.mode.GlobalStringSetting;
import freed.settings.mode.SettingInterface;
import freed.settings.mode.SettingMode;
import freed.settings.mode.TypedSettingMode;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;
import freed.utils.XmlUtil;

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



    private <T extends SettingInterface> T getNewSetting(SettingKeys.Key key)
    {
        Constructor ctr = key.getType().getConstructors()[0];
        T settingInterface = null;
        try {
            settingInterface = (T)ctr.newInstance(SettingsManager.getInstance().getResString(key.getRessourcesStringID()));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return settingInterface;
    }


    public void save()
    {
        new SettingsSaver().saveSettings(settings,appdataFolder);
        mediaProfilesManager.save(appdataFolder);
    }

    public void load()
    {
        new SettingsLoader().loadSettings(settings,appdataFolder);
        mediaProfilesManager.load(appdataFolder);
    }

    public void reset()
    {
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
