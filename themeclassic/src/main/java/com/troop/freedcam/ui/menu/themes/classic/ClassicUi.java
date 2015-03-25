package com.troop.freedcam.ui.menu.themes.classic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.I_Fragment;
import com.troop.freedcam.ui.menu.themes.R;
import com.troop.freedcam.ui.menu.themes.classic.manual.ManualMenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ShutterHandler;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ShutterItemsFragments;

/**
 * Created by troop on 24.03.2015.
 */
public class ClassicUi extends AbstractFragment implements I_Fragment
{
    public MenuFragment menuFragment;
    public ManualMenuFragment manualMenuFragment;
    public ShutterItemsFragments shutterItemsFragment;
    AppSettingsManager appSettingsManager;
    AbstractCameraUiWrapper cameraUiWrapper;
    I_Activity i_activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.classicui, container, false);
        menuFragment = new MenuFragment();
        shutterItemsFragment = new ShutterItemsFragments();
        manualMenuFragment = new ManualMenuFragment();
        setcameraWrapper();
        inflateShutterItemFragment();
        return view;
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        this.cameraUiWrapper = wrapper;
        setcameraWrapper();
    }

    private void setcameraWrapper() {
        if (manualMenuFragment != null)
            manualMenuFragment.SetCameraUIWrapper(cameraUiWrapper, appSettingsManager);
        if (menuFragment != null)
            menuFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView());
        if (shutterItemsFragment != null)
            shutterItemsFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView());
    }

    @Override
    public void SetAppSettings(AppSettingsManager appSettingsManager) {
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public void SetI_Activity(I_Activity i_activity) {
        this.i_activity = i_activity;
    }


    public void inflateShutterItemFragment()
    {
        shutterItemsFragment.SetAppSettings(appSettingsManager);
        shutterItemsFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView());

        if (!shutterItemsFragment.isAdded())
        {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.layout__cameraControls, shutterItemsFragment, "Controls");
            transaction.commit();
        }
    }

    public void inflateMenuFragment()
    {

        menuFragment.SetAppSettings(appSettingsManager, i_activity);
        menuFragment.SetCameraUIWrapper(cameraUiWrapper, i_activity.GetSurfaceView());
        if (!menuFragment.isAdded()) {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.add(R.id.v2_settings_menu, menuFragment, "Menu");
            transaction.commit();
        }
    }

    @Override
    public void deflateMenuFragment() {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();
    }

    public void inflateManualMenuFragment()
    {

        manualMenuFragment.SetCameraUIWrapper(cameraUiWrapper, appSettingsManager);
        if (!manualMenuFragment.isAdded()) {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
            transaction.add(R.id.manualMenuHolder, manualMenuFragment, "ManualMenu");
            transaction.commit();
        }
    }

    @Override
    public void deflateManualMenuFragment() {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.remove(manualMenuFragment);
        fragmentTransaction.commit();
    }

    public ShutterHandler getShutterHandler()
    {
        return shutterItemsFragment.shutterHandler;
    }
}
