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

package com.freedcam.apis.basecamera.modules;

import android.content.Context;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.interfaces.I_ModuleHandler;
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

    private String TAG = AbstractModuleHandler.class.getSimpleName();
    public ModuleEventHandler moduleEventHandler;
    public AbstractMap<String, AbstractModule> moduleList;
    protected AbstractModule currentModule;
    protected AbstractCameraHolder cameraHolder;

    protected I_worker workerListner;



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
        else return KEYS.MODULE_PICTURE;
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
