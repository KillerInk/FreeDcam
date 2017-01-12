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
import android.content.res.Resources;
import android.text.TextUtils;

import com.troop.freedcam.R;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.utils.DeviceUtils.Devices;

import static freed.cam.apis.KEYS.BAYER;


/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager {
    private final String TAG = AppSettingsManager.class.getSimpleName();

    private int currentcamera;
    private String camApiString = AppSettingsManager.API_1;
    private Devices device;

    private final String FEATUREDETECTED = "featuredetected";

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
    public static final String SETTING_COLORCORRECTION = "colorcorrection";

    public static final String SETTING_TONEMAP = "tonemap";

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

    private static final String VALUES = "values";
    private static final String SUPPORTED = "supported";
    private static final String KEY = "key";
    private static final String TYPE = "type";


    public static final String SETTINGS_PREVIEWZOOM = "previewzoom";

    public static final String SETTING_BASE_FOLDER = "base_folder";

    public static final String SETTING_MEDIAPROFILES = "media_profiles";

    public static final String SETTING_AFBRACKETMAX = "afbracketmax";
    public static final String SETTING_AFBRACKETMIN = "afbracketmin";
    public static final String SETTINGS_NIGHTOVERLAY = "nighoverlay";

    public final SettingMode pictureFormat;

    public final SettingMode rawPictureFormat;

    public final SettingMode pictureSize;

    public final SettingMode focusMode;

    public final SettingMode exposureMode;

    public final SettingMode whiteBalanceMode;

    public final SettingMode colorMode;

    public final SettingMode flashMode;


    public final SettingMode isoMode;


    public final SettingMode antiBandingMode;

    public final SettingMode imagePostProcessing;

    public final SettingMode previewSize;


    public final SettingMode jpegQuality;

    public final SettingMode aeBracket;

    public final SettingMode previewFps;

    public final SettingMode previewFormat;

    public final SettingMode sceneMode;

    public final SettingMode redEyeMode;

    public final SettingMode lenshade;

    public final SettingMode zeroshutterlag;

    public final SettingMode sceneDetectMode;

    public final SettingMode memoryColorEnhancement;

    public final SettingMode videoSize;

    public final SettingMode correlatedDoubleSampling;


    public final SettingMode opticalImageStabilisation;

    public final SettingMode videoHDR;

    public final SettingMode videoHFR;

    public final TypeSettingsMode manualFocus;


    public final SettingMode denoiseMode;

    public final SettingMode controlMode;

    public final SettingMode edgeMode;

    public final SettingMode digitalImageStabilisationMode;

    public final SettingMode hotpixelMode;


    private SharedPreferences settings;
    private Resources resources;

    public AppSettingsManager(SharedPreferences sharedPreferences, Resources resources)
    {
        settings = sharedPreferences;
        this.resources = resources;
        pictureFormat = new SettingMode(getString(R.string.aps_pictureformat));
        rawPictureFormat = new SettingMode(getString(R.string.aps_rawpictureformat));
        pictureSize = new SettingMode(getString(R.string.aps_picturesize));
        focusMode = new SettingMode(getString(R.string.aps_focusmode));
        exposureMode = new SettingMode(getString(R.string.aps_exposuremode));
        whiteBalanceMode = new SettingMode(getString(R.string.aps_whitebalancemode));
        colorMode = new SettingMode(getString(R.string.aps_colormode));
        flashMode = new SettingMode(getString(R.string.aps_flashmode));
        isoMode = new SettingMode(getString(R.string.aps_isomode));
        antiBandingMode = new SettingMode(getString(R.string.aps_antibandingmode));
        imagePostProcessing = new SettingMode(getString(R.string.aps_ippmode));
        previewSize = new SettingMode(getString(R.string.aps_previewsize));
        jpegQuality = new SettingMode(getString(R.string.aps_jpegquality));
        aeBracket = new SettingMode(getString(R.string.aps_aebrackethdr));
        previewFps = new SettingMode(getString(R.string.aps_previewfps));
        previewFormat = new SettingMode(getString(R.string.aps_previewformat));
        sceneMode = new SettingMode(getString(R.string.aps_scenemode));
        redEyeMode = new SettingMode(getString(R.string.aps_redeyemode));
        lenshade = new SettingMode(getString(R.string.aps_lenshademode));
        zeroshutterlag = new SettingMode(getString(R.string.aps_zslmode));
        sceneDetectMode = new SettingMode(getString(R.string.aps_scenedetectmode));
        memoryColorEnhancement = new SettingMode(getString(R.string.aps_memorycolorenhancementmode));
        videoSize = new SettingMode(getString(R.string.aps_videosize));
        correlatedDoubleSampling = new SettingMode(getString(R.string.aps_cds));
        opticalImageStabilisation = new SettingMode(getString(R.string.aps_ois));
        videoHDR = new SettingMode(getString(R.string.aps_videohdr));
        videoHFR = new SettingMode(getString(R.string.aps_videohfr));
        controlMode = new SettingMode(getString(R.string.aps_controlmode));
        denoiseMode = new SettingMode(getString(R.string.aps_denoisemode));
        edgeMode = new SettingMode(getString(R.string.aps_edgemode));
        digitalImageStabilisationMode = new SettingMode(getString(R.string.aps_digitalimagestabmode));
        hotpixelMode = new SettingMode(getString(R.string.aps_hotpixel));

        manualFocus = new TypeSettingsMode(getString(R.string.aps_manualfocus));
    }

    private String getString(int id)
    {
        return resources.getString(id);
    }

    public boolean areFeaturesDetected()
    {
        return settings.getBoolean(FEATUREDETECTED,false);
    }

    public void setAreFeaturesDetected(boolean detected)
    {
        settings.edit().putBoolean(FEATUREDETECTED,detected).commit();
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

        public SettingMode(String value_key)
        {
            this.value_key = value_key;
            this.values_key = value_key + getString(R.string.aps_values);
            this.supported_key= value_key + getString(R.string.aps_supported);
            this.KEY_value = value_key + getString(R.string.aps_key);
        }

        public void setValues(String[] ar)
        {
            setStringArray(values_key, ar);
        }

        public String[] getValues()
        {
            return getStringArray(values_key);
        }

        public boolean contains(String value)
        {
            String[] values = getValues();
            for (String v : values)
            {
                if (v.equals(value))
                    return true;
            }
            return false;
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

        public TypeSettingsMode(String value_key) {
            super(value_key);
            this.type = value_key + getString(R.string.aps_type);
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
