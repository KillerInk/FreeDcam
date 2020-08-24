package freed.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.GlobalBooleanSettingMode;
import freed.settings.mode.SettingInterface;
import freed.settings.mode.SettingMode;
import freed.settings.mode.TypedSettingMode;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.XmlUtil;

public class SettingsLoader {

    private final String TAG = SettingsLoader.class.getSimpleName();

    public void loadSettings(SettingLayout settingLayout,File appdata)
    {
        File configFile = new File(appdata.getAbsolutePath()+"/freed_config.xml");
        if (configFile.exists())
        {
            try {
                String xmlsource = StringUtils.getString(new FileInputStream(configFile));
                XmlElement xmlElement = XmlElement.parse(xmlsource);
                settingLayout.active_api = xmlElement.findChild(XmlUtil.TAG_ACTIVE_API).getValue();
                settingLayout.device = xmlElement.findChild(XmlUtil.DEVICE).getValue();
                settingLayout.app_version = xmlElement.findChild(XmlUtil.APP_VERSION).getIntValue(0);
                settingLayout.hasCamera2Features = xmlElement.findChild(XmlUtil.HAS_CAMERA2_FEATURES).getBooleanValue();
                settingLayout.areFeaturesDetected = xmlElement.findChild(XmlUtil.ARE_FEATURES_DETECTED).getBooleanValue();
                settingLayout.writeToExternalSD = xmlElement.findChild(XmlUtil.WRITE_TO_EXTERNALSD).getBooleanValue();
                settingLayout.showHelpOverlayOnStart = xmlElement.findChild(XmlUtil.SHOW_HELPOVERLAY_ONSTART).getBooleanValue();
                settingLayout.isZteAE = xmlElement.findChild(XmlUtil.IS_ZTE_AE).getBooleanValue();
                settingLayout.extSdFolderUri = xmlElement.findChild(XmlUtil.EXT_SD_FOLDER_URI).getValue();
                if (settingLayout.extSdFolderUri.equals("null"))
                    settingLayout.extSdFolderUri = null;
                try {
                    settingLayout.framework = Frameworks.valueOf(xmlElement.findChild(XmlUtil.FRAMEWORK).getValue());
                }
                catch (ClassCastException ex)
                {
                    Log.d(TAG, "failed to parse Framework, use Default");
                    settingLayout.framework = Frameworks.Default;
                }
                catch (IllegalArgumentException ex)
                {
                    Log.d(TAG, "failed to parse Framework, use Default");
                    settingLayout.framework = Frameworks.Default;
                }
                XmlElement globalsettings = xmlElement.findChild(XmlUtil.GLOBAL_SETTINGS);
                List<XmlElement> globalSettings = globalsettings.findChildren(XmlUtil.SETTING);
                for (XmlElement profile : globalSettings)
                {
                    addSettingElement(settingLayout.global_settings, profile);
                }

                List<XmlElement> apilist = xmlElement.findChildren(XmlUtil.TAG_API);
                for (XmlElement element: apilist)
                {
                    String api_name = element.getAttribute("name","camera1");
                    SettingLayout.CameraId camera;
                    if ((camera = settingLayout.api_hashmap.get(api_name)) == null)
                        camera = new SettingLayout.CameraId();
                    parseCameraNode(camera, element);
                    settingLayout.api_hashmap.put(api_name, camera);
                }
            }
            catch (IOException ex)
            {
                Log.WriteEx(ex);
            }
        }
    }

    private void parseCameraNode(SettingLayout.CameraId camera, XmlElement element) {
        parseActiveCameraAndAvailibleCameras(camera, element);
        parseCameraSettings(camera, element);
    }

