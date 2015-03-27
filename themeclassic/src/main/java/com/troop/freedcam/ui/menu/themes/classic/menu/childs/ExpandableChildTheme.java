package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;
import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by George on 3/9/2015.
 */
public class ExpandableChildTheme extends ExpandableChild
{
    I_Activity activity_v2;
    public ExpandableChildTheme(Context context,I_Activity activity_v2, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
        this.activity_v2 = activity_v2;
    }

    @Override
    public void setValue(String value) {
        valueTextView.setText(value);
        parameterHolder.SetValue(value, true);
        appSettingsManager.SetTheme(value);
        activity_v2.SetTheme(value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }

    @Override
    public void onValueChanged(String val) {

    }

    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow)
    {
        this.parameterHolder = parameterHolder;
        this.modulesToShow = modulesToShow;
        String s = appSettingsManager.GetTheme();
        if (s.equals("")) {
            s = "Classic";
            appSettingsManager.setString(settingsname,s);
        }
        valueTextView.setText(s);
        //setValue(s);

    }
}
