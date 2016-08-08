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

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import freed.cam.apis.KEYS;
import freed.utils.DeviceUtils.Devices;


/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager {
    private final String TAG = AppSettingsManager.class.getSimpleName();
    private final String appsettingspath;
    private int currentcamera;
    private String camApiString = AppSettingsManager.API_1;
    private Devices device;


    public static final String SETTING_CURRENTCAMERA = "currentcamera";
    public static final String SETTING_ANTIBANDINGMODE = "antibandingmode";
    public static final String SETTING_COLORMODE = "colormode";
    public static final String SETTING_ISOMODE = "isomode";
    public static final String SETTING_EXPOSUREMODE = "exposuremode";
    public static final String SETTING_WHITEBALANCEMODE = "whitebalancemode";
    public static final String SETTING_IMAGEPOSTPROCESSINGMODE = "ippmode";
    public static final String SETTING_PICTURESIZE = "picturesize";
    public static final String SETTING_PICTUREFORMAT = "pictureformat";
    public static final String SETTING_JPEGQUALITY = "jpegquality";
    //defcomg was here
    //1-29-2016 6:15
    public static final String SETTING_HDRMODE = "hdrmode";
    // 1-29-2016 11:49
    public static final String SETTING_VideoBitrate = "videobitrate";
    public static final String SETTING_HELP = "help";
    //
    public static final String SETTING_GUIDE = "guide";
    //done
    public static final String SETTING_CURRENTMODULE = "currentmodule";
    public static final String SETTING_PREVIEWSIZE = "previewsize";
    public static final String SETTING_PREVIEWFPS = "previewfps";
    public static final String SETTING_PREVIEWFORMAT = "previewformat";
    public static final String SETTING_FLASHMODE = "flashmode";
    public static final String SETTING_SCENEMODE = "scenemode";
    public static final String SETTING_FOCUSMODE = "focusmode";
    public static final String SETTING_REDEYE_MODE = "redeyemode";
    public static final String SETTING_LENSSHADE_MODE = "lenshademode";
    public static final String SETTING_ZEROSHUTTERLAG_MODE = "zslmode";
    public static final String SETTING_SCENEDETECT_MODE = "scenedetectmode";
    public static final String SETTING_DENOISE_MODE = "denoisetmode";
    public static final String SETTING_DIS_MODE = "digitalimagestabmode";
    public static final String SETTING_MCE_MODE = "memorycolorenhancementmode";
    public static final String SETTING_SKINTONE_MODE = "skintonemode";
    public static final String SETTING_NIGHTEMODE = "nightmode";
    public static final String SETTING_NONZSLMANUALMODE = "nonzslmanualmode";
    public static final String SETTING_AEBRACKET = "aebrackethdr";
    public static final String SETTING_EXPOSURELONGTIME = "expolongtime";
    public static final String SETTING_HISTOGRAM = "histogram";
    public static final String SETTING_VIDEOSIZE = "videosize";
    public static final String SETTING_VIDEPROFILE = "videoprofile";
    public static final String SETTING_VIDEOHDR = "videohdr";
    public static final String SETTING_HighFramerateVideo = "highframeratevideo";
    public static final String SETTING_HighSpeedVideo = "highspeedvideo";
    public static final String SETTING_VIDEOSTABILIZATION = "videostabilization";
    ///                  Video Override
    // public static String SETTING_VIDEOHDR = "videohfr";
    // public static String SETTING_VIDEOHDR = "videohsr";

    public static final String SETTTING_BAYERFORMAT = "bayerformat";
    public static final String SETTTING_AE_PRIORITY = "aepriority";
    public static final String SETTTING_CUSTOMMATRIX = "custommatrix";
    ////////// overide end
    public static final String SETTING_VIDEOTIMELAPSEFRAME = "timelapseframe";
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
    public static final String SETTING_CDS = "cds";
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

    public static final String SETTING_OIS = "ois";
    public static final String SETTING_Filter = "filter";

    public static final String API_SONY = "playmemories";
    public static final String API_1 = "camera1";
    public static final String API_2 = "camera2";


    public static final String MWB = "mbw";
    public static final String MCONTRAST = "mcontrast";
    public static final String MCONVERGENCE = "mconvergence";
    public static final String MEXPOSURE = "mexposure";
    public static final String MF = "mf";
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
    public static final String SETTING_STACKMODE = "stackmode";

    public static final String SETTINGS_PREVIEWZOOM = "previewzoom";

    public static final String SETTING_BASE_FOLDER = "base_folder";

    private HashMap<String, String> appsettingsList;

    public AppSettingsManager(Context context) {
        appsettingsList = new HashMap<>();
        appsettingspath = context.getFilesDir().getPath() + File.separator + "setting.txt";
        loadAppSettings();
    }

    public void setCamApi(String api) {
        camApiString = api;
        appsettingsList.put(SETTING_API, api);
    }

    public String getCamApi() {
        camApiString = appsettingsList.get(SETTING_API);
        return camApiString;
    }

    public void SetDevice(Devices device) {
        this.device = device;
        String t = device.name();
        appsettingsList.put("DEVICE", t);
    }

    public Devices getDevice() {
        String t = appsettingsList.get("DEVICE");
        return TextUtils.isEmpty(t) ? null : Devices.valueOf(t);
    }

    public void setshowHelpOverlay(boolean value) {
        appsettingsList.put("showhelpoverlay", Boolean.toString(value));
    }

    public boolean getShowHelpOverlay() {
        String tmp = appsettingsList.get("showhelpoverlay");
        return TextUtils.isEmpty(tmp) || Boolean.valueOf(tmp);
    }

    public void SetBaseFolder(String uri) {
        appsettingsList.put(SETTING_BASE_FOLDER, uri);
    }

    public String GetBaseFolder() {
        return appsettingsList.get(SETTING_BASE_FOLDER);
    }

    public void SetCurrentCamera(int currentcamera) {
        this.currentcamera = currentcamera;
        appsettingsList.put(SETTING_CURRENTCAMERA, currentcamera + "");
    }

    public int GetCurrentCamera() {
        String cam = appsettingsList.get(SETTING_CURRENTCAMERA);
        return TextUtils.isEmpty(cam) ? 0 : Integer.parseInt(cam);
    }

    public void SetCurrentModule(String modulename) {
        appsettingsList.put(getApiSettingString(SETTING_CURRENTMODULE), modulename);
    }

    public String GetCurrentModule() {
        String mod = appsettingsList.get(getApiSettingString(SETTING_CURRENTMODULE));
        if (mod != null && !mod.equals(""))
            return mod;
        return KEYS.MODULE_PICTURE;
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
        appsettingsList.put(CAMERA2FULLSUPPORTED, value);
    }

    public String IsCamera2FullSupported() {
        String t = appsettingsList.get(CAMERA2FULLSUPPORTED);
        return TextUtils.isEmpty(t) ? "" : t;
    }

    private void loadAppSettings() {
        File appsettings = new File(appsettingspath);
        if (appsettings.exists()) {
            BufferedReader br = null;

            try {
                br = new BufferedReader(new FileReader(appsettings));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("=");
                    appsettingsList.put(split[0], split[1]);
                }
            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                appsettingsList = new HashMap<>();
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String t = appsettingsList.get("DEVICE");
        if (!TextUtils.isEmpty(t))
            device = Devices.valueOf(t);
    }

    public void SaveAppSettings() {
        File mprof = new File(appsettingspath);

        if (!mprof.exists()) {
            try {
                mprof.createNewFile();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }

        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(mprof));

            for (Entry<String, String> entry : appsettingsList.entrySet()) {
                br.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            Logger.exception(e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                Logger.exception(e);
            }
        }

    }

    public String getString(String valueToGet, String defaultValue) {
        String ret = appsettingsList.get(getApiSettingString(valueToGet));
        return TextUtils.isEmpty(ret) ? defaultValue : appsettingsList.get(ret);
    }

    public String getString(String valueToGet) {
        String ret = appsettingsList.get(getApiSettingString(valueToGet));
        return TextUtils.isEmpty(ret) ? "" : ret;
    }

    public void setString(String settingsName, String Value) {
        appsettingsList.put(getApiSettingString(settingsName), Value);
    }

    public boolean getBoolean(String valueToGet, boolean defaultValue) {
        String tmp = appsettingsList.get(getApiSettingString(valueToGet));
        return TextUtils.isEmpty(tmp) ? defaultValue : Boolean.parseBoolean(tmp);
    }

    public void setBoolean(String valueToSet, boolean defaultValue) {
        setString(valueToSet, defaultValue + "");
    }
}
