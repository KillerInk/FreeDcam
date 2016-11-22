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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.View;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;

/**
 * Created by troop on 09.09.2015.
 */
public class UiSettingsFocusPeak extends UiSettingsChild implements SettingsChildClick
{
    private ModuleChangedReciever moduleChangedReciever;

    public UiSettingsFocusPeak(Context context) {
        super(context);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        moduleChangedReciever = new ModuleChangedReciever();
        getContext().registerReceiver(moduleChangedReciever, new IntentFilter("troop.com.freedcam.MODULE_CHANGED"));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(moduleChangedReciever);
    }

    public void SetUiItemClickListner(SettingsChildClick menuItemClick) {
        SetMenuItemClickListner(this,false);
    }

    public void SetCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        onModuleChanged(cameraUiWrapper.GetModuleHandler().GetCurrentModuleName());

    }

    @Override
    public void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment)
    {
        if (parameter == null)
            return;
        if (parameter.GetValue().equals(KEYS.ON)) {
            parameter.SetValue(KEYS.OFF, false);
        }
        else{
            parameter.SetValue(KEYS.ON,false);}

    }


    private class ModuleChangedReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String module = intent.getStringExtra("INTENT_EXTRA_MODULENAME");
            if ((module.equals(KEYS.MODULE_PICTURE)
                    || module.equals(KEYS.MODULE_HDR)
                    || module.equals(KEYS.MODULE_INTERVAL)
                    || module.equals(KEYS.MODULE_AFBRACKET))
                    && parameter != null && parameter.IsSupported())
                setVisibility(View.VISIBLE);
            else
                setVisibility(View.GONE);
        }
    }
}
