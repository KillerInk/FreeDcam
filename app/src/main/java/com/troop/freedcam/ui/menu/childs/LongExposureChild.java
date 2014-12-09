package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;

/**
 * Created by troop on 13.11.2014.
 */
public class LongExposureChild extends ExpandableChild {
    public LongExposureChild(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongExposureChild(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LongExposureChild(Context context) {
        super(context);
    }

    @Override
    public void setParameterHolder(I_ModeParameter parameterHolder, AppSettingsManager appSettingsManager, String settingsname, ArrayList<String> modulesToShow, AbstractCameraUiWrapper cameraUiWrapper) {
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
        this.cameraUiWrapper = cameraUiWrapper;
        this.parameterHolder = parameterHolder;
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue.equals("")) {
            appSettingsManager.setString(settingsname, "1");
            Log.d(getTAG(), "No appSetting set default " + Name + ":" + 1);
        }
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
        AddModulesToShow(modulesToShow);
    }

    @Override
    public void setValue(String value) {
        valueTextView.setText(value);
        //parameterHolder.SetValue(value, true);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }
}
