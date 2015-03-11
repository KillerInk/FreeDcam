package com.troop.freedcam.ui.menu.childs;

import android.content.Context;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoSizeExpandableChild extends ExpandableChild
{
    AppSettingsManager appSettingsManager;
    String settingsname;
    public VideoSizeExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context,group, name);
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
    }

    @Override
    public String getName() {
        return super.getName();
    }


    @Override
    public String Value() {
        return super.Value();
    }

    @Override
    public void setValue(String value) {
        parameterHolder.SetValue(value, true);
        nameTextView.setText(Name);
        valueTextView.setText(value);
    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }

    @Override
    public void setParameterHolder(AbstractModeParameter parameterHolder,ArrayList<String> modulesToShow) {
        super.setParameterHolder(parameterHolder, modulesToShow);
        nameTextView.setText(Name);
        valueTextView.setText(parameterHolder.GetValue());

    }


    @Override
    public String ModuleChanged(String module) {
        return super.ModuleChanged(module);
    }

    @Override
    protected String getTAG() {
        return super.getTAG();
    }
}
