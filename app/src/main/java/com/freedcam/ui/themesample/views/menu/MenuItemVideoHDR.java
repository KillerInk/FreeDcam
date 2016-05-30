package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;

import java.util.ArrayList;

/**
 * Created by troop on 17.08.2015.
 */
public class MenuItemVideoHDR extends MenuItem
{

    private ArrayList<String> modulesToShow;
    private String currentModule;
    private AbstractModuleHandler moduleHandler;
    public MenuItemVideoHDR(Context context) {
        super(context);
    }

    public MenuItemVideoHDR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetModulesToShow(ArrayList<String> modulesToShow, AbstractModuleHandler moduleHandler)
    {
        this.modulesToShow = modulesToShow;
        this.moduleHandler = moduleHandler;
    }

    @Override
    public void SetValue(String value)
    {
        if (parameter != null && parameter.IsSupported() && moduleHandler.GetCurrentModule() != null)
        {
            if (settingsname != null && !settingsname.equals(""))
                appSettingsManager.setString(settingsname, value);
            if (modulesToShow.contains(moduleHandler.GetCurrentModuleName()))
                parameter.SetValue(value, true);
            onValueChanged(value);
        }
    }

    @Override
    public void ModuleChanged(String module) {
        this.currentModule = module;
    }
}