    private void parseActiveCameraAndAvailibleCameras(SettingLayout.CameraId camera, XmlElement element) {
        camera.active_camera = element.findChild(XmlUtil.ACTIVE_CAMERA).getIntValue(0);
        camera.overrideDngProfile = element.findChild(XmlUtil.OVERRIDE_DNGPROFILE).getBooleanValue();
        camera.maxCameraExposureTime = element.findChild(XmlUtil.MAX_CAMERA_EXPOSURETIME).getLongValue();
        camera.minCameraExposureTime = element.findChild(XmlUtil.MIN_CAMERA_EXPOSURETIME).getLongValue();
        camera.maxCameraIso = element.findChild(XmlUtil.MAX_CAMERA_ISO).getIntValue(0);
        camera.minCameraFocus = element.findChild(XmlUtil.MIN_CAMERA_FOCUS).getFloatValue();

        XmlElement apisettings = element.findChild(XmlUtil.API_SETTINGS);
        List<XmlElement> apiSettings = apisettings.findChildren(XmlUtil.SETTING);
        for (XmlElement profile : apiSettings)
        {
            addSettingElement(camera.api_settings, profile);
        }

        XmlElement cameraids = element.findChild(XmlUtil.CAMERA_IDS);
        List<XmlElement> ids = cameraids.findChildren(XmlUtil.IDS);
        int camids[] = new int[ids.size()];
        for (int i= 0; i<ids.size();i++)
            camids[i] = ids.get(i).getIntValue(0);
        camera.camera_ids =camids;
    }

    private void parseCameraSettings(SettingLayout.CameraId camera, XmlElement element) {
        XmlElement camsettings = element.findChild(XmlUtil.CAMERA_SETTINGS);

        List<XmlElement> cameraSettings = camsettings.findChildren(XmlUtil.ID);
        for (int i = 0; i< cameraSettings.size(); i++)
        {
            int id = cameraSettings.get(i).getIntAttribute("name",0);
            SettingLayout.CameraId.CameraSettings settings;
            if ((settings = camera.cameraid_settings.get(id)) == null)
                settings = new SettingLayout.CameraId.CameraSettings();
            settings.isFrontCamera = cameraSettings.get(i).findChild(XmlUtil.FRONT_CAMERA).getBooleanValue();
            parseSettings(cameraSettings.get(i), settings);
            camera.cameraid_settings.put(id,settings);
        }
    }

    private void parseSettings(XmlElement xmlElement, SettingLayout.CameraId.CameraSettings settings) {
        List<XmlElement> settingsxml = xmlElement.findChildren(XmlUtil.SETTING);
        for (XmlElement profile : settingsxml)
        {
            addSettingElement(settings.cameraid_settings, profile);
        }
    }

    private void addSettingElement(HashMap<SettingKeys.Key, SettingInterface> hashMap, XmlElement profile) {
        String type  = profile.getAttribute("type","AbstractSettingMode");
        String key = profile.getAttribute("name","manualmf");
        SettingKeys.Key foundKey = findKey(key);
        if (type.equals(ApiBooleanSettingMode.class.getSimpleName()))
        {
            ApiBooleanSettingMode apiBooleanSettingMode = new ApiBooleanSettingMode(foundKey);
            apiBooleanSettingMode.loadXmlNode(profile);
            hashMap.put(foundKey,apiBooleanSettingMode);
        }
        else if (type.equals(GlobalBooleanSettingMode.class.getSimpleName()))
        {
            GlobalBooleanSettingMode apiBooleanSettingMode = new GlobalBooleanSettingMode(foundKey);
            apiBooleanSettingMode.loadXmlNode(profile);
            hashMap.put(foundKey,apiBooleanSettingMode);
        }
        else if (type.equals(TypedSettingMode.class.getSimpleName()))
        {

            TypedSettingMode apiBooleanSettingMode = new TypedSettingMode(foundKey);
            apiBooleanSettingMode.loadXmlNode(profile);
            hashMap.put(foundKey,apiBooleanSettingMode);
        }
        else if (type.equals(SettingMode.class.getSimpleName()))
        {
            SettingMode apiBooleanSettingMode = new SettingMode(foundKey);
            apiBooleanSettingMode.loadXmlNode(profile);
            hashMap.put(foundKey,apiBooleanSettingMode);
        }
    }

    private SettingKeys.Key findKey(String val)
    {
        SettingKeys.Key[] key = SettingKeys.getKeyList();
        for (SettingKeys.Key k : key)
            if (FreedApplication.getStringFromRessources(k.getRessourcesStringID()).equals(val))
                return k;
        return null;
    }
}
