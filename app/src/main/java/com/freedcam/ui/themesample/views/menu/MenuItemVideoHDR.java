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

package com.freedcam.ui.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.basecamera.modules.AbstractModuleHandler;

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
    public void onModuleChanged(String module) {
        currentModule = module;
    }
}
