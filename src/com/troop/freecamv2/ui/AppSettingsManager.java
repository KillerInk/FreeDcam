package com.troop.freecamv2.ui;

import android.content.SharedPreferences;

import com.troop.freecamv2.camera.modules.ModuleHandler;

/**
 * Created by troop on 19.08.2014.
 */
public class AppSettingsManager
{
    SharedPreferences appSettings;
    private int currentcamera = 0;

    public static String SETTING_CURRENTCAMERA = "currentcamera";
    public static String SETTING_ANTIBANDINGMODE = "antibandingmode";
    public static String SETTING_COLORMODE = "colormode";
    public static String SETTING_ISOMODE = "isomode";
    public static String SETTING_EXPOSUREMODE = "exposuremode";
    public static String SETTING_IMAGEPOSTPROCESSINGMODE = "ippmode";
    public static String SETTING_PICTURESIZE = "picturesize";
    public static String SETTING_CURRENTMODULE = "currentmodule";
    public static String SETTING_PREVIEWSIZE = "previewsize";

    public AppSettingsManager(SharedPreferences appSettings)
    {
        this.appSettings = appSettings;
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
        String newstring = valueToGet + currentcamera;
        return appSettings.getString(newstring, "");
    }

    public void setString(String valueToSet, String Value)
    {
        String newstring = valueToSet + currentcamera;
        appSettings.edit().putString(newstring, Value).commit();
    }
}
