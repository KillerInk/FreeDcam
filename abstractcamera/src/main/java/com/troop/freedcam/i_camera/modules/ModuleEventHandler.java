package com.troop.freedcam.i_camera.modules;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by troop on 23.08.2014.
 */
public class ModuleEventHandler
{
    //holds all listner for the modulechanged event
    ArrayList<I_ModuleEvent> moduleChangedListner;
    //holds all listner for workfinishedlistner
    ArrayList<I_WorkEvent> WorkFinishedListners;
    //holds all listner for recorstatechanged
    ArrayList<I_RecorderStateChanged> RecorderStateListners;

    public  ModuleEventHandler()
    {
        moduleChangedListner = new ArrayList<I_ModuleEvent>();
        WorkFinishedListners = new ArrayList<I_WorkEvent>();
        RecorderStateListners = new ArrayList<I_RecorderStateChanged>();
    }

    /**
     * Add a listner for Moudlechanged events
     * @param listner the listner for the event
     */
    public  void addListner(I_ModuleEvent listner)
    {
        moduleChangedListner.add(listner);
    }

    /**
     * Gets thrown when the module has changed
     * @param module the new module that gets loaded
     */
    public void ModuleHasChanged(String module)
    {
        for (int i =0; i < moduleChangedListner.size(); i++)
        {
            if (moduleChangedListner.get(i) == null) {
                moduleChangedListner.remove(i);
                i--;
            }
            else
            {
                moduleChangedListner.get(i).ModuleChanged(module);
            }
        }
    }

    /**
     * add listner for workfinished
     * @param i_workEvent the listner for that event
     */
    public void AddWorkFinishedListner(I_WorkEvent i_workEvent)
    {
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
