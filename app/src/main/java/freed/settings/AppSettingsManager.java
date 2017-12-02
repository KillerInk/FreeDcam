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

package freed.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.jni.RawToDng;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;

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
    private HashMap<String, ToneMapProfile> tonemapProfiles;
    private LongSparseArray<DngProfile> dngProfileHashMap;
    private byte[] opcode2;
    private byte[] opcode3;

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
    public static final int HDR_MOTO = 3;

    public static final int ISOMANUAL_QCOM = 0;
    public static final int ISOMANUAL_SONY =1;
    public static final int ISOMANUAL_MTK =2;
    public static final int ISOMANUAL_KRILLIN =3;



    public static final String CURRENTCAMERA = "currentcamera";
    public static final String NIGHTMODE = "nightmode";
    public static final String VIDEOPROFILE = "videoprofile";
    public static final String TIMELAPSEFRAME = "timelapseframe";
    public static final String SETTING_API = "sonyapi";
    public static final String SETTING_LOCATION = "location";
    public static final String SETTING_EXTERNALSHUTTER = "externalShutter";

    public static final String SETTING_TIMER = "timer";

    public static final String SETTING_FOCUSPEAK = "focuspeak";

    public static final String SETTING_EXTERNALSD = "extSD";

    public static final String API_SONY = "playmemories";
    public static final String API_1 = "camera1";
    public static final String API_2 = "camera2";


    public static final String APPVERSION = "appversion";

    public static final String HAS_CAMERA2_FEATURES = "camera2fullsupport";

    public static final String SETTING_HORIZONT = "horizont";

    public static final String SETTING_BASE_FOLDER = "base_folder";

    public static final String SETTING_MEDIAPROFILES = "media_profiles";

    public static final String SETTING_AFBRACKETMAX = "afbracketmax";
    public static final String SETTING_AFBRACKETMIN = "afbracketmin";
    public static final String SETTINGS_NIGHTOVERLAY = "nighoverlay";

    public SettingMode pictureFormat;
    public SettingMode rawPictureFormat;
    public SettingMode pictureSize;
    public SettingMode focusMode;
    public SettingMode exposureMode;
    public SettingMode whiteBalanceMode;
    public SettingMode colorMode;
    public SettingMode flashMode;
    public SettingMode isoMode;
    public SettingMode antiBandingMode;
    public SettingMode imagePostProcessing;
    public SettingMode previewSize;
    public SettingMode jpegQuality;
    public SettingMode aeBracket;
    public SettingMode previewFps;
    public SettingMode previewFormat;
    public SettingMode sceneMode;
    public SettingMode redEyeMode;
    public SettingMode lenshade;
    public SettingMode zeroshutterlag;
    public SettingMode sceneDetectMode;
    public SettingMode memoryColorEnhancement;
    public SettingMode videoSize;
    public SettingMode correlatedDoubleSampling;
    public SettingMode opticalImageStabilisation;
    public SettingMode videoHDR;
    public SettingMode videoHFR;
    public SettingMode denoiseMode;
    public SettingMode temporal_nr;
    public SettingMode temporal_video_nr;
    public SettingMode seemore_tonemap;
    public SettingMode truepotrait;
    public SettingMode optizoom;
    public SettingMode chromaflash;
    public SettingMode rawdumpinterface;
    public SettingMode pdafcontrol;
    public SettingMode refocus;

    public SettingMode controlMode;
    public SettingMode edgeMode;
    public SettingMode digitalImageStabilisationMode;
    public SettingMode hotpixelMode;
    public SettingMode aePriorityMode;
    public TypeSettingsMode hdrMode;
    public SettingMode modules;
    public SettingMode nonZslManualMode;
    public SettingMode virtualLensfilter;
    public TypeSettingsMode nightMode;
    public SettingMode videoProfile;
    public SettingMode videoStabilisation;
    public SettingMode interval;
    public SettingMode intervalDuration;
    public SettingMode opcode;
    public SettingMode matrixset;
    public SettingMode sdcardlocation;
    public SettingMode colorCorrectionMode;
    public SettingMode objectTracking;
    public SettingMode toneMapMode;
    public SettingMode postviewSize;
    public SettingMode zoommode;
    public SettingMode scalePreview;
    public SettingMode guide;
    public SettingMode previewFpsRange;
    public SettingMode tonemapProfilesSettings;

    public TypeSettingsMode manualFocus;
    public SettingMode manualExposureCompensation;
    public TypeSettingsMode manualExposureTime;
    public TypeSettingsMode manualIso;
    public SettingMode manualSaturation;
    public SettingMode manualSharpness;
    public SettingMode manualBrightness;
    public SettingMode manualContrast;
    public SettingMode manualFnumber;
    public SettingMode manualZoom;
    public SettingMode manualBurst;
    public SettingMode manualConvergence;
    public SettingMode manualFx;
    public SettingMode manualProgramShift;
    public SettingMode manualPreviewZoom;

    public SettingMode selfTimer;

    public SettingMode dualPrimaryCameraMode;
    public SettingMode manualAperture;
    public SettingMode ae_TagetFPS;

    public TypeSettingsMode manualWhiteBalance;

    public BooleanSettingsMode opencamera1Legacy;
    public BooleanSettingsMode useHuaweiCam2Extension;
    public BooleanSettingsMode support12bitRaw;
    public BooleanSettingsMode orientationhack;
    public BooleanSettingsMode qcomAFocus;
    public BooleanSettingsMode dngSupportManualModes;
    public BooleanSettingsMode forceRawToDng;
    public BooleanSettingsMode needRestartAfterCapture;

    public String[] opcodeUrlList;


    private SharedPreferences settings;
    private Resources resources;

    private boolean isInit =false;

    private static AppSettingsManager appSettingsManager  = new AppSettingsManager();

    private AppSettingsManager()
    {

    }

    public static AppSettingsManager getInstance()
    {
        return appSettingsManager;
    }

    public synchronized void init(SharedPreferences sharedPreferences, Resources resources)
    {
        if (isInit)
            return;
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
        //**********************************************QC PARAMS NEW HAL
        temporal_nr = new SettingMode(getResString(R.string.aps_tnr));
        temporal_video_nr = new SettingMode(getResString(R.string.aps_tnr_v));
        seemore_tonemap = new SettingMode(getResString(R.string.aps_seemore));
        pdafcontrol = new SettingMode(getResString(R.string.aps_pdaf));
        rawdumpinterface = new SettingMode(getResString(R.string.aps_rdi));
        chromaflash = new SettingMode(getResString(R.string.aps_chroma_flash));
        optizoom = new SettingMode(getResString(R.string.aps_optizoom));
        refocus = new SettingMode(getResString(R.string.aps_refocus));
        truepotrait = new SettingMode(getResString(R.string.aps_truepotrait));
        //*******************************************************************

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
        useHuaweiCam2Extension = new BooleanSettingsMode(getResString(R.string.aps_usehuaweicam2));
        support12bitRaw = new BooleanSettingsMode(getResString(R.string.aps_support12bitraw));
        qcomAFocus = new BooleanSettingsMode(getResString(R.string.aps_qcomfocus));
        dngSupportManualModes = new BooleanSettingsMode(getResString(R.string.aps_dngsupportmanualmodes));
        forceRawToDng = new BooleanSettingsMode(getResString(R.string.aps_forcerawtondng));
        needRestartAfterCapture = new BooleanSettingsMode(getResString(R.string.aps_needrestartaftercapture));

        ae_TagetFPS = new SettingMode(getResString(R.string.aps_ae_targetFPS));

        orientationhack = new BooleanSettingsMode(getResString(R.string.aps_orientationHack));
        tonemapProfilesSettings = new SettingMode(getResString(R.string.aps_tonemapProfile));
        selfTimer = new SettingMode(getResString(R.string.aps_selftimer));


        loadOpCodes();

        parseXml(sharedPreferences, resources);

        isInit = true;

    }

    public void release()
    {
        isInit = false;
        resources = null;
        settings = null;
    }

    public boolean isInit()
    {
        return isInit;
    }

    private void parseXml(SharedPreferences sharedPreferences, Resources resources) {
        XmlParserWriter parser = new XmlParserWriter();
        //first time init
        matrixes = parser.getMatrixes(resources);
        mDevice = sharedPreferences.getString("DEVICE","");
        tonemapProfiles = parser.getToneMapProfiles();
        if (mDevice == null || TextUtils.isEmpty(mDevice))
        {
            Log.d(TAG, "Lookup ConfigFile");
            parser.parseAndFindSupportedDevice(resources,matrixes);
        }
        else //load only stuff for dng
        {
            Log.d(TAG, "load dngProfiles");
            opcodeUrlList = new String[2];
            dngProfileHashMap = parser.getDngProfiles(matrixes);
        }
    }

    private void loadOpCodes()
    {
        File op2 = new File(StringUtils.GetFreeDcamConfigFolder+"opc2.bin");
        if (op2.exists())
            try {
                opcode2 = RawToDng.readFile(op2);
                Log.d(TAG, "opcode2 size" + opcode2.length);
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        File op3 = new File(StringUtils.GetFreeDcamConfigFolder+"opc3.bin");
        if (op3.exists())
            try {
                opcode3 = RawToDng.readFile(op3);
                Log.d(TAG, "opcode3 size" + opcode3.length);
            } catch (IOException e) {
                Log.WriteEx(e);
            }
    }

    public void RESET()
    {
        settings.edit().clear().commit();
        parseXml(settings, resources);
    }

    public boolean appVersionHasChanged()
    {
        return BuildConfig.VERSION_CODE != getAppVersion();
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

    public HashMap<String, ToneMapProfile> getToneMapProfiles()
    {
        return tonemapProfiles;
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

    public void setZteAe(boolean legacy)
    {
        settings.edit().putBoolean("zteae",legacy).commit();
    }

    public boolean isOverrideDngProfile()
    {
        return settings.getBoolean("overrideprofile", false);
    }

    public void setsOverrideDngProfile(boolean legacy)
    {
        settings.edit().putBoolean("overrideprofile",legacy).commit();
    }

    public int getAppVersion()
    {
        return settings.getInt(APPVERSION,0);
    }

    public void setAppVersion(int version)
    {
        settings.edit().putInt(APPVERSION,version).commit();
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
        SharedPreferences.Editor editor =  settings.edit();
        editor.putLong("camera2maxexposuretime",max);
        editor.commit();
        Log.d(TAG,"Override max expotime:" +settings.getLong("camera2maxexposuretime",0));
    }

    public void setCamera2MinExposureTime(long min)
    {
        SharedPreferences.Editor editor =  settings.edit();
        editor.putLong("camera2minexposuretime",min);
        editor.commit();
        Log.d(TAG,"Override min expotime:" +settings.getLong("camera2minexposuretime",0));
    }
    public long getCamera2MinExposureTime()
    {
        return settings.getLong("camera2minexposuretime",0);
    }

    public int getCamera2MaxIso()
    {
        return settings.getInt("camera2maxiso",0);
    }

    public void setCamera2MaxIso(int max)
    {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("camera2maxiso",max);
        editor.commit();
        Log.d(TAG,"Override max iso:" +settings.getInt("camera2maxiso",0));
    }

    public void setDevice(String device) {
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
        if (TextUtils.isEmpty(modules.get()))
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

    public void setHasCamera2Features(boolean value) {
        settings.edit().putBoolean(HAS_CAMERA2_FEATURES, value).commit();
    }

    public boolean hasCamera2Features() {
        return settings.getBoolean(HAS_CAMERA2_FEATURES,false);
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

    public byte[] getOpcode2()
    {
        return opcode2;
    }

    public byte[] getOpcode3()
    {
        return opcode3;
    }


    ///XML STUFF



}
