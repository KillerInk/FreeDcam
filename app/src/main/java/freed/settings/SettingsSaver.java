package freed.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import freed.settings.mode.SettingInterface;
import freed.utils.Log;
import freed.utils.XmlUtil;

public class SettingsSaver {

    private final String TAG = SettingsSaver.class.getSimpleName();
    public void saveSettings(SettingLayout settingLayout, File appdata)
    {

        File configFile = new File(appdata.getAbsolutePath() + "/freed_config.xml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));) {
            Log.d(TAG, "Write global settings");

            XmlUtil.writeTagStart(writer, XmlUtil.TAG_APIS);
            XmlUtil.writeTagWithValue(writer, XmlUtil.TAG_ACTIVE_API, settingLayout.active_api);
            XmlUtil.writeTagWithValue(writer, XmlUtil.DEVICE, settingLayout.device);
            XmlUtil.writeTagWithValue(writer, XmlUtil.FRAMEWORK, settingLayout.framework.toString());
            XmlUtil.writeTagWithValue(writer, XmlUtil.APP_VERSION, String.valueOf(settingLayout.app_version));
            XmlUtil.writeTagWithValue(writer, XmlUtil.HAS_CAMERA2_FEATURES, String.valueOf(settingLayout.hasCamera2Features));
            XmlUtil.writeTagWithValue(writer, XmlUtil.ARE_FEATURES_DETECTED, String.valueOf(settingLayout.areFeaturesDetected));
            XmlUtil.writeTagWithValue(writer, XmlUtil.WRITE_TO_EXTERNALSD, String.valueOf(settingLayout.writeToExternalSD));
            XmlUtil.writeTagWithValue(writer, XmlUtil.SHOW_HELPOVERLAY_ONSTART, String.valueOf(settingLayout.showHelpOverlayOnStart));
            XmlUtil.writeTagWithValue(writer, XmlUtil.IS_ZTE_AE, String.valueOf(settingLayout.isZteAE));
            XmlUtil.writeTagWithValue(writer, XmlUtil.EXT_SD_FOLDER_URI, settingLayout.extSdFolderUri);
            XmlUtil.writeTagStart(writer,XmlUtil.GLOBAL_SETTINGS);
            writeCameraIdSettings(writer, settingLayout.global_settings);
            XmlUtil.writeTagEnd(writer,XmlUtil.GLOBAL_SETTINGS);
            Log.d(TAG, "Write api Settings");
            for (String api : settingLayout.api_hashmap.keySet())
            {
                SettingLayout.CameraId camera = settingLayout.api_hashmap.get(api);
                XmlUtil.writeNodeWithName(writer,XmlUtil.TAG_API,api);
                writeApiNode(writer,camera);
                XmlUtil.writeTagEnd(writer,XmlUtil.TAG_API);
            }
            XmlUtil.writeTagEnd(writer,XmlUtil.TAG_APIS);
        } catch (IOException e) {
            Log.WriteEx(e);
        }
    }

    private void writeApiNode(BufferedWriter writer, SettingLayout.CameraId camera) throws IOException {
        Log.d(TAG, "Write api node");
        XmlUtil.writeTagWithValue(writer,XmlUtil.ACTIVE_CAMERA, String.valueOf(camera.active_camera));
        XmlUtil.writeTagWithValue(writer,XmlUtil.OVERRIDE_DNGPROFILE, String.valueOf(camera.overrideDngProfile));
        XmlUtil.writeTagWithValue(writer,XmlUtil.MAX_CAMERA_EXPOSURETIME, String.valueOf(camera.maxCameraExposureTime));
        XmlUtil.writeTagWithValue(writer,XmlUtil.MIN_CAMERA_EXPOSURETIME, String.valueOf(camera.minCameraExposureTime));
        XmlUtil.writeTagWithValue(writer,XmlUtil.MAX_CAMERA_ISO, String.valueOf(camera.maxCameraIso));
        XmlUtil.writeTagWithValue(writer,XmlUtil.MIN_CAMERA_FOCUS, String.valueOf(camera.minCameraFocus));
        XmlUtil.writeTagStart(writer,XmlUtil.API_SETTINGS);
        writeCameraIdSettings(writer, camera.api_settings);
        XmlUtil.writeTagEnd(writer,XmlUtil.API_SETTINGS);
        if (camera.camera_ids != null)
            writeCameraIds(writer,camera.camera_ids);
        if (camera.cameraid_settings != null) {
            writeCameraSettings(writer, camera.cameraid_settings);
        }
    }

    private void writeCameraSettings(BufferedWriter writer, HashMap<Integer, SettingLayout.CameraId.CameraSettings> cameraid_settings) throws IOException {
        Log.d(TAG, "Write camera settings");
        XmlUtil.writeTagStart(writer,XmlUtil.CAMERA_SETTINGS);
        for (int s : cameraid_settings.keySet()) {
            XmlUtil.writeNodeWithName(writer, XmlUtil.ID, String.valueOf(s));
            XmlUtil.writeTagWithValue(writer,XmlUtil.FRONT_CAMERA, String.valueOf(cameraid_settings.get(s).isFrontCamera));
            writeCameraIdSettings(writer, cameraid_settings.get(s).cameraid_settings);
            XmlUtil.writeTagEnd(writer,XmlUtil.ID);
        }
        XmlUtil.writeTagEnd(writer,XmlUtil.CAMERA_SETTINGS);
    }

    private void writeCameraIdSettings(BufferedWriter writer, HashMap<SettingKeys.Key, SettingInterface> stringVideoMediaProfileHashMap) throws IOException {
        Log.d(TAG, "write camera id settings");
        for (SettingKeys.Key s : stringVideoMediaProfileHashMap.keySet())
        {
            writer.write(stringVideoMediaProfileHashMap.get(s).getXmlString());
        }
    }

    private void writeCameraIds(BufferedWriter writer, int[] cams) throws IOException {
        XmlUtil.writeTagStart(writer, XmlUtil.CAMERA_IDS);
        for(int i : cams)
            XmlUtil.writeTagWithValue(writer,XmlUtil.IDS, String.valueOf(i));
        XmlUtil.writeTagEnd(writer,XmlUtil.CAMERA_IDS);
    }

}
