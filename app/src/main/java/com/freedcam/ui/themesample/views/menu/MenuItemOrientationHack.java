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

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.Camera1Fragment;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 21.07.2015.
 */
public class MenuItemOrientationHack extends MenuItem
{
    private CameraWrapperInterface cameraUiWrapper;

    public MenuItemOrientationHack(Context context) {
        super(context);
    }

    public MenuItemOrientationHack(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void SetCameraUIWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(""))
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack, KEYS.OFF);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.ON))
            onValueChanged(KEYS.ON);
        else
            onValueChanged(KEYS.OFF);
    }

    @Override
    public String[] GetValues() {
        return new String[] {KEYS.ON, KEYS.OFF};
    }

    @Override
    public void SetValue(String value)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack, value);
        if (cameraUiWrapper instanceof Camera1Fragment) {
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetCameraRotation();
            cameraUiWrapper.GetParameterHandler().SetPictureOrientation(0);
        }
        else if(cameraUiWrapper instanceof Camera1Fragment)
        {
            ((Camera1Fragment) cameraUiWrapper).cameraHolder.StopPreview();
            ((Camera1Fragment) cameraUiWrapper).cameraHolder.StartPreview();

        }
        onValueChanged(value);
    }

    @Override
    public void onValuesChanged(String[] values) {

    }
}
