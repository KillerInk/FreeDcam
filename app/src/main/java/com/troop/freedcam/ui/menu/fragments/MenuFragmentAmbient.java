package com.troop.freedcam.ui.menu.fragments;

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

import com.troop.freedcam.R;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.TextureView.PreviewHandler;
import com.troop.freedcam.ui.menu.MenuHandler;

/**
 * Created by George on 3/20/2015.
 */
    public class MenuFragmentAmbient extends MenuFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        view = inflater.inflate(R.layout.menu_ambient_fragment, container, false);
        settingsLayoutHolder = (LinearLayout)view.findViewById(R.id.settings_menuHolder);
        menuHandler = new MenuHandler(this,(MainActivity_v2)getActivity(), appSettingsManager);
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
                    final Drawable drawable = new BitmapDrawable(((MainActivity_v2) getActivity()).previewHandler.AmbientCover);
                    drawable.setBounds(0,0, view.getWidth(), view.getHeight());
                    settingsLayoutHolder.setBackgroundDrawable(drawable);
                }
                else
                {
                    final Drawable drawable = new BitmapDrawable(getResources(),((MainActivity_v2) getActivity()).previewHandler.AmbientCover);
                    drawable.setBounds(0,0, view.getWidth(), view.getHeight());
                    settingsLayoutHolder.setBackground(drawable);
                    settingsLayoutHolder.refreshDrawableState();
                }

            }
        });




        return view;
    }
}
