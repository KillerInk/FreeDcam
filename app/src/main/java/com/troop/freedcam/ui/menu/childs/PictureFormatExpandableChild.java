package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by troop on 20.10.2014.
 */
public class PictureFormatExpandableChild extends ExpandableChild {
    public PictureFormatExpandableChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureFormatExpandableChild(Context context) {
        super(context);
    }

    public PictureFormatExpandableChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setValue(String value)
    {
        parameterHolder.SetValue(value, true);
        valueTextView.setText(value);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }

    @Override
    public String Value() {
        return super.Value();
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
        //TODO code design fail
        //process raw supported devices
        settingValue = setDeviceSettings(parameterHolder, appSettingsManager, settingsname, cameraUiWrapper, campara, settingValue);
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
        appSettingsManager.setString(settingsname, settingValue);
        AddModulesToShow(modulesToShow);
    }

    private String setDeviceSettings(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, AbstractCameraUiWrapper cameraUiWrapper, String campara, String settingValue) {
        if (settingValue.equals(""))
        {
            appSettingsManager.setString(settingsname, "jpeg");
            settingValue = "jpeg";
        }
        return settingValue;
    }
}
