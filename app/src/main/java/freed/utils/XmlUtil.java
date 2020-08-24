package freed.utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class XmlUtil {

    public static final String TAG_ACTIVE_API = "active_api";
    public static final String TAG_APIS = "apis";
    public static final String TAG_API = "api";
    public static final String LINE_END = "\r\n";
    public static final String ACTIVE_CAMERA = "active_camera";
    public static final String CAMERA_IDS = "camera_ids";
    public static final String IDS = "ids";
    public static final String CAMERA_SETTINGS = "camera_settings";
    public static final String ID = "id";
    public static final String SETTING = "setting";
    public static final String DEVICE = "device";
    public static final String FRAMEWORK = "framework";
    public static final String APP_VERSION = "app_version";
    public static final String FRONT_CAMERA = "front_camera";
    public static final String HAS_CAMERA2_FEATURES = "has_camera2_features";
    public static final String OVERRIDE_DNGPROFILE = "override_dngprofile";
    public static final String MAX_CAMERA_EXPOSURETIME = "max_camera_exposure_time";
    public static final String MIN_CAMERA_EXPOSURETIME = "min_camera_exposure_time";
    public static final String MAX_CAMERA_ISO = "max_camera_iso";
    public static final String MIN_CAMERA_FOCUS = "min_camera_focus";
    public static final String WRITE_TO_EXTERNALSD = "write_to_external_sd";
    public static final String SHOW_HELPOVERLAY_ONSTART = "show_helpoverlay_onstart";
    public static final String EXT_SD_FOLDER_URI = "ext_sd_folder_uri";
    public static final String IS_ZTE_AE = "is_zte_ae";
    public static final String ARE_FEATURES_DETECTED = "are_features_detected";
    public static final String GLOBAL_SETTINGS = "global_settings";
    public static final String API_SETTINGS = "api_settings";

    public static void writeNodeWithName(BufferedWriter writer, String tag, String name) throws IOException {
        writeLine(writer,"<"+tag+ " name = \""+ name +"\">");
    }

    public static void writeTagEnd(BufferedWriter writer,String tag) throws IOException {
        writeLine(writer,"</"+tag+">");
    }

    public static void writeTagStart(BufferedWriter writer,String tag) throws IOException {
        writeLine(writer,"<"+tag+">");
    }

    public static void writeLine(BufferedWriter writer, String s) throws IOException {
        writer.write(s + LINE_END);
    }

    public static String getTagStringWithValue(String tag, String value)
    {
        return  "<" + tag +">" + value + "</" + tag +">";
    }

    public static void writeTagWithValue(BufferedWriter writer, String tag, String val) throws IOException {
        writeLine(writer, getTagStringWithValue(tag, val));
    }
}
