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

import android.text.TextUtils;

import androidx.collection.LongSparseArray;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freed.FreedApplication;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.dng.ToneMapProfile;
import freed.jni.OpCode;
import freed.utils.Log;
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
    public static final String SETTING_LOCATION = "location";
    public static final String SETTING_FOCUSPEAK = "focuspeak";
    public static final String SETTING_EXTERNALSD = "extSD";
    public static final String API_SONY = "playmemories";
    public static final String API_1 = "camera1";
    public static final String API_2 = "camera2";

    public List<OpCodeUrl> opcodeUrlList;

    private final String TAG = SettingsManager.class.getSimpleName();

    private HashMap<String, CustomMatrix> matrixes;
    private HashMap<String, ToneMapProfile> tonemapProfiles;
    private HashMap<String, VideoToneCurveProfile> videoToneCurveProfiles;
    private LongSparseArray<DngProfile> dngProfileHashMap;
    private OpCode opCode;
    private static volatile boolean isInit =false;
    private SettingsStorage settingsStorage;

    private static SettingsManager settingsManager = new SettingsManager();



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
        new XmlParserWriter().saveToneCurveProfiles(videoToneCurveProfiles, getAppDataFolder());
    }

    public static <T> T get(SettingKeys.Key<T> key)
    {
        return key.getType().cast(getInstance().settingsStorage.get(key));
    }

    public static <T> T getGlobal(SettingKeys.Key<T> key)
    {
        return key.getType().cast(getInstance().settingsStorage.getGlobal(key));
    }

    public static <T> T getApi(SettingKeys.Key<T> key)
    {
        return key.getType().cast(getInstance().settingsStorage.getApiSetting(key));
    }

    public void init()
    {
        //check if its not already init while a other task waited for it
        if (isInit)
            return;
        isInit = true;
        File ext = FreedApplication.getContext().getExternalFilesDir(null);
        if (ext != null && ext.exists())
            settingsStorage = new SettingsStorage(ext);
        else
            settingsStorage = new SettingsStorage(FreedApplication.getContext().getFilesDir());

        Log.d(TAG, "load Settings");
        settingsStorage.load();
        videoToneCurveProfiles = new XmlParserWriter().getToneCurveProfiles(getAppDataFolder());
        //loadOpCodes();
        parseXml();
    }

    public void release()
    {
        //settingsmap.clear();
        isInit = false;
        settingsStorage.reset();
    }

    public boolean isInit()
    {
        return isInit;
    }

    public File getAppDataFolder()
    {
        return  settingsStorage.appdataFolder;
    }

    private void parseXml() {
        XmlParserWriter parser = new XmlParserWriter();
        //first time init
        matrixes = parser.getMatrixes(FreedApplication.getContext().getResources(),settingsStorage.appdataFolder);

        tonemapProfiles = parser.getToneMapProfiles(settingsStorage.appdataFolder);
        videoToneCurveProfiles = parser.getToneCurveProfiles(settingsStorage.appdataFolder);
        if (settingsStorage.getDevice() == null || TextUtils.isEmpty(settingsStorage.getDevice()))
        {
            Log.d(TAG, "Lookup PreDefinedConfigFile");
            parser.parseAndFindSupportedDevice(FreedApplication.getContext().getResources(),matrixes,settingsStorage.appdataFolder);
            Log.d(TAG, "Lookup PreDefinedConfigFile done");
        }
        dngProfileHashMap = parser.getDngProfiles(matrixes,settingsStorage.appdataFolder);
        opcodeUrlList = new ArrayList<>();
        loadOpCodes();
    }

    private void loadOpCodes()
    {
        new Thread(() -> {
            try {
                File op2 = new File(settingsStorage.appdataFolder.getAbsolutePath()+"/"+settingsStorage.getActiveCamera()+"opc2.bin");
                File op3 = new File(settingsStorage.appdataFolder.getAbsolutePath()+"/"+settingsStorage.getActiveCamera()+"opc3.bin");
                if (op2.exists() || op3.exists())
                    opCode = new OpCode(op2,op3);
                else
                    opCode = null;
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }



        }).start();
    }

    public synchronized void RESET()
    {
        settingsStorage.reset();
        //settings.edit().clear().commit();
        parseXml();
        settingsStorage.save();
    }

    public boolean appVersionHasChanged()
    {
        return BuildConfig.VERSION_CODE != getAppVersion();
    }

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
        return settingsStorage.isZteAE();
    }

    public void setZteAe(boolean legacy)
    {
        settingsStorage.setIsZteAE(legacy);
    }


    public void setsOverrideDngProfile(boolean legacy)
    {
        settingsStorage.setOverrideDngProfile(legacy);
    }

    public boolean getOverrideDngProfile()
    {
        return settingsStorage.overrideDngProfile();
    }

    public int getAppVersion()
    {
        return settingsStorage.getAppVersion();
    }

    public void setAppVersion(int version)
    {
        settingsStorage.setAppVersion(version);
    }

    public void setCamApi(String api) {
        settingsStorage.setApi(api);
    }

    public String getCamApi() {
        return settingsStorage.getApi();
    }

    public long getCamera2MaxExposureTime()
    {
        return settingsStorage.getCameraMaxExposureTime();
    }

    public void setCamera2MaxExposureTime(long max)
    {
        settingsStorage.setCameraMaxExposureTime(max);
    }

    public void setCamera2MinExposureTime(long min)
    {
        settingsStorage.setCameraMinExposureTime(min);
    }
    public long getCamera2MinExposureTime()
    {
        return settingsStorage.getCameraMinExposureTime();
    }

    public int getCamera2MaxIso()
    {
        return settingsStorage.getCameraMaxIso();
    }

    public void setCamera2MaxIso(int max)
    {
        settingsStorage.setCameraMaxIso(max);
    }

    public void setCamera2MinFocusPosition(float pos)
    {
        settingsStorage.setCameraMinFocus(pos);
    }

    public float getCamera2MinFocusPosition()
    {
        return settingsStorage.getCameraMinFocus();
    }

    public void setDevice(String device) {
        settingsStorage.setDevice(device);
    }

    public String getDeviceString() {
        return settingsStorage.getDevice();
    }

    public void setshowHelpOverlay(boolean value) {
        settingsStorage.setShowHelpOverlayOnStart(value);
    }

    public boolean getShowHelpOverlay() {
        return settingsStorage.showHelpOverlayOnStart();
    }

    public void SetBaseFolder(String uri) {
        settingsStorage.setExtSDFolderUri(uri);
    }

    public String GetBaseFolder() {
        return settingsStorage.getExtSDFolderUri();
    }

    public void SetCurrentCamera(int currentcamera) {
        settingsStorage.setActiveCamera(currentcamera);
        loadOpCodes();
    }

    public int GetCurrentCamera() {
        return settingsStorage.getActiveCamera();
    }

    public void setCameraIds(int[] cameras)
    {
        Log.d(TAG, "set camera ids");
        settingsStorage.setActiveCameraIds(cameras);
    }

    public int[] getCameraIds()
    {
        return settingsStorage.getActiveCameraIds();
    }

    public void SetCurrentModule(String modulename) {
        getApi(SettingKeys.Module).set(modulename);
    }

    public String GetCurrentModule()
    {
        if (TextUtils.isEmpty(getApi(SettingKeys.Module).get()))
            return FreedApplication.getStringFromRessources(R.string.module_picture);
        return getApi(SettingKeys.Module).get();
    }


    public boolean GetWriteExternal() {
        return settingsStorage.writeToExternalSD();
    }

    public void SetWriteExternal(boolean write) {
        settingsStorage.setWriteToExternalSD(write);
    }

    public void setHasCamera2Features(boolean value) {
        settingsStorage.setHasCamera2Features(value);
    }

    public boolean hasCamera2Features() {
        return settingsStorage.hasCamera2Features();
    }

    public HashMap<String,VideoMediaProfile> getMediaProfiles()
    {
        return settingsStorage.getApiVideoMediaProfiles();
    }

    public void saveMediaProfiles(HashMap<String,VideoMediaProfile> mediaProfileHashMap)
    {
        settingsStorage.setApiVideoMediaProfiles(mediaProfileHashMap);
    }

    public void setFramework(Frameworks frameWork)
    {
        settingsStorage.setFramework(frameWork);
    }

    public Frameworks getFrameWork()
    {
        return settingsStorage.getFramework();
    }

    public void setIsFrontCamera(boolean isFront)
    {
        settingsStorage.setIsFrontCamera(isFront);
    }

    public boolean getIsFrontCamera()
    {
        return settingsStorage.isFrontCamera();
    }

    public void setAreFeaturesDetected(boolean isFront)
    {
        settingsStorage.setFeaturesAreDetected(isFront);
    }

    public boolean getAreFeaturesDetected()
    {
        return settingsStorage.areFeaturesDetected();
    }

    public boolean getCamIsFrontCamera(int id)
    {
        return settingsStorage.isFrontCamera(id);
    }

    public OpCode getOpCode() {
        return opCode;
    }

    ///XML STUFF



}
