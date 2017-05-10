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
import android.os.Build;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.sonyremote.sonystuff.XmlElement;
import freed.cam.featuredetector.Camera1FeatureDetectorTask;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;

/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager {


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

        private String presetKey;

        public SettingMode(String value_key)
        {
            this.value_key = value_key;
            this.values_key = value_key + getResString(R.string.aps_values);
            this.supported_key= value_key + getResString(R.string.aps_supported);
            this.KEY_value = value_key + getResString(R.string.aps_key);
            this.presetKey = value_key + "preset";
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

        public boolean isPresetted()
        {
            return getBoolean(presetKey,false);
        }

        public void setIsSupported(boolean supported)
        {
            setBoolean(supported_key, supported);
        }

        public void setIsPresetted(boolean preset)
        {
            setBoolean(presetKey, preset);
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
        private String mode;

        public TypeSettingsMode(String value_key) {
            super(value_key);
            this.type = value_key + getResString(R.string.aps_type);
            this.mode = value_key + getResString(R.string.aps_mode);
        }

        public int getType()
        {
            return getApiInt(type);
        }

        public void setType(int typevalue)
        {
            setApiInt(type,typevalue);
        }

        public String getMode()
        {
            return getApiString(mode);
        }

        public void setMode(String modevalue)
        {
            setApiString(mode,modevalue);
        }
    }

    public class BooleanSettingsMode
    {
        //String to get the value
        private String value_key;
        private String presetKey;

        public BooleanSettingsMode(String value_key)
        {
            this.value_key = value_key;
            this.presetKey = value_key + "preset";
        }

        public boolean getBoolean()
        {
            return settings.getBoolean(value_key, false);
        }

        public void setBoolean(boolean value)
        {
            settings.edit().putBoolean(value_key, value).commit();
        }

        public void setIsPresetted(boolean preset)
        {
            settings.edit().putBoolean(presetKey, preset).commit();
        }

        public boolean isPresetted()
        {
            return settings.getBoolean(presetKey, false);
        }

    }

    private final String TAG = AppSettingsManager.class.getSimpleName();

    private int currentcamera;
    private String camApiString = AppSettingsManager.API_1;
   /* private Devices device;*/
    private String mDevice;
    private HashMap<String, CustomMatrix> matrixes;
    private LongSparseArray<DngProfile> dngProfileHashMap;

    private final String FEATUREDETECTED = "featuredetected";

    public static final int JPEG= 0;
    public static final int RAW = 1;
    public static final int DNG = 2;


    public static final int SHUTTER_HTC =0;
    public static final int SHUTTER_LG = 1;
    public static final int SHUTTER_MTK = 2;
    public static final int SHUTTER_QCOM_MILLISEC = 3;
    public static final int SHUTTER_QCOM_MICORSEC = 4;
    public static final int SHUTTER_MEIZU = 5;
    public static final int SHUTTER_KRILLIN = 6;
    public static final int SHUTTER_SONY = 7;
    public static final int SHUTTER_G2PRO = 8;
    public static final int SHUTTER_ZTE = 9;

    public static final int FRAMEWORK_NORMAL = 0;
    public static final int FRAMEWORK_LG = 1;
    public static final int FRAMEWORK_MTK = 2;
    public static final int FRAMEWORK_MOTO_EXT = 3;
    public static final int FRAMEWORK_SONY_CAMERAEXTENSION = 4;
    public static final String FRAMEWORK = "framework";

    public static final int NIGHTMODE_XIAOMI = 0;
    public static final int NIGHTMODE_ZTE = 1;

    public static final int HDR_MORPHO = 0;
    public static final int HDR_AUTO = 1;
    public static final int HDR_LG = 2;

    public static final int ISOMANUAL_QCOM = 0;
    public static final int ISOMANUAL_SONY =1;
    public static final int ISOMANUAL_MTK =2;
    public static final int ISOMANUAL_KRILLIN =3;



    public static final String CURRENTCAMERA = "currentcamera";
    public static final String NIGHTMODE = "nightmode";
    public static final String VIDEOPROFILE = "videoprofile";
    public static final String CUSTOMMATRIX = "custommatrix";
    public static final String TIMELAPSEFRAME = "timelapseframe";
    public static final String SETTING_API = "sonyapi";
    public static final String SETTING_LOCATION = "location";
    public static final String SETTING_EXTERNALSHUTTER = "externalShutter";
    public static final String SETTING_OrientationHack = "orientationHack";
    public static final String SETTING_TIMER = "timer";

    public static final String SETTING_FOCUSPEAK = "focuspeak";

    public static final String SETTING_EXTERNALSD = "extSD";

    public static final String API_SONY = "playmemories";
    public static final String API_1 = "camera1";
    public static final String API_2 = "camera2";


    public static final String APPVERSION = "appversion";

    public static final String CAMERA2FULLSUPPORTED = "camera2fullsupport";

    public static final String SETTING_HORIZONT = "horizont";

    public static final String SETTING_AEB1 = "aeb1";
    public static final String SETTING_AEB2 = "aeb2";
    public static final String SETTING_AEB3 = "aeb3";

    public static final String SETTING_AEB4 = "aeb4";

    public static final String SETTING_AEB5 = "aeb5";
    public static final String SETTING_AEB6 = "aeb6";
    public static final String SETTING_AEB7 = "aeb7";

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
    public final SettingMode denoiseMode;
    public final SettingMode controlMode;
    public final SettingMode edgeMode;
    public final SettingMode digitalImageStabilisationMode;
    public final SettingMode hotpixelMode;
    public final SettingMode aePriorityMode;
    public final TypeSettingsMode hdrMode;
    public final SettingMode modules;
    public final SettingMode nonZslManualMode;
    public final SettingMode virtualLensfilter;
    public final TypeSettingsMode nightMode;
    public final SettingMode videoProfile;
    public final SettingMode videoStabilisation;
    public final SettingMode interval;
    public final SettingMode intervalDuration;
    public final SettingMode opcode;
    public final SettingMode matrixset;
    public final SettingMode sdcardlocation;
    public final SettingMode colorCorrectionMode;
    public final SettingMode objectTracking;
    public final SettingMode toneMapMode;
    public final SettingMode postviewSize;
    public final SettingMode zoommode;
    public final SettingMode scalePreview;
    public final SettingMode guide;
    public final SettingMode previewFpsRange;

    public final TypeSettingsMode manualFocus;
    public final SettingMode manualExposureCompensation;
    public final TypeSettingsMode manualExposureTime;
    public final TypeSettingsMode manualIso;
    public final SettingMode manualSaturation;
    public final SettingMode manualSharpness;
    public final SettingMode manualBrightness;
    public final SettingMode manualContrast;
    public final SettingMode manualFnumber;
    public final SettingMode manualZoom;
    public final SettingMode manualBurst;
    public final SettingMode manualConvergence;
    public final SettingMode manualFx;
    public final SettingMode manualProgramShift;
    public final SettingMode manualPreviewZoom;

    public final SettingMode dualPrimaryCameraMode;
    public final SettingMode manualAperture;

    public final TypeSettingsMode manualWhiteBalance;

    public final BooleanSettingsMode opencamera1Legacy;

    public String[] opcodeUrlList;


    private SharedPreferences settings;
    private Resources resources;

    public AppSettingsManager(SharedPreferences sharedPreferences, Resources resources)
    {
        settings = sharedPreferences;
        this.resources = resources;
        Log.d(TAG, "Version/Build:" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE + " Last Version: " + getAppVersion());

       /* if (getdevice() == null)
            SetDevice(new DeviceUtils().getDevice(getResources()));*/

        pictureFormat = new SettingMode(getResString(R.string.aps_pictureformat));
        rawPictureFormat = new SettingMode(getResString(R.string.aps_rawpictureformat));
        pictureSize = new SettingMode(getResString(R.string.aps_picturesize));
        focusMode = new SettingMode(getResString(R.string.aps_focusmode));
        exposureMode = new SettingMode(getResString(R.string.aps_exposuremode));
        whiteBalanceMode = new SettingMode(getResString(R.string.aps_whitebalancemode));
        colorMode = new SettingMode(getResString(R.string.aps_colormode));
        flashMode = new SettingMode(getResString(R.string.aps_flashmode));
        isoMode = new SettingMode(getResString(R.string.aps_isomode));
        antiBandingMode = new SettingMode(getResString(R.string.aps_antibandingmode));
        imagePostProcessing = new SettingMode(getResString(R.string.aps_ippmode));
        previewSize = new SettingMode(getResString(R.string.aps_previewsize));
        jpegQuality = new SettingMode(getResString(R.string.aps_jpegquality));
        aeBracket = new SettingMode(getResString(R.string.aps_aebrackethdr));
        previewFps = new SettingMode(getResString(R.string.aps_previewfps));
        previewFormat = new SettingMode(getResString(R.string.aps_previewformat));
        sceneMode = new SettingMode(getResString(R.string.aps_scenemode));
        redEyeMode = new SettingMode(getResString(R.string.aps_redeyemode));
        lenshade = new SettingMode(getResString(R.string.aps_lenshademode));
        zeroshutterlag = new SettingMode(getResString(R.string.aps_zslmode));
        sceneDetectMode = new SettingMode(getResString(R.string.aps_scenedetectmode));
        memoryColorEnhancement = new SettingMode(getResString(R.string.aps_memorycolorenhancementmode));
        videoSize = new SettingMode(getResString(R.string.aps_videosize));
        correlatedDoubleSampling = new SettingMode(getResString(R.string.aps_cds));
        opticalImageStabilisation = new SettingMode(getResString(R.string.aps_ois));
        videoHDR = new SettingMode(getResString(R.string.aps_videohdr));
        videoHFR = new SettingMode(getResString(R.string.aps_videohfr));
        controlMode = new SettingMode(getResString(R.string.aps_controlmode));
        denoiseMode = new SettingMode(getResString(R.string.aps_denoisemode));
        edgeMode = new SettingMode(getResString(R.string.aps_edgemode));
        digitalImageStabilisationMode = new SettingMode(getResString(R.string.aps_digitalimagestabmode));
        hotpixelMode = new SettingMode(getResString(R.string.aps_hotpixel));
        aePriorityMode = new SettingMode(getResString(R.string.aps_ae_priortiy));
        hdrMode = new TypeSettingsMode(getResString(R.string.aps_hdrmode));
        modules = new SettingMode(getResString(R.string.aps_module));
        nonZslManualMode = new SettingMode(getResString(R.string.aps_nonzslmanualmode));
        virtualLensfilter = new SettingMode(getResString(R.string.aps_virtuallensfilter));
        nightMode = new TypeSettingsMode(getResString(R.string.aps_nightmode));
        videoProfile = new SettingMode(getResString(R.string.aps_videoProfile));
        videoStabilisation = new SettingMode(getResString(R.string.aps_videoStabilisation));
        interval = new SettingMode(getResString(R.string.aps_interval));
        intervalDuration = new SettingMode(getResString(R.string.aps_interval_duration));
        opcode = new SettingMode(getResString(R.string.aps_opcode));
        matrixset = new SettingMode(getResString(R.string.aps_matrixset));
        sdcardlocation = new SettingMode(getResString(R.string.aps_sdcard));
        colorCorrectionMode = new SettingMode(getResString(R.string.aps_cctmode));
        objectTracking = new SettingMode(getResString(R.string.aps_objecttracking));
        toneMapMode = new SettingMode(getResString(R.string.aps_tonemapmode));
        postviewSize = new SettingMode(getResString(R.string.aps_postviewsize));
        zoommode = new SettingMode(getResString(R.string.aps_zoommode));
        scalePreview = new SettingMode(getResString(R.string.aps_scalePreview));
        guide = new SettingMode(getResString(R.string.aps_guide));
        previewFpsRange = new SettingMode(getResString(R.string.aps_previewfpsrange));


        manualFocus = new TypeSettingsMode(getResString(R.string.aps_manualfocus));
        manualExposureCompensation = new SettingMode(getResString(R.string.aps_manualexpocomp));
        manualExposureTime = new TypeSettingsMode(getResString(R.string.aps_manualexpotime));
        manualWhiteBalance = new TypeSettingsMode(getResString(R.string.aps_manualwb));
        manualIso = new TypeSettingsMode(getResString(R.string.aps_manualiso));
        manualSaturation = new SettingMode(getResString(R.string.aps_manualsaturation));
        manualSharpness = new SettingMode(getResString(R.string.aps_manualsharpness));
        manualBrightness = new SettingMode(getResString(R.string.aps_manualbrightness));
        manualContrast = new SettingMode(getResString(R.string.aps_manualcontrast));
        manualFnumber = new SettingMode(getResString(R.string.aps_manualfnum));
        manualZoom = new SettingMode(getResString(R.string.aps_manualzoom));
        manualBurst = new SettingMode(getResString(R.string.aps_manualburst));
        manualConvergence = new SettingMode(getResString(R.string.aps_manualconvergence));
        manualFx = new SettingMode(getResString(R.string.aps_manualfx));
        manualProgramShift = new SettingMode(getResString(R.string.aps_manualprogramshift));
        manualPreviewZoom = new SettingMode(getResString(R.string.aps_manualpreviewzoom));
        manualAperture = new SettingMode(getResString(R.string.aps_manualaperture));

        opencamera1Legacy = new BooleanSettingsMode(getResString(R.string.aps_opencamera1legacy));
        dualPrimaryCameraMode = new SettingMode(getResString(R.string.aps_dualprimarycameramode));


        //first time init
        matrixes = getMatrixes();
        mDevice = sharedPreferences.getString("DEVICE","");
        if (mDevice == null || TextUtils.isEmpty(mDevice))
        {
            Log.d(TAG, "Lookup ConfigFile");
            parseAndFindSupportedDevice();
        }
        else //load only stuff for dng
        {
            Log.d(TAG, "load dngProfiles");
            opcodeUrlList = new String[2];
            dngProfileHashMap = getDngProfiles();
        }


    }

    public void RESET()
    {
        settings.edit().clear().commit();
    }

    public String getResString(int id)
    {
        return resources.getString(id);
    }

    public Resources getResources()
    { return resources;}

    public LongSparseArray<DngProfile> getDngProfilesMap()
    {
        return dngProfileHashMap;
    }

    public HashMap<String, CustomMatrix> getMatrixesMap()
    {
        return matrixes;
    }

    public boolean areFeaturesDetected()
    {
        return settings.getBoolean(FEATUREDETECTED,false);
    }

    public void setAreFeaturesDetected(boolean detected)
    {
        settings.edit().putBoolean(FEATUREDETECTED,detected).commit();
    }

    public boolean isZteAe()
    {
        return settings.getBoolean("zteae", false);
    }

    private void setZteAe(boolean legacy)
    {
        settings.edit().putBoolean("zteae",legacy).commit();
    }

    public boolean isForceRawToDng()
    {
        return settings.getBoolean("forcerawtodng", false);
    }

    private void setForceRawToDng(boolean legacy)
    {
        settings.edit().putBoolean("forcerawtodng",legacy).commit();
    }

    public boolean useQcomFocus()
    {
        return settings.getBoolean(getResString(R.string.aps_qcomfocus),false);
    }

    public boolean needRestartAfterCapture()
    {
        return settings.getBoolean("needrestartaftercapture", false);
    }

    private void setNeedRestartAfterCapture(boolean legacy)
    {
        settings.edit().putBoolean("needrestartaftercapture",legacy).commit();
    }


    public void setUseQcomFocus(boolean hasQcomFocus)
    {
        settings.edit().putBoolean(getResString(R.string.aps_qcomfocus),hasQcomFocus).commit();
    }

    public int getAppVersion()
    {
        return settings.getInt(APPVERSION,0);
    }

    public void setAppVersion(int version)
    {
        settings.edit().putInt(APPVERSION,version).commit();
    }

    private void setDngManualsSupported(boolean supported)
    {
        setBoolean("dngmanualSupported", supported);
    }

    public boolean getDngManualsSupported()
    {
        return getBoolean("dngmanualSupported", true);
    }

    private void putString(String settingsval, String toSet)
    {
        settings.edit().putString(settingsval,toSet).commit();
    }

    public boolean getBoolean(String settings_key, boolean defaultValue)
    {
        return settings.getBoolean(getApiSettingString(settings_key), defaultValue);
    }

    public void setBoolean(String settings_key, boolean valuetoSet) {

        settings.edit().putBoolean(getApiSettingString(settings_key), valuetoSet).commit();
    }

    public void setCamApi(String api) {
        camApiString = api;
        putString(SETTING_API, api);
    }

    public String getCamApi() {
        camApiString = settings.getString(SETTING_API, API_1);
        return camApiString;
    }

    public long getCamera2MaxExposureTime()
    {
        return settings.getLong("camera2maxexposuretime",0);
    }

    public void setCamera2MaxExposureTime(long max)
    {
        settings.edit().putLong("camera2maxexposuretime",max).commit();
    }

    public int getCamera2MaxIso()
    {
        return settings.getInt("camera2maxiso",0);
    }

    public void setCamera2MaxIso(int max)
    {
        settings.edit().putInt("camera2maxiso",max).commit();
    }

    private void setDevice(String device) {
        this.mDevice = device;
        putString("DEVICE", mDevice);
    }

    public String getDeviceString() {
        return mDevice;
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
        modules.set(modulename);
    }

    public String GetCurrentModule()
    {
        if (modules.get().equals(""))
            return getResString(R.string.module_picture);
        return modules.get();
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

    public void SetCamera2FullSupported(boolean value) {
        settings.edit().putBoolean(CAMERA2FULLSUPPORTED, value).commit();
    }

    public boolean IsCamera2FullSupported() {
        return settings.getBoolean(CAMERA2FULLSUPPORTED,false);
    }

    private String getApiString(String valueToGet, String defaultValue) {
        return settings.getString(getApiSettingString(valueToGet), defaultValue);
    }

    public String getApiString(String valueToGet) {
        return settings.getString(getApiSettingString(valueToGet),"");
    }

    private int getApiInt(String valueToGet) {
        return settings.getInt(getApiSettingString(valueToGet),0);
    }

    private void setApiInt(String key,int valueToSet) {
        settings.edit().putInt(getApiSettingString(key),valueToSet).commit();
    }

    public void setApiString(String settingsName, String Value) {
        putString(getApiSettingString(settingsName), Value);
    }

    public final static String SPLITTCHAR = "'";
    public void setStringArray(String settingsName, String[] Value) {
        String tmp ="";
        for (int i= 0; i<Value.length;i++)
            tmp += Value[i]+SPLITTCHAR;
        putString(getApiSettingString(settingsName), tmp);
    }

    public String[] getStringArray(String settingsname)
    {
        return getApiString(settingsname).split(SPLITTCHAR);
    }
    
    public HashMap<String,VideoMediaProfile> getMediaProfiles()
    {
        Set<String> tmp = settings.getStringSet(getApiSettingString(SETTING_MEDIAPROFILES),new HashSet<String>());
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

    public void setFramework(int frameWork)
    {
        settings.edit().putInt(FRAMEWORK, frameWork).commit();
    }

    public int getFrameWork()
    {
        return settings.getInt(FRAMEWORK,0);
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


    ///XML STUFF

    private void parseAndFindSupportedDevice()
    {
        try {
            String xmlsource = getString(resources.openRawResource(R.raw.supported_devices));
            XmlElement rootElement = XmlElement.parse(xmlsource);
            if (rootElement.getTagName().equals("devices"))
            {
                List<XmlElement> devicesList = rootElement.findChildren("device");
                Log.d(TAG, "Found " + devicesList.size() + " Devices in Xml");

                for (XmlElement device_element: devicesList)
                {
                    List<XmlElement> models = device_element.findChild("models").findChildren("item");
                    for (XmlElement mod : models)
                    {
                        if (mod.getValue().equals(Build.MODEL)) {

                            setDevice(device_element.getAttribute("name",""));
                            Log.d(TAG, "Found Device:" + getDeviceString());

                            XmlElement camera1element = device_element.findChild("camera1");


                            if (!camera1element.isEmpty()) {
                                Log.d(TAG, "Found camera1 overrides");
                                Log.v(TAG, camera1element.dumpChildElementsTagNames());
                                if (!camera1element.findChild("framework").isEmpty())
                                {
                                    setFramework(Integer.parseInt(camera1element.findChild("framework").getValue()));
                                }
                                else
                                    setFramework(Camera1FeatureDetectorTask.getFramework());

                                if (!camera1element.findChild("dngmanual").isEmpty())
                                    setDngManualsSupported(Boolean.parseBoolean(camera1element.findChild("dngmanual").getValue()));
                                else
                                    setDngManualsSupported(true);
                                Log.d(TAG, "dng manual supported:" + getDngManualsSupported());

                                if (!camera1element.findChild("opencameralegacy").isEmpty()) {
                                    opencamera1Legacy.setBoolean(Boolean.parseBoolean(camera1element.findChild("opencameralegacy").getValue()));
                                    opencamera1Legacy.setIsPresetted(true);
                                }

                                Log.d(TAG, "OpenLegacy: " + opencamera1Legacy.getBoolean() + " isPresetted:" + opencamera1Legacy.isPresetted());

                                if (!camera1element.findChild("zteae").isEmpty())
                                    setZteAe(Boolean.parseBoolean(camera1element.findChild("zte").getValue()));
                                else
                                    setZteAe(false);

                                Log.d(TAG, "isZteAE:" + isZteAe());

                                if (!camera1element.findChild("needrestartaftercapture").isEmpty())
                                    setNeedRestartAfterCapture(Boolean.parseBoolean(camera1element.findChild("needrestartaftercapture").getValue()));
                                else
                                    setNeedRestartAfterCapture(false);

                                if (!camera1element.findChild("burst").isEmpty()) {
                                    manualBurst.setIsSupported(true);
                                    int max = Integer.parseInt(camera1element.findChild("burst").getValue());
                                    manualBurst.setValues(createStringArray(1, max, 1));
                                    manualBurst.set(1 + "");
                                } else
                                    manualBurst.setIsSupported(false);
                                manualBurst.setIsPresetted(true);

                                if (!camera1element.findChild("nightmode").isEmpty()) {
                                    nightMode.setIsSupported(true);
                                    int type = Integer.parseInt(camera1element.findChild("nightmode").getValue());
                                    nightMode.setType(type);
                                } else
                                    nightMode.setIsSupported(false);
                                nightMode.setIsPresetted(true);

                                if (!camera1element.findChild("whitebalance").isEmpty())
                                {
                                    //TODO handel sdk specific
                                    Log.d(TAG, "override manual whiteblalance");
                                    int min = camera1element.findChild("whitebalance").findChild("min").getIntValue(2000);
                                    int max  = camera1element.findChild("whitebalance").findChild("max").getIntValue(8000);
                                    int step = camera1element.findChild("whitebalance").findChild("step").getIntValue(100);
                                    manualWhiteBalance.setKEY(camera1element.findChild("whitebalance").findChild("key").getValue());
                                    manualWhiteBalance.setMode(camera1element.findChild("whitebalance").findChild("mode").getValue());
                                    manualWhiteBalance.setValues(Camera1FeatureDetectorTask.createWBStringArray(min,max,step,this));
                                    manualWhiteBalance.setIsSupported(true);
                                    manualWhiteBalance.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("manualiso").isEmpty())
                                {
                                    Log.d(TAG, "override manual iso");
                                    if (!camera1element.findChild("manualiso").getAttribute("supported","false").isEmpty())
                                    {
                                        if (camera1element.findChild("manualiso").getAttribute("supported","false").equals("false")) {
                                            manualIso.setIsSupported(false);
                                            manualIso.setIsPresetted(true);
                                        }
                                        else
                                        {
                                            manualIso.setIsSupported(true);
                                            manualIso.setIsPresetted(true);
                                            setManualIso(camera1element.findChild("manualiso"));
                                        }
                                    }
                                    else
                                    {
                                        if(!camera1element.findChild("manualiso").findChildren("framework").isEmpty())
                                        {
                                            List<XmlElement> frameworksiso = camera1element.findChild("manualiso").findChildren("framework");
                                            for(XmlElement framiso : frameworksiso)
                                            {
                                                if (Integer.parseInt(framiso.getAttribute("type","0")) == getFrameWork())
                                                    setManualIso(framiso);
                                            }
                                        }
                                        else
                                            setManualIso(camera1element.findChild("manualiso"));
                                        manualIso.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("exposuretime").isEmpty())
                                {
                                    Log.d(TAG, "override manual exposuretime");
                                    if (!camera1element.findChild("exposuretime").findChild("values").isEmpty())
                                    {
                                        String name = camera1element.findChild("exposuretime").findChild("values").getValue();
                                        manualExposureTime.setValues(getResources().getStringArray(getResources().getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
                                    }
                                    if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
                                    {
                                        manualExposureTime.setKEY(camera1element.findChild("exposuretime").findChild("key").getValue());
                                    }
                                    if (!camera1element.findChild("exposuretime").findChild("key").isEmpty())
                                    {
                                        manualExposureTime.setType(camera1element.findChild("exposuretime").findChild("type").getIntValue(0));
                                        manualExposureTime.setIsSupported(true);
                                    }
                                    else {
                                        manualExposureTime.setIsSupported(false);
                                        manualExposureTime.setKEY("unsupported");
                                    }
                                    manualExposureTime.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("hdrmode").isEmpty())
                                {
                                    Log.d(TAG, "override hdr");
                                    if (camera1element.findChild("hdrmode").getAttribute("supported","false") != null)
                                    {
                                        if (!Boolean.parseBoolean(camera1element.findChild("hdrmode").getAttribute("supported","false")))
                                            hdrMode.setIsSupported(false);
                                        else{
                                            hdrMode.setIsSupported(true);
                                            hdrMode.setType(camera1element.findChild("hdrmode").getIntValue(1));
                                        }
                                    }
                                    hdrMode.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("virtuallensfilter").isEmpty())
                                {
                                    virtualLensfilter.setIsSupported(true);
                                }

                                if (!camera1element.findChild("denoise").isEmpty())
                                {
                                    if (!camera1element.findChild("denoise").getBooleanValue())
                                    {
                                        denoiseMode.setIsSupported(false);
                                        denoiseMode.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("digitalimagestab").isEmpty())
                                {
                                    if (!camera1element.findChild("digitalimagestab").getBooleanValue())
                                    {
                                        digitalImageStabilisationMode.setIsSupported(false);
                                        digitalImageStabilisationMode.setIsPresetted(true);
                                    }
                                }

                                if (!camera1element.findChild("manualfocus").isEmpty())
                                {
                                    Log.d(TAG, "override manual focus");
                                    List<XmlElement> mfs = camera1element.findChildren("manualfocus");
                                    if (mfs.size() > 1) {
                                        for (XmlElement mf : mfs) {
                                            if (mf.getIntAttribute("version", 0) == Build.VERSION.SDK_INT) {
                                                setManualFocus(mf);
                                            }
                                        }
                                    }
                                    else
                                        setManualFocus(mfs.get(0));
                                    manualFocus.setIsPresetted(true);
                                }

                                if (!camera1element.findChild("rawformat").isEmpty())
                                {
                                    Log.d(TAG, "override rawpictureformat");
                                    rawPictureFormat.set(camera1element.findChild("rawformat").getValue());
                                    rawPictureFormat.setIsPresetted(true);
                                    rawPictureFormat.setIsSupported(true);
                                }

                                if (!camera1element.findChild("opticalimagestab").isEmpty())
                                {
                                    opticalImageStabilisation.set(camera1element.findChild("opticalimagestab").findChild("key").getValue());
                                    opticalImageStabilisation.setValues(camera1element.findChild("opticalimagestab").findChild("values").getValue().split(","));
                                    opticalImageStabilisation.setIsSupported(true);
                                    opticalImageStabilisation.setIsPresetted(true);
                                }
                            }

                            XmlElement camera2element = device_element.findChild("camera2");
                            if (!camera2element.isEmpty()) {
                                Log.d(TAG,"Found Camera2 overrides");
                                if (!camera2element.findChild("forcerawtodng").isEmpty())
                                    setForceRawToDng(camera2element.findChild("forcerawtodng").getBooleanValue());
                                if (!camera2element.findChild("maxexposuretime").isEmpty())
                                {
                                    setCamera2MaxExposureTime(camera2element.findChild("maxexposuretime").getLongValue());
                                }
                                if (!camera2element.findChild("maxiso").isEmpty())
                                    setCamera2MaxIso(camera2element.findChild("maxiso").getIntValue(0));
                            }

                            dngProfileHashMap = new LongSparseArray<>();
                            getDngStuff(dngProfileHashMap, device_element);
                            Log.d(TAG, "Save Dng Profiles:" + dngProfileHashMap.size());
                            saveDngProfiles(dngProfileHashMap);

                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
    }

    private void setManualFocus(XmlElement element)
    {
        if (element.findChild("min") != null)
        {
            manualFocus.setMode(element.findChild("mode").getValue());
            manualFocus.setType(element.findChild("type").getIntValue(-1));
            manualFocus.setIsSupported(true);
            manualFocus.setKEY(element.findChild("key").getValue());
            manualFocus.setValues(Camera1FeatureDetectorTask.createManualFocusValues(element.findChild("min").getIntValue(0),element.findChild("max").getIntValue(0),element.findChild("step").getIntValue(0),this));
        }
        else
            manualFocus.setIsSupported(false);
    }

    private void setManualIso(XmlElement element)
    {
        if (!element.findChild("min").isEmpty()) {
            int min = element.findChild("min").getIntValue(100);
            int max = element.findChild("max").getIntValue(1600);
            int step = element.findChild("step").getIntValue(50);
            int type = element.findChild("type").getIntValue(0);
            manualIso.setType(type);
            manualIso.setKEY(element.findChild("key").getValue());
            manualIso.setValues(Camera1FeatureDetectorTask.createIsoValues(min, max, step, this));
            manualIso.setIsSupported(true);
            manualIso.setIsPresetted(true);
        }
        else if (!element.findChild("values").isEmpty())
        {
            String name = element.findChild("values").getValue();
            manualIso.setValues(getResources().getStringArray(getResources().getIdentifier(name, "array", BuildConfig.APPLICATION_ID)));
            manualIso.setKEY(element.findChild("key").getValue());
            int type = element.findChild("type").getIntValue(0);
            manualIso.setType(type);
            manualIso.setIsSupported(true);
            manualIso.setIsPresetted(true);
        }
    }

    private String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    private LongSparseArray<DngProfile> getDngProfiles()
    {
        LongSparseArray<DngProfile> map = new LongSparseArray<>();
        try {
            File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"dngprofiles.xml");
            Log.d(TAG, configFile.getAbsolutePath() + " exists:" + configFile.exists());

            String xmlsource = getString(new FileInputStream(configFile));
            Log.d(TAG, xmlsource);
            XmlElement rootElement = XmlElement.parse(xmlsource);
            if (rootElement.getTagName().equals("devices"))
            {
                List<XmlElement> devicesList = rootElement.findChildren("device");
                XmlElement device = devicesList.get(0);
                getDngStuff(map, device);
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
        return map;
    }

    private void getDngStuff(LongSparseArray<DngProfile> map, XmlElement device_element) {
        if (!device_element.getAttribute("opcode2", "").isEmpty())
            opcodeUrlList[0] = device_element.getAttribute("opcode2", "");
        if (!device_element.getAttribute("opcode3", "").isEmpty())
            opcodeUrlList[1] = device_element.getAttribute("opcode3", "");

        Log.d(TAG, device_element.dumpChildElementsTagNames());
        List<XmlElement> fsizeList = device_element.findChildren("filesize");
        Log.d(TAG, "Found Dng Profiles:" + fsizeList.size());
        for (XmlElement filesize_element : fsizeList) {
            long filesize = Long.parseLong(filesize_element.getAttribute("size", "0"));
            Log.d(TAG, filesize_element.dumpChildElementsTagNames());
            DngProfile profile = getProfile(filesize_element);
            map.put(filesize, profile);
        }
    }

    private DngProfile getProfile(XmlElement element)
    {
        int blacklvl = Integer.parseInt(element.findChild("blacklvl").getValue());
        int width = Integer.parseInt(element.findChild("width").getValue());
        int height = Integer.parseInt(element.findChild("height").getValue());
        int rawType = Integer.parseInt(element.findChild("rawtype").getValue());
        String colorpattern = element.findChild("colorpattern").getValue();
        int rowsize = Integer.parseInt(element.findChild("rowsize").getValue());
        String matrixset = element.findChild("matrixset").getValue();

        return new DngProfile(blacklvl,width,height,rawType,colorpattern,rowsize,matrixes.get(matrixset), matrixset);
    }

    private HashMap<String, CustomMatrix> getMatrixes()
    {
        HashMap<String, CustomMatrix> matrixHashMap = new HashMap<>();
        try {
            String xmlsource = getString(resources.openRawResource(R.raw.matrixes));
            XmlElement rootElement = XmlElement.parse(xmlsource);
            if (rootElement.getTagName().equals("matrixes"))
            {
                List<XmlElement> profileElements = rootElement.findChildren("matrix");
                for (XmlElement xmlElement: profileElements)
                {
                    String name  = xmlElement.getAttribute("name", "");
                    String c1 = xmlElement.findChild("color1").getValue();
                    String c2 = xmlElement.findChild("color2").getValue();
                    String neut = xmlElement.findChild("neutral").getValue();
                    String forward1 = xmlElement.findChild("forward1").getValue();
                    String forward2 = xmlElement.findChild("forward2").getValue();
                    String reduction1 = xmlElement.findChild("reduction1").getValue();
                    String reduction2 = xmlElement.findChild("reduction2").getValue();
                    String noise = xmlElement.findChild("noise").getValue();
                    CustomMatrix mat = new CustomMatrix(c1,c2,neut,forward1,forward2,reduction1,reduction2,noise);
                    matrixHashMap.put(name,mat);
                }
            }
        } catch (IOException e) {
            Log.WriteEx(e);
        }
        return matrixHashMap;
    }

    private String getString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString();
    }

    public void saveDngProfiles(LongSparseArray<DngProfile> dngProfileList)
    {
        BufferedWriter writer = null;
        try {

            File configFile = new File(StringUtils.GetFreeDcamConfigFolder+"dngprofiles.xml");
            Log.d(TAG, configFile.getAbsolutePath() + " exists:" + configFile.exists());
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            if (!configFile.getParentFile().exists())
                configFile.getParentFile().mkdirs();
            Log.d(TAG, configFile.getParentFile().getAbsolutePath() + " exists:" + configFile.getParentFile().exists());
            configFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(configFile));
            writer.write("<devices>" + "\r\n");
            writer.write("<device name = \""+ mDevice +"\">\r\n");

            for (int i =0; i< dngProfileList.size();i++)
            {
                long t = dngProfileList.keyAt(i);
                Log.d(TAG, "Write Profile: " + t);
                writer.write(dngProfileList.get(t).getXmlString(t));
            }

            writer.write("</device>" + "\r\n");
            writer.write("</devices>" + "\r\n");
            writer.flush();

        } catch (IOException e) {
            Log.WriteEx(e);
        }
        finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}
