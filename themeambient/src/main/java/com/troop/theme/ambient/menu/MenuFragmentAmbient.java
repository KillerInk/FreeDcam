package com.troop.theme.ambient.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.themenubia.menu.MenuFragmentNubia;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuHandler;
import com.troop.theme.ambient.AmbientUi;
import com.troop.theme.ambient.R;

/**
 * Created by George on 3/20/2015.
 */
    public class MenuFragmentAmbient extends MenuFragmentNubia
{
    AmbientUi ambientUi;

    public MenuFragmentAmbient(AmbientUi ambientUi)
    {
        this.ambientUi = ambientUi;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        view = inflater.inflate(R.layout.menu_ambient_fragment, container, false);
        settingsLayoutHolder = (LinearLayout)view.findViewById(R.id.settings_menuHolder);
        menuHandler = new MenuHandler(this, appSettingsManager, i_activity);
        menuHandler.SetCameraUiWrapper(cameraUiWrapper, surfaceView);
        settingsLayoutHolder = (LinearLayout)view.findViewById(R.id.settings_menuHolder);


        settingsLayoutHolder.setBackgroundColor(Color.TRANSPARENT);
        settingsLayoutHolder.post(new Runnable() {
            @Override
            public void run()
            {
                settingsLayoutHolder.setBackgroundColor(Color.TRANSPARENT);
                if (Build.VERSION.SDK_INT < 16)
                {
                    final Drawable drawable = new BitmapDrawable(ambientUi.AmbientCover);
                    drawable.setBounds(0,0, view.getWidth(), view.getHeight());
                    settingsLayoutHolder.setBackgroundDrawable(drawable);
                }
                else
                {
                    final Drawable drawable = new BitmapDrawable(getResources(),ambientUi.AmbientCover);
                    drawable.setBounds(0,0, view.getWidth(), view.getHeight());
                    settingsLayoutHolder.setBackground(drawable);
                    settingsLayoutHolder.refreshDrawableState();
                }

            }
        });




        return view;
    }

    public void SetAmbientUI(AmbientUi ambientUi)
    {
        this.ambientUi = ambientUi;
    }
}
