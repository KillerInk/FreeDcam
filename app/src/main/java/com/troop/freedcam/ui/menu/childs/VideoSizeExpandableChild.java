package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoSizeExpandableChild extends ExpandableChild
{
    public VideoSizeExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context,group, name, appSettingsManager,settingsname);
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
        appSettingsManager.setString(settingsname, value);
        parameterHolder.SetValue(value, true);
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
    }

    @Override
    public I_ModeParameter getParameterHolder() {
        return super.getParameterHolder();
    }

    @Override
    public void setParameterHolder(I_ModeParameter parameterHolder,ArrayList<String> modulesToShow) {
        super.setParameterHolder(parameterHolder, modulesToShow);
        String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue == null || settingValue == "") {
            settingValue = campara;
            appSettingsManager.setString(settingsname, settingValue);
        }
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
        parameterHolder.SetValue(settingValue, false);
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
