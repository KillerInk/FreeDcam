package com.troop.theme.minimal.menu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuHandler;
import com.troop.theme.minimal.R;

/**
 * Created by George on 3/20/2015.
 */
public class MenuFragmentMinimal extends MenuFragment {


    public MenuFragmentMinimal(AppSettingsManager appSettingsManager, I_Activity i_activity) {
        super(appSettingsManager, i_activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        view = inflater.inflate(R.layout.menu_minimal_fragment, container, false);
        settingsLayoutHolder = (LinearLayout)view.findViewById(R.id.settings_menuHolder);
        menuHandler = new MenuHandler(this, appSettingsManager,i_activity);
        menuHandler.SetCameraUiWrapper(cameraUiWrapper, surfaceView);

        settingsLayoutHolder = (LinearLayout)view.findViewById(R.id.settings_menuHolder);


        settingsLayoutHolder.setBackgroundColor(Color.TRANSPARENT);
        settingsLayoutHolder.post(new Runnable() {
            @Override
            public void run() {

                settingsLayoutHolder.setBackgroundDrawable(null);
                settingsLayoutHolder.setBackgroundColor(Color.argb(200,20,20,20));
            }
        });
        i_activity.MenuActive(true);


        return view;
    }


}
