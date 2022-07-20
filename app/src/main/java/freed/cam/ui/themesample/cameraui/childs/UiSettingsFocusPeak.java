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

package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.settings.SettingsManager;
import freed.settings.mode.BooleanSettingModeInterface;

/**
 * Created by troop on 09.09.2015.
 */
@AndroidEntryPoint
public class UiSettingsFocusPeak extends UiSettingsChild implements SettingsChildClick
{
    @Inject
    SettingsManager settingsManager;
    public UiSettingsFocusPeak(Context context) {
        super(context);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void SetUiItemClickListner(SettingsChildClick menuItemClick) {
        SetMenuItemClickListner(this,false);
    }

    public void SetCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        onModuleChanged(cameraUiWrapper.getModuleHandler().getCurrentModuleName());
        /*if (!settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.off.name()))
            onViewStateChanged(AbstractParameter.ViewState.Visible);*/

    }

    @Override
    public void onSettingsChildClick(SettingsChildAbstract item, boolean fromLeftFragment)
    {
        if (parameter == null)
            return;
        if (parameter instanceof BooleanSettingModeInterface) {
            ((BooleanSettingModeInterface) parameter).set(!((BooleanSettingModeInterface) parameter).get());
        }
        else {
            if (parameter.getStringValue().equals(getResources().getString(R.string.on_))) {
                parameter.setStringValue(getResources().getString(R.string.off_), false);
            } else {
                parameter.setStringValue(getResources().getString(R.string.on_), false);
            }
        }

    }


    @Override
    public void onModuleChanged(String module)
    {
       /* if (*//*(module.equals(getResources().getString(R.string.module_picture))
                || module.equals(getResources().getString(R.string.module_hdr))
                || module.equals(getResources().getString(R.string.module_interval))
        || module.equals(getResources().getString(R.string.module_afbracket)))
                &&*//* parameter != null && parameter.getViewState() == AbstractParameter.ViewState.Visible)
            setVisibility(View.VISIBLE);
        else
            setVisibility(View.GONE);*/
    }
}
