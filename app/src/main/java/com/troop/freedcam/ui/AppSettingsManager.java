package com.troop.freedcam.ui;

import android.content.SharedPreferences;
import android.os.Build;

import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;

/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager
{
    SharedPreferences appSettings;
    public MainActivity_v2 context;
    private int currentcamera = 0;
    boolean sonyCam = false;

    public static String SETTING_CURRENTCAMERA = "currentcamera";
    public static String SETTING_ANTIBANDINGMODE = "antibandingmode";
    public static String SETTING_COLORMODE = "colormode";
    public static String SETTING_ISOMODE = "isomode";
    public static String SETTING_EXPOSUREMODE = "exposuremode";
    public static String SETTING_WHITEBALANCEMODE = "whitebalancemode";
    public static String SETTING_IMAGEPOSTPROCESSINGMODE = "ippmode";
    public static String SETTING_PICTURESIZE = "picturesize";
    public static String SETTING_PICTUREFORMAT = "pictureformat";
    public static String SETTING_JPEGQUALITY = "jpegquality";
    public static String SETTING_CURRENTMODULE = "currentmodule";
    public static String SETTING_PREVIEWSIZE = "previewsize";
    public static String SETTING_PREVIEWFPS = "previewfps";
    public static String SETTING_PREVIEWFORMAT = "previewformat";
    public static String SETTING_FLASHMODE = "flashmode";
    public static String SETTING_SCENEMODE = "scenemode";
    public static String SETTING_FOCUSMODE = "focusmode";
    public static String SETTING_REDEYE_MODE = "redeyemode";
    public static String SETTING_LENSSHADE_MODE = "lenshademode";
    public static String SETTING_ZEROSHUTTERLAG_MODE = "zslmode";
    public static String SETTING_SCENEDETECT_MODE = "scenedetectmode";
    public static String SETTING_DENOISE_MODE = "denoisetmode";
    public static String SETTING_DIS_MODE = "digitalimagestabmode";
    public static String SETTING_MCE_MODE = "memorycolorenhancementmode";
    public static String SETTING_SKINTONE_MODE = "skintonemode";
    public static String SETTING_NIGHTEMODE = "nightmode";
    public static String SETTING_NONZSLMANUALMODE = "nonzslmanualmode";
    public static String SETTING_AEBRACKET = "aebrackethdr";
    public static String SETTING_EXPOSURELONGTIME = "expolongtime";
    public static String SETTING_HISTOGRAM = "histogram";
    public static String SETTING_VIDEOSIZE = "videosize";
    public static String SETTING_VIDEPROFILE = "videoprofile";
    public static String SETTING_VIDEOHDR = "videohdr";
    public static String SETTING_VIDEOTIMELAPSEFRAME = "timelapseframe";
    public static String SETTING_SONYAPI = "sonyapi";


    public AppSettingsManager(SharedPreferences appSettings, MainActivity_v2 context)
    {
        this.appSettings = appSettings;
        this.context = context;
    }

    public void setSonyCam(boolean isSony)
    {
        sonyCam = isSony;
        appSettings.edit().putBoolean(SETTING_SONYAPI, isSony).commit();
    }

    public boolean getSonyCam()
    {
        sonyCam = appSettings.getBoolean(SETTING_SONYAPI, false);
        return sonyCam;
    }

    public void setshowHelpOverlay(boolean value)
    {
        appSettings.edit().putBoolean("showhelpoverlay", value).commit();
    }

    public boolean getShowHelpOverlay()
    {
        return appSettings.getBoolean("showhelpoverlay", true);
    }


    public void SetCurrentCamera(int currentcamera)
    {
        this.currentcamera = currentcamera;
        appSettings.edit().putInt(SETTING_CURRENTCAMERA, currentcamera).commit();
    }

    public int GetCurrentCamera()
    {
        currentcamera = appSettings.getInt(SETTING_CURRENTCAMERA, 0);
        return currentcamera;
    }

    public void SetCurrentModule(String modulename)
    {
        appSettings.edit().putString(SETTING_CURRENTMODULE, modulename).commit();
    }

    public String GetCurrentModule()
    {
        return appSettings.getString(SETTING_CURRENTMODULE, ModuleHandler.MODULE_PICTURE);
    }

    public String getString(String valueToGet)
    {
        String newstring;
        if (sonyCam)
            newstring = valueToGet + "sony";
        else /*if(Build.VERSION.SDK_INT < 21)*/
            newstring = valueToGet + currentcamera;
        /*else
            newstring = valueToGet + currentcamera + "api2";*/
        return appSettings.getString(newstring, "");
    }

    public void setString(String valueToSet, String Value)
    {
        String newstring;
        if (sonyCam)
            newstring = valueToSet + "sony";
        else /*if(Build.VERSION.SDK_INT < 21)*/
            newstring = valueToSet + currentcamera;
        /*else
            newstring = valueToSet + currentcamera + "api2";*/
        appSettings.edit().putString(newstring, Value).commit();
    }
}
