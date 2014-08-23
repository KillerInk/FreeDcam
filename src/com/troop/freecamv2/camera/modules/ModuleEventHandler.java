package com.troop.freecamv2.camera.modules;

import java.util.ArrayList;

/**
 * Created by troop on 23.08.2014.
 */
public class ModuleEventHandler
{
    ArrayList<I_ModuleEvent> moduleChangedListner;

    public  ModuleEventHandler()
    {
        moduleChangedListner = new ArrayList<I_ModuleEvent>();
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
}
