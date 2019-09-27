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
import android.text.TextUtils;

import androidx.collection.LongSparseArray;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
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
import freed.jni.OpCode;
import freed.settings.mode.SettingInterface;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;
import freed.views.VideoToneCurveProfile;

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
    public static final String SETTING_TIMER = "timer";
    public static final String SETTING_FOCUSPEAK = "focuspeak";
    public static final String SETTING_EXTERNALSD = "extSD";
    public static final String API_SONY = "playmemories";
    public static final String API_1 = "camera1";
    public static final String API_2 = "camera2";
    public static final String APPVERSION = "appversion";
    public static final String HAS_CAMERA2_FEATURES = "camera2fullsupport";
    public static final String SETTING_BASE_FOLDER = "base_folder";
    public static final String SETTING_MEDIAPROFILES = "media_profiles";
    public static final String SETTING_AFBRACKETMAX = "afbracketmax";
    public static final String SETTING_AFBRACKETMIN = "afbracketmin";

    public List<OpCodeUrl> opcodeUrlList;

    private final String TAG = SettingsManager.class.getSimpleName();

    private String mDevice;
    private HashMap<String, CustomMatrix> matrixes;
    private HashMap<String, ToneMapProfile> tonemapProfiles;
    private HashMap<String, VideoToneCurveProfile> videoToneCurveProfiles;
    private LongSparseArray<DngProfile> dngProfileHashMap;
    private OpCode opCode;
    //private SharedPreferences settings;
    private Resources resources;
    private boolean isInit =false;
    private Frameworks frameworks;

    private SettingsStorage settingsStorage;

    private static SettingsManager settingsManager = new SettingsManager();

    private static HashMap<SettingKeys.Key, SettingInterface> settingsmap = new HashMap<>();



    private SettingsManager()
    {

    }

    public static SettingsManager getInstance()
    {
        return settingsManager;
    }

    public void save()
    {
        if (settingsStorage != null)
            settingsStorage.save();
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
        isInit = true;
        //settings = sharedPreferences;
        settingsStorage = new SettingsStorage();
        settingsStorage.load();
        this.resources = resources;
        SettingKeys.Key[] keys = SettingKeys.getKeyList();

        for (SettingKeys.Key k: keys)
            createSetting(k);

        try {
            String fw = settingsStorage.getString(FRAMEWORK,"Default");
            frameworks = Frameworks.valueOf(fw);
        }
        catch (ClassCastException ex)
        {
            Log.d(TAG, "failed to parse Framework, use Default");
            frameworks = Frameworks.Default;
        }


        loadOpCodes();

        parseXml(settingsStorage, resources);



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
        settingsStorage.save();
        settingsmap.clear();
        isInit = false;
        resources = null;
        //settings = null;
        settingsStorage.reset();
    }

    public boolean isInit()
    {
        return isInit;
    }

    private void parseXml(SettingsStorage sharedPreferences, Resources resources) {
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
        new Thread(() -> {
            File op2 = new File(StringUtils.GetFreeDcamConfigFolder+settingsStorage.getInt(CURRENTCAMERA,0)+"opc2.bin");
            File op3 = new File(StringUtils.GetFreeDcamConfigFolder+settingsStorage.getInt(CURRENTCAMERA,0)+"opc3.bin");
            if (op2.exists() || op3.exists())
                opCode = new OpCode(op2,op3);
            else
                opCode = null;

        }).start();

    }

    public void RESET()
    {
        settingsStorage.reset();
        //settings.edit().clear().commit();
        parseXml(settingsStorage, resources);
        settingsStorage.save();
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

    public HashMap<String, VideoToneCurveProfile> getVideoToneCurveProfiles()
    {
        return videoToneCurveProfiles;
    }

    public void saveVideoToneCurveProfile(VideoToneCurveProfile videoToneCurveProfile)
    {
        videoToneCurveProfiles.put(videoToneCurveProfile.name, videoToneCurveProfile);
    }


    public boolean isZteAe()
    {
        return settingsStorage.getBoolean("zteae", false);
    }

    public void setZteAe(boolean legacy)
    {
        settingsStorage.setBoolean("zteae",legacy);
    }


    public void setsOverrideDngProfile(boolean legacy)
    {
        settingsStorage.setBoolean("overrideprofile",legacy);
    }

    public int getAppVersion()
    {
        return settingsStorage.getInt(APPVERSION, 0);
    }

    public void setAppVersion(int version)
    {
        settingsStorage.setInt(APPVERSION, version);
    }

    @Override
    public boolean getApiBoolean(String settings_key, boolean defaultValue)
    {
        return settingsStorage.getApiBoolean(settings_key,defaultValue);
    }

    @Override
    public boolean getBoolean(String settings_key, boolean defaultValue)
    {
        return settingsStorage.getBoolean(settings_key,defaultValue);
    }

    @Override
    public void setApiBoolean(String settings_key, boolean valuetoSet) {
        settingsStorage.setApiBoolean(settings_key,valuetoSet);
    }

    @Override
    public void setBoolean(String settings_key, boolean valuetoSet) {
        settingsStorage.setBoolean(settings_key,valuetoSet);
    }

    public void setCamApi(String api) {
        settingsStorage.setString(SETTING_API,api);
    }

    public String getCamApi() {
        return settingsStorage.getString(SETTING_API, API_1);
    }

    public long getCamera2MaxExposureTime()
    {
        return settingsStorage.getLong("camera2maxexposuretime",0);
    }

    public void setCamera2MaxExposureTime(long max)
    {
        settingsStorage.setLong("camera2maxexposuretime",max);
        Log.d(TAG,"Override max expotime:" +settingsStorage.getLong("camera2maxexposuretime",0));
    }

    public void setCamera2MinExposureTime(long min)
    {
        settingsStorage.setLong("camera2minexposuretime",min);
        Log.d(TAG,"Override min expotime:" +settingsStorage.getLong("camera2minexposuretime",0));
    }
    public long getCamera2MinExposureTime()
    {
        return settingsStorage.getLong("camera2minexposuretime",0);
    }

    public int getCamera2MaxIso()
    {
        return settingsStorage.getInt("camera2maxiso",0);
    }

    public void setCamera2MaxIso(int max)
    {
        settingsStorage.setInt("camera2maxiso",max);
        Log.d(TAG,"Override max iso:" +settingsStorage.getInt("camera2maxiso",0));
    }

    public void setCamera2MinFocusPosition(float pos)
    {
        settingsStorage.setFloat("camera2minfocuspos",pos);
        Log.d(TAG,"Override min focus position:" +settingsStorage.getFloat("camera2minfocuspos",0));
    }

    public float getCamera2MinFocusPosition()
    {
        return settingsStorage.getFloat("camera2minfocuspos",0);
    }

    public void setDevice(String device) {
        this.mDevice = device;
        settingsStorage.setString("DEVICE", mDevice);
    }

    public String getDeviceString() {
        return mDevice;
    }

    public void setshowHelpOverlay(boolean value) {
        settingsStorage.setBoolean("showhelpoverlay", value);
    }

    public boolean getShowHelpOverlay() {
        return settingsStorage.getBoolean("showhelpoverlay", true);
    }

    public void SetBaseFolder(String uri) {
        settingsStorage.setString(SETTING_BASE_FOLDER, uri);
    }

    public String GetBaseFolder() {
        return settingsStorage.getString(SETTING_BASE_FOLDER, null);
    }

    public void SetCurrentCamera(int currentcamera) {
        settingsStorage.setInt(CURRENTCAMERA, currentcamera);
        loadOpCodes();
    }

    public int GetCurrentCamera() {
        return settingsStorage.getInt(CURRENTCAMERA, 0);
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


    public boolean GetWriteExternal() {
        return getApiBoolean(SETTING_EXTERNALSD, false);
    }

    public void SetWriteExternal(boolean write) {
        setApiBoolean(SETTING_EXTERNALSD, write);
    }

    public void setHasCamera2Features(boolean value) {
        settingsStorage.setBoolean(HAS_CAMERA2_FEATURES, value);
    }

    public boolean hasCamera2Features() {
        return settingsStorage.getBoolean(HAS_CAMERA2_FEATURES,false);
    }

    public void setCamerasCount(int count)
    {
        setApiInt("camerascount",count);
    }

    public int getCamerasCount()
    {
        return getApiInt("camerascount");
    }

    @Override
    public String getApiString(String valueToGet) {
        return settingsStorage.getApiString(valueToGet,"");
    }
    @Override
    public int getApiInt(String valueToGet) {
        return settingsStorage.getApiInt(valueToGet,0);
    }

    @Override
    public void setApiInt(String key,int valueToSet) {
        settingsStorage.setApiInt(key,valueToSet);
    }

    @Override
    public void setApiString(String settingsName, String Value) {
        settingsStorage.setApiString(settingsName, Value);
    }

    @Override
    public void setStringArray(String settingsName, String[] Value) {
        settingsStorage.setApiStringArray(settingsName,Value);
    }

    @Override
    public String[] getStringArray(String settingsname)
    {
        return settingsStorage.getApiStringArray(settingsname, null);
    }
    
    public HashMap<String,VideoMediaProfile> getMediaProfiles()
    {
        return settingsStorage.getApiVideoMediaProfiles(videoprofiles,null);
    }

    private final String videoprofiles = "videoProfileshashmap";
    public void saveMediaProfiles(HashMap<String,VideoMediaProfile> mediaProfileHashMap)
    {
        settingsStorage.setApiVideoMediaProfiles(videoprofiles, mediaProfileHashMap);
    }

    public void setFramework(Frameworks frameWork)
    {
        frameworks = frameWork;
        settingsStorage.setString(FRAMEWORK, frameWork.toString());
    }

    public Frameworks getFrameWork()
    {
        return frameworks;
    }


    public static final String FRONTCAMERA ="frontcamera";
    public void setIsFrontCamera(boolean isFront)
    {
        settingsStorage.setApiBoolean(FRONTCAMERA, isFront);
    }

    public boolean getIsFrontCamera()
    {
        return settingsStorage.getApiBoolean(FRONTCAMERA, false);
    }

    public OpCode getOpCode() {
        return opCode;
    }

    ///XML STUFF



}
