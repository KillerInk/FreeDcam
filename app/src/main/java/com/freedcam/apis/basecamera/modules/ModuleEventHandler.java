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
import android.os.Looper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by troop on 23.08.2014.
 */
public class ModuleEventHandler
{
    //holds all listner for the modulechanged event
    private final ArrayList<ModuleChangedEvent> moduleChangedListner;
    //holds all listner for workfinishedlistner
    private final ArrayList<I_WorkEvent> WorkFinishedListners;
    //holds all listner for recorstatechanged
    private final ArrayList<I_RecorderStateChanged> RecorderStateListners;
    private final Handler uihandler;

    public  ModuleEventHandler()
    {
        moduleChangedListner = new ArrayList<>();
        WorkFinishedListners = new ArrayList<>();
        RecorderStateListners = new ArrayList<>();
        uihandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Add a listner for Moudlechanged events
     * @param listner the listner for the event
     */
    public  void addListner(ModuleChangedEvent listner)
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
                        moduleChangedListner.get(toget).onModuleChanged(module);
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
