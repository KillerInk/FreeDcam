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

import android.os.Handler;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.interfaces.ModuleHandlerInterface;
import com.freedcam.apis.basecamera.interfaces.ModuleInterface;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractModuleHandler implements ModuleHandlerInterface
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

    private final ArrayList<CaptureStateChanged> onCaptureStateChangedListners;

    private final String TAG = AbstractModuleHandler.class.getSimpleName();
    public ModuleEventHandler moduleEventHandler;
    public AbstractMap<String, ModuleInterface> moduleList;
    protected ModuleInterface currentModule;
    protected CameraWrapperInterface cameraUiWrapper;

    protected CaptureStateChanged workerListner;

    //holds all listner for the modulechanged event
    private final ArrayList<I_ModuleEvent> moduleChangedListner;
    //holds all listner for workfinishedlistner
    private final ArrayList<I_WorkEvent> WorkFinishedListners;
    //holds all listner for recorstatechanged
    private final ArrayList<I_RecorderStateChanged> RecorderStateListners;
    private Handler uihandler;



    protected AppSettingsManager appSettingsManager;

    public AbstractModuleHandler(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        moduleList = new HashMap<>();
        moduleChangedListner = new ArrayList<>();
        WorkFinishedListners = new ArrayList<>();
        RecorderStateListners = new ArrayList<>();
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
    public ModuleInterface GetCurrentModule() {
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

    /**
     * Add a listner for Moudlechanged events
     * @param listner the listner for the event
     */
    public  void addListner(I_ModuleEvent listner)
    {
        if (!moduleChangedListner.contains(listner))
            moduleChangedListner.add(listner);
    }

    /**
     * Gets thrown when the module has changed
     * @param module the new module that gets loaded
     */
    public void ModuleHasChanged(final String module)
    {
        if (moduleChangedListner.size() == 0)
            return;
        for (int i = 0; i < moduleChangedListner.size(); i++)
        {
            if (moduleChangedListner.get(i) == null) {
                moduleChangedListner.remove(i);
                i--;
            }
            else
            {
                final int toget = i;
                uihandler.post(new Runnable() {
                    @Override
                    public void run() {
                        moduleChangedListner.get(toget).ModuleChanged(module);
                    }
                });

            }
        }
    }

    /**
     * add listner for workfinished
     * @param i_workEvent the listner for that event
     */
    public void AddWorkFinishedListner(I_WorkEvent i_workEvent)
    {
        if (!WorkFinishedListners.contains(i_workEvent))
            WorkFinishedListners.add(i_workEvent);
    }

    public void WorkFinished(File filePath)
    {
        for (I_WorkEvent listner : WorkFinishedListners)
            listner.WorkHasFinished(filePath);
    }

    public void AddRecoderChangedListner(I_RecorderStateChanged recorderStateChanged)
    {
        RecorderStateListners.add(recorderStateChanged);
    }

    public void onRecorderstateChanged(int state)
    {
        for (I_RecorderStateChanged lisn : RecorderStateListners)
            lisn.RecordingStateChanged(state);
    }

    //clears all listner this happens when the camera gets destroyed
    public void CLEAR()
    {
        moduleChangedListner.clear();
        WorkFinishedListners.clear();
        RecorderStateListners.clear();
    }

}
