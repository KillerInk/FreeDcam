package com.troop.freedcam.ui.handler;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;
import com.troop.freedcam.ui.menu.fragments.MenuFragment;
import com.troop.freedcam.ui.menu.fragments.MenuFragmentAmbient;
import com.troop.freedcam.ui.menu.fragments.MenuFragmentMaterial;
import com.troop.freedcam.ui.menu.fragments.MenuFragmentMinimal;
import com.troop.freedcam.ui.menu.fragments.MenuFragmentNubia;
import com.troop.freedcam.ui.menu.fragments.ShutterItemFragmentAmbient;
import com.troop.freedcam.ui.menu.fragments.ShutterItemFragmentMaterial;
import com.troop.freedcam.ui.menu.fragments.ShutterItemFragmentMinimal;
import com.troop.freedcam.ui.menu.fragments.ShutterItemFragmentNubia;
import com.troop.freedcam.ui.menu.fragments.ShutterItemsFragments;

/**
 * Created by Ingo on 14.03.2015.
 */
public class ThemeHandler implements I_ModuleEvent
{
    AppSettingsManager appSettingsManager;
    MainActivity_v2 activity_v2;

    public ThemeHandler(MainActivity_v2 activity_v2, AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
        this.activity_v2 = activity_v2;

    }

    public void GetThemeFragment()
    {
        String theme = appSettingsManager.GetTheme();

        if (activity_v2.shutterItemsFragment != null)
        {
            android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
            transaction.remove(activity_v2.shutterItemsFragment);
            transaction.commit();
            activity_v2.shutterItemsFragment.onDestroyView();

            activity_v2.shutterItemsFragment = null;
        }

        if (theme.equals("Ambient"))
            activity_v2.shutterItemsFragment = new ShutterItemFragmentAmbient();
        if (theme.equals("Classic"))
            activity_v2.shutterItemsFragment = new ShutterItemsFragments();
        if (theme.equals("Material"))
            activity_v2.shutterItemsFragment = new ShutterItemFragmentMaterial();
        if (theme.equals("Minimal"))
            activity_v2.shutterItemsFragment = new ShutterItemFragmentMinimal();
        if (theme.equals("Nubia"))
            activity_v2.shutterItemsFragment = new ShutterItemFragmentNubia();


    }

    public void SettingsMenuFragment()
    {
        String theme = appSettingsManager.GetTheme();

        if (activity_v2.menuFragment != null)
        {
            android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
            transaction.remove(activity_v2.menuFragment);
            transaction.commit();
            activity_v2.menuFragment.onDestroyView();

            activity_v2.menuFragment = null;
        }

        if (theme.equals("Ambient"))
            activity_v2.menuFragment = new MenuFragmentAmbient();
        if (theme.equals("Classic"))
            activity_v2.menuFragment = new MenuFragment();
        if (theme.equals("Material"))
            activity_v2.menuFragment = new MenuFragmentMaterial();
        if (theme.equals("Minimal"))
            activity_v2.menuFragment = new MenuFragmentMinimal();
        if (theme.equals("Nubia"))
            activity_v2.menuFragment = new MenuFragmentNubia();


    }

    public void SetTheme(String theme)
    {
        GetThemeFragment();
        activity_v2.inflateShutterItemFragment();


        SettingsMenuFragment();
        activity_v2.inflateMenuFragment();
        activity_v2.updatePreviewHandler();


        //activity_v2.shutterItemsFragment.ParametersLoaded();
    }

    @Override
    public String ModuleChanged(String module) {
        activity_v2.updatePreviewHandler();

        return null;


    }




}
