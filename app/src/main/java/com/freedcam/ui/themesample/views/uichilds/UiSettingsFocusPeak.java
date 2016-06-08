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

package com.freedcam.ui.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.ui.themesample.subfragments.Interfaces;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_MenuItemClick;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

/**
 * Created by troop on 09.09.2015.
 */
public class UiSettingsFocusPeak extends UiSettingsChild implements I_MenuItemClick {
    public UiSettingsFocusPeak(Context context) {
        super(context);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void SetMenuItemListner(I_MenuItemClick menuItemClick) {
        SetMenuItemListner(this,false);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {

        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);

        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());

    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment)
    {
        if (parameter == null)
            return;
        if (parameter.GetValue().equals(StringUtils.ON)) {
            try {
                parameter.SetValue(StringUtils.OFF, false);
            }
            catch (Exception ex)
            {
                Logger.d("Freedcam", ex.getMessage());
            }
        }
        else
            parameter.SetValue(StringUtils.ON,false);
    }

    @Override
    public void ModuleChanged(String module)
    {
        if ((module.equals(KEYS.MODULE_PICTURE) || module.equals(KEYS.MODULE_HDR)|| module.equals(KEYS.MODULE_INTERVAL)) && parameter != null && parameter.IsSupported())
            setVisibility(VISIBLE);
        else
            setVisibility(GONE);
    }
}
