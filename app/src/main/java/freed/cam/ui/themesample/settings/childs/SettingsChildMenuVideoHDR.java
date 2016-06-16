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

package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;

/**
 * Created by troop on 17.08.2015.
 */
public class SettingsChildMenuVideoHDR extends SettingsChildMenu
{

    private ArrayList<String> modulesToShow;
    private String currentModule;
    private ModuleHandlerAbstract moduleHandler;
    public SettingsChildMenuVideoHDR(Context context) {
        super(context);
    }

    public SettingsChildMenuVideoHDR(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetModulesToShow(ArrayList<String> modulesToShow, ModuleHandlerAbstract moduleHandler)
    {
        this.modulesToShow = modulesToShow;
        this.moduleHandler = moduleHandler;
    }

    @Override
    public void SetValue(String value)
    {
        if (parameter != null && parameter.IsSupported() && moduleHandler.GetCurrentModule() != null)
        {
            if (key_appsettings != null && !key_appsettings.equals(""))
                fragment_activityInterface.getAppSettings().setString(key_appsettings, value);
            if (modulesToShow.contains(moduleHandler.GetCurrentModuleName()))
                parameter.SetValue(value, true);
            onParameterValueChanged(value);
        }
    }

    @Override
    public void onModuleChanged(String module) {
        currentModule = module;
    }
}
