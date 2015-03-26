package com.troop.theme.ambient;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;
import com.troop.theme.ambient.menu.MenuFragmentAmbient;
import com.troop.theme.ambient.shutter.ShutterItemFragmentAmbient;

/**
 * Created by troop on 26.03.2015.
 */
public class AmbientUi extends ClassicUi
{
    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container)
    {
        view = inflater.inflate(R.layout.classicui, container, false);
        shutterItemsFragment = new ShutterItemFragmentAmbient();
        menuFragment = new MenuFragmentAmbient();
        manualMenuFragment = new NubiaManualMenuFragment();
    }
}
