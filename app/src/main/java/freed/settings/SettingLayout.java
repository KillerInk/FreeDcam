package freed.settings;

import java.util.HashMap;

import freed.settings.mode.XmlSettingInterface;

public class SettingLayout {
    public String device;
    public Frameworks framework = Frameworks.Default;
    public int app_version;
    public String active_api;
    public boolean hasCamera2Features;
    public HashMap<String, CameraId> api_hashmap;
    public boolean writeToExternalSD;
    public boolean showHelpOverlayOnStart;
    public boolean isZteAE;
    public String extSdFolderUri;
    public boolean areFeaturesDetected;

    public HashMap<SettingKeys.Key, XmlSettingInterface> global_settings = new HashMap<>();

    public static class CameraId
    {
        public int active_camera;
        public int[] camera_ids;
        public HashMap<Integer, CameraSettings> cameraid_settings = new HashMap<>();
        public HashMap<SettingKeys.Key, XmlSettingInterface> api_settings = new HashMap<>();
        public boolean overrideDngProfile;
        public long maxCameraExposureTime;
        public long minCameraExposureTime;
        public int maxCameraIso;
        public float minCameraFocus;

        public static class CameraSettings
        {
            public boolean isFrontCamera;
            public HashMap<SettingKeys.Key, XmlSettingInterface> cameraid_settings = new HashMap<>();
        }
    }

    public SettingLayout()
    {
        api_hashmap = new HashMap();
    }

}
