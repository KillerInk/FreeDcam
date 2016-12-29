/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.camera2.parameters.manual.ManualFocus;
import freed.utils.DeviceUtils.Devices;

import static freed.cam.apis.KEYS.BAYER;
import static freed.cam.apis.KEYS.JPEG_QUALITY;


/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager {
    private final String TAG = AppSettingsManager.class.getSimpleName();

    private int currentcamera;
    private String camApiString = AppSettingsManager.API_1;
    private Devices device;

    public static final int JPEG= 0;
    public static final int RAW = 1;
    public static final int DNG = 2;

    public static final String[] CaptureMode =
            {
                    KEYS.JPEG,
                    BAYER,
                    KEYS.DNG
            };


    public static final String CURRENTCAMERA = "currentcamera";

    //defcomg was here
    //1-29-2016 6:15
    public static final String HDRMODE = "hdrmode";
    // 1-29-2016 11:49
    public static final String VIDEOBITRATE = "videobitrate";
    public static final String HELP = "help";
    //
    public static final String GUIDE = "guide";
    //done
    public static final String CURRENTMODULE = "currentmodule";

    public static final String DENOISETMODE = "denoisetmode";
    public static final String DIGITALIMAGESTABMODE = "digitalimagestabmode";
    public static final String SKINTONEMODE = "skintonemode";
    public static final String NIGHTMODE = "nightmode";
    public static final String NONZSLMANUALMODE = "nonzslmanualmode";

    public static final String EXPOLONGTIME = "expolongtime";
    public static final String HISTOGRAM = "histogram";

    public static final String VIDEOPROFILE = "videoprofile";
    public static final String HIGHFRAMERATEVIDEO = "highframeratevideo";
    public static final String HIGHSPEEDVIDEO = "highspeedvideo";
    public static final String VIDEOSTABILIZATION = "videostabilization";
    ///                  Video Override
    // public static String VIDEOHDR = "videohfr";
    // public static String VIDEOHDR = "videohsr";

    public static final String BAYERFORMAT = "bayerformat";
    public static final String AEPRIORITY = "aepriority";
    public static final String CUSTOMMATRIX = "custommatrix";
    ////////// overide end
    public static final String TIMELAPSEFRAME = "timelapseframe";
    public static final String SETTING_API = "sonyapi";
    public static final String SETTING_DNG = "dng";
    public static final String SETTING_AEBRACKETACTIVE = "aebracketactive";
    public static final String SETTING_OBJECTTRACKING = "objecttracking";
    public static final String SETTING_LOCATION = "location";
    public static final String SETTING_EXTERNALSHUTTER = "externalShutter";
    public static final String SETTING_OrientationHack = "orientationHack";

    public static final String SETTING_INTERVAL = "innterval";
    public static final String SETTING_INTERVAL_DURATION = "interval_duration";
    public static final String SETTING_TIMER = "timer";

    public static final String SETTING_CAMERAMODE = "camMode";
    public static final String SETTING_DUALMODE = "dualMode";
    public static final String SETTING_Theme = "theme";

    public static final String SETTING_SECUREMODE = "securemode";
    public static final String SETTING_TNR = "tnr";
    public static final String SETTING_RDI = "rdi";
    public static final String SETTING_EDGE = "edge";
    public static final String SETTING_COLORCORRECTION = "colorcorrection";
    public static final String SETTING_HOTPIXEL = "hotpixel";
    public static final String SETTING_TONEMAP = "tonemap";
    public static final String SETTING_CONTROLMODE = "controlmode";
    public static final String SETTING_FOCUSPEAK = "focuspeak";

    public static final String SETTING_EXTERNALSD = "extSD";


    public static final String SETTING_Filter = "filter";

    public static final String API_SONY = "playmemories";
    public static final String API_1 = "camera1";
    public static final String API_2 = "camera2";


    public static final String MWB = "mbw";
    public static final String MCONTRAST = "mcontrast";
    public static final String MCONVERGENCE = "mconvergence";
    public static final String MEXPOSURE = "mexposure";

    public static final String MSHARPNESS = "msharpness";
    public static final String MSHUTTERSPEED = "mshutterspeed";
    public static final String MBRIGHTNESS = "mbrightness";
    public static final String MISO = "miso";
    public static final String MSATURATION = "msaturation";
    public static final String MCCT = "mcct";
    public static final String MBURST = "mburst";

    public static final String APPVERSION = "appversion";

    public static final String CAMERA2FULLSUPPORTED = "camera2fullsupport";

    public static final String SETTING_HORIZONT = "horizont";

    public static final String SETTING_CAPTUREBURSTEXPOSURES = "captureburstexposures";
    public static final String SETTING_MORPHOHDR = "morphohdr";
    public static final String SETTING_MORPHOHHT = "morphohht";

    public static final String SETTING_AEB1 = "aeb1";
    public static final String SETTING_AEB2 = "aeb2";
    public static final String SETTING_AEB3 = "aeb3";

    public static final String SETTING_AEB4 = "aeb4";

    public static final String SETTING_AEB5 = "aeb5";
    public static final String SETTING_AEB6 = "aeb6";
    public static final String SETTING_AEB7 = "aeb7";


    public static final String SETTINGS_PREVIEWZOOM = "previewzoom";

    public static final String SETTING_BASE_FOLDER = "base_folder";

    public static final String SETTING_MEDIAPROFILES = "media_profiles";

    public static final String SETTING_AFBRACKETMAX = "afbracketmax";
    public static final String SETTING_AFBRACKETMIN = "afbracketmin";
    public static final String SETTINGS_NIGHTOVERLAY = "nighoverlay";

    public static final String PICTUREFORMAT = "pictureformat";
    public final static String PICTURE_FORMAT_VALUES = "pictureformatvalues";
    public static final String PICTURE_FORMAT_SUPPORTED = "pictureformatsupported";
    public final SettingMode pictureFormat;

    public final static String RAWPICTUREFORMAT = "rawpictureformat";
    public final static String RAW_PICTURE_FORMAT_VALUES = "rawpictureformatvalues";
    public static final String RAW_PICTURE_FORMAT_SUPPORTED = "rawpictureformatsupported";
    public final SettingMode rawPictureFormat;

    public static final String PICTURESIZE = "picturesize";
    public final static String PICTURE_SIZE_VALUES = "picturesizevalues";
    public final static String PICTURE_SIZE_SUPPORTED = "picturesizesupported";
    public final SettingMode pictureSize;

    public static final String FOCUSMODE = "focusmode";
    public final static String FOCUS_VALUES = "focusvalues";
    public final static String FOCUS_SUPPORTED = "focusmodesupported";
    public final SettingMode focusMode;

    public static final String EXPOSUREMODE = "exposuremode";
    public final static String EXPOSURE_VALUES = "exposurevalues";
    public final static String EXPOSURE_SUPPORTED = "exposuremodesupported";
    public final static String EXPOSUREMODE_KEY = "exposuremode_key";
    public final SettingMode exposureMode;

    public static final String WHITEBALANCEMODE = "whitebalancemode";
    public final static String WHITEBALANCEMODEVALUES = "whitebalancemodevalues";
    public final static String WHITEBALANCEMODESUPPORTED = "whitebalancemodesupported";
    public final SettingMode whiteBalanceMode;

    public static final String COLORMODE = "colormode";
    public final static String COLORMODEVALUES = "colormodevalues";
    public final static String COLORMODESUPPORTED = "colormodesupported";
    public final SettingMode colorMode;

    public static final String FLASHMODE = "flashmode";
    public final static String FLASHMODEVALUES = "flashmodevalues";
    public final static String FLASHMODESUPPORTED = "flashmodesupported";
    public final SettingMode flashMode;


    public static final String ISOMODE = "isomode";
    public final static String ISOMODEVALUES = "isomodevalues";
    public final static String ISOMODESUPPORTED = "isomodesupported";
    public final static String ISOMODE_KEY = "isomode_key";
    public final SettingMode isoMode;


    public static final String ANTIBANDINGMODE = "antibandingmode";
    public final static String ANTIBANDINGMODEVALUES = "antibandingmodevalues";
    public final static String ANTIBANDINGMODESUPPORTED = "antibandingmodesupported";
    public final SettingMode antiBandingMode;

    public static final String IMAGEPOSTPROCESSINGMODE = "ippmode";
    public final static String IMAGEPOSTPROCESSINGMODEVALUES = "imagepostprocessingmodevalues";
    public final static String IMAGEPOSTPROCESSINGGMODESUPPORTED = "imagepostprocessingmodesupported";
    public final SettingMode imagePostProcessing;

    public static final String PREVIEWSIZE = "previewsize";
    public final static String PREVIEWSIZEMODEVALUES = "previewsizemodevalues";
    public final static String PREVIEWSIZEMODESUPPORTED = "previewsizemodesupported";
    public final SettingMode previewSize;


    public static final String JPEGQUALITY = "jpegquality";
    public final static String JPEG_QUALITY_VALUES = "jpegqualityvalues";
    public final static String JPEG_QUALITY_SUPPORTED = "jpegqualitysupported";
    public final SettingMode jpegQuality;

    public static final String AEBRACKETHDR = "aebrackethdr";
    public final static String AE_BRACKET_VALUES = "aebracketvalues";
    public final static String AE_BRACKET_SUPPORTED = "aebracketsupported";
    public final SettingMode aeBracket;

    public static final String PREVIEWFPS = "previewfps";
    public final static String PREVIEW_FPS_VALUES = "previewfpsvalues";
    public final static String PREVIEW_FPS_SUPPORTED = "previewfpssupported";
    public final SettingMode previewFps;

    public static final String PREVIEWFORMAT = "previewformat";
    public final static String PREVIEW_FORMAT_VALUES = "previewformatvalues";
    public final static String PREVIEW_FORMAT_SUPPORTED = "previewformatsupported";
    public final SettingMode previewFormat;

    public static final String SCENEMODE = "scenemode";
    public final static String SCENE_VALUES = "scenevalues";
    public final static String SCENE_SUPPORTED = "scenesupported";
    public final SettingMode sceneMode;

    public static final String REDEYEMODE = "redeyemode";
    public final static String REDEYE_VALUES = "redeyevalues";
    public final static String REDEYE_SUPPORTED = "redeyesupported";
    public final SettingMode redEyeMode;

    public static final String LENSHADEMODE = "lenshademode";
    public static final String LENSHADEMODE_VALUES = "lenshademodevalues";
    public static final String LENSHADEMODE_SUPPORTED = "lenshademodesupported";
    public final SettingMode lenshade;

    public static final String ZSLMODE = "zslmode";
    public static final String ZSLMODE_VALUES = "zslmodevalues";
    public static final String ZSLMODE_SUPPORTED = "zslmodesupported";
    public static final String ZSLMODE_KEY = "zslmode";
    public final SettingMode zeroshutterlag;

    public static final String SCENEDETECTMODE = "scenedetectmode";
    public static final String SCENEDETECTMODE_VALUES = "scenedetectmodevalues";
    public static final String SCENEDETECTMODE_SUPPORTED = "scenedetectmodesupported";
    public final SettingMode sceneDetectMode;

    public static final String MEMORYCOLORENHANCEMENTMODE = "memorycolorenhancementmode";
    public static final String MEMORYCOLORENHANCEMENTMODE_VALUES = "memorycolorenhancementmode_values";
    public static final String MEMORYCOLORENHANCEMENTMODE_SUPPORTED = "memorycolorenhancementmode_values";
    public final SettingMode memoryColorEnhancement;

    public static final String VIDEOSIZE = "videosize";
    public static final String VIDEOSIZE_VALUES = "videosizevalues";
    public static final String VIDEOSIZE_SUPPORTED = "videosizesupported";
    public final SettingMode videoSize;

    public static final String CORRELATEDDOUBLESAMPLING = "cds";
    public static final String CORRELATEDDOUBLESAMPLING_VALUES = "cdsvalues";
    public static final String CORRELATEDDOUBLESAMPLING_SUPPORTED = "cdssupported";
    public final SettingMode correlatedDoubleSampling;


    public static final String SETTING_OIS = "ois";
    public static final String SETTING_OIS_VALUES = "oisvalues";
    public static final String SETTING_OIS_SUPPORTED = "oissupported";
    public static final String SETTING_OIS_KEY= "oiskey";
    public final SettingMode opticalImageStabilisation;

    public static final String VIDEOHDR = "videohdr";
    public static final String VIDEOHDR_VALUES = "videohdrvalues";
    public static final String VIDEOHDR_SUPPORTED = "videohdrsupported";
    public static final String VIDEOHDR_KEY= "videohdrkey";
    public final SettingMode videoHDR;

    public static final String VIDEOHFR = "videohfr";
    public static final String VIDEOHFR_VALUES = "videohfrvalues";
    public static final String VIDEOHFR_SUPPORTED = "videohfrsupported";
    public static final String VIDEOHFR_KEY = "videohfrkey";
    public final SettingMode videoHFR;

    public static final String MANUAL_FOCUS = "mf";
    public static final String MANUAL_FOCUS_KEY = "mfkey";
    public static final String MANUAL_FOCUS_SUPPORTED = "mfsupported";
    public static final String MANUAL_FOCUS_VALUES = "mfvalues";
    public static final String MANUAL_FOCUS_TYP = "mftype";
    public final TypeSettingsMode manualFocus;


    private SharedPreferences settings;

    public AppSettingsManager(SharedPreferences sharedPreferences)
    {
        settings = sharedPreferences;

        pictureFormat = new SettingMode(PICTUREFORMAT, PICTURE_FORMAT_VALUES,PICTURE_FORMAT_SUPPORTED);
        rawPictureFormat = new SettingMode(RAWPICTUREFORMAT, RAW_PICTURE_FORMAT_VALUES,RAW_PICTURE_FORMAT_SUPPORTED);
        pictureSize = new SettingMode(PICTURESIZE, PICTURE_SIZE_VALUES, PICTURE_SIZE_SUPPORTED);
        focusMode = new SettingMode(FOCUSMODE,FOCUS_VALUES, FOCUS_SUPPORTED);
        exposureMode = new SettingMode(EXPOSUREMODE,EXPOSURE_VALUES, EXPOSURE_SUPPORTED,EXPOSUREMODE_KEY);
        whiteBalanceMode = new SettingMode(WHITEBALANCEMODE,WHITEBALANCEMODEVALUES,WHITEBALANCEMODESUPPORTED);
        colorMode = new SettingMode(COLORMODE, COLORMODEVALUES,COLORMODESUPPORTED);
        flashMode = new SettingMode(FLASHMODE,FLASHMODEVALUES,FLASHMODESUPPORTED);
        isoMode = new SettingMode(ISOMODE,ISOMODEVALUES,ISOMODESUPPORTED, ISOMODE_KEY);
        antiBandingMode = new SettingMode(ANTIBANDINGMODE,ANTIBANDINGMODEVALUES,ANTIBANDINGMODESUPPORTED);
        imagePostProcessing = new SettingMode(IMAGEPOSTPROCESSINGMODE,IMAGEPOSTPROCESSINGMODEVALUES,IMAGEPOSTPROCESSINGGMODESUPPORTED);
        previewSize = new SettingMode(PREVIEWSIZE, PREVIEWSIZEMODEVALUES, PREVIEWSIZEMODESUPPORTED);
        jpegQuality = new SettingMode(JPEG_QUALITY,JPEG_QUALITY_VALUES,JPEG_QUALITY_SUPPORTED);
        aeBracket = new SettingMode(AEBRACKETHDR, AE_BRACKET_VALUES,AE_BRACKET_SUPPORTED);
        previewFps = new SettingMode(PREVIEWFPS,PREVIEW_FPS_VALUES,PREVIEW_FPS_SUPPORTED);
        previewFormat = new SettingMode(PREVIEWFORMAT,PREVIEW_FORMAT_VALUES,PREVIEW_FORMAT_SUPPORTED);
        sceneMode = new SettingMode(SCENEMODE,SCENE_VALUES,SCENE_SUPPORTED);
        redEyeMode = new SettingMode(REDEYEMODE,REDEYE_VALUES,REDEYE_SUPPORTED);
        lenshade = new SettingMode(LENSHADEMODE,LENSHADEMODE_VALUES,LENSHADEMODE_SUPPORTED);
        zeroshutterlag = new SettingMode(ZSLMODE,ZSLMODE_VALUES,ZSLMODE_SUPPORTED,ZSLMODE_KEY);
        sceneDetectMode = new SettingMode(SCENEDETECTMODE,SCENEDETECTMODE_VALUES,SCENEDETECTMODE_SUPPORTED);
        memoryColorEnhancement = new SettingMode(MEMORYCOLORENHANCEMENTMODE, MEMORYCOLORENHANCEMENTMODE_VALUES, MEMORYCOLORENHANCEMENTMODE_SUPPORTED);
        videoSize = new SettingMode(VIDEOSIZE,VIDEOSIZE_VALUES,VIDEOSIZE_SUPPORTED);
        correlatedDoubleSampling = new SettingMode(CORRELATEDDOUBLESAMPLING,CORRELATEDDOUBLESAMPLING_VALUES,CORRELATEDDOUBLESAMPLING_SUPPORTED);
        opticalImageStabilisation = new SettingMode(SETTING_OIS,SETTING_OIS_VALUES,SETTING_OIS_SUPPORTED, SETTING_OIS_KEY);
        videoHDR = new SettingMode(VIDEOHDR,VIDEOHDR_VALUES,VIDEOHDR_SUPPORTED,VIDEOHDR_KEY);
        videoHFR = new SettingMode(VIDEOHFR,VIDEOHFR_VALUES,VIDEOHFR_SUPPORTED,VIDEOHFR_KEY);

        manualFocus = new TypeSettingsMode(MANUAL_FOCUS,MANUAL_FOCUS_VALUES,MANUAL_FOCUS_SUPPORTED,MANUAL_FOCUS_KEY,MANUAL_FOCUS_TYP);
    }


    private void putString(String settingsval, String toSet)
    {
        settings.edit().putString(settingsval,toSet).commit();
    }

    public void setCamApi(String api) {
        camApiString = api;
        putString(SETTING_API, api);
    }

    public String getCamApi() {
        camApiString = settings.getString(SETTING_API, API_1);
        return camApiString;
    }

    public void SetDevice(Devices device) {
        this.device = device;
        String t = device.name();
        putString("DEVICE", t);
    }

    public Devices getDevice() {
        String t = settings.getString("DEVICE", null);
        return TextUtils.isEmpty(t) ? null : Devices.valueOf(t);
    }

    public void setshowHelpOverlay(boolean value) {
        settings.edit().putBoolean("showhelpoverlay", value).commit();
    }

    public boolean getShowHelpOverlay() {
        return settings.getBoolean("showhelpoverlay", true);
    }

    public void SetBaseFolder(String uri) {
        putString(SETTING_BASE_FOLDER, uri);
    }

    public String GetBaseFolder() {
        return settings.getString(SETTING_BASE_FOLDER, null);
    }

    public void SetCurrentCamera(int currentcamera) {
        this.currentcamera = currentcamera;
        settings.edit().putInt(CURRENTCAMERA, currentcamera).commit();
    }

    public int GetCurrentCamera() {
        return settings.getInt(CURRENTCAMERA, 0);
    }

    public void SetCurrentModule(String modulename) {
        putString(getApiSettingString(CURRENTMODULE), modulename);
    }

    public String GetCurrentModule() {
        return settings.getString(getApiSettingString(CURRENTMODULE), KEYS.MODULE_PICTURE);
    }

    /**
     * All apis can have same parameters and to use same SETTINGS strings in ui
     * that create the extended string to load it
     * so when setting is like mexposure it gets extended to camera1mexposure0
     * camera1 is the api
     * mexposure is the settingsName
     * 0 is the camera to that the settings belong
     *
     * @param settingsName to use
     * @return
     */
    private String getApiSettingString(String settingsName) {
        StringBuilder newstring = new StringBuilder();
        if (API_SONY.equals(camApiString))
            newstring.append(API_SONY).append(settingsName);
        else if (API_1.equals(camApiString))
            newstring.append(API_1).append(settingsName).append(currentcamera);
        else
            newstring.append(API_2).append(settingsName).append(currentcamera);
        return newstring.toString();
    }


    public boolean GetWriteExternal() {
        return getBoolean(SETTING_EXTERNALSD, false);
    }

    public void SetWriteExternal(boolean write) {
        setBoolean(SETTING_EXTERNALSD, write);
    }

    public void SetCamera2FullSupported(String value) {
        putString(CAMERA2FULLSUPPORTED, value);
    }

    public String IsCamera2FullSupported() {
        String t = settings.getString(CAMERA2FULLSUPPORTED, "");
        return TextUtils.isEmpty(t) ? "" : t;
    }

    public String getApiString(String valueToGet, String defaultValue) {
        return settings.getString(getApiSettingString(valueToGet), defaultValue);
    }

    public String getApiString(String valueToGet) {
        return settings.getString(getApiSettingString(valueToGet),"");
    }

    public int getApiInt(String valueToGet) {
        return settings.getInt(getApiSettingString(valueToGet),0);
    }

    public void setApiInt(String key,int valueToSet) {
        settings.edit().putInt(key,valueToSet).commit();
    }

    public void setApiString(String settingsName, String Value) {
        putString(getApiSettingString(settingsName), Value);
    }

    public void setStringArray(String settingsName, String[] Value) {
        String tmp ="";
        for (int i= 0; i<Value.length;i++)
            tmp += Value[i]+",";
        putString(getApiSettingString(settingsName), tmp);
    }

    public String[] getStringArray(String settingsname)
    {
        return getApiString(settingsname).split(",");
    }

    public boolean getBoolean(String settings_key, boolean defaultValue)
    {
        boolean ret = settings.getBoolean(getApiSettingString(settings_key), defaultValue);
        return ret;
    }

    public void setBoolean(String settings_key, boolean valuetoSet) {

        settings.edit().putBoolean(getApiSettingString(settings_key), valuetoSet).commit();
    }

    public HashMap<String,VideoMediaProfile> getMediaProfiles()
    {
        Set<String> tmp = settings.getStringSet(getApiSettingString(SETTING_MEDIAPROFILES),new HashSet<String>());
        /*String tmp = appsettingsList.get();*/
        String[] split = new String[tmp.size()];
        tmp.toArray(split);
        HashMap<String,VideoMediaProfile>  hashMap = new HashMap<>();
        for (int i = 0; i < split.length; i++) {
            VideoMediaProfile mp = new VideoMediaProfile(split[i]);
            hashMap.put(mp.ProfileName, mp);
        }

        return hashMap;
    }

    public void saveMediaProfiles(HashMap<String,VideoMediaProfile> mediaProfileHashMap)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(getApiSettingString(SETTING_MEDIAPROFILES));
        editor.commit();
        Set<String> set =  new HashSet<String>();
        for (VideoMediaProfile profile : mediaProfileHashMap.values())
            set.add(profile.GetString());
        editor.putStringSet(getApiSettingString(SETTING_MEDIAPROFILES), set);
        if (!settings.getBoolean("tmp", false))
            editor.putBoolean("tmp", true);
        else
            editor.putBoolean("tmp",false);
        editor.commit();
    }


    public static final int FRAMEWORK_NORMAL = 0;
    public static final int FRAMEWORK_LG = 1;
    public static final int FRAMEWORK_MTK = 2;
    public static final int FRAMEWORK_MOTO_EXT = 3;
    public static final String FRAMEWORK = "framework";

    public void setFramework(int frameWork)
    {
        settings.edit().putInt(FRAMEWORK, frameWork).commit();
    }

    public int getFrameWork()
    {
        return settings.getInt(FRAMEWORK,0);
    }


    public static final String CAN_OPEN_LEGACY ="canopenlegacy";
    public void setCanOpenLegacy(boolean canopen)
    {
        settings.edit().putBoolean(CAN_OPEN_LEGACY, canopen).commit();
    }

    public boolean getCanOpenLegacy()
    {
        return settings.getBoolean(CAN_OPEN_LEGACY, false);
    }

    public static final String FRONTCAMERA ="frontcamera";
    public void setIsFrontCamera(boolean isFront)
    {
        settings.edit().putBoolean(getApiSettingString(FRONTCAMERA), isFront).commit();
    }

    public boolean getIsFrontCamera()
    {
        return settings.getBoolean(getApiSettingString(FRONTCAMERA), false);
    }

    public class SettingMode
    {
        //String to get if supported
        private String supported_key;
        //String to get the values
        private String values_key;
        //String to get the value
        private String value_key;
        //String to get the value from the cameraparameters
        private String KEY_value;

        public SettingMode(String value_key,String values_key, String supported_key)
        {
            this.value_key = value_key;
            this.values_key = values_key;
            this.supported_key= supported_key;
        }

        public SettingMode(String value_key,String values_key, String supported_key, String KEY_value) {
            this(value_key, values_key, supported_key);
            this.KEY_value = KEY_value;
        }

        public void setValues(String[] ar)
        {
            setStringArray(values_key, ar);
        }

        public String[] getValues()
        {
            return getStringArray(values_key);
        }

        public boolean isSupported()
        {
            return getBoolean(supported_key,false);
        }

        public void setIsSupported(boolean supported)
        {
            setBoolean(supported_key, supported);
        }

        public String get()
        {
            return getApiString(value_key);
        }

        public void set(String valueToSet)
        {
            setApiString(value_key,valueToSet);
        }

        public String getKEY()
        {
            return getApiString(KEY_value);
        }

        public void setKEY(String KEY)
        {
            setApiString(KEY_value,KEY);
        }

    }

    public class TypeSettingsMode extends SettingMode
    {
        private String type;

        public TypeSettingsMode(String value_key,String values_key, String supported_key, String KEY_value, String type) {
            super(value_key, values_key, supported_key, KEY_value);
            this.type = type;
        }

        public int getType()
        {
            return getApiInt(type);
        }

        public void setType(int typevalue)
        {
            setApiInt(type,typevalue);
        }
    }
}
