package com.freedcam.apis.basecamera.camera.modules;

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
    private ArrayList<I_ModuleEvent> moduleChangedListner;
    //holds all listner for workfinishedlistner
    private ArrayList<I_WorkEvent> WorkFinishedListners;
    //holds all listner for recorstatechanged
    private ArrayList<I_RecorderStateChanged> RecorderStateListners;
    private Handler uihandler;

    public  ModuleEventHandler()
    {
        moduleChangedListner = new ArrayList<>();
        WorkFinishedListners = new ArrayList<>();
        RecorderStateListners = new ArrayList<>();
        uihandler = new Handler(Looper.getMainLooper());
    }

    public void RunOnUiThread(Runnable runnable)
    {
        uihandler.post(runnable);
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
        for (int i =0; i < moduleChangedListner.size(); i++)
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
