package com.freedcam.apis.basecamera.camera.modules;

import android.content.Context;

import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.interfaces.I_ModuleHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractModuleHandler implements I_ModuleHandler
{

    public enum CaptureModes
    {
        video_recording_stop,
        video_recording_start,
        image_capture_stop,
        image_capture_start,
        continouse_capture_start,
        continouse_capture_stop,
        continouse_capture_work_start,
        continouse_capture_work_stop,
        cont_capture_stop_while_working,
        cont_capture_stop_while_notworking,
    }

    public interface I_worker
    {
        void onCaptureStateChanged(CaptureModes captureModes);
    }

    ArrayList<I_worker> workers;

    private static String TAG = AbstractModuleHandler.class.getSimpleName();
    public ModuleEventHandler moduleEventHandler;
    public AbstractMap<String, AbstractModule> moduleList;
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

        workerListner = new I_worker() {
            @Override
            public void onCaptureStateChanged(CaptureModes captureModes) {
                for (int i =0; i < workers.size(); i++)
                {
                    if (workers.get(i) == null) {
                        workers.remove(i);
                        i--;
                    }
                    else
                    {
                        workers.get(i).onCaptureStateChanged(captureModes);
                    }
                }
            }
        };
    }

    /**
     * Load the new module
     * @param name of the module to load
     */
    @Override
    public void SetModule(String name) {
        if (currentModule !=null) {
            currentModule.DestroyModule();
            currentModule.SetWorkerListner(null);

        }
        currentModule = moduleList.get(name);
        currentModule.InitModule();
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
