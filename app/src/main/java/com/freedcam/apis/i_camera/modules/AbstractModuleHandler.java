package com.freedcam.apis.i_camera.modules;

import android.content.Context;

import com.freedcam.apis.i_camera.AbstractCameraHolder;
import com.freedcam.apis.i_camera.interfaces.I_ModuleHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

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

    ArrayList<I_worker> workers;

    private static String TAG = AbstractModuleHandler.class.getSimpleName();
    public ModuleEventHandler moduleEventHandler;
    public ArrayList<String> PictureModules;
    public ArrayList<String> LongeExpoModules;
    public ArrayList<String> VideoModules;
    public ArrayList<String> AllModules;
    public ArrayList<String> HDRModule;
    public HashMap<String, AbstractModule> moduleList;
    protected AbstractModule currentModule;
    AbstractCameraHolder cameraHolder;

    protected I_worker workerListner;

    public static final String MODULE_VIDEO = "module_video";
    public static final String MODULE_PICTURE = "module_picture";
    public static final String MODULE_HDR = "module_hdr";
    public static final String MODULE_BURST = "module_burst";
    public static final String MODULE_LONGEXPO = "module_longexposure";
    public static final String MODULE_STACKING = "module_stacking";
    public static final String MODULE_FILM_SNAPSHOT = "module_film_snapshot";
    public static final String MODULE_FILM_VIDEO = "module_film_snapshot";
    public static final String MODULE_INTERVAL = "module_interval";
    public static final String MODULE_ALL = "module_all";

    protected Context context;
    protected AppSettingsManager appSettingsManager;

    public AbstractModuleHandler(AbstractCameraHolder cameraHolder, Context context,AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = cameraHolder;
        moduleList  = new HashMap<>();
        this.context = context;
        this.appSettingsManager = appSettingsManager;

        moduleEventHandler = new ModuleEventHandler();
        workers = new ArrayList<>();
        PictureModules = new ArrayList<>();
        PictureModules.add(MODULE_PICTURE);
        PictureModules.add(MODULE_BURST);
        PictureModules.add(MODULE_HDR);
        //PictureModules.add();
        VideoModules = new ArrayList<>();
        VideoModules.add(MODULE_VIDEO);
        AllModules = new ArrayList<>();
        AllModules.add(MODULE_ALL);
        LongeExpoModules = new ArrayList<>();
        LongeExpoModules.add(MODULE_LONGEXPO);

        HDRModule = new ArrayList<>();
        HDRModule.add(MODULE_HDR);

        workerListner = new I_worker() {
            @Override
            public void onWorkStarted() {
                for (int i =0; i < workers.size(); i++)
                {
                    if (workers.get(i) == null) {
                        workers.remove(i);
                        i--;
                    }
                    else
                    {
                        workers.get(i).onWorkStarted();
                    }
                }
            }

            @Override
            public void onWorkFinished(final boolean finished)
            {
                for (int i =0; i < workers.size(); i++)
                {
                    if (workers.get(i) == null) {
                        workers.remove(i);
                        i--;
                    }
                    else
                    {
                        workers.get(i).onWorkFinished(finished);
                    }
                }
            }
        };
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
        Logger.d(TAG, "Set Module to " + name);
    }

    @Override
    public String GetCurrentModuleName() {
        if (currentModule != null)
            return currentModule.ModuleName();
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
    public void SetWorkListner(I_worker workerListner)
    {
        if (!workers.contains(workerListner))
            workers.add(workerListner);
    }


    public void CLEARWORKERLISTNER()
    {
        if (workers != null)
            workers.clear();
    }

    protected void initModules()
    {

    }
}
