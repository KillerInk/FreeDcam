package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by George on 3/9/2015.
 */
public class ExpandableChildTheme extends ExpandableChild
{
    MainActivity_v2 activity_v2;
    public ExpandableChildTheme(MainActivity_v2 context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
        this.activity_v2 = context;
    }

    @Override
    public void setValue(String value) {
        valueTextView.setText(value);
        parameterHolder.SetValue(value, true);
        appSettingsManager.setString(settingsname, value);
        activity_v2.themeHandler.SetTheme(value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }

    @Override
    public void onValueChanged(String val) {

    }

    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow)
    {
        this.parameterHolder = parameterHolder;
        this.modulesToShow = modulesToShow;
        String s = appSettingsManager.getString(settingsname);
        if (s.equals("")) {
            s = "Classic";
            appSettingsManager.setString(settingsname,s);
        }
        valueTextView.setText(s);
        //setValue(s);

    }
}
