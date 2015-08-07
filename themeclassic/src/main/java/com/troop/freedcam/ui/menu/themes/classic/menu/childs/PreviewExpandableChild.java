package com.troop.freedcam.ui.menu.themes.classic.menu.childs;

import android.content.Context;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_PreviewSizeEvent;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;


/**
 * Created by troop on 06.09.2014.
 */
public class PreviewExpandableChild extends ExpandableChild
{

    private I_PreviewSizeEvent previewSizeEvent;

    public PreviewExpandableChild(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }
}
