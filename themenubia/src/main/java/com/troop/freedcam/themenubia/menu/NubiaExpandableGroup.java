package com.troop.freedcam.themenubia.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.troop.freedcam.themenubia.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.menu.ExpandableGroup;

/**
 * Created by troop on 23.03.2015.
 */
public class NubiaExpandableGroup extends ExpandableGroup {
    public NubiaExpandableGroup(Context context, LinearLayout submenu, AppSettingsManager appSettingsManager) {
        super(context, submenu, appSettingsManager);
    }

    @Override
    protected void infalteTheme(LayoutInflater inflater) {
        inflater.inflate(R.layout.expandable_groups_nubia, this);
    }
}
