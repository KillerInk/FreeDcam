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
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_Module;
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
    public enum CaptureStates
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

    public interface CaptureStateChanged
    {
        void onCaptureStateChanged(CaptureStates captureStates);
    }

    private ArrayList<CaptureStateChanged> onCaptureStateChangedListners;

    private String TAG = AbstractModuleHandler.class.getSimpleName();
    public ModuleEventHandler moduleEventHandler;
    public AbstractMap<String, I_Module> moduleList;
    protected I_Module currentModule;
    protected I_CameraUiWrapper cameraUiWrapper;

    protected CaptureStateChanged workerListner;



    protected Context context;
    protected AppSettingsManager appSettingsManager;

    public AbstractModuleHandler(Context context, I_CameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        moduleList  = new HashMap<>();
        this.context = context;
        this.appSettingsManager = cameraUiWrapper.GetAppSettingsManager();

        moduleEventHandler = new ModuleEventHandler();
        onCaptureStateChangedListners = new ArrayList<>();

        workerListner = new CaptureStateChanged() {
            @Override
            public void onCaptureStateChanged(CaptureStates captureStates) {
                for (int i = 0; i < onCaptureStateChangedListners.size(); i++)
                {
                    if (onCaptureStateChangedListners.get(i) == null) {
                        onCaptureStateChangedListners.remove(i);
                        i--;
                    }
                    else
                    {
                        onCaptureStateChangedListners.get(i).onCaptureStateChanged(captureStates);
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
            currentModule.SetCaptureStateChangedListner(null);

        }
        currentModule = moduleList.get(name);
        currentModule.InitModule();
        moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
        currentModule.SetCaptureStateChangedListner(workerListner);
        Logger.d(TAG, "Set Module to " + name);
    }

    @Override
    public String GetCurrentModuleName() {
        if (currentModule != null)
            return currentModule.ModuleName();
        else return KEYS.MODULE_PICTURE;
    }

    @Override
    public I_Module GetCurrentModule() {
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
    public void SetWorkListner(CaptureStateChanged workerListner)
    {
        if (!onCaptureStateChangedListners.contains(workerListner))
            onCaptureStateChangedListners.add(workerListner);
    }


    public void CLEARWORKERLISTNER()
    {
        if (onCaptureStateChangedListners != null)
            onCaptureStateChangedListners.clear();
    }

}
