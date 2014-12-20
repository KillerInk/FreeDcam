package com.troop.freedcam.camera.modules;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by troop on 23.08.2014.
 */
public class ModuleEventHandler
{
    ArrayList<I_ModuleEvent> moduleChangedListner;
    ArrayList<I_WorkEvent> WorkFinishedListners;
    ArrayList<I_RecorderStateChanged> RecorderStateListners;

    public  ModuleEventHandler()
    {
        moduleChangedListner = new ArrayList<I_ModuleEvent>();
        WorkFinishedListners = new ArrayList<I_WorkEvent>();
        RecorderStateListners = new ArrayList<I_RecorderStateChanged>();
    }

    public  void addListner(I_ModuleEvent listner)
    {
        moduleChangedListner.add(listner);
    }

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

    public void CLEAR()
    {
        moduleChangedListner.clear();
        WorkFinishedListners.clear();
        RecorderStateListners.clear();
    }
}
