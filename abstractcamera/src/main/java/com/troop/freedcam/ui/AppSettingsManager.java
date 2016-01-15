package com.troop.freedcam.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;



/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager
{
    SharedPreferences appSettings;
    public Context context;
    private int currentcamera = 0;
    String camApiString = API_1;

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
    final public static String SETTING_CHROMAFLASH_MODE = "chromaflashmode";
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


    ////////// overide end
    final public static String SETTING_VIDEOTIMELAPSEFRAME = "timelapseframe";
    final public static String SETTING_SONYAPI = "sonyapi";
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

    final public static String APPVERSION = "appversion";

    final public static String CAMERA2FULLSUPPORTED = "camera2fullsupport";

    final public static String SETTING_HORIZONT = "horizont";


    public AppSettingsManager(SharedPreferences appSettings, Context context)
    {
        this.appSettings = appSettings;
        try {
            String appver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (!appSettings.getString(APPVERSION, "").equals(appver))
            {
                appSettings.edit().clear().commit();
                appSettings.edit().putString(APPVERSION, appver).commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.context = context;
    }

    public void setCamApi(String api)
    {
        camApiString = api;
        appSettings.edit().putString(SETTING_SONYAPI, api).apply();
    }

    public String getCamApi()
    {
        camApiString = appSettings.getString(SETTING_SONYAPI, API_1);
        return camApiString;
    }

    public void setshowHelpOverlay(boolean value)
    {
        appSettings.edit().putBoolean("showhelpoverlay", value).apply();
    }

    public boolean getShowHelpOverlay()
    {
        return appSettings.getBoolean("showhelpoverlay", true);
    }

    public void SetTheme(String theme)
    {
        appSettings.edit().putString(AppSettingsManager.SETTING_Theme, theme).apply();
    }

    public String GetTheme()
    {
        return appSettings.getString(AppSettingsManager.SETTING_Theme, "Classic");
    }


    public void SetCurrentCamera(int currentcamera)
    {
        this.currentcamera = currentcamera;
        appSettings.edit().putInt(SETTING_CURRENTCAMERA, currentcamera).apply();
    }

    public int GetCurrentCamera()
    {
        currentcamera = appSettings.getInt(SETTING_CURRENTCAMERA, 0);
        return currentcamera;
    }

    public void SetCurrentModule(String modulename)
    {
        String newstring;
        if (API_SONY.equals(camApiString))
            newstring = SETTING_CURRENTMODULE + API_SONY;
        else if(API_1.equals(camApiString))
            newstring = SETTING_CURRENTMODULE;
        else
            newstring = SETTING_CURRENTMODULE + API_2;
        appSettings.edit().putString(newstring, modulename).apply();
    }

    public String GetCurrentModule()
    {
        String newstring;
        if (API_SONY.equals(camApiString))
            newstring = SETTING_CURRENTMODULE + API_SONY;
        else if(API_1.equals(camApiString))
            newstring = SETTING_CURRENTMODULE;
        else
            newstring = SETTING_CURRENTMODULE + API_2;
        return appSettings.getString(newstring, AbstractModuleHandler.MODULE_PICTURE);
    }

    public String getString(String valueToGet)
    {
        String newstring;
        if (API_SONY.equals(camApiString))
            newstring = valueToGet + API_SONY;
        else if(API_1.equals(camApiString))
            newstring = valueToGet + currentcamera;
        else
            newstring = valueToGet + currentcamera + API_2;
        return appSettings.getString(newstring, "");
    }

    public void setString(String settingsName, String Value)
    {
        String newstring;
        if (API_SONY.equals(camApiString))
            newstring = settingsName + API_SONY;
        else if(API_1.equals(camApiString))
            newstring = settingsName + currentcamera;
        else
            newstring = settingsName + currentcamera + API_2;
        appSettings.edit().putString(newstring, Value).apply();
    }

    public boolean GetWriteExternal()
    {
        return appSettings.getBoolean(AppSettingsManager.SETTING_EXTERNALSD, false);
    }

    public void SetWriteExternal(boolean write)
    {
        appSettings.edit().putBoolean(AppSettingsManager.SETTING_EXTERNALSD, write).apply();
    }

    public void SetCamera2FullSupported(String value)
    {
        appSettings.edit().putString(AppSettingsManager.CAMERA2FULLSUPPORTED, value).apply();
    }

    public String IsCamera2FullSupported()
    {
        return appSettings.getString(AppSettingsManager.CAMERA2FULLSUPPORTED, "");
    }
}
