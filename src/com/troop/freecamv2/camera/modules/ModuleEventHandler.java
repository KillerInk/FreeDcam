package com.troop.freecamv2.camera.modules;

import java.util.ArrayList;

/**
 * Created by troop on 23.08.2014.
 */
public class ModuleEventHandler
{
    ArrayList<I_ModuleEvent> moduleChangedListner;
    ArrayList<I_WorkEvent> WorkFinishedListners;

    public  ModuleEventHandler()
    {
        moduleChangedListner = new ArrayList<I_ModuleEvent>();
        WorkFinishedListners = new ArrayList<I_WorkEvent>();
    }

    public  void addListner(I_ModuleEvent listner)
    {
        moduleChangedListner.add(listner);
    }

    public void ModuleHasChanged(String module)
    {
        for (I_ModuleEvent listner : moduleChangedListner)
            listner.ModuleChanged(module);
    }

    public void AddWorkFinishedListner(I_WorkEvent i_workEvent)
    {
        WorkFinishedListners.add(i_workEvent);
    }

    public void WorkFinished(String filePath)
    {
        for (I_WorkEvent listner : WorkFinishedListners)
            listner.WorkHasFinished(filePath);
    }
}
