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

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freedcam.apis.basecamera.interfaces.ModeParameterInterface;
import com.freedcam.apis.basecamera.modules.ModuleChangedEvent;
import com.freedcam.apis.basecamera.parameters.I_ParametersLoaded;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_MenuItemClick;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R.drawable;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.string;
import com.troop.freedcam.R.styleable;

/**
 * Created by troop on 11.06.2015.
 */
public class UiSettingsChild extends LinearLayout implements ModuleChangedEvent, I_ModeParameterEvent ,I_ParametersLoaded ,OnClickListener
{
    protected Context context;
    private String headerText;
    private LinearLayout laybg;
    protected TextView valueText;
    protected ModeParameterInterface parameter;
    protected I_Activity i_activity;
    private String TAG;
    protected String settingsname;
    protected I_MenuItemClick onItemClick;
    private final boolean logging =false;
    private boolean fromleft;

    public UiSettingsChild(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public void SetStuff(I_Activity i_activity, String settingvalue)
    {
        this.i_activity = i_activity;
        settingsname = settingvalue;
    }

    public UiSettingsChild(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init(context);
        //get custom attributs
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                styleable.UiSettingsChild,
                0, 0
        );
        //try to set the attributs
        try
        {
            TAG = (String)a.getText(styleable.UiSettingsChild_HeaderText);

            headerText = String.valueOf(a.getText(styleable.UiSettingsChild_HeaderText));

            valueText.setText(a.getText(styleable.UiSettingsChild_ValueText));
        }
        finally {
            a.recycle();
        }
        sendLog("Ctor done");
    }

    protected void sendLog(String log)
    {
        if (logging)
            Logger.d(TAG, log);
    }

    public UiSettingsChild(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setEnabled(true);
        setClickable(true);
        this.context = context;
        init(context);
    }

    protected void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateTheme(inflater);

        //headerText = (TextView)findViewById(R.id.textView);
        //headerText.setSelected(true);

        laybg = (LinearLayout) findViewById(id.LAYbg);
       // laybg.setBackgroundDrawable(switchICOn(headerText));

        if(context.getResources().getString(string.uisetting_wb_header) == headerText)
        {
            laybg.setBackgroundDrawable(context.getResources().getDrawable(drawable.quck_set_wb));
        }

        valueText = (TextView) findViewById(id.textView2);
        valueText.setSelected(true);
        setOnClickListener(this);

    }

    protected void inflateTheme(LayoutInflater inflater)
    {
        inflater.inflate(layout.ui_settingschild, this);
    }

    public void SetMenuItemListner(I_MenuItemClick menuItemClick, boolean fromleft)
    {
        onItemClick = menuItemClick;
        this.fromleft = fromleft;
    }

    public void SetMenuItemListner(I_MenuItemClick menuItemClick)
    {
        onItemClick = menuItemClick;
    }

    public void SetParameter(ModeParameterInterface parameter)
    {
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
            onIsSupportedChanged(parameter.IsVisible());
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
        }


        setTextToTextBox(parameter);
    }

    public ModeParameterInterface GetParameter()
    {
        return parameter;
    }

    protected void setTextToTextBox(ModeParameterInterface parameter)
    {
        if (parameter != null && parameter.IsSupported())
        {
            onIsSupportedChanged(true);
            String campara = parameter.GetValue();
            if (campara != null && !campara.equals(""))
                onValueChanged(campara);
            //onIsSupportedChanged(true);
        }
        else
            onIsSupportedChanged(false);
    }

    public String[] GetValues()
    {
        if (parameter != null && parameter.IsSupported())
            return parameter.GetValues();
        else return null;
    }

    public void SetValue(String value)
    {
        if (parameter != null && parameter.IsSupported())
        {
            if (settingsname != null && !settingsname.equals(""))
                i_activity.getAppSettings().setString(settingsname, value);
            try {
                parameter.SetValue(value, true);
            }
            catch (NullPointerException ex)
            {
                Logger.exception(ex);
            }
            onValueChanged(value);
        }
    }

    //AbstractModeParameter.I_ModeParameterEvent implementation
    @Override
    public void onValueChanged(String val)
    {
        sendLog("Set Value to:" + val);
        valueText.setText(val);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported)
    {
        sendLog("isSupported:" + isSupported);
        if (isSupported) {
            setVisibility(View.VISIBLE);
            animate().setListener(null).scaleY(1f).setDuration(300);
        }
        else
            animate().setListener(hideListner).scaleY(0f).setDuration(300);
    }

    private final AnimatorListener hideListner = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onIsSetSupportedChanged(boolean isSupported)
    {
        sendLog("isSetSupported:" + isSupported);
        setEnabled(isSupported);
    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    @Override
    public void onModuleChanged(String module) {
    }

    @Override
    public void ParametersLoaded()
    {
        sendLog("Parameters Loaded");
        if (parameter != null && parameter.IsSupported()) {
            setTextToTextBox(parameter);
            onIsSupportedChanged(true);
        }
        else
            onIsSupportedChanged(false);
    }

    @Override
    public void onClick(View v) {
        if (onItemClick != null)
            onItemClick.onMenuItemClick(this, fromleft);
    }
}
