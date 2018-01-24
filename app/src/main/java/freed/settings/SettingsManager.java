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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.jni.RawToDng;
import freed.settings.mode.SettingInterface;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;

/**
 * Created by troop on 19.08.2014.
 */
public class SettingsManager implements SettingsManagerInterface {


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
    public static final int ISOMANUAL_Xiaomi =5;

    public static final String CURRENTCAMERA = "currentcamera";
    public static final String NIGHTMODE = "nightmode";
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

    public List<OpCodeUrl> opcodeUrlList;

    private final String TAG = SettingsManager.class.getSimpleName();
    private int currentcamera;
    private String camApiString = SettingsManager.API_1;
    private String mDevice;
    private HashMap<String, CustomMatrix> matrixes;
    private HashMap<String, ToneMapProfile> tonemapProfiles;
    private LongSparseArray<DngProfile> dngProfileHashMap;
    private byte[] opcode2;
    private byte[] opcode3;
    private SharedPreferences settings;
    private Resources resources;
    private boolean isInit =false;
    private Frameworks frameworks;

    private static SettingsManager settingsManager = new SettingsManager();

    private static HashMap<SettingKeys.Key, SettingInterface> settingsmap = new HashMap<>();

    private SettingsManager()
    {

    }

    public static SettingsManager getInstance()
    {
        return settingsManager;
    }

    public static <T> T get(SettingKeys.Key<T> key)
    {
        return key.getType().cast(settingsmap.get(key));
    }

    public synchronized void init(SharedPreferences sharedPreferences, Resources resources)
    {
        //check if its not already init while a other task waited for it
        if (isInit)
            return;
        settings = sharedPreferences;

        this.resources = resources;
        SettingKeys.Key[] keys = SettingKeys.getKeyList();

        for (SettingKeys.Key k: keys)
            createSetting(k);

        camApiString = settings.getString(SETTING_API, API_1);
        //get last used camera, without it default camera is always 0
        currentcamera = GetCurrentCamera();
        try {
            String fw = settings.getString(FRAMEWORK,"Default");
            frameworks = Frameworks.valueOf(fw);
        }
        catch (ClassCastException ex)
        {
            Log.d(TAG, "failed to parse Framework, use Default");
            frameworks = Frameworks.Default;
        }


        loadOpCodes();

        parseXml(sharedPreferences, resources);

        isInit = true;

    }

    private void createSetting(SettingKeys.Key key)
    {
        Constructor ctr = key.getType().getConstructors()[0];
        try {
            SettingInterface settingInterface = (SettingInterface)ctr.newInstance(this,getResString(key.getRessourcesStringID()));
            settingsmap.put(key,settingInterface);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
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
        }
        dngProfileHashMap = parser.getDngProfiles(matrixes);
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

    @Override
    public boolean getApiBoolean(String settings_key, boolean defaultValue)
    {
        return settings.getBoolean(getApiSettingString(settings_key), defaultValue);
    }

    @Override
    public boolean getBoolean(String settings_key, boolean defaultValue)
    {
        return settings.getBoolean(settings_key, defaultValue);
    }

    @Override
    public void setApiBoolean(String settings_key, boolean valuetoSet) {

        settings.edit().putBoolean(getApiSettingString(settings_key), valuetoSet).commit();
    }

    @Override
    public void setBoolean(String settings_key, boolean valuetoSet) {

        settings.edit().putBoolean(settings_key, valuetoSet).commit();
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
        get(SettingKeys.Module).set(modulename);
    }

    public String GetCurrentModule()
    {
        if (TextUtils.isEmpty(get(SettingKeys.Module).get()))
            return getResString(R.string.module_picture);
        return get(SettingKeys.Module).get();
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
        return getApiBoolean(SETTING_EXTERNALSD, false);
    }

    public void SetWriteExternal(boolean write) {
        setApiBoolean(SETTING_EXTERNALSD, write);
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

    @Override
    public String getApiString(String valueToGet) {
        return settings.getString(getApiSettingString(valueToGet),"");
    }
    @Override
    public int getApiInt(String valueToGet) {
        return settings.getInt(getApiSettingString(valueToGet),0);
    }

    @Override
    public void setApiInt(String key,int valueToSet) {
        settings.edit().putInt(getApiSettingString(key),valueToSet).commit();
    }

    @Override
    public void setApiString(String settingsName, String Value) {
        putString(getApiSettingString(settingsName), Value);
    }

    public final static String SPLITTCHAR = "'";
    @Override
    public void setStringArray(String settingsName, String[] Value) {
        String tmp ="";
        for (int i= 0; i<Value.length;i++)
            tmp += Value[i]+SPLITTCHAR;
        putString(getApiSettingString(settingsName), tmp);
    }

    @Override
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

    public void setFramework(Frameworks frameWork)
    {
        frameworks = frameWork;
        settings.edit().putString(FRAMEWORK, frameWork.toString()).commit();
    }

    public Frameworks getFrameWork()
    {
        return frameworks;
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
