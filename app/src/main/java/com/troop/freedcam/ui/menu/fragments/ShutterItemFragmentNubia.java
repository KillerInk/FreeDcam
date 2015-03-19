package com.troop.freedcam.ui.menu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.ui.handler.ExposureLockHandler;
import com.troop.freedcam.ui.handler.ShutterHandler;
import com.troop.freedcam.ui.menu.fragments.nubia.NubiaCameraSwitchHandler;
import com.troop.freedcam.ui.menu.fragments.nubia.NubiaFlashSwitch;
import com.troop.freedcam.ui.menu.fragments.nubia.NubiaModuleSwitch;
import com.troop.freedcam.ui.menu.fragments.nubia.NubiaNightSwitch;
import com.troop.freedcam.ui.switches.CameraSwitchHandler;
import com.troop.freedcam.ui.switches.FlashSwitchHandler;
import com.troop.freedcam.ui.switches.ModuleSwitchHandler;
import com.troop.freedcam.ui.switches.NightModeSwitchHandler;

/**
 * Created by troop on 12.03.2015.
 */
public class ShutterItemFragmentNubia extends ShutterItemsFragments
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.shutteritems_nubia_fragment, container, false);
        cameraSwitchHandler = new NubiaCameraSwitchHandler(view, appSettingsManager);
        shutterHandler = new ShutterHandler(view, this);
        moduleSwitchHandler = new NubiaModuleSwitch(view, appSettingsManager, this);
        flashSwitchHandler = new NubiaFlashSwitch(view, appSettingsManager, this);
        nightModeSwitchHandler = new NubiaNightSwitch(view, appSettingsManager,this);
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
