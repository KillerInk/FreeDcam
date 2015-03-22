package com.troop.freedcam.ui.menu.themes.classic.manual;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.manual.ManualMenuHandler;

/**
 * Created by troop on 16.03.2015.
 */
public class ManualMenuFragment extends Fragment
{

    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;


    public ManualMenuHandler manualMenuHandler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.manualmenu_fragment, container, false);

        manualMenuHandler = new ManualMenuHandler(view, appSettingsManager, this);
        manualMenuHandler.SetCameraUIWrapper(cameraUiWrapper);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        if (manualMenuHandler != null)
            manualMenuHandler.SetCameraUIWrapper(cameraUiWrapper);
    }
}
