package com.troop.freedcam.themenubia.menu.childs;

import android.content.Context;
import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.I_VideoProfile;

import java.util.ArrayList;

/**
 * Created by troop on 23.03.2015.
 */
public class NubiaPictureFormatExpandableChild extends NubiaExpandableChild {
    public NubiaPictureFormatExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }

    public I_VideoProfile PictureFormatChangedHandler;

    @Override
    public void setValue(String value)
    {
        parameterHolder.SetValue(value, true);
        valueTextView.setText(value);
        if (!(parameterHolder instanceof BaseModeParameterSony))
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
        if (PictureFormatChangedHandler != null) {
            String campara = parameterHolder.GetValue();
            PictureFormatChangedHandler.VideoProfileChanged(campara);
        }

    }

    @Override
    public void onValueChanged(String val)
    {
        super.onValueChanged(val);
    }
}
