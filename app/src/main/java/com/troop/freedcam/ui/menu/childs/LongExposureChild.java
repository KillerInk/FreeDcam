package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

import java.util.ArrayList;

/**
 * Created by troop on 13.11.2014.
 */
public class LongExposureChild extends ExpandableChild {
    public LongExposureChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname,ArrayList<String> modulesToShow) {
        super(context, group, name, appSettingsManager, settingsname, modulesToShow);
    }

    @Override
    public void setParameterHolder(I_ModeParameter parameterHolder) {
        this.parameterHolder = parameterHolder;
        String settingValue = appSettingsManager.getString(settingsname);
        if (settingValue.equals("")) {
            appSettingsManager.setString(settingsname, "1");
            Log.d(getTAG(), "No appSetting set default " + Name + ":" + 1);
        }
        nameTextView.setText(Name);
        valueTextView.setText(appSettingsManager.getString(settingsname));
    }

    @Override
    public void setValue(String value) {
        valueTextView.setText(value);
        appSettingsManager.setString(settingsname, value);
        Log.d(getTAG(), "Set " + Name + ":" + value);
    }
}
