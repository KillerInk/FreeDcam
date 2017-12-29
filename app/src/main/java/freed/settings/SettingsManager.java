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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
public class SettingsManager {


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
    public static final int ISOMANUAL_LG =4;

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

        private String type;
        private String mode;

        public SettingMode(String value_key)
        {
            this.value_key = value_key;
            this.values_key = value_key + getResString(R.string.aps_values);
            this.supported_key= value_key + getResString(R.string.aps_supported);
            this.KEY_value = value_key + getResString(R.string.aps_key);
            this.presetKey = value_key + "preset";
            this.type = value_key + getResString(R.string.aps_type);
            this.mode = value_key + getResString(R.string.aps_mode);
        }

        public boolean getBoolean()
        {
            return settings.getBoolean(value_key, false);
        }

        public void setBoolean(boolean value)
        {
            settings.edit().putBoolean(value_key, value).commit();
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
            return SettingsManager.this.getBoolean(supported_key,false);
        }

        public boolean isPresetted()
        {
            return SettingsManager.this.getBoolean(presetKey,false);
        }

        public void setIsSupported(boolean supported)
        {
            SettingsManager.this.setBoolean(supported_key, supported);
        }

        public void setIsPresetted(boolean preset)
        {
            SettingsManager.this.setBoolean(presetKey, preset);
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

    private final String TAG = SettingsManager.class.getSimpleName();

    private int currentcamera;
    private String camApiString = SettingsManager.API_1;
    private String mDevice;
    private HashMap<String, CustomMatrix> matrixes;
    private HashMap<String, ToneMapProfile> tonemapProfiles;
    private LongSparseArray<DngProfile> dngProfileHashMap;
    private byte[] opcode2;
    private byte[] opcode3;

    private final String FEATUREDETECTED = "featuredetected";




    public List<OpCodeUrl> opcodeUrlList;


    private SharedPreferences settings;
    private Resources resources;

    private boolean isInit =false;

    private static SettingsManager settingsManager = new SettingsManager();

    private static HashMap<Settings, SettingMode> settingsmap = new HashMap<>();

    private SettingsManager()
    {

    }

    public static SettingsManager getInstance()
    {
        return settingsManager;
    }

    public static SettingMode get(Settings key)
    {
        return settingsmap.get(key);
    }

    public synchronized void init(SharedPreferences sharedPreferences, Resources resources)
    {
        //check if its not already init while a other task waited for it
        if (isInit)
            return;
        settings = sharedPreferences;
        this.resources = resources;
        Log.d(TAG, "Version/Build:" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE + " Last Version: " + getAppVersion());
        settingsmap.put(Settings.PictureFormat, new SettingMode(getResString(R.string.aps_pictureformat)));
        settingsmap.put(Settings.rawPictureFormatSetting,new SettingMode(getResString(R.string.aps_rawpictureformat)));
        settingsmap.put(Settings.PictureSize,new SettingMode(getResString(R.string.aps_picturesize)));
        settingsmap.put(Settings.FocusMode,new SettingMode(getResString(R.string.aps_focusmode)));
        settingsmap.put(Settings.ExposureMode,new SettingMode(getResString(R.string.aps_exposuremode)));
        settingsmap.put(Settings.WhiteBalanceMode,new SettingMode(getResString(R.string.aps_whitebalancemode)));
        settingsmap.put(Settings.ColorMode,new SettingMode(getResString(R.string.aps_colormode)));
        settingsmap.put(Settings.FlashMode,new SettingMode(getResString(R.string.aps_flashmode)));
        settingsmap.put(Settings.IsoMode,new SettingMode(getResString(R.string.aps_isomode)));
        settingsmap.put(Settings.AntiBandingMode,new SettingMode(getResString(R.string.aps_antibandingmode)));
        settingsmap.put(Settings.ImagePostProcessing,new SettingMode(getResString(R.string.aps_ippmode)));
        settingsmap.put(Settings.PreviewSize,new SettingMode(getResString(R.string.aps_previewsize)));
        settingsmap.put(Settings.JpegQuality,new SettingMode(getResString(R.string.aps_jpegquality)));
        settingsmap.put(Settings.AE_Bracket,new SettingMode(getResString(R.string.aps_aebrackethdr)));
        settingsmap.put(Settings.PreviewFPS,new SettingMode(getResString(R.string.aps_previewfps)));
        settingsmap.put(Settings.PreviewFormat,new SettingMode(getResString(R.string.aps_previewformat)));
        settingsmap.put(Settings.SceneMode,new SettingMode(getResString(R.string.aps_scenemode)));
        settingsmap.put(Settings.RedEye,new SettingMode(getResString(R.string.aps_redeyemode)));
        settingsmap.put(Settings.LensShade,new SettingMode(getResString(R.string.aps_lenshademode)));
        settingsmap.put(Settings.ZSL,new SettingMode(getResString(R.string.aps_zslmode)));
        settingsmap.put(Settings.SceneDetect,new SettingMode(getResString(R.string.aps_scenedetectmode)));
        settingsmap.put(Settings.MemoryColorEnhancement,new SettingMode(getResString(R.string.aps_memorycolorenhancementmode)));
        settingsmap.put(Settings.VideoSize,new SettingMode(getResString(R.string.aps_videosize)));
        settingsmap.put(Settings.CDS_Mode,new SettingMode(getResString(R.string.aps_cds)));
        settingsmap.put(Settings.oismode,new SettingMode(getResString(R.string.aps_ois)));
        settingsmap.put(Settings.VideoHDR,new SettingMode(getResString(R.string.aps_videohdr)));
        settingsmap.put(Settings.VideoHighFramerate,new SettingMode(getResString(R.string.aps_videohfr)));
        settingsmap.put(Settings.ControlMode,new SettingMode(getResString(R.string.aps_controlmode)));
        settingsmap.put(Settings.Denoise,new SettingMode(getResString(R.string.aps_denoisemode)));
        settingsmap.put(Settings.TNR,new SettingMode(getResString(R.string.aps_tnr)));
        settingsmap.put(Settings.TNR_V,new SettingMode(getResString(R.string.aps_tnr_v)));
        settingsmap.put(Settings.SeeMore,new SettingMode(getResString(R.string.aps_seemore)));
        settingsmap.put(Settings.PDAF,new SettingMode(getResString(R.string.aps_pdaf)));
        settingsmap.put(Settings.RDI,new SettingMode(getResString(R.string.aps_rdi)));
        settingsmap.put(Settings.ChromaFlash,new SettingMode(getResString(R.string.aps_chroma_flash)));
        settingsmap.put(Settings.OptiZoom,new SettingMode(getResString(R.string.aps_optizoom)));
        settingsmap.put(Settings.ReFocus,new SettingMode(getResString(R.string.aps_refocus)));
        settingsmap.put(Settings.TruePotrait,new SettingMode(getResString(R.string.aps_truepotrait)));
        settingsmap.put(Settings.EdgeMode,new SettingMode(getResString(R.string.aps_edgemode)));
        settingsmap.put(Settings.DigitalImageStabilization,new SettingMode(getResString(R.string.aps_digitalimagestabmode)));
        settingsmap.put(Settings.HotPixelMode,new SettingMode(getResString(R.string.aps_hotpixel)));
        settingsmap.put(Settings.AE_PriorityMode,new SettingMode(getResString(R.string.aps_ae_priortiy)));
        settingsmap.put(Settings.HDRMode,new SettingMode(getResString(R.string.aps_hdrmode)));
        settingsmap.put(Settings.Module,new SettingMode(getResString(R.string.aps_module)));
        settingsmap.put(Settings.NonZslManualMode,new SettingMode(getResString(R.string.aps_nonzslmanualmode)));
        settingsmap.put(Settings.LensFilter,new SettingMode(getResString(R.string.aps_virtuallensfilter)));
        settingsmap.put(Settings.NightMode,new SettingMode(getResString(R.string.aps_nightmode)));
        settingsmap.put(Settings.VideoProfiles,new SettingMode(getResString(R.string.aps_videoProfile)));
        settingsmap.put(Settings.VideoStabilization,new SettingMode(getResString(R.string.aps_videoStabilisation)));
        settingsmap.put(Settings.IntervalShutterSleep,new SettingMode(getResString(R.string.aps_interval)));
        settingsmap.put(Settings.IntervalDuration,new SettingMode(getResString(R.string.aps_interval_duration)));
        settingsmap.put(Settings.opcode,new SettingMode(getResString(R.string.aps_opcode)));
        settingsmap.put(Settings.matrixChooser,new SettingMode(getResString(R.string.aps_matrixset)));
        settingsmap.put(Settings.SdSaveLocation,new SettingMode(getResString(R.string.aps_sdcard)));
        settingsmap.put(Settings.ColorCorrectionMode,new SettingMode(getResString(R.string.aps_cctmode)));
        settingsmap.put(Settings.ObjectTracking,new SettingMode(getResString(R.string.aps_objecttracking)));
        settingsmap.put(Settings.ToneMapMode,new SettingMode(getResString(R.string.aps_tonemapmode)));
        settingsmap.put(Settings.PostViewSize,new SettingMode(getResString(R.string.aps_postviewsize)));
        settingsmap.put(Settings.ZoomSetting,new SettingMode(getResString(R.string.aps_zoommode)));
        settingsmap.put(Settings.scalePreview,new SettingMode(getResString(R.string.aps_scalePreview)));
        settingsmap.put(Settings.GuideList,new SettingMode(getResString(R.string.aps_guide)));
        settingsmap.put(Settings.PreviewFpsRange,new SettingMode(getResString(R.string.aps_previewfpsrange)));
        settingsmap.put(Settings.M_Focus,new SettingMode(getResString(R.string.aps_manualfocus)));
        settingsmap.put(Settings.M_ExposureCompensation,new SettingMode(getResString(R.string.aps_manualexpocomp)));
        settingsmap.put(Settings.M_ExposureTime,new SettingMode(getResString(R.string.aps_manualexpotime)));
        settingsmap.put(Settings.M_Whitebalance,new SettingMode(getResString(R.string.aps_manualwb)));
        settingsmap.put(Settings.M_ManualIso,new SettingMode(getResString(R.string.aps_manualiso)));
        settingsmap.put(Settings.M_Saturation,new SettingMode(getResString(R.string.aps_manualsaturation)));
        settingsmap.put(Settings.M_Sharpness,new SettingMode(getResString(R.string.aps_manualsharpness)));
        settingsmap.put(Settings.M_Brightness,new SettingMode(getResString(R.string.aps_manualbrightness)));
        settingsmap.put(Settings.M_Contrast,new SettingMode(getResString(R.string.aps_manualcontrast)));
        settingsmap.put(Settings.M_Fnumber,new SettingMode(getResString(R.string.aps_manualfnum)));
        settingsmap.put(Settings.M_Zoom,new SettingMode(getResString(R.string.aps_manualzoom)));
        settingsmap.put(Settings.M_Burst,new SettingMode(getResString(R.string.aps_manualburst)));
        settingsmap.put(Settings.M_3D_Convergence,new SettingMode(getResString(R.string.aps_manualconvergence)));
        settingsmap.put(Settings.M_FX,new SettingMode(getResString(R.string.aps_manualfx)));
        settingsmap.put(Settings.M_ProgramShift,new SettingMode(getResString(R.string.aps_manualprogramshift)));
        settingsmap.put(Settings.M_PreviewZoom,new SettingMode(getResString(R.string.aps_manualpreviewzoom)));
        settingsmap.put(Settings.M_Aperture,new SettingMode(getResString(R.string.aps_manualaperture)));
        settingsmap.put(Settings.openCamera1Legacy,new SettingMode(getResString(R.string.aps_opencamera1legacy)));
        settingsmap.put(Settings.dualPrimaryCameraMode,new SettingMode(getResString(R.string.aps_dualprimarycameramode)));
        settingsmap.put(Settings.useHuaweiCamera2Extension,new SettingMode(getResString(R.string.aps_usehuaweicam2)));
        settingsmap.put(Settings.support12bitRaw,new SettingMode(getResString(R.string.aps_support12bitraw)));
        settingsmap.put(Settings.useQcomFocus,new SettingMode(getResString(R.string.aps_qcomfocus)));
        settingsmap.put(Settings.forceRawToDng,new SettingMode(getResString(R.string.aps_forcerawtondng)));
        settingsmap.put(Settings.needRestartAfterCapture,new SettingMode(getResString(R.string.aps_needrestartaftercapture)));
        settingsmap.put(Settings.Ae_TargetFPS,new SettingMode(getResString(R.string.aps_ae_targetFPS)));
        settingsmap.put(Settings.orientationHack,new SettingMode(getResString(R.string.aps_orientationHack)));
        settingsmap.put(Settings.tonemapChooser,new SettingMode(getResString(R.string.aps_tonemapProfile)));
        settingsmap.put(Settings.selfTimer,new SettingMode(getResString(R.string.aps_selftimer)));

        camApiString = settings.getString(SETTING_API, API_1);
        //get last used camera, without it default camera is always 0
        currentcamera = GetCurrentCamera();

        loadOpCodes();

        parseXml(sharedPreferences, resources);

        isInit = true;

    }

    public void release()
    {
        settingsmap.clear();
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
            opcodeUrlList = new ArrayList<>();
            dngProfileHashMap = parser.getDngProfiles(matrixes);
        }
    }

