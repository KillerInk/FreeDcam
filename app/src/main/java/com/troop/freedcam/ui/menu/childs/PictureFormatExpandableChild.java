package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 20.10.2014.
 */
public class PictureFormatExpandableChild extends ExpandableChild
{
    public I_VideoProfile PictureFormatChangedHandler;
    public PictureFormatExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }


    @Override
    public void setValue(String value)
    {
        parameterHolder.SetValue(value, true);
        valueTextView.setText(value);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
        if (PictureFormatChangedHandler != null)
            PictureFormatChangedHandler.VideoProfileChanged(value);
    }

    @Override
    public String Value() {
        return super.Value();
    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }


    public void setParameterHolder(AbstractModeParameter parameterHolder, ArrayList<String> modulesToShow) {
       super.setParameterHolder(parameterHolder,modulesToShow);

    }

    @Override
    public void onValueChanged(String val)
    {
        if (appSettingsManager.getString(settingsname).equals(""))
        {
            appSettingsManager.setString(settingsname, "jpeg");
        }
        super.onValueChanged(val);
        if (PictureFormatChangedHandler != null)
            PictureFormatChangedHandler.VideoProfileChanged(val);
    }
}
