package com.troop.freedcam.ui.menu.themes.classic.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.R;


/**
 * Created by troop on 27.02.2015.
 */
public class MenuFragment extends Fragment implements I_ParametersLoaded
{
    public LinearLayout settingsLayoutHolder;
    public MenuHandler menuHandler;
    public AppSettingsManager appSettingsManager;
    public AbstractCameraUiWrapper cameraUiWrapper;
    public SurfaceView surfaceView;
    public I_Activity i_activity;

    public View view;

    public MenuFragment(AppSettingsManager appSettingsManager, I_Activity i_activity)
    {
        this.appSettingsManager = appSettingsManager;
        this.i_activity = i_activity;
        menuHandler = new MenuHandler(this, appSettingsManager, i_activity);
    }

    public void CLEAR()
    {
        menuHandler.CLEARPARENT();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        view = inflater.inflate(R.layout.menu_fragment, container, false);
        settingsLayoutHolder = (LinearLayout)view.findViewById(R.id.settings_menuHolder);
        menuHandler.INIT();
        return view;
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView surfaceView)
    {
        this.surfaceView =surfaceView;
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        if (menuHandler != null)
            menuHandler.SetCameraUiWrapper(cameraUiWrapper, surfaceView);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void ParametersLoaded() {
        menuHandler.SetCameraUiWrapper(cameraUiWrapper,surfaceView);
    }
}