    private void loadOpCodes()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File op2 = new File(StringUtils.GetFreeDcamConfigFolder+ currentcamera+"opc2.bin");
                if (op2.exists())
                    try {
                        opcode2 = RawToDng.readFile(op2);
                        Log.d(TAG, "opcode2 size" + opcode2.length);
                    } catch (IOException e) {
                        Log.WriteEx(e);
                    }
                    else
                        opcode2 = null;
                File op3 = new File(StringUtils.GetFreeDcamConfigFolder+currentcamera+"opc3.bin");
                if (op3.exists())
                    try {
                        opcode3 = RawToDng.readFile(op3);
                        Log.d(TAG, "opcode3 size" + opcode3.length);
                    } catch (IOException e) {
                        Log.WriteEx(e);
                    }
                    else
                        opcode3 = null;
            }
        }).start();

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
        loadOpCodes();
    }

    public int GetCurrentCamera() {
        return settings.getInt(CURRENTCAMERA, 0);
    }

    public void SetCurrentModule(String modulename) {
        settingsmap.get(Settings.Module).set(modulename);
    }

    public String GetCurrentModule()
    {
        if (TextUtils.isEmpty(settingsmap.get(Settings.Module).get()))
            return getResString(R.string.module_picture);
        return settingsmap.get(Settings.Module).get();
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
        return camApiString+settingsName+currentcamera;
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

    public void setCamerasCount(int count)
    {
        setApiInt("camerascount",count);
    }

    public int getCamerasCount()
    {
        return getApiInt("camerascount");
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
