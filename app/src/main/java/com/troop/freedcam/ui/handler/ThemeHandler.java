package com.troop.freedcam.ui.handler;

import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.themenubia.shutter.ShutterItemFragmentNubia;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.menu.themes.classic.AbstractFragment;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ShutterHandler;
import com.troop.theme.minimal.shutter.ShutterItemFragmentMinimal;
import com.troop.theme.ambient.shutter.ShutterItemFragmentAmbient;
import com.troop.theme.material.shutter.ShutterItemFragmentMaterial;

/**
 * Created by Ingo on 14.03.2015.
 */
public class ThemeHandler implements I_ModuleEvent
{
    AppSettingsManager appSettingsManager;
    MainActivity_v2 activity_v2;
    LinearLayout uiLayout;
    AbstractFragment uiFragment;
    AbstractCameraUiWrapper cameraUiWrapper;

    public ThemeHandler(MainActivity_v2 activity_v2, AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
        this.activity_v2 = activity_v2;
        uiLayout = (LinearLayout) activity_v2.findViewById(R.id.themeFragmentholder);

    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public void GetThemeFragment()
    {
        String theme = appSettingsManager.GetTheme();

        if (uiFragment != null)
        {
            android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
            transaction.remove(uiFragment);
            transaction.commit();
            uiFragment.onDestroyView();

            uiFragment = null;
        }

        if (theme.equals("Ambient")) {
            //activity_v2.shutterItemsFragment = new ShutterItemFragmentAmbient();
        }
        if (theme.equals("Classic"))
        {
            ClassicUi CuiFragment = new ClassicUi();
            CuiFragment.SetI_Activity(activity_v2);
            CuiFragment.SetAppSettings(appSettingsManager);
            CuiFragment.SetCameraUIWrapper(cameraUiWrapper);
            uiFragment = CuiFragment;
            android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.themeFragmentholder, CuiFragment, "Main");
            transaction.commit();
        }

        if (theme.equals("Material")) {
            //activity_v2.shutterItemsFragment = new ShutterItemFragmentMaterial();
        }
        if (theme.equals("Minimal")) {
            //activity_v2.shutterItemsFragment = new ShutterItemFragmentMinimal();
        }
        if (theme.equals("Nubia")) {
            //activity_v2.shutterItemsFragment = new ShutterItemFragmentNubia();
        }


    }

    public void inflateMenu()
    {
        uiFragment.inflateMenuFragment();
    }

    public void deflateMenu()
    {
        uiFragment.deflateMenuFragment();
    }

    public void inflateManualMenuFragment()
    {
        uiFragment.inflateManualMenuFragment();
    }

    public void deflateManualMenuFragment()
    {
        uiFragment.deflateManualMenuFragment();
    }

    public ShutterHandler getShutterHandler()
    {
        return uiFragment.getShutterHandler();
    }

    /*public void SettingsMenuFragment()
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


    }*/

    /*public void GetManualMenuFragment()
    {
        String theme = appSettingsManager.GetTheme();

        if (activity_v2.manualMenuFragment != null)
        {
            android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
            transaction.remove(activity_v2.manualMenuFragment);
            transaction.commit();
            activity_v2.manualMenuFragment.onDestroyView();

            activity_v2.manualMenuFragment = null;
        }

        if (theme.equals("Ambient"))
            activity_v2.manualMenuFragment = new NubiaManualMenuFragment();
        if (theme.equals("Classic"))
            activity_v2.manualMenuFragment = new ManualMenuFragment();
        if (theme.equals("Material"))
            activity_v2.manualMenuFragment = new NubiaManualMenuFragment();
        if (theme.equals("Minimal"))
            activity_v2.manualMenuFragment = new NubiaManualMenuFragment();
        if (theme.equals("Nubia"))
            activity_v2.manualMenuFragment = new NubiaManualMenuFragment();
    }*/

    public void SetTheme(String theme)
    {
        GetThemeFragment();

        activity_v2.updatePreviewHandler();


        //activity_v2.shutterItemsFragment.ParametersLoaded();
    }

    @Override
    public String ModuleChanged(String module) {
        activity_v2.updatePreviewHandler();

        return null;


    }




}
