package com.troop.freedcam.i_camera.modules;

import android.util.Log;

import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_ModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractModuleHandler implements I_ModuleHandler
{
    String TAG = AbstractModuleHandler.class.getSimpleName();
    public ModuleEventHandler moduleEventHandler;
    public ArrayList<String> PictureModules;
    public ArrayList<String> LongeExpoModules;
    public ArrayList<String> VideoModules;
    public ArrayList<String> AllModules;
    public HashMap<String, AbstractModule> moduleList;
    protected AppSettingsManager appSettingsManager;
    protected AbstractModule currentModule;
    AbstractCameraHolder cameraHolder;

    public static final String MODULE_VIDEO = "module_video";
    public static final String MODULE_PICTURE = "module_picture";
    public static final String MODULE_HDR = "module_hdr";
    public static final String MODULE_BURST = "module_burst";
    public static final String MODULE_LONGEXPO = "module_longexposure";
    public static final String MODULE_ALL = "module_all";

    public AbstractModuleHandler(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = cameraHolder;
        this.appSettingsManager = appSettingsManager;
        moduleList  = new HashMap<String, AbstractModule>();
        moduleEventHandler = new ModuleEventHandler();
        PictureModules = new ArrayList<String>();
        PictureModules.add(MODULE_PICTURE);
        PictureModules.add(MODULE_BURST);
        PictureModules.add(MODULE_HDR);
        //PictureModules.add();
        VideoModules = new ArrayList<String>();
        VideoModules.add(MODULE_VIDEO);
        AllModules = new ArrayList<String>();
        AllModules.add(MODULE_ALL);
        LongeExpoModules = new ArrayList<String>();
        LongeExpoModules.add(MODULE_LONGEXPO);

    }

    @Override
    public void SetModule(String name)
    {
        if (currentModule !=null)
            currentModule.UnloadNeededParameters();
        currentModule = moduleList.get(name);
        currentModule.LoadNeededParameters();
        moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
        Log.d(TAG, "Set Module to " + name);
    }

    @Override
    public String GetCurrentModuleName() {
        if (currentModule != null)
            return currentModule.name;
        else return "";
    }

    @Override
    public AbstractModule GetCurrentModule() {
        if (currentModule != null)
            return currentModule;
        return null;
    }

    @Override
    public boolean DoWork() {
        if (currentModule != null) {
            currentModule.DoWork();
            return true;
        }
        else
            return false;
    }

    protected void initModules()
    {

    }
}
