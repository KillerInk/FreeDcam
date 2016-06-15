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

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.ui.I_Activity;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class MenuItemIntervalDuration extends MenuItem
{
    private CameraWrapperInterface cameraUiWrapper;

    public MenuItemIntervalDuration(Context context) {
        super(context);
    }

    public MenuItemIntervalDuration(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void SetStuff(I_Activity i_activity, String settingvalue) {
        super.SetStuff(i_activity, settingvalue);
        //onValueChanged(appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION));
    }

    public void SetCameraUIWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        SetParameter(cameraUiWrapper.GetParameterHandler().IntervalDuration);
    }

    @Override
    public String[] GetValues() {

        //return new String[] {StringUtils.ON, StringUtils.OFF};
        return parameter.GetValues();
    }

    @Override
    public void SetValue(String value)
    {
        i_activity.getAppSettings().setString(AppSettingsManager.SETTING_INTERVAL_DURATION, value);
        onValueChanged(value);
        parameter.SetValue(value,true);
    }
}