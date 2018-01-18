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

package freed.cam.ui.themesample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.utils.Log;

/**
 * Created by troop on 16.06.2016.
 */
public abstract class SettingsChildAbstract extends LinearLayout implements SettingsChildInterface, ParameterEvents
{

    public interface SettingsChildClick
    {
        void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment);
    }

    public interface CloseChildClick
    {
        void onCloseClicked(String value);
    }

    protected ParameterInterface parameter;
    protected ActivityInterface fragment_activityInterface;
    protected String key_appsettings;
    protected TextView valueText;

    protected SettingsChildClick onItemClick;
    protected boolean fromleft;

    public SettingsChildAbstract(Context context, ParameterInterface parameter)
    {
        super(context);
        this.parameter = parameter;
        if (parameter == null)
            return;
        String value = parameter.GetStringValue();
        parameter.addEventListner(this);
        parameter.fireStringValueChanged(value);
    }


    @Override
    public void SetStuff(ActivityInterface fragment_activityInterface, String settingvalue)
    {
        this.fragment_activityInterface = fragment_activityInterface;
        key_appsettings = settingvalue;
    }

    public SettingsChildAbstract(Context context) {
        super(context);
    }

    public SettingsChildAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected abstract void sendLog(String log);

    protected abstract void init(Context context);
    protected abstract void inflateTheme(LayoutInflater inflater);

    public void SetMenuItemClickListner(SettingsChildClick menuItemClick, boolean fromleft)
    {
        onItemClick = menuItemClick;
        this.fromleft = fromleft;
    }

    public void SetUiItemClickListner(SettingsChildClick menuItemClick)
    {
        onItemClick = menuItemClick;
    }

    public void SetParameter(ParameterInterface parameter) {
        if (parameter == null || !parameter.IsSupported())
        {
            onIsSupportedChanged(false);
            sendLog("Paramters is null or Unsupported");
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
            return;
        }
        else
        {

            if (parameter != null) {
                parameter.fireIsReadOnlyChanged(parameter.IsVisible());
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
        }
    }

    @Override
    public ParameterInterface GetParameter()
    {
        return parameter;
    }

    @Override
    public String[] GetValues()
    {
        if (parameter != null && parameter.IsSupported())
            return parameter.getStringValues();
        else return null;
    }

    @Override
    public void SetValue(String value)
    {
        if (parameter != null && parameter.IsSupported())
        {
            try {
                parameter.SetValue(value, true);
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }
        }
    }
}
