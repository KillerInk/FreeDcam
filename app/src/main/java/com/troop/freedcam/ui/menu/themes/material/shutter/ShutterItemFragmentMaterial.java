package com.troop.freedcam.ui.menu.themes.material.shutter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ExposureLockHandler;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ShutterHandler;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ShutterItemsFragments;


/**
 * Created by George on 3/17/2015.
 */
public class ShutterItemFragmentMaterial extends ShutterItemsFragments {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.shutteritems_material_fragment, container, false);
        cameraSwitchHandler = new MaterialCameraSwitchHandler(view, appSettingsManager);
        shutterHandler = new ShutterHandler(view, this);
        moduleSwitchHandler = new MaterialModeSwitch(view, appSettingsManager, this);
        flashSwitchHandler = new MaterialFlashSwitch(view, appSettingsManager, this);
        nightModeSwitchHandler = new MaterialNightSwitch(view, appSettingsManager,this);
        exposureLockHandler = new ExposureLockHandler(view, appSettingsManager);
        exitButton = (TextView)view.findViewById(R.id.textView_Exit);

        if( ViewConfiguration.get(getActivity()).hasPermanentMenuKey())
        {
            exitButton.setVisibility(View.GONE);
        }
        else
        {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    getActivity().finish();
                }
            });
        }

        setCameraUIwrapper();
        ParametersLoaded();
        fragmentloaded = true;
        return view;
    }



}
