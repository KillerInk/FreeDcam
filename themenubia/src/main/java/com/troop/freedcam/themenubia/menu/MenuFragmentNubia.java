package com.troop.freedcam.themenubia.menu;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.themenubia.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuFragment;

/**
 * Created by George on 3/20/2015.
 */
public class MenuFragmentNubia extends MenuFragment {
    public MenuFragmentNubia(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        super(appSettingsManager, i_activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        view = inflater.inflate(R.layout.menu_nubia_fragment, container, false);
        settingsLayoutHolder = (LinearLayout)view.findViewById(R.id.settings_menuHolder);
        menuHandler.INIT();



        settingsLayoutHolder.setBackgroundColor(Color.TRANSPARENT);
        settingsLayoutHolder.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT <16)
                    settingsLayoutHolder.setBackgroundDrawable(null);
                else
                    settingsLayoutHolder.setBackground(null);
                settingsLayoutHolder.setBackgroundColor(Color.argb(200,90,90,90));
            }
        });


        return view;
    }

    @Override
    protected void createMenuHandler(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        menuHandler = new NubiaMenuHandler(this, appSettingsManager,i_activity);
    }
}
