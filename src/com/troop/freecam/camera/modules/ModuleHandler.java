package com.troop.freecam.camera.modules;

import android.util.Log;

import com.troop.freecam.camera.BaseCameraHolder;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 16.08.2014.
 */
public class ModuleHandler
{
    HashMap<String, AbstractModule> moduleList;
    BaseCameraHolder cameraHolder;
    AppSettingsManager appSettingsManager;
    SoundPlayer soundPlayer;
    AbstractModule currentModule;
    final String TAG = "freecam.ModuleHandler";

    public  ModuleHandler (BaseCameraHolder cameraHolder, AppSettingsManager appSettingsManager, SoundPlayer soundPlayer)
    {
        this.cameraHolder = cameraHolder;
        this.appSettingsManager = appSettingsManager;
        this.soundPlayer = soundPlayer;
        moduleList  = new HashMap<String, AbstractModule>();
        PictureModule pictureModule = new PictureModule(cameraHolder, soundPlayer, appSettingsManager);
        moduleList.put(pictureModule.ModuleName(), pictureModule);
        VideoModule videoModule = new VideoModule(cameraHolder, soundPlayer, appSettingsManager);
        moduleList.put(videoModule.ModuleName(), videoModule);
    }


    public void SetModule(String name)
    {
        currentModule = moduleList.get(name);
        Log.d(TAG, "Set Module to " + name);
    }

    public String GetCurrentModuleName()
    {
        if (currentModule != null)
            return currentModule.name;
        else return "";
    }

    public boolean DoWork()
    {
        if (currentModule != null && !currentModule.IsWorking()) {
            currentModule.DoWork();
            return true;
        }
        else
            return false;
    }

}
