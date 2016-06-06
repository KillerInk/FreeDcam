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
import android.view.LayoutInflater;

import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;

/**
 * Created by troop on 14.06.2015.
 */
public class MenuItemTheme extends MenuItem {
    public MenuItemTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuItemTheme(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        super.inflateTheme(inflater);
    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        if (parameter == null)
        {
            this.setVisibility(GONE);
            return;
        }
        else
            this.setVisibility(VISIBLE);
        this.parameter = parameter;
        String s = appSettingsManager.GetTheme();
        if (s == null || s.equals("")) {
            s = "Sample";
            appSettingsManager.setString(settingsname, s);
        }
        valueText.setText(s);
    }

    @Override
    public void SetValue(String value) {
        appSettingsManager.SetTheme(value);
        i_activity.SetTheme(value);
        onValueChanged(value);
    }
}
