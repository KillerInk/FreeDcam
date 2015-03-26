package com.troop.freedcam.themenubia;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.themenubia.menu.MenuFragmentNubia;
import com.troop.freedcam.themenubia.shutter.ShutterItemFragmentNubia;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;

/**
 * Created by troop on 26.03.2015.
 */
public class NubiaUi extends ClassicUi
{
    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.classicui, container, false);
        shutterItemsFragment = new ShutterItemFragmentNubia();
        menuFragment = new MenuFragmentNubia();
        manualMenuFragment = new NubiaManualMenuFragment();
    }
}
