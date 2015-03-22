package com.troop.freedcam.i_camera.modules;

import android.util.Log;

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
    public interface I_worker
    {
        void onWorkStarted();
        void onWorkFinished(boolean finished);
    }

    private static String TAG = AbstractModuleHandler.class.getSimpleName();
    public ModuleEventHandler moduleEventHandler;
    public ArrayList<String> PictureModules;
    public ArrayList<String> LongeExpoModules;
    public ArrayList<String> VideoModules;
    public ArrayList<String> AllModules;
    public ArrayList<String> HDRModule;
    public HashMap<String, AbstractModule> moduleList;
    protected AppSettingsManager appSettingsManager;
    protected AbstractModule currentModule;
    AbstractCameraHolder cameraHolder;

    protected I_worker workerListner;

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

        HDRModule = new ArrayList<String>();
        HDRModule.add(MODULE_HDR);

    }

    @Override
    public void SetModule(String name)
    {
        if (currentModule !=null) {
            currentModule.UnloadNeededParameters();
            currentModule.SetWorkerListner(null);

        }
        currentModule = moduleList.get(name);
        currentModule.LoadNeededParameters();
        moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
        currentModule.SetWorkerListner(workerListner);
        Log.d(TAG, "Set Module to " + name);
    }

    @Override
    public String GetCurrentModuleName() {
        if (currentModule != null)
            return currentModule.name;
        else return AbstractModuleHandler.MODULE_PICTURE;
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

    @Override
    public void SetWorkListner(I_worker workerListner) {
        this.workerListner = workerListner;
    }

    protected void initModules()
    {

    }
}
