package com.freedcam.utils;

import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager
{
    final private String TAG = AppSettingsManager.class.getSimpleName();
    final String appsettingspath = StringUtils.GetFreeDcamConfigFolder+"setting.txt";
    /*SharedPreferences appSettings;
    public Context context;*/
    private int currentcamera = 0;
    private String camApiString = API_1;


    final public static String SETTING_CURRENTCAMERA = "currentcamera";
    final public static String SETTING_ANTIBANDINGMODE = "antibandingmode";
    final public static String SETTING_COLORMODE = "colormode";
    final public static String SETTING_ISOMODE = "isomode";
    final public static String SETTING_EXPOSUREMODE = "exposuremode";
    final public static String SETTING_WHITEBALANCEMODE = "whitebalancemode";
    final public static String SETTING_IMAGEPOSTPROCESSINGMODE = "ippmode";
    final public static String SETTING_PICTURESIZE = "picturesize";
    final public static String SETTING_PICTUREFORMAT = "pictureformat";
    final public static String SETTING_JPEGQUALITY = "jpegquality";
    //defcomg was here
    //1-29-2016 6:15
    final public static String SETTING_HDRMODE = "hdrmode";
    // 1-29-2016 11:49
    final public static String SETTING_VideoBitrate = "videobitrate";
    final public static String SETTING_HELP ="help";
    //
    final public static String SETTING_GUIDE = "guide";
    //done
    final public static String SETTING_CURRENTMODULE = "currentmodule";
    final public static String SETTING_PREVIEWSIZE = "previewsize";
    final public static String SETTING_PREVIEWFPS = "previewfps";
    final public static String SETTING_PREVIEWFORMAT = "previewformat";
    final public static String SETTING_FLASHMODE = "flashmode";
    final public static String SETTING_SCENEMODE = "scenemode";
    final public static String SETTING_FOCUSMODE = "focusmode";
    final public static String SETTING_REDEYE_MODE = "redeyemode";
    final public static String SETTING_LENSSHADE_MODE = "lenshademode";
    final public static String SETTING_ZEROSHUTTERLAG_MODE = "zslmode";
    final public static String SETTING_SCENEDETECT_MODE = "scenedetectmode";
    final public static String SETTING_DENOISE_MODE = "denoisetmode";
    final public static String SETTING_DIS_MODE = "digitalimagestabmode";
    final public static String SETTING_MCE_MODE = "memorycolorenhancementmode";
    final public static String SETTING_SKINTONE_MODE = "skintonemode";
    final public static String SETTING_NIGHTEMODE = "nightmode";
    final public static String SETTING_NONZSLMANUALMODE = "nonzslmanualmode";
    final public static String SETTING_AEBRACKET = "aebrackethdr";
    final public static String SETTING_EXPOSURELONGTIME = "expolongtime";
    final public static String SETTING_HISTOGRAM = "histogram";
    final public static String SETTING_VIDEOSIZE = "videosize";
    final public static String SETTING_VIDEPROFILE = "videoprofile";
    final public static String SETTING_VIDEOHDR = "videohdr";
    final public static String SETTING_HighFramerateVideo = "highframeratevideo";
    final public static String SETTING_HighSpeedVideo = "highspeedvideo";
    final public static String SETTING_VIDEOSTABILIZATION = "videostabilization";
    ///                  Video Override
   // public static String SETTING_VIDEOHDR = "videohfr";
   // public static String SETTING_VIDEOHDR = "videohsr";

    final public static String SETTTING_BAYERFORMAT ="bayerformat";
    final public static String SETTTING_AE_PRIORITY ="aepriority";
    final public static String SETTTING_CUSTOMMATRIX ="custommatrix";
    ////////// overide end
    final public static String SETTING_VIDEOTIMELAPSEFRAME = "timelapseframe";
    final public static String SETTING_API = "sonyapi";
    final public static String SETTING_DNG = "dng";
    final public static String SETTING_AEBRACKETACTIVE = "aebracketactive";
    final public static String SETTING_OBJECTTRACKING = "objecttracking";
    final public static String SETTING_LOCATION = "location";
    final public static String SETTING_EXTERNALSHUTTER = "externalShutter";
    final public static String SETTING_OrientationHack = "orientationHack";

    final public static String SETTING_INTERVAL = "innterval";
    final public static String SETTING_INTERVAL_DURATION = "interval_duration";
    final public static String SETTING_TIMER = "timer";

    final public static String SETTING_CAMERAMODE = "camMode";
    final public static String SETTING_DUALMODE = "dualMode";
    final public static String SETTING_Theme = "theme";
    final public static String SETTING_CDS = "cds";
    final public static String SETTING_SECUREMODE = "securemode";
    final public static String SETTING_TNR = "tnr";
    final public static String SETTING_RDI = "rdi";
    final public static String SETTING_EDGE = "edge";
    final public static String SETTING_COLORCORRECTION = "colorcorrection";
    final public static String SETTING_HOTPIXEL = "hotpixel";
    final public static String SETTING_TONEMAP = "tonemap";
    final public static String SETTING_CONTROLMODE = "controlmode";
    final public static String SETTING_FOCUSPEAK = "focuspeak";

    final public static String SETTING_EXTERNALSD = "extSD";

    final public static String SETTING_OIS = "ois";
    final public static String SETTING_Filter = "filter";

    final public static String API_SONY = "playmemories";
    final public static String API_1 = "camera1";
    final public static String API_2 = "camera2";


    final public static String MWB = "mbw";
    final public static String MCONTRAST = "mcontrast";
    final public static String MCONVERGENCE = "mconvergence";
    final public static String MEXPOSURE = "mexposure";
    final public static String MF = "mf";
    final public static String MSHARPNESS = "msharpness";
    final public static String MSHUTTERSPEED = "mshutterspeed";
    final public static String MBRIGHTNESS = "mbrightness";
    final public static String MISO = "miso";
    final public static String MSATURATION = "msaturation";
    final public static String MCCT = "mcct";
    final public static String MBURST = "mburst";

    final public static String APPVERSION = "appversion";

    final public static String CAMERA2FULLSUPPORTED = "camera2fullsupport";

    final public static String SETTING_HORIZONT = "horizont";

    final public static String SETTING_CAPTUREBURSTEXPOSURES = "captureburstexposures";
    final public static String SETTING_MORPHOHDR = "morphohdr";
    final public static String SETTING_MORPHOHHT = "morphohht";
    final public static String SETTING_AEB1= "aeb1";
    final public static String SETTING_AEB2= "aeb2";
    final public static String SETTING_AEB3= "aeb3";
    final public static String SETTING_STACKMODE = "stackmode";

    final public static String SETTINGS_PREVIEWZOOM = "previewzoom";

    final public static String SETTING_BASE_FOLDER = "base_folder";

    private HashMap<String,String> appsettingsList = new HashMap<>();

    public AppSettingsManager()
    {
        loadAppSettings();
    }

    public void setCamApi(String api)
    {
        camApiString = api;
        appsettingsList.put(SETTING_API,api);
    }

    public String getCamApi()
    {
        camApiString = appsettingsList.get(SETTING_API);
        return camApiString;
    }

    public void setshowHelpOverlay(boolean value)
    {
        appsettingsList.put("showhelpoverlay",value+"");
    }

    public boolean getShowHelpOverlay() {
        String tmp = appsettingsList.get("showhelpoverlay");
        return !(tmp != null && !tmp.equals("")) || Boolean.parseBoolean(tmp);
    }

    public void SetBaseFolder(String uri)
    {
        appsettingsList.put(SETTING_BASE_FOLDER,uri);
    }

    public String GetBaseFolder() {
        return getString(SETTING_BASE_FOLDER,"");
    }

    public void SetTheme(String theme)
    {
        appsettingsList.put(SETTING_Theme,theme);
    }

    public String GetTheme()
    {
        String t = appsettingsList.get(SETTING_Theme);
        if (t == null)
            t = "Sample";
        return t;
    }


    public void SetCurrentCamera(int currentcamera)
    {
        this.currentcamera = currentcamera;
        appsettingsList.put(SETTING_CURRENTCAMERA, currentcamera+"");
    }

    public int GetCurrentCamera()
    {
        final String cam = appsettingsList.get(SETTING_CURRENTCAMERA);
        if (cam == null || cam.equals(""))
            return 0;
        return Integer.parseInt(cam);
    }

    public void SetCurrentModule(String modulename)
    {
        appsettingsList.put(getApiSettingString(SETTING_CURRENTMODULE), modulename);
    }

    public String GetCurrentModule()
    {
        final String mod = appsettingsList.get(getApiSettingString(SETTING_CURRENTMODULE));
        if (mod != null && !mod.equals(""))
            return mod;
        return AbstractModuleHandler.MODULE_PICTURE;
    }

    private String getApiSettingString(String settingsName)
    {
        final StringBuilder newstring = new StringBuilder();
        if (API_SONY.equals(camApiString))
            newstring.append(API_SONY).append(settingsName);
        else if(API_1.equals(camApiString))
            newstring.append(API_1).append(settingsName).append(currentcamera);
        else
            newstring.append(API_2).append(settingsName).append(currentcamera);
        return newstring.toString();
    }



    public boolean GetWriteExternal()
    {
        return getBoolean(SETTING_EXTERNALSD, false);
    }

    public void SetWriteExternal(boolean write)
    {
        setBoolean(SETTING_EXTERNALSD,write);

    }

    public void SetCamera2FullSupported(String value)
    {
        appsettingsList.put(CAMERA2FULLSUPPORTED, value);
    }

    public String IsCamera2FullSupported()
    {
        String t = appsettingsList.get(CAMERA2FULLSUPPORTED);
        if (t != null)
            return t;
        return "";
    }

    private void loadAppSettings()
    {
        File appsettings = new File(appsettingspath);
        if (appsettings.exists())
        {
            BufferedReader br = null;

            try {
                br = new BufferedReader(new FileReader(appsettings));
                String line;
                while ((line = br.readLine()) != null)
                {
                    String[]split = line.split("=");
                    appsettingsList.put(split[0],split[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void SaveAppSettings()
    {
        File mprof = new File(appsettingspath);

        if(!mprof.exists()) {
            try {
                mprof.createNewFile();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }

        BufferedWriter br = null;
        try
        {
            br = new BufferedWriter(new FileWriter(mprof));

            for (Map.Entry<String,String> entry : appsettingsList.entrySet()) {
                br.write(entry.getKey() + "=" + entry.getValue()+"\n");
            }
        } catch (IOException e)
        {
            Logger.exception(e);
        }
        finally
        {
            try {
                br.close();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }

    }

    public String getString(String valueToGet, String defaultValue)
    {
        String ret = appsettingsList.get(getApiSettingString(valueToGet));
        if (ret!=null && !ret.equals(""))
            return appsettingsList.get(ret);
        else return defaultValue;
    }

    public String getString(String valueToGet)
    {
        String ret = appsettingsList.get(getApiSettingString(valueToGet));
        if (ret!=null && !ret.equals(""))
            return ret;
        else return "";
    }

    public void setString(String settingsName, String Value)
    {
        appsettingsList.put(getApiSettingString(settingsName),Value);
    }

    public boolean getBoolean(String valueToGet, boolean defaultValue)
    {
        String tmp = appsettingsList.get(getApiSettingString(valueToGet));
        if (tmp!=null && !tmp.equals(""))
            return Boolean.parseBoolean(tmp);
        else return defaultValue;
    }

    public void setBoolean(String valueToSet, boolean defaultValue)
    {
        setString(valueToSet,defaultValue+"");
    }
}
