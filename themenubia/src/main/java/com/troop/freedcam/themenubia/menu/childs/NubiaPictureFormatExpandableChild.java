package com.troop.freedcam.themenubia.menu.childs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.themenubia.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.I_VideoProfile;
import com.troop.freedcam.ui.menu.themes.classic.menu.childs.PictureFormatExpandableChild;

import java.util.ArrayList;

/**
 * Created by troop on 23.03.2015.
 */
public class NubiaPictureFormatExpandableChild extends PictureFormatExpandableChild {
    public NubiaPictureFormatExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        inflater.inflate(R.layout.expandable_childs_nubia, this);
    }
}
