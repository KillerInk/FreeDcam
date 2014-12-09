package com.troop.freedcam.i_camera.modules;

import com.troop.freedcam.camera.modules.AbstractModule;
import com.troop.freedcam.camera.modules.ModuleEventHandler;

import java.util.ArrayList;

/**
 * Created by troop on 09.12.2014.
 */
public class AbstractModuleHandler implements I_ModuleHandler
{
    public ModuleEventHandler moduleEventHandler;
    public ArrayList<String> PictureModules;
    public ArrayList<String> LongeExpoModules;
    public ArrayList<String> VideoModules;
    public ArrayList<String> AllModules;
    @Override
    public void SetModule(String name) {

    }

    @Override
    public String GetCurrentModuleName() {
        return null;
    }

    @Override
    public AbstractModule GetCurrentModule() {
        return null;
    }

    @Override
    public boolean DoWork() {
        return false;
    }
}
