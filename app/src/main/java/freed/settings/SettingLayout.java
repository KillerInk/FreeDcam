package freed.settings;

import java.util.HashMap;

import freed.settings.mode.SettingInterface;

public class SettingLayout {
    public String active_api;
    public HashMap<String, CameraId> api_hashmap;

    public class CameraId
    {
        public int active_camera;
        public int[] camera_ids;
        public HashMap<Integer, CameraSettings> cameraid_settings;

        public class CameraSettings
        {
            public HashMap<SettingKeys.Key, SettingInterface> cameraid_settings;
        }
    }
}
