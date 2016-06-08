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

import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.utils.AppSettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GeorgeKiarie on 1/29/2016.
 */
public class MenuItemVideoBitrate extends MenuItem {


    private List<String> Bitrates;

    public MenuItemVideoBitrate(Context context) {
        super(context);
    }



    public MenuItemVideoBitrate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_VideoBitrate, value);
        onValueChanged(value);
    }



    @Override
    public String[] GetValues()
    {
        return Bitrates.toArray(new String[Bitrates.size()]);
    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        if (parameter == null || !parameter.IsSupported())
        {
            onIsSupportedChanged(false);
            sendLog("Paramters is null or Unsupported");
            return;
        }

        Bitrates = new ArrayList<>();
        Bitrates.add("Default");
        Bitrates.add("200Mbps");
        Bitrates.add("150Mbps");
        Bitrates.add("100Mbps");
        Bitrates.add("80Mbps");
        Bitrates.add("60Mbps");
        Bitrates.add("50Mbps");
        Bitrates.add("40Mbps");
        Bitrates.add("30Mbps");
        Bitrates.add("10Mbps");
        Bitrates.add("5Mbps");
        Bitrates.add("2Mbps");

        if (Bitrates.size() > 0)
            onIsSupportedChanged(true);
        else
            onIsSupportedChanged(false);

        this.parameter = parameter;

        onValueChanged(appSettingsManager.getString(AppSettingsManager.SETTING_VideoBitrate));
    }
}