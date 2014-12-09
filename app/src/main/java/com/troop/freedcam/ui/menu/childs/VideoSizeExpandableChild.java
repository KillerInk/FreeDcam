package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoSizeExpandableChild extends ExpandableChild
{

    public VideoSizeExpandableChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoSizeExpandableChild(Context context) {
        super(context);
    }

    public VideoSizeExpandableChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String Name) {
        super.setName(Name);
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
    public void setParameterHolder(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, AbstractCameraUiWrapper cameraUiWrapper) {
        this.parameterHolder = parameterHolder;
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        this.cameraUiWrapper = cameraUiWrapper;
        String campara = parameterHolder.GetValue();
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue == null || settingValue == "") {
            settingValue = campara;
            appSettingsManager.setString(settingsname, settingValue);
        }
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
        parameterHolder.SetValue(settingValue, false);
        AddModulesToShow(modulesToShow);
    }

    @Override
    public void AddModulesToShow(ArrayList<String> modulesToShow) {
        super.AddModulesToShow(modulesToShow);
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
