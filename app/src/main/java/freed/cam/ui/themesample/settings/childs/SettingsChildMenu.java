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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.styleable;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.settings.AppSettingsManager;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsChildMenu extends UiSettingsChild
{
    private TextView description;

    private TextView headerText;

    public SettingsChildMenu(Context context) {
        super(context);
    }

    public SettingsChildMenu(Context context,int headerid, int descriptionid)
    {
        super(context);
        headerText.setText(getResources().getText(headerid));
        description.setText(getResources().getText(descriptionid));
    }

    public SettingsChildMenu(Context context, AppSettingsManager.SettingMode settingsMode, ParameterInterface parameter) {
        super(context, settingsMode, parameter);
    }

    public SettingsChildMenu(Context context, AppSettingsManager.SettingMode settingsMode, ParameterInterface parameter, int headerid, int descriptionid)
    {
        super(context,settingsMode,parameter);
        headerText.setText(getResources().getText(headerid));
        description.setText(getResources().getText(descriptionid));
        valueText.setText(settingsMode.get());
    }

    public SettingsChildMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        //get custom attributs
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.SettingsChildMenu,
                0, 0
        );
        TypedArray b = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.UiSettingsChild,
                0, 0
        );
        //try to set the attributs
        try
        {

            headerText.setText(b.getText(styleable.UiSettingsChild_HeaderText));

            description.setText(a.getText(styleable.SettingsChildMenu_Description));
        }
        finally {
            a.recycle();
        }
        sendLog("Ctor done");
    }

    @Override
    protected void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateTheme(inflater);
        headerText = (TextView) findViewById(id.textview_menuitem_header);
        valueText = (TextView) findViewById(id.textview_menuitem_header_value);
        description = (TextView) findViewById(id.textview_menuitem_description);
        LinearLayout toplayout = (LinearLayout) findViewById(id.menu_item_toplayout);
        setOnClickListener(this);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        inflater.inflate(layout.settings_menu_item, this);
    }

    @Override
    public void onStringValueChanged(String val) {
        sendLog("Set Value to:" + val);
        if (valueText != null)
            valueText.setText(val);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null)
            onItemClick.onSettingsChildClick(this, false);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {
        sendLog("isSupported:" + isSupported);
        if (isSupported) {
            setVisibility(View.VISIBLE);
        }
        else
            setVisibility(View.GONE);
    }
}
